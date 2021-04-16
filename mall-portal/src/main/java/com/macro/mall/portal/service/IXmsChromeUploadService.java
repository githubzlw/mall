package com.macro.mall.portal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.macro.mall.entity.XmsChromeUpload;
import com.macro.mall.portal.domain.XmsChromeUploadParam;

import java.util.List;

/**
 * <p>
 * 谷歌扩展插件抓取内容上传 服务类
 * </p>
 *
 * @author jack.luo
 * @since 2021-04-16
 */
public interface IXmsChromeUploadService extends IService<XmsChromeUpload> {

    /**
     * 数据上传
     */
    void upload(XmsChromeUploadParam xmsChromeUploadParam);

    /**
     * 显示上传列表
     */
    List<XmsChromeUpload> list(Long memberId, Integer pageNum, Integer pageSize);
}
