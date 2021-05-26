/**
  * Copyright 2019 bejson.com 
  */
package com.macro.mall.shopify.pojo.orders;

import lombok.Data;

/**
 * Auto-generated: 2019-04-30 15:43:48
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Data
public class Shipping_address {

    private long id;
    private long order_no;
    private String first_name;
    private String address1;
    private String phone;
    private String city;
    private String zip;
    private String province;
    private String country;
    private String last_name;
    private String address2;
    private String company;
    private double latitude;
    private double longitude;
    private String name;
    private String country_code;
    private String province_code;
    public void setFirst_name(String first_name) {
         this.first_name = first_name;
     }
     public String getFirst_name() {
         return first_name;
     }

    public void setAddress1(String address1) {
         this.address1 = address1;
     }
     public String getAddress1() {
         return address1;
     }

    public void setPhone(String phone) {
         this.phone = phone;
     }
     public String getPhone() {
         return phone;
     }

    public void setCity(String city) {
         this.city = city;
     }
     public String getCity() {
         return city;
     }

    public void setZip(String zip) {
         this.zip = zip;
     }
     public String getZip() {
         return zip;
     }

    public void setProvince(String province) {
         this.province = province;
     }
     public String getProvince() {
         return province;
     }

    public void setCountry(String country) {
         this.country = country;
     }
     public String getCountry() {
         return country;
     }

    public void setLast_name(String last_name) {
         this.last_name = last_name;
     }
     public String getLast_name() {
         return last_name;
     }

    public void setAddress2(String address2) {
         this.address2 = address2;
     }
     public String getAddress2() {
         return address2;
     }

    public void setCompany(String company) {
         this.company = company;
     }
     public String getCompany() {
         return company;
     }

    public void setLatitude(double latitude) {
         this.latitude = latitude;
     }
     public double getLatitude() {
         return latitude;
     }

    public void setLongitude(double longitude) {
         this.longitude = longitude;
     }
     public double getLongitude() {
         return longitude;
     }

    public void setName(String name) {
         this.name = name;
     }
     public String getName() {
         return name;
     }

    public void setCountry_code(String country_code) {
         this.country_code = country_code;
     }
     public String getCountry_code() {
         return country_code;
     }

    public void setProvince_code(String province_code) {
         this.province_code = province_code;
     }
     public String getProvince_code() {
         return province_code;
     }

}