package com.macro.mall.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.macro.mall.entity.XmsCustomerProduct;
import com.macro.mall.mapper.XmsCustomerProductMapper;
import com.macro.mall.service.IXmsCustomerProductService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * <p>
 * 客户的产品表 服务实现类
 * </p>
 *
 * @author jack.luo
 * @since 2021-04-28
 */
@Service
public class XmsCustomerProductServiceImpl extends ServiceImpl<XmsCustomerProductMapper, XmsCustomerProduct> implements IXmsCustomerProductService {

}
