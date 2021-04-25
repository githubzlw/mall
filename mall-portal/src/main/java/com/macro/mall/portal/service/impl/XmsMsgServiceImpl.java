package com.macro.mall.portal.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.macro.mall.entity.XmsMsg;
import com.macro.mall.mapper.XmsMsgMapper;
import com.macro.mall.portal.dao.MsgDao;
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
    @Override
    public List<XmsMsg> unreadMsgList(String userName) {
        return msgDao.unreadMsgList(userName);
    }
}
