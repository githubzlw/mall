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
public class Orders {

    private int num_id;
    private long id;
    private String email;
    private String closed_at;
    private String created_at;
    private String updated_at;
    private int number;
    private String note;
    private String token;
    private String gateway;
    private boolean test;
    private String total_price;
    private String subtotal_price;
    private int total_weight;
    private String total_tax;
    private boolean taxes_included;
    private String currency;
    private String financial_status;
    private boolean confirmed;
    private String total_discounts;
    private String total_line_items_price;
    private String cart_token;
    private boolean buyer_accepts_marketing;
    private String name;
    private String referring_site;
    private String landing_site;
    private String cancelled_at;
    private String cancel_reason;
    private String total_price_usd;
    private String checkout_token;
    private String reference;
    private String user_id;
    private String location_id;
    private String source_identifier;
    private String source_url;
    private String processed_at;
    private String device_id;
    private String phone;
    private String customer_locale;
    private String app_id;
    private String browser_ip;
    private String landing_site_ref;
    private int order_number;
    private List<Discount_applications> discount_applications;
    private List<Discount_codes> discount_codes;
    private List<Note_attributes> note_attributes;
    private List<String> payment_gateway_names;
    private String processing_method;
    private long checkout_id;
    private String source_name;
    private String fulfillment_status;
    private List<Tax_lines> tax_lines;
    private String tags;
    private String contact_email;
    private String order_status_url;
    private String presentment_currency;
    private Total_line_items_price_set total_line_items_price_set;
    private Total_discounts_set total_discounts_set;
    private Total_shipping_price_set total_shipping_price_set;
    private Subtotal_price_set subtotal_price_set;
    private Total_price_set total_price_set;
    private Total_tax_set total_tax_set;
    private String admin_graphql_api_id;
    private List<Line_items> line_items;
    private List<Shipping_lines> shipping_lines;
    private Billing_address billing_address;
    private Shipping_address shipping_address;
    private List<Fulfillments> fulfillments;
    private Client_details client_details;
    private List<Refunds> refunds;
    private Payment_details payment_details;
    private Customer customer;

    private String shopify_name;
    private String create_time;
    private String update_time;

    public void setId(long id) {
         this.id = id;
     }
     public long getId() {
         return id;
     }

    public void setEmail(String email) {
         this.email = email;
     }
     public String getEmail() {
         return email;
     }

    public void setClosed_at(String closed_at) {
         this.closed_at = closed_at;
     }
     public String getClosed_at() {
         return closed_at;
     }

    public void setCreated_at(String created_at) {
         this.created_at = created_at;
     }
     public String getCreated_at() {
         return created_at;
     }

    public void setUpdated_at(String updated_at) {
         this.updated_at = updated_at;
     }
     public String getUpdated_at() {
         return updated_at;
     }

    public void setNumber(int number) {
         this.number = number;
     }
     public int getNumber() {
         return number;
     }

    public void setNote(String note) {
         this.note = note;
     }
     public String getNote() {
         return note;
     }

    public void setToken(String token) {
         this.token = token;
     }
     public String getToken() {
         return token;
     }

    public void setGateway(String gateway) {
         this.gateway = gateway;
     }
     public String getGateway() {
         return gateway;
     }

    public void setTest(boolean test) {
         this.test = test;
     }
     public boolean getTest() {
         return test;
     }

    public void setTotal_price(String total_price) {
         this.total_price = total_price;
     }
     public String getTotal_price() {
         return total_price;
     }

    public void setSubtotal_price(String subtotal_price) {
         this.subtotal_price = subtotal_price;
     }
     public String getSubtotal_price() {
         return subtotal_price;
     }

    public void setTotal_weight(int total_weight) {
         this.total_weight = total_weight;
     }
     public int getTotal_weight() {
         return total_weight;
     }

    public void setTotal_tax(String total_tax) {
         this.total_tax = total_tax;
     }
     public String getTotal_tax() {
         return total_tax;
     }

    public void setTaxes_included(boolean taxes_included) {
         this.taxes_included = taxes_included;
     }
     public boolean getTaxes_included() {
         return taxes_included;
     }

    public void setCurrency(String currency) {
         this.currency = currency;
     }
     public String getCurrency() {
         return currency;
     }

    public void setFinancial_status(String financial_status) {
         this.financial_status = financial_status;
     }
     public String getFinancial_status() {
         return financial_status;
     }

    public void setConfirmed(boolean confirmed) {
         this.confirmed = confirmed;
     }
     public boolean getConfirmed() {
         return confirmed;
     }

    public void setTotal_discounts(String total_discounts) {
         this.total_discounts = total_discounts;
     }
     public String getTotal_discounts() {
         return total_discounts;
     }

    public void setTotal_line_items_price(String total_line_items_price) {
         this.total_line_items_price = total_line_items_price;
     }
     public String getTotal_line_items_price() {
         return total_line_items_price;
     }

    public void setCart_token(String cart_token) {
         this.cart_token = cart_token;
     }
     public String getCart_token() {
         return cart_token;
     }

    public void setBuyer_accepts_marketing(boolean buyer_accepts_marketing) {
         this.buyer_accepts_marketing = buyer_accepts_marketing;
     }
     public boolean getBuyer_accepts_marketing() {
         return buyer_accepts_marketing;
     }

    public void setName(String name) {
         this.name = name;
     }
     public String getName() {
         return name;
     }

    public void setReferring_site(String referring_site) {
         this.referring_site = referring_site;
     }
     public String getReferring_site() {
         return referring_site;
     }

    public void setLanding_site(String landing_site) {
         this.landing_site = landing_site;
     }
     public String getLanding_site() {
         return landing_site;
     }

    public void setCancelled_at(String cancelled_at) {
         this.cancelled_at = cancelled_at;
     }
     public String getCancelled_at() {
         return cancelled_at;
     }

    public void setCancel_reason(String cancel_reason) {
         this.cancel_reason = cancel_reason;
     }
     public String getCancel_reason() {
         return cancel_reason;
     }

    public void setTotal_price_usd(String total_price_usd) {
         this.total_price_usd = total_price_usd;
     }
     public String getTotal_price_usd() {
         return total_price_usd;
     }

    public void setCheckout_token(String checkout_token) {
         this.checkout_token = checkout_token;
     }
     public String getCheckout_token() {
         return checkout_token;
     }

    public void setReference(String reference) {
         this.reference = reference;
     }
     public String getReference() {
         return reference;
     }

    public void setUser_id(String user_id) {
         this.user_id = user_id;
     }
     public String getUser_id() {
         return user_id;
     }

    public void setLocation_id(String location_id) {
         this.location_id = location_id;
     }
     public String getLocation_id() {
         return location_id;
     }

    public void setSource_identifier(String source_identifier) {
         this.source_identifier = source_identifier;
     }
     public String getSource_identifier() {
         return source_identifier;
     }

    public void setSource_url(String source_url) {
         this.source_url = source_url;
     }
     public String getSource_url() {
         return source_url;
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

    public void setPhone(String phone) {
         this.phone = phone;
     }
     public String getPhone() {
         return phone;
     }

    public void setCustomer_locale(String customer_locale) {
         this.customer_locale = customer_locale;
     }
     public String getCustomer_locale() {
         return customer_locale;
     }

    public void setApp_id(String app_id) {
         this.app_id = app_id;
     }
     public String getApp_id() {
         return app_id;
     }

    public void setBrowser_ip(String browser_ip) {
         this.browser_ip = browser_ip;
     }
     public String getBrowser_ip() {
         return browser_ip;
     }

    public void setLanding_site_ref(String landing_site_ref) {
         this.landing_site_ref = landing_site_ref;
     }
     public String getLanding_site_ref() {
         return landing_site_ref;
     }

    public void setOrder_number(int order_number) {
         this.order_number = order_number;
     }
     public int getOrder_number() {
         return order_number;
     }

    public void setDiscount_applications(List<Discount_applications> discount_applications) {
         this.discount_applications = discount_applications;
     }
     public List<Discount_applications> getDiscount_applications() {
         return discount_applications;
     }

    public void setDiscount_codes(List<Discount_codes> discount_codes) {
         this.discount_codes = discount_codes;
     }
     public List<Discount_codes> getDiscount_codes() {
         return discount_codes;
     }

    public void setNote_attributes(List<Note_attributes> note_attributes) {
         this.note_attributes = note_attributes;
     }
     public List<Note_attributes> getNote_attributes() {
         return note_attributes;
     }

    public void setPayment_gateway_names(List<String> payment_gateway_names) {
         this.payment_gateway_names = payment_gateway_names;
     }
     public List<String> getPayment_gateway_names() {
         return payment_gateway_names;
     }

    public void setProcessing_method(String processing_method) {
         this.processing_method = processing_method;
     }
     public String getProcessing_method() {
         return processing_method;
     }

    public void setCheckout_id(long checkout_id) {
         this.checkout_id = checkout_id;
     }
     public long getCheckout_id() {
         return checkout_id;
     }

    public void setSource_name(String source_name) {
         this.source_name = source_name;
     }
     public String getSource_name() {
         return source_name;
     }

    public void setFulfillment_status(String fulfillment_status) {
         this.fulfillment_status = fulfillment_status;
     }
     public String getFulfillment_status() {
         return fulfillment_status;
     }

    public void setTax_lines(List<Tax_lines> tax_lines) {
         this.tax_lines = tax_lines;
     }
     public List<Tax_lines> getTax_lines() {
         return tax_lines;
     }

    public void setTags(String tags) {
         this.tags = tags;
     }
     public String getTags() {
         return tags;
     }

    public void setContact_email(String contact_email) {
         this.contact_email = contact_email;
     }
     public String getContact_email() {
         return contact_email;
     }

    public void setOrder_status_url(String order_status_url) {
         this.order_status_url = order_status_url;
     }
     public String getOrder_status_url() {
         return order_status_url;
     }

    public void setPresentment_currency(String presentment_currency) {
         this.presentment_currency = presentment_currency;
     }
     public String getPresentment_currency() {
         return presentment_currency;
     }

    public void setTotal_line_items_price_set(Total_line_items_price_set total_line_items_price_set) {
         this.total_line_items_price_set = total_line_items_price_set;
     }
     public Total_line_items_price_set getTotal_line_items_price_set() {
         return total_line_items_price_set;
     }

    public void setTotal_discounts_set(Total_discounts_set total_discounts_set) {
         this.total_discounts_set = total_discounts_set;
     }
     public Total_discounts_set getTotal_discounts_set() {
         return total_discounts_set;
     }

    public void setTotal_shipping_price_set(Total_shipping_price_set total_shipping_price_set) {
         this.total_shipping_price_set = total_shipping_price_set;
     }
     public Total_shipping_price_set getTotal_shipping_price_set() {
         return total_shipping_price_set;
     }

    public void setSubtotal_price_set(Subtotal_price_set subtotal_price_set) {
         this.subtotal_price_set = subtotal_price_set;
     }
     public Subtotal_price_set getSubtotal_price_set() {
         return subtotal_price_set;
     }

    public void setTotal_price_set(Total_price_set total_price_set) {
         this.total_price_set = total_price_set;
     }
     public Total_price_set getTotal_price_set() {
         return total_price_set;
     }

    public void setTotal_tax_set(Total_tax_set total_tax_set) {
         this.total_tax_set = total_tax_set;
     }
     public Total_tax_set getTotal_tax_set() {
         return total_tax_set;
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

    public void setShipping_lines(List<Shipping_lines> shipping_lines) {
         this.shipping_lines = shipping_lines;
     }
     public List<Shipping_lines> getShipping_lines() {
         return shipping_lines;
     }

    public void setBilling_address(Billing_address billing_address) {
         this.billing_address = billing_address;
     }
     public Billing_address getBilling_address() {
         return billing_address;
     }

    public void setShipping_address(Shipping_address shipping_address) {
         this.shipping_address = shipping_address;
     }
     public Shipping_address getShipping_address() {
         return shipping_address;
     }

    public void setFulfillments(List<Fulfillments> fulfillments) {
         this.fulfillments = fulfillments;
     }
     public List<Fulfillments> getFulfillments() {
         return fulfillments;
     }

    public void setClient_details(Client_details client_details) {
         this.client_details = client_details;
     }
     public Client_details getClient_details() {
         return client_details;
     }

    public void setRefunds(List<Refunds> refunds) {
         this.refunds = refunds;
     }
     public List<Refunds> getRefunds() {
         return refunds;
     }

    public void setPayment_details(Payment_details payment_details) {
         this.payment_details = payment_details;
     }
     public Payment_details getPayment_details() {
         return payment_details;
     }

    public void setCustomer(Customer customer) {
         this.customer = customer;
     }
     public Customer getCustomer() {
         return customer;
     }

}