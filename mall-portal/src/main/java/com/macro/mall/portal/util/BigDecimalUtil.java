package com.macro.mall.portal.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * 数值精度截取工具类
 */
public class BigDecimalUtil {

    private static DecimalFormat priceFormat = new DecimalFormat("#0.00");
    /**
     * 截取float类型的数据
     *
     * @param floatVal    : 原值
     * @param truncateNum : 截取位数
     * @return
     */
    public static float truncateFloat(float floatVal, int truncateNum) {
        float resultVal = floatVal;
        BigDecimal bigDecimal = new BigDecimal(floatVal);
        if (truncateNum > 0) {

            resultVal = bigDecimal.setScale(truncateNum, BigDecimal.ROUND_HALF_UP).floatValue();
        } else {
            resultVal = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
        }
        return resultVal;
    }


    /**
     * @param doubleVal   : 原值
     * @param truncateNum : 截取位数
     * @return
     */
    public static double truncateDouble(double doubleVal, int truncateNum) {
        double resultVal = 0;
        BigDecimal bigDecimal = new BigDecimal(doubleVal);
        if (truncateNum > 0) {
            resultVal = bigDecimal.setScale(truncateNum, BigDecimal.ROUND_HALF_UP).doubleValue();
        } else {
            resultVal = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        }
        return resultVal;
    }

    /**
     * @param doubleVal   : 原值
     * @param truncateNum : 截取位数
     * @return
     */
    public static String truncateDoubleToString(double doubleVal, int truncateNum) {
        return String.valueOf(truncateDouble(doubleVal, truncateNum));
    }

    public static void main(String[] args) {
        BigDecimal bigDecimal = new BigDecimal(1500.189);
        System.err.println(bigDecimal.subtract(new BigDecimal(20.166)));
    }

    public static BigDecimal mul(BigDecimal value1,BigDecimal value2,int pointLenth){

        return  value1.multiply(value2).setScale(pointLenth,BigDecimal.ROUND_HALF_UP);
    }
    /**
     * @param doubleVal   : 原值
     * @param truncateNum : 截取位数
     * @return
     */
    public static double truncateDoubleFomart(double doubleVal, int truncateNum) {
        double resultVal = 0;
        BigDecimal bigDecimal = new BigDecimal(doubleVal);
        if (truncateNum > 0) {
            resultVal = bigDecimal.setScale(truncateNum, BigDecimal.ROUND_HALF_UP).doubleValue();
        } else {
            resultVal = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        }
        return Double.parseDouble(priceFormat.format(resultVal));
    }

}
