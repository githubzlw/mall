package com.macro.mall.portal.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.OmsCartItem;
import com.macro.mall.model.PmsSkuStock;
import com.macro.mall.portal.domain.CartProduct;
import com.macro.mall.portal.domain.CartPromotionItem;
import com.macro.mall.portal.service.OmsCartItemService;
import com.macro.mall.portal.service.PmsPortalProductService;
import com.macro.mall.portal.service.UmsMemberService;
import com.macro.mall.portal.util.BigDecimalUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 购物车管理Controller
 * Created by macro on 2018/8/2.
 */
@Controller
@Api(tags = "OmsCartItemController", description = "购物车管理")
@RequestMapping("/cart")
public class OmsCartItemController {
    @Autowired
    private OmsCartItemService cartItemService;
    @Autowired
    private UmsMemberService memberService;
     @Autowired
    private PmsPortalProductService portalProductService;

    @ApiOperation("添加商品到购物车")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult add(@RequestBody OmsCartItem cartItem) {
        int count = cartItemService.add(cartItem);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @ApiOperation("获取某个会员的购物车列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<OmsCartItem>> list() {
        List<OmsCartItem> cartItemList = cartItemService.list(memberService.getCurrentMember().getId());
        if (CollectionUtil.isNotEmpty(cartItemList)) {
            Map<String, OmsCartItem> clMap = new HashMap<>();
            List<Long> productIdList = new ArrayList<>();
            List<Long> skuIdList = new ArrayList<>();
            cartItemList.forEach(e -> {
                clMap.put(e.getProductId().toString() + "_" + e.getProductSkuId().toString(), e);
                productIdList.add(e.getProductId());
                skuIdList.add(e.getProductSkuId());
            });
            List<PmsSkuStock> skuStockList = portalProductService.queryByProductAndIds(productIdList, skuIdList);
            if(CollectionUtil.isNotEmpty(skuStockList)){
                skuStockList.forEach(e->{
                    if(clMap.containsKey(e.getProductId().toString() + "_" + e.getId().toString())){
                        JSONObject param = new JSONObject();
                        param.put("maxMoq", e.getMaxMoq());
                        param.put("maxPrice", e.getMaxPrice());
                        param.put("minMoq",e.getMinMoq());
                        param.put("minPrice", e.getMinPrice());
                        param.put("moq", e.getMoq());
                        param.put("price", e.getPrice());
                        param.put("volume", e.getVolume());
                        param.put("weight", e.getWeight());
                        param.put("volumeHeight", e.getVolumeHeight());
                        param.put("volumeLenght", e.getVolumeLenght());
                        param.put("volumeWidth", e.getVolumeWidth());
                        clMap.get(e.getProductId().toString() + "_" + e.getId().toString()).setMoqInfo(param.toJSONString());
                    }
                });
            }
        }
        return CommonResult.success(cartItemList);
    }

    @ApiOperation("获取某个会员的购物车列表,包括促销信息")
    @RequestMapping(value = "/list/promotion", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<CartPromotionItem>> listPromotion(@RequestParam(required = false) List<Long> cartIds) {
        List<CartPromotionItem> cartPromotionItemList = cartItemService.listPromotion(memberService.getCurrentMember().getId(), cartIds);
        return CommonResult.success(cartPromotionItemList);
    }

    @ApiOperation("修改购物车中某个商品的数量")
    @RequestMapping(value = "/update/quantity", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult updateQuantity(@RequestParam Long id,
                                       @RequestParam Integer quantity) {
        int count = cartItemService.updateQuantity(id, memberService.getCurrentMember().getId(), quantity);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @ApiOperation("获取购物车中某个商品的规格,用于重选规格")
    @RequestMapping(value = "/getProduct/{productId}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CartProduct> getCartProduct(@PathVariable Long productId) {
        CartProduct cartProduct = cartItemService.getCartProduct(productId);
        return CommonResult.success(cartProduct);
    }

    @ApiOperation("修改购物车中商品的规格")
    @RequestMapping(value = "/update/attr", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updateAttr(@RequestBody OmsCartItem cartItem) {
        int count = cartItemService.updateAttr(cartItem);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }


    @ApiOperation("选中购物车中的某个商品")
    @RequestMapping(value = "/selectProducts", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult selectProducts(@RequestParam("ids") List<Long> ids, @RequestParam("checkFlag") Integer checkFlag) {
        Assert.isTrue(null != checkFlag && checkFlag >= 0, "checkFlag null");
        int count = cartItemService.selectProducts(memberService.getCurrentMember().getId(), ids, checkFlag);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @ApiOperation("删除购物车中的某个商品")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult delete(@RequestParam("ids") List<Long> ids) {
        int count = cartItemService.delete(memberService.getCurrentMember().getId(), ids);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @ApiOperation("清空购物车")
    @RequestMapping(value = "/clear", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult clear() {
        int count = cartItemService.clear(memberService.getCurrentMember().getId());
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }
}
