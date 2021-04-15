package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.portal.domain.XmsChromeUploadParam;
import com.macro.mall.portal.service.XmsChromeUploadService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * 谷歌扩展插件抓取内容上传Controller
 * Created by jack.luo on 2021/4/15.
 */
@Controller
@Api(tags = "XmsChromeUploadController", description = "谷歌扩展插件抓取内容上传")
@RequestMapping("/chrome")
public class XmsChromeUploadController {
    @Autowired
    private XmsChromeUploadService xmsChromeUploadService;

    @ApiOperation("添加商品到购物车")
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult upload(@RequestBody XmsChromeUploadParam xmsChromeUploadParam) {

        xmsChromeUploadService.upload(xmsChromeUploadParam);
        return CommonResult.success(null,"上传成功");
    }

}
