package com.macro.mall.portal.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.common.util.RequestUtil;
import com.macro.mall.entity.UmsSourcingSearchLog;
import com.macro.mall.portal.domain.SourcingSearchParam;
import com.macro.mall.portal.service.UmsSourcingSearchLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * sourcing搜索日志Controller
 * Created by zlw on 2021/4/23.
 */
@Controller
@Api(tags = "UmsSourcingSearchController", description = "sourcing搜索日志")
@RequestMapping("/sourcingSearchLog")
public class UmsSourcingSearchLogController {

    @Autowired
    private UmsSourcingSearchLogService umsSourcingSearchLogService;

    @ApiOperation("插入搜索日志")
    @RequestMapping(value = "/insertSearchLog", method = RequestMethod.POST)
    @ResponseBody
    @CrossOrigin("*")
    public CommonResult insertSearchLog(HttpServletRequest request, SourcingSearchParam sourcingSearchParam) {

        Assert.isTrue(null != sourcingSearchParam.getSourcingSearch(), "sourcingSearchParam null");

        String ip = RequestUtil.getRequestIp(request);
        sourcingSearchParam.setIp(ip);

        umsSourcingSearchLogService.insertSourcingSearchLog(sourcingSearchParam);
        return CommonResult.success(null, "插入成功");
    }

    @ApiOperation("读取搜索日志")
    @RequestMapping(value = "/getSearchLogList", method = RequestMethod.POST)
    @ResponseBody
    @CrossOrigin("*")
    public CommonResult getSearchLogList(SourcingSearchParam sourcingSearchParam,
                                         @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                         @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize) {

        Page<UmsSourcingSearchLog> umsSourcingSearchLogList =
                umsSourcingSearchLogService.getSearchLogList(sourcingSearchParam, pageNum, pageSize);

        return CommonResult.success(umsSourcingSearchLogList);
    }


    @ApiOperation("增加下载次数")
    @RequestMapping(value = "/addTotal", method = RequestMethod.POST)
    @ResponseBody
    @CrossOrigin("*")
    public CommonResult addTotal() {
        umsSourcingSearchLogService.addTotal();
        return CommonResult.success(null, "修改成功");
    }

    @ApiOperation("获取下载次数")
    @RequestMapping(value = "/getTotal", method = RequestMethod.POST)
    @ResponseBody
    @CrossOrigin("*")
    public CommonResult getTotal() {
        Long total = umsSourcingSearchLogService.getTotal();
        return CommonResult.success(total);
    }


}
