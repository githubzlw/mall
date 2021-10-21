package com.macro.mall.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.dto.XmsSourcingInfoParam;
import com.macro.mall.entity.XmsCustomerProduct;
import com.macro.mall.entity.XmsSourcingList;
import com.macro.mall.service.IXmsCustomerProductService;
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

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;


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
    @Resource
    private IXmsCustomerProductService xmsCustomerProductService;

    @ApiOperation("客户Sourcing列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult list(XmsSourcingInfoParam sourcingParam) {

        Assert.notNull(sourcingParam, "sourcingParam null");
        try {
            if (null == sourcingParam.getPageSize() || sourcingParam.getPageSize() <= 0) {
                sourcingParam.setPageSize(10);
            }
            if (null == sourcingParam.getPageNum() || sourcingParam.getPageNum() <= 0) {
                sourcingParam.setPageNum(1);
            }

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
            return CommonResult.failed("updateSourceLink error:" + e.getMessage());
        }
    }

    @ApiOperation("客户Sourcing状态更新")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "XmsSourcingList的ID", dataType = "Long", required = true),
            @ApiImplicitParam(name = "status", value = "状态：0已接收;1处理中;2已处理;4取消;5无效数据;-1->删除", dataType = "String", required = true)})
    @RequestMapping(value = "/updateSourceStatus", method = RequestMethod.POST)
    public CommonResult updateSourceStatus(Long id, Integer status) {
        Assert.isTrue(null != id && id > 0, "id null");
        Assert.isTrue(null != status && status > -2, "status null");
        XmsSourcingList sourcingInfo = new XmsSourcingList();
        try {
            sourcingInfo.setId(id);
            sourcingInfo.setStatus(status);
            this.xmsSourcingListService.updateSourceStatus(sourcingInfo);
            if (status == 2) {


                XmsSourcingList byId = this.xmsSourcingListService.getById(id);
                QueryWrapper<XmsCustomerProduct> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(XmsCustomerProduct::getSourcingId, byId.getId()).eq(XmsCustomerProduct::getProductId, byId.getProductId()).eq(XmsCustomerProduct::getMemberId, byId.getMemberId())
                        .eq(XmsCustomerProduct::getStatus, 9);
                List<XmsCustomerProduct> list = this.xmsCustomerProductService.list(queryWrapper);
                if (CollectionUtil.isNotEmpty(list)) {

                    // 更新youLiveProduct的状态
                    UpdateWrapper<XmsCustomerProduct> updateCustomWrapper = new UpdateWrapper<>();
                    updateCustomWrapper.lambda().set(XmsCustomerProduct::getImportFlag, 1).set(XmsCustomerProduct::getStatus, 1)
                            .eq(XmsCustomerProduct::getSourcingId, byId.getId()).eq(XmsCustomerProduct::getProductId, byId.getProductId())
                            .eq(XmsCustomerProduct::getMemberId, byId.getMemberId())
                            .eq(XmsCustomerProduct::getStatus, 9);
                    this.xmsCustomerProductService.update(updateCustomWrapper);

                    UpdateWrapper<XmsSourcingList> updateSourcingWrapper = new UpdateWrapper<>();
                    updateSourcingWrapper.lambda().set(XmsSourcingList::getAddProductFlag, 1).set(XmsSourcingList::getUpdateTime, new Date())
                            .eq(XmsSourcingList::getId, byId.getId());
                    this.xmsSourcingListService.update(updateSourcingWrapper);
                    list.clear();
                }


            }
            return CommonResult.success(sourcingInfo);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("updateSourceStatus,sourcingInfo[{}],error:", sourcingInfo, e);
            return CommonResult.failed("updateSourceStatus error:" + e.getMessage());
        }
    }


}
