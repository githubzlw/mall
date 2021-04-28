package com.macro.mall.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.macro.mall.entity.XmsChromeUpload;
import com.macro.mall.entity.XmsSourcingList;
import com.macro.mall.mapper.XmsChromeUploadMapper;
import com.macro.mall.mapper.XmsSourcingListMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: 产品清洗工具类
 * @date:2021-04-25
 */
@Service
@Slf4j
public class ProductUtils {

    private final XmsChromeUploadMapper xmsChromeUploadMapper;
    private final XmsSourcingListMapper xmsSourcingListMapper;
    private final AtomicReference<Boolean> referenceFlag;

    @Autowired
    public ProductUtils(XmsChromeUploadMapper xmsChromeUploadMapper, XmsSourcingListMapper xmsSourcingListMapper) {
        this.xmsChromeUploadMapper = xmsChromeUploadMapper;
        this.xmsSourcingListMapper = xmsSourcingListMapper;
        this.referenceFlag = new AtomicReference<>();
    }


    /**
     * sourcing的商品清洗
     */
    public void cleaningData() {

        try {
            // 如果有执行的，不再重复处理
            if (!this.referenceFlag.compareAndSet(false, true)) {
                return;
            }

            XmsChromeUpload chromeUpload = new XmsChromeUpload();
            chromeUpload.setClearFlag(-2);
            // 获取商品数据列表
            QueryWrapper<XmsChromeUpload> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(XmsChromeUpload::getStatus, 0L);
            List<XmsChromeUpload> chromeUploadList = this.xmsChromeUploadMapper.selectList(queryWrapper);
            if (CollectionUtil.isNotEmpty(chromeUploadList)) {
                chromeUploadList.forEach(this::cleaningSingleData);
                chromeUploadList.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("cleaningData,error:", e);
        } finally {
            this.referenceFlag.set(false);
        }


    }


    public void cleaningSingleData(XmsChromeUpload chromeUpload) {
        Assert.notNull(chromeUpload, "chromeUpload null");
        XmsChromeUpload updateChromeUpload = new XmsChromeUpload();
        updateChromeUpload.setId(chromeUpload.getId());

        try {

            updateChromeUpload.setClearFlag(1);
            this.xmsChromeUploadMapper.updateById(updateChromeUpload);

            XmsSourcingList sourcingInfo = new XmsSourcingList();
            sourcingInfo.setMemberId(chromeUpload.getMemberId());
            sourcingInfo.setUsername(chromeUpload.getUsername());
            // 处理url
            if (StrUtil.isNotEmpty(chromeUpload.getUrl())) {
                if (chromeUpload.getUrl().contains("?")) {
                    sourcingInfo.setUrl(chromeUpload.getUrl().substring(0, chromeUpload.getUrl().indexOf("?")));
                } else {
                    sourcingInfo.setUrl(chromeUpload.getUrl());
                }
            }
            // 处理title
            if (StrUtil.isNotEmpty(chromeUpload.getTitle())) {
                String tempTitle = chromeUpload.getTitle().replaceAll("[\\u4e00-\\u9fa5]", "");
                if (StrUtil.isNotEmpty(tempTitle)) {
                    sourcingInfo.setTitle(tempTitle.trim());
                }
            }
            // 处理img
            if (StrUtil.isNotEmpty(chromeUpload.getImages())) {
                sourcingInfo.setImages(chromeUpload.getImages());
            }
            // 处理价格
            // US $9.86 - 13.50 US $12.33 - 16.88-20%
            if (StrUtil.isNotEmpty(chromeUpload.getPrice())) {
                // .replace("$", "@")
                String tempPrice = chromeUpload.getPrice().trim();
                if (tempPrice.indexOf("US") == 0) {
                    String[] priceArr = tempPrice.substring(2).split("US");
                    sourcingInfo.setPrice(priceArr[0].trim());
                } else {
                    sourcingInfo.setPrice(chromeUpload.getPrice().trim());
                }
            }
            // 处理 shippingFee
            // Shipping: US $5.14
            if (StrUtil.isNotEmpty(chromeUpload.getShippingFee())) {
                if (chromeUpload.getShippingFee().contains("Shipping: US $")) {
                    sourcingInfo.setCost(chromeUpload.getShippingFee().replace("Shipping: US $", "").trim());
                } else {
                    sourcingInfo.setCost(chromeUpload.getShippingFee().trim());
                }
                sourcingInfo.setCost(sourcingInfo.getCost().replace("AWG", "").trim());

            }

            if (StrUtil.isNotEmpty(chromeUpload.getShippingBy())) {
                sourcingInfo.setShipping(chromeUpload.getShippingBy());
            }

            sourcingInfo.setSiteType(chromeUpload.getSiteType());
            sourcingInfo.setStatus(chromeUpload.getStatus());
            this.xmsSourcingListMapper.insert(sourcingInfo);

            updateChromeUpload.setClearFlag(2);
            this.xmsChromeUploadMapper.updateById(updateChromeUpload);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("cleaningSingleData,error:", e);
            updateChromeUpload.setClearFlag(-1);
            this.xmsChromeUploadMapper.updateById(updateChromeUpload);
        }
    }

}
