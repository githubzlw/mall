package com.macro.mall.portal.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.entity.XmsCustomerProduct;
import com.macro.mall.entity.XmsPmsProductEdit;
import com.macro.mall.entity.XmsPmsSkuStockEdit;
import com.macro.mall.entity.XmsSourcingList;
import com.macro.mall.model.PmsSkuStock;
import com.macro.mall.model.UmsMember;
import com.macro.mall.portal.domain.*;
import com.macro.mall.portal.service.*;
import com.macro.mall.portal.util.BeanCopyUtil;
import com.macro.mall.portal.util.OrderPrefixEnum;
import com.macro.mall.portal.util.OrderUtils;
import com.macro.mall.portal.util.PayUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
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
    private PmsPortalProductService pmsPortalProductService;


    @ApiOperation("sourcingList列表")
    @RequestMapping(value = "/sourcingList", method = RequestMethod.GET)
    public CommonResult sourcingList(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                     @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {

        XmsSourcingInfoParam sourcingParam = new XmsSourcingInfoParam();
        try {

            sourcingParam.setPageNum(pageNum);
            sourcingParam.setPageSize(pageSize);
            sourcingParam.setMemberId(this.umsMemberService.getCurrentMember().getId());
            sourcingParam.setUsername(this.umsMemberService.getCurrentMember().getUsername());
            Page<XmsSourcingList> listPage = this.xmsSourcingListService.list(sourcingParam);
            return CommonResult.success(listPage);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("sourcingList,sourcingParam[{}],error:", sourcingParam, e);
            return CommonResult.failed("query failed");
        }
    }

    @ApiOperation("SourcingList保存客户编辑的信息")
    @RequestMapping(value = "/saveSourcingProduct", method = RequestMethod.POST)
    public CommonResult saveSourcingProduct(SourcingProductParam sourcingProductParam) {
        Assert.notNull(sourcingProductParam, "sourcingProductParam null");
        Assert.isTrue(null != sourcingProductParam.getId() && sourcingProductParam.getId() > 0, "productId null");
        Assert.isTrue(CollectionUtil.isNotEmpty(sourcingProductParam.getSkuList()), "skuList null");

        UmsMember currentMember = this.umsMemberService.getCurrentMember();
        try {
            // 检查数据是否存在
            PmsPortalProductDetail detail = this.pmsPortalProductService.detail(sourcingProductParam.getId());
            if (null == detail) {
                return CommonResult.validateFailed("No data available");
            }

            List<XmsPmsSkuStockEdit> stockEditList = sourcingProductParam.getSkuList();
            if (CollectionUtil.isEmpty(stockEditList)) {
                return CommonResult.validateFailed("No sku available");
            }

            // 判断是否存在编辑表数据
            QueryWrapper<XmsPmsProductEdit> productEditWrapper = new QueryWrapper<>();
            productEditWrapper.lambda().eq(XmsPmsProductEdit::getMemberId, currentMember.getId()).eq(XmsPmsProductEdit::getId, sourcingProductParam.getId());

            int count = this.xmsPmsProductEditService.count(productEditWrapper);

            // 如果存在，则进行更新处理，sku查询是否重复处理
            if (count > 0) {
                // 处理sku数据
                QueryWrapper<XmsPmsSkuStockEdit> skuEditWrapper = new QueryWrapper<>();
                skuEditWrapper.lambda().eq(XmsPmsSkuStockEdit::getProductId, sourcingProductParam.getId());
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
            } else {
                // 如果不存在，则进行插入处理
                // 插入product数据
                XmsPmsProductEdit pmsProductEdit = new XmsPmsProductEdit();
                BeanUtil.copyProperties(sourcingProductParam, pmsProductEdit);
                pmsProductEdit.setMemberId(currentMember.getId());
                this.xmsPmsProductEditService.save(pmsProductEdit);
                // 插入sku数据
                this.xmsPmsSkuStockEditService.saveBatch(stockEditList);
                stockEditList.clear();
            }
            return CommonResult.success(1);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("saveSourcingProduct,sourcingProductParam[{}],error:", sourcingProductParam, e);
            return CommonResult.failed("saveSourcingProduct failed");
        }
    }


    @ApiOperation("SourcingList删除")
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
            updateWrapper.lambda().eq(XmsSourcingList::getId, sourcingId).set(XmsSourcingList::getStatus, -1);
            boolean update = this.xmsSourcingListService.update(null, updateWrapper);
            return CommonResult.success(update);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("deleteSourcing,sourcingId[{}],error:", sourcingId, e);
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
            product.setSourcingId(sourcingId.intValue());
            boolean isCheck = this.xmsSourcingListService.checkHasXmsCustomerProduct(product);

            if (isCheck) {
                return CommonResult.validateFailed("The data already exists");
            }

            // 设置产品信息
            product.setProductId(Long.parseLong(String.valueOf(xmsSourcingList.getProductId())));
            product.setSourceLink(xmsSourcingList.getSourceLink());
            product.setStatus(0);
            product.setCreateTime(new Date());
            product.setUpdateTime(new Date());
            this.xmsCustomerProductService.save(product);
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
            String orderNo = this.orderUtils.getOrderNoByRedis(OrderPrefixEnum.SourcingList.getName());
            OrderPayParam orderPayParam = new OrderPayParam();
            BeanUtil.copyProperties(sourcingPayParam, orderPayParam);
            // 生成订单并且计算总价格
            GenerateOrderParam generateOrderParam = GenerateOrderParam.builder().orderNo(orderNo).totalFreight(totalFreight).currentMember(currentMember).pmsSkuStockList(pmsSkuStockList).orderPayParam(orderPayParam).type(0).build();
            GenerateOrderResult orderResult = this.orderUtils.generateOrder(generateOrderParam);

            skuCodeList.clear();
            productIdList.clear();
            skuStockList.clear();
            pmsSkuStockList.clear();
            orderNumMap.clear();

            return this.payUtil.beforePayAndPay(orderResult, currentMember, request);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("payBySourcingProduct,sourcingPayParam[{}],error:", sourcingPayParam, e);
            return CommonResult.failed("execute failed");
        }
    }

}
