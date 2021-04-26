package com.macro.mall.portal.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.macro.mall.entity.XmsMsgrecycle;
import com.macro.mall.mapper.XmsMsgrecycleMapper;
import com.macro.mall.portal.service.IXmsMsgrecycleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author jack.luo
 * @since 2021-04-22
 */
@Service
public class XmsMsgrecycleServiceImpl extends ServiceImpl<XmsMsgrecycleMapper, XmsMsgrecycle> implements IXmsMsgrecycleService {

    @Autowired
    private XmsMsgrecycleMapper xmsMsgrecycleMapper;


//    @Override
//    public List<XmsMsgrecycle> readMsgList(String mail) {
//
//        //封装查询条件
//        LambdaQueryWrapper<XmsMsgrecycle> query
//                = Wrappers.<XmsMsgrecycle>lambdaQuery().eq(XmsMsgrecycle::getUid, mail).eq(XmsMsgrecycle::getIsread,1).eq(XmsMsgrecycle::getIsdelete,0);
//
//        return xmsMsgrecycleMapper.selectList(query);
//    }
}
