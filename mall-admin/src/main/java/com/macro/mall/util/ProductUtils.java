package com.macro.mall.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.util.StringUtil;
import com.macro.mall.dto.PmsProductAttributeParam;
import com.macro.mall.dto.PmsProductParam;
import com.macro.mall.entity.*;
import com.macro.mall.mapper.*;
import com.macro.mall.model.PmsProductAttributeValue;
import com.macro.mall.model.PmsProductCategory;
import com.macro.mall.model.PmsProductCategoryExample;
import com.macro.mall.model.PmsSkuStock;
import com.macro.mall.service.PmsProductAttributeCategoryService;
import com.macro.mall.service.PmsProductAttributeService;
import com.macro.mall.service.PmsProductService;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: 产品清洗工具类
 * @date:2021-04-25
 */
@Service
@Slf4j
public class ProductUtils {

    private final XmsChromeUploadMapper xmsChromeUploadMapper;
    private final XmsSourcingListMapper xmsSourcingListMapper;
    private final AtomicReference<Boolean> referenceFlag;
    @Autowired
    private PmsProductService pmsProductService;
    @Autowired
    private PmsProductAttributeCategoryService productAttributeCategoryService;
    @Autowired
    private PmsProductAttributeService productAttributeService;
    @Autowired
    private PmsProductCategoryMapper productCategoryMapper;
    @Autowired
    private XmsListOfCountriesMapper listOfCountriesMapper;
    @Autowired
    private XmsCustomerProductMapper xmsCustomerProductMapper;
    @Autowired
    private XmsShopifyPidInfoMapper xmsShopifyPidInfoMapper;
    @Autowired
    public ProductUtils(XmsChromeUploadMapper xmsChromeUploadMapper, XmsSourcingListMapper xmsSourcingListMapper) {
        this.xmsChromeUploadMapper = xmsChromeUploadMapper;
        this.xmsSourcingListMapper = xmsSourcingListMapper;
        this.referenceFlag = new AtomicReference<>();
    }


    /**
     * sourcing的商品清洗
     */
    public void cleaningData() {

        try {
            // 如果有执行的，不再重复处理
            if (!this.referenceFlag.compareAndSet(false, true)) {
                return;
            }

            XmsChromeUpload chromeUpload = new XmsChromeUpload();
            // 获取商品数据列表
            QueryWrapper<XmsChromeUpload> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(XmsChromeUpload::getClearFlag, 0L);
            List<XmsChromeUpload> chromeUploadList = this.xmsChromeUploadMapper.selectList(queryWrapper);

            // 产品关联表插入
            if (CollectionUtil.isNotEmpty(chromeUploadList)) {
                chromeUploadList.forEach(this::insertPms);
            }

        } catch (Exception e) {
            e.printStackTrace();
            log.error("cleaningData,error:", e);
        } finally {
            this.referenceFlag.set(false);
        }


    }

    // 插入产品，及关数据
    public void insertPms(XmsChromeUpload chromeUpload) {

        try{
            //查询产品分类
            PmsProductCategoryExample example = new PmsProductCategoryExample();
            example.createCriteria().andNameEqualTo(getSiteTwo(chromeUpload.getSiteType().toString()));
            List<PmsProductCategory> productCategoryList = productCategoryMapper.selectByExample(example);
            //添加商品属性分类
            int maxProductAttributeCategoryId = productAttributeCategoryService.create(getSite(chromeUpload.getSiteType().toString())+"-"+UUID.randomUUID());
            // 添加商品属性信息
            // 添加商品属性值信息
            List<PmsProductAttributeValue> productAttributeValueList = new ArrayList<PmsProductAttributeValue>();

            //清洗规格颜色
            String cType = this.cleaningCType(chromeUpload.getType(),chromeUpload.getSiteType());
            if(StrUtil.isNotEmpty(cType)){

                if(cType.indexOf(";")>0){
                    String[] cTypeArry =  cType.split(";");
                    for(int i=0 ;i<cTypeArry.length;i++){
                        PmsProductAttributeValue pmsProductAttributeValue = new PmsProductAttributeValue();
                        PmsProductAttributeParam productAttributeParam = new PmsProductAttributeParam();
                        productAttributeParam.setProductAttributeCategoryId(Long.valueOf(maxProductAttributeCategoryId));
                        if(StringUtil.isNotEmpty(cTypeArry[i].split(":")[0])){
                            productAttributeParam.setName(cTypeArry[i].split(":")[0]);
                        }
                        if(StringUtil.isNotEmpty(cTypeArry[i].split(":")[1])){
                            productAttributeParam.setInputList(cTypeArry[i].split(":")[1]);
                        }
                        productAttributeParam.setHandAddStatus(1);
                        productAttributeParam.setType(0);
                        int maxId = productAttributeService.create(productAttributeParam);
                        productAttributeValueList = new ArrayList<PmsProductAttributeValue>();
                        pmsProductAttributeValue.setProductAttributeId(Long.valueOf(maxId));
                        pmsProductAttributeValue.setValue(cTypeArry[i].split(":")[1]);
                        productAttributeValueList.add(pmsProductAttributeValue);
                    }
                }else{
                    PmsProductAttributeValue pmsProductAttributeValue = new PmsProductAttributeValue();
                    PmsProductAttributeParam productAttributeParam = new PmsProductAttributeParam();
                    productAttributeParam.setProductAttributeCategoryId(Long.valueOf(maxProductAttributeCategoryId));
                    if(StringUtil.isNotEmpty(cType.split(":")[0])){
                        productAttributeParam.setName(cType.split(":")[0]);
                    }
                    if(StringUtil.isNotEmpty(cType.split(":")[1])){
                        productAttributeParam.setInputList(cType.split(":")[1]);
                    }

                    productAttributeParam.setHandAddStatus(1);
                    productAttributeParam.setType(0);
                    int maxId = productAttributeService.create(productAttributeParam);
                    pmsProductAttributeValue.setProductAttributeId(Long.valueOf(maxId));
                    pmsProductAttributeValue.setValue(cType.split(":")[1]);
                    productAttributeValueList.add(pmsProductAttributeValue);
                }
            }

            //添加产品表
            PmsProductParam productParam = new PmsProductParam();
            XmsSourcingList xmsSourcingList = getXmsSourcingList(chromeUpload);
            //分类id
            productParam.setProductCategoryId(productCategoryList.get(0).getId());
            //属性分类id
            productParam.setProductAttributeCategoryId(Long.valueOf(maxProductAttributeCategoryId));
            //产品名
            productParam.setName(xmsSourcingList.getTitle());
            //主图
            productParam.setPic(xmsSourcingList.getImages());
            //橱窗图
            productParam.setAlbumPics(cleaningWinPic(chromeUpload.getImages(),"",chromeUpload.getSiteType()));
            //描述
            productParam.setDescription(chromeUpload.getProductDetail());
            //详情
            productParam.setDetailHtml(chromeUpload.getProductDescription());
            //交期
            productParam.setLeadTime(chromeUpload.getLeadTime());
            productParam.setProductSn("");
            //价格(新加字段)
            productParam.setPriceXj(xmsSourcingList.getPrice());
            //url(新加字段)
            productParam.setUrl(xmsSourcingList.getUrl());
            //moq
            productParam.setMoq(chromeUpload.getMoq());
            //shippingfee
            productParam.setShippingFee(chromeUpload.getShippingFee());
            //shippingby
            productParam.setShippingBy(chromeUpload.getShippingBy());

            productParam.setProductAttributeValueList(productAttributeValueList);
            int productMaxId = pmsProductService.create(productParam);

            //XmsSourcingList表插入
            this.cleaningSingleData(chromeUpload,productMaxId);
        }catch (Exception e){
            e.printStackTrace();
            log.error("insertPms,error:", e);
        }

    }


    // shopify产品数据插入产品，及关数据
    public boolean insertShopifyToPms(XmsCustomerProduct customerProduct) {

        try{
            //查询产品分类
            PmsProductCategoryExample example = new PmsProductCategoryExample();
            example.createCriteria().andNameEqualTo(getSiteTwo(customerProduct.getSiteType().toString()));
            List<PmsProductCategory> productCategoryList = productCategoryMapper.selectByExample(example);
            //添加商品属性分类
            int maxProductAttributeCategoryId = productAttributeCategoryService.create(getSite(customerProduct.getSiteType().toString())+"-"+UUID.randomUUID());
            // 添加商品属性信息
            // 添加商品属性值信息
            List<PmsProductAttributeValue> productAttributeValueList = new ArrayList<>();

            //清洗规格颜色
            JSONObject jsonObject = JSONObject.parseObject(customerProduct.getShopifyJson());
            JSONArray optionsArr = jsonObject.getJSONArray("options");

            if(null != optionsArr && optionsArr.size() > 0) {
                for (int i = 0; i < optionsArr.size(); i++) {
                    JSONObject opChild = optionsArr.getJSONObject(i);
                    String name = opChild.getString("name");
                    if (StrUtil.isNotBlank(name)) {
                        PmsProductAttributeParam productAttributeParam = new PmsProductAttributeParam();
                        productAttributeParam.setProductAttributeCategoryId((long) maxProductAttributeCategoryId);
                        productAttributeParam.setName(name);
                        productAttributeParam.setHandAddStatus(1);
                        productAttributeParam.setType(0);
                        if (opChild.containsKey("values") && StrUtil.isNotBlank(opChild.getString("values"))) {
                            int maxId = productAttributeService.create(productAttributeParam);
                            JSONArray jsonArray = opChild.getJSONArray("values");
                            if (null != jsonArray && jsonArray.size() > 0) {
                                for (int j = 0; j < jsonArray.size(); j++) {
                                    PmsProductAttributeValue pmsProductAttributeValue = new PmsProductAttributeValue();
                                    pmsProductAttributeValue.setProductAttributeId((long) maxId);
                                    pmsProductAttributeValue.setValue(jsonArray.getString(j));
                                    productAttributeValueList.add(pmsProductAttributeValue);
                                }
                            }
                        }
                    }

                }
            }

            //添加产品表
            PmsProductParam productParam = new PmsProductParam();
            XmsSourcingList xmsSourcingList = getXmsSourcingListByCustom(customerProduct);
            //分类id
            productParam.setProductCategoryId(productCategoryList.get(0).getId());
            //属性分类id
            productParam.setProductAttributeCategoryId((long) maxProductAttributeCategoryId);
            //产品名
            productParam.setName(xmsSourcingList.getTitle());
            //主图
            productParam.setPic(xmsSourcingList.getImages());
            //橱窗图
            productParam.setAlbumPics(xmsSourcingList.getImages());
            //描述
            productParam.setDescription(xmsSourcingList.getSourceLink());
            xmsSourcingList.setSourceLink(null);
            //详情
            productParam.setDetailHtml(xmsSourcingList.getRemark());
            xmsSourcingList.setRemark(null);
            //交期
            //productParam.setLeadTime(chromeUpload.getLeadTime());
            productParam.setProductSn("");
            //价格(新加字段)
            productParam.setPriceXj(xmsSourcingList.getPrice());
            //url(新加字段)
            productParam.setUrl(xmsSourcingList.getUrl());
            //moq
            productParam.setMoq("1");
            //shippingfee
            //productParam.setShippingFee(chromeUpload.getShippingFee());
            //shippingby
            //productParam.setShippingBy(chromeUpload.getShippingBy());

            productParam.setProductAttributeValueList(productAttributeValueList);
            // 插入商品
            int productMaxId = this.pmsProductService.create(productParam);
            xmsSourcingList.setProductId((long) productMaxId);
            xmsSourcingList.setAddProductFlag(1);
            // 插入sourcing
            this.xmsSourcingListMapper.insert(xmsSourcingList);
            // 更新shopify
            customerProduct.setProductId((long)productMaxId);
            customerProduct.setSourcingId(xmsSourcingList.getId());
            this.xmsCustomerProductMapper.updateById(customerProduct);

            QueryWrapper<XmsShopifyPidInfo> pidInfoWrapper = new QueryWrapper<>();
            pidInfoWrapper.lambda().eq(XmsShopifyPidInfo::getShopifyName, customerProduct.getShopifyName())
            .eq(XmsShopifyPidInfo::getShopifyPid, customerProduct.getShopifyProductId());
            XmsShopifyPidInfo xmsShopifyPidInfo = this.xmsShopifyPidInfoMapper.selectOne(pidInfoWrapper);
            if(null == xmsShopifyPidInfo){
                xmsShopifyPidInfo = new XmsShopifyPidInfo();
                xmsShopifyPidInfo.setCreateTime(new Date());
                xmsShopifyPidInfo.setPid(String.valueOf(productMaxId));
                xmsShopifyPidInfo.setPublish(0);
                xmsShopifyPidInfo.setShopifyInfo("shopify product to sourcing");
                xmsShopifyPidInfo.setShopifyName(customerProduct.getShopifyName());
                xmsShopifyPidInfo.setShopifyPid(String.valueOf(customerProduct.getShopifyProductId()));
                this.xmsShopifyPidInfoMapper.insert(xmsShopifyPidInfo);
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
            log.error("insertShopifyToPms,error:", e);
            return false;
        }

    }


    /**
     * 插入sourcing
     * @param customerProduct
     * @return
     */
    private XmsSourcingList getXmsSourcingListByCustom(XmsCustomerProduct customerProduct) {
        Assert.notNull(customerProduct, "customerProduct null");

        JSONObject jsonObject = JSONObject.parseObject(customerProduct.getShopifyJson());

        XmsSourcingList sourcingInfo = new XmsSourcingList();
        sourcingInfo.setMemberId(customerProduct.getMemberId());
        sourcingInfo.setUsername(customerProduct.getUsername());
        // 处理url
        sourcingInfo.setUrl(jsonObject.getString("admin_graphql_api_id"));
        // 处理title
        if (StrUtil.isNotEmpty(customerProduct.getTitle())) {
            String tempTitle = customerProduct.getTitle().replaceAll("[\\u4e00-\\u9fa5]", "");
            if (StrUtil.isNotEmpty(tempTitle)) {
                sourcingInfo.setTitle(tempTitle.trim());
            }
        }
        // 处理img
        JSONArray imagesArr = jsonObject.getJSONArray("images");
        if (null != imagesArr && imagesArr.size() > 0) {

            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < imagesArr.size(); i++) {
                sb.append("," + imagesArr.getJSONObject(i).getString("src"));
            }
            sourcingInfo.setImages(sb.toString().substring(1));
        }
        sourcingInfo.setPrice(customerProduct.getShopifyPrice());
        //阿里价格处理

        // 处理 shippingFee
        sourcingInfo.setCost("0");
        //sourcingInfo.setCountryId();
        //sourcingInfo.setShipping();
        sourcingInfo.setSiteType(11);
        sourcingInfo.setStatus(0);
        sourcingInfo.setCreateTime(new Date());
        sourcingInfo.setUpdateTime(new Date());
        // sourcingInfo.setSkuJson(jsonObject.getString("handle"));
        sourcingInfo.setSourceLink(jsonObject.getString("handle"));
        sourcingInfo.setTitle(customerProduct.getTitle());
        sourcingInfo.setRemark(jsonObject.getString("body_html"));
        return sourcingInfo;
    }


    //api 数据插入产品表
    public int apiDataInsertPms(XmsChromeUpload chromeUpload,List<PmsSkuStock> skuStockList, String note){
        //查询产品分类
        PmsProductCategoryExample example = new PmsProductCategoryExample();
        example.createCriteria().andNameEqualTo(getSiteTwo(chromeUpload.getSiteType().toString()));
        List<PmsProductCategory> productCategoryList = productCategoryMapper.selectByExample(example);
        //添加商品属性分类
        int maxProductAttributeCategoryId = productAttributeCategoryService.create(getSite(chromeUpload.getSiteType().toString())+"-"+UUID.randomUUID());
        // 添加商品属性信息
        List<PmsProductAttributeValue> productAttributeValueList = new ArrayList<PmsProductAttributeValue>();

        //清洗规格颜色
        String cType = chromeUpload.getType();
        if(StrUtil.isNotEmpty(cType)){

            if(cType.indexOf(";")>0){
                String[] cTypeArry =  cType.split(";");
                for(int i=0 ;i<cTypeArry.length;i++){
                    PmsProductAttributeValue pmsProductAttributeValue = new PmsProductAttributeValue();
                    PmsProductAttributeParam productAttributeParam = new PmsProductAttributeParam();
                    productAttributeParam.setProductAttributeCategoryId(Long.valueOf(maxProductAttributeCategoryId));
                    if(StringUtil.isNotEmpty(cTypeArry[i].split(":")[0])){
                        productAttributeParam.setName(cTypeArry[i].split(":")[0]);
                    }
                    if(StringUtil.isNotEmpty(cTypeArry[i].split(":")[1])){
                        productAttributeParam.setInputList(cTypeArry[i].split(":")[1]);
                    }
                    productAttributeParam.setHandAddStatus(1);
                    productAttributeParam.setType(0);
                    int maxId = productAttributeService.create(productAttributeParam);
                    // 添加商品属性值信息
                    productAttributeValueList = new ArrayList<PmsProductAttributeValue>();
                    pmsProductAttributeValue.setProductAttributeId(Long.valueOf(maxId));
                    pmsProductAttributeValue.setValue(cTypeArry[i].split(":")[1]);
                    productAttributeValueList.add(pmsProductAttributeValue);
                }
            }else{
                PmsProductAttributeValue pmsProductAttributeValue = new PmsProductAttributeValue();
                PmsProductAttributeParam productAttributeParam = new PmsProductAttributeParam();
                productAttributeParam.setProductAttributeCategoryId(Long.valueOf(maxProductAttributeCategoryId));
                if(StringUtil.isNotEmpty(cType.split(":")[0])){
                    productAttributeParam.setName(cType.split(":")[0]);
                }
                if(StringUtil.isNotEmpty(cType.split(":")[1])){
                    productAttributeParam.setInputList(cType.split(":")[1]);
                }

                productAttributeParam.setHandAddStatus(1);
                productAttributeParam.setType(0);
                int maxId = productAttributeService.create(productAttributeParam);
                pmsProductAttributeValue.setProductAttributeId((long) maxId);
                pmsProductAttributeValue.setValue(cType.split(":")[1]);
                productAttributeValueList.add(pmsProductAttributeValue);
            }
        }

        //添加产品表
        PmsProductParam productParam = new PmsProductParam();
        productParam.setNote(note);
        //分类id
        productParam.setProductCategoryId(productCategoryList.get(0).getId());
        //属性分类id
        productParam.setProductAttributeCategoryId((long) maxProductAttributeCategoryId);
        //产品名
        productParam.setName(chromeUpload.getTitle());
        //主图
        productParam.setPic(chromeUpload.getPic());
        //橱窗图
        productParam.setAlbumPics(chromeUpload.getImages());
        //描述
        productParam.setDescription(chromeUpload.getProductDetail());
        //详情
        productParam.setDetailHtml(chromeUpload.getProductDescription());
        //交期
        productParam.setLeadTime(chromeUpload.getLeadTime());
        productParam.setProductSn("");
        //价格(新加字段)
        productParam.setPriceXj(chromeUpload.getPrice());
        //url(新加字段)
        productParam.setUrl(chromeUpload.getUrl());
        //moq
        productParam.setMoq(chromeUpload.getMoq());
        //shippingfee
        productParam.setShippingFee(chromeUpload.getShippingFee());
        //shippingby
        productParam.setShippingBy(chromeUpload.getShippingBy());

        productParam.setProductAttributeValueList(productAttributeValueList);
        productParam.setSkuStockList(skuStockList);
        int productMaxId = pmsProductService.create(productParam);

        // 产品id
        return  productMaxId;
    }



    public void cleaningSingleData(XmsChromeUpload chromeUpload,int productMaxId) {
        Assert.notNull(chromeUpload, "chromeUpload null");
        XmsChromeUpload updateChromeUpload = new XmsChromeUpload();
        updateChromeUpload.setId(chromeUpload.getId());

        try {

            updateChromeUpload.setClearFlag(1);
            updateChromeUpload.setUpdateTime(new Date());
            this.xmsChromeUploadMapper.updateById(updateChromeUpload);

            XmsSourcingList sourcingInfo = getXmsSourcingList(chromeUpload);
            sourcingInfo.setProductId(Long.valueOf(productMaxId));
            this.xmsSourcingListMapper.insert(sourcingInfo);

            updateChromeUpload.setClearFlag(2);
            updateChromeUpload.setUpdateTime(new Date());
            this.xmsChromeUploadMapper.updateById(updateChromeUpload);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("cleaningSingleData,error:", e);
            updateChromeUpload.setClearFlag(-1);
            updateChromeUpload.setUpdateTime(new Date());
            this.xmsChromeUploadMapper.updateById(updateChromeUpload);
        }
    }


    public XmsSourcingList getXmsSourcingList(XmsChromeUpload chromeUpload) {
        Assert.notNull(chromeUpload, "chromeUpload null");

        XmsSourcingList sourcingInfo = new XmsSourcingList();
        sourcingInfo.setMemberId(chromeUpload.getMemberId());
        sourcingInfo.setUsername(chromeUpload.getUsername());
        // 处理url
        if (StrUtil.isNotEmpty(chromeUpload.getUrl())) {
            if (chromeUpload.getUrl().contains("?")) {
                sourcingInfo.setUrl(chromeUpload.getUrl().substring(0, chromeUpload.getUrl().indexOf("?")));
            } else {
                sourcingInfo.setUrl(chromeUpload.getUrl());
            }
        }
        // 处理title
        if (StrUtil.isNotEmpty(chromeUpload.getTitle())) {
            String tempTitle = chromeUpload.getTitle().replaceAll("[\\u4e00-\\u9fa5]", "");
            if (StrUtil.isNotEmpty(tempTitle)) {
                sourcingInfo.setTitle(tempTitle.trim());
            }
        }
        // 处理img
        if (StrUtil.isNotEmpty(chromeUpload.getImages())) {
            sourcingInfo.setImages(cleaningPic(chromeUpload.getImages(),chromeUpload.getSiteType()));
        }
        // 处理价格
//        // US $9.86 - 13.50 US $12.33 - 16.88-20%
//        if (StrUtil.isNotEmpty(chromeUpload.getPrice())) {
//            // .replace("$", "@")
//            String tempPrice = chromeUpload.getPrice().trim();
//            if (tempPrice.indexOf("US") == 0) {
//                String[] priceArr = tempPrice.substring(2).split("US");
//                sourcingInfo.setPrice(priceArr[0].trim());
//            } else {
//                sourcingInfo.setPrice(chromeUpload.getPrice().trim());
//            }
//        }
        sourcingInfo.setPrice(this.cleaningPrice(chromeUpload.getPrice(),chromeUpload.getSiteType()));
        //阿里价格处理
        sourcingInfo.setPricePs(cleaningAliPrice(this.cleaningPrice(chromeUpload.getPrice(),chromeUpload.getSiteType()),chromeUpload.getSiteType()));

        // 处理 shippingFee
        sourcingInfo.setCost(this.cleaningShippingFee(chromeUpload.getShippingFee(),chromeUpload.getSiteType()));
//        // Shipping: US $5.14
//        if (StrUtil.isNotEmpty(chromeUpload.getShippingFee())) {
//            if (chromeUpload.getShippingFee().contains("Shipping: US $")) {
//                sourcingInfo.setCost(chromeUpload.getShippingFee().replace("Shipping: US $", "").trim());
//            } else {
//                sourcingInfo.setCost(chromeUpload.getShippingFee().trim());
//            }
//            sourcingInfo.setCost(sourcingInfo.getCost().replace("AWG", "").trim());
//
//        }

//        if (StrUtil.isNotEmpty(chromeUpload.getShippingBy())) {
//            sourcingInfo.setShipping(chromeUpload.getShippingBy());
//        }
        // 处理 shippingFee
        String shipingbyC = this.cleaningShippingBy(chromeUpload.getShippingBy(),chromeUpload.getSiteType());
        sourcingInfo.setCountryId(this.getCountId(shipingbyC));
        sourcingInfo.setShipping(StringUtil.isNotEmpty(shipingbyC) && shipingbyC.indexOf(";")>0 ?shipingbyC.split(";")[1]:shipingbyC);
        sourcingInfo.setSiteType(chromeUpload.getSiteType());
        sourcingInfo.setStatus(0);
        sourcingInfo.setCreateTime(new Date());
        sourcingInfo.setUpdateTime(new Date());
        return sourcingInfo;
    }

    // 取得国家id
    public int getCountId(String shipingbyC) {
        int countId = 0;

        if(StringUtil.isEmpty(shipingbyC)){
            countId = 36;
        }else{
            List<XmsListOfCountries> countriesList = new ArrayList<>();
            QueryWrapper<XmsListOfCountries> queryWrapper = new QueryWrapper<>();
            queryWrapper.like("english_name_of_country",shipingbyC.split(";")[0]);
            countriesList = this.listOfCountriesMapper.selectList(queryWrapper);
            if (CollectionUtil.isNotEmpty(countriesList)) {
                countId = countriesList.get(0).getId();
            }else{
                countId =36;
            }
        }

        return countId;
    }

    // 规格清洗
    public String cleaningCType(String cType,int site) {

        String result = new String();
        if(site == 1){
            result = cleaningAlibabaType(cType);
        }else if(site == 2){
            result = cleaningAliExpressType(cType);
        }else if(site == 3){
            result = cleaningAliExpressType(cType);
        }else if(site == 4){
            result = cleaningAmazonType(cType);
        }else if(site == 5){
            result = cleaningWayfairType(cType);
        }else if(site == 6){
            result = cleaningEbayType(cType);
        }else if(site == 7){
            result = cleaningWalmartType(cType);
        }else if(site == 8){

        }

        return result;
    }


    // 主图处理
    public String cleaningPic(String pic,int site) {

        if(StrUtil.isEmpty(pic)){
            return "";
        }
        // Wayfair
        if(site == 5){
            pic = pic.split(";")[0];
        }

        return pic;
    }

    // 橱窗图处理
    public String cleaningWinPic(String pic,String winPic,int site) {

        if(StrUtil.isEmpty(pic)){
            return "";
        }
        // Wayfair
        if(site == 5){
            pic = pic.replace(";",",");
            pic = pic.substring(pic.indexOf(",")+1);
        }else{
            pic = winPic;
        }

        return pic;
    }

    // price clean
    // 价格
    public static String cleaningPrice(String price,int site) {

        if(StrUtil.isEmpty(price)){
            return "";
        }
        if(!price.contains("<") && !price.contains("[")){
            return price;
        }
        StringBuilder result = new StringBuilder();
        Document doc = Jsoup.parse(price);
        //alibaba
        if (site == 1) {
            List<String> prcieList = new ArrayList();
            JSONArray jsonArr = JSONArray.parseArray(price);
            if (jsonArr != null && jsonArr.size() > 0) {
                for (int i = 0; i < jsonArr.size(); i++) {
                    JSONObject json = jsonArr.getJSONObject(i);
                    String spDate = json.getString("ma_spec_price");
                    prcieList.add(spDate);
                }
            }
            if(prcieList.size()==1){
                result.append(prcieList.get(0));
            }else{
                result.append(prcieList.get(prcieList.size()-1));
                result.append("-");
                result.append(prcieList.get(0));
            }

            //ebay
        }else if(site==6){
            // 价格
            Elements element1 = doc.select("span.notranslate");

            if(element1.size()>0){
                result.append(element1.get(0).text());
            }
            //aliexpress esalipress
        }else if(site == 2 || site == 3){
            Elements element1 = doc.select("span.product-price-value");

            if(element1.size()==1){
                result.append(element1.get(0).text());
            }
            if(element1.size()==2){
//                result.append(element1.get(0).text());
//                result.append(";");
                result.append(element1.get(1).text());
            }

            //walmart
        }else if(site == 7){
            Elements element1 = doc.select("span.price-characteristic");

            result.append(element1.attr("content"));
        }

        //正常价格
        if(StrUtil.isEmpty(result.toString())){
            result.append(price);
        }

        return result.toString();
    }

    // price ShippingFee
    // 运费运输方式
    public static String cleaningShippingFee(String shippingFee,int site) {

        if(StrUtil.isEmpty(shippingFee)){
            return "";
        }
        StringBuilder result = new StringBuilder();
        Document doc = Jsoup.parse(shippingFee);
        //ebay
        if(site==1){
            Elements elementSpan = doc.select("span.from-num");
            result.append(elementSpan.text());
        }else if(site == 2 || site == 3){
            if(StringUtil.isNotEmpty(shippingFee)){
                if(shippingFee.contains("Shipping:")){
                    result.append(shippingFee.replace("Shipping:","").replace("&nbsp;","").trim());
                }
            }

        }

        return result.toString();
    }

    // ali价格处理
    public static String cleaningAliPrice(String priceUnit,int site) {

        if(StrUtil.isEmpty(priceUnit)){
            return "";
        }
        StringBuilder result = new StringBuilder();

         if(site == 2 || site == 3){
            if(priceUnit.contains("-")){
                result.append(StrUtils.matchStr(priceUnit.split("-")[0], "(\\d+(\\.\\d+){0,1})"));
                result.append("-");
                result.append(StrUtils.matchStr(priceUnit.split("-")[1], "(\\d+(\\.\\d+){0,1})"));
            }else{
                result.append(StrUtils.matchStr(priceUnit, "(\\d+(\\.\\d+){0,1})"));
            }
        }

        return result.toString();
    }



    // shippingBy
    // 运费方式
    public static String cleaningShippingBy(String shippingBy,int site) {

        if(StrUtil.isEmpty(shippingBy)){
            return "";
        }
        StringBuilder result = new StringBuilder();
        //alibaba
        if(site==1){

            if(StringUtil.isNotEmpty(shippingBy)){
                if(shippingBy.contains("to")){
                    if(shippingBy.contains("by")){
                        String to = shippingBy.substring(shippingBy.indexOf("to")+2,shippingBy.indexOf("by"));
                        if(StringUtil.isNotEmpty(to)){
                            result.append(to.trim());
                        }
                        String by = shippingBy.substring(shippingBy.indexOf("by")+2);
                        if(StringUtil.isNotEmpty(by)){
                            result.append(";"+by.trim());
                        }
                    }
                    else{
                        String to = shippingBy.substring(shippingBy.indexOf("to")+2);
                        if(StringUtil.isNotEmpty(to)){
                            result.append(to.trim());
                        }
                    }
                }
            }
        }else if(site == 2 || site == 3){
            if(StringUtil.isNotEmpty(shippingBy)){
                if(shippingBy.contains("to")){
                    if(shippingBy.contains("via")){
                        String to = shippingBy.substring(shippingBy.indexOf("to")+2,shippingBy.indexOf("via"));
                        if(StringUtil.isNotEmpty(to)){
                            result.append(to.trim());
                        }
                        String by = shippingBy.substring(shippingBy.indexOf("via")+3);
                        if(StringUtil.isNotEmpty(by)){
                            result.append(";"+by.trim());
                        }
                    }
                    else{
                        String to = shippingBy.substring(shippingBy.indexOf("to")+2);
                        if(StringUtil.isNotEmpty(to)){
                            result.append(to.trim());
                        }
                    }
                }
            }

        }

        return result.toString();
    }

    // AliExpress
    public String cleaningAliExpressType(String type) {

        if(StrUtil.isEmpty(type)){
            return "";
        }

        StringBuilder typeResult = new StringBuilder();

        Document doc = Jsoup.parse(type);
        // 顏色：值
        Elements elementDL = doc.select("div.sku-property");

        for (Element element : elementDL) {

            String skuType = "";

            Elements sku_title = element.select("div.sku-title");
            if (sku_title != null && sku_title.size() > 0) {
                skuType = sku_title.get(0).text();
                if (StringUtil.isNotEmpty(skuType)) {
                    skuType = skuType.substring(0, skuType.indexOf(":"));
                    if(skuType.contains("Fit") || skuType.contains("Condition")){
                        continue;
                    }
                }
                if (!"".equals(typeResult) && typeResult.length() > 0) {
                    typeResult.append(";");
                }
                typeResult.append(skuType + ":");
            }


            Elements elementImg = element.select("div.sku-property-image").select("img");
            if(elementImg == null || elementImg.size() == 0){
                elementImg = element.select("span.sku-property-color-inner");
            }


            for (Element element1 : elementImg) {
                if (typeResult.lastIndexOf(":") != (typeResult.length() - 1)) {
                    typeResult.append(",");
                }
                typeResult.append(element1.attr("title"));
            }

            Elements elementSpan = element.select("div.sku-property-text");
            for (Element element2 : elementSpan) {

                String spanText = element2.select("span").first().text();
                if (StringUtil.isNotEmpty(spanText)) {
                    if (typeResult.lastIndexOf(":") != (typeResult.length() - 1)) {
                        typeResult.append(",");
                    }
                    typeResult.append(spanText);
                }


            }

        }


        return typeResult.toString();
    }

    // 规格Alibaba
    public static String cleaningAlibabaType(String type) {

        if(StrUtil.isEmpty(type)){
            return "";
        }

        StringBuilder typeResult = new StringBuilder();

        Document doc = Jsoup.parse(type);
        // 顏色：值
        Elements elementDL = doc.select("dl");

        for (Element  element: elementDL) {

            String skuType = "";
            String temp = element.attr("class");
            if(element.attr("class").contains("sku-attr-dl")){
                Elements elementDt = element.select("dt");
                if("name".equals(elementDt.attr("class"))){
                    skuType= elementDt.attr("title");
                    if(!"".equals(typeResult) && typeResult.length() > 0){
                        typeResult.append(";");
                    }
                    typeResult.append(skuType+":");
                }
                Elements elementDd = element.select("dd");
                Elements elementImg = elementDd.select("img");
                for (Element element1 : elementImg) {
                    if(typeResult.lastIndexOf(":") != (typeResult.length()-1)){
                        typeResult.append(",");
                    }
                    typeResult.append(element1.attr("title"));
                }

                Elements elementSpan = elementDd.select("span.sku-attr-val-frame");
                for(Element element2 : elementSpan){
                    String title = element2.attr("title");
                    if(StringUtil.isNotEmpty(title)){
                        if(typeResult.lastIndexOf(":") != (typeResult.length()-1)){
                            typeResult.append(",");
                        }
                        typeResult.append(title);
                    }
                    else{
                        String spanText = element2.select("span").first().text();
                        if(StringUtil.isNotEmpty(spanText)){
                            if(typeResult.lastIndexOf(":") != (typeResult.length()-1)){
                                typeResult.append(",");
                            }
                            typeResult.append(spanText);
                        }
                        else{
                            String spanTextTitle = element2.select("span.color").attr("title");
                            if(StringUtil.isNotEmpty(spanTextTitle)){
                                if(typeResult.lastIndexOf(":") != (typeResult.length()-1)){
                                    typeResult.append(",");
                                }
                                typeResult.append(spanTextTitle);
                            }
                        }
                    }

                }


            }
        }


        return typeResult.toString();
    }


    // 规格Wayfair
    public static String cleaningWayfairType(String type) {

        if(StrUtil.isEmpty(type)){
            return "";
        }
        StringBuilder typeResult = new StringBuilder();

        Document doc = Jsoup.parse(type);
        // 顏色：值
        Elements elementDL = doc.select("div.pl-Box--pb-4");

        for (Element element : elementDL) {

            String skuType = "";

            Elements elementP = element.select("p.pl-Text--bold");

            if(elementP.size()!=0){
                skuType = elementP.get(0).text();

                if (StrUtil.isNotEmpty(skuType)) {
                    if (!"".equals(typeResult) && typeResult.length() > 0) {
                        typeResult.append(";");
                    }
                    typeResult.append(skuType);
                }
            }



            Elements elementText = element.select("div.VisualOptionCard-nameText").select("span.OptionName");

            for (Element element1 : elementText) {
                if (typeResult.lastIndexOf(":") != (typeResult.length() - 1)) {
                    typeResult.append(",");
                }
                typeResult.append(element1.text());
            }

        }


        return typeResult.toString();
    }


    // 规格Walmart
    public static String cleaningWalmartType(String type) {

        if(StrUtil.isEmpty(type)){
            return "";
        }

        StringBuilder typeResult = new StringBuilder();

        Document doc = Jsoup.parse(type);
        // 顏色：值
        Elements elementDL = doc.select("div.variants__contain");

        for (Element element : elementDL) {

            String skuType = "";

            skuType = element.attr("label");

            if (StringUtil.isNotEmpty(skuType)) {
                if (!"".equals(typeResult) && typeResult.length() > 0) {
                    typeResult.append(";");
                }
                typeResult.append(skuType + ":");
            }




            Elements elementText = element.select("div.text-center");
            if(elementText == null || elementText.size() == 0 ){
                elementText = element.select("div.variants__list").get(0).select("div.var__overlay");
            }

            if(elementText != null && elementText.size() > 0){
                for (Element element1 : elementText) {
                    if (typeResult.lastIndexOf(":") != (typeResult.length() - 1)) {
                        typeResult.append(",");
                    }
                    if(StringUtil.isNotEmpty(element1.text())){
                        typeResult.append(element1.text());
                    }
                    else{
                        typeResult.append(element1.attr("data-label"));
                    }
                }
            }
            else{
                typeResult = new StringBuilder();
                for (Element element2 : elementDL) {

                    skuType = "";

                    skuType = element2.select("span.varslabel__label").get(0).text();

                    if (StringUtil.isNotEmpty(skuType)) {
                        if (!"".equals(typeResult) && typeResult.length() > 0) {
                            typeResult.append(";");
                        }
                        typeResult.append(skuType);
                    }



                }


                Elements elementRadio = elementDL.select("input.var__radio");
                if(elementRadio != null && elementRadio.size() > 0){
                    for (Element element1 : elementRadio) {
                        if (typeResult.lastIndexOf(":") != (typeResult.length() - 1)) {
                            typeResult.append(",");
                        }
                        typeResult.append(element1.attr("data-label"));
                    }
                }
                else{
                    typeResult = new StringBuilder();
                }

            }

        }






        return typeResult.toString().replaceAll(": ",":");
    }

    // 规格Amazon
    public static String cleaningAmazonType(String type) {

        if(StrUtil.isEmpty(type)){
            return "";
        }
        StringBuilder typeResult = new StringBuilder();

        Document doc = Jsoup.parse(type);
        // 顏色：值
        Elements elementSize = doc.getElementsByAttributeValue("id","variation_size_name");

        String sizeStr = elementSize.get(0).select("label.a-form-label").text();

        typeResult.append(sizeStr);

        Elements elementSelect = elementSize.get(0).select("select.a-native-dropdown");

        Elements elementOptions = elementSelect.get(0).select("option");

        for(Element element : elementOptions){
            String spanText = element.text();
            if (StrUtil.isNotEmpty(spanText) && !"选择".equals(spanText)) {
                if (typeResult.lastIndexOf(":") != (typeResult.length() - 1)) {
                    typeResult.append(",");
                }
                typeResult.append(spanText);
            }
        }

        Elements elementColor = doc.getElementsByAttributeValue("id","variation_color_name");

        String colorStr = elementColor.get(0).select("label.a-form-label").text();

        typeResult.append(";"+colorStr);

        Elements elementUl = elementColor.get(0).select("ul.a-unordered-list");

        Elements elementLi = elementUl.get(0).select("li");

        for(Element element : elementLi){
            String spanText = element.attr("title");
            if (StrUtil.isNotEmpty(spanText)) {
                if (typeResult.lastIndexOf(":") != (typeResult.length() - 1)) {
                    typeResult.append(",");
                }
                typeResult.append(spanText.replace("选择","").trim());
            }
        }

        return typeResult.toString();
    }

    // 规格Ebay
    public static String cleaningEbayType(String type) {

        if(StrUtil.isEmpty(type)){
            return "";
        }
        StringBuilder typeResult = new StringBuilder();

        Document doc = Jsoup.parse(type);
        // 顏色：值
        Elements elementFlL = doc.select("div.u-flL");

        typeResult.append(elementFlL.get(0).text());

        typeResult.append(elementFlL.get(1).text());


        Elements elementDspn = doc.select("div.vi-bbox-dspn");

        if(elementDspn != null && elementDspn.size() > 0){
            String dspnStr = elementDspn.get(0).select("label").get(0).text();

            typeResult.append(";"+dspnStr);


            Elements elementSelect = doc.select("select.msku-sel");

            Elements elementOption = elementSelect.get(0).select("option");

            for(Element element : elementOption){
                String spanText = element.text();
                if (StringUtil.isNotEmpty(spanText) && !spanText.contains("请选择")) {
                    if (typeResult.lastIndexOf(":") != (typeResult.length() - 1)) {
                        typeResult.append(",");
                    }
                    typeResult.append(spanText);
                }
            }

        }


        return typeResult.toString().replace("：",":");
    }

    //根据id对应不同网站
    public static String getSite(String site) {
        switch (site) {
            case "1":
                return "ALIBABA";
            case "2":
                return "ALIEXPRESS";
            case "3":
                return "ESALIEXPRESS";
            case "4":
                return "AMAZON";
            case "5":
                return "WAYFAIR";
            case "6":
                return "EBAY";
            case "7":
                return "WALMART";
            case "8":
                return "ALI1688";
            default:
                return "ALI1688";
        }
    }
    //根据id对应不同网站
    public static String getSiteTwo(String site) {
        switch (site) {
            case "1":
                return "ALIBABA_CHILD";
            case "2":
                return "ALIEXPRESS_CHILD";
            case "3":
                return "ESALIEXPRESS_CHILD";
            case "4":
                return "AMAZON_CHILD";
            case "5":
                return "WAYFAIR_CHILD";
            case "6":
                return "EBAY_CHILD";
            case "7":
                return "WALMART_CHILD";
            case "8":
                return "ALI1688_CHILD";
            case "11":
                return "SHOPIFY_CHILD";
            default:
                return "ALI1688_CHILD";
        }
    }


}
