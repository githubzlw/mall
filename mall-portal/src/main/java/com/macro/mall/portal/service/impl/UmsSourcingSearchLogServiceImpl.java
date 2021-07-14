package com.macro.mall.portal.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.macro.mall.entity.UmsSourcingSearchLog;
import com.macro.mall.entity.XmsMsg;
import com.macro.mall.entity.XmsPayment;
import com.macro.mall.entity.XmsSourcingList;
import com.macro.mall.mapper.UmsSourcingSearchLogMapper;
import com.macro.mall.mapper.XmsMsgMapper;
import com.macro.mall.portal.dao.MsgDao;
import com.macro.mall.portal.domain.SourcingSearchParam;
import com.macro.mall.portal.domain.XmsMsgParam;
import com.macro.mall.portal.service.UmsSourcingSearchLogService;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 公告表 服务实现类
 * </p>
 *
 * @author jack.luo
 * @since 2021-04-22
 */
@Service
public class UmsSourcingSearchLogServiceImpl extends ServiceImpl<UmsSourcingSearchLogMapper, UmsSourcingSearchLog> implements UmsSourcingSearchLogService {

    @Autowired
    private UmsSourcingSearchLogMapper umsSourcingSearchLogMapper;


    @Override
    public Page<UmsSourcingSearchLog> getSearchLogList(SourcingSearchParam sourcingSearchParam, Integer pageNum, Integer pageSize) {
        Page<UmsSourcingSearchLog> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<UmsSourcingSearchLog> lambdaQuery = Wrappers.lambdaQuery();
        if (StringUtils.isNotBlank(sourcingSearchParam.getIp())) {
            lambdaQuery.eq(UmsSourcingSearchLog::getIp, sourcingSearchParam.getIp());
        }
        if (StringUtils.isNotBlank(sourcingSearchParam.getSourcingSearch())) {
            lambdaQuery.like(UmsSourcingSearchLog::getSourcingSearch, sourcingSearchParam.getSourcingSearch());
        }

        lambdaQuery.orderByDesc(UmsSourcingSearchLog::getCreateTime);

        return umsSourcingSearchLogMapper.selectPage(page, lambdaQuery);
    }

    @Override
    public void insertSourcingSearchLog(SourcingSearchParam sourcingSearchParam) {
        UmsSourcingSearchLog umsSourcingSearchLog = new UmsSourcingSearchLog();
        umsSourcingSearchLog.setIp(sourcingSearchParam.getIp());
        umsSourcingSearchLog.setSourcingSearch(sourcingSearchParam.getSourcingSearch());
        umsSourcingSearchLog.setCreateTime(new Date());
        umsSourcingSearchLogMapper.insert(umsSourcingSearchLog);
    }
}
