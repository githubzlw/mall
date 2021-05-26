/**
  * Copyright 2019 bejson.com 
  */
package com.macro.mall.shopify.pojo.orders;

/**
 * Auto-generated: 2019-04-30 15:43:48
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class Tax_lines {

    private String title;
    private String price;
    private double rate;
    private Price_set price_set;
    public void setTitle(String title) {
         this.title = title;
     }
     public String getTitle() {
         return title;
     }

    public void setPrice(String price) {
         this.price = price;
     }
     public String getPrice() {
         return price;
     }

    public void setRate(double rate) {
         this.rate = rate;
     }
     public double getRate() {
         return rate;
     }

    public void setPrice_set(Price_set price_set) {
         this.price_set = price_set;
     }
     public Price_set getPrice_set() {
         return price_set;
     }

}