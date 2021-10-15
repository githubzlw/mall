package com.macro.mall.portal.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.common.enums.MailTemplateType;
import com.macro.mall.entity.XmsShopifyAuth;
import com.macro.mall.model.UmsMember;
import com.macro.mall.model.UmsMemberExample;
import com.macro.mall.portal.cache.RedisUtil;
import com.macro.mall.portal.config.MicroServiceConfig;
import com.macro.mall.portal.config.ShopifyConfig;
import com.macro.mall.portal.domain.FacebookPojo;
import com.macro.mall.portal.domain.MemberDetails;
import com.macro.mall.portal.service.IXmsShopifyAuthService;
import com.macro.mall.portal.service.UmsMemberService;
import com.macro.mall.portal.util.SourcingUtils;
import com.macro.mall.tools.bean.WelcomeMailTemplateBean;
import com.macro.mall.tools.service.EmailService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
    @Autowired
    private UmsMemberService memberService;

    @Autowired
    private SourcingUtils sourcingUtils;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private EmailService emailService;
    @Autowired
    private MicroServiceConfig microServiceConfig;
    @Autowired
    private IXmsShopifyAuthService xmsShopifyAuthService;

    @ApiOperation("会员注册")
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult register(@RequestParam String username,
                                 @RequestParam String password,
                                 @RequestParam String organizationname,
                                 @RequestParam String monthlyOrders, String uuid,
                                 @RequestParam Integer countryId) {
        this.memberService.register(username, password, organizationname, monthlyOrders, 0, countryId);
        String token = this.memberService.login(username, password);
        if (token == null) {
            return CommonResult.validateFailed("用户名或密码错误");
        }
        // 整合sourcing数据
        if (StrUtil.isNotEmpty(uuid) && uuid.length() > 10) {
            this.sourcingUtils.mergeSourcingList(this.memberService.getCurrentMember(), uuid);
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
        MemberDetails userinfo = (MemberDetails) memberService.loadUserByUsername(usernamez);
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", token);
        tokenMap.put("tokenHead", tokenHead);
        tokenMap.put("mail", usernamez);
        tokenMap.put("sourcingCountryId", String.valueOf(userinfo.getUmsMember().getSourcingCountryId()));

        memberService.updateSecurityContext();

        UmsMember currentMember = memberService.getById(userinfo.getUmsMember().getId());
        // 整合sourcing数据
        if (StrUtil.isNotEmpty(uuid) && uuid.length() > 10) {
            this.sourcingUtils.mergeSourcingList(currentMember, uuid);
        }
        tokenMap.put("nickName", currentMember.getNickname());
        tokenMap.put("guidedFlag", String.valueOf(currentMember.getGuidedFlag()));
        tokenMap.put("logoUrl", currentMember.getLogoUrl());
        return CommonResult.success(tokenMap);
    }


    @ApiOperation("会员Shopify登录")
    @RequestMapping(value = "/loginWithShopify", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult loginWithShopify(@RequestParam String username, @RequestParam String password,
                                         @RequestParam String shopifyName, @RequestParam String uuid) {
        Assert.isTrue(StrUtil.isNotBlank(username), "username null");
        //Assert.isTrue(StrUtil.isNotBlank(password),"password null");
        Assert.isTrue(StrUtil.isNotBlank(shopifyName), "shopifyName null");
        Assert.isTrue(StrUtil.isNotBlank(uuid), "uuid null");

        Object clientId = redisUtil.hmgetObj(ShopifyConfig.SHOPIFY_KEY + uuid, "clientId");

        if (null == clientId || StringUtils.isBlank(clientId.toString())) {
            return CommonResult.validateFailed("Uuid is invalid");
        }

        String token;
        if (StrUtil.isNotBlank(password)) {
            token = memberService.login(username, password);
        } else {
            token = memberService.loginNoPassWord(username);
        }
        if (token == null) {
            return CommonResult.validateFailed("用户名或密码错误");
        }

        MemberDetails userinfo = (MemberDetails) memberService.loadUserByUsername(username);
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", token);
        tokenMap.put("tokenHead", tokenHead);
        tokenMap.put("mail", username);
        tokenMap.put("sourcingCountryId", String.valueOf(userinfo.getUmsMember().getSourcingCountryId()));

        // 绑定shopify到客户ID

        QueryWrapper<XmsShopifyAuth> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(XmsShopifyAuth::getShopName, shopifyName).notIn(XmsShopifyAuth::getUuid, uuid).nested(wrapper -> wrapper.eq(XmsShopifyAuth::getMemberId, 0).or().eq(XmsShopifyAuth::getMemberId, userinfo.getUmsMember().getId()));
        this.xmsShopifyAuthService.remove(queryWrapper);

        this.memberService.updateShopifyInfo(userinfo.getUmsMember().getId(), shopifyName, 1);
        UpdateWrapper<XmsShopifyAuth> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().set(XmsShopifyAuth::getMemberId, userinfo.getUmsMember().getId()).set(XmsShopifyAuth::getUpdateTime, new Date()).eq(XmsShopifyAuth::getShopName, shopifyName).eq(XmsShopifyAuth::getUuid, uuid);
        this.xmsShopifyAuthService.update(updateWrapper);

        this.memberService.updateSecurityContext();

        UmsMember currentMember = this.memberService.getById(userinfo.getUmsMember().getId());
        tokenMap.put("nickName", currentMember.getNickname());
        tokenMap.put("guidedFlag", String.valueOf(currentMember.getGuidedFlag()));
        tokenMap.put("logoUrl", currentMember.getLogoUrl());
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

    @ApiOperation("获取会员信息")
    @RequestMapping(value = "/getInfo", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult getInfo() {
        UmsMember member = memberService.getCurrentMember();
        UmsMember byId = this.memberService.getById(member.getId());
        return CommonResult.success(byId);
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
    public CommonResult googleAuth(@RequestParam("idtokenstr") String idtokenstr, @RequestParam("uuid") String uuid) {

        LOGGER.info("google login begin");
        ImmutablePair<String, String> pair = null;
        try {
            if (StringUtils.isNotEmpty(idtokenstr)) {
                idtokenstr = idtokenstr.replaceAll("Bearer ", "");
            }
            pair = memberService.googleAuth(idtokenstr);

            if (pair == null) {
                return CommonResult.failed("mail get failed");
            }

            UmsMember userInfo = memberService.getByUsername(pair.getRight());
            if (userInfo == null) {
                memberService.register(pair.getRight(), pair.getRight(), "", "", 1, 36);
                userInfo = memberService.getByUsername(pair.getRight());
            }
            String token = memberService.login(pair.getRight(), pair.getRight());
            if (token == null) {
                return CommonResult.validateFailed("用户名或密码错误");
            }
            // 整合sourcing数据
            if (StrUtil.isNotEmpty(uuid) && uuid.length() > 10) {
                this.sourcingUtils.mergeSourcingList(userInfo, uuid);
            }

            Map<String, String> tokenMap = new HashMap<>();
            tokenMap.put("token", token);
            tokenMap.put("tokenHead", tokenHead);
            tokenMap.put("mail", pair.getRight());
            if (userInfo == null) {
                tokenMap.put("nickName", "");
            } else {
                tokenMap.put("nickName", userInfo.getNickname());
            }

            return CommonResult.success(tokenMap);
        } catch (Exception e) {
            LOGGER.error("googleAuth", e);
            return CommonResult.failed("googleLogin failed");
        }
    }

//    @RequestMapping(value = "/getFacebookURL", method = RequestMethod.GET)
//    @ApiOperation("get FacebookURL")
//    @ResponseBody
//    public CommonResult getFacebookUrl() {
//
//        try{
//            return CommonResult.success(memberService.getFacebookUrl());
//
//        }catch (Exception e){
//            return CommonResult.failed(e.getMessage());
//        }
//
//    }

    @ApiOperation("facebook登录")
    @RequestMapping(value = "/facebookLogin", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult facebookAuth(@RequestParam("fToken") String fToken, @RequestParam("facebookId") String facebookId, @RequestParam("uuid") String uuid) {

        LOGGER.info("facebook login begin");
        try {
            FacebookPojo bean = memberService.facebookAuth(facebookId, fToken);
            if (bean == null) {
                return CommonResult.failed("mail get failed");
            }
            UmsMember userInfo = memberService.getByUsername(bean.getEmail());
            if (userInfo == null) {
                memberService.register(bean.getEmail(), bean.getEmail(), "", "", 2, 36);
                userInfo = memberService.getByUsername(bean.getEmail());
            }
            String token = memberService.login(bean.getEmail(), bean.getEmail());
            if (token == null) {
                return CommonResult.validateFailed("用户名或密码错误");
            }
            // 整合sourcing数据
            if (StrUtil.isNotEmpty(uuid) && uuid.length() > 10) {
                this.sourcingUtils.mergeSourcingList(userInfo, uuid);
            }
            Map<String, String> tokenMap = new HashMap<>();
            tokenMap.put("token", token);
            tokenMap.put("tokenHead", tokenHead);
            tokenMap.put("mail", bean.getEmail());
            if (userInfo == null) {
                tokenMap.put("nickName", "");
            } else {
                tokenMap.put("nickName", userInfo.getNickname());
            }
            return CommonResult.success(tokenMap);
        } catch (Exception e) {
            LOGGER.error("facebookLogin", e);
            return CommonResult.failed("facebookLogin failed");
        }
    }


    @ApiOperation("修改客户信息")
    @RequestMapping(value = "/updateUserInfo", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updateUserInfo(@RequestParam String niceName,
                                       @RequestParam String monthlyOrderQuantity,
                                       @RequestParam String organizationName) {
        int info = memberService.updateUserInfo(niceName, monthlyOrderQuantity, organizationName);
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


    @ApiOperation("找回密码")
    @RequestMapping(value = "/retrievePassword", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult retrievePassword(@RequestParam("userName") String userName) {

        try {
            UmsMember member = memberService.getByUsername(userName);
            if (null == member || member.getId() == 0) {
                return CommonResult.failed("No such user");
            }
            // 生产随机码，放入redis
            String uuid = UUID.randomUUID().toString();
            redisUtil.hmsetObj(SourcingUtils.RETRIEVE_PASSWORD_KEY, uuid, userName, 60 * 60 * 48);
            // 发送邮件

            String linkUrl = microServiceConfig.getMallPassActivate() + "?userName=" + userName + "&uuid=" + uuid;
            WelcomeMailTemplateBean mailTemplateBean = new WelcomeMailTemplateBean();
            mailTemplateBean.setName(userName);
            mailTemplateBean.setSubject("retrievePassword");
            mailTemplateBean.setActivationCode(linkUrl);
            mailTemplateBean.setTo(userName);
            mailTemplateBean.setTest(false);
            mailTemplateBean.setTemplateType(MailTemplateType.ACCOUNT_UPDATE);
            emailService.send(mailTemplateBean);
            return CommonResult.success(linkUrl);
        } catch (Exception e) {
            LOGGER.error("retrievePassword", e);
            return CommonResult.failed("retrievePassword failed");
        }
    }

    @ApiOperation("验证激活参数")
    @RequestMapping(value = "/checkUUid", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult checkUUid(@RequestParam("userName") String userName, @RequestParam("uuid") String uuid) {

        try {
            Object tempName = redisUtil.hmgetObj(SourcingUtils.RETRIEVE_PASSWORD_KEY, uuid);
            if (null == tempName || !userName.equalsIgnoreCase(tempName.toString())) {
                return CommonResult.success("uuid or userName invalid");
            }
            return CommonResult.success("uuid and userName valid");
        } catch (Exception e) {
            LOGGER.error("resetPassword,checkUUid[{}],error:", userName, e);
            return CommonResult.failed("checkUUid failed");
        }
    }


    @ApiOperation("重新设置密码")
    @RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult resetPassword(@RequestParam("userName") String userName, @RequestParam("password") String password, @RequestParam("uuid") String uuid) {

        try {
            Object tempName = redisUtil.hmgetObj(SourcingUtils.RETRIEVE_PASSWORD_KEY, uuid);
            if (null == tempName || !userName.equalsIgnoreCase(tempName.toString())) {
                return CommonResult.failed("uuid invalid");
            }
            UmsMember member = memberService.getByUsername(userName);
            if (null == member || member.getId() == 0) {
                return CommonResult.failed("No such user");
            }
            memberService.resetPassword(member.getId(), password);
            // 激活完成后清除数据

            // redisUtil.hmsetObj(SourcingUtils.RETRIEVE_PASSWORD_KEY, uuid, userName, 10);
            redisUtil.hDelete(SourcingUtils.RETRIEVE_PASSWORD_KEY, uuid);
            return CommonResult.success("resetPassword success");
        } catch (Exception e) {
            LOGGER.error("resetPassword,userName[{}],error:", userName, e);
            return CommonResult.failed("resetPassword failed");
        }
    }


    @ApiOperation("验证旧密码")
    @RequestMapping(value = "/verifyOldPassword", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult verifyOldPassword(@RequestParam String username,
                                          @RequestParam String password) {
        String token = memberService.verifyOldPassword(username, password);
        return CommonResult.success(token);
    }

    @ApiOperation("其他信息设置")
    @RequestMapping(value = "/setOtherInfo", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult setOtherInfo(UmsMember umsMember) {
        UmsMember currentMember = this.memberService.getCurrentMember();
        try {
            umsMember.setId(currentMember.getId());
            this.memberService.setOtherInfo(umsMember);
            return CommonResult.success(umsMember);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("setOtherInfo,umsMember[{}],error:", umsMember, e);
            return CommonResult.failed("setOtherInfo error");
        }
    }


    @ApiOperation("判断邮箱是否注册")
    @RequestMapping(value = "/checkUserName", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult checkUserName(String userName) {
        try {
            UmsMember byUsername = this.memberService.getByUsername(userName);
            if (null != byUsername && null != byUsername.getId() && byUsername.getId() > 0) {
                return CommonResult.success(1);
            } else {
                return CommonResult.success(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("checkUserName,userName[{}],error:", userName, e);
            return CommonResult.failed("checkUserName error");
        }
    }


    @ApiOperation("设置logo")
    @RequestMapping(value = "/setLogo", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult setLogo(String logoUrl) {
        Assert.isTrue(StrUtil.isNotBlank(logoUrl), "logoUrl null");
        UmsMember currentMember = this.memberService.getCurrentMember();
        try {
            UmsMember byUsername = new UmsMember();
            byUsername.setId(currentMember.getId());
            byUsername.setLogoUrl(logoUrl);
            this.memberService.setLogo(byUsername);
            return CommonResult.success(0);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("setLogo,currentMember[{}],logoUrl[{}],error:", logoUrl, logoUrl, e);
            return CommonResult.failed("setLogo error");
        }
    }

}
