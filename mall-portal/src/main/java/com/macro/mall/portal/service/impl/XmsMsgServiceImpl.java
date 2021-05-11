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
    public List<XmsMsg> unreadMsgList(XmsMsgParam xmsMsgParam,Integer pageNum, Integer pageSize) {
        int offset = pageSize * (pageNum - 1);
        return msgDao.unreadMsgList(xmsMsgParam.getEmail(),xmsMsgParam.getType(),xmsMsgParam.getStartDate(),xmsMsgParam.getEndDate(),offset, pageSize);
    }

    @Override
    public List<XmsMsg> readMsgList(XmsMsgParam xmsMsgParam,Integer pageNum, Integer pageSize) {
        int offset = pageSize * (pageNum - 1);
        return msgDao.readMsgList(xmsMsgParam.getEmail(),xmsMsgParam.getType(),xmsMsgParam.getStartDate(),xmsMsgParam.getEndDate(),offset, pageSize);
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
