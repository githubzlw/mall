package com.macro.mall.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.macro.mall.dto.XmsSourcingInfoParam;
import com.macro.mall.entity.XmsSourcingList;

/**
 * <p>
 * sourcing表 服务类
 * </p>
 *
 * @author jack.luo
 * @since 2021-04-20
 */
public interface IXmsSourcingListService extends IService<XmsSourcingList> {

    Page<XmsSourcingList> list(XmsSourcingInfoParam sourcingParam);

    void updateSourceLink(XmsSourcingList sourcingInfo);

}
