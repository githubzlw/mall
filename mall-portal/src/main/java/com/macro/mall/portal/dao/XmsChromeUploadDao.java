package com.macro.mall.portal.dao;

import com.macro.mall.entity.XmsChromeUpload;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 谷歌扩展插件抓取内容上传自定义Dao
 * Created by jack.luo on 2021/4/15.
 */

public interface XmsChromeUploadDao {

    /**
     * 获取列表
     */
    List<XmsChromeUpload> getList(@Param("memberId") Long memberId, @Param("offset") Integer offset, @Param("limit") Integer limit);


}
