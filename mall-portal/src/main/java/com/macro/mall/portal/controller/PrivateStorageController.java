package com.macro.mall.portal.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.entity.XmsCustomerSkuStock;
import com.macro.mall.portal.domain.XmsCustomerSkuStockParam;
import com.macro.mall.portal.service.IXmsCustomerSkuStockService;
import com.macro.mall.portal.service.UmsMemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.macro.mall.portal.controller
 * @date:2021-05-07
 */
@Api(tags = "PrivateStorageController", description = "个人库存操作相关接口")
@RestController
@RequestMapping("/privateStorage")
@Slf4j
public class PrivateStorageController {


    @Autowired
    private UmsMemberService umsMemberService;
    @Autowired
    private IXmsCustomerSkuStockService iXmsCustomerSkuStockService;


    @ApiOperation("获取客户产品列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult list(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                             @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize, String title) {

        XmsCustomerSkuStockParam skuStockParam = new XmsCustomerSkuStockParam();
        try {
            skuStockParam.setPageNum(pageNum);
            skuStockParam.setPageSize(pageSize);
            skuStockParam.setMemberId(this.umsMemberService.getCurrentMember().getId());
            skuStockParam.setUsername(this.umsMemberService.getCurrentMember().getUsername());
            skuStockParam.setTitle(title);
            Page<XmsCustomerSkuStock> skuStockPage = this.iXmsCustomerSkuStockService.list(skuStockParam);
            // 可能需要加工一下，合并相同sku的数据
            return CommonResult.success(skuStockPage);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("list,skuStockParam[{}],error:", skuStockParam, e);
            return CommonResult.failed("query failed");
        }
    }

}
