package com.macro.mall.portal.service.impl;

import com.macro.mall.mapper.XmsMsgrecycleMapper;
import com.macro.mall.model.XmsMsg;
import com.macro.mall.model.XmsMsgrecycle;
import com.macro.mall.model.XmsMsgrecycleExample;
import com.macro.mall.portal.dao.MsgDao;
import com.macro.mall.portal.service.MsgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 首页内容管理Service实现类
 * Created by zlw on 2021/4/23.
 */
@Service
public class MsgServiceImpl implements MsgService {

    @Autowired
    private MsgDao msgDao;
    @Autowired
    private XmsMsgrecycleMapper msgrecycleMapper;

    @Override
    public List<XmsMsgrecycle> readMsgList(String mail) {
        XmsMsgrecycleExample example = new XmsMsgrecycleExample();
        example.createCriteria()
                .andUidEqualTo(mail)
                .andIsreadEqualTo(1)
                .andIsdeleteEqualTo(0);
        return msgrecycleMapper.selectByExample(example);
    }

    @Override
    public List<XmsMsg> unreadMsgList(String userName) {
        return msgDao.unreadMsgList(userName);
    }

}
