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
 * 支付表
 * </p>
 *
 * @author jack.luo
 * @since 2021-09-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="XmsPaymentLog对象", description="支付表")
public class XmsPaymentLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "会员ID")
    private Long memberId;

    @ApiModelProperty(value = "会员登录名（邮箱）")
    private String username;

    @ApiModelProperty(value = "订单id")
    private String orderNo;

    @ApiModelProperty(value = "paypal付款流水号tx")
    private String paymentId;

    @ApiModelProperty(value = "付款金额")
    private Float paymentAmount;

    @ApiModelProperty(value = "付款状态:0 失败(Failed) 1 成功(Success) 2进行中(Pending)")
    private Integer payStatus;

    @ApiModelProperty(value = "本地交易申请号")
    private String paySid;

    @ApiModelProperty(value = "0是paypal支付，1 余额支付 ")
    private Integer payType;

    @ApiModelProperty(value = "paypal回调返回的id")
    private String paymentNo;

    @ApiModelProperty(value = "交易费用")
    private Double transactionFee;

    @ApiModelProperty(value = "paypal返回的ID")
    private String paypalId;

    @ApiModelProperty(value = "订单支付时的汇率")
    private Double exchangeRate;

    @ApiModelProperty(value = "订单描述")
    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "支付来源 0未知 1采购库存 2sourcing下单 3充值")
    private Integer payFrom;

    @ApiModelProperty(value = "订单信息")
    private String orderInfo;


}
