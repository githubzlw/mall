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
public class Refund_line_items {

    private long id;
    private int quantity;
    private long line_item_id;
    private long location_id;
    private String restock_type;
    private int subtotal;
    private double total_tax;
    private Subtotal_set subtotal_set;
    private Total_tax_set total_tax_set;
    private Line_item line_item;
    public void setId(long id) {
         this.id = id;
     }
     public long getId() {
         return id;
     }

    public void setQuantity(int quantity) {
         this.quantity = quantity;
     }
     public int getQuantity() {
         return quantity;
     }

    public void setLine_item_id(long line_item_id) {
         this.line_item_id = line_item_id;
     }
     public long getLine_item_id() {
         return line_item_id;
     }

    public void setLocation_id(long location_id) {
         this.location_id = location_id;
     }
     public long getLocation_id() {
         return location_id;
     }

    public void setRestock_type(String restock_type) {
         this.restock_type = restock_type;
     }
     public String getRestock_type() {
         return restock_type;
     }

    public void setSubtotal(int subtotal) {
         this.subtotal = subtotal;
     }
     public int getSubtotal() {
         return subtotal;
     }

    public void setTotal_tax(double total_tax) {
         this.total_tax = total_tax;
     }
     public double getTotal_tax() {
         return total_tax;
     }

    public void setSubtotal_set(Subtotal_set subtotal_set) {
         this.subtotal_set = subtotal_set;
     }
     public Subtotal_set getSubtotal_set() {
         return subtotal_set;
     }

    public void setTotal_tax_set(Total_tax_set total_tax_set) {
         this.total_tax_set = total_tax_set;
     }
     public Total_tax_set getTotal_tax_set() {
         return total_tax_set;
     }

    public void setLine_item(Line_item line_item) {
         this.line_item = line_item;
     }
     public Line_item getLine_item() {
         return line_item;
     }

}