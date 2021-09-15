package com.macro.mall.shopify.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.entity.*;
import com.macro.mall.mapper.*;
import com.macro.mall.shopify.config.ShopifyConfig;
import com.macro.mall.shopify.config.ShopifyRestTemplate;
import com.macro.mall.shopify.exception.ShopifyException;
import com.macro.mall.shopify.pojo.AddProductBean;
import com.macro.mall.shopify.pojo.ProductRequestWrap;
import com.macro.mall.shopify.pojo.ShopifyData;
import com.macro.mall.shopify.pojo.SkuVal;
import com.macro.mall.shopify.pojo.product.*;
import com.macro.mall.shopify.service.XmsShopifyProductService;
import com.macro.mall.shopify.util.StrUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ShopifyService实现类
 * Created by zlw on 2021/5/10.
 */
@Service
public class XmsShopifyProductServiceImpl implements XmsShopifyProductService {

    private static final Logger LOGGER = LoggerFactory.getLogger(XmsShopifyProductServiceImpl.class);
    private static String chineseChar = "([\\一-\\龥]+)";//()表示匹配字符串，[]表示在首尾字符范围  从 \\一 到 \\龥字符之间，+号表示至少出现一次

    @Autowired
    private XmsPmsProductEditMapper productMapper;
    @Autowired
    private XmsPmsSkuStockEditMapper skuStockMapper;
    @Autowired
    private XmsShopifyPidInfoMapper shopifyPidInfoMapper;
    @Autowired
    private XmsShopifyAuthMapper xmsShopifyAuthMapper;

    private final ShopifyRestTemplate shopifyRestTemplate;

    private final ShopifyConfig config;
    @Autowired
    private XmsShopifyCollectionsMapper xmsShopifyCollectionsMapper;
    @Autowired
    private XmsCustomerProductMapper customerProductMapper;
    @Autowired
    private XmsSourcingListMapper sourcingListMapper;

    public XmsShopifyProductServiceImpl(XmsShopifyPidInfoMapper shopifyPidInfoMapper, ShopifyConfig config, ShopifyRestTemplate shopifyRestTemplate) {
        this.shopifyPidInfoMapper = shopifyPidInfoMapper;
        this.config = config;
        this.shopifyRestTemplate = shopifyRestTemplate;
    }

    @Override
    public CommonResult pushProduct(AddProductBean addProductBean) {
        try {

            LOGGER.info("begin push addProductBean[{}] to shopify[{}]", addProductBean, addProductBean.getShopName());
            ProductRequestWrap wrap = new ProductRequestWrap();
            wrap.setPid(addProductBean.getPid());
            wrap.setSourcingId(addProductBean.getSourcingId());
            wrap.setPublished("1".equalsIgnoreCase(addProductBean.getPublished()));
            wrap.setShopname(addProductBean.getShopName());
            List<String> skuList = Arrays.asList(addProductBean.getSkuCodes().split(","));
            wrap.setSkus(skuList);

            if (StrUtil.isNotBlank(addProductBean.getCollectionId())) {
                wrap.setCollectionId(addProductBean.getCollectionId());
            }
            if (StrUtil.isNotBlank(addProductBean.getProductTags())) {
                wrap.setProductTags(addProductBean.getProductTags());
            }
            if (StrUtil.isNotBlank(addProductBean.getProductType())) {
                wrap.setProductType(addProductBean.getProductType());
            }


            ProductWraper wraper = this.pushProductWFW(wrap, addProductBean.getMemberId());
            if (wraper != null && wraper.getProduct() != null && wraper.getProduct().getId() != 0L && !wraper.isPush()) {
                return CommonResult.success("PUSH SUCCESSED");
            } else if (wraper != null && wraper.isPush()) {
                return CommonResult.failed("PRODUCT HAD PUSHED");
            } else {
                return CommonResult.failed("NO PRODUCT TO PUSH");
            }

        } catch (Exception e) {
            LOGGER.error("PUSH PRODUCT FAILED:", e);
            return CommonResult.failed(e.getMessage());
        }
    }

    public ProductWraper pushProductWFW(ProductRequestWrap wrap, Long memberId) throws ShopifyException {
        //验证是否已经铺货过
        ProductWraper productWraper = checkPush(wrap.getShopname(), wrap.getPid());
        if (productWraper != null) {
            return productWraper;
        }
        // 产品信息数据查询
        XmsPmsProductEdit pmsProduct = this.productMapper.selectById(Long.valueOf(wrap.getPid()));
        ShopifyData goods = composeShopifyData(pmsProduct, wrap.getSite());

        QueryWrapper<XmsPmsSkuStockEdit> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(XmsPmsSkuStockEdit::getProductId, pmsProduct.getProductId());
        List<XmsPmsSkuStockEdit> skuList = this.skuStockMapper.selectList(queryWrapper);

        List<XmsPmsSkuStockEdit> collect = skuList.stream().filter(e -> wrap.getSkus().contains(e.getSkuCode())).collect(Collectors.toList());
        goods.setSkuList(collect);

        goods.setSkus(wrap.getSkus());
        goods.setPublished(wrap.isPublished());
        goods.setBodyHtml(wrap.isBodyHtml());
        return this.onlineProduct(wrap, goods, memberId);
    }

    /**
     * mongo 数据转换
     *
     * @param goods
     * @param site
     * @return
     */
    public static ShopifyData composeShopifyData(XmsPmsProductEdit goods, int site) {

        ShopifyData data = new ShopifyData();
        data.setPid(String.valueOf(goods.getProductId()));
        data.setInfo(detail(goods));
        data.setInfoHtml(goods.getDetailHtml());
        data.setName(goods.getName());
        data.setPrice(goods.getPriceXj());
        data.setImage(image(goods));
        data.setPerWeight(String.valueOf(goods.getWeight()));
        data.setCategory(String.valueOf(goods.getProductCategoryId()));
        data.setTags(goods.getShopifyTags());
        data.setProductType(goods.getShopifyType());
        data.setPerWeight(String.valueOf(goods.getWeight().setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()));
        return data;
    }

    public Product toProduct(ShopifyData goods) throws ShopifyException {
        Product product = new Product();
        product.setId(Long.parseLong(goods.getPid()));
        product.setTitle(goods.getName());
        // product.setPublished(goods.isPublished());
        product.setPublished(true);
        if (goods.isBodyHtml()) {
            String info_ori = goods.getInfoHtml();
            StringBuilder details = details(goods.getInfo());
            details.append(info_ori);
            product.setBody_html(details.toString());
        } else {
            product.setBody_html("");
        }

        product.setVendor(goods.getVendor());
        product.setProduct_type(goods.getCategory());
        product.setTags(goods.getTags());
        product.setProduct_type(goods.getProductType());
        product.setWeight_value(goods.getPerWeight());
        product.setPublished_scope("web");
        //product.setPublished_at();
        //product.setTemplate_suffix("");
        OptionWrap wrap;
        try {
            wrap = optionVariant(goods.getSkuList());
        } catch (Exception e) {
            LOGGER.error("Option and Variant error", e.getMessage());
            throw new ShopifyException("1005", e.getMessage());
        }

        if (wrap != null && wrap.getOptions() == null) {
            throw new ShopifyException("Product options has something wrong");
        } else if (wrap != null) {
            product.setOptions(wrap.getOptions());
        }

        List<Variants> lstVariants = wrap.getVariants();
        if (lstVariants.isEmpty()) {
            Variants variant = variant(goods.getPrice(), goods.getPerWeight());
            lstVariants.add(variant);
        }
        product.setVariants(lstVariants);


        List<String> lstImg = goods.getImage();
        lstImg.addAll(wrap.getLstImages());
        List<Images> lstImages = images(lstImg);
        if (lstImages.isEmpty()) {
            throw new ShopifyException("Product has no images");
        }
        product.setImages(lstImages);
        return product;
    }

    /**
     * 图片
     *
     * @param pImage
     * @return
     */
    private List<Images> images(List<String> pImage) throws ShopifyException {
        if (pImage == null || pImage.isEmpty()) {
            throw new ShopifyException("The image is empty");
        }
        List<Images> lstImages = Lists.newArrayList();
        Set<String> setImage = Sets.newHashSet(pImage);
        Images images;
        Iterator<String> iterator = setImage.iterator();
        while (iterator.hasNext()) {

//                String imgSrc = iterator.next().replace(".60x60", ".400x400");
            images = new Images();
            images.setSrc(iterator.next());

            lstImages.add(images);

        }
        return lstImages;
    }

    private Variants variant(String goodsPrice, String weight) throws ShopifyException {
        if (StringUtils.isBlank(goodsPrice)) {
            throw new ShopifyException("The price is not valid");
        }
        goodsPrice = goodsPrice.split("-")[0];
        Variants variants = new Variants();
        variants.setPrice(goodsPrice);
        variants.setRequires_shipping(true);
        variants.setWeight(weight);
        variants.setWeight_unit("kg");
        variants.setCountry_code_of_origin("CN");
        variants.setInventory_policy("deny");
        variants.setInventory_quantity(9999);
        variants.setInventory_management("shopify");
        List<PresentmentPrices> presentment_prices = Lists.newArrayList();
        PresentmentPrices prices = new PresentmentPrices();
//        prices.setCompare_at_price(null);
        Price price = new Price();
        price.setAmount(goodsPrice);
        price.setCurrency_code("USD");
        prices.setPrice(price);
        presentment_prices.add(prices);
        variants.setPresentment_prices(presentment_prices);
        return variants;
    }

    /**
     * 明细
     *
     * @param detail
     * @return
     */
    private StringBuilder details(List<String> detail) {
        StringBuilder sb = new StringBuilder();
        if (detail != null && !detail.isEmpty()) {
            sb.append("<div>");
            detail.stream().forEach(d -> {
                sb.append("<span style=\"margin-left: 10px;\">").append(d).append("</span><br>");
            });
            sb.append("</div");
        }
        return sb;
    }

    private static List<String> detail(XmsPmsProductEdit goods) {
        String detail = goods.getDescription();
        List<String> list = Lists.newArrayList();
        if (StringUtils.isNotBlank(detail) && detail.length() > 2) {
            String[] details = detail.substring(1, detail.length() - 1).split(", ");
            int details_length = details.length;
            for (int i = 0; i < details_length; i++) {
                String str_detail = details[i].trim().replaceAll(chineseChar, "");
                if (str_detail.isEmpty() || StrUtils.isMatch(str_detail.substring(0, 1), "\\d+")) {
                    continue;
                }
                if (StrUtils.isFind(str_detail, "(brand\\:)")) {
                    continue;
                }
                if (str_detail.length() < 2) {
                    continue;
                }
                if (StrUtils.isFind(str_detail, "(([uU][0-9a-f]+){2,})")) {
                    continue;
                }
                list.add(str_detail.substring(0, 1).toUpperCase() + str_detail.substring(1, str_detail.length()));
            }
        }

        return list;
    }

    private static List<String> image(XmsPmsProductEdit goods) {
        List<String> imgList = Lists.newArrayList();
        String img = goods.getAlbumPics();
        if (StringUtils.isNotBlank(img)) {
            img = img.replace("[", "")
                    .replace("]", "")
                    .replaceAll("http://", "https://").trim();
            String[] imgs = img.split(",\\s*");
            for (int i = imgs.length - 1; i >= 0; i--) {
                if (imgs[i].contains("http://") || imgs[i].contains("https://")) {
                    imgList.add(imgs[i].replaceAll("http://", "https://"));
                } else {
                    imgList.add(imgs[i]);
                }
            }
        }
        String imgMain = goods.getPic();
        if (StringUtils.isNotBlank(imgMain)) {
            imgList.add(imgMain.replaceAll("http://", "https://"));
        }
        return imgList;
    }

    private ProductWraper checkPush(String shopName, String pid) {
        XmsShopifyPidInfo shopifyBean = checkProduct(shopName, pid);
        if (shopifyBean != null && StringUtils.isNotBlank(shopifyBean.getShopifyPid())) {
            ProductWraper wraper = new ProductWraper();
            if (StringUtils.isNotBlank(shopifyBean.getShopifyInfo())) {
                wraper = JSON.parseObject(shopifyBean.getShopifyInfo(), ProductWraper.class);
            }
            if (shopifyBean.getPublish() > 0) {
                wraper.setPush(true);
                return wraper;
            }
        }
        return null;
    }

    public XmsShopifyPidInfo checkProduct(String shopname, String itemId) throws ShopifyException {
        XmsShopifyPidInfo shopifyBean = new XmsShopifyPidInfo();
        shopifyBean.setShopifyName(shopname);
        shopifyBean.setPid(itemId);
        return selectShopifyId(shopifyBean);
    }

    public XmsShopifyPidInfo selectShopifyId(XmsShopifyPidInfo shopifyBean) {
        LambdaQueryWrapper<XmsShopifyPidInfo> lambdaQuery = Wrappers.lambdaQuery();
        lambdaQuery.eq(XmsShopifyPidInfo::getShopifyName, shopifyBean.getShopifyName())
                .eq(XmsShopifyPidInfo::getPid, shopifyBean.getPid());
        return shopifyPidInfoMapper.selectOne(lambdaQuery);
    }

    public ProductWraper onlineProduct(ProductRequestWrap wrap, ShopifyData goods, Long memberId) throws ShopifyException {
        String shopname = wrap.getShopname();
        Product product = toProduct(goods);
        if (StrUtil.isNotBlank(wrap.getCollectionId())) {
            product.setCollection_id(wrap.getCollectionId());
        }
        if (StrUtil.isNotBlank(wrap.getProductType())) {
            product.setProduct_type(wrap.getProductType());
        }
        if (StrUtil.isNotBlank(wrap.getProductTags())) {
            JSONArray jsonArray = new JSONArray();
            jsonArray.addAll(Arrays.asList(wrap.getProductTags().split(",")));
            product.setTags(jsonArray.toJSONString());
        }

        XmsShopifyPidInfo shopifyBean = new XmsShopifyPidInfo();
        shopifyBean.setShopifyName(shopname);
        shopifyBean.setPid(goods.getPid());
        XmsShopifyPidInfo shopifyId = selectShopifyId(shopifyBean);
        if (shopifyId != null) {
            product.setId(Long.parseLong(shopifyId.getShopifyPid()));
        }
        ProductWraper productWraper = new ProductWraper();
        productWraper.setProduct(product);
        productWraper = this.addProduct(shopname, productWraper, goods.getPid());

        if (productWraper != null && productWraper.getProduct() != null) {

            // 如果有collectionId则绑定数据
            this.dealShopifyCollections(wrap, productWraper);

            // 铺货完成后，绑定店铺数据信息，方便下单后对应ID获取我们产 品ID
            shopifyBean.setShopifyPid(String.valueOf(productWraper.getProduct().getId()));
            shopifyBean.setShopifyInfo(JSONObject.toJSONString(productWraper));
            shopifyBean.setPublish(product.isPublished() ? 1 : 0);
            shopifyBean.setCreateTime(new Date());
            this.insertShopifyIdWithPid(shopifyBean);

            this.updateYouLiveProduct(Long.parseLong(goods.getPid()), wrap.getSourcingId(), productWraper.getProduct(), goods.getPrice(), memberId);

            // 异步抓取商品信息

        }
        return productWraper;
    }


    private void dealShopifyCollections(ProductRequestWrap wrap, ProductWraper productWraper) {
        try {
            // 如果有collectionId则绑定数据
            QueryWrapper<XmsShopifyAuth> authQueryWrapper = new QueryWrapper<>();
            authQueryWrapper.lambda().eq(XmsShopifyAuth::getShopName, wrap.getShopname());
            XmsShopifyAuth shopifyAuth = this.xmsShopifyAuthMapper.selectOne(authQueryWrapper);
            String token = shopifyAuth.getAccessToken();

            if (StrUtil.isNotBlank(wrap.getCollectionId())) {
                QueryWrapper<XmsShopifyCollections> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(XmsShopifyCollections::getCollectionsId, Long.parseLong(wrap.getCollectionId()));
                XmsShopifyCollections shopifyCollections = this.xmsShopifyCollectionsMapper.selectOne(queryWrapper);
                if (null != shopifyCollections) {
                    String collKey = shopifyCollections.getCollKey();
                    if ("custom_collections".equalsIgnoreCase(collKey)) {
                        /**
                         * PUT /admin/api/2021-07/custom_collections/841564295.json
                         * {
                         *   "custom_collection": {
                         *     "id": 841564295,
                         *     "collects": [
                         *       {
                         *         "product_id": 921728736,
                         *         "position": 1
                         *       },
                         *       {
                         *         "id": 455204334,
                         *         "position": 2
                         *       }
                         *     ]
                         *   }
                         * }
                         */
                        JSONObject jsonObject = new JSONObject();
                        JSONObject custom_collectionJson = new JSONObject();
                        custom_collectionJson.put("id", wrap.getCollectionId());
                        JSONArray collectsArr = new JSONArray();
                        JSONObject product_json = new JSONObject();
                        product_json.put("product_id", productWraper.getProduct().getId());
                        product_json.put("position", "1");
                        collectsArr.add(product_json);
                        custom_collectionJson.put("collects", collectsArr);
                        jsonObject.put("custom_collection", custom_collectionJson);

                        //请求数据

                        String returnJson = shopifyRestTemplate.put(String.format(config.SHOPIFY_URI_PUT_CUSTOM_COLLECTIONS, wrap.getShopname(), wrap.getCollectionId()), token, jsonObject);
                        //返回结果
                        /**
                         * {
                         *   "custom_collection": {
                         *     "handle": "ipods",
                         *     "id": 841564295,
                         *     "updated_at": "2021-07-01T15:10:06-04:00",
                         *     "published_at": "2008-02-01T19:00:00-05:00",
                         *     "sort_order": "manual",
                         *     "template_suffix": null,
                         *     "published_scope": "web",
                         *     "title": "IPods",
                         *     "body_html": "<p>The best selling ipod ever</p>",
                         *     "admin_graphql_api_id": "gid://shopify/Collection/841564295",
                         *     "image": {
                         *       "created_at": "2021-07-01T14:49:47-04:00",
                         *       "alt": "iPod Nano 8gb",
                         *       "width": 123,
                         *       "height": 456,
                         *       "src": "https://cdn.shopify.com/s/files/1/0006/9093/3842/collections/ipod_nano_8gb.jpg?v=1625165387"
                         *     }
                         *   }
                         * }
                         */
                        JSONObject rsJson = JSONObject.parseObject(returnJson);
                        if (null == rsJson || !rsJson.containsKey("custom_collection")) {
                            System.err.println("-----------custom_collection put error:[" + returnJson + "]");
                        }
                    } else {
                        /**
                         *PUT /admin/api/2021-07/smart_collections/482865238/order.json?products[]=921728736&products[]=632910392
                         *
                         */
                        Map<String, Object> map = new HashMap<>();
                        String url = String.format(config.SHOPIFY_URI_PUT_SMART_COLLECTIONS, wrap.getShopname(), wrap.getCollectionId()) + "?products[]=" + productWraper.getProduct().getId();
                        String returnJson = shopifyRestTemplate.put(url, token, map);
                        if (null == returnJson) {
                            System.err.println("-----------smart_collections put error:[" + returnJson + "]");
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * insertShopifyIdWithPid
     *
     * @param shopifyBean
     * @return
     */
    public int insertShopifyIdWithPid(XmsShopifyPidInfo shopifyBean) {
        XmsShopifyPidInfo sopifyId = selectShopifyId(shopifyBean);
        int result = 0;
        if (sopifyId != null) {
            UpdateWrapper<XmsShopifyPidInfo> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("shopify_name", shopifyBean.getShopifyName()).eq("pid", shopifyBean.getPid());
            XmsShopifyPidInfo bean = new XmsShopifyPidInfo();

            BeanUtil.copyProperties(sopifyId, bean);
            bean.setUpdateTime(new Date());
            bean.setPublish(shopifyBean.getPublish());
            bean.setShopifyInfo(shopifyBean.getShopifyInfo());
            result = shopifyPidInfoMapper.update(bean, updateWrapper);
        } else {
            shopifyBean.setUpdateTime(new Date());
            result = shopifyPidInfoMapper.insert(shopifyBean);
        }
        return result;
    }

    /**
     * 更新YouLiveProduct表关联shopify的productId
     *
     * @param productId
     * @param product
     */
    public void updateYouLiveProduct(Long productId, Long sourcingId, Product product, String price, Long memberId) {
        Long shopifyProductId = product.getId();
        QueryWrapper<XmsCustomerProduct> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(XmsCustomerProduct::getProductId, productId)
                .eq(XmsCustomerProduct::getMemberId, memberId)
                .eq(XmsCustomerProduct::getSourcingId, sourcingId);
        XmsCustomerProduct customerProduct = this.customerProductMapper.selectOne(queryWrapper);
        if (null != customerProduct) {
            UpdateWrapper<XmsCustomerProduct> updateWrapper = new UpdateWrapper<>();
            updateWrapper.lambda()
                    .set(XmsCustomerProduct::getShopifyProductId, shopifyProductId)
                    .set(XmsCustomerProduct::getShopifyPrice, price)
                    .set(XmsCustomerProduct::getUpdateTime, new Date())
                    .set(XmsCustomerProduct::getSyncTime, new Date())
                    .eq(XmsCustomerProduct::getId, customerProduct.getId());
            this.customerProductMapper.update(null, updateWrapper);

            // 铺货成功后，设置sourcing的状态
            UpdateWrapper<XmsSourcingList> sourcingWrapper = new UpdateWrapper<>();
            sourcingWrapper.lambda().eq(XmsSourcingList::getProductId, productId).eq(XmsSourcingList::getMemberId, memberId)
                    .set(XmsSourcingList::getAddProductFlag, 1);
            this.sourcingListMapper.update(null, sourcingWrapper);
        }
    }


    /**
     * 解析sku,转成shopify网站的Options以及Variants
     *
     * @param skuList
     */
    public OptionWrap optionVariant(List<XmsPmsSkuStockEdit> skuList) throws Exception {
        if (skuList == null || skuList.isEmpty()) {
            return OptionWrap.builder().lstImages(Lists.newArrayList())
                    .options(Lists.newArrayList()).variants(Lists.newArrayList()).build();
        }
        List<String> image = Lists.newArrayList();
        List<Variants> lstVariants = Lists.newArrayList();
        List<Options> lstOptions = Lists.newArrayList();
        Map<String, Options> optionMap = Maps.newHashMap();
        Variants variants;
        for (int i = 0; i < skuList.size(); i++) {
            variants = new Variants();
            variants.setPrice(String.valueOf(skuList.get(i).getPrice()));
            variants.setSku(skuList.get(i).getSkuCode());
            variants.setRequires_shipping(true);
            variants.setWeight(String.valueOf(skuList.get(i).getWeight()));
            variants.setWeight_unit("kg");
            variants.setCountry_code_of_origin("CN");
            variants.setInventory_policy("deny");
            variants.setInventory_quantity(skuList.get(i).getStock());
            variants.setInventory_management("shopify");
//            variants.setCompare_at_price(String.valueOf(skuList.get(i).getComparedAtPrice()));
            image.add(skuList.get(i).getPic());
            // 规格数据
            Gson gson = new Gson();
            List<SkuVal> list = gson.fromJson(skuList.get(i).getSpData(),
                    new TypeToken<List<SkuVal>>() {
                    }.getType());

            for (int j = 0; j < list.size(); j++) {
                options(optionMap, list.get(j));
                if (j == 0) {
                    variants.setOption1(list.get(j).getValue());
                } else if (j == 1) {
                    variants.setOption2(list.get(j).getValue());
                } else if (j == 2) {
                    variants.setOption3(list.get(j).getValue());
                } else {

                }
            }
            lstVariants.add(variants);
        }

        optionMap.entrySet().stream().forEach(o -> lstOptions.add(o.getValue()));
        return OptionWrap.builder().lstImages(image)
                .options(lstOptions).variants(lstVariants).build();
    }

    /**
     * options转换
     *
     * @param optionMap
     * @param typeBean
     */
    private void options(Map<String, Options> optionMap, SkuVal typeBean) {
        Options options = optionMap.get(typeBean.getKey());
        options = options == null ? new Options() : options;
        options.setName(typeBean.getKey());
        List<String> values = options.getValues();
        values = values == null ? Lists.newArrayList() : values;
        if (!values.contains(typeBean.getValue())) {
            values.add(typeBean.getValue());
            values = values.stream().sorted().collect(Collectors.toList());
        }
        options.setValues(values);
        optionMap.put(typeBean.getKey(), options);
    }


    /**
     * 铺货到shopify
     *
     * @param shopName
     * @param productWraper
     * @return
     */
    public ProductWraper addProduct(String shopName, ProductWraper productWraper, String productId) {

        Assert.notNull(productWraper, "product object is null");
        LOGGER.info("shopName:[{}] productWraper:[{}]", shopName, productWraper);
        ProductWraper result = new ProductWraper();
        try {
            Gson gson = new Gson();
            PushPrduct wrap = new PushPrduct();
            wrap.setProduct(productWraper.getProduct());
            String json = gson.toJson(wrap);

            // 判断是否 已经铺货

            QueryWrapper<XmsShopifyAuth> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(XmsShopifyAuth::getShopName, shopName);
            XmsShopifyAuth shopifyAuth = this.xmsShopifyAuthMapper.selectOne(queryWrapper);
            String token = shopifyAuth.getAccessToken();

            QueryWrapper<XmsShopifyPidInfo> pidInfoQueryWrapper = new QueryWrapper<>();
            pidInfoQueryWrapper.lambda().eq(XmsShopifyPidInfo::getPid, productId);
            XmsShopifyPidInfo xmsShopifyPidInfo = this.shopifyPidInfoMapper.selectOne(pidInfoQueryWrapper);

            LOGGER.info("add product to myself shop:[{}]", shopName);
            String returnJson;
            if (null != xmsShopifyPidInfo) {

                JSONObject saveObject = new JSONObject();

                JSONObject product = new JSONObject();


                // 获取最新的解析json数据
                JSONObject oldJson = JSONObject.parseObject(xmsShopifyPidInfo.getShopifyInfo()).getJSONObject("product");
                JSONArray oldImgs = oldJson.getJSONArray("images");
                JSONArray oldVariants = oldJson.getJSONArray("variants");
                JSONArray oldOptions = oldJson.getJSONArray("options");

                Product saveProduct = productWraper.getProduct();
                // 1. 匹配imgs，多余的新增

                JSONArray newImgs = new JSONArray();
                if (CollectionUtil.isNotEmpty(saveProduct.getImages())) {
                    List<Images> productImages = saveProduct.getImages();
                    int currImgCount = productImages.size();
                    if (null == oldImgs || oldImgs.size() == 0) {
                        for (int i = 0; i < currImgCount; i++) {
                            JSONObject tmJson = new JSONObject();
                            tmJson.put("src", productImages.get(i).getSrc());
                            newImgs.add(tmJson);
                        }
                    } else {
                        if (currImgCount <= oldImgs.size()) {
                            // 标识 当前的图片比原来的少，需要移除
                            // 匹配前面currImgCount 取oldImgs.size()填入，然后 新增img
                            for (int i = 0; i < currImgCount; i++) {
                                JSONObject tmJson = oldImgs.getJSONObject(i);
                                JSONObject nwJson = new JSONObject();
                                nwJson.put("id", tmJson.getString("id"));
                                nwJson.put("src", productImages.get(i).getSrc());
                                newImgs.add(nwJson);
                            }
                        } else {
                            for (int i = oldImgs.size(); i < currImgCount - 1; i++) {
                                JSONObject tmJson = new JSONObject();
                                tmJson.put("src", productImages.get(i).getSrc());
                                newImgs.add(tmJson);
                            }
                        }
                    }
                }
                product.put("images", newImgs);


                // 2.匹配 variants，多余的新增
                JSONArray newVariants = new JSONArray();
                if (CollectionUtil.isNotEmpty(saveProduct.getVariants())) {
                    List<Variants> variants = saveProduct.getVariants();

                    // PUT和POST
                    if (null == oldVariants || oldVariants.size() == 0) {
                        for (int i = 0; i < variants.size(); i++) {
                            JSONObject tmJson = new JSONObject();
                            tmJson.put("price", variants.get(i).getPrice());
                            tmJson.put("grams", variants.get(i).getGrams());
                            tmJson.put("title", variants.get(i).getTitle());
                            tmJson.put("product_id", xmsShopifyPidInfo.getShopifyPid());
                            tmJson.put("sku", variants.get(i).getSku());
                            tmJson.put("option1", variants.get(i).getOption1());
                            tmJson.put("option2", variants.get(i).getOption2());
                            tmJson.put("weight", variants.get(i).getWeight());
                            tmJson.put("weight_unit", variants.get(i).getWeight_unit());
                            newVariants.add(tmJson);
                        }
                    } else {
                        Map<String, Variants> variantsMap = new HashMap<>();
                        variants.forEach(e -> variantsMap.put(e.getSku(), e));

                        Set<String> skuSet = new HashSet<>();
                        for (int i = 0; i < oldVariants.size(); i++) {
                            JSONObject jsonObject = oldVariants.getJSONObject(i);
                            if (variantsMap.containsKey(jsonObject.getString("sku"))) {
                                Variants sku = variantsMap.get(jsonObject.getString("sku"));
                                JSONObject tmJson = new JSONObject();
                                skuSet.add(sku.getSku());
                                tmJson.put("id", jsonObject.getString("id"));
                                tmJson.put("price", sku.getPrice());
                                tmJson.put("grams", sku.getInventory_quantity());
                                tmJson.put("title", sku.getOption1() + " / " + sku.getOption2());
                                tmJson.put("option1", sku.getOption1());
                                tmJson.put("option2", sku.getOption2());
                                tmJson.put("option3", sku.getOption3());
                                tmJson.put("weight", sku.getWeight());
                                tmJson.put("weight_unit", sku.getWeight_unit());
                                newVariants.add(tmJson);
                            }
                        }
                        variantsMap.forEach((k, v) -> {
                            if (!skuSet.contains(k)) {
                                JSONObject tmJson = new JSONObject();
                                tmJson.put("price", v.getPrice());
                                tmJson.put("grams", v.getInventory_quantity());
                                tmJson.put("title", v.getOption1() + " / " + v.getOption2());
                                tmJson.put("product_id", xmsShopifyPidInfo.getShopifyPid());
                                tmJson.put("sku", v.getSku());
                                tmJson.put("option1", v.getOption1());
                                tmJson.put("option2", v.getOption2());
                                tmJson.put("option3", v.getOption3());
                                tmJson.put("weight", v.getWeight());
                                tmJson.put("weight_unit", v.getWeight_unit());
                                newVariants.add(tmJson);
                            }
                        });
                        variantsMap.clear();
                        skuSet.clear();
                    }
                }
                product.put("variants", newVariants);

                // 3.匹配 options，多余的新增
                JSONArray newOptions = new JSONArray();
                if (CollectionUtil.isNotEmpty(saveProduct.getOptions())) {
                    List<Options> optionsList = saveProduct.getOptions();

                    // PUT和POST
                    if (null == oldOptions || oldOptions.size() == 0) {
                        for (int i = 0; i < optionsList.size(); i++) {
                            JSONObject tmJson = new JSONObject();
                            tmJson.put("product_id", xmsShopifyPidInfo.getShopifyPid());
                            tmJson.put("name", optionsList.get(i).getName());
                            tmJson.put("values", optionsList.get(i).getValues());
                            newOptions.add(tmJson);
                        }
                    } else {
                        Map<String, Options> optionsMap = new HashMap<>();
                        optionsList.forEach(e -> optionsMap.put(e.getName(), e));

                        Set<String> nameSet = new HashSet<>();
                        for (int i = 0; i < oldVariants.size(); i++) {
                            JSONObject jsonObject = oldVariants.getJSONObject(i);
                            if (optionsMap.containsKey(jsonObject.getString("name"))) {
                                Options nameOp = optionsMap.get(jsonObject.getString("name"));
                                JSONObject tmJson = new JSONObject();
                                nameSet.add(nameOp.getName());
                                tmJson.put("id", jsonObject.getString("id"));
                                tmJson.put("name", jsonObject.getString("name"));
                                tmJson.put("values", jsonObject.getJSONArray("values"));
                                newOptions.add(tmJson);
                            }
                        }
                        optionsMap.forEach((k, v) -> {
                            if (!nameSet.contains(k)) {
                                JSONObject tmJson = new JSONObject();
                                tmJson.put("product_id", xmsShopifyPidInfo.getShopifyPid());
                                tmJson.put("name", v.getName());
                                tmJson.put("values", v.getValues());
                                newOptions.add(tmJson);
                            }
                        });
                        optionsMap.clear();
                        nameSet.clear();
                    }
                }
                product.put("options", newOptions);

                // 4.设置tags
                if (StrUtil.isNotBlank(saveProduct.getTags())) {
                    product.put("tags", JSONArray.parseArray(saveProduct.getTags()));
                }


                // 5.设置product_type
                product.put("product_type", saveProduct.getProduct_type());

                // 6.组合PID数据 title和body_html
                product.put("id", xmsShopifyPidInfo.getShopifyPid());
                product.put("title", productWraper.getProduct().getTitle());
                product.put("body_html", productWraper.getProduct().getBody_html());
                // 7.完整更新
                saveObject.put("product", product);

                returnJson = this.shopifyRestTemplate.put(String.format(config.SHOPIFY_URI_PUT_PRODUCTS, shopName, xmsShopifyPidInfo.getShopifyPid()), token, saveObject);
                result = gson.fromJson(returnJson, ProductWraper.class);
            } else {
                returnJson = this.shopifyRestTemplate.postForObject(String.format(config.SHOPIFY_URI_PRODUCTS, shopName), token, json);
                result = gson.fromJson(returnJson, ProductWraper.class);
            }
            LOGGER.info("returnJson:[{}]", returnJson);
        } catch (Exception e) {
            LOGGER.error("postForObject", e);
            throw e;
        }
        return result;
    }
}
