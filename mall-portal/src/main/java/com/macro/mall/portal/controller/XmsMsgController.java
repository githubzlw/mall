package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.entity.XmsMsg;
import com.macro.mall.portal.domain.XmsMsgParam;
import com.macro.mall.portal.service.IXmsMsgService;
import com.macro.mall.portal.service.IXmsMsgrecycleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 消息管理Controller
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

    @ApiOperation("用户新消息插入")
    @RequestMapping(value = "/insetMsgList", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult insetMsgList(XmsMsgParam xmsMsgParam) {

        xmsMsgService.insetMsgList(xmsMsgParam);
        return CommonResult.success(null,"插入成功");
    }

    @ApiOperation("用户已读消息插入")
    @RequestMapping(value = "/insetMsgRecycle", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult insetMsgRecycle(@RequestParam String mail,
                                            @RequestParam Integer id) {

        xmsMsgrecycleService.insetMsgRecycle(mail,id);
        return CommonResult.success(null,"插入成功");
    }


    @ApiOperation("删除消息")
    @RequestMapping(value = "/updateMsgRecycle", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updateMsgRecycle(@RequestParam String mail,
                                        @RequestParam Integer id) {

        xmsMsgrecycleService.updateMsgRecycle(mail,id);
        return CommonResult.success(null,"删除更新成功");
    }

    @ApiOperation("已读用户消息记录")
    @RequestMapping(value = "/readMsgList", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<List<XmsMsg>> readMsgList(XmsMsgParam xmsMsgParam,
                                                  @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                  @RequestParam(value = "pageSize", defaultValue = "6") Integer pageSize) {

        List<XmsMsg> msgList = xmsMsgService.readMsgList(xmsMsgParam,pageNum,pageSize);
        return CommonResult.success(msgList);
    }

    @ApiOperation("消息回收站表没有读过的用户消息记录")
    @RequestMapping(value = "/unreadMsgList", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<List<XmsMsg>> unreadMsgList(XmsMsgParam xmsMsgParam,
                                                    @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                    @RequestParam(value = "pageSize", defaultValue = "6") Integer pageSize) {
        List<XmsMsg> unMsgList = xmsMsgService.unreadMsgList(xmsMsgParam,pageNum,pageSize);
        return CommonResult.success(unMsgList);
    }

    @ApiOperation("已读用户消息记录总数")
    @RequestMapping(value = "/readMsgListCount", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult readMsgListCount(XmsMsgParam xmsMsgParam) {

        int count = xmsMsgService.readMsgListCount(xmsMsgParam);
        return CommonResult.success(count);
    }

    @ApiOperation("消息回收站表没有读过的用户消息总数")
    @RequestMapping(value = "/unreadMsgListCount", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult unreadMsgListCount(XmsMsgParam xmsMsgParam) {
        int count = xmsMsgService.unreadMsgListCount(xmsMsgParam);
        return CommonResult.success(count);
    }


}
