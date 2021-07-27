package com.macro.mall.service;

import com.macro.mall.dto.UmsAdminParam;
import com.macro.mall.dto.UpdateAdminPasswordParam;
import com.macro.mall.model.UmsAdmin;
import com.macro.mall.model.UmsMember;
import com.macro.mall.model.UmsResource;
import com.macro.mall.model.UmsRole;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 后台用户管理Service
 * Created by macro on 2018/4/26.
 */
public interface UmsMemberService {

    /**
     * 根据用户名或昵称分页查询用户
     */
    List<UmsMember> list(String keyword, Integer pageSize, Integer pageNum);


}
