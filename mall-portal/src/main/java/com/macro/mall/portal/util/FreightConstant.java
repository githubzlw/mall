package com.macro.mall.portal.util;

import java.math.BigDecimal;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.macro.mall.portal.util
 * @date:2021-04-14
 */
public class FreightConstant {

    /**
     * 美元汇率
     */
    public static final double EXCHANGE_RATE = 6.5;

    public static final double FBA = 10d;
    public static final String Epacket = "9-15";
    public static final String JCEX_ = "5-9";
    public static final String EPACKET_NAME = "ePacket";
    public static final String EMS_NAME = "ems";
    public static final String JCEX_NAME = "JCEX";
    //Description: 发送邮件过期时间
    public static final long SENDEMSILEXPIRATIONTIME = 1000 * 60 * 60 * 24;
    //Description: eub 名字
    public static final String EUB_NAME = "EUB";
    public static final String DHL_NAME = "DHL";
    public static final String FEDEX_NAME = "FEDEX";
    public static final String CHINA_POST = "China post";
    public static final String DDP_NAME = "Bulk Air Freight";
    public static final String CHINA_POST_AIR_MAIL = "Register Air Mail";
    public static final String AIR_NAME = "Air";
    public static final String CIF_NAME = "CIF";
    public static final String IMPORTX_STANDARD = "ImportX Standard";
    public static final String CNE_NAME = "CNE";
    public static final String SHIP_CHINA = "Ship Within China";
    public static final String ECONOMIC = "economic";
    public static final Integer CHINAID = 248;
    //鞋类别ID
    public static final String SHOP_CAT_ID = "1038378";
    //运动鞋类别ID
    public static final String SPORTS_SHOES_CAT_ID = "121876002";
    //户外鞋类别ID
    public static final String OUTDOOR_SHOES_CAT_ID = "121900002";
    //店铺总金额必须大于 15 才不收手续费
    public static final double THISHOPSUMPRICE = 15d;
    //店铺总金额 低于15$需要收取 1.5 手续费
    public static final double PROCESSINGFEE = 1.5d;
    //EUB eub Handling Fee
    public static final BigDecimal EUBHANDLINGFEE = new BigDecimal("2.2");
    //购物车重量低于0.5 则减去50¥ 费用
    public static final BigDecimal MIXWEIGHTDISCOUNT = BigDecimal.valueOf(DoubleUtil.round(50d / EXCHANGE_RATE, 2));

    //Description: 运费利润率
	public static final double PROFITMARGIN = 1.3d;

	// Description : Bulk Air Freight min freight cost
	public static final double MINBULKAIRFREIGHTCOST = 1d;

}
