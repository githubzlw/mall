package com.macro.mall.portal.controller;

import cn.hutool.core.util.StrUtil;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.common.util.UrlUtil;
import com.macro.mall.model.UmsMember;
import com.macro.mall.portal.service.UmsMemberService;
import com.macro.mall.portal.util.SourcingUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

/**
 * 会员登录注册管理Controller
 * Created by macro on 2018/8/3.
 */
@Controller
@Api(tags = "UmsMemberController", description = "会员登录注册管理")
@RequestMapping("/sso")
public class UmsMemberController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UmsMemberController.class);

    @Value("${jwt.tokenHeader}")
    private String tokenHeader;
    @Value("${jwt.tokenHead}")
    private String tokenHead;
    @Value("${tpurl.tpLogin}")
    public String tpLogin;
    @Autowired
    private UmsMemberService memberService;

    @Autowired
    private SourcingUtils sourcingUtils;

    @ApiOperation("会员注册")
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult register(@RequestParam String username,
                                 @RequestParam String password,
                                 @RequestParam String organizationname,
                                 @RequestParam String monthlyOrders) {
        memberService.register(username, password, organizationname, monthlyOrders, 0);
        String token = memberService.login(username, password);
        if (token == null) {
            return CommonResult.validateFailed("用户名或密码错误");
        }
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", token);
        tokenMap.put("tokenHead", tokenHead);
        return CommonResult.success(tokenMap);
        //return CommonResult.success(null,"注册成功");
    }

    @ApiOperation("会员登录")
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult login(@RequestParam String usernamez,
                              @RequestParam String passwordz, @RequestParam String uuid) {
        String token = memberService.login(usernamez, passwordz);
        if (token == null) {
            return CommonResult.validateFailed("用户名或密码错误");
        }
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", token);

        tokenMap.put("tokenHead", tokenHead);
        tokenMap.put("mail", usernamez);

        // 整合sourcing数据
        if (StrUtil.isNotEmpty(uuid)) {
            this.sourcingUtils.mergeSourcingList(memberService.getCurrentMember(), uuid);
        }
        tokenMap.put("guidedFlag", String.valueOf(memberService.getCurrentMember().getGuidedFlag()));
        return CommonResult.success(tokenMap);
    }

    @ApiOperation("获取会员信息")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult info(Principal principal) {
        if (principal == null) {
            return CommonResult.unauthorized(null);
        }
        UmsMember member = memberService.getCurrentMember();
        return CommonResult.success(member);
    }

    @ApiOperation("获取验证码")
    @RequestMapping(value = "/getAuthCode", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult getAuthCode(@RequestParam String telephone) {
        String authCode = memberService.generateAuthCode(telephone);
        return CommonResult.success(authCode, "获取验证码成功");
    }

    @ApiOperation("修改密码")
    @RequestMapping(value = "/updatePassword", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updatePassword(@RequestParam String telephone,
                                       @RequestParam String password,
                                       @RequestParam String authCode) {
        memberService.updatePassword(telephone, password, authCode);
        return CommonResult.success(null, "密码修改成功");
    }


    @ApiOperation(value = "刷新token")
    @RequestMapping(value = "/refreshToken", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult refreshToken(HttpServletRequest request) {
        String token = request.getHeader(tokenHeader);
        String refreshToken = memberService.refreshToken(token);
        if (refreshToken == null) {
            return CommonResult.failed("token已经过期！");
        }
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", refreshToken);
        tokenMap.put("tokenHead", tokenHead);
        return CommonResult.success(tokenMap);
    }

    @ApiOperation("google登录")
    @RequestMapping(value = "/googleLogin", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult googleAuth(@RequestParam String idtokenstr) {

        LOGGER.info("google login begin");
        ImmutablePair<String, String> pair = null;
        try {
            pair = memberService.googleAuth(idtokenstr);
            memberService.register(pair.getRight(), pair.getRight(), "", "", 1);

            String token = memberService.login(pair.getRight(), pair.getRight());
            if (token == null) {
                return CommonResult.validateFailed("用户名或密码错误");
            }
            Map<String, String> tokenMap = new HashMap<>();
            tokenMap.put("token", token);
            tokenMap.put("tokenHead", tokenHead);
            return CommonResult.success(tokenMap);
        } catch (Exception e) {
            LOGGER.error("googleAuth", e);
        }
        return CommonResult.success(null, "成功");
    }

    @ApiOperation("facebook登录")
    @RequestMapping(value = "/facebookLogin", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult facebookAuth(@RequestParam String idtokenstr) {

        LOGGER.info("facebook login begin");
        try {
            String email = UrlUtil.getInstance().facebookAuth(idtokenstr, tpLogin);
            memberService.register(email, "", "", "", 2);
        } catch (Exception e) {
            LOGGER.error("facebookLogin", e);
        }
        return CommonResult.success(null, "成功");
    }


    @ApiOperation("修改客户信息")
    @RequestMapping(value = "/updateUserInfo", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updatePassword(@RequestParam String niceName,
                                       @RequestParam String monthlyOrderQuantity) {
        int info = memberService.updateUserInfo(niceName, monthlyOrderQuantity);
        return CommonResult.success(info, "修改客户信息成功");
    }



    @ApiOperation("设置引导状态")
    @RequestMapping(value = "/setGuided", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult setGuided() {

        UmsMember member = memberService.getCurrentMember();
        try {
            int i = memberService.updateGuidedFlag(member.getId());
            return CommonResult.success(i);
        } catch (Exception e) {
            LOGGER.error("setGuided", e);
            return CommonResult.failed("setGuided failed");
        }
    }
}
