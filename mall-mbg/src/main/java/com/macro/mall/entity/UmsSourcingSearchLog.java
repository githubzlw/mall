package com.macro.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * sourcing搜索记录
 * </p>
 *
 * @author jack.luo
 * @since 2021-07-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "UmsSourcingSearchLog对象", description = "sourcing搜索记录")
public class UmsSourcingSearchLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String sourcingSearch;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    private String ip;


}
