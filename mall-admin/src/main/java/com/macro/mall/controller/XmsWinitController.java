package com.macro.mall.controller;

import cn.hutool.core.util.StrUtil;
import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.domain.WinitParam;
import com.macro.mall.domain.XmsShopifyOrderinfoParam;
import com.macro.mall.entity.XmsShopifyOrderComb;
import com.macro.mall.util.WinitUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.macro.mall.controller
 * @date:2021-10-08
 */
@Api(tags = "XmsWinitController", description = "Winit调用接口")
@RestController
@Slf4j
@RequestMapping("/xmsWinit")
public class XmsWinitController {

    @Resource
    private WinitUtils winitUtils;

    @ApiOperation("获取库存")
    @RequestMapping(value = "/queryWarehouseStorage", method = RequestMethod.GET)
    public CommonResult queryWarehouseStorage(WinitParam winitParam) {

        Assert.notNull(winitParam, "winitParam null");
        Assert.isTrue(StrUtil.isNotBlank(winitParam.getWarehouseId()), "warehouseID null");

        try {
            int storageTotal = this.winitUtils.getAndQueryWarehouseStorage(winitParam);
            return CommonResult.success(storageTotal);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("queryWarehouseStorage,winitParam[{}],error:", winitParam, e);
            return CommonResult.failed("query failed");
        }
    }
}
