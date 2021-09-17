package com.macro.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 抓取PID图片错误记录
 * </p>
 *
 * @author jack.luo
 * @since 2021-09-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="XmsShopifyPidImgError对象", description="抓取PID图片错误记录")
public class XmsShopifyPidImgError implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "店铺名称")
    private String shopifyName;

    @ApiModelProperty(value = "商品的id")
    private String shopifyPid;

    private Long total;


}
