package com.macro.mall.shopify.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.macro.mall.entity.XmsSourcingList;
import com.macro.mall.mapper.XmsSourcingListMapper;
import com.macro.mall.shopify.service.IXmsSourcingListService;
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
public class XmsSourcingListServiceImpl extends ServiceImpl<XmsSourcingListMapper, XmsSourcingList> implements IXmsSourcingListService {}
