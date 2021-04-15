package com.macro.mall.portal.service.impl;

import com.macro.mall.common.exception.Asserts;
import com.macro.mall.mapper.UmsMemberMapper;
import com.macro.mall.mapper.XmsChromeUploadMapper;
import com.macro.mall.model.*;
import com.macro.mall.portal.domain.XmsChromeUploadParam;
import com.macro.mall.portal.service.XmsChromeUploadService;
import com.macro.mall.portal.service.mapstruct.XmsChromeUploadMapstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

/**
 * @author jack.luo
 * @date 2021/4/15
 */
@Service
public class XmsChromeUploadServiceImpl implements XmsChromeUploadService {

    @Autowired
    private UmsMemberMapper memberMapper;

    @Autowired
    private XmsChromeUploadMapper xmsChromeUploadMapper;

    @Autowired
    private XmsChromeUploadMapstruct mapstruct;

    @Override
    public void upload(XmsChromeUploadParam xmsChromeUploadParam) {

        //查询是否已有该用户
        UmsMemberExample example = new UmsMemberExample();
        example.createCriteria().andUsernameEqualTo(xmsChromeUploadParam.getUsername());
        List<UmsMember> umsMembers = memberMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(umsMembers)) {
            Asserts.fail("用户不存在");
        }
        //进行添加操作
        XmsChromeUpload xmsChromeUpload = mapstruct.toDto(xmsChromeUploadParam);
        if(xmsChromeUploadParam.getUrl().contains("aliexpress.com")) {
            //速卖通
            xmsChromeUpload.setSiteType(2);
        }else{
            Asserts.fail("不属于抓取网站范围");
        }
        xmsChromeUpload.setMemberId(umsMembers.get(0).getId());
        xmsChromeUpload.setStatus(1);
        Date now = new Date();
        xmsChromeUpload.setCreateTime(now);
        xmsChromeUpload.setUpdateTime(now);

        xmsChromeUploadMapper.insert(xmsChromeUpload);

    }
}
