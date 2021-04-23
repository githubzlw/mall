package com.macro.mall.mapper;

import com.macro.mall.model.XmsMsgrecycle;
import com.macro.mall.model.XmsMsgrecycleExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface XmsMsgrecycleMapper {
    long countByExample(XmsMsgrecycleExample example);

    int deleteByExample(XmsMsgrecycleExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(XmsMsgrecycle record);

    int insertSelective(XmsMsgrecycle record);

    List<XmsMsgrecycle> selectByExample(XmsMsgrecycleExample example);

    XmsMsgrecycle selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") XmsMsgrecycle record, @Param("example") XmsMsgrecycleExample example);

    int updateByExample(@Param("record") XmsMsgrecycle record, @Param("example") XmsMsgrecycleExample example);

    int updateByPrimaryKeySelective(XmsMsgrecycle record);

    int updateByPrimaryKey(XmsMsgrecycle record);
}