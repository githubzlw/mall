package com.macro.mall.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.importExpress.common.pojo
 * @date:2021-04-01
 */
@Data
@ApiModel(value = "网站buyForMe", description = "buyForMe的Bean")
public class SiteSourcingParam {

    @ApiModelProperty("PID")
    private String pid;
    @ApiModelProperty("BFM的商品URL")
    private String url;
    @ApiModelProperty("BFM的商品名称")
    private String name;
    @ApiModelProperty("BFM的图片链接")
    private String img;
    /**
     * 1 速卖通 2 taobao 3...
     */
    private int siteFlag;


}
