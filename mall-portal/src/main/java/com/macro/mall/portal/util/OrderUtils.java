package com.macro.mall.portal.util;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.macro.mall.entity.XmsCustomerSkuStock;
import com.macro.mall.mapper.OmsOrderMapper;
import com.macro.mall.mapper.UmsMemberMapper;
import com.macro.mall.model.*;
import com.macro.mall.portal.cache.RedisUtil;
import com.macro.mall.portal.dao.PortalOrderItemDao;
import com.macro.mall.portal.domain.GenerateOrderParam;
import com.macro.mall.portal.domain.GenerateOrderResult;
import com.macro.mall.portal.service.IXmsCustomerSkuStockService;
import com.macro.mall.portal.service.OmsCartItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.macro.mall.portal.util
 * @date:2021-05-06
 */
@Service
public class OrderUtils {

    private static final String SOURCING_ORDER_NO = "sourcing:order";
    private static final String PAYMENT_ID = "payment:id:";
    private static final String PAY_ORDER_NO = "pay:order:";


    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private OmsOrderMapper orderMapper;

    @Autowired
    private PortalOrderItemDao orderItemDao;

    @Autowired
    private OmsCartItemService cartItemService;

    @Autowired
    private IXmsCustomerSkuStockService iXmsCustomerSkuStockService;

    @Autowired
    private UmsMemberMapper memberMapper;



    /**
     * 支付成功后更新订单状态和库存状态
     *
     * @param orderNo
     * @param flag    1:成功 0:失败
     */
    public void paySuccessUpdate(String orderNo, int flag) {
        synchronized (orderNo) {
            OmsOrderExample example = new OmsOrderExample();
            example.createCriteria().andOrderSnEqualTo(orderNo);
            List<OmsOrder> omsOrders = this.orderMapper.selectByExample(example);
            Long memberId = 0L;
            if (CollectionUtil.isNotEmpty(omsOrders)) {
                memberId = omsOrders.get(0).getMemberId();
                OmsOrder tempOrder = new OmsOrder();
                tempOrder.setId(omsOrders.get(0).getId());
                tempOrder.setStatus(flag > 0 ? 5 : -1);
                this.orderMapper.updateByPrimaryKeySelective(tempOrder);
                omsOrders.clear();
            }
            // LP开头的订单，清空购物车数据
            if (flag > 0 && orderNo.indexOf("LP") == 0) {
                List<OmsCartItem> cartItemList = this.cartItemService.list(memberId);
                if (CollectionUtil.isNotEmpty(cartItemList)) {
                    cartItemList = cartItemList.stream().filter(e -> e.getCheckFlag() == 1).collect(Collectors.toList());
                    if (CollectionUtil.isNotEmpty(cartItemList)) {

                        List<Long> collect = cartItemList.stream().mapToLong(OmsCartItem::getId).boxed().collect(Collectors.toList());
                        if (CollectionUtil.isNotEmpty(collect)) {
                            this.cartItemService.delete(memberId, collect);
                        }
                    }
                }
            }
            // SL和LP开头的订单，更新库存
            if (orderNo.indexOf(OrderPrefixEnum.LiveProduct.getName()) == 0 || orderNo.indexOf(OrderPrefixEnum.SourcingList.getName()) == 0) {
                this.iXmsCustomerSkuStockService.updateStateByOrderNo(orderNo, flag > 0 ? 2 : 3);
            }
        }
    }


    /**
     * 生成正常订单
     *
     * @param generateParam
     * @return
     */
    public GenerateOrderResult generateOrder(GenerateOrderParam generateParam) {
        synchronized (generateParam.getCurrentMember().getId()) {
            GenerateOrderResult orderResult = new GenerateOrderResult();
            double productCost = 0;// 商品金额
            double totalAmount; // 总金额
            double payAmount = 0; // PayPal支付
            double balanceAmount = 0; // 余额支付


            OmsOrder order = new OmsOrder();
            order.setOrderSn(generateParam.getOrderNo());
            order.setMemberId(generateParam.getCurrentMember().getId());
            order.setMemberUsername(generateParam.getCurrentMember().getUsername());

            // 地址相关
            order.setReceiverCountry(generateParam.getOrderPayParam().getReceiverCountry());
            order.setReceiverProvince(generateParam.getOrderPayParam().getReceiverProvince());
            order.setReceiverCity(generateParam.getOrderPayParam().getReceiverCity());
            order.setReceiverRegion(generateParam.getOrderPayParam().getReceiverRegion());
            order.setReceiverName(generateParam.getOrderPayParam().getReceiverName());
            order.setReceiverPhone(generateParam.getOrderPayParam().getReceiverPhone());
            order.setReceiverPostCode(generateParam.getOrderPayParam().getReceiverPostCode());


            order.setDeleteStatus(0);
            order.setStatus(0);// 待付款
            order.setCreateTime(new Date());
            order.setFreightAmount(new BigDecimal(generateParam.getTotalFreight()));
            order.setPromotionAmount(BigDecimal.ZERO);

            // 地址相关信息
            Map<String, String> addressMap = new HashMap<>();
            addressMap.put("modeOfTransportation", generateParam.getOrderPayParam().getModeOfTransportation());
            addressMap.put("deliveryTime", generateParam.getOrderPayParam().getDeliveryTime());
            addressMap.put("address", generateParam.getOrderPayParam().getReceiverDetailAddress());
            order.setNote(JSONObject.toJSONString(addressMap));


            List<OmsOrderItem> orderItemList = new ArrayList<>();

            List<XmsCustomerSkuStock> skuStockInsertList = new ArrayList<>();
            // 计算商品中价格
            for (PmsSkuStock pmsSkuStock : generateParam.getPmsSkuStockList()) {
                productCost += pmsSkuStock.getPrice().floatValue() * pmsSkuStock.getStock();
                OmsOrderItem orderItem = new OmsOrderItem();
                orderItem.setOrderSn(generateParam.getOrderNo());
                orderItem.setProductId(pmsSkuStock.getProductId());
                orderItem.setProductPrice(pmsSkuStock.getPrice());
                orderItem.setProductQuantity(pmsSkuStock.getStock());
                orderItem.setProductSkuCode(pmsSkuStock.getSkuCode());
                orderItem.setProductPic(pmsSkuStock.getPic());
                orderItem.setProductAttr(pmsSkuStock.getSpData());
                orderItemList.add(orderItem);

                XmsCustomerSkuStock tempSkuStock = new XmsCustomerSkuStock();
                tempSkuStock.setUsername(generateParam.getCurrentMember().getUsername());
                tempSkuStock.setMemberId(generateParam.getCurrentMember().getId());
                tempSkuStock.setProductId(pmsSkuStock.getProductId());
                tempSkuStock.setPrice(pmsSkuStock.getPrice());
                tempSkuStock.setSpData(pmsSkuStock.getSpData());
                tempSkuStock.setStock(0);
                tempSkuStock.setLockStock(pmsSkuStock.getStock());
                tempSkuStock.setSkuCode(pmsSkuStock.getSkuCode());
                tempSkuStock.setSkuStockId(pmsSkuStock.getId().intValue());
                tempSkuStock.setStatus(1);
                tempSkuStock.setOrderNo(generateParam.getOrderNo());
                skuStockInsertList.add(tempSkuStock);
            }

            totalAmount = productCost + generateParam.getTotalFreight();

            UmsMember umsMember = memberMapper.selectByPrimaryKey(generateParam.getCurrentMember().getId());
            Double tempBalance = umsMember.getBalance();
            // 存在余额则部分余额支付或者余额支付
            if (null != tempBalance && tempBalance > 0) {

                if (tempBalance >= totalAmount) {
                    payAmount = 0;
                    balanceAmount = totalAmount;
                } else {
                    balanceAmount = tempBalance;
                    payAmount = totalAmount - balanceAmount;
                    order.setBalanceAmount(tempBalance);

                }
            } else {
                payAmount = totalAmount;
            }
            order.setTotalAmount(new BigDecimal(productCost));
            order.setPayAmount(new BigDecimal(payAmount));
            order.setBalanceAmount(balanceAmount);

            orderResult.setOrderNo(generateParam.getOrderNo());
            orderResult.setBalanceAmount(balanceAmount);
            orderResult.setPayAmount(payAmount);
            orderResult.setTotalAmount(totalAmount);
            orderResult.setProductCost(productCost);
            orderResult.setTotalFreight(generateParam.getTotalFreight());

            this.orderMapper.insert(order);
            orderItemList.forEach(e-> e.setOrderId(order.getId()));
            this.orderItemDao.insertList(orderItemList);

            // 如果是库存，进行库存处理：每次都是插入库存，方便处理
            if (0 == generateParam.getType()) {
                if (CollectionUtil.isNotEmpty(skuStockInsertList)) {
                    if (payAmount == 0) {
                        // 仅余额支付的话，设置库存状态可用
                        skuStockInsertList.forEach(e -> e.setStatus(2));
                    }
                    this.iXmsCustomerSkuStockService.saveBatch(skuStockInsertList);
                }
            }
            skuStockInsertList.clear();
            orderItemList.clear();

            return orderResult;
        }

    }

    /**
     * 充值的订单数据
     */
    public void generateBalanceOrder(String orderNo, Double totalAmount, Long memberId, String username) {
        OmsOrder order = new OmsOrder();
        order.setOrderSn(orderNo);
        order.setMemberId(memberId);
        order.setMemberUsername(username);
        order.setReceiverName("receiverName");
        order.setReceiverPhone("receiverPhone");
        order.setDeleteStatus(0);
        order.setStatus(0);// 待付款
        order.setCreateTime(new Date());
        order.setFreightAmount(BigDecimal.ZERO);
        order.setPromotionAmount(BigDecimal.ZERO);

        // 地址相关信息
        Map<String, String> addressMap = new HashMap<>();
        addressMap.put("modeOfTransportation", "modeOfTransportation");
        addressMap.put("deliveryTime", "deliveryTime");
        addressMap.put("address", "address");
        order.setTotalAmount(new BigDecimal(totalAmount));
        order.setPayAmount(new BigDecimal(totalAmount));
        this.orderMapper.insert(order);
    }


    /**
     * 获取订单号并且放入Redis中
     *
     * @return
     */
    public String getOrderNoByRedis(String preStr) {
        synchronized (OrderUtils.class) {
            Map<String, Object> objectMap = redisUtil.hmgetObj(SOURCING_ORDER_NO);
            String orderNo = this.getOrderNo(preStr);
            if (objectMap == null || objectMap.size() == 0) {
                objectMap = new HashMap<>();
            } else {
                while (objectMap.containsKey(orderNo)) {
                    orderNo = this.getOrderNo(preStr);
                }
            }
            objectMap.put(orderNo, 1);
            redisUtil.hmsetObj(SOURCING_ORDER_NO, objectMap, RedisUtil.EXPIRATION_TIME_1_DAY);
            return orderNo;
        }
    }


    /**
     * 根据订单号判断是否支付
     *
     * @param orderNo
     * @return true:支付 false:未支付
     */
    public boolean checkPayStatusByOrderNo(String orderNo) {
        OmsOrderExample example = new OmsOrderExample();
        example.createCriteria().andOrderSnEqualTo(orderNo);
        List<OmsOrder> omsOrders = this.orderMapper.selectByExample(example);
        // 订单状态：0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->无效订单；-1->支付失败")
        return !(CollectionUtil.isEmpty(omsOrders) || 0 == omsOrders.get(0).getStatus() || -1 == omsOrders.get(0).getStatus());
    }

    /**
     * 判断订单的paymentId是否支付
     *
     * @param paymentId
     * @return
     */
    public boolean checkIsPay(String paymentId) {
        return this.redisUtil.hmgetObj(PAYMENT_ID).containsKey(paymentId);
    }

    public boolean setOrderIsPay(String paymentId) {
        synchronized (OrderUtils.class) {
            return this.redisUtil.hmsetObj(PAYMENT_ID, paymentId, 1, RedisUtil.EXPIRATION_TIME_1_DAY);
        }
    }


    private String getOrderNo(String preStr) {
        synchronized (OrderUtils.class) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-ddHH:mm:ss");
            LocalDateTime nowDateTime = LocalDateTime.now();
            String dateTimeString = formatter.format(nowDateTime);
            Random r = new Random();
            String subStr = dateTimeString.replace("-", "");
            subStr = subStr.substring(0, subStr.indexOf(":")).trim();
            return preStr + subStr + r.ints(1001, 9999).findFirst().getAsInt();
        }
    }

}
