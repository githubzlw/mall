package com.macro.mall.portal.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.common.util.BeanCopyUtil;
import com.macro.mall.common.util.UrlUtil;
import com.macro.mall.entity.*;
import com.macro.mall.model.PmsSkuStock;
import com.macro.mall.model.ProductSkuSaveEdit;
import com.macro.mall.model.UmsMember;
import com.macro.mall.portal.cache.RedisUtil;
import com.macro.mall.portal.config.MicroServiceConfig;
import com.macro.mall.portal.domain.*;
import com.macro.mall.portal.enums.PayFromEnum;
import com.macro.mall.portal.service.*;
import com.macro.mall.portal.enums.OrderPrefixEnum;
import com.macro.mall.portal.util.OrderUtils;
import com.macro.mall.portal.util.PayUtil;
import com.macro.mall.portal.util.SourcingUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.util.Assert;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.macro.mall.portal.controller
 * @date:2021-04-26
 */
@Api(tags = "XmsSourcingController", description = "Sourcing??????????????????")
@RestController
@RequestMapping("/sourcing")
@Slf4j
public class XmsSourcingController {

    @Autowired
    private UmsMemberService umsMemberService;
    @Autowired
    private IXmsSourcingListService xmsSourcingListService;
    @Autowired
    private IXmsCustomerProductService xmsCustomerProductService;
    @Autowired
    private OrderUtils orderUtils;
    @Autowired
    private IPmsSkuStockService iPmsSkuStockService;
    @Autowired
    private PayUtil payUtil;
    @Autowired
    private IXmsPmsProductEditService xmsPmsProductEditService;
    @Autowired
    private IXmsPmsSkuStockEditService xmsPmsSkuStockEditService;
    @Autowired
    private MicroServiceConfig microServiceConfig;
    @Autowired
    private PmsPortalProductService pmsPortalProductService;
    @Autowired
    private RedisUtil redisUtil;
    private UrlUtil urlUtil = UrlUtil.getInstance();
    @Autowired
    private IXmsProductSaveEditService xmsProductSaveEditService;
    @Autowired
    private SourcingUtils sourcingUtils;

    @InitBinder
    protected void init(HttpServletRequest request, ServletRequestDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }

    @ApiOperation("sourcingList??????")
    @RequestMapping(value = "/sourcingList", method = RequestMethod.GET)
    public CommonResult sourcingList(XmsSourcingInfoParam sourcingParam) {

        Assert.isTrue(null != sourcingParam, "sourcingParam null");

        UmsMember currentMember = this.umsMemberService.getCurrentMember();

        try {

            // ??????sourcing??????
            String uuid = sourcingParam.getUuid();
            if (StrUtil.isNotEmpty(uuid) && uuid.length() > 10) {
                this.sourcingUtils.mergeSourcingList(currentMember, uuid);
            }

            if (null == sourcingParam.getPageNum() || sourcingParam.getPageNum() == 0) {
                sourcingParam.setPageNum(1);
            }
            if (null == sourcingParam.getPageSize() || sourcingParam.getPageSize() == 0) {
                sourcingParam.setPageSize(10);
            }
            sourcingParam.setMemberId(currentMember.getId());
            sourcingParam.setUsername(currentMember.getUsername());
            Page<XmsSourcingList> listPage = this.xmsSourcingListService.list(sourcingParam);

            if (CollectionUtil.isNotEmpty(listPage.getRecords())) {
                listPage.getRecords().forEach(e -> {
                    if (StrUtil.isEmpty(e.getCost())) {
                        e.setCost("");
                    }
                    if (StrUtil.isNotBlank(e.getImages())) {
                        e.setImages(e.getImages().split(",")[0]);
                    }
                    if (null == e.getChooseType() || e.getChooseType() == 0) {
                        // ??????????????????ChooseType??????????????????????????????????????????
                        e.setChooseType(currentMember.getSourcingChooseType());
                        e.setTypeOfShipping(currentMember.getSourcingTypeOfShipping());
                        e.setCountryName(currentMember.getSourcingCountryName());
                        e.setCountryId(currentMember.getSourcingCountryId());
                        e.setStateName(currentMember.getSourcingStateName());
                        e.setCustomType(currentMember.getSourcingCustomType());
                        e.setOrderQuantity(currentMember.getSourcingOrderQuantity());
                        e.setRemark(currentMember.getSourcingRemark());
                        e.setPrcFlag(currentMember.getSourcingPrcFlag());
                        e.setCifPort(currentMember.getSourcingCifPort());
                        e.setFbaWarehouse(currentMember.getSourcingFbaWarehouse());
                    }
                });
            }

            return CommonResult.success(listPage);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("sourcingList,sourcingParam[{}],error:", sourcingParam, e);
            return CommonResult.failed("query failed");
        }
    }


    @ApiOperation("sourcingList??????")
    @RequestMapping(value = "/sourcingListStatistics", method = RequestMethod.GET)
    public CommonResult sourcingListStatistics(String url, String uuid) {

        UmsMember currentMember = this.umsMemberService.getCurrentMember();

        try {
            // ??????sourcing??????
            if (StrUtil.isNotEmpty(uuid) && uuid.length() > 10) {
                this.sourcingUtils.mergeSourcingList(currentMember, uuid);
            }

            LambdaQueryWrapper<XmsSourcingList> lambdaQuery = Wrappers.lambdaQuery();
            lambdaQuery.eq(XmsSourcingList::getUsername, currentMember.getUsername());
            lambdaQuery.gt(XmsSourcingList::getStatus, -1);
            if (StrUtil.isNotEmpty(url)) {
                lambdaQuery.and(query -> query.like(XmsSourcingList::getTitle, url).or().like(XmsSourcingList::getUrl, url));
            }
            List<XmsSourcingList> list = this.xmsSourcingListService.list(lambdaQuery);

            Map<String, Integer> mapStatistics = new HashMap<>();
            mapStatistics.put("all", 0);
            mapStatistics.put("inProgressing", 0);
            mapStatistics.put("Pending", 0);
            mapStatistics.put("Failed", 0);
            mapStatistics.put("Success", 0);
            mapStatistics.put("Cancel", 0);

            if (CollectionUtil.isNotEmpty(list)) {
                mapStatistics.put("all", list.size());
                // ?????????0->????????????1->????????????2->????????? 4->?????????5->??????????????? -1->?????????
                int Pending = (int) list.stream().filter(e -> 0 == e.getStatus()).count();
                mapStatistics.put("Pending", Pending);

                int inProgressing = (int) list.stream().filter(e -> 1 == e.getStatus()).count();
                mapStatistics.put("inProgressing", inProgressing);

                int Failed = (int) list.stream().filter(e -> 5 == e.getStatus()).count();
                mapStatistics.put("Failed", Failed);

                int Success = (int) list.stream().filter(e -> 2 == e.getStatus()).count();
                mapStatistics.put("Success", Success);

                int Cancel = (int) list.stream().filter(e -> 4 == e.getStatus()).count();
                mapStatistics.put("Cancel", Cancel);

                list.clear();
            }
            return CommonResult.success(mapStatistics);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("sourcingListStatistics,currentMember[{}],error:", currentMember, e);
            return CommonResult.failed("query failed");
        }
    }

    @ApiOperation("sourcingList???Pending??????")
    @RequestMapping(value = "/sourcingListPendingCount", method = RequestMethod.GET)
    public CommonResult sourcingListPendingCount() {

        UmsMember currentMember = this.umsMemberService.getCurrentMember();
        try {
            LambdaQueryWrapper<XmsSourcingList> lambdaQuery = Wrappers.lambdaQuery();
            lambdaQuery.eq(XmsSourcingList::getMemberId, currentMember.getId());
            lambdaQuery.eq(XmsSourcingList::getStatus, 0);
            int count = this.xmsSourcingListService.count(lambdaQuery);
            return CommonResult.success(count);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("sourcingListPendingCount,currentMember[{}],error:", currentMember, e);
            return CommonResult.failed("query failed");
        }
    }


    @ApiOperation("sourcingListByUuid??????")
    @RequestMapping(value = "/sourcingListByUuid", method = RequestMethod.GET)
    public CommonResult sourcingListByUuid(XmsSourcingInfoParam sourcingParam) {

        Assert.isTrue(null != sourcingParam, "sourcingParam null");
        Assert.isTrue(StrUtil.isNotBlank(sourcingParam.getUsername()), "username null");

        try {

            if (null == sourcingParam.getPageNum() || sourcingParam.getPageNum() == 0) {
                sourcingParam.setPageNum(1);
            }
            if (null == sourcingParam.getPageSize() || sourcingParam.getPageSize() == 0) {
                sourcingParam.setPageSize(10);
            }
            Page<XmsSourcingList> listPage = this.xmsSourcingListService.list(sourcingParam);

            if (CollectionUtil.isNotEmpty(listPage.getRecords())) {
                listPage.getRecords().forEach(e -> {
                    if (StrUtil.isEmpty(e.getCost())) {
                        e.setCost("");
                    }
                });
            }

            return CommonResult.success(listPage);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("sourcingList,sourcingParam[{}],error:", sourcingParam, e);
            return CommonResult.failed("query failed");
        }
    }


    @ApiOperation("sourcingListByUuid??????")
    @RequestMapping(value = "/sourcingListStatisticsByUuid", method = RequestMethod.GET)
    public CommonResult sourcingListStatisticsByUuid(String url, String uuid) {

        // Assert.isTrue(StrUtil.isNotBlank(url), "url null");
        Assert.isTrue(StrUtil.isNotBlank(uuid), "uuid null");

        try {

            LambdaQueryWrapper<XmsSourcingList> lambdaQuery = Wrappers.lambdaQuery();
            lambdaQuery.eq(XmsSourcingList::getUsername, uuid);
            lambdaQuery.gt(XmsSourcingList::getStatus, -1);
            if (StrUtil.isNotEmpty(url)) {
                lambdaQuery.and(query -> query.like(XmsSourcingList::getTitle, url).or().like(XmsSourcingList::getUrl, url));
            }
            List<XmsSourcingList> list = this.xmsSourcingListService.list(lambdaQuery);

            Map<String, Integer> mapStatistics = new HashMap<>();
            mapStatistics.put("all", 0);
            mapStatistics.put("inProgressing", 0);
            mapStatistics.put("Pending", 0);
            mapStatistics.put("Failed", 0);
            mapStatistics.put("Success", 0);
            mapStatistics.put("Cancel", 0);

            if (CollectionUtil.isNotEmpty(list)) {
                mapStatistics.put("all", list.size());
                // ?????????0->????????????1->????????????2->????????? 4->?????????5->??????????????? -1->?????????
                int Pending = (int) list.stream().filter(e -> 0 == e.getStatus()).count();
                mapStatistics.put("Pending", Pending);

                int inProgressing = (int) list.stream().filter(e -> 1 == e.getStatus()).count();
                mapStatistics.put("inProgressing", inProgressing);

                int Failed = (int) list.stream().filter(e -> 5 == e.getStatus()).count();
                mapStatistics.put("Failed", Failed);

                int Success = (int) list.stream().filter(e -> 2 == e.getStatus()).count();
                mapStatistics.put("Success", Success);

                int Cancel = (int) list.stream().filter(e -> 4 == e.getStatus()).count();
                mapStatistics.put("Cancel", Cancel);

                list.clear();
            }
            return CommonResult.success(mapStatistics);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("sourcingListStatistics,uuid[{}],error:", uuid, e);
            return CommonResult.failed("query failed");
        }
    }


    @ApiOperation("SourcingList???????????????????????????")
    @RequestMapping(value = "/saveSourcingProduct", method = RequestMethod.POST)
    public CommonResult saveSourcingProduct(SourcingProductParam sourcingProductParam) {
        Assert.notNull(sourcingProductParam, "sourcingProductParam null");
        Assert.isTrue(null != sourcingProductParam.getProductId() && sourcingProductParam.getProductId() > 0, "productId null");
        Assert.isTrue(null != sourcingProductParam.getSourcingId() && sourcingProductParam.getSourcingId() > 0, "sourcingId null");
        Assert.isTrue(null != sourcingProductParam.getWeight() && sourcingProductParam.getWeight().doubleValue() > 0, "weight null");
        Assert.isTrue(StrUtil.isNotBlank(sourcingProductParam.getSkuList()), "skuList null");
        Assert.isTrue(StrUtil.isNotBlank(sourcingProductParam.getSaveList()), "saveList null");

        UmsMember currentMember = this.umsMemberService.getCurrentMember();
        try {
            // ????????????????????????
            XmsSourcingList xmsSourcingList = this.xmsSourcingListService.getById(sourcingProductParam.getSourcingId());
            if (null == xmsSourcingList) {
                return CommonResult.validateFailed("No data available");
            }

            List<ProductSkuSaveEdit> saveEditList = JSONArray.parseArray(sourcingProductParam.getSaveList(), ProductSkuSaveEdit.class);

            List<XmsPmsSkuStockEdit> stockEditList = JSONArray.parseArray(sourcingProductParam.getSkuList(), XmsPmsSkuStockEdit.class);
            if (CollectionUtil.isEmpty(stockEditList)) {
                return CommonResult.validateFailed("No sku available");
            }
            StringBuffer skuSb = new StringBuffer();
            stockEditList.forEach(e -> {
                e.setMemberId(currentMember.getId());
                skuSb.append(",").append(e.getSkuCode());
            });

            synchronized (currentMember.getId()) {


                Long productId;
                // ?????????????????????????????????
                QueryWrapper<XmsPmsProductEdit> productEditWrapper = new QueryWrapper<>();
                productEditWrapper.lambda().eq(XmsPmsProductEdit::getProductId, sourcingProductParam.getProductId())
                        .eq(XmsPmsProductEdit::getMemberId, currentMember.getId());

                List<XmsPmsProductEdit> list = this.xmsPmsProductEditService.list(productEditWrapper);

                QueryWrapper<XmsProductSaveEdit> saveEditQueryWrapper = new QueryWrapper<>();
                saveEditQueryWrapper.lambda().eq(XmsProductSaveEdit::getMemberId, currentMember.getId())
                        .eq(XmsProductSaveEdit::getProductId, sourcingProductParam.getProductId());
                XmsProductSaveEdit one = this.xmsProductSaveEditService.getOne(saveEditQueryWrapper);
                if (null == one || null == one.getId() || one.getId() <= 0) {
                    one = new XmsProductSaveEdit();
                    one.setCreateTime(new Date());
                    one.setProductId(sourcingProductParam.getProductId());
                    one.setMemberId(currentMember.getId());
                }

                // ??????sku?????????
                one.setSkuJson(JSONArray.toJSONString(saveEditList));
                one.setCollectionId(sourcingProductParam.getCollectionId());
                one.setProductTags(sourcingProductParam.getProductTags());
                one.setProductType(sourcingProductParam.getProductType());

                // ???????????????????????????????????????sku????????????????????????
                if (CollectionUtil.isNotEmpty(list)) {
                    XmsPmsProductEdit pmsProductEdit = list.get(0);
                    pmsProductEdit.setAlbumPics(sourcingProductParam.getAlbumPics());
                    // productId = pmsProductEdit.getId();
                    productId = sourcingProductParam.getProductId();
                    pmsProductEdit.setName(sourcingProductParam.getName());
                    // ??????sku??????
                    QueryWrapper<XmsPmsSkuStockEdit> skuEditWrapper = new QueryWrapper<>();
                    skuEditWrapper.lambda().eq(XmsPmsSkuStockEdit::getProductId, sourcingProductParam.getProductId());
                    List<XmsPmsSkuStockEdit> editList = this.xmsPmsSkuStockEditService.list(skuEditWrapper);
                    if (CollectionUtil.isNotEmpty(editList)) {
                        Map<String, XmsPmsSkuStockEdit> skuStockEditMap = new HashMap<>();
                        editList.forEach(e -> skuStockEditMap.put(e.getSkuCode(), e));

                        List<XmsPmsSkuStockEdit> updateList = stockEditList.stream().filter(e -> skuStockEditMap.containsKey(e.getSkuCode())).collect(Collectors.toList());
                        List<XmsPmsSkuStockEdit> insertList = stockEditList.stream().filter(e -> !skuStockEditMap.containsKey(e.getSkuCode())).collect(Collectors.toList());

                        if (CollectionUtil.isNotEmpty(updateList)) {
                            updateList.forEach(e -> {
                                e.setId(skuStockEditMap.get(e.getSkuCode()).getId());
                                e.setSpData(skuStockEditMap.get(e.getSkuCode()).getSpData());
                                e.setPrice(skuStockEditMap.get(e.getSkuCode()).getPrice());
                            });
                            this.xmsPmsSkuStockEditService.saveOrUpdateBatch(updateList);
                            updateList.clear();
                        }

                        if (CollectionUtil.isNotEmpty(insertList)) {
                            this.xmsPmsSkuStockEditService.saveBatch(insertList);
                            insertList.clear();
                        }

                        editList.clear();
                    } else {
                        this.xmsPmsSkuStockEditService.saveBatch(stockEditList);
                        stockEditList.clear();
                    }
                    pmsProductEdit.setUpdateTime(new Date());
                    this.xmsPmsProductEditService.updateById(pmsProductEdit);

                    one.setProductJson(JSONObject.toJSONString(pmsProductEdit));
                } else {
                    // ???????????????????????????????????????
                    // ??????product??????
                    XmsPmsProductEdit pmsProductEdit = new XmsPmsProductEdit();
                    BeanUtil.copyProperties(sourcingProductParam, pmsProductEdit);
                    pmsProductEdit.setProductId(sourcingProductParam.getProductId());
                    pmsProductEdit.setMemberId(currentMember.getId());
                    pmsProductEdit.setWeight(sourcingProductParam.getWeight());
                    pmsProductEdit.setUpdateTime(new Date());

                    one.setProductJson(JSONObject.toJSONString(pmsProductEdit));

                    this.xmsPmsProductEditService.save(pmsProductEdit);
                    // productId = pmsProductEdit.getId();
                    productId = sourcingProductParam.getProductId();
                    // ??????sku??????
                    this.xmsPmsSkuStockEditService.saveBatch(stockEditList);
                    stockEditList.clear();
                }

                saveEditList.clear();
                // ??????????????????
                this.xmsProductSaveEditService.saveOrUpdate(one);

                // ??????shopify??????
                UmsMember byId = this.umsMemberService.getById(currentMember.getId());
                if (StrUtil.isEmpty(byId.getShopifyName())) {
                    return CommonResult.validateFailed("Please bind the store first");
                }


                QueryWrapper<XmsCustomerProduct> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(XmsCustomerProduct::getProductId, sourcingProductParam.getProductId())
                        .eq(XmsCustomerProduct::getSourcingId, sourcingProductParam.getSourcingId())
                        .eq(XmsCustomerProduct::getMemberId, currentMember.getId());
                int count = this.xmsCustomerProductService.count(queryWrapper);
                if (count == 0) {
                    XmsCustomerProduct customerProduct = new XmsCustomerProduct();
                    customerProduct.setShopifyProductId(0L);
                    customerProduct.setCreateTime(new Date());
                    customerProduct.setSourcingId(sourcingProductParam.getSourcingId());
                    customerProduct.setProductId(sourcingProductParam.getProductId());
                    customerProduct.setSiteType(xmsSourcingList.getSiteType());
                    customerProduct.setCostPrice(sourcingProductParam.getPriceXj());
                    customerProduct.setSourceLink(xmsSourcingList.getSourceLink());
                    customerProduct.setStatus(1);
                    customerProduct.setShopifyName(byId.getShopifyName());
                    customerProduct.setTitle(sourcingProductParam.getName());
                    customerProduct.setImg(sourcingProductParam.getPic());
                    customerProduct.setMemberId(currentMember.getId());
                    customerProduct.setUsername(currentMember.getUsername());
                    this.xmsCustomerProductService.save(customerProduct);
                }

                Map<String, String> param = new HashMap<>();
                param.put("pid", String.valueOf(productId));
                param.put("published", "0");
                param.put("shopName", byId.getShopifyName());
                param.put("skuCodes", skuSb.toString().substring(1));
                param.put("collectionId", sourcingProductParam.getCollectionId());
                param.put("productType", sourcingProductParam.getProductType());
                param.put("productTags", sourcingProductParam.getProductTags());
                param.put("memberId", String.valueOf(currentMember.getId()));
                param.put("sourcingId", String.valueOf(sourcingProductParam.getSourcingId()));

                JSONObject jsonObject = this.urlUtil.postURL(microServiceConfig.getShopifyUrl() + "/addProduct", param);

                if (null != jsonObject) {
                    return JSONObject.parseObject(jsonObject.toJSONString(), CommonResult.class);
                }
                return CommonResult.failed("addProduct error");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("saveSourcingProduct,sourcingProductParam[{}],error:", sourcingProductParam, e);
            return CommonResult.failed("saveSourcingProduct failed");
        }
    }


    @ApiOperation("SourcingList??????")
    @RequestMapping(value = "/deleteSourcing", method = RequestMethod.POST)
    @ApiImplicitParams({@ApiImplicitParam(name = "sourcingId", value = "sourcing??????ID", required = true, dataType = "Long")})
    public CommonResult deleteSourcing(Long sourcingId) {

        UmsMember currentMember = this.umsMemberService.getCurrentMember();
        try {
            // ????????????????????????
            XmsSourcingList xmsSourcingList = this.xmsSourcingListService.getById(sourcingId);
            if (null == xmsSourcingList) {
                return CommonResult.validateFailed("No data available");
            }

            UpdateWrapper<XmsSourcingList> updateWrapper = new UpdateWrapper<>();
            updateWrapper.lambda().eq(XmsSourcingList::getId, sourcingId).set(XmsSourcingList::getStatus, 4);
            boolean update = this.xmsSourcingListService.update(null, updateWrapper);

            /*// sourclinglist?????????????????????quato price?????????????????????????????????
            UpdateWrapper<XmsCustomerProduct> productUpdateWrapper = new UpdateWrapper<>();
            productUpdateWrapper.lambda().set(XmsCustomerProduct::getImportFlag, 0)
                    .eq(XmsCustomerProduct::getSourcingId, sourcingId)
                    .eq(XmsCustomerProduct::getMemberId, currentMember.getId());

            this.xmsCustomerProductService.update(productUpdateWrapper);*/

            return CommonResult.success(update);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("deleteSourcing,sourcingId[{}],error:", sourcingId, e);
            return CommonResult.failed("deleteSourcing error");
        }
    }


    @ApiOperation("SourcingList????????????")
    @RequestMapping(value = "/deleteBatchSourcing", method = RequestMethod.POST)
    public CommonResult deleteBatchSourcing(@RequestParam("sourcingIdList") List<Long> sourcingIdList) {
        Assert.isTrue(CollectionUtil.isNotEmpty(sourcingIdList), "sourcingIdList null");
        try {

            UpdateWrapper<XmsSourcingList> updateWrapper = new UpdateWrapper<>();
            updateWrapper.lambda().in(XmsSourcingList::getId, sourcingIdList).set(XmsSourcingList::getStatus, -1);
            boolean update = this.xmsSourcingListService.update(null, updateWrapper);
            return CommonResult.success(update);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("deleteBatchSourcing,sourcingIdList[{}],error:", sourcingIdList, e);
            return CommonResult.failed("deleteSourcing error");
        }
    }


    @ApiOperation("SourcingList????????????????????????")
    @RequestMapping(value = "/addToYouLiveProductList", method = RequestMethod.POST)
    @ApiImplicitParams({@ApiImplicitParam(name = "sourcingId", value = "sourcing??????ID", required = true, dataType = "Long")})
    public CommonResult addToYouLiveProductList(Long sourcingId) {
        try {
            // ????????????????????????
            XmsSourcingList xmsSourcingList = this.xmsSourcingListService.getById(sourcingId);
            if (null == xmsSourcingList) {
                return CommonResult.validateFailed("No data available");
            }

            // ????????????????????????
            XmsCustomerProduct product = new XmsCustomerProduct();
            product.setMemberId(this.umsMemberService.getCurrentMember().getId());
            product.setUsername(this.umsMemberService.getCurrentMember().getUsername());
            product.setSourcingId(sourcingId);
            product.setProductId(xmsSourcingList.getProductId());
            boolean isCheck = this.xmsSourcingListService.checkHasXmsCustomerProduct(product);

            if (isCheck) {
                return CommonResult.validateFailed("The data already exists");
            }

            // ??????????????????
            product.setProductId(Long.parseLong(String.valueOf(xmsSourcingList.getProductId())));
            product.setSourceLink(xmsSourcingList.getSourceLink());
            product.setStatus(0);
            product.setSiteType(xmsSourcingList.getSiteType());
            product.setCreateTime(new Date());
            product.setUpdateTime(new Date());
            product.setImportFlag(1);
            this.xmsCustomerProductService.save(product);
            // ????????????????????????
            UpdateWrapper<XmsSourcingList> updateWrapper = new UpdateWrapper<>();
            updateWrapper.lambda().eq(XmsSourcingList::getId, xmsSourcingList.getId()).set(XmsSourcingList::getAddProductFlag, 1);
            this.xmsSourcingListService.update(null, updateWrapper);
            return CommonResult.success(product);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("addToMyProductList,sourcingId[{}],error:", sourcingId, e);
            return CommonResult.failed("addToMyProductList error");
        }
    }


    @ApiOperation("?????????Sourcing????????????")
    @RequestMapping(value = "/payBySourcingProduct", method = RequestMethod.POST)
    public CommonResult payBySourcingProduct(HttpServletRequest request, SourcingPayParam sourcingPayParam) {

        Assert.isTrue(null != sourcingPayParam, "productPayParam null");
        Assert.isTrue(null != sourcingPayParam.getProductId() && sourcingPayParam.getProductId() > 0, "productId null");
        Assert.isTrue(null != sourcingPayParam.getSourcingId() && sourcingPayParam.getSourcingId() > 0, "sourcingId null");
        Assert.isTrue(CollectionUtil.isNotEmpty(sourcingPayParam.getSkuCodeAndNumList()), "skuCodeAndNumList null");
        Assert.isTrue(StrUtil.isNotBlank(sourcingPayParam.getReceiverName()), "receiverName null");
        Assert.isTrue(StrUtil.isNotBlank(sourcingPayParam.getReceiverPhone()), "receiverPhone null");
        Assert.isTrue(StrUtil.isNotBlank(sourcingPayParam.getReceiverCountry()), "receiverCountry null");
        Assert.isTrue(StrUtil.isNotBlank(sourcingPayParam.getReceiverPostCode()), "receiverPostCode null");

        try {
            UmsMember currentMember = this.umsMemberService.getCurrentMember();
            Long memberId = currentMember.getId();
            String username = currentMember.getUsername();

            // ??????SourcingList
            XmsSourcingInfoParam sourcingParam = new XmsSourcingInfoParam();
            sourcingParam.setUsername(username);
            sourcingParam.setMemberId(memberId);
            sourcingParam.setProductId(sourcingPayParam.getProductId());
            XmsSourcingList xmsSourcingList = this.xmsSourcingListService.querySingleSourcingList(sourcingParam);
            if (null == xmsSourcingList) {
                return CommonResult.failed("No data available");
            }

            // ???????????????skuCode
            Map<String, Integer> orderNumMap = new HashMap<>();// ?????????????????????
            List<String> skuCodeList = new ArrayList<>();
            sourcingPayParam.getSkuCodeAndNumList().forEach(e -> {
                String[] arr = e.split(":");
                skuCodeList.add(arr[0]);
                orderNumMap.put(sourcingPayParam.getProductId() + "_" + arr[0], Integer.parseInt(arr[1]));
            });

            // ???????????????sku??????
            List<Long> productIdList = new ArrayList<>();
            productIdList.add(sourcingPayParam.getProductId());
            List<PmsSkuStock> skuStockList = this.iPmsSkuStockService.getSkuStockByParam(productIdList, skuCodeList);
            if (CollectionUtil.isEmpty(skuStockList)) {
                return CommonResult.failed("No data available");
            }

            List<PmsSkuStock> pmsSkuStockList = BeanCopyUtil.deepListCopy(skuStockList);// ????????????
            // ??????????????????skuCode??????????????????????????????
            pmsSkuStockList.forEach(e -> e.setStock(orderNumMap.getOrDefault(e.getProductId() + "_" + e.getSkuCode(), 0)));
            pmsSkuStockList = pmsSkuStockList.stream().filter(e -> e.getStock() > 0).collect(Collectors.toList());

            // ???????????????????????????
            double totalFreight = 0;

            // ?????????????????????????????????
            String orderNo = this.orderUtils.getOrderNoByRedis(OrderPrefixEnum.PURCHASE_STOCK_ORDER.getCode());
            OrderPayParam orderPayParam = new OrderPayParam();
            BeanUtil.copyProperties(sourcingPayParam, orderPayParam);
            // ?????????????????????????????????
            GenerateOrderParam generateOrderParam = GenerateOrderParam.builder().orderNo(orderNo).totalFreight(totalFreight).currentMember(currentMember).pmsSkuStockList(pmsSkuStockList).orderPayParam(orderPayParam).type(0).build();
            GenerateOrderResult orderResult = this.orderUtils.generateOrder(generateOrderParam);

            /*// ????????????????????????
            QueryWrapper<XmsCustomerProduct> productQueryWrapper = new QueryWrapper<>();
            productQueryWrapper.lambda().eq(XmsCustomerProduct::getProductId, sourcingPayParam.getProductId()).eq(XmsCustomerProduct::getSourcingId, sourcingPayParam.getSourcingId());
            List<XmsCustomerProduct> list = this.xmsCustomerProductService.list(productQueryWrapper);
            if (CollectionUtil.isNotEmpty(list)) {
                if (StrUtil.isNotEmpty(list.get(0).getAddress())) {
                    if (!list.get(0).getAddress().contains(sourcingPayParam.getReceiverCountry())) {
                        list.get(0).setAddress(list.get(0).getAddress() + "," + sourcingPayParam.getReceiverCountry() + ",");
                    }
                } else {
                    list.get(0).setAddress(sourcingPayParam.getReceiverCountry() + ",");
                }
                list.get(0).setUpdateTime(new Date());
                this.xmsCustomerProductService.updateById(list.get(0));
            }*/

            skuCodeList.clear();
            productIdList.clear();
            skuStockList.clear();
            pmsSkuStockList.clear();
            orderNumMap.clear();

            return this.payUtil.beforePayAndPay(orderResult, currentMember, request, PayFromEnum.SOURCING_ORDER, this.redisUtil);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("payBySourcingProduct,sourcingPayParam[{}],error:", sourcingPayParam, e);
            return CommonResult.failed("execute failed");
        }
    }


    @ApiOperation("??????Sourcing")
    @RequestMapping(value = "/sourcingAgain", method = RequestMethod.POST)
    @ApiImplicitParams({@ApiImplicitParam(name = "sourcingId", value = "sourcing??????ID", required = true, dataType = "Long")})
    public CommonResult sourcingAgain(Long sourcingId) {
        UmsMember currentMember = this.umsMemberService.getCurrentMember();
        try {
            // ????????????????????????
            XmsSourcingList xmsSourcingList = this.xmsSourcingListService.getById(sourcingId);
            if (null == xmsSourcingList) {
                return CommonResult.validateFailed("No data available");
            }
            if (!xmsSourcingList.getUsername().equalsIgnoreCase(currentMember.getUsername()) && !xmsSourcingList.getMemberId().equals(currentMember.getId())) {
                return CommonResult.validateFailed("No data available");
            }
            if (4 == xmsSourcingList.getStatus() || 5 == xmsSourcingList.getStatus() || -1 == xmsSourcingList.getStatus()) {
                UpdateWrapper<XmsSourcingList> updateWrapper = new UpdateWrapper<>();
                updateWrapper.lambda().eq(XmsSourcingList::getId, sourcingId)
                        .set(XmsSourcingList::getStatus, 0)
                        .set(XmsSourcingList::getCreateTime, new Date())
                        .set(XmsSourcingList::getUpdateTime, new Date());
                boolean update = this.xmsSourcingListService.update(null, updateWrapper);
                return CommonResult.success(update);
            } else {
                return CommonResult.failed("this sourcing state is not cancel or faild");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("sourcingAgain,sourcingId[{}],error:", sourcingId, e);
            return CommonResult.failed("sourcingAgain error");
        }
    }


    @ApiOperation("??????Sourcing??????")
    @RequestMapping(value = "/updateSourcingInfo", method = RequestMethod.POST)
    public CommonResult updateSourcingInfo(SiteSourcingParam siteSourcingParam) {

        Assert.notNull(siteSourcingParam, "siteSourcingParam null");

        try {

            UpdateWrapper<XmsSourcingList> updateWrapper = new UpdateWrapper<>();
            boolean update = false;
            //1:Drop Shipping
            if (siteSourcingParam.getChooseType() == 1) {
                updateWrapper.lambda().eq(XmsSourcingList::getId, siteSourcingParam.getId())
                        .set(XmsSourcingList::getChooseType, siteSourcingParam.getChooseType())
                        .set(XmsSourcingList::getOrderQuantity, siteSourcingParam.getAverageDailyOrder())
                        .set(XmsSourcingList::getRemark, siteSourcingParam.getData());
                update = this.xmsSourcingListService.update(null, updateWrapper);
            }
            //2:Wholesale and Bulk Shipping
            if (siteSourcingParam.getChooseType() == 2) {
                updateWrapper.lambda().eq(XmsSourcingList::getId, siteSourcingParam.getId())
                        .set(XmsSourcingList::getChooseType, siteSourcingParam.getChooseType())
                        .set(XmsSourcingList::getOrderQuantity, siteSourcingParam.getAverageDailyOrder())
                        .set(XmsSourcingList::getRemark, siteSourcingParam.getData())
                        .set(XmsSourcingList::getTypeOfShipping, siteSourcingParam.getTypeOfShipping())
                        .set(XmsSourcingList::getCountryName, siteSourcingParam.getCountryName())
                        .set(XmsSourcingList::getStateName, siteSourcingParam.getStateName())
                        .set(XmsSourcingList::getFbaWarehouse, siteSourcingParam.getFbaWarehouse());
                update = this.xmsSourcingListService.update(null, updateWrapper);
            }
            //4:Product Customization
            if (siteSourcingParam.getChooseType() == 4) {
                updateWrapper.lambda().eq(XmsSourcingList::getId, siteSourcingParam.getId())
                        .set(XmsSourcingList::getChooseType, siteSourcingParam.getChooseType())
                        .set(XmsSourcingList::getCustomType, siteSourcingParam.getCustomType())
                        .set(XmsSourcingList::getOrderQuantity, siteSourcingParam.getAverageDailyOrder())
                        .set(XmsSourcingList::getRemark, siteSourcingParam.getData());
                update = this.xmsSourcingListService.update(null, updateWrapper);
            }
            return CommonResult.success(update);


        } catch (Exception e) {
            e.printStackTrace();
            log.error("updateSourcingInfo,siteBuyForMeParam[{}],error:", siteSourcingParam, e);
            return CommonResult.failed(e.getMessage());
        }
    }


    @ApiOperation("???????????????????????????????????????")
    @RequestMapping(value = "/beforeSyncPublicProduct", method = RequestMethod.POST)
    public CommonResult beforeSyncPublicProduct(Long productId) {
        Assert.isTrue(null != productId && productId > 0, "productId null");

        UmsMember currentMember = this.umsMemberService.getCurrentMember();
        try {

            // ??????????????????
            PmsPortalProductDetail detail = this.pmsPortalProductService.detail(productId);
            if (null == detail) {
                return CommonResult.failed("no this product");
            }

            QueryWrapper<XmsSourcingList> sourcingWrapper = new QueryWrapper<>();
            sourcingWrapper.lambda().eq(XmsSourcingList::getProductId, productId).eq(XmsSourcingList::getMemberId, currentMember.getId());
            XmsSourcingList sourcingOne = this.xmsSourcingListService.getOne(sourcingWrapper);
            if (null != sourcingOne && sourcingOne.getId() > 0) {
                // ???????????????????????????
                return CommonResult.success(sourcingOne);
            }
            sourcingWrapper = new QueryWrapper<>();


            sourcingWrapper.lambda().eq(XmsSourcingList::getProductId, productId);
            List<XmsSourcingList> list = this.xmsSourcingListService.list(sourcingWrapper);
            if (CollectionUtil.isEmpty(list)) {
                return CommonResult.failed("no this sourcing");
            }

            /*// ????????????sku??????

            QueryWrapper<XmsPmsSkuStockEdit> stockEditQueryWrapper = new QueryWrapper<>();
            stockEditQueryWrapper.lambda().eq(XmsPmsSkuStockEdit::getProductId, productId).eq(XmsPmsSkuStockEdit::getMemberId, currentMember.getId());
            this.xmsPmsSkuStockEditService.remove(stockEditQueryWrapper);

            QueryWrapper<XmsPmsProductEdit> productEditWrapper = new QueryWrapper<>();
            productEditWrapper.lambda().eq(XmsPmsProductEdit::getProductId, productId).eq(XmsPmsProductEdit::getMemberId, currentMember.getId());
            XmsPmsProductEdit one = this.xmsPmsProductEditService.getOne(productEditWrapper);

            if (null != one) {
                Long id = one.getId();
                BeanUtil.copyProperties(detail.getProduct(), one);
                one.setId(id);
            } else {
                one = new XmsPmsProductEdit();
                BeanUtil.copyProperties(detail.getProduct(), one);
                one.setId(null);
                one.setCreateTime(new Date());

            }
            one.setProductId(productId);
            one.setMemberId(currentMember.getId());
            this.xmsPmsProductEditService.save(one);

            if (CollectionUtil.isNotEmpty(detail.getSkuStockList())) {
                List<XmsPmsSkuStockEdit> stockEditList = new ArrayList<>();

                detail.getSkuStockList().forEach(e -> {
                    XmsPmsSkuStockEdit temp = new XmsPmsSkuStockEdit();
                    BeanUtil.copyProperties(e, temp);
                    temp.setId(null);
                    temp.setMemberId(currentMember.getId());
                    temp.setProductId(productId);
                    temp.setCreateTime(new Date());
                    stockEditList.add(temp);
                });
                this.xmsPmsSkuStockEditService.saveBatch(stockEditList);
            }*/

            // ????????????sourcingList??????
            XmsSourcingList sourcingList = new XmsSourcingList();


            sourcingList.setUsername(currentMember.getUsername());
            sourcingList.setMemberId(currentMember.getId());
            sourcingList.setCreateTime(new Date());
            sourcingList.setUpdateTime(new Date());
            sourcingList.setProductId(productId);
            sourcingList.setUrl(detail.getProduct().getUrl());

            sourcingList.setUpdateTime(new Date());
            if (StrUtil.isNotBlank(detail.getProduct().getAlbumPics())) {
                sourcingList.setImages(detail.getProduct().getAlbumPics());
            } else {
                sourcingList.setImages(detail.getProduct().getPic());
            }

            sourcingList.setTitle(detail.getProduct().getName());
            sourcingList.setStatus(2);
            sourcingList.setSiteType(list.get(0).getSiteType());
            sourcingList.setRemark("public to sourcing");
            sourcingList.setOrderQuantity(detail.getProduct().getStock());
            sourcingList.setPrice(detail.getProduct().getPriceXj());
            this.xmsSourcingListService.save(sourcingList);
            return CommonResult.success(sourcingList);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("beforeSyncPublicProduct,productId[{}],error:", productId, e);
            return CommonResult.failed("beforeSyncPublicProduct failed");
        }
    }

}

