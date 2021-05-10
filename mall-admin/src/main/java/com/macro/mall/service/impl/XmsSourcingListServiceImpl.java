package com.macro.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.macro.mall.dto.XmsSourcingInfoParam;
import com.macro.mall.entity.XmsChromeUpload;
import com.macro.mall.entity.XmsSourcingList;
import com.macro.mall.mapper.XmsChromeUploadMapper;
import com.macro.mall.mapper.XmsSourcingListMapper;
import com.macro.mall.service.IXmsSourcingListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

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

    @Autowired
    private XmsChromeUploadMapper xmsChromeUploadMapper;


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

            // 添加货源后，加入到Upload表中清洗
            LambdaQueryWrapper<XmsChromeUpload> lambdaQuery = Wrappers.lambdaQuery();
            lambdaQuery.eq(XmsChromeUpload::getUsername, selectById.getUsername());
            lambdaQuery.eq(XmsChromeUpload::getSourcingId, sourcingInfo.getId());
            XmsChromeUpload chromeUpload = this.xmsChromeUploadMapper.selectOne(lambdaQuery);
            if (null != chromeUpload) {
                chromeUpload.setUrl(sourcingInfo.getSourceLink());
                chromeUpload.setClearFlag(0);
                chromeUpload.setSourceFlag(2);
                chromeUpload.setUpdateTime(new Date());
                this.xmsChromeUploadMapper.updateById(chromeUpload);
            } else {
                chromeUpload = new XmsChromeUpload();
                chromeUpload.setId(null);
                chromeUpload.setUsername(selectById.getUsername());
                chromeUpload.setMemberId(selectById.getMemberId());
                chromeUpload.setUrl(sourcingInfo.getSourceLink());
                chromeUpload.setClearFlag(0);
                chromeUpload.setSourceFlag(2);
                chromeUpload.setCreateTime(new Date());
                chromeUpload.setUpdateTime(new Date());
                this.xmsChromeUploadMapper.insert(chromeUpload);
            }

        }

    }

}
