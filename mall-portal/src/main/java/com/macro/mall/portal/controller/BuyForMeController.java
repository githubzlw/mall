package com.macro.mall.portal.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.UmsMember;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.macro.mall.portal.buyforme
 * @date:2021-04-15
 */
@Api(tags = "BuyForMeController", description = "buyForMe的调用接口")
@RestController
@Slf4j
@RequestMapping("/buyForMe")
public class BuyForMeController {

    private final SourcingUtils sourcingUtils;

    private final UmsMemberService umsMemberService;

    @Autowired
    public BuyForMeController(SourcingUtils sourcingUtils, UmsMemberService umsMemberService) {
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
            UmsMember currentMember = umsMemberService.getCurrentMember();
            siteSourcing.setUserId(currentMember.getId());
            siteSourcing.setUserName(currentMember.getUsername());
            //siteBuyForMe.setUserName("1071083166@qq.com");
            // 生成PID和catid数据
            sourcingUtils.checkSiteFlagByUrl(siteSourcing);

            // 添加到购物车
            // sourcingUtils.addBfmCart(siteSourcing);

            // 异步加载数据
            sourcingUtils.checkAndLoadDataAsync(siteSourcing);

            // 清空redis数据
            sourcingUtils.deleteRedisCar(currentMember.getId());

            return CommonResult.success(siteSourcing);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("getInfoByUrl,siteBuyForMe[{}],error:", siteSourcing, e);
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
            sourcingUtils.checkSiteFlagByImg(siteSourcing);

            UmsMember currentMember = umsMemberService.getCurrentMember();
            siteSourcing.setUserId(currentMember.getId());
            siteSourcing.setUserName(currentMember.getUsername());
            // 添加到购物车
            // sourcingUtils.addBfmCart(siteSourcing); c

            // 添加到sourcingList
            sourcingUtils.saveSourcingInfo(siteSourcing);

            // 清空redis数据
            sourcingUtils.deleteRedisCar(currentMember.getId());

            return CommonResult.success(siteSourcing);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("getInfoByUrl,siteBuyForMeParam[{}],error:", siteSourcingParam, e);
            return CommonResult.failed(e.getMessage());
        }
    }


    @ApiOperation(value = "BuyForMe的购物车List", notes = "BuyForMe逻辑")
    @GetMapping("/queryBfmCartList")
    public CommonResult queryBfmCartList() {
        String userId = String.valueOf(umsMemberService.getCurrentMember().getId());
        try {

            Map<String, Object> objectMap = sourcingUtils.getCarToRedis(userId);
            if (null == objectMap) {
                objectMap = new HashMap<>();
            }

            List<SiteSourcing> carList = new ArrayList<>();
            objectMap.forEach((k, v) -> {
                SiteSourcing redisBuyForMe = JSONObject.parseObject(v.toString(), SiteSourcing.class);
                carList.add(redisBuyForMe);
            });
            return CommonResult.success(carList);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("queryBfmCartList,userId:[{}],error:", userId, e);
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
        try {
            SiteSourcing siteSourcing = new SiteSourcing();
            siteSourcing.setUrl(url);
            // 生成PID和catid数据
            this.sourcingUtils.checkSiteFlagByUrl(siteSourcing);

            JSONObject jsonObject = this.sourcingUtils.checkAndLoadData(siteSourcing);
            // 添加到购物车
            siteSourcing.setUserId(umsMemberService.getCurrentMember().getId());
            siteSourcing.setUserName(umsMemberService.getCurrentMember().getUsername());
            this.sourcingUtils.addBfmCart(siteSourcing);
            return CommonResult.success(jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("getInfoByUrl,url[{}],error:", url, e);
            return CommonResult.failed(e.getMessage());
        }
    }


    @ApiOperation(value = "缓存img信息", notes = "BuyForMe逻辑")
    @PostMapping("/saveImg")
    public CommonResult getInfoByUrl(String title, String img) {
        Assert.isTrue(StrUtil.isNotBlank(title), "title null");
        Assert.isTrue(StrUtil.isNotBlank(img), "img null");
        try {

            SiteSourcing siteSourcing = new SiteSourcing();
            siteSourcing.setName(title);
            siteSourcing.setImg(img);
            sourcingUtils.checkSiteFlagByImg(siteSourcing);
            // 添加到购物车
            siteSourcing.setUserId(umsMemberService.getCurrentMember().getId());
            siteSourcing.setUserName(umsMemberService.getCurrentMember().getUsername());
            this.sourcingUtils.addBfmCart(siteSourcing);
            return CommonResult.success(siteSourcing);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("saveImg,title[{}],img[{}],error:", title, img, e);
            return CommonResult.failed(e.getMessage());
        }
    }

}
