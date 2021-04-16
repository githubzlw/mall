package com.macro.mall.portal.service.mapstruct;

import com.macro.mall.common.base.BaseMapper;
import com.macro.mall.entity.XmsChromeUpload;
import com.macro.mall.portal.domain.XmsChromeUploadParam;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
* @author jack.luo
* @date 2021-04-15
*/
@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface XmsChromeUploadMapstruct extends BaseMapper<XmsChromeUpload, XmsChromeUploadParam> {
}