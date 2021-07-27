package com.macro.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.macro.mall.domain.XmsMsgParam;
import com.macro.mall.entity.XmsMsg;

import java.util.List;

/**
 * <p>
 * 公告表 服务类
 * </p>
 *
 * @author jack.luo
 * @since 2021-04-22
 */
public interface IXmsMsgService extends IService<XmsMsg> {
    /**
     * 没有读过的用户消息记录
     */
    List<XmsMsg> unreadMsgList(XmsMsgParam xmsMsgParam, Integer pageNum, Integer pageSize);

    /**
     * 已读的用户消息记录
     */
    List<XmsMsg> readMsgList(XmsMsgParam xmsMsgParam, Integer pageNum, Integer pageSize);

    void insetMsgList(XmsMsgParam xmsMsgParam);


    int readMsgListCount(XmsMsgParam xmsMsgParam);

    int unreadMsgListCount(XmsMsgParam xmsMsgParam);
}
