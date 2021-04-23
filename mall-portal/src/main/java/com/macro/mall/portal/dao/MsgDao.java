package com.macro.mall.portal.dao;

import com.macro.mall.model.XmsMsg;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 首页内容管理自定义Dao
 * Created by zlw on 2021/4/21.
 */
public interface MsgDao {

    /**
     * 没有读过的用户消息记录
     */
    List<XmsMsg> unreadMsgList(@Param("mail") String mail);

}
