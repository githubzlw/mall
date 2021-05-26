/**
  * Copyright 2019 bejson.com 
  */
package com.macro.mall.shopify.pojo.orders;
import java.util.List;

/**
 * Auto-generated: 2019-04-30 15:43:48
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class Line_item {

    private long id;
    private long variant_id;
    private String title;
    private int quantity;
    private String sku;
    private String variant_title;
    private String vendor;
    private String fulfillment_service;
    private long product_id;
    private boolean requires_shipping;
    private boolean taxable;
    private boolean gift_card;
    private String name;
    private String variant_inventory_management;
    private List<String> properties;
    private boolean product_exists;
    private int fulfillable_quantity;
    private int grams;
    private String price;
    private String total_discount;
    private String fulfillment_status;
    private Price_set price_set;
    private Total_discount_set total_discount_set;
    private List<String> discount_allocations;
    private String admin_graphql_api_id;
    private List<Tax_lines> tax_lines;
    public void setId(long id) {
         this.id = id;
     }
     public long getId() {
         return id;
     }

    public void setVariant_id(long variant_id) {
         this.variant_id = variant_id;
     }
     public long getVariant_id() {
         return variant_id;
     }

    public void setTitle(String title) {
         this.title = title;
     }
     public String getTitle() {
         return title;
     }

    public void setQuantity(int quantity) {
         this.quantity = quantity;
     }
     public int getQuantity() {
         return quantity;
     }

    public void setSku(String sku) {
         this.sku = sku;
     }
     public String getSku() {
         return sku;
     }

    public void setVariant_title(String variant_title) {
         this.variant_title = variant_title;
     }
     public String getVariant_title() {
         return variant_title;
     }

    public void setVendor(String vendor) {
         this.vendor = vendor;
     }
     public String getVendor() {
         return vendor;
     }

    public void setFulfillment_service(String fulfillment_service) {
         this.fulfillment_service = fulfillment_service;
     }
     public String getFulfillment_service() {
         return fulfillment_service;
     }

    public void setProduct_id(long product_id) {
         this.product_id = product_id;
     }
     public long getProduct_id() {
         return product_id;
     }

    public void setRequires_shipping(boolean requires_shipping) {
         this.requires_shipping = requires_shipping;
     }
     public boolean getRequires_shipping() {
         return requires_shipping;
     }

    public void setTaxable(boolean taxable) {
         this.taxable = taxable;
     }
     public boolean getTaxable() {
         return taxable;
     }

    public void setGift_card(boolean gift_card) {
         this.gift_card = gift_card;
     }
     public boolean getGift_card() {
         return gift_card;
     }

    public void setName(String name) {
         this.name = name;
     }
     public String getName() {
         return name;
     }

    public void setVariant_inventory_management(String variant_inventory_management) {
         this.variant_inventory_management = variant_inventory_management;
     }
     public String getVariant_inventory_management() {
         return variant_inventory_management;
     }

    public void setProperties(List<String> properties) {
         this.properties = properties;
     }
     public List<String> getProperties() {
         return properties;
     }

    public void setProduct_exists(boolean product_exists) {
         this.product_exists = product_exists;
     }
     public boolean getProduct_exists() {
         return product_exists;
     }

    public void setFulfillable_quantity(int fulfillable_quantity) {
         this.fulfillable_quantity = fulfillable_quantity;
     }
     public int getFulfillable_quantity() {
         return fulfillable_quantity;
     }

    public void setGrams(int grams) {
         this.grams = grams;
     }
     public int getGrams() {
         return grams;
     }

    public void setPrice(String price) {
         this.price = price;
     }
     public String getPrice() {
         return price;
     }

    public void setTotal_discount(String total_discount) {
         this.total_discount = total_discount;
     }
     public String getTotal_discount() {
         return total_discount;
     }

    public void setFulfillment_status(String fulfillment_status) {
         this.fulfillment_status = fulfillment_status;
     }
     public String getFulfillment_status() {
         return fulfillment_status;
     }

    public void setPrice_set(Price_set price_set) {
         this.price_set = price_set;
     }
     public Price_set getPrice_set() {
         return price_set;
     }

    public void setTotal_discount_set(Total_discount_set total_discount_set) {
         this.total_discount_set = total_discount_set;
     }
     public Total_discount_set getTotal_discount_set() {
         return total_discount_set;
     }

    public void setDiscount_allocations(List<String> discount_allocations) {
         this.discount_allocations = discount_allocations;
     }
     public List<String> getDiscount_allocations() {
         return discount_allocations;
     }

    public void setAdmin_graphql_api_id(String admin_graphql_api_id) {
         this.admin_graphql_api_id = admin_graphql_api_id;
     }
     public String getAdmin_graphql_api_id() {
         return admin_graphql_api_id;
     }

    public void setTax_lines(List<Tax_lines> tax_lines) {
         this.tax_lines = tax_lines;
     }
     public List<Tax_lines> getTax_lines() {
         return tax_lines;
     }

}