package com.macro.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 客户余额变更记录
 * </p>
 *
 * @author jack.luo
 * @since 2021-05-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="XmsRecordOfChangeInBalance对象", description="客户余额变更记录")
public class XmsRecordOfChangeInBalance implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "订单id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long memberId;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "当前余额")
    private Double currentBalance;

    @ApiModelProperty(value = "操作值")
    private Double operatingValue;

    @ApiModelProperty(value = "操作类型 0:扣除余额 1:增加余额")
    private Integer operatingType;

    @ApiModelProperty(value = "操作结果")
    private Double operatingResult;

    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;


}
