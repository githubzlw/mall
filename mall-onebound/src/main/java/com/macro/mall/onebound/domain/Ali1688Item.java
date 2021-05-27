package com.macro.mall.onebound.domain;


import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * @author jack.luo
 * @date 2019/11/5
 */
@Data
public class Ali1688Item {

    private String num_iid;

    private String pic_url;

    private String title;

    private String price;

    private String promotion_price;

    private String volume;

    private String post_fee;

    private String sales;

    private String detail_url;

    public int getSalesOfParse(){
        if(StringUtils.isNotEmpty(this.sales)){
            if(StringUtils.contains(this.sales,"万")){
                return NumberUtils.toInt(StringUtils.replace(this.sales, "万", "0000"),1);
            }else if(StringUtils.contains(this.sales,"千")){
                return NumberUtils.toInt(StringUtils.replace(this.sales, "千", "000"),1);
            }else if(StringUtils.contains(this.sales,"百")){
                return NumberUtils.toInt(StringUtils.replace(this.sales, "百", "00"),1);
            }else{
                return NumberUtils.toInt(this.sales);
            }
        }else{
            return 0;
        }
    }

    public static void main(String[] args){
        System.out.println(NumberUtils.toInt("1.1000"));
        System.out.println(NumberUtils.toInt("11000"));
        System.out.println(NumberUtils.toInt("11000万"));
    }

}
