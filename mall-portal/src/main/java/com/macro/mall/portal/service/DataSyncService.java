package com.macro.mall.portal.service;

import com.macro.mall.model.PmsProduct;
import com.macro.mall.model.UmsMember;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 数据同步Service
 * Created by jinjie on 2021/5/14.
 */
public interface DataSyncService {

    /**
     * 获取所有的用户信息
     */
    List<UmsMember> getAllUser(Long id);

    /**
     * 获取所有的用户信息
     */
    List<PmsProduct> getAllProduct(Long id);


}
