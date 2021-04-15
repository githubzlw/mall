package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.XmsChromeUpload;
import com.macro.mall.portal.domain.MemberProductCollection;
import com.macro.mall.portal.domain.XmsChromeUploadParam;
import com.macro.mall.portal.service.UmsMemberService;
import com.macro.mall.portal.service.XmsChromeUploadService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 谷歌扩展插件抓取内容上传Controller
 * Created by jack.luo on 2021/4/15.
 */
@Controller
@Api(tags = "XmsChromeUploadController", description = "谷歌扩展插件抓取内容上传")
@RequestMapping("/chrome")
public class XmsChromeUploadController {

    @Autowired
    private UmsMemberService umsMemberService;

    @Autowired
    private XmsChromeUploadService xmsChromeUploadService;

    @ApiOperation("上传")
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult upload(@RequestBody XmsChromeUploadParam xmsChromeUploadParam) {

        xmsChromeUploadService.upload(xmsChromeUploadParam);
        return CommonResult.success(null,"上传成功");
    }

    @ApiOperation("显示上传列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<XmsChromeUpload>> list(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                            @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize) {

        List<XmsChromeUpload> result = xmsChromeUploadService.list(umsMemberService.getCurrentMember().getId(),pageNum,pageSize);
        return CommonResult.success(result);
    }

}