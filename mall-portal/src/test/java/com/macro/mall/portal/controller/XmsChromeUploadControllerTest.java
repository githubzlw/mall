//package com.macro.mall.portal.controller;
//
//import cn.hutool.json.JSONUtil;
//import com.macro.mall.portal.domain.XmsChromeUploadParam;
//import org.junit.jupiter.api.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.ResultActions;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
//import org.springframework.web.bind.annotation.PostMapping;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@RunWith(SpringRunner.class)
//@WebMvcTest(XmsChromeUploadController.class)
//class XmsChromeUploadControllerTest {
//
//    @Autowired
//    private MockMvc mvc;
//
//    @Test
//    public void content() throws Exception
//    {
//        mvc.perform( MockMvcRequestBuilders
//                .get("/home/content")
//                .accept(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.employees").exists())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.employees[*].employeeId").isNotEmpty());
//    }
//
//    @Test
//    public void upload() throws Exception
//    {
//        XmsChromeUploadParam param = new XmsChromeUploadParam();
//        mvc.perform(MockMvcRequestBuilders
//                .post("/upload")
//                .content(JSONUtil.parseObj(param).toString())
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isCreated())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.employeeId").exists());
//    }
//
//
//}
