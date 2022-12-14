package com.macro.mall.service;

import com.macro.mall.model.UmsMember;

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

    Long getListCount();

    List<UmsMember> getListByLimit(Integer pageSize, Integer pageNum);

    List<UmsMember> getByList(List<Long> list);

}
