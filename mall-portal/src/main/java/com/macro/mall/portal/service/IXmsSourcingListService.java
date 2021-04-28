package com.macro.mall.portal.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.macro.mall.entity.XmsCustomerProduct;
import com.macro.mall.entity.XmsSourcingList;
import com.baomidou.mybatisplus.extension.service.IService;
import com.macro.mall.portal.domain.XmsSourcingInfoParam;

/**
 * <p>
 * sourcing表 服务类
 * </p>
 *
 * @author jack.luo
 * @since 2021-04-28
 */
public interface IXmsSourcingListService extends IService<XmsSourcingList> {

    Page<XmsSourcingList> list(XmsSourcingInfoParam sourcingParam);

    boolean checkHasXmsCustomerProduct(XmsCustomerProduct product);

}
