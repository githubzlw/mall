package com.macro.mall.portal.service;

import com.macro.mall.portal.domain.XmsChromeUploadParam;

/**
 * 谷歌扩展插件抓取内容上传Service
 * Created by jack.luo on 2021/4/15.
 */
public interface XmsChromeUploadService {
    /**
     * 数据上传
     */
    void upload(XmsChromeUploadParam xmsChromeUploadParam);

}
