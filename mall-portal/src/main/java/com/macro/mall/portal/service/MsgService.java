package com.macro.mall.portal.service;

import com.macro.mall.model.XmsMsg;
import com.macro.mall.model.XmsMsgrecycle;

import java.util.List;

/**
 * 首页内容管理Service
 * Created by zlw on 2021/04/23.
 */
public interface MsgService {

    /**
     * 已读的用户消息记录
     */
    List<XmsMsgrecycle> readMsgList(String userName);

    /**
     * 没有读过的用户消息记录
     */
    List<XmsMsg> unreadMsgList(String userName);
}
