package com.macro.mall.shopify.pojo.orders;

import lombok.Data;

/**
 * Auto-generated: 2019-04-30 15:43:48
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Data
public class Client_details {

    private String browser_ip;
    private String accept_language;
    private String user_agent;
    private String session_hash;
    private String browser_width;
    private String browser_height;
    public void setBrowser_ip(String browser_ip) {
         this.browser_ip = browser_ip;
     }
     public String getBrowser_ip() {
         return browser_ip;
     }

    public void setAccept_language(String accept_language) {
         this.accept_language = accept_language;
     }
     public String getAccept_language() {
         return accept_language;
     }

    public void setUser_agent(String user_agent) {
         this.user_agent = user_agent;
     }
     public String getUser_agent() {
         return user_agent;
     }

    public void setSession_hash(String session_hash) {
         this.session_hash = session_hash;
     }
     public String getSession_hash() {
         return session_hash;
     }

    public void setBrowser_width(String browser_width) {
         this.browser_width = browser_width;
     }
     public String getBrowser_width() {
         return browser_width;
     }

    public void setBrowser_height(String browser_height) {
         this.browser_height = browser_height;
     }
     public String getBrowser_height() {
         return browser_height;
     }

}