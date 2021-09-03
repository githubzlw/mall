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
public class Refunds {

    private long id;
    private long order_id;
    private String created_at;
    private String note;
    private long user_id;
    private String processed_at;
    private boolean restock;
    private String admin_graphql_api_id;
    private List<Refund_line_items> refund_line_items;
    private List<Transactions> transactions;
    private List<Object> order_adjustments;
    public void setId(long id) {
         this.id = id;
     }
     public long getId() {
         return id;
     }

    public void setOrder_id(long order_id) {
         this.order_id = order_id;
     }
     public long getOrder_id() {
         return order_id;
     }

    public void setCreated_at(String created_at) {
         this.created_at = created_at;
     }
     public String getCreated_at() {
         return created_at;
     }

    public void setNote(String note) {
         this.note = note;
     }
     public String getNote() {
         return note;
     }

    public void setUser_id(long user_id) {
         this.user_id = user_id;
     }
     public long getUser_id() {
         return user_id;
     }

    public void setProcessed_at(String processed_at) {
         this.processed_at = processed_at;
     }
     public String getProcessed_at() {
         return processed_at;
     }

    public void setRestock(boolean restock) {
         this.restock = restock;
     }
     public boolean getRestock() {
         return restock;
     }

    public void setAdmin_graphql_api_id(String admin_graphql_api_id) {
         this.admin_graphql_api_id = admin_graphql_api_id;
     }
     public String getAdmin_graphql_api_id() {
         return admin_graphql_api_id;
     }

    public void setRefund_line_items(List<Refund_line_items> refund_line_items) {
         this.refund_line_items = refund_line_items;
     }
     public List<Refund_line_items> getRefund_line_items() {
         return refund_line_items;
     }

    public void setTransactions(List<Transactions> transactions) {
         this.transactions = transactions;
     }
     public List<Transactions> getTransactions() {
         return transactions;
     }

    public void setOrder_adjustments(List<Object> order_adjustments) {
         this.order_adjustments = order_adjustments;
     }
     public List<Object> getOrder_adjustments() {
         return order_adjustments;
     }

}