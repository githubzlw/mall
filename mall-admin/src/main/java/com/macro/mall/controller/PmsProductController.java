package com.macro.mall.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.domain.SiteSourcingParam;
import com.macro.mall.dto.PmsProductParam;
import com.macro.mall.dto.PmsProductQueryParam;
import com.macro.mall.dto.PmsProductResult;
import com.macro.mall.entity.XmsChromeUpload;
import com.macro.mall.model.PmsProduct;
import com.macro.mall.model.PmsSkuStock;
import com.macro.mall.service.PmsProductService;
import com.macro.mall.service.XmsAli1688Service;
import com.macro.mall.service.XmsAliExpressService;
import com.macro.mall.util.ProductUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

/**
 * 商品管理Controller
 * Created by macro on 2018/4/26.
 */
@RestController
@Api(tags = "PmsProductController", description = "商品管理")
@RequestMapping("/product")
public class PmsProductController {
    @Autowired
    private PmsProductService productService;

    @Autowired
    private ProductUtils productUtils;
    @Autowired
    private XmsAliExpressService expressService;
    @Autowired
    private XmsAli1688Service ali1688Service;

    @ApiOperation("创建商品")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult create(@RequestBody PmsProductParam productParam) {
        int count = productService.create(productParam);
        if (count > 0) {
            return CommonResult.success(count);
        } else {
            return CommonResult.failed();
        }
    }

    @ApiOperation("根据商品id获取商品编辑信息")
    @RequestMapping(value = "/updateInfo/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<PmsProductResult> getUpdateInfo(@PathVariable Long id) {
        PmsProductResult productResult = productService.getUpdateInfo(id);
        return CommonResult.success(productResult);
    }

    @GetMapping(value = "/getProductInfo")
    @ApiOperation("根据id获得产品数据")
    @ResponseBody
    public CommonResult getProductInfo(@ApiParam(name = "id", value = "产品id", required = true) Long id) {
        PmsProductResult productResult = productService.getUpdateInfo(id);
        return CommonResult.success(productResult);
    }

    @GetMapping(value = "/getCustomProductInfo")
    @ApiOperation("根据id获得产品数据")
    @ResponseBody
    public CommonResult getCustomProductInfo(@ApiParam(name = "id", value = "产品id", required = true) Long id) {
        PmsProductResult productResult = productService.getUpdateInfo(id);
        return CommonResult.success(productResult);
    }


    @ApiOperation("更新商品")
    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult update(@PathVariable Long id, @RequestBody PmsProductParam productParam) {
        int count = productService.update(id, productParam);
        if (count > 0) {
            return CommonResult.success(count);
        } else {
            return CommonResult.failed();
        }
    }

    @ApiOperation("查询商品")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<PmsProduct>> getList(PmsProductQueryParam productQueryParam,
                                                        @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
                                                        @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        List<PmsProduct> productList = productService.list(productQueryParam, pageSize, pageNum);
        return CommonResult.success(CommonPage.restPage(productList));
    }

    @ApiOperation("根据商品名称或货号模糊查询")
    @RequestMapping(value = "/simpleList", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<PmsProduct>> getList(String keyword) {
        List<PmsProduct> productList = productService.list(keyword);
        return CommonResult.success(productList);
    }

    @ApiOperation("批量修改审核状态")
    @RequestMapping(value = "/update/verifyStatus", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updateVerifyStatus(@RequestParam("ids") List<Long> ids,
                                           @RequestParam("verifyStatus") Integer verifyStatus,
                                           @RequestParam("detail") String detail) {
        int count = productService.updateVerifyStatus(ids, verifyStatus, detail);
        if (count > 0) {
            return CommonResult.success(count);
        } else {
            return CommonResult.failed();
        }
    }

    @ApiOperation("批量上下架")
    @RequestMapping(value = "/update/publishStatus", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updatePublishStatus(@RequestParam("ids") List<Long> ids,
                                            @RequestParam("publishStatus") Integer publishStatus) {
        int count = productService.updatePublishStatus(ids, publishStatus);
        if (count > 0) {
            return CommonResult.success(count);
        } else {
            return CommonResult.failed();
        }
    }

    @ApiOperation("批量推荐商品")
    @RequestMapping(value = "/update/recommendStatus", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updateRecommendStatus(@RequestParam("ids") List<Long> ids,
                                              @RequestParam("recommendStatus") Integer recommendStatus) {
        int count = productService.updateRecommendStatus(ids, recommendStatus);
        if (count > 0) {
            return CommonResult.success(count);
        } else {
            return CommonResult.failed();
        }
    }

    @ApiOperation("批量设为新品")
    @RequestMapping(value = "/update/newStatus", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updateNewStatus(@RequestParam("ids") List<Long> ids,
                                        @RequestParam("newStatus") Integer newStatus) {
        int count = productService.updateNewStatus(ids, newStatus);
        if (count > 0) {
            return CommonResult.success(count);
        } else {
            return CommonResult.failed();
        }
    }

    @ApiOperation("批量修改删除状态")
    @RequestMapping(value = "/update/deleteStatus", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updateDeleteStatus(@RequestParam("ids") List<Long> ids,
                                           @RequestParam("deleteStatus") Integer deleteStatus) {
        int count = productService.updateDeleteStatus(ids, deleteStatus);
        if (count > 0) {
            return CommonResult.success(count);
        } else {
            return CommonResult.failed();
        }
    }

    @ApiOperation("批量修改产品状态发布")
    @RequestMapping(value = "/update/productStatus", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updateProductStatus(@RequestParam("ids") List<Long> ids,
                                           @RequestParam("productStatus") Integer productStatus) {
        int count = productService.updateProductStatus(ids, productStatus);
        if (count > 0) {
            return CommonResult.success(count);
        } else {
            return CommonResult.failed();
        }
    }


    @GetMapping(value = "/updateProductCancle")
    @ApiOperation("前台取消商品")
    @ResponseBody
    public CommonResult updateProductCancle(@ApiParam(name = "id", value = "产品id", required = true) Long id
    ,@ApiParam(name = "productStatus", value = "状态", required = true) Integer productStatus) {
        List<Long> ids = new ArrayList<>();
        ids.add(id);
        int count = productService.updateProductStatus(ids, productStatus);
        if (count > 0) {
            return CommonResult.success(count);
        } else {
            return CommonResult.failed();
        }
    }


    @PostMapping(value = "/saveOneBoundProduct")
    @ApiOperation("保存万邦商品数据")
    @ResponseBody
    public CommonResult saveOneBoundProduct(SiteSourcingParam sourcingParam) {
        Assert.notNull(sourcingParam, "sourcingParam null");
        Assert.isTrue(StrUtil.isNotBlank(sourcingParam.getPid()), "pid null");
        Assert.isTrue(sourcingParam.getSiteFlag() > 0, "siteFlag 0");
        // Assert.isTrue(siteFlag == 1 || siteFlag == 2 || siteFlag == 3, "siteFlag no in 1,2,3");

        try {
            return CommonResult.success(this.saveToProduct(sourcingParam));
        } catch (Exception e) {
            e.printStackTrace();
            return CommonResult.failed(e.getMessage());
        }
    }


    private Integer saveToProduct(SiteSourcingParam sourcingParam) {

        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("oneTimeOrderOnly", String.valueOf(sourcingParam.getOneTimeOrderOnly()));
        requestMap.put("chooseType", String.valueOf(sourcingParam.getChooseType()));
        requestMap.put("typeOfShipping", String.valueOf(sourcingParam.getTypeOfShipping()));
        requestMap.put("countryName", sourcingParam.getCountryName());
        requestMap.put("stateName", sourcingParam.getStateName());
        requestMap.put("customType", sourcingParam.getCustomType());
        requestMap.put("cifPort", sourcingParam.getCifPort());
        requestMap.put("fbaWarehouse", sourcingParam.getFbaWarehouse());

        XmsChromeUpload chromeUpload = new XmsChromeUpload();
        if (sourcingParam.getSiteFlag() == 2 || sourcingParam.getSiteFlag() == 3) {
            //先保存到product的数据库
            JSONObject jsonObject = this.checkAndLoadDataCircle(sourcingParam.getPid(), sourcingParam.getSiteFlag());
            if (jsonObject.containsKey("item")) {
                jsonObject = jsonObject.getJSONObject("item");
            }
            List<PmsSkuStock> skuStockList = new ArrayList<>();

            chromeUpload.setSiteType(sourcingParam.getSiteFlag());
            chromeUpload.setUrl(jsonObject.getString("detail_url"));
            chromeUpload.setPrice(jsonObject.getString("price"));
            chromeUpload.setTitle(jsonObject.getString("title"));
            chromeUpload.setPic(jsonObject.getString("pic_url"));

            if (jsonObject.containsKey("item_imgs")) {
                JSONArray item_imgs = jsonObject.getJSONArray("item_imgs");
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < item_imgs.size(); i++) {
                    if (i == item_imgs.size() - 1) {
                        sb.append(item_imgs.getJSONObject(i).getString("url"));
                    } else {
                        sb.append(item_imgs.getJSONObject(i).getString("url") + ",");
                    }
                }
                chromeUpload.setImages(sb.toString());
            }
            chromeUpload.setProductDescription(jsonObject.getString("desc"));
            if (jsonObject.containsKey("props_list")) {
                chromeUpload.setProductDetail(jsonObject.getString("props_list"));
            } else if (jsonObject.containsKey("props")) {
                chromeUpload.setProductDetail(jsonObject.getString("props"));
            }

                /*chromeUpload.setLeadTime();
                chromeUpload.setMoq();
                chromeUpload.setShippingFee();
                chromeUpload.setShippingBy();*/

            Map<String, Set<String>> typeMap = new HashMap<>();
            if (jsonObject.containsKey("skus")) {
                JSONArray jsonArray = jsonObject.getJSONObject("skus").getJSONArray("sku");
                JSONArray prop_imgsArray = jsonObject.getJSONObject("prop_imgs").getJSONArray("prop_img");

                Set<String> checkSet = new HashSet<>();
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject tempSkuJson = jsonArray.getJSONObject(i);
                    PmsSkuStock tempSkuStock = new PmsSkuStock();

                    if (tempSkuJson.containsKey("properties_name")) {
                        // 在sku里面取值type
                        // sku
                        String[] properties_names = tempSkuJson.getString("properties_name").split(";");
                        if (null != properties_names) {

                            List<Map<String, String>> spDataList = new ArrayList<>();
                            for (String childPro : properties_names) {
                                String[] split = childPro.split(":");
                                Map<String, String> childMap = new HashMap<>();
                                if (split.length == 4) {
                                    childMap.put("key", split[2]);
                                    childMap.put("value", split[3]);
                                    if(!typeMap.containsKey(split[2])){
                                        typeMap.put(split[2], new HashSet<>());
                                    }
                                    typeMap.get(split[2]).add(split[3].trim());
                                } else if (split.length >= 2) {
                                    childMap.put("key", split[0]);
                                    childMap.put("value", split[1]);

                                    if(!typeMap.containsKey(split[0])){
                                        typeMap.put(split[0], new HashSet<>());
                                    }
                                    typeMap.get(split[0]).add(split[1].trim());
                                }
                                if (childMap.size() > 0) {
                                    spDataList.add(childMap);
                                }
                            }
                            if(typeMap.size() > 0){
                                StringBuffer sb = new StringBuffer();
                                typeMap.forEach((k,v)->{
                                    sb.append(k + ":");
                                    if(CollectionUtil.isNotEmpty(v)){
                                        v.forEach(cl-> sb.append("," + cl));
                                    }
                                    sb.append(";");
                                });
                                chromeUpload.setType(sb.toString().replace(":,",":"));
                            }
                            tempSkuStock.setSpData(JSONObject.toJSONString(spDataList));
                            if(StrUtil.isNotEmpty(tempSkuStock.getSpData())){
                                if(checkSet.contains(tempSkuStock.getSpData().toLowerCase())){
                                    continue;
                                } else{
                                    checkSet.add(tempSkuStock.getSpData().toLowerCase());
                                }
                            }
                        }
                    }
                    //
                    tempSkuStock.setPic(tempSkuJson.getString("img"));
                    if (StrUtil.isEmpty(tempSkuStock.getPic()) && null != prop_imgsArray && tempSkuJson.containsKey("properties")) {
                        String[] tempPopIdArr = tempSkuJson.getString("properties").split(";");
                        for (int k = 0; k < tempPopIdArr.length; k++) {
                            for (int j = 0; j < prop_imgsArray.size(); j++) {
                                if (tempPopIdArr[k].equalsIgnoreCase(prop_imgsArray.getJSONObject(j).getString("properties"))) {
                                    tempSkuStock.setPic(prop_imgsArray.getJSONObject(j).getString("url"));
                                    break;
                                }
                            }
                        }
                    }
                    if(tempSkuJson.containsKey("price")){
                        tempSkuStock.setPrice(new BigDecimal(tempSkuJson.getDouble("price")));
                    }
                    skuStockList.add(tempSkuStock);
                }
            }
            //保存
            return this.productUtils.apiDataInsertPms(chromeUpload, skuStockList, JSONObject.toJSONString(requestMap));
        } else if (sourcingParam.getSiteFlag() == 1) {
            //先保存到product的数据库
            JSONObject jsonObject = this.checkAndLoadDataCircle(sourcingParam.getPid(), sourcingParam.getSiteFlag());
            if (jsonObject.containsKey("item")) {
                jsonObject = jsonObject.getJSONObject("item");
            }
            List<PmsSkuStock> skuStockList = new ArrayList<>();

            chromeUpload.setSiteType(sourcingParam.getSiteFlag());
            chromeUpload.setUrl(jsonObject.getString("detail_url"));
            chromeUpload.setPrice(jsonObject.getString("price"));
            chromeUpload.setTitle(jsonObject.getString("title"));
            chromeUpload.setPic(jsonObject.getString("pic_url"));

            if (jsonObject.containsKey("item_imgs")) {
                JSONArray item_imgs = jsonObject.getJSONArray("item_imgs");
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < item_imgs.size(); i++) {
                    if(i == item_imgs.size() - 1){
                        sb.append(item_imgs.getJSONObject(i).getString("url"));
                    } else{
                        sb.append(item_imgs.getJSONObject(i).getString("url") + ",");
                    }
                }
                chromeUpload.setImages(sb.toString());
            }

            chromeUpload.setProductDescription(jsonObject.getString("desc"));
            if (jsonObject.containsKey("props_list")) {
                chromeUpload.setProductDetail(jsonObject.getString("props_list"));
            } else if (jsonObject.containsKey("props")) {
                chromeUpload.setProductDetail(jsonObject.getString("props"));
            }

                /*chromeUpload.setLeadTime();
                chromeUpload.setMoq();
                chromeUpload.setShippingFee();
                chromeUpload.setShippingBy();*/

            if (jsonObject.containsKey("skus")) {
                Map<String, Set<String>> typeMap = new HashMap<>();

                JSONArray jsonArray = jsonObject.getJSONObject("skus").getJSONArray("sku");
                JSONArray prop_imgsArray = jsonObject.getJSONObject("prop_imgs").getJSONArray("prop_img");
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject tempSkuJson = jsonArray.getJSONObject(i);
                    PmsSkuStock tempSkuStock = new PmsSkuStock();

                    if (tempSkuJson.containsKey("properties_name")) {
                        // sku
                        String[] properties_names = tempSkuJson.getString("properties_name").split(";");
                        if (null != properties_names) {
                            List<Map<String, String>> spDataList = new ArrayList<>();
                            for (String childPro : properties_names) {
                                String[] split = childPro.split(":");
                                Map<String, String> childMap = new HashMap<>();
                                if (split.length == 4) {
                                    childMap.put("key", split[2]);
                                    childMap.put("value", split[3]);
                                    if(!typeMap.containsKey(split[2])){
                                        typeMap.put(split[2], new HashSet<>());
                                    }
                                    typeMap.get(split[2]).add(split[3].trim());
                                } else if (split.length >= 2) {
                                    childMap.put("key", split[0]);
                                    childMap.put("value", split[1]);

                                    if(!typeMap.containsKey(split[0])){
                                        typeMap.put(split[0], new HashSet<>());
                                    }
                                    typeMap.get(split[0]).add(split[1].trim());
                                }
                                if (childMap.size() > 0) {
                                    spDataList.add(childMap);
                                }
                            }
                            if(typeMap.size() > 0){
                                StringBuffer sb = new StringBuffer();
                                typeMap.forEach((k,v)->{
                                    sb.append(k + ":");
                                    if(CollectionUtil.isNotEmpty(v)){
                                        v.forEach(cl-> sb.append("," + cl));
                                    }
                                    sb.append(";");
                                });
                                chromeUpload.setType(sb.toString().replace(":,",":"));
                            }
                            tempSkuStock.setSpData(JSONObject.toJSONString(spDataList));
                        }
                    }
                    //
                    tempSkuStock.setPic(tempSkuJson.getString("img"));
                    if (StrUtil.isEmpty(tempSkuStock.getPic()) && null != prop_imgsArray && tempSkuJson.containsKey("properties")) {
                        String[] tempPopIdArr = tempSkuJson.getString("properties").split(";");
                        for (int k = 0; k < tempPopIdArr.length; k++) {
                            for (int j = 0; j < prop_imgsArray.size(); j++) {
                                if (tempPopIdArr[k].equalsIgnoreCase(prop_imgsArray.getJSONObject(j).getString("properties"))) {
                                    tempSkuStock.setPic(prop_imgsArray.getJSONObject(j).getString("url"));
                                    break;
                                }
                            }
                        }
                    }
                    if(tempSkuJson.containsKey("price")){
                        tempSkuStock.setPrice(new BigDecimal(tempSkuJson.getDouble("price")));
                    }
                    skuStockList.add(tempSkuStock);
                }
            }
            //保存
            return this.productUtils.apiDataInsertPms(chromeUpload, skuStockList, JSONObject.toJSONString(requestMap));
        } else {
            BeanUtil.copyProperties(sourcingParam, chromeUpload);
            chromeUpload.setTitle(sourcingParam.getName());
            chromeUpload.setPic(sourcingParam.getImg());
            chromeUpload.setSiteType(sourcingParam.getSiteFlag());
            return this.productUtils.apiDataInsertPms(chromeUpload, null, JSONObject.toJSONString(requestMap));
        }

    }


    private JSONObject checkAndLoadDataCircle(String pid, int siteFlag){
        JSONObject jsonObject = this.checkAndLoadData(pid, siteFlag);
        if((siteFlag == 2 || siteFlag == 3) && ( null == jsonObject || jsonObject.size() == 0)){
            jsonObject = this.checkAndLoadData(pid, siteFlag);
        }
        return jsonObject;
    }

    private JSONObject checkAndLoadData(String pid, int siteFlag) {
        try {
            if (siteFlag == 1) {
                return this.ali1688Service.getAlibabaDetail(Long.parseLong(pid), true);
            } else if (siteFlag == 2 || siteFlag == 3) {
                return this.expressService.getItemInfo(pid, true);
            }
            return new JSONObject();
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONObject();
        }
    }

}
