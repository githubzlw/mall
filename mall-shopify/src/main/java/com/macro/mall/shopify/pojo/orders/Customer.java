package com.macro.mall.shopify.pojo.orders;

import lombok.Data;

/**
 * Auto-generated: 2019-04-30 15:43:48
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Data
public class Customer {

    private long id;
    private String email;
    private boolean accepts_marketing;
    private String created_at;
    private String updated_at;
    private String first_name;
    private String last_name;
    private int orders_count;
    private String state;
    private String total_spent;
    private long last_order_id;
    private String note;
    private boolean verified_email;
    private String multipass_identifier;
    private boolean tax_exempt;
    private String phone;
    private String tags;
    private String last_order_name;
    private String currency;
    private String accepts_marketing_updated_at;
    private String marketing_opt_in_level;
    private String admin_graphql_api_id;
    private Default_address default_address;
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

    public void setAccepts_marketing(boolean accepts_marketing) {
         this.accepts_marketing = accepts_marketing;
     }
     public boolean getAccepts_marketing() {
         return accepts_marketing;
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

    public void setFirst_name(String first_name) {
         this.first_name = first_name;
     }
     public String getFirst_name() {
         return first_name;
     }

    public void setLast_name(String last_name) {
         this.last_name = last_name;
     }
     public String getLast_name() {
         return last_name;
     }

    public void setOrders_count(int orders_count) {
         this.orders_count = orders_count;
     }
     public int getOrders_count() {
         return orders_count;
     }

    public void setState(String state) {
         this.state = state;
     }
     public String getState() {
         return state;
     }

    public void setTotal_spent(String total_spent) {
         this.total_spent = total_spent;
     }
     public String getTotal_spent() {
         return total_spent;
     }

    public void setLast_order_id(long last_order_id) {
         this.last_order_id = last_order_id;
     }
     public long getLast_order_id() {
         return last_order_id;
     }

    public void setNote(String note) {
         this.note = note;
     }
     public String getNote() {
         return note;
     }

    public void setVerified_email(boolean verified_email) {
         this.verified_email = verified_email;
     }
     public boolean getVerified_email() {
         return verified_email;
     }

    public void setMultipass_identifier(String multipass_identifier) {
         this.multipass_identifier = multipass_identifier;
     }
     public String getMultipass_identifier() {
         return multipass_identifier;
     }

    public void setTax_exempt(boolean tax_exempt) {
         this.tax_exempt = tax_exempt;
     }
     public boolean getTax_exempt() {
         return tax_exempt;
     }

    public void setPhone(String phone) {
         this.phone = phone;
     }
     public String getPhone() {
         return phone;
     }

    public void setTags(String tags) {
         this.tags = tags;
     }
     public String getTags() {
         return tags;
     }

    public void setLast_order_name(String last_order_name) {
         this.last_order_name = last_order_name;
     }
     public String getLast_order_name() {
         return last_order_name;
     }

    public void setCurrency(String currency) {
         this.currency = currency;
     }
     public String getCurrency() {
         return currency;
     }

    public void setAccepts_marketing_updated_at(String accepts_marketing_updated_at) {
         this.accepts_marketing_updated_at = accepts_marketing_updated_at;
     }
     public String getAccepts_marketing_updated_at() {
         return accepts_marketing_updated_at;
     }

    public void setMarketing_opt_in_level(String marketing_opt_in_level) {
         this.marketing_opt_in_level = marketing_opt_in_level;
     }
     public String getMarketing_opt_in_level() {
         return marketing_opt_in_level;
     }

    public void setAdmin_graphql_api_id(String admin_graphql_api_id) {
         this.admin_graphql_api_id = admin_graphql_api_id;
     }
     public String getAdmin_graphql_api_id() {
         return admin_graphql_api_id;
     }

    public void setDefault1_address(Default_address default1_address) {
         this.default_address = default1_address;
     }
     public Default_address getDefault1_address() {
         return default_address;
     }

}