package com.macro.mall.onebound.rest;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.onebound.service.AliExpressService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.macro.mall.onebound.rest
 * @date:2021-05-27
 */
@Controller
@Api(tags = "AliController", description = "ali的商品抓取")
@RequestMapping("/oneBound")
public class AliController {


    @Autowired
    private AliExpressService expressService;


    @GetMapping("/aliExpress/details/{pid}")
    @ResponseBody
    public CommonResult getDetails(@PathVariable(name = "pid") String pid) {
        return expressService.getDetails(pid);
    }

}
