package com.macro.mall.portal.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.portal.domain.SiteSourcing;
import com.macro.mall.portal.util.SourcingUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.macro.mall.portal.buyforme
 * @date:2021-04-15
 */
@Api(tags = "XmsAddProductByUrlController", description = "AddProductByUrl的调用接口")
@RestController
@Slf4j
@RequestMapping("/addProductByUrl")
public class XmsAddProductByUrlController {

    @Autowired
    private SourcingUtils sourcingUtils;

    @ApiOperation(value = "BuyForMe的购物车List", notes = "BuyForMe逻辑")
    @GetMapping("/queryCartList")
    public CommonResult queryBfmCartList(String userId, Integer siteFlag) {
        Assert.isTrue(StrUtil.isNotBlank(userId), "userId null");
        Assert.isTrue(null != siteFlag && siteFlag > 0, "siteFlag null");
        try {
            return CommonResult.success(this.sourcingUtils.getCarFromRedis(userId, siteFlag, SourcingUtils.USER_NAME_ADD_PRODUCT_BY_URL));
        } catch (Exception e) {
            e.printStackTrace();
            log.error("queryBfmCartList,userId:[{}],error:", userId, e);
            return CommonResult.failed(e.getMessage());
        }
    }



    @ApiOperation(value = "根据万邦接口获取URL详细信息", notes = "BuyForMe逻辑")
    @GetMapping("/getInfoByUrl")
    public CommonResult getInfoByUrl(String url, String userId, Integer siteFlag) {
        Assert.isTrue(StrUtil.isNotBlank(url), "url null");
        Assert.isTrue(StrUtil.isNotBlank(userId), "userId null");
        Assert.isTrue(null != siteFlag && siteFlag > 0, "siteFlag null");
        try {

            SiteSourcing siteSourcing = new SiteSourcing();
            siteSourcing.setUrl(url);
            // 生成PID和catid数据
            this.sourcingUtils.checkSiteFlagByUrl(siteSourcing);

            JSONObject jsonObject = this.sourcingUtils.checkAndLoadData(siteSourcing);
            // 添加到购物车
            siteSourcing.setUserId(Long.parseLong(userId));
            siteSourcing.setUserName(SourcingUtils.USER_NAME_ADD_PRODUCT_BY_URL);
            siteSourcing.setSiteFlag(siteFlag);
            this.sourcingUtils.addBfmCart(siteSourcing);
            return CommonResult.success(siteSourcing);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("getInfoByUrl,url[{}],error:", url, e);
            return CommonResult.failed(e.getMessage());
        }
    }



    @ApiOperation(value = "删除sourcing前的缓存信息", notes = "BuyForMe逻辑")
    @PostMapping("/deleteCart")
    public CommonResult deleteBfmCart(String pid, String userId, Integer siteFlag) {
        Assert.isTrue(StrUtil.isNotBlank(pid), "pid null");
        Assert.isTrue(null != siteFlag && siteFlag > 0, "siteFlag null");
        try {

            SiteSourcing siteSourcing = new SiteSourcing();
            siteSourcing.setPid(pid);
            siteSourcing.setSiteFlag(siteFlag);
            siteSourcing.setUserId(Long.parseLong(userId));
            siteSourcing.setUserName(SourcingUtils.USER_NAME_ADD_PRODUCT_BY_URL);
            this.sourcingUtils.deleteBfmCart(siteSourcing);
            return CommonResult.success(siteSourcing);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("saveImg,pid[{}],siteFlag[{}],error:", pid, siteFlag, e);
            return CommonResult.failed(e.getMessage());
        }
    }


}
