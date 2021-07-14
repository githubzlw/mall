package com.macro.mall.portal.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.macro.mall.entity.UmsSourcingSearchLog;
import com.macro.mall.portal.domain.SourcingSearchParam;

/**
 * <p>
 * 公告表 服务类
 * </p>
 *
 * @author jack.luo
 * @since 2021-04-22
 */
public interface UmsSourcingSearchLogService extends IService<UmsSourcingSearchLog> {
    /**
     * 搜索记录
     */
    Page<UmsSourcingSearchLog> getSearchLogList(SourcingSearchParam sourcingSearchParam, Integer pageNum, Integer pageSize);


    void insertSourcingSearchLog(SourcingSearchParam sourcingSearchParam);

}
