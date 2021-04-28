package com.macro.mall.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.dto.XmsSourcingInfoParam;
import com.macro.mall.entity.XmsSourcingList;
import com.macro.mall.service.IXmsSourcingListService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * 客户Sourcing申请处理API
 */
@RestController
@Api(tags = "XmsSourcingController", description = "客户申请表")
@RequestMapping("/sourcing")
@Slf4j
public class XmsSourcingController {


    @Autowired
    private IXmsSourcingListService xmsSourcingListService;

    @ApiOperation("客户Sourcing列表")
    @RequestMapping(value = "/list", method = {RequestMethod.POST, RequestMethod.GET})
    public CommonResult list(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                             @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {

        XmsSourcingInfoParam sourcingParam = new XmsSourcingInfoParam();
        try {
            Page<XmsSourcingList> sourcingListPage = this.xmsSourcingListService.list(sourcingParam);
            return CommonResult.success(sourcingListPage);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("list,sourcingParam[{}],error:", sourcingParam, e);
            return CommonResult.failed("query list error!");
        }
    }


    @ApiOperation("客户Sourcing货源更新")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "XmsSourcingList的ID", dataType = "Long", required = true),
            @ApiImplicitParam(name = "sourceLink", value = "货源链接", dataType = "String", required = true)})
    @RequestMapping(value = "/updateSourceLink", method = RequestMethod.POST)
    public CommonResult updateSourceLink(Long id, String sourceLink) {
        Assert.isTrue(null != id && id > 0, "id null");
        Assert.isTrue(StrUtil.isNotBlank(sourceLink), "sourceLink null");
        XmsSourcingList sourcingInfo = new XmsSourcingList();
        try {
            sourcingInfo.setId(id);
            sourcingInfo.setSourceLink(sourceLink);
            this.xmsSourcingListService.updateSourceLink(sourcingInfo);
            return CommonResult.success(sourcingInfo);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("updateSourceLink,sourcingInfo[{}],error:", sourcingInfo, e);
            return CommonResult.failed("query list error!");
        }
    }


}
