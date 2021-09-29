package com.macro.mall.shopify.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.macro.mall.entity.XmsShopifyAuth;

import java.io.IOException;
import java.util.HashMap;

/**
 * <p>
 * shopify授权token表 服务类
 * </p>
 *
 * @author jack.luo
 * @since 2021-05-11
 */
public interface IXmsShopifyAuthService extends IService<XmsShopifyAuth> {

    HashMap<String, String> getAccessToken(String shopname, String code) throws IOException;

    String getShopifyToken(String shopName, Long memberId);

}
