package com.macro.mall.shopify.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.macro.mall.entity.XmsShopifyAuth;
import com.macro.mall.mapper.XmsShopifyAuthMapper;
import com.macro.mall.shopify.config.ShopifyRestTemplate;
import com.macro.mall.shopify.service.IXmsShopifyAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;

/**
 * <p>
 * shopify授权token表 服务实现类
 * </p>
 *
 * @author jack.luo
 * @since 2021-05-11
 */
@Service
public class XmsShopifyAuthServiceImpl extends ServiceImpl<XmsShopifyAuthMapper, XmsShopifyAuth> implements IXmsShopifyAuthService {


    @Autowired
    private XmsShopifyAuthMapper xmsShopifyAuthMapper;

    @Autowired
    private ShopifyRestTemplate shopifyRestTemplate;

    @Override
    public HashMap<String, String> getAccessToken(String shopName, String code) throws IOException {
        return shopifyRestTemplate.postForEntity(shopName, code);
    }


    /**
     * get token by shopName
     *
     * @param shopName
     * @return
     */
    @Override
    public String getShopifyToken(String shopName, Long memberId) {

        QueryWrapper<XmsShopifyAuth> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(XmsShopifyAuth::getShopName, shopName).eq(XmsShopifyAuth::getMemberId, memberId);
        XmsShopifyAuth shopifyAuth = xmsShopifyAuthMapper.selectOne(queryWrapper);
        return shopifyAuth.getAccessToken();
    }

}
