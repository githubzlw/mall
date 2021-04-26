package com.macro.mall.portal.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.macro.mall.entity.XmsMsg;
import com.macro.mall.mapper.XmsMsgMapper;
import com.macro.mall.portal.dao.MsgDao;
import com.macro.mall.portal.domain.XmsMsgParam;
import com.macro.mall.portal.service.IXmsMsgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 公告表 服务实现类
 * </p>
 *
 * @author jack.luo
 * @since 2021-04-22
 */
@Service
public class XmsMsgServiceImpl extends ServiceImpl<XmsMsgMapper, XmsMsg> implements IXmsMsgService {

    @Autowired
    private MsgDao msgDao;

    @Autowired
    private XmsMsgMapper xmsMsgMapper;

    @Override
    public List<XmsMsg> unreadMsgList(String mail, Integer type) {

        return msgDao.unreadMsgList(mail,type);
    }

    @Override
    public List<XmsMsg> readMsgList(String mail, Integer type) {

        return msgDao.readMsgList(mail,type);
    }

    @Override
    public void insetMsgList(XmsMsgParam xmsMsgParam) {

        XmsMsg xmsMsg = new XmsMsg();

        xmsMsg.setMail(xmsMsgParam.getEmail());
        xmsMsg.setContent(xmsMsgParam.getContent());
        xmsMsg.setType(xmsMsgParam.getType());
        xmsMsg.setTitle(xmsMsgParam.getTitle());
        xmsMsgMapper.insert(xmsMsg);
    }

}
