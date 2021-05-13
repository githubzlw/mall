package com.macro.mall.portal.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.UmsMember;
import com.macro.mall.portal.domain.SiteSourcing;
import com.macro.mall.portal.domain.SiteSourcingParam;
import com.macro.mall.portal.service.UmsMemberService;
import com.macro.mall.portal.util.SourcingUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: jinjie
 * @version: v1.0
 * @description: com.macro.mall.portal.buyforme
 * @date:2021-04-15
 */
@Api(tags = "DataSync", description = "数据同步的调用接口")
@RestController
@Slf4j
@RequestMapping("/dataSync")
public class DataSync {


    private final UmsMemberService umsMemberService;

    @Autowired
    public DataSync(UmsMemberService umsMemberService) {
        this.umsMemberService = umsMemberService;
    }


    @ApiOperation(value = "同步用户信息", notes = "同步逻辑")
    @PostMapping("/getAllUser")
    @ResponseBody
    public CommonResult getAllUser(@RequestParam(value = "id", required = false) Long id) {

        try {
            List<UmsMember> umsMemberList = umsMemberService.getAllUser(id);

            return CommonResult.success(umsMemberList);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("getAllUser,error:", e);
            return CommonResult.failed(e.getMessage());
        }
    }

}
