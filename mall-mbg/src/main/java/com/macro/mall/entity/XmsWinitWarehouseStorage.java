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
 * 查询总库存
 * </p>
 *
 * @author jack.luo
 * @since 2021-10-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="XmsWinitWarehouseStorage对象", description="查询总库存")
public class XmsWinitWarehouseStorage implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "商品名称")
    private String productName;

    @ApiModelProperty(value = "万邑通仓库ID")
    @TableField("warehouse_iD")
    private String warehouseId;

    @ApiModelProperty(value = "万邑通仓库Code")
    private String warehouseCode;

    @ApiModelProperty(value = "万邑通仓库名称")
    private String warehouseName;

    @ApiModelProperty(value = "在库库存")
    private String inventory;

    @ApiModelProperty(value = "可用库存	")
    private String qtyAvailable;

    @ApiModelProperty(value = "禁止出库库存")
    private String prohibitUsableQty;

    @ApiModelProperty(value = "在途库存")
    private String pipelineInventory;

    @ApiModelProperty(value = "待出库库存	待出库=出库订单占用+冻结库存+丢失确认中库存")
    private String reservedInventory;

    @ApiModelProperty(value = "存储仓库存")
    private String qtySw;

    @ApiModelProperty(value = "商品编码")
    private String productCode;

    @ApiModelProperty(value = "商品规格")
    private String specification;

    @ApiModelProperty(value = "是否失效")
    private String isActive;

    @ApiModelProperty(value = "历史代销量")
    private String qtySellHisOut;

    @ApiModelProperty(value = "产品重量(kg)")
    private Double productWeight;

    @ApiModelProperty(value = "产品长度(cm)")
    private Double productLength;

    @ApiModelProperty(value = "产品高度(cm)")
    private Double productHeight;

    @ApiModelProperty(value = "产品宽度(cm)")
    private Double productWidth;

    @ApiModelProperty(value = "每页的数据量")
    private Integer currentPageSize;

    @ApiModelProperty(value = "总数据页数")
    private Integer currentPageNum;

    @ApiModelProperty(value = "总数据量")
    private Integer total;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;


}
