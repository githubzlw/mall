package com.macro.mall.portal.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.common.util.RequestUtil;
import com.macro.mall.entity.UmsSourcingSearchLog;
import com.macro.mall.entity.XmsMsg;
import com.macro.mall.portal.domain.SourcingSearchParam;
import com.macro.mall.portal.domain.XmsMsgParam;
import com.macro.mall.portal.service.UmsSourcingSearchLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * sourcing搜索Controller
 * Created by zlw on 2021/4/23.
 */
@Controller
@Api(tags = "UmsSourcingSearchController", description = "sourcing搜索")
@RequestMapping("/sourcingSearchLog")
public class UmsSourcingSearchLogController {

    @Autowired
    private UmsSourcingSearchLogService umsSourcingSearchLogService;

    @ApiOperation("插入搜索日志")
    @RequestMapping(value = "/insertSearchLog", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult insertSearchLog(HttpServletRequest request, SourcingSearchParam sourcingSearchParam) {

        String ip = RequestUtil.getRequestIp(request);
        sourcingSearchParam.setIp(ip);

        umsSourcingSearchLogService.insertSourcingSearchLog(sourcingSearchParam);
        return CommonResult.success(null, "插入成功");
    }

    @ApiOperation("读取搜索日志")
    @RequestMapping(value = "/getSearchLogList", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult getSearchLogList(SourcingSearchParam sourcingSearchParam,
                                         @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                         @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize) {

        Page<UmsSourcingSearchLog> umsSourcingSearchLogList =
                umsSourcingSearchLogService.getSearchLogList(sourcingSearchParam, pageNum, pageSize);

        return CommonResult.success(umsSourcingSearchLogList);
    }

}
