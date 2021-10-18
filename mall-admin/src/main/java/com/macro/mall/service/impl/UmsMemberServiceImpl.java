package com.macro.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.macro.mall.entity.XmsListOfCountries;
import com.macro.mall.mapper.UmsMemberMapper;
import com.macro.mall.mapper.XmsListOfCountriesMapper;
import com.macro.mall.model.UmsMember;
import com.macro.mall.model.UmsMemberExample;
import com.macro.mall.service.UmsMemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 后台用户管理Service实现类
 * Created by macro on 2018/4/26.
 */
@Service
public class UmsMemberServiceImpl implements UmsMemberService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UmsMemberServiceImpl.class);

    @Autowired
    private UmsMemberMapper umsMemberMapper;

    @Autowired
    private XmsListOfCountriesMapper listOfCountriesMapper;


    @Override
    public List<UmsMember> list(String keyword, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        UmsMemberExample example = new UmsMemberExample();
        UmsMemberExample.Criteria criteria = example.createCriteria();
        if (!StringUtils.isEmpty(keyword)) {
            criteria.andUsernameLike("%" + keyword + "%");
            example.or(example.createCriteria().andNicknameLike("%" + keyword + "%"));
        }

        example.setOrderByClause("create_time desc");

        List<UmsMember> umsMemberList = umsMemberMapper.selectByExample(example);

        QueryWrapper<XmsListOfCountries> queryWrapper = new QueryWrapper<>();
        List<XmsListOfCountries> xmsListOfCountriesList = listOfCountriesMapper.selectList(queryWrapper);
        for (UmsMember umsMember : umsMemberList) {
            for (XmsListOfCountries xmsListOfCountries : xmsListOfCountriesList) {
                if (umsMember.getCountryId() == xmsListOfCountries.getId()) {
                    umsMember.setCountryName(xmsListOfCountries.getEnglishNameOfCountry());
                    break;
                }
            }
        }

        return umsMemberList;
    }

    @Override
    public Long getListCount() {
        UmsMemberExample example = new UmsMemberExample();
        example.createCriteria().andShopifyNameIsNotNull();
        return umsMemberMapper.countByExample(example);
    }

    @Override
    public List<UmsMember> getListByLimit(Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        UmsMemberExample example = new UmsMemberExample();
        example.createCriteria().andShopifyNameIsNotNull();
        return umsMemberMapper.selectByExample(example);
    }

    @Override
    public List<UmsMember> getByList(List<Long> list) {
        UmsMemberExample example = new UmsMemberExample();
        example.createCriteria().andIdIn(list);
        return umsMemberMapper.selectByExample(example);
    }


}
