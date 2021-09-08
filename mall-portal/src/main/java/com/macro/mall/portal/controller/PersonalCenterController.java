package com.macro.mall.portal.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.entity.XmsPayment;
import com.macro.mall.entity.XmsRecordOfChangeInBalance;
import com.macro.mall.portal.domain.XmsPaymentParam;
import com.macro.mall.portal.service.IXmsPaymentService;
import com.macro.mall.portal.service.IXmsRecordOfChangeInBalanceService;
import com.macro.mall.portal.service.UmsMemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.macro.mall.portal.controller
 * @date:2021-05-10
 */
@Api(tags = "PersonalCenterController", description = "客户个人中心操作相关接口")
@RestController
@RequestMapping("/personal")
@Slf4j
public class PersonalCenterController {

    @Autowired
    private UmsMemberService umsMemberService;
    @Autowired
    private IXmsPaymentService xmsPaymentService;
    @Autowired
    private IXmsRecordOfChangeInBalanceService xmsRecordOfChangeInBalanceService;

    @ApiOperation("支付记录列表")
    @RequestMapping(value = "/paymentRecords", method = RequestMethod.GET)
    public CommonResult paymentRecords(XmsPaymentParam xmsPaymentParam) {
        Assert.notNull(xmsPaymentParam, "xmsPaymentParam null");
        Assert.isTrue(null != xmsPaymentParam.getPageNum() && xmsPaymentParam.getPageNum() > 0, "pageNum null");
        Assert.isTrue(null != xmsPaymentParam.getPageSize() && xmsPaymentParam.getPageSize() > 0, "pageSize null");

        try {
            xmsPaymentParam.setMemberId(this.umsMemberService.getCurrentMember().getId());
            xmsPaymentParam.setUsername(this.umsMemberService.getCurrentMember().getUsername());
            Page<XmsPayment> listPage = this.xmsPaymentService.list(xmsPaymentParam);
            if(CollectionUtil.isNotEmpty(listPage.getRecords())){
                listPage.getRecords().forEach(e-> e.setRemark(null));
            }
            return CommonResult.success(listPage);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("paymentRecords,xmsPaymentParam[{}],error:", xmsPaymentParam, e);
            return CommonResult.failed("query failed");
        }
    }


    @ApiOperation("余额变更记录列表")
    @RequestMapping(value = "/balanceRecords", method = RequestMethod.GET)
    public CommonResult balanceRecords(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                       @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {

        try {
            Page<XmsRecordOfChangeInBalance> listPage = this.xmsRecordOfChangeInBalanceService.list(pageNum, pageSize, this.umsMemberService.getCurrentMember().getUsername());
            return CommonResult.success(listPage);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("paymentRecords,pageNum[{}],pageSize[{}],error:", pageNum, pageSize, e);
            return CommonResult.failed("query failed");
        }
    }
}
