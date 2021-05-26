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
public class Transactions {

    private long id;
    private long order_id;
    private String kind;
    private String gateway;
    private String status;
    private String message;
    private String created_at;
    private boolean test;
    private String authorization;
    private String location_id;
    private String user_id;
    private long parent_id;
    private String processed_at;
    private String device_id;
    private Receipt receipt;
    private String error_code;
    private String source_name;
    private String amount;
    private String currency;
    private String admin_graphql_api_id;
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

    public void setKind(String kind) {
         this.kind = kind;
     }
     public String getKind() {
         return kind;
     }

    public void setGateway(String gateway) {
         this.gateway = gateway;
     }
     public String getGateway() {
         return gateway;
     }

    public void setStatus(String status) {
         this.status = status;
     }
     public String getStatus() {
         return status;
     }

    public void setMessage(String message) {
         this.message = message;
     }
     public String getMessage() {
         return message;
     }

    public void setCreated_at(String created_at) {
         this.created_at = created_at;
     }
     public String getCreated_at() {
         return created_at;
     }

    public void setTest(boolean test) {
         this.test = test;
     }
     public boolean getTest() {
         return test;
     }

    public void setAuthorization(String authorization) {
         this.authorization = authorization;
     }
     public String getAuthorization() {
         return authorization;
     }

    public void setLocation_id(String location_id) {
         this.location_id = location_id;
     }
     public String getLocation_id() {
         return location_id;
     }

    public void setUser_id(String user_id) {
         this.user_id = user_id;
     }
     public String getUser_id() {
         return user_id;
     }

    public void setParent_id(long parent_id) {
         this.parent_id = parent_id;
     }
     public long getParent_id() {
         return parent_id;
     }

    public void setProcessed_at(String processed_at) {
         this.processed_at = processed_at;
     }
     public String getProcessed_at() {
         return processed_at;
     }

    public void setDevice_id(String device_id) {
         this.device_id = device_id;
     }
     public String getDevice_id() {
         return device_id;
     }

    public void setReceipt(Receipt receipt) {
         this.receipt = receipt;
     }
     public Receipt getReceipt() {
         return receipt;
     }

    public void setError_code(String error_code) {
         this.error_code = error_code;
     }
     public String getError_code() {
         return error_code;
     }

    public void setSource_name(String source_name) {
         this.source_name = source_name;
     }
     public String getSource_name() {
         return source_name;
     }

    public void setAmount(String amount) {
         this.amount = amount;
     }
     public String getAmount() {
         return amount;
     }

    public void setCurrency(String currency) {
         this.currency = currency;
     }
     public String getCurrency() {
         return currency;
     }

    public void setAdmin_graphql_api_id(String admin_graphql_api_id) {
         this.admin_graphql_api_id = admin_graphql_api_id;
     }
     public String getAdmin_graphql_api_id() {
         return admin_graphql_api_id;
     }

}