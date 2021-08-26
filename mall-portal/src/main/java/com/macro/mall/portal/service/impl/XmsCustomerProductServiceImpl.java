package com.macro.mall.portal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.macro.mall.entity.XmsCustomerProduct;
import com.macro.mall.mapper.XmsCustomerProductMapper;
import com.macro.mall.portal.domain.XmsCustomerProductParam;
import com.macro.mall.portal.service.IXmsCustomerProductService;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * <p>
 * 客户的产品表 服务实现类
 * </p>
 *
 * @author jack.luo
 * @since 2021-04-28
 */
@Service
public class XmsCustomerProductServiceImpl extends ServiceImpl<XmsCustomerProductMapper, XmsCustomerProduct> implements IXmsCustomerProductService {


    @Autowired
    private XmsCustomerProductMapper xmsCustomerProductMapper;

    @Override
    public Page<XmsCustomerProduct> list(XmsCustomerProductParam productParam) {

        Page<XmsCustomerProduct> page = new Page<>(productParam.getPageNum(), productParam.getPageSize());
        LambdaQueryWrapper<XmsCustomerProduct> lambdaQuery = Wrappers.lambdaQuery();
        lambdaQuery.eq(XmsCustomerProduct::getUsername, productParam.getUsername());
        if (!StringUtils.isEmpty(productParam.getTitle())) {
            lambdaQuery.like(XmsCustomerProduct::getTitle, productParam.getTitle());
        }
        lambdaQuery.orderByDesc(XmsCustomerProduct::getCreateTime);
        return this.xmsCustomerProductMapper.selectPage(page, lambdaQuery);
    }

    @Override
    public List<XmsCustomerProduct> queryByUserInfo(String userName , Long memberId) {
        LambdaQueryWrapper<XmsCustomerProduct> lambdaQuery = Wrappers.lambdaQuery();
        lambdaQuery.eq(XmsCustomerProduct::getUsername, userName);
        return this.xmsCustomerProductMapper.selectList(lambdaQuery);
    }


}
