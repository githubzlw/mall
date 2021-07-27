package com.macro.mall.portal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.macro.mall.common.enums.ChromeUploadSiteEnum;
import com.macro.mall.common.exception.Asserts;
import com.macro.mall.entity.XmsChromeUpload;
import com.macro.mall.mapper.UmsMemberMapper;
import com.macro.mall.mapper.XmsChromeUploadMapper;
import com.macro.mall.model.UmsMember;
import com.macro.mall.model.UmsMemberExample;
import com.macro.mall.portal.domain.XmsChromeUploadParam;
import com.macro.mall.portal.service.IXmsChromeUploadService;
import com.macro.mall.portal.service.mapstruct.XmsChromeUploadMapstruct;
import com.macro.mall.security.util.JwtTokenUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author jack.luo
 * @date 2021/4/15
 */
@Service
public class XmsChromeUploadServiceImpl extends ServiceImpl<XmsChromeUploadMapper, XmsChromeUpload> implements IXmsChromeUploadService {

    @Autowired
    private UmsMemberMapper memberMapper;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private XmsChromeUploadMapper xmsChromeUploadMapper;

    @Autowired
    private XmsChromeUploadMapstruct mapstruct;

    @Override
    public void upload(XmsChromeUploadParam xmsChromeUploadParam) {

        String token = xmsChromeUploadParam.getToken();
        String username="";
        Long userId=0L;
        if(StringUtils.isNotEmpty(token)){
            if(token.startsWith("TOURIST_")){
                //游客
                username = token;
            }else{
                //查询是否已有该用户
                UmsMemberExample example = new UmsMemberExample();
                username = jwtTokenUtil.getUserNameFromToken(token);
                if(StringUtils.isNotEmpty(username)){
                    example.createCriteria().andUsernameEqualTo(username);
                    List<UmsMember> umsMembers = memberMapper.selectByExample(example);
                    if (CollectionUtils.isEmpty(umsMembers)) {
                        Asserts.fail("用户不存在");
                    }
                    userId = umsMembers.get(0).getId();
                }else{
                    Asserts.fail("token无效");
                }
            }
        }else{
            Asserts.fail("未传用户信息");
        }

        //进行添加操作
        XmsChromeUpload xmsChromeUpload = mapstruct.toDto(xmsChromeUploadParam);

        //判断是否属于抓取网站范围
        // List<ChromeUploadSiteEnum> collectFilter = Arrays.stream(ChromeUploadSiteEnum.values()).filter(i -> xmsChromeUploadParam.getUrl().contains(i.getSiteDomain())).collect(Collectors.toList());

        String pattern = "(https:\\/\\/)?(\\w)*\\.(%s)\\.com";
        List<ChromeUploadSiteEnum> collectFilter = Arrays.stream(ChromeUploadSiteEnum.values()).filter(i -> {
            String patternRs = String.format(pattern, i.getSiteName());
            Pattern r = Pattern.compile(patternRs);
            Matcher m = r.matcher(xmsChromeUploadParam.getUrl());
            return m.find();
        }).collect(Collectors.toList());

        if(collectFilter.size() ==1 ){
            xmsChromeUpload.setSiteType(collectFilter.get(0).getSiteType());
        }else{
            Asserts.fail("不属于抓取网站范围");
        }

        xmsChromeUpload.setMemberId(userId);
        xmsChromeUpload.setStatus(1);
        xmsChromeUpload.setUsername(username);

        boolean result = this.save(xmsChromeUpload);
        Asserts.isTrue(result, "插入数据库失败");

    }

    @Override
    public Page<XmsChromeUpload> list(Long memberId, Integer pageNum, Integer pageSize) {

        Page<XmsChromeUpload> page = new Page<>(pageNum, pageSize);

        //封装查询条件
        LambdaQueryWrapper<XmsChromeUpload> query
                = Wrappers.<XmsChromeUpload>lambdaQuery().eq(XmsChromeUpload::getMemberId, memberId).orderByDesc(XmsChromeUpload::getUpdateTime);
        Page<XmsChromeUpload> xmsChromeUploadPage = xmsChromeUploadMapper.selectPage(page, query);
        return xmsChromeUploadPage;

    }
}
