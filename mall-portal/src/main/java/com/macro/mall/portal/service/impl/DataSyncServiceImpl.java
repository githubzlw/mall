package com.macro.mall.portal.service.impl;

import com.macro.mall.common.exception.Asserts;
import com.macro.mall.mapper.PmsProductMapper;
import com.macro.mall.mapper.UmsMemberLevelMapper;
import com.macro.mall.mapper.UmsMemberMapper;
import com.macro.mall.model.*;
import com.macro.mall.portal.domain.MemberDetails;
import com.macro.mall.portal.service.DataSyncService;
import com.macro.mall.portal.service.UmsMemberCacheService;
import com.macro.mall.portal.service.UmsMemberService;
import com.macro.mall.security.util.JwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * 会员管理Service实现类
 * Created by macro on 2018/8/3.
 */
@Service
public class DataSyncServiceImpl implements DataSyncService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataSyncServiceImpl.class);

    @Autowired
    private UmsMemberMapper memberMapper;

    @Autowired
    private PmsProductMapper pmsProductMapper;

    @Override
    public List<UmsMember> getAllUser(Long id) {

        UmsMemberExample example = new UmsMemberExample();
        if (id != null && id > 0) {
            example.createCriteria().andIdEqualTo(id);
        }

        List<UmsMember> memberList = memberMapper.selectByExample(example);

        return memberList;
    }

    @Override
    public List<PmsProduct> getAllProduct(Long id) {
        PmsProductExample example = new PmsProductExample();
        if (id != null && id > 0) {
            example.createCriteria().andIdEqualTo(id);
        }

        List<PmsProduct> pmsProductList = pmsProductMapper.selectByExample(example);

        return pmsProductList;
    }

}
