package com.macro.mall.dao;

import com.macro.mall.entity.XmsMsg;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 消息管理自定义Dao
 * Created by zlw on 2021/4/21.
 */
public interface MsgDao {

    /**
     * 没有读过的用户消息记录
     */
    List<XmsMsg> unreadMsgList(@Param("mail") String mail, @Param("type") Integer type, @Param("startDate") String startDate,
                               @Param("endDate") String endDate, @Param("offset") Integer offset, @Param("limit") Integer limit);

    /**
     * 已读过的用户消息记录
     */
    List<XmsMsg> readMsgList(@Param("mail") String mail, @Param("type") Integer type, @Param("startDate") String startDate,
                             @Param("endDate") String endDate, @Param("offset") Integer offset, @Param("limit") Integer limit);

    int unreadMsgListCount(@Param("mail") String mail, @Param("type") Integer type, @Param("startDate") String startDate,
                           @Param("endDate") String endDate);

    int readMsgListCount(@Param("mail") String mail, @Param("type") Integer type, @Param("startDate") String startDate,
                         @Param("endDate") String endDate);

}
