package com.macro.mall.portal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.macro.mall.entity.XmsMsg;
import com.macro.mall.entity.XmsMsgrecycle;
import com.macro.mall.portal.domain.XmsMsgParam;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jack.luo
 * @since 2021-04-22
 */
public interface IXmsMsgrecycleService extends IService<XmsMsgrecycle> {

    /**
     * 插入用户已读消息记录
     */
    void insetMsgRecycle(String uid,Integer msgid);


    /**
     * 删除消息
     */
    void updateMsgRecycle(String uid,Integer msgid);
}
