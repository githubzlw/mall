package com.macro.mall.portal.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.macro.mall.entity.XmsCustomerProduct;
import com.macro.mall.entity.XmsSourcingList;
import com.macro.mall.mapper.XmsCustomerProductMapper;
import com.macro.mall.mapper.XmsSourcingListMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.macro.mall.portal.domain.XmsSourcingInfoParam;
import com.macro.mall.portal.service.IXmsSourcingListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * sourcing表 服务实现类
 * </p>
 *
 * @author jack.luo
 * @since 2021-04-28
 */
@Service
public class XmsSourcingListServiceImpl extends ServiceImpl<XmsSourcingListMapper, XmsSourcingList> implements IXmsSourcingListService {


    @Autowired
    private XmsSourcingListMapper xmsSourcingListMapper;

    @Autowired
    private XmsCustomerProductMapper xmsCustomerProductMapper;


    public Page<XmsSourcingList> list(XmsSourcingInfoParam sourcingParam) {

        Page<XmsSourcingList> page = new Page<>(sourcingParam.getPageNum(), sourcingParam.getPageSize());
        LambdaQueryWrapper<XmsSourcingList> lambdaQuery = Wrappers.lambdaQuery();
        lambdaQuery.eq(XmsSourcingList::getUsername, sourcingParam.getUsername());
        lambdaQuery.ge(XmsSourcingList::getStatus, -1);
        if (null != sourcingParam.getStatus() && sourcingParam.getStatus() > -1) {
            lambdaQuery.eq(XmsSourcingList::getStatus, sourcingParam.getStatus());
        }
        if (StrUtil.isNotEmpty(sourcingParam.getUrl())) {
            lambdaQuery.and(wrapper -> wrapper.like(XmsSourcingList::getUrl, sourcingParam.getUrl()).or().eq(XmsSourcingList::getTitle, sourcingParam.getUrl()));
        }
        lambdaQuery.orderByDesc(XmsSourcingList::getCreateTime);
        return this.xmsSourcingListMapper.selectPage(page, lambdaQuery);
    }

    @Override
    public boolean checkHasXmsCustomerProduct(XmsCustomerProduct product) {

        LambdaQueryWrapper<XmsCustomerProduct> lambdaQuery = Wrappers.lambdaQuery();
        lambdaQuery.eq(XmsCustomerProduct::getUsername, product.getUsername())
                .eq(XmsCustomerProduct::getSourcingId, product.getSourcingId());
        return this.xmsCustomerProductMapper.selectCount(lambdaQuery) > 0;
    }

    @Override
    public XmsSourcingList querySingleSourcingList(XmsSourcingInfoParam sourcingParam) {
        LambdaQueryWrapper<XmsSourcingList> lambdaQuery = Wrappers.lambdaQuery();
        lambdaQuery.eq(XmsSourcingList::getUsername, sourcingParam.getUsername())
                .eq(XmsSourcingList::getProductId, sourcingParam.getProductId());
        return this.xmsSourcingListMapper.selectOne(lambdaQuery);
    }

}
