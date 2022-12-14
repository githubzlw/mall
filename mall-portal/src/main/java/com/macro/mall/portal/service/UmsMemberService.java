package com.macro.mall.portal.service;

import com.macro.mall.model.UmsMember;
import com.macro.mall.portal.domain.FacebookPojo;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

/**
 * 会员管理Service
 * Created by macro on 2018/8/3.
 */
public interface UmsMemberService {
    /**
     * 根据用户名获取会员
     */
    UmsMember getByUsername(String username);

    UmsMember getByUsernameNoCache(String username);

    /**
     * 根据会员编号获取会员
     */
    UmsMember getById(Long id);

    /**
     * 用户注册
     */
    @Transactional
    void register(String username, String password, String organizationname,String monthlyOrders,Integer loginType,Integer countryId);

    void registerNew(String username, String password, String organizationname,String monthlyOrders,Integer loginType,Integer countryId);

    /**
     * 生成验证码
     */
    String generateAuthCode(String telephone);

    /**
     * 修改密码
     */
    @Transactional
    void updatePassword(String telephone, String password, String authCode);

    /**
     * 重置密码
     */
    @Transactional
    void resetPassword(Long memberId, String password);

    /**
     * 修改昵称和每月订单量
     *
     * @param niceName
     * @param monthlyOrderQuantity
     * @return
     */
    int updateUserInfo(String niceName, String monthlyOrderQuantity, String organizationName);

    /**
     * 获取当前登录会员
     */
    UmsMember getCurrentMember();

    /**
     * 根据会员id修改会员积分
     */
    void updateIntegration(Long id,Integer integration);


    /**
     * 获取用户信息
     */
    UserDetails loadUserByUsername(String username);

    /**
     * 登录后获取token
     */
    String login(String username, String password);

    /**
     * 登录后获取token
     */
    String loginNoPassWord(String username);

    /**
     * 刷新token
     */
    String refreshToken(String token);


    /**
     *更新客户的 shopify信息
     * @param id
     * @param shopifyName
     * @param shopifyFlag
     * @return
     */
    int updateShopifyInfo(Long id, String shopifyName, Integer shopifyFlag);

    /**
     * google登录验证
     */
    ImmutablePair<String, String> googleAuth(String idTokenString) throws IOException;

    FacebookPojo facebookAuth(String facebookId,String fToken);
//    /**
//     * get facebook login url
//     * @return
//     */
//    String getFacebookUrl();

    int updateGuidedFlag(Long id);

    String verifyOldPassword(String username, String password);

    /**
     * 更新缓存信息
     */
    void updateSecurityContext();

    void setOtherInfo(UmsMember umsMember);

    void setLogo(UmsMember ums);

    int clearOtherShopifyInfo(Long id, String shopifyName);

    int getByShopifyName(String shopifyName);
}
