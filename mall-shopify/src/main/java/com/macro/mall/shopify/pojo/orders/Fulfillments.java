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
public class Fulfillments {

    private long id;
    private long order_id;
    private String status;
    private String created_at;
    private String service;
    private String updated_at;
    private String tracking_company;
    private String shipment_status;
    private long location_id;
    private String tracking_number;
    private List<String> tracking_numbers;
    private String tracking_url;
    private List<String> tracking_urls;
    private Receipt receipt;
    private String name;
    private String admin_graphql_api_id;
    private List<Line_items> line_items;
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

    public void setStatus(String status) {
         this.status = status;
     }
     public String getStatus() {
         return status;
     }

    public void setCreated_at(String created_at) {
         this.created_at = created_at;
     }
     public String getCreated_at() {
         return created_at;
     }

    public void setService(String service) {
         this.service = service;
     }
     public String getService() {
         return service;
     }

    public void setUpdated_at(String updated_at) {
         this.updated_at = updated_at;
     }
     public String getUpdated_at() {
         return updated_at;
     }

    public void setTracking_company(String tracking_company) {
         this.tracking_company = tracking_company;
     }
     public String getTracking_company() {
         return tracking_company;
     }

    public void setShipment_status(String shipment_status) {
         this.shipment_status = shipment_status;
     }
     public String getShipment_status() {
         return shipment_status;
     }

    public void setLocation_id(long location_id) {
         this.location_id = location_id;
     }
     public long getLocation_id() {
         return location_id;
     }

    public void setTracking_number(String tracking_number) {
         this.tracking_number = tracking_number;
     }
     public String getTracking_number() {
         return tracking_number;
     }

    public void setTracking_numbers(List<String> tracking_numbers) {
         this.tracking_numbers = tracking_numbers;
     }
     public List<String> getTracking_numbers() {
         return tracking_numbers;
     }

    public void setTracking_url(String tracking_url) {
         this.tracking_url = tracking_url;
     }
     public String getTracking_url() {
         return tracking_url;
     }

    public void setTracking_urls(List<String> tracking_urls) {
         this.tracking_urls = tracking_urls;
     }
     public List<String> getTracking_urls() {
         return tracking_urls;
     }

    public void setReceipt(Receipt receipt) {
         this.receipt = receipt;
     }
     public Receipt getReceipt() {
         return receipt;
     }

    public void setName(String name) {
         this.name = name;
     }
     public String getName() {
         return name;
     }

    public void setAdmin_graphql_api_id(String admin_graphql_api_id) {
         this.admin_graphql_api_id = admin_graphql_api_id;
     }
     public String getAdmin_graphql_api_id() {
         return admin_graphql_api_id;
     }

    public void setLine_items(List<Line_items> line_items) {
         this.line_items = line_items;
     }
     public List<Line_items> getLine_items() {
         return line_items;
     }

}