package com.macro.mall.portal.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.common.util.UrlUtil;
import com.macro.mall.model.UmsMember;
import com.macro.mall.portal.config.MicroServiceConfig;
import com.macro.mall.portal.domain.SiteSourcing;
import com.macro.mall.portal.domain.SiteSourcingParam;
import com.macro.mall.portal.service.UmsMemberService;
import com.macro.mall.portal.util.SourcingUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.macro.mall.portal.buyforme
 * @date:2021-04-15
 */
@Api(tags = "XmsBuyForMeController", description = "buyForMe的调用接口")
@RestController
@Slf4j
@RequestMapping("/buyForMe")
public class XmsBuyForMeController {

    private final SourcingUtils sourcingUtils;

    private final UmsMemberService umsMemberService;

    @Autowired
    private MicroServiceConfig microServiceConfig;
    private UrlUtil instance = UrlUtil.getInstance();

    @Autowired
    public XmsBuyForMeController(SourcingUtils sourcingUtils, UmsMemberService umsMemberService) {
        this.sourcingUtils = sourcingUtils;
        this.umsMemberService = umsMemberService;
    }


    @ApiOperation(value = "根据URL加入购物车", notes = "BuyForMe逻辑")
    @PostMapping("/addBfmCartByUrl")
    public CommonResult addBfmCartByUrl(SiteSourcingParam siteSourcingParam) {
        Assert.notNull(siteSourcingParam, "siteBuyForMeParam null");
        Assert.isTrue(StrUtil.isNotBlank(siteSourcingParam.getUrl()), "url null");

        Assert.isTrue((null != siteSourcingParam.getAverageDailyOrder() && siteSourcingParam.getAverageDailyOrder() > 0) || (null != siteSourcingParam.getOneTimeOrderOnly() && siteSourcingParam.getOneTimeOrderOnly() > 0), "averageDailyOrder or oneTimeOrderOnly null");

        SiteSourcing siteSourcing = new SiteSourcing();
        BeanUtil.copyProperties(siteSourcingParam, siteSourcing);

        try {
            UmsMember currentMember = this.umsMemberService.getCurrentMember();
            siteSourcing.setUserId(currentMember.getId());
            siteSourcing.setUserName(currentMember.getUsername());
            //siteBuyForMe.setUserName("1071083166@qq.com");
            // 生成PID和catid数据
            this.sourcingUtils.checkSiteFlagByUrl(siteSourcing);

            Long productId = this.saveOneBoundToProduct(siteSourcing);
            if (null == productId || productId <= 0) {
                return CommonResult.failed("before add product failed!");
            }
            siteSourcing.setProductId(productId);

            // 异步加载数据
            this.sourcingUtils.checkAndLoadDataAsync(siteSourcing);

            // 清空redis数据
            this.sourcingUtils.deleteRedisCar(currentMember.getId());

            return CommonResult.success(siteSourcing);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("addBfmCartByUrl,siteBuyForMe[{}],error:", siteSourcing, e);
            return CommonResult.failed(e.getMessage());
        }
    }

    @ApiOperation(value = "根据URL加入购物车WithUuid", notes = "BuyForMe逻辑")
    @PostMapping("/addBfmCartByUrlWithUuid")
    public CommonResult addBfmCartByUrlWithUuid(SiteSourcingParam siteSourcingParam) {
        Assert.notNull(siteSourcingParam, "siteBuyForMeParam null");
        Assert.isTrue(StrUtil.isNotBlank(siteSourcingParam.getUrl()), "url null");

        Assert.isTrue((null != siteSourcingParam.getAverageDailyOrder() && siteSourcingParam.getAverageDailyOrder() > 0) || (null != siteSourcingParam.getOneTimeOrderOnly() && siteSourcingParam.getOneTimeOrderOnly() > 0), "averageDailyOrder or oneTimeOrderOnly null");
        Assert.isTrue(StrUtil.isNotBlank(siteSourcingParam.getUuid()), "uuid null");

        SiteSourcing siteSourcing = new SiteSourcing();
        BeanUtil.copyProperties(siteSourcingParam, siteSourcing);

        try {
            siteSourcing.setUserName(siteSourcingParam.getUuid());
            // 生成PID和catid数据
            this.sourcingUtils.checkSiteFlagByUrl(siteSourcing);

            Long productId = this.saveOneBoundToProduct(siteSourcing);
            if (null == productId || productId <= 0) {
                return CommonResult.failed("before add product failed!");
            }
            siteSourcing.setProductId(productId);

            // 异步加载数据
            this.sourcingUtils.checkAndLoadDataAsync(siteSourcing);

            // 清空redis数据
            this.sourcingUtils.deleteRedisCar(siteSourcingParam.getUuid());

            return CommonResult.success(siteSourcing);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("addBfmCartByUrlWithUuid,siteBuyForMe[{}],error:", siteSourcing, e);
            return CommonResult.failed(e.getMessage());
        }
    }


    @ApiOperation(value = "根据Img加入购物车", notes = "BuyForMe逻辑")
    @PostMapping("/addBfmCartByImg")
    public CommonResult addBfmCartByImg(SiteSourcingParam siteSourcingParam) {


        Assert.notNull(siteSourcingParam, "siteBuyForMeParam null");
        Assert.isTrue(StrUtil.isNotBlank(siteSourcingParam.getImg()), "img null");
        Assert.isTrue(null != siteSourcingParam.getChooseType() && siteSourcingParam.getChooseType() > 0, "chooseType null");

        Assert.isTrue((null != siteSourcingParam.getAverageDailyOrder() && siteSourcingParam.getAverageDailyOrder() > 0) || (null != siteSourcingParam.getOneTimeOrderOnly() && siteSourcingParam.getOneTimeOrderOnly() > 0), "averageDailyOrder or oneTimeOrderOnly null");

        SiteSourcing siteSourcing = new SiteSourcing();
        BeanUtil.copyProperties(siteSourcingParam, siteSourcing);

        try {
            // 生成PID和catid数据
            this.sourcingUtils.checkSiteFlagByImg(siteSourcing);

            UmsMember currentMember = this.umsMemberService.getCurrentMember();
            siteSourcing.setUserId(currentMember.getId());
            siteSourcing.setUserName(currentMember.getUsername());
            // 添加到购物车
            // sourcingUtils.addBfmCart(siteSourcing);

            Long productId = this.saveOneBoundToProduct(siteSourcing);
            if (null == productId || productId <= 0) {
                return CommonResult.failed("before add product failed!");
            }
            siteSourcing.setProductId(productId);

            // 添加到sourcingList
            this.sourcingUtils.saveSourcingInfo(siteSourcing);

            // 清空redis数据
            this.sourcingUtils.deleteRedisCar(currentMember.getId());

            return CommonResult.success(siteSourcing);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("addBfmCartByImg,siteBuyForMeParam[{}],error:", siteSourcingParam, e);
            return CommonResult.failed(e.getMessage());
        }
    }


    @ApiOperation(value = "根据Img加入购物车WithUuid", notes = "BuyForMe逻辑")
    @PostMapping("/addBfmCartByImgWithUuid")
    public CommonResult addBfmCartByImgWithUuid(SiteSourcingParam siteSourcingParam) {


        Assert.notNull(siteSourcingParam, "siteBuyForMeParam null");
        Assert.isTrue(StrUtil.isNotBlank(siteSourcingParam.getImg()), "img null");
        Assert.isTrue(null != siteSourcingParam.getChooseType() && siteSourcingParam.getChooseType() > 0, "chooseType null");

        Assert.isTrue((null != siteSourcingParam.getAverageDailyOrder() && siteSourcingParam.getAverageDailyOrder() > 0) || (null != siteSourcingParam.getOneTimeOrderOnly() && siteSourcingParam.getOneTimeOrderOnly() > 0), "averageDailyOrder or oneTimeOrderOnly null");

        Assert.isTrue(StrUtil.isNotBlank(siteSourcingParam.getUuid()), "uuid null");
        SiteSourcing siteSourcing = new SiteSourcing();
        BeanUtil.copyProperties(siteSourcingParam, siteSourcing);

        try {
            // 生成PID和catid数据
            this.sourcingUtils.checkSiteFlagByImg(siteSourcing);

            siteSourcing.setUserName(siteSourcingParam.getUuid());
            // 添加到购物车
            // sourcingUtils.addBfmCart(siteSourcing);

            Long productId = this.saveOneBoundToProduct(siteSourcing);
            if (null == productId || productId <= 0) {
                return CommonResult.failed("before add product failed!");
            }
            siteSourcing.setProductId(productId);

            // 添加到sourcingList
            this.sourcingUtils.saveSourcingInfo(siteSourcing);

            // 清空redis数据
            this.sourcingUtils.deleteRedisCar(siteSourcingParam.getUuid());

            return CommonResult.success(siteSourcing);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("addBfmCartByImgWithUuid,siteBuyForMeParam[{}],error:", siteSourcingParam, e);
            return CommonResult.failed(e.getMessage());
        }
    }


    @ApiOperation(value = "BuyForMe的购物车List", notes = "BuyForMe逻辑")
    @GetMapping("/queryBfmCartList")
    public CommonResult queryBfmCartList() {
        String userId = String.valueOf(umsMemberService.getCurrentMember().getId());
        try {
            return CommonResult.success(this.sourcingUtils.getCarFromRedis(userId, 0 , null));
        } catch (Exception e) {
            e.printStackTrace();
            log.error("queryBfmCartList,userId:[{}],error:", userId, e);
            return CommonResult.failed(e.getMessage());
        }
    }



    @ApiOperation(value = "BuyForMe的购物车ListWithUuid", notes = "BuyForMe逻辑")
    @GetMapping("/queryBfmCartListWithUuid")
    public CommonResult queryBfmCartListWithUuid(String uuid) {
        Assert.isTrue(StrUtil.isNotBlank(uuid), "uuid null");
        try {
            return CommonResult.success(this.sourcingUtils.getCarFromRedis(uuid, 0 , null));
        } catch (Exception e) {
            e.printStackTrace();
            log.error("queryBfmCartList,uuid:[{}],error:", uuid, e);
            return CommonResult.failed(e.getMessage());
        }
    }


    @ApiOperation(value = "BuyForMe上传图片", notes = "BuyForMe逻辑")
    @PostMapping("/uploadImg")
    public CommonResult uploadImg(HttpServletRequest request, @RequestParam("file") MultipartFile file) {
        try {
            String uploadImg = sourcingUtils.saveUploadImg(request, file);
            if (StrUtil.isNotEmpty(uploadImg)) {
                // 放入redis中，做成PID数据
                return CommonResult.success(uploadImg);
            }
            return CommonResult.failed("upload error!");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("uploadImg,error:", e);
            return CommonResult.failed("upload error!");
        }
    }


    @ApiOperation(value = "根据万邦接口获取URL详细信息", notes = "BuyForMe逻辑")
    @GetMapping("/getInfoByUrl")
    public CommonResult getInfoByUrl(String url) {
        Assert.isTrue(StrUtil.isNotBlank(url), "url null");
        UmsMember currentMember = umsMemberService.getCurrentMember();
        try {

            SiteSourcing siteSourcing = new SiteSourcing();
            siteSourcing.setUrl(url);
            // 生成PID和catid数据
            this.sourcingUtils.checkSiteFlagByUrl(siteSourcing);

            JSONObject jsonObject = this.sourcingUtils.checkAndLoadData(siteSourcing);
            /*if(siteSourcing.getSiteFlag() > 0 && siteSourcing.getSiteFlag() <= 3 && jsonObject.size() == 0){
                return CommonResult.failed("get data failed");
            }*/
            // 添加到购物车
            siteSourcing.setUserId(currentMember.getId());
            siteSourcing.setUserName(currentMember.getUsername());
            this.sourcingUtils.addBfmCart(siteSourcing);
            return CommonResult.success(siteSourcing);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("getInfoByUrl,url[{}],error:", url, e);
            return CommonResult.failed(e.getMessage());
        }
    }

    @ApiOperation(value = "根据万邦接口获取URL详细信息WithUuid", notes = "BuyForMe逻辑")
    @GetMapping("/getInfoByUrlWithUuid")
    public CommonResult getInfoByUrlWithUuid(String url, String userName) {
        Assert.isTrue(StrUtil.isNotBlank(url), "url null");
        Assert.isTrue(StrUtil.isNotBlank(userName), "userName null");
        try {

            SiteSourcing siteSourcing = new SiteSourcing();
            siteSourcing.setUrl(url);
            // 生成PID和catid数据
            this.sourcingUtils.checkSiteFlagByUrl(siteSourcing);
            this.sourcingUtils.checkAndLoadData(siteSourcing);
            // 添加到购物车
            siteSourcing.setUserName(userName);
            this.sourcingUtils.addBfmCart(siteSourcing);
            return CommonResult.success(siteSourcing);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("getInfoByUrl,url[{}],error:", url, e);
            return CommonResult.failed(e.getMessage());
        }
    }


    @ApiOperation(value = "缓存img信息", notes = "BuyForMe逻辑")
    @PostMapping("/saveImg")
    public CommonResult saveImg(String title, String img) {
        Assert.isTrue(StrUtil.isNotBlank(title), "title null");
        Assert.isTrue(StrUtil.isNotBlank(img), "img null");
        try {

            UmsMember currentMember = umsMemberService.getCurrentMember();
            SiteSourcing siteSourcing = new SiteSourcing();
            siteSourcing.setName(title);
            siteSourcing.setImg(img);
            sourcingUtils.checkSiteFlagByImg(siteSourcing);
            // 添加到购物车
            siteSourcing.setUserId(currentMember.getId());
            siteSourcing.setUserName(currentMember.getUsername());
            this.sourcingUtils.addBfmCart(siteSourcing);
            return CommonResult.success(siteSourcing);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("saveImg,title[{}],img[{}],error:", title, img, e);
            return CommonResult.failed(e.getMessage());
        }
    }

    @ApiOperation(value = "删除sourcing前的缓存信息", notes = "BuyForMe逻辑")
    @PostMapping("/deleteBfmCart")
    public CommonResult deleteBfmCart(String pid, Integer siteFlag) {
        Assert.isTrue(StrUtil.isNotBlank(pid), "pid null");
        Assert.isTrue(null != siteFlag && siteFlag > 0, "siteFlag null");
        try {

            UmsMember currentMember = umsMemberService.getCurrentMember();
            SiteSourcing siteSourcing = new SiteSourcing();
            siteSourcing.setPid(pid);
            siteSourcing.setSiteFlag(siteFlag);
            siteSourcing.setUserId(currentMember.getId());
            siteSourcing.setUserName(currentMember.getUsername());
            this.sourcingUtils.deleteBfmCart(siteSourcing);
            return CommonResult.success(siteSourcing);
            // return CommonResult.success(this.sourcingUtils.getCarFromRedis(String.valueOf(currentMember.getId())));
        } catch (Exception e) {
            e.printStackTrace();
            log.error("deleteBfmCart,pid[{}],siteFlag[{}],error:", pid, siteFlag, e);
            return CommonResult.failed(e.getMessage());
        }
    }

    @ApiOperation(value = "删除sourcing前的缓存信息WithUuid", notes = "BuyForMe逻辑")
    @PostMapping("/deleteBfmCartWithUuid")
    public CommonResult deleteBfmCartWithUuid(String pid, Integer siteFlag, String uuid) {
        Assert.isTrue(StrUtil.isNotBlank(pid), "pid null");
        Assert.isTrue(null != siteFlag && siteFlag > 0, "siteFlag null");
        Assert.isTrue(StrUtil.isNotBlank(uuid), "uuid null");
        try {

            SiteSourcing siteSourcing = new SiteSourcing();
            siteSourcing.setPid(pid);
            siteSourcing.setSiteFlag(siteFlag);
            siteSourcing.setUserName(uuid);
            this.sourcingUtils.deleteBfmCart(siteSourcing);
            return CommonResult.success(siteSourcing);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("deleteBfmCartWithUuid,pid[{}],siteFlag[{}],error:", pid, siteFlag, e);
            return CommonResult.failed(e.getMessage());
        }
    }

    @ApiOperation(value = "速卖通搜索", notes = "BuyForMe逻辑")
    @PostMapping("/aliSearch")
    public CommonResult aliSearch(String keyword, Integer page) {
        Assert.isTrue(StrUtil.isNotBlank(keyword), "keyword null");
        Assert.isTrue(null != page && page > 0, "page null");
        try {
            Map<String, String> requestMap = new HashMap<>();
            requestMap.put("keyword", keyword);
            requestMap.put("page", String.valueOf(page));

            JSONObject jsonObject = instance.postURL(microServiceConfig.getOneBoundApi() + "/aliSearch", requestMap);
            if (null == jsonObject || jsonObject.size() == 0) {
                jsonObject = instance.postURL(microServiceConfig.getOneBoundApi() + "/aliSearch", requestMap);
            }
            if (null != jsonObject && jsonObject.containsKey("code") && jsonObject.getInteger("code") == 200) {
                JSONObject dataJson = jsonObject.getJSONObject("data");
                return CommonResult.success(dataJson);
            } else {
                return CommonResult.failed(null == jsonObject ? "get data error" : jsonObject.toJSONString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("aliSearch,keyword[{}],page[{}],error:", keyword, page, e);
            return CommonResult.failed(e.getMessage());
        }
    }



    private Long saveOneBoundToProduct(SiteSourcing siteSourcing) throws IOException {
        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("pid", siteSourcing.getPid());
        requestMap.put("siteFlag", String.valueOf(siteSourcing.getSiteFlag()));
        requestMap.put("url", siteSourcing.getUrl());
        requestMap.put("name", siteSourcing.getName());
        requestMap.put("img", siteSourcing.getImg());
        requestMap.put("oneTimeOrderOnly", String.valueOf(siteSourcing.getOneTimeOrderOnly()));
        requestMap.put("chooseType", String.valueOf(siteSourcing.getChooseType()));
        requestMap.put("typeOfShipping", String.valueOf(siteSourcing.getTypeOfShipping()));
        requestMap.put("countryName", siteSourcing.getCountryName());
        requestMap.put("stateName", siteSourcing.getStateName());
        requestMap.put("customType", siteSourcing.getCustomType());
        requestMap.put("cifPort", siteSourcing.getCifPort());
        requestMap.put("fbaWarehouse", siteSourcing.getFbaWarehouse());

        String resUrl = microServiceConfig.getProductUrl() + "/saveOneBoundProduct";
        JSONObject jsonObject = instance.postURL(resUrl, requestMap);
        if (jsonObject.containsKey("code") && 200 == jsonObject.getIntValue("code")) {
            return jsonObject.getLong("data");
        } else {
            System.err.println(jsonObject);
            return null;
        }
    }

}
