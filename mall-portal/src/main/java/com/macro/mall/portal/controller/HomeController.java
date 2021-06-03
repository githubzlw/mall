package com.macro.mall.portal.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.common.util.UrlUtil;
import com.macro.mall.entity.XmsPmsProductEdit;
import com.macro.mall.entity.XmsPmsSkuStockEdit;
import com.macro.mall.model.CmsSubject;
import com.macro.mall.model.PmsProduct;
import com.macro.mall.model.PmsProductCategory;
import com.macro.mall.model.UmsMember;
import com.macro.mall.portal.config.MicroServiceConfig;
import com.macro.mall.portal.domain.HomeContentResult;
import com.macro.mall.portal.service.HomeService;
import com.macro.mall.portal.service.IXmsPmsProductEditService;
import com.macro.mall.portal.service.IXmsPmsSkuStockEditService;
import com.macro.mall.portal.service.UmsMemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 首页内容管理Controller
 * Created by macro on 2019/1/28.
 */
@Controller
@Api(tags = "HomeController", description = "首页内容管理")
@RequestMapping("/home")
public class HomeController {
    @Autowired
    private HomeService homeService;
    private UrlUtil urlUtil = UrlUtil.getInstance();
    @Autowired
    private MicroServiceConfig microServiceConfig;
    @Autowired
    private UmsMemberService umsMemberService;
    @Autowired
    private IXmsPmsProductEditService xmsPmsProductEditService;
    @Autowired
    private IXmsPmsSkuStockEditService xmsPmsSkuStockEditService;

    @ApiOperation("首页内容页信息展示")
    @RequestMapping(value = "/content", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<HomeContentResult> content() {
        HomeContentResult contentResult = homeService.content();
        return CommonResult.success(contentResult);
    }

    @ApiOperation("分页获取推荐商品")
    @RequestMapping(value = "/recommendProductList", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<PmsProduct>> recommendProductList(@RequestParam(value = "pageSize", defaultValue = "4") Integer pageSize,
                                                               @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        List<PmsProduct> productList = homeService.recommendProductList(pageSize, pageNum);
        return CommonResult.success(productList);
    }

    @ApiOperation("获取首页商品分类")
    @RequestMapping(value = "/productCateList/{parentId}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<PmsProductCategory>> getProductCateList(@PathVariable Long parentId) {
        List<PmsProductCategory> productCategoryList = homeService.getProductCateList(parentId);
        return CommonResult.success(productCategoryList);
    }

    @ApiOperation("根据分类获取专题")
    @RequestMapping(value = "/subjectList", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<CmsSubject>> getSubjectList(@RequestParam(required = false) Long cateId,
                                                         @RequestParam(value = "pageSize", defaultValue = "4") Integer pageSize,
                                                         @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        List<CmsSubject> subjectList = homeService.getSubjectList(cateId,pageSize,pageNum);
        return CommonResult.success(subjectList);
    }

    @ApiOperation("分页获取人气推荐商品")
    @RequestMapping(value = "/hotProductList", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<PmsProduct>> hotProductList(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                         @RequestParam(value = "pageSize", defaultValue = "6") Integer pageSize) {
        List<PmsProduct> productList = homeService.hotProductList(pageNum,pageSize);
        return CommonResult.success(productList);
    }

    @ApiOperation("分页获取新品推荐商品")
    @RequestMapping(value = "/newProductList", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<PmsProduct>> newProductList(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                         @RequestParam(value = "pageSize", defaultValue = "6") Integer pageSize) {
        List<PmsProduct> productList = homeService.newProductList(pageNum,pageSize);
        return CommonResult.success(productList);
    }

    @GetMapping(value = "/getProductInfo")
    @ApiOperation("根据id获得产品数据")
    @ResponseBody
    public CommonResult getProductInfo(@RequestParam Long id) {
        UmsMember currentMember = this.umsMemberService.getCurrentMember();
        try {
            QueryWrapper<XmsPmsProductEdit> productEditWrapper = new QueryWrapper<>();
            productEditWrapper.lambda().eq(XmsPmsProductEdit::getMemberId, currentMember.getId()).eq(XmsPmsProductEdit::getId, id);
            int count = this.xmsPmsProductEditService.count(productEditWrapper);
            if (count == 0) {
                JSONObject jsonObject = this.urlUtil.callUrlByGet(this.microServiceConfig.getProductUrl() + "/getProductInfo?id=" + id);
                CommonResult commonResult = JSONObject.parseObject(jsonObject.toJSONString(), CommonResult.class);
                return commonResult;
            } else {
                JSONObject jsonObject = this.urlUtil.callUrlByGet(this.microServiceConfig.getProductUrl() + "/getCustomProductInfo?id=" + id);
                CommonResult commonResult = JSONObject.parseObject(jsonObject.toJSONString(), CommonResult.class);
                return commonResult;
            }
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }
    }

    @GetMapping(value = "/updateProductCancle")
    @ApiOperation("产品取消")
    @ResponseBody
    public CommonResult updateProductCancle(@RequestParam("ids") Long id,
                                            @RequestParam("productStatus") Integer productStatus) {
        try {
                JSONObject jsonObject = this.urlUtil.callUrlByGet(this.microServiceConfig.getProductUrl() + "/updateProductCancle?id=" + id+"&productStatus="+productStatus);
                CommonResult commonResult = JSONObject.parseObject(jsonObject.toJSONString(), CommonResult.class);
                return commonResult;
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }
    }
}
