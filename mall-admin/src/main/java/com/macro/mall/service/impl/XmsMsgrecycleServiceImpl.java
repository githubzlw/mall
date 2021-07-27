package com.macro.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.macro.mall.entity.XmsMsgrecycle;
import com.macro.mall.mapper.XmsMsgrecycleMapper;
import com.macro.mall.service.IXmsMsgrecycleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zlw
 * @since 2021-04-22
 */
@Service
public class XmsMsgrecycleServiceImpl extends ServiceImpl<XmsMsgrecycleMapper, XmsMsgrecycle> implements IXmsMsgrecycleService {

    @Autowired
    private XmsMsgrecycleMapper xmsMsgrecycleMapper;

    @Override
    public void insetMsgRecycle(String uid, Integer msgid) {

        XmsMsgrecycle xmsMsgrecycle = new XmsMsgrecycle();
        xmsMsgrecycle.setUid(uid);
        xmsMsgrecycle.setMsgid(msgid);
        xmsMsgrecycleMapper.insert(xmsMsgrecycle);
    }

    @Override
    public void updateMsgRecycle(String uid, Integer msgid) {

        UpdateWrapper<XmsMsgrecycle> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("uid", uid).eq("msgid", msgid).set("isdelete", 1);

        xmsMsgrecycleMapper.update(null, updateWrapper);
    }

}
