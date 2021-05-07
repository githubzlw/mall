package com.macro.mall.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.util.StringUtil;
import com.macro.mall.dto.PmsProductAttributeParam;
import com.macro.mall.dto.PmsProductParam;
import com.macro.mall.entity.XmsChromeUpload;
import com.macro.mall.entity.XmsSourcingList;
import com.macro.mall.mapper.PmsProductAttributeCategoryMapper;
import com.macro.mall.mapper.PmsProductCategoryMapper;
import com.macro.mall.mapper.XmsChromeUploadMapper;
import com.macro.mall.mapper.XmsSourcingListMapper;
import com.macro.mall.model.PmsProductAttributeValue;
import com.macro.mall.model.PmsProductCategory;
import com.macro.mall.model.PmsProductCategoryExample;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
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
    private PmsProductAttributeCategoryMapper productAttributeCategoryMapper;
    @Autowired
    private PmsProductCategoryMapper productCategoryMapper;
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
            queryWrapper.lambda().eq(XmsChromeUpload::getClearFlag, -1L);
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

            productParam.setProductAttributeValueList(productAttributeValueList);
            int productMaxId = pmsProductService.create(productParam);

            //XmsSourcingList表插入
            this.cleaningSingleData(chromeUpload,productMaxId);
        }catch (Exception e){
            e.printStackTrace();
            log.error("insertPms,error:", e);
        }

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
            sourcingInfo.setImages(chromeUpload.getImages());
        }
        // 处理价格
        // US $9.86 - 13.50 US $12.33 - 16.88-20%
        if (StrUtil.isNotEmpty(chromeUpload.getPrice())) {
            // .replace("$", "@")
            String tempPrice = chromeUpload.getPrice().trim();
            if (tempPrice.indexOf("US") == 0) {
                String[] priceArr = tempPrice.substring(2).split("US");
                sourcingInfo.setPrice(priceArr[0].trim());
            } else {
                sourcingInfo.setPrice(chromeUpload.getPrice().trim());
            }
        }
        // 处理 shippingFee
        // Shipping: US $5.14
        if (StrUtil.isNotEmpty(chromeUpload.getShippingFee())) {
            if (chromeUpload.getShippingFee().contains("Shipping: US $")) {
                sourcingInfo.setCost(chromeUpload.getShippingFee().replace("Shipping: US $", "").trim());
            } else {
                sourcingInfo.setCost(chromeUpload.getShippingFee().trim());
            }
            sourcingInfo.setCost(sourcingInfo.getCost().replace("AWG", "").trim());

        }

        if (StrUtil.isNotEmpty(chromeUpload.getShippingBy())) {
            sourcingInfo.setShipping(chromeUpload.getShippingBy());
        }

        sourcingInfo.setSiteType(chromeUpload.getSiteType());
        sourcingInfo.setStatus(chromeUpload.getStatus());
        sourcingInfo.setCreateTime(new Date());
        sourcingInfo.setUpdateTime(new Date());
        return sourcingInfo;

    }



//    public String getSkuInfo(String sku){
//
//        JSONArray jsonArr = JSONArray.parseArray(sku);
//        if (jsonArr != null && jsonArr.size() > 0) {
//            for (int i = 0; i < jsonArr.size(); i++) {
//                JSONObject json = jsonArr.getJSONObject(i);
////                String pid = json.getJSONObject("item").getString("num_iid");
//                String spDate = json.getString("properties_name");
//                String pic = json.getString("img");
//                String price = json.getString("price");
//                String skuId = json.getString("sku_id");
//            }
//        }
//        return "";
//    }

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
                if (StrUtil.isNotEmpty(skuType)) {
                    skuType = skuType.substring(0, skuType.indexOf(":"));
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
                if (StrUtil.isNotEmpty(spanText)) {
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
                    if(StrUtil.isNotEmpty(title)){
                        if(typeResult.lastIndexOf(":") != (typeResult.length()-1)){
                            typeResult.append(",");
                        }
                        typeResult.append(title);
                    }
                    else{
                        String spanText = element2.select("span").first().text();
                        if(StrUtil.isNotEmpty(spanText)){
                            if(typeResult.lastIndexOf(":") != (typeResult.length()-1)){
                                typeResult.append(",");
                            }
                            typeResult.append(spanText);
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
            default:
                return "ALI1688_CHILD";
        }
    }


}
