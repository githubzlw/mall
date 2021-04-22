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
 * 
 * </p>
 *
 * @author jack.luo
 * @since 2021-04-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="XmsMsgrecycle对象", description="")
public class XmsMsgrecycle implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "用户id")
    private String uid;

    @ApiModelProperty(value = "消息id")
    private Integer msgid;

    @ApiModelProperty(value = "0-未读  1-已读")
    private Integer isread;

    @ApiModelProperty(value = "0-未删除 1-删除")
    private Boolean isdelete;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;


}
