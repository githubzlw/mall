package com.macro.mall.portal.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.entity.XmsShopifyPidInfo;
import com.macro.mall.entity.XmsSourcingList;
import com.macro.mall.model.PmsProduct;
import com.macro.mall.model.UmsMember;
import com.macro.mall.portal.domain.PmsPortalProductDetail;
import com.macro.mall.portal.domain.PmsProductCategoryNode;
import com.macro.mall.portal.service.IXmsShopifyPidInfoService;
import com.macro.mall.portal.service.IXmsSourcingListService;
import com.macro.mall.portal.service.PmsPortalProductService;
import com.macro.mall.portal.service.UmsMemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 前台商品管理Controller
 * Created by macro on 2020/4/6.
 */
@Controller
@Api(tags = "PmsPortalProductController", description = "前台商品管理")
@RequestMapping("/product")
@Slf4j
public class PmsPortalProductController {

    @Autowired
    private PmsPortalProductService portalProductService;
    @Autowired
    private UmsMemberService umsMemberService;
    @Autowired
    private IXmsSourcingListService xmsSourcingListService;
    @Autowired
    private IXmsShopifyPidInfoService xmsShopifyPidInfoService;

    @ApiOperation(value = "综合搜索、筛选、排序")
    @ApiImplicitParam(name = "sort", value = "排序字段:0->按相关度；1->按新品；2->按销量；3->价格从低到高；4->价格从高到低",
            defaultValue = "0", allowableValues = "0,1,2,3,4", paramType = "query", dataType = "integer")
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<PmsProduct>> search(@RequestParam(required = false) String keyword,
                                                       @RequestParam(required = false) Long brandId,
                                                       @RequestParam(required = false) Long productCategoryId,
                                                       @RequestParam(required = false, defaultValue = "0") Integer pageNum,
                                                       @RequestParam(required = false, defaultValue = "5") Integer pageSize,
                                                       @RequestParam(required = false, defaultValue = "0") Integer sort) {
        List<PmsProduct> productList = portalProductService.search(keyword, brandId, productCategoryId, pageNum, pageSize, sort);
        return CommonResult.success(CommonPage.restPage(productList));
    }

    @ApiOperation("以树形结构获取所有商品分类")
    @RequestMapping(value = "/categoryTreeList", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<PmsProductCategoryNode>> categoryTreeList() {
        List<PmsProductCategoryNode> list = portalProductService.categoryTreeList();
        return CommonResult.success(list);
    }

    @ApiOperation("获取前台商品详情")
    @RequestMapping(value = "/detail/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<PmsPortalProductDetail> detail(@PathVariable Long id) {
        PmsPortalProductDetail productDetail = portalProductService.detail(id);
        return CommonResult.success(productDetail);
    }


    @ApiOperation("获取公用的商品数据")
    @RequestMapping(value = "/getPublicProduct", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<PmsProduct>> getPublicProduct(@RequestParam(required = false, defaultValue = "1") Integer pageNum,
                                                                 @RequestParam(required = false, defaultValue = "20") Integer pageSize,
                                                                 @RequestParam(required = false) String title,
                                                                 @RequestParam(required = false, defaultValue = "001") String time) {
        try {
            UmsMember currentMember = this.umsMemberService.getCurrentMember();
            List<PmsProduct> productList = this.portalProductService.getPublicProduct(pageNum, pageSize, title);
            if (CollectionUtil.isNotEmpty(productList)) {
                List<Long> collect = productList.stream().mapToLong(PmsProduct::getId).boxed().collect(Collectors.toList());
                QueryWrapper<XmsShopifyPidInfo> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(XmsShopifyPidInfo::getShopifyName, currentMember.getShopifyName())
                        .in(XmsShopifyPidInfo::getMemberId, currentMember.getId())
                        .in(XmsShopifyPidInfo::getPid, collect);
                List<XmsShopifyPidInfo> list = this.xmsShopifyPidInfoService.list(queryWrapper);
                List<String> pidList = list.stream().map(XmsShopifyPidInfo::getPid).collect(Collectors.toList());

                queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().in(XmsShopifyPidInfo::getPid, collect);
                list = this.xmsShopifyPidInfoService.list(queryWrapper);

                Map<String, List<XmsShopifyPidInfo>> pidMap = new HashMap<>();
                if(CollectionUtil.isNotEmpty(list)){
                    pidMap = list.stream().collect(Collectors.groupingBy(XmsShopifyPidInfo::getPid));
                }

                Map<String, List<XmsShopifyPidInfo>> finalPidMap = pidMap;
                productList.forEach(e -> {
                    String tempId = String.valueOf(e.getId());
                    // note是1的表示已经导入到shopify，0未导入
                    if (pidList.contains(tempId)) {
                        e.setNote("1");
                    } else {
                        e.setNote("0");
                    }
                    if(StrUtil.isBlank(e.getMoq())){
                        e.setMoq("1");
                    }
                    // lowStock  里面的数量标识已经同步的数量
                    if(finalPidMap.containsKey(tempId)){
                        e.setLowStock(finalPidMap.get(tempId).size());
                    } else{
                        e.setLowStock(0);
                    }
                });
                pidMap.clear();
                list.clear();
                collect.clear();
                pidList.clear();
            }
            return CommonResult.success(CommonPage.restPage(productList));
        } catch (Exception e) {
            e.printStackTrace();
            log.error("getPublicProduct, title:[{}],pageNum:[{}],pageSize:[{}]", title, pageNum, pageSize, e);
            return CommonResult.failed("getPublicProduct error");
        }

    }

}
