package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.entity.XmsMsg;
import com.macro.mall.entity.XmsMsgrecycle;
import com.macro.mall.portal.service.IXmsMsgService;
import com.macro.mall.portal.service.IXmsMsgrecycleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 首页内容管理Controller
 * Created by zlw on 2021/4/23.
 */
@Controller
@Api(tags = "XmsMsgController", description = "站内消息")
@RequestMapping("/msg")
public class XmsMsgController {

    @Autowired
    private IXmsMsgrecycleService xmsMsgrecycleService;

    @Autowired
    private IXmsMsgService xmsMsgService;

    @ApiOperation("已读用户消息记录")
    @RequestMapping(value = "/readMsgList", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<XmsMsgrecycle>> readMsgList(@RequestParam String username) {
        List<XmsMsgrecycle> msgList = xmsMsgrecycleService.readMsgList(username);
        return CommonResult.success(msgList);
    }

    @ApiOperation("消息回收站表没有读过的用户消息记录")
    @RequestMapping(value = "/unreadMsgList", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<XmsMsg>> unreadMsgList(@RequestParam String username) {
        List<XmsMsg> unMsgList = xmsMsgService.unreadMsgList(username);
        return CommonResult.success(unMsgList);
    }
}
