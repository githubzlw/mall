package com.macro.mall.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.domain.XmsPaymentParam;
import com.macro.mall.dto.OmsOrderDetail;
import com.macro.mall.dto.SyncOrderParam;
import com.macro.mall.entity.XmsPayment;
import com.macro.mall.model.OmsOrder;
import com.macro.mall.service.OmsOrderService;
import com.macro.mall.service.UmsMemberService;
import com.macro.mall.service.XmsPaymentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.macro.mall.controller
 * @date:2021-05-14
 */
@RestController
@Api(tags = "XmsPaymentController", description = "支付数据接口")
@RequestMapping("/xmsPayment")
@Slf4j
public class XmsPaymentController {

    @Autowired
    private XmsPaymentService xmsPaymentService;

    @ApiOperation("支付记录列表")
    @RequestMapping(value = "/paymentRecords", method = RequestMethod.GET)
    public CommonResult paymentRecords(XmsPaymentParam xmsPaymentParam) {
        Assert.notNull(xmsPaymentParam, "xmsPaymentParam null");
        Assert.isTrue(null != xmsPaymentParam.getPageNum() && xmsPaymentParam.getPageNum() > 0, "pageNum null");
        Assert.isTrue(null != xmsPaymentParam.getPageSize() && xmsPaymentParam.getPageSize() > 0, "pageSize null");

        try {
            Page<XmsPayment> listPage = this.xmsPaymentService.list(xmsPaymentParam);
            return CommonResult.success(listPage);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("paymentRecords,xmsPaymentParam[{}],error:", xmsPaymentParam, e);
            return CommonResult.failed("query failed");
        }
    }
}
