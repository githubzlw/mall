package com.macro.mall.portal.service;

import com.macro.mall.model.XmsChromeUpload;
import com.macro.mall.portal.domain.XmsChromeUploadParam;

import java.util.List;

/**
 * 谷歌扩展插件抓取内容上传Service
 * Created by jack.luo on 2021/4/15.
 */
public interface XmsChromeUploadService {
    /**
     * 数据上传
     */
    void upload(XmsChromeUploadParam xmsChromeUploadParam);
    /**
     * 显示上传列表
     */
    List<XmsChromeUpload> list(Long memberId, Integer pageNum, Integer pageSize);
}
