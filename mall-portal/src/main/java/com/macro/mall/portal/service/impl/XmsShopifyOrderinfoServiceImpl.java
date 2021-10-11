package com.macro.mall.portal.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.macro.mall.common.api.CommonPage;
import com.macro.mall.entity.*;
import com.macro.mall.mapper.XmsShopifyOrderAddressMapper;
import com.macro.mall.mapper.XmsShopifyOrderDetailsMapper;
import com.macro.mall.mapper.XmsShopifyOrderinfoMapper;
import com.macro.mall.mapper.XmsShopifyPidInfoMapper;
import com.macro.mall.portal.dao.XmsShopifyOrderinfoDao;
import com.macro.mall.portal.domain.FulfillmentOrderItem;
import com.macro.mall.portal.domain.ShopifyOrderDetailsShort;
import com.macro.mall.portal.domain.XmsShopifyOrderinfoParam;
import com.macro.mall.portal.service.IXmsShopifyOrderinfoService;
import com.macro.mall.portal.service.IXmsShopifyPidImgService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


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
    @Autowired
    private XmsShopifyOrderinfoDao xmsShopifyOrderinfoDao;
    @Autowired
    private XmsShopifyOrderDetailsMapper xmsShopifyOrderDetailsMapper;
    @Autowired
    private XmsShopifyOrderAddressMapper xmsShopifyOrderAddressMapper;
    @Autowired
    private IXmsShopifyPidImgService xmsShopifyPidImgService;
    @Autowired
    private XmsShopifyPidInfoMapper xmsShopifyPidInfoMapper;

    @Override
    public CommonPage<XmsShopifyOrderComb> list(XmsShopifyOrderinfoParam orderinfoParam) {
        if (null == orderinfoParam.getPageNum() || 0 == orderinfoParam.getPageNum()) {
            orderinfoParam.setPageNum(1);
        }
        if (null == orderinfoParam.getPageSize() || 0 == orderinfoParam.getPageSize()) {
            orderinfoParam.setPageSize(5);
        }
        if (StrUtil.isBlank(orderinfoParam.getUrl())) {
            orderinfoParam.setUrl(null);
        }
        if (StrUtil.isBlank(orderinfoParam.getCountryName())) {
            orderinfoParam.setCountryName(null);
        }
        if (StrUtil.isBlank(orderinfoParam.getBeginTime())) {
            orderinfoParam.setBeginTime(null);
        } else {
            orderinfoParam.setBeginTime(orderinfoParam.getBeginTime().substring(0, 10) + " 00:00:00");
        }
        if (StrUtil.isBlank(orderinfoParam.getEndTime())) {
            orderinfoParam.setEndTime(null);
        } else {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate dateTime = LocalDate.parse(orderinfoParam.getEndTime().substring(0, 10), dateTimeFormatter);
            LocalDate plusDays = dateTime.plusDays(1);
            orderinfoParam.setEndTime(plusDays.format(dateTimeFormatter) + " 00:00:00");
        }
        PageHelper.startPage(orderinfoParam.getPageNum(), orderinfoParam.getPageSize());

        List<XmsShopifyOrderinfo> orderinfoList = this.xmsShopifyOrderinfoDao.queryForList(orderinfoParam);

        CommonPage<XmsShopifyOrderinfo> orderPage = CommonPage.restPage(orderinfoList);
        //设置分页信息
        CommonPage<XmsShopifyOrderComb> resultPage = new CommonPage<>();
        resultPage.setPageNum(orderPage.getPageNum());
        resultPage.setPageSize(orderPage.getPageSize());
        resultPage.setTotal(orderPage.getTotal());
        resultPage.setTotalPage(orderPage.getTotalPage());
        if (CollUtil.isEmpty(orderinfoList)) {
            return resultPage;
        }

        List<Long> orderIds = orderinfoList.stream().map(XmsShopifyOrderinfo::getOrderNo).collect(Collectors.toList());

        // 详情
        QueryWrapper<XmsShopifyOrderDetails> detailsQueryWrapperWrapper = new QueryWrapper<>();
        detailsQueryWrapperWrapper.lambda().in(XmsShopifyOrderDetails::getOrderNo, orderIds);
        List<XmsShopifyOrderDetails> detailsList = this.xmsShopifyOrderDetailsMapper.selectList(detailsQueryWrapperWrapper);
        if (CollectionUtil.isNotEmpty(detailsList)) {
            List<Long> collect = detailsList.stream().map(XmsShopifyOrderDetails::getProductId).collect(Collectors.toList());
            QueryWrapper<XmsShopifyPidImg> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().in(XmsShopifyPidImg::getShopifyPid, collect);
            List<XmsShopifyPidImg> list = this.xmsShopifyPidImgService.list(queryWrapper);
            Map<Long, String> imgMap = new HashMap<>();
            list.forEach(e -> imgMap.put(Long.parseLong(e.getShopifyPid()), e.getImg()));
            detailsList.forEach(e -> {
                if (imgMap.containsKey(e.getProductId())) {
                    e.setMainImg(imgMap.get(e.getProductId()));
                }
            });
            collect.clear();
            list.clear();
            imgMap.clear();
        }
        // 地址
        QueryWrapper<XmsShopifyOrderAddress> addressQueryWrapper = new QueryWrapper<>();
        addressQueryWrapper.lambda().in(XmsShopifyOrderAddress::getOrderNo, orderIds);
        List<XmsShopifyOrderAddress> xmsShopifyOrderAddresses = this.xmsShopifyOrderAddressMapper.selectList(addressQueryWrapper);

        List<XmsShopifyOrderComb> combList = new ArrayList<>();
        for (XmsShopifyOrderinfo omsOrder : orderinfoList) {
            XmsShopifyOrderComb orderComb = new XmsShopifyOrderComb();
            BeanUtil.copyProperties(omsOrder, orderComb);
            List<XmsShopifyOrderDetails> relatedItemList = detailsList.stream().filter(item -> item.getOrderNo().equals(orderComb.getOrderNo())).collect(Collectors.toList());
            orderComb.setDetailsList(relatedItemList);
            long sum = relatedItemList.stream().mapToLong(XmsShopifyOrderDetails::getQuantity).sum();
            orderComb.setTotalQuantity(sum);

            XmsShopifyOrderAddress tempOrderAddress = xmsShopifyOrderAddresses.stream().filter(e -> e.getOrderNo().equals(orderComb.getOrderNo())).findFirst().orElse(null);
            if (null == tempOrderAddress) {
                orderComb.setAddressInfo(new XmsShopifyOrderAddress());
            } else {
                orderComb.setAddressInfo(tempOrderAddress);
            }
            // https://busysell-test.myshopify.com/admin/orders/4053447803073
            orderComb.setShopifyOrderUrl("https://" + orderComb.getShopifyName() + ".myshopify.com/admin/orders/" + orderComb.getOrderNo());
            combList.add(orderComb);
        }


        resultPage.setList(combList);
        return resultPage;
    }

    @Override
    public int queryCount(XmsShopifyOrderinfoParam xmsShopifyOrderinfoParam) {
        return this.xmsShopifyOrderinfoDao.queryCount(xmsShopifyOrderinfoParam);
    }

    @Override
    public List<XmsShopifyPidInfo> queryByShopifyLineItem(String shopifyName, List<Long> lineItems) {
        QueryWrapper<XmsShopifyPidInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(XmsShopifyPidInfo::getShopifyName, shopifyName).in(XmsShopifyPidInfo::getShopifyPid, lineItems);
        return this.xmsShopifyPidInfoMapper.selectList(queryWrapper);
    }

    @Override
    public void dealShopifyOrderDetailsMainImg(Map<Long, List<ShopifyOrderDetailsShort>> shortMap) {
        List<Long> pdIdList = new ArrayList<>();
        shortMap.forEach((k, v) -> {
            if (CollectionUtil.isNotEmpty(v)) {
                v.forEach(cl -> {
                    if (!pdIdList.contains(cl.getProductId())) {
                        pdIdList.add(cl.getProductId());
                    }
                });
            }
        });
        if (CollectionUtil.isNotEmpty(pdIdList)) {
            QueryWrapper<XmsShopifyPidImg> pidImgWrapper = new QueryWrapper<>();
            pidImgWrapper.lambda().in(XmsShopifyPidImg::getShopifyPid, pdIdList);
            List<XmsShopifyPidImg> list = this.xmsShopifyPidImgService.list(pidImgWrapper);
            Map<Long, String> imgMap = new HashMap<>();
            list.forEach(e -> imgMap.put(Long.parseLong(e.getShopifyPid()), e.getImg()));
            shortMap.forEach((k, v) -> {
                if (CollectionUtil.isNotEmpty(v)) {
                    v.forEach(cl -> cl.setMainImg(imgMap.getOrDefault(cl.getProductId(), "")) );
                }
            });
            pdIdList.clear();
            list.clear();
            imgMap.clear();
        }
    }

    @Override
    public void dealItemImg(List<FulfillmentOrderItem> itemList){
        Set<Long> pdIdList = new HashSet<>();
        itemList.forEach(e-> pdIdList.add(e.getProductId()) );

        if (CollectionUtil.isNotEmpty(pdIdList)) {
            QueryWrapper<XmsShopifyPidImg> pidImgWrapper = new QueryWrapper<>();
            pidImgWrapper.lambda().in(XmsShopifyPidImg::getShopifyPid, pdIdList);
            List<XmsShopifyPidImg> list = this.xmsShopifyPidImgService.list(pidImgWrapper);
            Map<Long, String> imgMap = new HashMap<>();
            list.forEach(e -> imgMap.put(Long.parseLong(e.getShopifyPid()), e.getImg()));
            itemList.forEach(e -> {
                e.setMainImg(imgMap.getOrDefault(e.getProductId(), ""));
            });
            pdIdList.clear();
            list.clear();
            imgMap.clear();
        }

    }

}
