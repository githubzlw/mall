/**
  * Copyright 2019 bejson.com 
  */
package com.macro.mall.shopify.pojo.orders;
import lombok.Data;

import java.util.List;

/**
 * Auto-generated: 2019-04-30 15:43:48
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Data
public class Shipping_lines {

    private long id;
    private String title;
    private String price;
    private String code;
    private String source;
    private String phone;
    private String requested_fulfillment_service_id;
    private String delivery_category;
    private String carrier_identifier;
    private String discounted_price;
    private Price_set price_set;
    private Discounted_price_set discounted_price_set;
    private List<String> discount_allocations;
    private List<String> tax_lines;
    public void setId(long id) {
         this.id = id;
     }
     public long getId() {
         return id;
     }

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

    public void setCode(String code) {
         this.code = code;
     }
     public String getCode() {
         return code;
     }

    public void setSource(String source) {
         this.source = source;
     }
     public String getSource() {
         return source;
     }

    public void setPhone(String phone) {
         this.phone = phone;
     }
     public String getPhone() {
         return phone;
     }

    public void setRequested_fulfillment_service_id(String requested_fulfillment_service_id) {
         this.requested_fulfillment_service_id = requested_fulfillment_service_id;
     }
     public String getRequested_fulfillment_service_id() {
         return requested_fulfillment_service_id;
     }

    public void setDelivery_category(String delivery_category) {
         this.delivery_category = delivery_category;
     }
     public String getDelivery_category() {
         return delivery_category;
     }

    public void setCarrier_identifier(String carrier_identifier) {
         this.carrier_identifier = carrier_identifier;
     }
     public String getCarrier_identifier() {
         return carrier_identifier;
     }

    public void setDiscounted_price(String discounted_price) {
         this.discounted_price = discounted_price;
     }
     public String getDiscounted_price() {
         return discounted_price;
     }

    public void setPrice_set(Price_set price_set) {
         this.price_set = price_set;
     }
     public Price_set getPrice_set() {
         return price_set;
     }

    public void setDiscounted_price_set(Discounted_price_set discounted_price_set) {
         this.discounted_price_set = discounted_price_set;
     }
     public Discounted_price_set getDiscounted_price_set() {
         return discounted_price_set;
     }

    public void setDiscount_allocations(List<String> discount_allocations) {
         this.discount_allocations = discount_allocations;
     }
     public List<String> getDiscount_allocations() {
         return discount_allocations;
     }

    public void setTax_lines(List<String> tax_lines) {
         this.tax_lines = tax_lines;
     }
     public List<String> getTax_lines() {
         return tax_lines;
     }

}