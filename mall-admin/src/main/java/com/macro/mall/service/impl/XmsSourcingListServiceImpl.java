package com.macro.mall.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
        // username
        if(StrUtil.isNotEmpty(sourcingParam.getUsername())){
            lambdaQuery.eq(XmsSourcingList::getUsername, sourcingParam.getUsername());
        }

        // status
        if (null != sourcingParam.getStatus() && sourcingParam.getStatus() > -1) {
            lambdaQuery.eq(XmsSourcingList::getStatus, sourcingParam.getStatus());
        }
        lambdaQuery.gt(XmsSourcingList::getStatus, -2);
        // url
        if (StrUtil.isNotEmpty(sourcingParam.getUrl())) {
            lambdaQuery.nested(wrapper -> wrapper.like(XmsSourcingList::getUrl, sourcingParam.getUrl()).or().like(XmsSourcingList::getTitle, sourcingParam.getUrl()));
        }
        // beginTime
        if (StrUtil.isNotEmpty(sourcingParam.getBeginTime())) {
            lambdaQuery.ge(XmsSourcingList::getCreateTime, sourcingParam.getBeginTime() + " 00:00:00");
        }
        // endTime
        if (StrUtil.isNotEmpty(sourcingParam.getEndTime())) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDateTime dateTime = LocalDateTime.parse(sourcingParam.getEndTime(), dateTimeFormatter);
            LocalDateTime plusDays = dateTime.plusDays(1);
            lambdaQuery.lt(XmsSourcingList::getCreateTime, plusDays.format(dateTimeFormatter) + " 00:00:00");
        }
        // siteType
        if(null != sourcingParam.getSiteType() && sourcingParam.getSiteType() > 0){
            lambdaQuery.eq(XmsSourcingList::getSiteType, sourcingParam.getSiteType() + " 00:00:00");
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

    @Override
    public void updateSourceStatus(XmsSourcingList sourcingInfo) {
        UpdateWrapper<XmsSourcingList> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().set(XmsSourcingList::getStatus, sourcingInfo.getStatus()).eq(XmsSourcingList::getId, sourcingInfo.getId());
        this.xmsSourcingListMapper.update(null, updateWrapper);

    }

}
