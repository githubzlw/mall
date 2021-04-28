package com.macro.mall.portal.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.entity.XmsCustomerProduct;
import com.macro.mall.entity.XmsSourcingList;
import com.macro.mall.portal.domain.XmsCustomerProductParam;
import com.macro.mall.portal.domain.XmsSourcingInfoParam;
import com.macro.mall.portal.service.IXmsCustomerProductService;
import com.macro.mall.portal.service.IXmsSourcingListService;
import com.macro.mall.portal.service.UmsMemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.macro.mall.portal.controller
 * @date:2021-04-26
 */
@Api(tags = "XmsSourcingController", description = "Sourcing操作相关接口")
@RestController
@RequestMapping("/sourcing")
@Slf4j
public class XmsSourcingController {

    @Autowired
    private UmsMemberService umsMemberService;

    @Autowired
    private IXmsSourcingListService xmsSourcingListService;

    @Autowired
    private IXmsCustomerProductService xmsCustomerProductService;


    @ApiOperation("sourcingList列表")
    @RequestMapping(value = "/sourcingList", method = RequestMethod.GET)
    public CommonResult sourcingList(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                     @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {

        XmsSourcingInfoParam sourcingParam = new XmsSourcingInfoParam();
        try {

            sourcingParam.setPageNum(pageNum);
            sourcingParam.setPageSize(pageSize);
            sourcingParam.setMemberId(this.umsMemberService.getCurrentMember().getId());
            sourcingParam.setUsername(this.umsMemberService.getCurrentMember().getUsername());
            Page<XmsSourcingList> listPage = this.xmsSourcingListService.list(sourcingParam);
            return CommonResult.success(listPage);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("sourcingList,sourcingParam[{}],error:", sourcingParam, e);
            return CommonResult.failed("query failed");
        }
    }


    @ApiOperation("SourcingList添加到客户产品表")
    @RequestMapping(value = "/addToMyProductList", method = RequestMethod.POST)
    @ApiImplicitParams({@ApiImplicitParam(name = "sourcingId", value = "sourcing表的ID", required = true, dataType = "Integer")})
    public CommonResult addToMyProductList(Long sourcingId) {
        try {
            // 检查数据是否存在
            XmsSourcingList xmsSourcingList = this.xmsSourcingListService.getById(sourcingId);
            if (null == xmsSourcingList) {
                return CommonResult.validateFailed("No data available");
            }

            // 检查数据是否插入
            XmsCustomerProduct product = new XmsCustomerProduct();
            product.setMemberId(this.umsMemberService.getCurrentMember().getId());
            product.setUsername(this.umsMemberService.getCurrentMember().getUsername());
            product.setSourcingId(sourcingId.intValue());
            boolean isCheck = this.xmsSourcingListService.checkHasXmsCustomerProduct(product);

            if (isCheck) {
                return CommonResult.validateFailed("The data already exists");
            }

            // 设置产品信息
            product.setProductId(Long.parseLong(String.valueOf(xmsSourcingList.getProductId())));
            product.setSourceLink(xmsSourcingList.getSourceLink());
            product.setStatus(0);
            product.setCreateTime(new Date());
            product.setUpdateTime(new Date());
            this.xmsCustomerProductService.save(product);
            return CommonResult.success(product);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("addToMyProductList,sourcingId[{}],error:", sourcingId, e);
            return CommonResult.failed("addToMyProductList error");
        }
    }

    @ApiOperation("获取客户产品列表")
    @RequestMapping(value = "/productList", method = RequestMethod.GET)
    public CommonResult productList(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                    @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {

        XmsCustomerProductParam productParam = new XmsCustomerProductParam();
        try {

            productParam.setPageNum(pageNum);
            productParam.setPageSize(pageSize);
            productParam.setMemberId(this.umsMemberService.getCurrentMember().getId());
            productParam.setUsername(this.umsMemberService.getCurrentMember().getUsername());
            Page<XmsCustomerProduct> productPage = this.xmsCustomerProductService.list(productParam);
            return CommonResult.success(productPage);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("productList,productParam[{}],error:", productParam, e);
            return CommonResult.failed("query failed");
        }
    }

}
