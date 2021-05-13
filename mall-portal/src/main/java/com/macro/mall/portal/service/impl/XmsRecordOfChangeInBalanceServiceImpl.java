package com.macro.mall.portal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.macro.mall.entity.XmsRecordOfChangeInBalance;
import com.macro.mall.mapper.XmsRecordOfChangeInBalanceMapper;
import com.macro.mall.portal.service.IXmsRecordOfChangeInBalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 客户余额变更记录 服务实现类
 * </p>
 *
 * @author jack.luo
 * @since 2021-05-10
 */
@Service
public class XmsRecordOfChangeInBalanceServiceImpl extends ServiceImpl<XmsRecordOfChangeInBalanceMapper, XmsRecordOfChangeInBalance> implements IXmsRecordOfChangeInBalanceService {


    @Autowired
    private XmsRecordOfChangeInBalanceMapper xmsRecordOfChangeInBalanceMapper;

    @Override
    public Page<XmsRecordOfChangeInBalance> list(Integer pageNum, Integer pageSize, String userName) {
        Page<XmsRecordOfChangeInBalance> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<XmsRecordOfChangeInBalance> lambdaQuery = Wrappers.lambdaQuery();
        lambdaQuery.eq(XmsRecordOfChangeInBalance::getUsername, userName);
        lambdaQuery.orderByDesc(XmsRecordOfChangeInBalance::getCreateTime);
        return this.xmsRecordOfChangeInBalanceMapper.selectPage(page, lambdaQuery);
    }
}
