package com.macro.mall.portal.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.macro.mall.entity.XmsRecordOfChangeInBalance;

/**
 * <p>
 * 客户余额变更记录 服务类
 * </p>
 *
 * @author jack.luo
 * @since 2021-05-10
 */
public interface IXmsRecordOfChangeInBalanceService extends IService<XmsRecordOfChangeInBalance> {

    Page<XmsRecordOfChangeInBalance> list(Integer pageNum, Integer pageSize, String userName);

}
