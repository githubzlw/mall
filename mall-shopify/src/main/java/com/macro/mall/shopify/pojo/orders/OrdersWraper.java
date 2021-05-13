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
public class OrdersWraper {

    private List<Orders> orders;
    public void setOrders(List<Orders> orders) {
         this.orders = orders;
     }
     public List<Orders> getOrders() {
         return orders;
     }

}