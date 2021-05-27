package com.macro.mall.onebound.util;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.importexpress.ali1688.util
 * @date:2020/4/27
 */
public class DataDealUtil {

    private static final double EXCHANGE_RATE = 6.5;
    private static final double RATE_1688_TAOBAO_PRICE = 1.15;

    private static final String PRICE_PRE = "US $";


    public static String dealAliPriceAndChange(String sourcePrice) {
        // "US $15.22 - 27.06"
        if(StringUtils.isBlank(sourcePrice)){
            return sourcePrice;
        }
        String tempPrice = sourcePrice;
        if (tempPrice.contains(PRICE_PRE)) {
            tempPrice = tempPrice.replace(PRICE_PRE, "");
            if (tempPrice.contains("-")) {
                String[] priceList = tempPrice.split("-");
                tempPrice = PRICE_PRE + changeAliPrice(priceList[0]) + " - " + changeAliPrice(priceList[1]);
            } else {
                tempPrice = PRICE_PRE + changeAliPrice(tempPrice);
            }
        } else {
            if (tempPrice.contains("-")) {
                String[] priceList = tempPrice.split("-");
                tempPrice = changeAliPrice(priceList[0]) + " - " + changeAliPrice(priceList[1]);
            } else {
                tempPrice = changeAliPrice(tempPrice);
            }
        }
        return tempPrice;
    }

    public static String changeAliPrice(String price) {
        double tempPrice = Double.parseDouble(price.trim());
        if (tempPrice > 0D) {
            if(tempPrice < 0.1){
                tempPrice = 0.2D;
            } else if (tempPrice < 10D) {
                tempPrice = tempPrice * 0.9;
            } else if (tempPrice < 30D) {
                tempPrice = tempPrice * 0.9;
            } else if (tempPrice < 100D) {
                tempPrice = tempPrice * 0.95;
            }
        }
        return new BigDecimal(tempPrice).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
    }


    public static String dealTaoBaoPriceAndChange(String sourcePrice) {
        // "US $15.22 - 27.06"
        if(StringUtils.isBlank(sourcePrice)){
            return sourcePrice;
        }
        String tempPrice = sourcePrice;
        if (tempPrice.contains(PRICE_PRE)) {
            tempPrice = tempPrice.replace(PRICE_PRE, "");
            if (tempPrice.contains("-")) {
                String[] priceList = tempPrice.split("-");
                tempPrice = PRICE_PRE + changeTaoBaoPrice(priceList[0]) + " - " + changeTaoBaoPrice(priceList[1]);
            } else {
                tempPrice = PRICE_PRE + changeTaoBaoPrice(tempPrice);
            }
        } else {
            if (tempPrice.contains("-")) {
                String[] priceList = tempPrice.split("-");
                tempPrice = changeTaoBaoPrice(priceList[0]) + " - " + changeTaoBaoPrice(priceList[1]);
            } else {
                tempPrice = changeTaoBaoPrice(tempPrice);
            }
        }
        return tempPrice;
    }

    public static String changeTaoBaoPrice(String tempPrice) {
        double changePrice = (Double.parseDouble(tempPrice)
                * RATE_1688_TAOBAO_PRICE) / EXCHANGE_RATE;
        if(changePrice < 0.1D){
            changePrice = 0.2D;
        }
        return new BigDecimal(changePrice).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
    }
}
