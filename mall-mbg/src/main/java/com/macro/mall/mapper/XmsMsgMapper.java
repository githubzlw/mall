package com.macro.mall.mapper;

import com.macro.mall.model.XmsMsg;
import com.macro.mall.model.XmsMsgExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface XmsMsgMapper {
    long countByExample(XmsMsgExample example);

    int deleteByExample(XmsMsgExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(XmsMsg record);

    int insertSelective(XmsMsg record);

    List<XmsMsg> selectByExampleWithBLOBs(XmsMsgExample example);

    List<XmsMsg> selectByExample(XmsMsgExample example);

    XmsMsg selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") XmsMsg record, @Param("example") XmsMsgExample example);

    int updateByExampleWithBLOBs(@Param("record") XmsMsg record, @Param("example") XmsMsgExample example);

    int updateByExample(@Param("record") XmsMsg record, @Param("example") XmsMsgExample example);

    int updateByPrimaryKeySelective(XmsMsg record);

    int updateByPrimaryKeyWithBLOBs(XmsMsg record);

    int updateByPrimaryKey(XmsMsg record);
}