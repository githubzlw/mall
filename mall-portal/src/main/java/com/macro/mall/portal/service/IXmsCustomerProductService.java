package com.macro.mall.portal.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.macro.mall.entity.XmsCustomerProduct;
import com.macro.mall.portal.domain.XmsCustomerProductParam;

import java.util.List;

/**
 * <p>
 * 客户的产品表 服务类
 * </p>
 *
 * @author jack.luo
 * @since 2021-04-28
 */
public interface IXmsCustomerProductService extends IService<XmsCustomerProduct> {

    Page<XmsCustomerProduct> list(XmsCustomerProductParam sourcingParam);


    List<XmsCustomerProduct> queryByUserInfo(String userName, Long memberId);

}
