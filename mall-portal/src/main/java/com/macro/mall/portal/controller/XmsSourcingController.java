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
import com.macro.mall.entity.XmsCustomerProduct;
import com.macro.mall.entity.XmsPmsProductEdit;
import com.macro.mall.entity.XmsPmsSkuStockEdit;
import com.macro.mall.entity.XmsSourcingList;
import com.macro.mall.model.PmsSkuStock;
import com.macro.mall.model.UmsMember;
import com.macro.mall.portal.cache.RedisUtil;
import com.macro.mall.portal.config.MicroServiceConfig;
import com.macro.mall.portal.domain.*;
import com.macro.mall.portal.enums.PayFromEnum;
import com.macro.mall.portal.service.*;
import com.macro.mall.portal.enums.OrderPrefixEnum;
import com.macro.mall.portal.util.OrderUtils;
import com.macro.mall.portal.util.PayUtil;
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
@Api(tags = "XmsSourcingController", description = "Sourcing操作相关接口")
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

    @InitBinder
    protected void init(HttpServletRequest request, ServletRequestDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }

    @ApiOperation("sourcingList列表")
    @RequestMapping(value = "/sourcingList", method = RequestMethod.GET)
    public CommonResult sourcingList(XmsSourcingInfoParam sourcingParam) {

        Assert.isTrue(null != sourcingParam, "sourcingParam null");


        try {

            if (null == sourcingParam.getPageNum() || sourcingParam.getPageNum() == 0) {
                sourcingParam.setPageNum(1);
            }
            if (null == sourcingParam.getPageSize() || sourcingParam.getPageSize() == 0) {
                sourcingParam.setPageSize(10);
            }
            sourcingParam.setMemberId(this.umsMemberService.getCurrentMember().getId());
            sourcingParam.setUsername(this.umsMemberService.getCurrentMember().getUsername());
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


    @ApiOperation("sourcingList统计")
    @RequestMapping(value = "/sourcingListStatistics", method = RequestMethod.GET)
    public CommonResult sourcingListStatistics(String url) {

        UmsMember currentMember = this.umsMemberService.getCurrentMember();

        try {


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
                // 状态：0->已接收；1->处理中；2->已处理 4->取消；5->无效数据； -1->删除；
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


    @ApiOperation("sourcingListByUuid列表")
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


    @ApiOperation("sourcingListByUuid统计")
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
                // 状态：0->已接收；1->处理中；2->已处理 4->取消；5->无效数据； -1->删除；
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


    @ApiOperation("SourcingList保存客户编辑的信息")
    @RequestMapping(value = "/saveSourcingProduct", method = RequestMethod.POST)
    public CommonResult saveSourcingProduct(SourcingProductParam sourcingProductParam) {
        Assert.notNull(sourcingProductParam, "sourcingProductParam null");
        Assert.isTrue(null != sourcingProductParam.getProductId() && sourcingProductParam.getProductId() > 0, "productId null");
        Assert.isTrue(null != sourcingProductParam.getSourcingId() && sourcingProductParam.getSourcingId() > 0, "sourcingId null");
        Assert.isTrue(null != sourcingProductParam.getWeight() && sourcingProductParam.getWeight().doubleValue() > 0, "weight null");
        Assert.isTrue(StrUtil.isNotBlank(sourcingProductParam.getSkuList()), "skuList null");

        UmsMember currentMember = this.umsMemberService.getCurrentMember();
        try {
            // 检查数据是否存在
            XmsSourcingList xmsSourcingList = this.xmsSourcingListService.getById(sourcingProductParam.getSourcingId());
            if (null == xmsSourcingList) {
                return CommonResult.validateFailed("No data available");
            }

            List<XmsPmsSkuStockEdit> stockEditList = JSONArray.parseArray(sourcingProductParam.getSkuList(), XmsPmsSkuStockEdit.class);
            if (CollectionUtil.isEmpty(stockEditList)) {
                return CommonResult.validateFailed("No sku available");
            }
            StringBuffer skuSb = new StringBuffer();
            stockEditList.forEach(e-> {
                e.setMemberId(currentMember.getId());
                skuSb.append("," + e.getSkuCode());
            });

            Long productId;
            // 判断是否存在编辑表数据
            QueryWrapper<XmsPmsProductEdit> productEditWrapper = new QueryWrapper<>();
            productEditWrapper.lambda().eq(XmsPmsProductEdit::getProductId, sourcingProductParam.getProductId());

            List<XmsPmsProductEdit> list = this.xmsPmsProductEditService.list(productEditWrapper);

            // 如果存在，则进行更新处理，sku查询是否重复处理
            if (CollectionUtil.isNotEmpty(list)) {
                XmsPmsProductEdit pmsProductEdit = list.get(0);
                pmsProductEdit.setAlbumPics(sourcingProductParam.getAlbumPics());
                productId = pmsProductEdit.getId();
                pmsProductEdit.setName(sourcingProductParam.getName());
                // 处理sku数据
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
                this.xmsPmsProductEditService.updateById(pmsProductEdit);
            } else {
                // 如果不存在，则进行插入处理
                // 插入product数据
                XmsPmsProductEdit pmsProductEdit = new XmsPmsProductEdit();
                BeanUtil.copyProperties(sourcingProductParam, pmsProductEdit);
                pmsProductEdit.setProductId(sourcingProductParam.getProductId());
                pmsProductEdit.setMemberId(currentMember.getId());
                pmsProductEdit.setWeight(sourcingProductParam.getWeight());
                this.xmsPmsProductEditService.save(pmsProductEdit);
                productId = pmsProductEdit.getId();
                // 插入sku数据
                this.xmsPmsSkuStockEditService.saveBatch(stockEditList);
                stockEditList.clear();
            }


            // 调用shopify铺货
            UmsMember byId = this.umsMemberService.getById(currentMember.getId());
            if (StrUtil.isEmpty(byId.getShopifyName())) {
                return CommonResult.validateFailed("Please bind the store first");
            }


            QueryWrapper<XmsCustomerProduct> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(XmsCustomerProduct::getProductId, productId).eq(XmsCustomerProduct::getMemberId, currentMember.getId());
            int count = this.xmsCustomerProductService.count(queryWrapper);
            if(count == 0){
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

            JSONObject jsonObject = this.urlUtil.postURL(microServiceConfig.getShopifyUrl() + "/addProduct", param);

            if (null != jsonObject) {
                return JSONObject.parseObject(jsonObject.toJSONString(), CommonResult.class);
            }
            return CommonResult.failed("addProduct error");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("saveSourcingProduct,sourcingProductParam[{}],error:", sourcingProductParam, e);
            return CommonResult.failed("saveSourcingProduct failed");
        }
    }


    @ApiOperation("SourcingList取消")
    @RequestMapping(value = "/deleteSourcing", method = RequestMethod.POST)
    @ApiImplicitParams({@ApiImplicitParam(name = "sourcingId", value = "sourcing表的ID", required = true, dataType = "Long")})
    public CommonResult deleteSourcing(Long sourcingId) {
        try {
            // 检查数据是否存在
            XmsSourcingList xmsSourcingList = this.xmsSourcingListService.getById(sourcingId);
            if (null == xmsSourcingList) {
                return CommonResult.validateFailed("No data available");
            }

            UpdateWrapper<XmsSourcingList> updateWrapper = new UpdateWrapper<>();
            updateWrapper.lambda().eq(XmsSourcingList::getId, sourcingId).set(XmsSourcingList::getStatus, 4);
            boolean update = this.xmsSourcingListService.update(null, updateWrapper);
            return CommonResult.success(update);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("deleteSourcing,sourcingId[{}],error:", sourcingId, e);
            return CommonResult.failed("deleteSourcing error");
        }
    }


    @ApiOperation("SourcingList批量删除")
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


    @ApiOperation("SourcingList添加到客户产品表")
    @RequestMapping(value = "/addToYouLiveProductList", method = RequestMethod.POST)
    @ApiImplicitParams({@ApiImplicitParam(name = "sourcingId", value = "sourcing表的ID", required = true, dataType = "Long")})
    public CommonResult addToYouLiveProductList(Long sourcingId) {
        try {
            // 检查数据是否存在
            XmsSourcingList xmsSourcingList = this.xmsSourcingListService.getById(sourcingId);
            if (null == xmsSourcingList) {
                return CommonResult.validateFailed("No data available");
            }

            // 检查数据是否插入
            XmsCustomerProduct product = new XmsCustomerProduct();
            product.setMemberId(this.umsMemberService.getCurrentMember().getId());
            product.setUsername(this.umsMemberService.getCurrentMember().getUsername());
            product.setSourcingId(sourcingId);
            product.setProductId(xmsSourcingList.getProductId());
            boolean isCheck = this.xmsSourcingListService.checkHasXmsCustomerProduct(product);

            if (isCheck) {
                return CommonResult.validateFailed("The data already exists");
            }

            // 设置产品信息
            product.setProductId(Long.parseLong(String.valueOf(xmsSourcingList.getProductId())));
            product.setSourceLink(xmsSourcingList.getSourceLink());
            product.setStatus(0);
            product.setSiteType(xmsSourcingList.getSiteType());
            product.setCreateTime(new Date());
            product.setUpdateTime(new Date());
            this.xmsCustomerProductService.save(product);
            // 设置同步成功标识
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


    @ApiOperation("客户在Sourcing商品下单")
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

            // 验证SourcingList
            XmsSourcingInfoParam sourcingParam = new XmsSourcingInfoParam();
            sourcingParam.setUsername(username);
            sourcingParam.setMemberId(memberId);
            sourcingParam.setProductId(sourcingPayParam.getProductId());
            XmsSourcingList xmsSourcingList = this.xmsSourcingListService.querySingleSourcingList(sourcingParam);
            if (null == xmsSourcingList) {
                return CommonResult.failed("No data available");
            }

            // 处理参数的skuCode
            Map<String, Integer> orderNumMap = new HashMap<>();// 放入下单的数据
            List<String> skuCodeList = new ArrayList<>();
            sourcingPayParam.getSkuCodeAndNumList().forEach(e -> {
                String[] arr = e.split(":");
                skuCodeList.add(arr[0]);
                orderNumMap.put(sourcingPayParam.getProductId() + "_" + arr[0], Integer.parseInt(arr[1]));
            });

            // 获取商品的sku数据
            List<Long> productIdList = new ArrayList<>();
            productIdList.add(sourcingPayParam.getProductId());
            List<PmsSkuStock> skuStockList = this.iPmsSkuStockService.getSkuStockByParam(productIdList, skuCodeList);
            if (CollectionUtil.isEmpty(skuStockList)) {
                return CommonResult.failed("No data available");
            }

            List<PmsSkuStock> pmsSkuStockList = BeanCopyUtil.deepListCopy(skuStockList);// 拷贝数据
            // 过滤未包含的skuCode并且赋值参数给的数量
            pmsSkuStockList.forEach(e -> e.setStock(orderNumMap.getOrDefault(e.getProductId() + "_" + e.getSkuCode(), 0)));
            pmsSkuStockList = pmsSkuStockList.stream().filter(e -> e.getStock() > 0).collect(Collectors.toList());

            // 根据运输方式算运费
            double totalFreight = 0;

            // 生成订单和订单详情信息
            String orderNo = this.orderUtils.getOrderNoByRedis(OrderPrefixEnum.PURCHASE_STOCK_ORDER.getCode());
            OrderPayParam orderPayParam = new OrderPayParam();
            BeanUtil.copyProperties(sourcingPayParam, orderPayParam);
            // 生成订单并且计算总价格
            GenerateOrderParam generateOrderParam = GenerateOrderParam.builder().orderNo(orderNo).totalFreight(totalFreight).currentMember(currentMember).pmsSkuStockList(pmsSkuStockList).orderPayParam(orderPayParam).type(0).build();
            GenerateOrderResult orderResult = this.orderUtils.generateOrder(generateOrderParam);

            // 更新客户库存数据
            QueryWrapper<XmsCustomerProduct> productQueryWrapper = new QueryWrapper<>();
            productQueryWrapper.lambda().eq(XmsCustomerProduct::getProductId, sourcingPayParam.getProductId()).eq(XmsCustomerProduct::getSourcingId, sourcingPayParam.getSourcingId());
            List<XmsCustomerProduct> list = this.xmsCustomerProductService.list(productQueryWrapper);
            if(CollectionUtil.isNotEmpty(list)) {
                if (StrUtil.isNotEmpty(list.get(0).getAddress())) {
                    if (!list.get(0).getAddress().contains(sourcingPayParam.getReceiverCountry())) {
                        list.get(0).setAddress(list.get(0).getAddress() + "," + sourcingPayParam.getReceiverCountry() + ",");
                    }
                } else {
                    list.get(0).setAddress(sourcingPayParam.getReceiverCountry() + ",");
                }
                list.get(0).setUpdateTime(new Date());
                this.xmsCustomerProductService.updateById(list.get(0));
            }

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


    @ApiOperation("再次Sourcing")
    @RequestMapping(value = "/sourcingAgain", method = RequestMethod.POST)
    @ApiImplicitParams({@ApiImplicitParam(name = "sourcingId", value = "sourcing表的ID", required = true, dataType = "Long")})
    public CommonResult sourcingAgain(Long sourcingId) {
        UmsMember currentMember = this.umsMemberService.getCurrentMember();
        try {
            // 检查数据是否存在
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


    @ApiOperation("更新Sourcing数据")
    @RequestMapping(value = "/updateSourcingInfo", method = RequestMethod.POST)
    public CommonResult updateSourcingInfo(SiteSourcingParam siteSourcingParam) {

        Assert.notNull(siteSourcingParam, "siteSourcingParam null");

        try {

            UpdateWrapper<XmsSourcingList> updateWrapper = new UpdateWrapper<>();
            boolean update = false;
            //1:Drop Shipping
            if(siteSourcingParam.getChooseType()==1){
                updateWrapper.lambda().eq(XmsSourcingList::getId, siteSourcingParam.getId())
                        .set(XmsSourcingList::getChooseType, siteSourcingParam.getChooseType())
                        .set(XmsSourcingList::getOrderQuantity, siteSourcingParam.getAverageDailyOrder())
                        .set(XmsSourcingList::getRemark, siteSourcingParam.getData());
                update = this.xmsSourcingListService.update(null, updateWrapper);
            }
            //2:Wholesale and Bulk Shipping
            if(siteSourcingParam.getChooseType()==2){
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
            if(siteSourcingParam.getChooseType()==4){
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


    @ApiOperation("公用的商品进行发布前的处理")
    @RequestMapping(value = "/beforeSyncPublicProduct", method = RequestMethod.POST)
    public CommonResult beforeSyncPublicProduct(Long productId) {
        Assert.isTrue(null != productId && productId > 0, "productId null");

        UmsMember currentMember = this.umsMemberService.getCurrentMember();
        try {

            // 检查原始数据
            PmsPortalProductDetail detail = this.pmsPortalProductService.detail(productId);
            if (null == detail) {
                return CommonResult.failed("no this product");
            }

            QueryWrapper<XmsSourcingList> sourcingWrapper = new QueryWrapper<>();
            sourcingWrapper.lambda().eq(XmsSourcingList::getProductId, productId).eq(XmsSourcingList::getMemberId, currentMember.getId());
            XmsSourcingList sourcingOne = this.xmsSourcingListService.getOne(sourcingWrapper);
            if(null != sourcingOne && sourcingOne.getId() > 0){
                // 说明有数据直接返回
                return CommonResult.success(sourcingOne);
            }
            sourcingWrapper = new QueryWrapper<>();


            sourcingWrapper.lambda().eq(XmsSourcingList::getProductId, productId);
            sourcingOne = this.xmsSourcingListService.getOne(sourcingWrapper);
            if(null == sourcingOne){
                return CommonResult.failed("no this sourcing");
            }

            /*// 直接删除sku数据

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

            // 优先插入sourcingList数据
            XmsSourcingList sourcingList = new XmsSourcingList();


            sourcingList.setUsername(currentMember.getUsername());
            sourcingList.setMemberId(currentMember.getId());
            sourcingList.setCreateTime(new Date());
            sourcingList.setUpdateTime(new Date());
            sourcingList.setProductId(productId);
            sourcingList.setUrl(detail.getProduct().getUrl());

            sourcingList.setUpdateTime(new Date());
            sourcingList.setImages(detail.getProduct().getAlbumPics());
            sourcingList.setTitle(detail.getProduct().getName());
            sourcingList.setStatus(2);
            sourcingList.setSiteType(sourcingOne.getSiteType());
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

