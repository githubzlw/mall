package com.macro.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.macro.mall.entity.XmsMsgrecycle;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author jack.luo
 * @since 2021-04-22
 */
public interface IXmsMsgrecycleService extends IService<XmsMsgrecycle> {

    /**
     * 插入用户已读消息记录
     */
    void insetMsgRecycle(String uid, Integer msgid);


    /**
     * 删除消息
     */
    void updateMsgRecycle(String uid, Integer msgid);
}
