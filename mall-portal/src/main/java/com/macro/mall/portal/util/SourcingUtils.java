package com.macro.mall.portal.util;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.portal.cache.RedisUtil;
import com.macro.mall.portal.config.MicroServiceConfig;
import com.macro.mall.portal.domain.SiteSourcing;
import com.macro.mall.portal.domain.XmsChromeUploadParam;
import com.macro.mall.portal.service.IXmsChromeUploadService;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.importExpress.util
 * @date:2020/3/19
 */
@Slf4j
@Service
public class SourcingUtils {
    public final String IMG_SEARCH_PATH = "/search/sourcing";
    public final String SOURCING_UPLOAD_IMG = "/sourcing/upload";

    public final String SOURCING_CAR = "sourcing:car:list";
    public final String SOURCING_GOODS = "sourcing:goods";
    public final String SOURCING_TAOBAO_IMG = "sourcing:taobao:img";

    public final long EXPIRATION_TIME_7_DAY = 3600 * 24 * 7;
    public final long EXPIRATION_TIME_1_SECOND = 1;
    private static final double RATE_1688_PYTHON_PRICE = 1.2;
    private static final double RATE_1688_TAOBAO_PRICE = 1.15;
    public final String REDIS_TAOBAO_IMG = "_taobao_img";

    private static final String IMG_SEARCH_CACHE = "/buy/imgSearchCache";

    private static Set<String> infringementSet = new HashSet<>();

    public final String BUYFORME_PID = "sourcing:pid";

    private static final long BUYFORME_PID_EXPIRE = 1000 * 60 * 60 * 24 * 7;

    private UrlUtil instance = UrlUtil.getInstance();

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private ExchangeRateUtils exchangeRateUtils;

    @Autowired
    private MicroServiceConfig microServiceConfig;

    @Autowired
    private IXmsChromeUploadService xmsChromeUploadService;

    private String imgUploadPath = null;

    static {
        initSet();
    }

    /**
     * 根据bean添加购物车
     *
     * @param siteSourcing
     */
    public void addBfmCart(SiteSourcing siteSourcing) {
        String userId = String.valueOf(siteSourcing.getUserId());
        Map<String, Object> objectMap = this.getCarToRedis(userId);
        if (null == objectMap) {
            objectMap = new HashMap<>();
        }
        String pidKey = siteSourcing.getPid() + "_" + siteSourcing.getSiteFlag();
        if (objectMap.containsKey(pidKey)) {
            String carInfo = objectMap.get(pidKey).toString();
            SiteSourcing redisBuyForMe = JSONObject.parseObject(carInfo, SiteSourcing.class);
            redisBuyForMe.setAverageDailyOrder(redisBuyForMe.getAverageDailyOrder() + siteSourcing.getAverageDailyOrder());
            redisBuyForMe.setOneTimeOrderOnly(redisBuyForMe.getOneTimeOrderOnly() + siteSourcing.getOneTimeOrderOnly());
            if (StrUtil.isNotEmpty(siteSourcing.getData())) {
                redisBuyForMe.setData(siteSourcing.getData());
            }
            objectMap.put(pidKey, JSONObject.toJSONString(redisBuyForMe));
        } else {
            objectMap.put(pidKey, JSONObject.toJSONString(siteSourcing));
        }
        this.addCarToRedis(userId, objectMap);
    }


    /**
     * 检查bean数据并且异步获取PID数据
     *
     * @param siteSourcing
     */
    @Async
    public void checkAndLoadDataAsync(SiteSourcing siteSourcing) {
        String userId = String.valueOf(siteSourcing.getUserId());
        try {

            Map<String, Object> objectMap = this.getCarToRedis(userId);
            if (null == objectMap) {
                objectMap = new HashMap<>();
            }
            String pidKey = siteSourcing.getPid() + "_" + siteSourcing.getSiteFlag();
            if (objectMap.containsKey(pidKey)) {
                SiteSourcing tempBfm = JSONObject.parseObject(objectMap.get(pidKey).toString(), SiteSourcing.class);

                if (StrUtil.isEmpty(tempBfm.getImg())) {
                    // 判断ALIEXPRESS
                    if (SiteFlagEnum.ALIEXPRESS.getFlag() == siteSourcing.getSiteFlag()) {
                        CommonResult jsonResult = this.getAliExpressDetails(siteSourcing.getPid());
                        this.setSiteBuyForMeInfo(jsonResult, siteSourcing, userId);

                    } else if (SiteFlagEnum.TAOBAO.getFlag() == siteSourcing.getSiteFlag()) {
                        // TAOBAO
                        CommonResult jsonResult = this.getTaoBaoDetails(siteSourcing.getPid());
                        this.setSiteBuyForMeInfo(jsonResult, siteSourcing, userId);
                    }
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
            log.error("checkAndLoadDataAsync,siteBuyForMe[{}],userId[{}],error", siteSourcing, userId, e);
        }

    }


    /**
     * 设置获取data数据并且更新购物车
     *
     * @param jsonResult
     * @param siteSourcing
     */
    private void setSiteBuyForMeInfo(CommonResult jsonResult, SiteSourcing siteSourcing, String userId) {
        if (null != jsonResult && null != jsonResult.getData()) {

            // siteBuyForMe.setData(jsonResult.getData().toString());
            JSONObject jsonObject = JSONObject.parseObject(jsonResult.getData().toString());
            if (SiteFlagEnum.ALIEXPRESS.getFlag() == siteSourcing.getSiteFlag()) {
                String pic_url = jsonObject.getString("pic_url");
                String title = jsonObject.getString("title");
                siteSourcing.setImg(pic_url);
                siteSourcing.setName(title);
            } else if (SiteFlagEnum.TAOBAO.getFlag() == siteSourcing.getSiteFlag()) {
                if (jsonObject.containsKey("item")) {
                    String pic_url = jsonObject.getJSONObject("item").getString("pic_url");
                    String title = jsonObject.getJSONObject("item").getString("title");
                    siteSourcing.setImg(pic_url);
                    siteSourcing.setName(title);
                }
            }
            this.changeToUploadParamAndSave(siteSourcing, jsonObject);
            // 更新购物车
            this.setSiteBuyForMeCartInfo(siteSourcing, userId);
        }
    }

    /**
     * 更新购物车的图片和title
     *
     * @param siteSourcing
     * @param userId
     */
    private void setSiteBuyForMeCartInfo(SiteSourcing siteSourcing, String userId) {

        Map<String, Object> objectMap = this.getCarToRedis(userId);
        if (null == objectMap) {
            objectMap = new HashMap<>();
        }
        String pidKey = siteSourcing.getPid() + "_" + siteSourcing.getSiteFlag();
        if (objectMap.containsKey(pidKey)) {
            String carInfo = objectMap.get(pidKey).toString();
            SiteSourcing redisBuyForMe = JSONObject.parseObject(carInfo, SiteSourcing.class);
            redisBuyForMe.setImg(siteSourcing.getImg());
            redisBuyForMe.setName(siteSourcing.getName());
            /*if (StrUtil.isNotEmpty(siteBuyForMe.getData())) {
                redisBuyForMe.setData(siteBuyForMe.getData());
            }*/
            objectMap.put(pidKey, JSONObject.toJSONString(redisBuyForMe));
            this.addCarToRedis(userId, objectMap);
        }
    }


    /**
     * 保存到jack的数据库
     *
     * @param siteSourcing
     */
    private void changeToUploadParamAndSave(SiteSourcing siteSourcing, JSONObject jsonObject) {
        if (SiteFlagEnum.ALIEXPRESS.getFlag() == siteSourcing.getSiteFlag() || SiteFlagEnum.TAOBAO.getFlag() == siteSourcing.getSiteFlag()) {
            XmsChromeUploadParam uploadParam = new XmsChromeUploadParam();
            uploadParam.setImages(siteSourcing.getImg());
            uploadParam.setUsername(siteSourcing.getUserName());
            uploadParam.setUrl(siteSourcing.getUrl());
            uploadParam.setTitle(siteSourcing.getName());
            uploadParam.setPrice(jsonObject.getString("price"));
            uploadParam.setSku(jsonObject.getString("sku"));
            uploadParam.setProductDetail(jsonObject.toJSONString());
            uploadParam.setProductDescription(jsonObject.getString("desc"));
            xmsChromeUploadService.upload(uploadParam);
        }

    }


    public String checkAndLoadData(SiteSourcing siteSourcing) {
        // 判断ALIEXPRESS
        if (SiteFlagEnum.ALIEXPRESS.getFlag() == siteSourcing.getSiteFlag()) {
            CommonResult jsonResult = this.getAliExpressDetails(siteSourcing.getPid());
            if (null != jsonResult && null != jsonResult.getData()) {
                return jsonResult.getData().toString();
            }
        } else if (SiteFlagEnum.TAOBAO.getFlag() == siteSourcing.getSiteFlag()) {
            // TAOBAO
            CommonResult jsonResult = this.getTaoBaoDetails(siteSourcing.getPid());
            if (null != jsonResult && null != jsonResult.getData()) {
                return jsonResult.getData().toString();
            }
        }
        return null;
    }


    public String saveUploadImg(HttpServletRequest request, MultipartFile uploadFile) throws IOException {
        if (StrUtil.isEmpty(imgUploadPath)) {
            imgUploadPath = request.getSession().getServletContext().getRealPath(SOURCING_UPLOAD_IMG);
        }

        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));

        if (!uploadFile.isEmpty()) {

            String originalName = uploadFile.getOriginalFilename();
            String newFilename = System.currentTimeMillis() + originalName.substring(originalName.lastIndexOf(".")).toLowerCase();

            String filePathPre = (imgUploadPath + "/" + today).replace("\\", "/");

            File fileDir = new File(filePathPre);
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }
            File tempFile = new File(fileDir, newFilename);
            uploadFile.transferTo(tempFile);
            File compressFile = this.compressFile(tempFile);
            return null == compressFile ? null : (SOURCING_UPLOAD_IMG + "/" + today + "/" + compressFile.getName()).replace("\\", "/");
        }
        return null;
    }

    @Async
    public void imgSearchCache(File file, String uuid, String imgSearchUrl, String today) {
        imgSearchByTaoBao(file, uuid, today);
    }


    public boolean imgSearchCacheSync(File file, String uuid, String imgSearchUrl, String today) {
        return imgSearchByTaoBao(file, uuid, today);
    }


    public boolean imgSearchByTaoBao(File file, String uuid, String today) {
        boolean isSu = false;
        try {
            String url = microServiceConfig.getUrl() + instance.ZUUL_ALI_1688.replace("18005", "18003")
                    .replace("api/ali1688-service/", "");
            JSONObject json = instance.postFile(file, "file", url + "searchimg/upload");

            Map<String, Object> objectMap = redisUtil.hmgetObj(SOURCING_TAOBAO_IMG);
            if (objectMap == null) {
                objectMap = new HashMap<>();
            }
            if (json.getInteger("code") == 200 && StrUtil.isNotBlank(json.getString("data")) && StrUtil.isNotBlank(json.getJSONObject("data").getString("items"))) {
                objectMap.put(uuid, json.getJSONObject("data").getJSONObject("items"));
                objectMap.put(uuid + REDIS_TAOBAO_IMG, IMG_SEARCH_PATH.substring(1) + "/" + today
                        + "/" + file.getName());
                redisUtil.hmsetObj(SOURCING_TAOBAO_IMG, objectMap, EXPIRATION_TIME_7_DAY);
                isSu = true;
            } else {
                log.error("imgSearchByTaoBao file[{}],error:[{}]", file, json);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("imgSearchByTaoBao file[{}],error:", file, e);
        }
        return isSu;
    }


    public CommonResult getAliExpressDetails(String pid) {

        try {

            JSONObject jsonObject = instance.callUrlByGet(microServiceConfig.getUrl() + instance.ZUUL_ALI_1688 + "aliExpress/details/" + pid);
            if (null != jsonObject && jsonObject.containsKey("code") && jsonObject.getInteger("code") == 200) {
                JSONObject dataJson = jsonObject.getJSONObject("data");
                dataJson.put("desc", this.dealDesc(dataJson.getString("desc")));
                // 放入redis中
                redisUtil.hmsetObj(SOURCING_GOODS, pid + "_" + SiteFlagEnum.ALIEXPRESS.getFlag(), dataJson);
                return CommonResult.success(dataJson);
            } else {
                return CommonResult.failed(null == jsonObject ? "get data error" : jsonObject.toJSONString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("getAliExpressDetails pid[{}],  error:", pid, e);
            return CommonResult.failed(e.getMessage());
        }
    }


    public CommonResult getTaoBaoDetails(String pid) {

        try {

            JSONObject jsonObject = instance.callUrlByGet(microServiceConfig.getUrl() + instance.ZUUL_ALI_1688 + "searchimg/details/" + pid);
            if (null != jsonObject && jsonObject.containsKey("code") && jsonObject.getInteger("code") == 200) {
                JSONObject dataJson = jsonObject.getJSONObject("data");
                dataJson.put("desc", this.dealDesc(dataJson.getString("desc")));
                // 放入redis中
                redisUtil.hmsetObj(SOURCING_GOODS, pid + "_" + SiteFlagEnum.TAOBAO.getFlag(), dataJson);
                return CommonResult.success(dataJson);
            } else {
                return CommonResult.failed(null == jsonObject ? "get data error" : jsonObject.toJSONString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("taobaoDetails pid[{}],  error:", pid, e);
            return CommonResult.failed(e.getMessage());
        }
    }

    public CommonResult getAmazonDetails(UrlUtil instance, String pid) {

        try {

            JSONObject jsonObject = instance.callUrlByGet(microServiceConfig.getUrl() + instance.ZUUL_ALI_1688 + "amazon/details/" + pid);
            if (null != jsonObject && jsonObject.containsKey("code") && jsonObject.getInteger("code") == 200) {
                JSONObject dataJson = jsonObject.getJSONObject("data");
                dataJson.put("desc", this.dealDesc(dataJson.getString("desc")));
                return CommonResult.success(dataJson);
            } else {
                return CommonResult.failed(null == jsonObject ? "get data error" : jsonObject.toJSONString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("getAmazonDetails pid[{}],  error:", pid, e);
            return CommonResult.failed(e.getMessage());
        }
    }

    /**
     * 根据图片生成PID等数据
     *
     * @param siteSourcing
     */
    public void checkSiteFlagByImg(SiteSourcing siteSourcing) {
        // 如果PID是空的，则进行生成PID数据
        if (StrUtil.isEmpty(siteSourcing.getPid()) && StrUtil.isNotEmpty(siteSourcing.getImg())) {
            String otherUrlPid = getOtherUrlPid(siteSourcing.getImg());
            siteSourcing.setPid(otherUrlPid);
            siteSourcing.setSiteFlag(SiteFlagEnum.IMG_ONLY.getFlag());
        }
    }

    /**
     * 根据URL判断网站类型
     *
     * @param siteSourcing
     */
    public void checkSiteFlagByUrl(SiteSourcing siteSourcing) {
        if (null != siteSourcing && StrUtil.isNotBlank(siteSourcing.getUrl())) {
            String url = siteSourcing.getUrl();
            if (url.contains("?")) {
                url = url.substring(0, url.indexOf("?"));
                siteSourcing.setUrl(url);
            }
            SiteFlagEnum siteFlagEnum = Arrays.stream(SiteFlagEnum.values()).filter(e -> siteSourcing.getUrl().contains(e.getUrl())).findFirst().orElse(null);

            String pid;
            if (null != siteFlagEnum) {
                siteSourcing.setSiteFlag(siteFlagEnum.getFlag());
                siteSourcing.setCatid(siteFlagEnum.getCatid());
                switch (siteFlagEnum.getFlag()) {
                    case 1:
                    case 2:
                        // 解析aliexpress
                        pid = dealAliExpressOrTaoBaoUrl(siteSourcing.getUrl());
                        siteSourcing.setPid(pid);
                        break;
                    default:
                        break;
                }
            } else {
                // 如果没有匹配到网站，则给出默认的PID
                siteSourcing.setSiteFlag(SiteFlagEnum.OTHER.getFlag());
                pid = getOtherUrlPid(siteSourcing.getUrl());
                siteSourcing.setPid(pid);
                siteSourcing.setCatid(SiteFlagEnum.OTHER.getCatid());
            }

        }
    }


    /**
     * 根据URL获取匹配的PID数据
     *
     * @param url
     * @return
     */
    private String getOtherUrlPid(String url) {
        synchronized (SourcingUtils.class) {
            Object pid = redisUtil.hmgetObj(BUYFORME_PID, url);
            if (null == pid) {
                pid = String.valueOf((System.currentTimeMillis() + 1700000000000L));
                Map<String, Object> map = new HashMap<>();
                map.put(url, pid);
                redisUtil.hmsetObj(BUYFORME_PID, map, BUYFORME_PID_EXPIRE);
                return (String) pid;
            }
            return (String) pid;
        }
    }


    /**
     * 速卖通或者TAOBAO的URL处理
     *
     * @param url
     * @return
     */
    public String dealAliExpressOrTaoBaoUrl(String url) {
        if (StrUtil.isBlank(url)) {
            return null;
        }
        if (url.contains("?")) {
            url = url.substring(0, url.indexOf("?"));
        }
        String tempKey = url.split(".html")[0];
        return tempKey.substring(tempKey.lastIndexOf("/") + 1);
    }


    public void addCarToRedis(String sessionId, Map<String, Object> objectMap) {
        redisUtil.hmsetObj(SOURCING_CAR + sessionId, objectMap, EXPIRATION_TIME_7_DAY);
    }


    public Map<String, Object> getCarToRedis(String sessionId) {
        return redisUtil.hmgetObj(SOURCING_CAR + sessionId);
    }

    public String getOrderNo() {
        synchronized (SourcingUtils.class) {
            String yearAndMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            Random r = new Random();
            return "AP" + yearAndMonth + r.ints(1001, 9999).findFirst().getAsInt();
        }

    }


    public String change1688PythonPrice(String tempPrice) {
        double changePrice = (Double.parseDouble(tempPrice)
                * SourcingUtils.RATE_1688_PYTHON_PRICE) / this.exchangeRateUtils.getUsdToCnyRate();
        return new BigDecimal(changePrice).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
    }

    public String change1688TaoBaoPrice(String tempPrice) {
        double changePrice = (Double.parseDouble(tempPrice)
                * SourcingUtils.RATE_1688_TAOBAO_PRICE) / this.exchangeRateUtils.getUsdToCnyRate();
        return new BigDecimal(changePrice).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
    }


    /**
     * 检查侵权关键词
     */
    public boolean checkInfringemenKeyWord(String keyWord) {
        if (infringementSet == null || infringementSet.size() == 0) {
            initSet();
        }
        return checkContains(keyWord.toLowerCase());
    }


    private boolean checkContains(String keyWord) {
        long count = infringementSet.stream().filter(keyWord::contains).count();
        return count > 0;
    }

    /*
     * 图片压缩实现
     *
     * @param sourceFile
     * @return
     */
    public File compressFile(File sourceFile) {
        File tempFile;
        InputStream in = null;
        try {
            in = new FileInputStream(sourceFile);
            tempFile = new File(sourceFile.getParentFile().getAbsolutePath().replace("\\", "/")
                    + "/cpf" + sourceFile.getName());

            float rate = 0f;
            if (sourceFile.length() > 1024 * 400) {
                // 按照文件大小
                rate = 1024f * 400 / sourceFile.length();
                // Thumbnails.of(in).scale(rate).outputQuality(rate).toFile(tempFile);
                Thumbnails.of(in).scale(rate).toFile(tempFile);
            } else {
                BufferedImage bimg = ImageIO.read(sourceFile);
                if (null != bimg && bimg.getWidth() > 280) {
                    // 按照宽度计算压缩率
                    rate = 280f / bimg.getWidth();
                    Thumbnails.of(in).scale(rate).toFile(tempFile);
                }
            }
            if (rate > 0) {
                return tempFile;
            } else {
                return sourceFile;
            }
        } catch (IOException e) {
            log.error("error", e);
            log.error("compressFile sourceFile[{}], ", sourceFile, e);

            return sourceFile;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }





    public void deleteRedisCar(String sessionId, int useId, String cookieId) {

        // System.err.println("deleteRedisCar:");
        // Arrays.stream(Thread.currentThread().getStackTrace()).forEach(System.err::println);

        Map<String, Object> objectMap = new HashMap<>();
        if (StrUtil.isNotBlank(sessionId)) {
            redisUtil.hmsetObj(SOURCING_CAR + sessionId, objectMap, EXPIRATION_TIME_1_SECOND);
        }
        if (useId > 0) {
            redisUtil.hmsetObj(SOURCING_CAR + useId, objectMap, EXPIRATION_TIME_1_SECOND);
        }
        if (StrUtil.isNotBlank(cookieId)) {
            redisUtil.hmsetObj(SOURCING_CAR + cookieId, objectMap, EXPIRATION_TIME_1_SECOND);
        }

    }


    public String dealDesc(String oldDesc) {
        if (StrUtil.isNotBlank(oldDesc)) {
            Document descDoc = Jsoup.parseBodyFragment(oldDesc);
            Elements aList = descDoc.getElementsByTag("a");
            if (null != aList && aList.size() > 0) {
                aList.remove();
            }
            descDoc.select(".bottom-recommendation").remove();
            // kse:widget
            descDoc.getElementsByTag("kse:widget").remove();
            Elements imgList = descDoc.getElementsByTag("img");
            Document descNew = new Document("desc");
            if (null != imgList && imgList.size() > 0) {
                StringBuffer sb = new StringBuffer();
                for (Element img : imgList) {
                    sb.append(img.outerHtml());
                }
                imgList.clear();
                return Jsoup.parseBodyFragment(sb.toString()).html();
            }
            return "";
        } else {
            return oldDesc;
        }
    }

    public String getHost(HttpServletRequest request, String today, File file) {
        return request.getRequestURL().toString().replace(IMG_SEARCH_CACHE, "") + "/" + IMG_SEARCH_PATH.substring(1) + "/" + today
                + "/" + file.getName();
    }

    public String getHost(HttpServletRequest request, String webImg) {
        return request.getRequestURL().toString().replace(IMG_SEARCH_CACHE, "") + webImg.replace("\\", "/");
    }


    public String getSearchParam(String sort) {
        /**
         * 0 Best Match, 1 Price Low To High,
         * 2 Best Selling 3 New Arrivals
         *
         * sort:排序[bid,_bid,_sale,_new]
         *   (bid:总价,sale:销量,new上架时间,加_前缀为从大到小排序)
         */
        switch (sort) {
            case "0":
                return "";
            case "1":
                return "bid";
            case "2":
                return "_sale";
            case "3":
                return "_new";
            default:
                return "";
        }
    }

    public String getPidByUrl(String url) {
        // https://detail.1688.com/offer/621729131265.html&idx=0?spm=a312h.2018_new_sem.dh_002.1.af3510d9CAhfYS&file=621729131265.html&idx=0
        if (StrUtil.isNotBlank(url) && url.contains(".html")) {
            return url.substring(url.indexOf("/offer/") + 7, url.indexOf(".html"));
        }
        return null;
    }


    private static void initSet() {
        infringementSet = new HashSet<>();
        infringementSet.add("caroon");
        infringementSet.add("disney");
        infringementSet.add("frozen");
        infringementSet.add("supreme");
        infringementSet.add("burberry");
        infringementSet.add("oakley");
        infringementSet.add("jawbreaker");
        infringementSet.add("evzero");
        infringementSet.add("fall line");
        infringementSet.add("juliet");
        infringementSet.add("x squared");
        infringementSet.add("gucci");
        infringementSet.add("cosplay");
        infringementSet.add("lv");
        infringementSet.add("louis vullton");
        infringementSet.add("chanel");
        infringementSet.add("hermes");
        infringementSet.add("valentino");
        infringementSet.add("ferragamo");
        infringementSet.add("zegne");
        infringementSet.add("armani");
        infringementSet.add("coach");
        infringementSet.add("kenzo");
        infringementSet.add("celine");
        infringementSet.add("chloe");
        infringementSet.add("cartier");
        infringementSet.add("tiffany");
        infringementSet.add("jimmy choo");
        infringementSet.add("birkin");
        infringementSet.add("kelly");
        infringementSet.add("lindy");
        infringementSet.add("miumiu");
        infringementSet.add("westwood");
        infringementSet.add("michael");
        infringementSet.add("kor");
        infringementSet.add("mcm");
        infringementSet.add("ugg");
        infringementSet.add("loewe");
        infringementSet.add("bottega veneta");
        infringementSet.add("bally");
        infringementSet.add("tods");
        infringementSet.add("prada");
        infringementSet.add("furla");
        infringementSet.add("moncler");
        infringementSet.add("versace");
        infringementSet.add("goyard");
        infringementSet.add("coccinelle");
        infringementSet.add("boss");
        infringementSet.add("tommy");
        infringementSet.add("jeanswest");
        infringementSet.add("nike");
        infringementSet.add("adidas");
        infringementSet.add("puma");
        infringementSet.add("rebbot");
        infringementSet.add("piaget");
        infringementSet.add("chaumet");
        infringementSet.add("bt21");
        infringementSet.add("bts");
        infringementSet.add("levi's");
        infringementSet.add("gxg");
        infringementSet.add("gap");
        infringementSet.add("h&m");
        infringementSet.add("uniqlo");
        infringementSet.add("mlb");
        infringementSet.add("chrownheart");
        infringementSet.add("montblanc");
        infringementSet.add("omega");
        infringementSet.add("longines");
        infringementSet.add("champion");
        infringementSet.add("chopard");
        infringementSet.add("blancpain");
        infringementSet.add("breguet");
        infringementSet.add("bvlgari");
        // sunglasses, gloves
        infringementSet.add("sunglasses");
        infringementSet.add("sunglass");
        infringementSet.add("sun glasses");
        infringementSet.add("sun glass");
        infringementSet.add("gloves");
        infringementSet.add("glove");
        infringementSet.add("glasses");
        infringementSet.add("hand bags");
        infringementSet.add("hand bag");
    }

    public void main(String[] args) {
        for (int i = 0; i < 30; i++) {
            System.err.println(getOrderNo());
        }
    }
}
