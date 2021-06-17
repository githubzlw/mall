package com.macro.mall.shopify.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.entity.XmsPmsProductEdit;
import com.macro.mall.entity.XmsPmsSkuStockEdit;
import com.macro.mall.entity.XmsShopifyAuth;
import com.macro.mall.entity.XmsShopifyPidInfo;
import com.macro.mall.mapper.XmsPmsProductEditMapper;
import com.macro.mall.mapper.XmsPmsSkuStockEditMapper;
import com.macro.mall.mapper.XmsShopifyAuthMapper;
import com.macro.mall.mapper.XmsShopifyPidInfoMapper;
import com.macro.mall.shopify.config.ShopifyConfig;
import com.macro.mall.shopify.config.ShopifyRestTemplate;
import com.macro.mall.shopify.exception.ShopifyException;
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
    private XmsPmsProductEditMapper xmsPmsProductEditMapper;
    @Autowired
    private XmsPmsSkuStockEditMapper xmsPmsSkuStockEditMapper;

    public XmsShopifyProductServiceImpl(XmsShopifyPidInfoMapper shopifyPidInfoMapper, ShopifyConfig config, ShopifyRestTemplate shopifyRestTemplate) {
        this.shopifyPidInfoMapper = shopifyPidInfoMapper;
        this.config = config;
        this.shopifyRestTemplate = shopifyRestTemplate;
    }

    @Override
    public CommonResult pushProduct(String pid, String shopName, boolean published) {
        try {

            LOGGER.info("begin push product[{}] to shopify[{}]", pid, shopName);
            ProductRequestWrap wrap = new ProductRequestWrap();
            wrap.setPid(pid);
            wrap.setPublished(published);
            wrap.setShopname(shopName);

            ProductWraper wraper = pushProductWFW(wrap);
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

    public ProductWraper pushProductWFW(ProductRequestWrap wrap) throws ShopifyException {
        //验证是否已经铺货过
        ProductWraper productWraper = checkPush(wrap.getShopname(), wrap.getPid());
        if (productWraper != null) {
            return productWraper;
        }
        // 产品信息数据查询
        XmsPmsProductEdit pmsProduct = productMapper.selectById(Long.valueOf(wrap.getPid()));
        ShopifyData goods = composeShopifyData(pmsProduct, wrap.getSite());

        QueryWrapper<XmsPmsSkuStockEdit> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(XmsPmsSkuStockEdit::getProductId, Long.valueOf(wrap.getPid()));
        List<XmsPmsSkuStockEdit> skuList= skuStockMapper.selectList(queryWrapper);
        goods.setSkuList(skuList);

        goods.setSkus(wrap.getSkus());
        goods.setPublished(wrap.isPublished());
        goods.setBodyHtml(wrap.isBodyHtml());
        return onlineProduct(wrap.getShopname(),goods);
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
        data.setPid(String.valueOf(goods.getId()));
        data.setInfo(detail(goods));
        data.setInfoHtml(goods.getDetailHtml());
        data.setName(goods.getName());
        data.setPrice(goods.getPriceXj());
        data.setImage(image(goods));
        data.setPerWeight(String.valueOf(goods.getWeight()));
        data.setCategory(String.valueOf(goods.getProductCategoryId()));
        data.setTags(goods.getShopifyTags());
        data.setProductType(goods.getShopifyType());
        return data;
    }

    public Product toProduct(ShopifyData goods) throws ShopifyException {
        Product product = new Product();
        product.setTitle(goods.getName());
        product.setPublished(goods.isPublished());
        if(goods.isBodyHtml()){
            String info_ori = goods.getInfoHtml();
            StringBuilder details = details(goods.getInfo());
            details.append(info_ori);
            product.setBody_html(details.toString());
        }

        product.setVendor(goods.getVendor());
        product.setProduct_type(goods.getCategory());
        product.setTags(goods.getTags());
        product.setProduct_type(goods.getProductType());
        OptionWrap wrap;
        try {
            wrap = optionVariant(goods.getSkuList());
        }catch (Exception e){
            LOGGER.error("Option and Variant error",e.getMessage());
            throw new ShopifyException("1005",e.getMessage());
        }

        if(wrap !=null && wrap.getOptions() == null){
            throw  new ShopifyException("Product options has something wrong");
        }else if(wrap !=null){
            product.setOptions(wrap.getOptions());
        }

        List<Variants> lstVariants = wrap.getVariants();
        if(lstVariants.isEmpty()){
            Variants variant = variant(goods.getPrice(),goods.getPerWeight());
            lstVariants.add(variant);
        }
        product.setVariants(lstVariants);


        List<String> lstImg = goods.getImage();
        lstImg.addAll(wrap.getLstImages());
        List<Images> lstImages = images(lstImg);
        if(lstImages.isEmpty()){
            throw  new ShopifyException("Product has no images");
        }
        product.setImages(lstImages);
        return product;
    }

    /**图片
     * @param pImage
     * @return
     */
    private List<Images>  images( List<String> pImage) throws ShopifyException{
        if(pImage == null || pImage.isEmpty()){
            throw  new ShopifyException("The image is empty");
        }
        List<Images> lstImages = Lists.newArrayList();
        Set<String> setImage = Sets.newHashSet(pImage);
        Images images;
        Iterator<String> iterator = setImage.iterator();
        while (iterator.hasNext()) {
            String imgSrc = iterator.next().replace(".60x60", ".400x400");
            images = new Images();
            images.setSrc(imgSrc);
            lstImages.add(images);
        }
        return lstImages;
    }

    private Variants variant(String goodsPrice,String weight) throws ShopifyException{
        if(StringUtils.isBlank(goodsPrice)){
            throw  new ShopifyException("The price is not valid");
        }
        goodsPrice  = goodsPrice.split("-")[0];
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
        prices.setCompare_at_price(null);
        Price price = new Price();
        price.setAmount(goodsPrice);
        price.setCurrency_code("USD");
        prices.setPrice(price);
        presentment_prices.add(prices);
        variants.setPresentment_prices(presentment_prices);
        return variants;
    }

    /**明细
     * @param detail
     * @return
     */
    private StringBuilder details(List<String> detail){
        StringBuilder sb = new StringBuilder();
        if (detail != null && !detail.isEmpty()) {
            sb.append("<div>");
            detail.stream().forEach(d->{
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
        String imgMain = goods.getPic();
        if (StringUtils.isNotBlank(imgMain)) {
            imgList.add(imgMain.replaceAll("http://", "https://"));
        }
        String img = goods.getAlbumPics();
        if (StringUtils.isNotBlank(img)) {
            img = img.replace("[", "")
                    .replace("]", "")
                    .replaceAll("http://", "https://").trim();
            String[] imgs = img.split(",\\s*");
            for (int i = 0; i < imgs.length; i++) {
                if (imgs[i].indexOf("http://") > -1 || imgs[i].indexOf("https://") > -1) {
                    imgList.add(imgs[i].replaceAll("http://", "https://"));
                } else {
                    imgList.add(imgs[i]);
                }
            }
        }
        return imgList;
    }

    private ProductWraper checkPush(String shopName,String pid){
        XmsShopifyPidInfo shopifyBean = checkProduct(shopName, pid);
        if(shopifyBean != null && StringUtils.isNotBlank(shopifyBean.getShopifyPid())){
            ProductWraper wraper = new ProductWraper();
            if(StringUtils.isNotBlank(shopifyBean.getShopifyInfo())){
                wraper = JSON.parseObject(shopifyBean.getShopifyInfo(),ProductWraper.class);
            }
            if(shopifyBean.getPublish() > 0){
                wraper.setPush(true);
                return wraper;
            }
        }
        return null;
    }

    public XmsShopifyPidInfo checkProduct(String shopname, String itemId) throws ShopifyException {
        XmsShopifyPidInfo  shopifyBean = new XmsShopifyPidInfo();
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

    public ProductWraper onlineProduct(String shopname, ShopifyData goods) throws ShopifyException {
        Product product = toProduct(goods);
        XmsShopifyPidInfo  shopifyBean = new XmsShopifyPidInfo();
        shopifyBean.setShopifyName(shopname);
        shopifyBean.setPid(goods.getPid());
        XmsShopifyPidInfo shopifyId = selectShopifyId(shopifyBean);
        if (shopifyId != null) {
            product.setId(Long.parseLong(shopifyId.getShopifyPid()));
        }
        ProductWraper productWraper = new ProductWraper();
        productWraper.setProduct(product);
        productWraper = addProduct(shopname, productWraper);

        if (productWraper != null && productWraper.getProduct() != null) {
            // 铺货完成后，绑定店铺数据信息，方便下单后对应ID获取我们产 品ID
            shopifyBean.setShopifyPid(String.valueOf(productWraper.getProduct().getId()));
            shopifyBean.setShopifyInfo(JSONObject.toJSONString(productWraper));
            shopifyBean.setPublish(product.isPublished() ? 1 : 0);
            insertShopifyIdWithPid(shopifyBean);

        }
        return productWraper;
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
            updateWrapper.eq("shopifyName",shopifyBean.getShopifyName()).eq("pid",shopifyBean.getPid());
            XmsShopifyPidInfo bean = new XmsShopifyPidInfo();
            bean.setShopifyPid(shopifyBean.getShopifyPid());
            bean.setPublish(shopifyBean.getPublish());
            bean.setShopifyInfo(shopifyBean.getShopifyInfo());
            result = shopifyPidInfoMapper.update(bean,updateWrapper);
        } else {
            result = shopifyPidInfoMapper.insert(shopifyBean);
        }
        return result;
    }


    /**解析sku,转成shopify网站的Options以及Variants
     * @param skuList
     */
    public OptionWrap optionVariant(List<XmsPmsSkuStockEdit> skuList) throws Exception {
        if(skuList == null || skuList.isEmpty()){
            return OptionWrap.builder().lstImages(Lists.newArrayList())
                    .options(Lists.newArrayList()).variants(Lists.newArrayList()).build();
        }
        List<String> image = Lists.newArrayList();
        List<Variants> lstVariants = Lists.newArrayList();
        List<Options> lstOptions = Lists.newArrayList();
        Map<String,Options> optionMap = Maps.newHashMap();
        Variants variants;
        for(int i=0 ;i<skuList.size();i++){
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
            variants.setCompare_at_price(String.valueOf(skuList.get(i).getComparedAtPrice()));
            image.add(skuList.get(i).getPic());
            // 规格数据
            Gson gson = new Gson();
            List<SkuVal> list = gson.fromJson(skuList.get(i).getSpData(),
                    new TypeToken<List<SkuVal>>(){}.getType());

            for(int j=0;j<list.size();j++){
                options(optionMap,list.get(j));
                if(j == 0){
                    variants.setOption1(list.get(j).getValue());
                }else if(j == 1){
                    variants.setOption2(list.get(j).getValue());
                }else if(j == 2){
                    variants.setOption3(list.get(j).getValue());
                }else{

                }
            }
            lstVariants.add(variants);
        }

        optionMap.entrySet().stream().forEach(o->lstOptions.add(o.getValue()));
        return OptionWrap.builder().lstImages(image)
                .options(lstOptions).variants(lstVariants).build();
    }

    /**options转换
     * @param optionMap
     * @param typeBean
     */
    private void options(Map<String,Options>optionMap,SkuVal typeBean){
        Options options = optionMap.get(typeBean.getKey());
        options = options == null ? new Options() : options;
        options.setName(typeBean.getKey());
        List<String> values = options.getValues();
        values = values == null ? Lists.newArrayList() : values;
        if(!values.contains(typeBean.getValue())){
            values.add(typeBean.getValue());
            values = values.stream().sorted().collect(Collectors.toList());
        }
        options.setValues(values);
        optionMap.put(typeBean.getKey(),options);
    }


    /**
     * 铺货到shopify
     *
     * @param shopName
     * @param productWraper
     * @return
     */
    public ProductWraper addProduct(String shopName, ProductWraper productWraper) {

        Assert.notNull(productWraper, "product object is null");
        LOGGER.info("shopName:[{}] productWraper:[{}]", shopName, productWraper);
        ProductWraper result = new ProductWraper();
        try {
            Gson gson = new Gson();
            PushPrduct wrap = new PushPrduct();
            wrap.setProduct(productWraper.getProduct());
            String json =  gson.toJson(wrap);

            QueryWrapper<XmsShopifyAuth> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(XmsShopifyAuth::getShopName, shopName);
            XmsShopifyAuth shopifyAuth = xmsShopifyAuthMapper.selectOne(queryWrapper);
            String token = shopifyAuth.getAccessToken();

            LOGGER.info("add product to myself shop:[{}]",shopName);
            String returnJson = shopifyRestTemplate.postForObject(String.format(config.SHOPIFY_URI_PRODUCTS, shopName), token, json);
            LOGGER.info("returnJson:[{}]", returnJson);
            result = gson.fromJson(returnJson, ProductWraper.class);

        }catch (Exception e){
            LOGGER.error("postForObject",e);
            throw e;
        }
        return result;
    }
}
