package com.macro.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.macro.mall.dto.XmsSourcingInfoParam;
import com.macro.mall.entity.XmsSourcingList;
import com.macro.mall.mapper.XmsSourcingListMapper;
import com.macro.mall.service.IXmsSourcingListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * sourcing表 服务实现类
 * </p>
 *
 * @author jack.luo
 * @since 2021-04-20
 */
@Service
public class XmsSourcingListServiceImpl extends ServiceImpl<XmsSourcingListMapper, XmsSourcingList> implements IXmsSourcingListService {

    @Autowired
    private XmsSourcingListMapper xmsSourcingListMapper;


    public Page<XmsSourcingList> list(XmsSourcingInfoParam sourcingParam) {

        Page<XmsSourcingList> page = new Page<>(sourcingParam.getPageNum(), sourcingParam.getPageSize());
        LambdaQueryWrapper<XmsSourcingList> lambdaQuery = Wrappers.lambdaQuery();
        lambdaQuery.eq(XmsSourcingList::getUsername, sourcingParam.getUsername());
        if (null != sourcingParam.getStatus() && sourcingParam.getStatus() > -1) {
            lambdaQuery.eq(XmsSourcingList::getStatus, sourcingParam.getStatus());
        }
        lambdaQuery.orderByDesc(XmsSourcingList::getCreateTime);
        return this.xmsSourcingListMapper.selectPage(page, lambdaQuery);
    }

    @Override
    public void updateSourceLink(XmsSourcingList sourcingInfo) {

        XmsSourcingList selectById = this.xmsSourcingListMapper.selectById(sourcingInfo.getId());
        if (null != selectById) {
            selectById.setSourceLink(sourcingInfo.getSourceLink());
            this.xmsSourcingListMapper.updateById(sourcingInfo);
        }
    }

}
