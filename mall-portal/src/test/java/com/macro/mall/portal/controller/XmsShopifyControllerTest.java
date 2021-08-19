package com.macro.mall.portal.controller;

import com.alibaba.fastjson.JSONObject;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.portal.MallPortalApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContext;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.macro.mall.portal.controller
 * @date:2021-08-19
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MallPortalApplication.class)
@ActiveProfiles("dev")

@Transactional
@Rollback()
public class XmsShopifyControllerTest {

    @Autowired
    private XmsShopifyController shopifyController;

    private MockMvc mockMvc;

    @Before
    public void setup() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(shopifyController).build();
    }

    @Test
    @WithUserDetails(value = "1071083166@qq.com", userDetailsServiceBeanName = "userDetailsService")
    public void testShopifyList() throws Exception {

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/shopify/list").param("pageNum", "1").param("pageSize", "4")).andExpect(status().isOk()).andDo(print()).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        CommonResult commonResult = JSONObject.parseObject(contentAsString, CommonResult.class);
        Assert.assertTrue("commonResult null", 200 == commonResult.getCode());
        System.err.println(JSONObject.toJSONString(commonResult));

    }

    @Test
    @WithUserDetails(value = "1071083166@qq.com", userDetailsServiceBeanName = "userDetailsService")
    public void testGetShopifyOrders() throws Exception {

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/shopify/getShopifyOrders")).andExpect(status().isOk()).andDo(print()).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        CommonResult commonResult = JSONObject.parseObject(contentAsString, CommonResult.class);
        Assert.assertTrue("commonResult null", 200 == commonResult.getCode());
        System.err.println(JSONObject.toJSONString(commonResult));

    }


}
