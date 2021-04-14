package com.macro.mall.mapper;

import com.macro.mall.model.XmsChromeUpload;
import com.macro.mall.model.XmsChromeUploadExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface XmsChromeUploadMapper {
    long countByExample(XmsChromeUploadExample example);

    int deleteByExample(XmsChromeUploadExample example);

    int deleteByPrimaryKey(Long id);

    int insert(XmsChromeUpload record);

    int insertSelective(XmsChromeUpload record);

    List<XmsChromeUpload> selectByExample(XmsChromeUploadExample example);

    XmsChromeUpload selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") XmsChromeUpload record, @Param("example") XmsChromeUploadExample example);

    int updateByExample(@Param("record") XmsChromeUpload record, @Param("example") XmsChromeUploadExample example);

    int updateByPrimaryKeySelective(XmsChromeUpload record);

    int updateByPrimaryKey(XmsChromeUpload record);
}