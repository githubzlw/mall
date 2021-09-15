package com.macro.mall.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.macro.mall.common.api.CommonPage;
import com.macro.mall.dao.XmsShopifyOrderinfoDao;
import com.macro.mall.domain.XmsShopifyOrderinfoParam;
import com.macro.mall.entity.*;
import com.macro.mall.mapper.*;
import com.macro.mall.model.UmsMember;
import com.macro.mall.model.UmsMemberExample;
import com.macro.mall.service.IXmsShopifyOrderinfoService;
import com.macro.mall.service.IXmsShopifyPidImgService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private XmsShopifyOrderinfoDao xmsShopifyOrderinfoDao;
    @Autowired
    private XmsShopifyOrderDetailsMapper xmsShopifyOrderDetailsMapper;
    @Autowired
    private XmsShopifyOrderAddressMapper xmsShopifyOrderAddressMapper;
    @Autowired
    private IXmsShopifyPidImgService xmsShopifyPidImgService;
    @Resource
    private UmsMemberMapper umsMemberMapper;

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
        if (StrUtil.isBlank(orderinfoParam.getShopifyName())) {
            orderinfoParam.setShopifyName(null);
        }
        if (StrUtil.isBlank(orderinfoParam.getOrderNo())) {
            orderinfoParam.setOrderNo(null);
        }
        if (StrUtil.isBlank(orderinfoParam.getCountryName())) {
            orderinfoParam.setCountryName(null);
        }
        if (StrUtil.isBlank(orderinfoParam.getBeginTime())) {
            orderinfoParam.setBeginTime(null);
        }
        if (StrUtil.isBlank(orderinfoParam.getEndTime())) {
            orderinfoParam.setEndTime(null);
        } else {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate dateTime = LocalDate.parse(orderinfoParam.getEndTime().substring(0, 10), dateTimeFormatter);
            LocalDate plusDays = dateTime.plusDays(1);
            orderinfoParam.setEndTime(plusDays.format(dateTimeFormatter));
        }
        PageHelper.startPage(orderinfoParam.getPageNum(), orderinfoParam.getPageSize());
        List<XmsShopifyOrderinfo> orderInfoList = this.xmsShopifyOrderinfoDao.queryForList(orderinfoParam);
        CommonPage<XmsShopifyOrderinfo> orderPage = CommonPage.restPage(orderInfoList);
        //设置分页信息
        CommonPage<XmsShopifyOrderComb> resultPage = new CommonPage<>();
        resultPage.setPageNum(orderPage.getPageNum());
        resultPage.setPageSize(orderPage.getPageSize());
        resultPage.setTotal(orderPage.getTotal());
        resultPage.setTotalPage(orderPage.getTotalPage());
        if (CollUtil.isEmpty(orderInfoList)) {
            return resultPage;
        }

        List<Long> orderIds = orderInfoList.stream().map(XmsShopifyOrderinfo::getOrderNo).collect(Collectors.toList());
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
        List<String> shopifyList = new ArrayList<>();
        for (XmsShopifyOrderinfo omsOrder : orderInfoList) {
            XmsShopifyOrderComb orderComb = new XmsShopifyOrderComb();
            BeanUtil.copyProperties(omsOrder, orderComb);

            shopifyList.add(orderComb.getShopifyName());

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
            combList.add(orderComb);
        }

        if (CollectionUtil.isNotEmpty(combList) && CollectionUtil.isNotEmpty(shopifyList)) {
            UmsMemberExample example = new UmsMemberExample();
            example.createCriteria().andShopifyNameIn(shopifyList);
            List<UmsMember> umsMembers = this.umsMemberMapper.selectByExample(example);
            if (CollectionUtil.isNotEmpty(umsMembers)) {
                Map<String, UmsMember> umsMemberMap = umsMembers.stream().collect(Collectors.toMap(UmsMember::getShopifyName, e -> e, (a, b) -> b));
                combList.forEach(e -> {
                    if (umsMemberMap.containsKey(e.getShopifyName())) {
                        e.setUserName(umsMemberMap.get(e.getShopifyName()).getUsername());
                    }
                });
                umsMembers.clear();
                umsMemberMap.clear();
            }
            shopifyList.clear();

        }

        resultPage.setList(combList);
        return resultPage;
    }

}
