package com.macro.mall.shopify.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.macro.mall.entity.XmsShopifyOrderinfo;
import com.macro.mall.mapper.XmsShopifyOrderinfoMapper;
import com.macro.mall.shopify.service.IXmsShopifyOrderinfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author jack.luo
 * @since 2021-05-12
 */
@Service
@Slf4j
public class XmsShopifyOrderinfoServiceImpl extends ServiceImpl<XmsShopifyOrderinfoMapper, XmsShopifyOrderinfo> implements IXmsShopifyOrderinfoService {

    @Autowired
    private XmsShopifyOrderinfoMapper xmsShopifyOrderinfoMapper;

    @Override
    public List<XmsShopifyOrderinfo> queryListByShopifyName(String shopifyName) {
        QueryWrapper<XmsShopifyOrderinfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(XmsShopifyOrderinfo::getShopifyName, shopifyName);
        return this.xmsShopifyOrderinfoMapper.selectList(queryWrapper);
    }

    @Override
    public List<XmsShopifyOrderinfo> queryListByOrderNo(Long orderNo) {
        QueryWrapper<XmsShopifyOrderinfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(XmsShopifyOrderinfo::getOrderNo, orderNo);
        return this.xmsShopifyOrderinfoMapper.selectList(queryWrapper);
    }

    @Override
    public int setTrackNo(Long orderNo, String trackNo) {

        QueryWrapper<XmsShopifyOrderinfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(XmsShopifyOrderinfo::getOrderNo, orderNo);
        XmsShopifyOrderinfo xmsShopifyOrderinfo = this.xmsShopifyOrderinfoMapper.selectOne(queryWrapper);
        if (null != xmsShopifyOrderinfo) {
            if (StrUtil.isBlank(xmsShopifyOrderinfo.getTrackNo())) {
                xmsShopifyOrderinfo.setTrackNo("," + trackNo);
            } else if (!xmsShopifyOrderinfo.getTrackNo().contains("," + trackNo)) {
                xmsShopifyOrderinfo.setTrackNo(xmsShopifyOrderinfo.getTrackNo() + "," + trackNo);
            }
            xmsShopifyOrderinfo.setUpdateTime(new Date());
            UpdateWrapper<XmsShopifyOrderinfo> updateWrapper = new UpdateWrapper<>();
            updateWrapper.lambda().set(XmsShopifyOrderinfo::getTrackNo, xmsShopifyOrderinfo.getTrackNo())
                    .set(XmsShopifyOrderinfo::getUpdateTime, new Date())
                    .eq(XmsShopifyOrderinfo::getId, xmsShopifyOrderinfo.getId());
            return this.xmsShopifyOrderinfoMapper.update(null, updateWrapper);
        }
        return 0;
    }
}
