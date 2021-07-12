package com.macro.mall.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.macro.mall.dao.MsgDao;
import com.macro.mall.domain.XmsMsgParam;
import com.macro.mall.entity.XmsMsg;
import com.macro.mall.mapper.XmsMsgMapper;
import com.macro.mall.service.IXmsMsgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
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
    public List<XmsMsg> unreadMsgList(XmsMsgParam xmsMsgParam, Integer pageNum, Integer pageSize) {
        int offset = pageSize * (pageNum - 1);
        return msgDao.unreadMsgList(xmsMsgParam.getEmail(), xmsMsgParam.getType(), xmsMsgParam.getStartDate(), xmsMsgParam.getEndDate(), offset, pageSize);
    }

    @Override
    public List<XmsMsg> readMsgList(XmsMsgParam xmsMsgParam, Integer pageNum, Integer pageSize) {
        int offset = pageSize * (pageNum - 1);
        return msgDao.readMsgList(xmsMsgParam.getEmail(), xmsMsgParam.getType(), xmsMsgParam.getStartDate(), xmsMsgParam.getEndDate(), offset, pageSize);
    }

    @Override
    public int unreadMsgListCount(XmsMsgParam xmsMsgParam) {

        return msgDao.unreadMsgListCount(xmsMsgParam.getEmail(), xmsMsgParam.getType(), xmsMsgParam.getStartDate(), xmsMsgParam.getEndDate());
    }

    @Override
    public int readMsgListCount(XmsMsgParam xmsMsgParam) {

        return msgDao.readMsgListCount(xmsMsgParam.getEmail(), xmsMsgParam.getType(), xmsMsgParam.getStartDate(), xmsMsgParam.getEndDate());
    }

    @Override
    public void insetMsgList(XmsMsgParam xmsMsgParam) {

        XmsMsg xmsMsg = new XmsMsg();

        xmsMsg.setMail(xmsMsgParam.getEmail());
        xmsMsg.setContent(xmsMsgParam.getContent());
        xmsMsg.setType(xmsMsgParam.getType());
        xmsMsg.setTitle(xmsMsgParam.getTitle());
        xmsMsg.setCreateTime(new Date());
        xmsMsg.setUpdateTime(new Date());
        xmsMsgMapper.insert(xmsMsg);
    }

}
