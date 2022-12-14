package com.macro.mall.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.math.BigDecimal;

public class PmsSkuStock implements Serializable {
    private Long id;

    private Long productId;

    @ApiModelProperty(value = "sku编码")
    private String skuCode;

    private BigDecimal price;

    @ApiModelProperty(value = "库存")
    private Integer stock;

    @ApiModelProperty(value = "预警库存")
    private Integer lowStock;

    @ApiModelProperty(value = "展示图片")
    private String pic;

    @ApiModelProperty(value = "销量")
    private Integer sale;

    @ApiModelProperty(value = "单品促销价格")
    private BigDecimal promotionPrice;

    @ApiModelProperty(value = "锁定库存")
    private Integer lockStock;

    @ApiModelProperty(value = "商品销售属性，json格式")
    private String spData;

    @ApiModelProperty(value = "最小批量价格（新加字段）")
    private BigDecimal minPrice;

    @ApiModelProperty(value = "最大批量价格（新加字段）")
    private BigDecimal maxPrice;

    @ApiModelProperty(value = "原价格批量（新加字段）")
    private Integer moq;

    @ApiModelProperty(value = "最小价格批量（新加字段）")
    private Integer minMoq;

    @ApiModelProperty(value = "最大价格批量（新加字段）")
    private Integer maxMoq;

    @ApiModelProperty(value = "重量（新加字段）")
    private BigDecimal weight;

    @ApiModelProperty(value = "体积长（新加字段）")
    private BigDecimal volumeLenght;

    @ApiModelProperty(value = "体积宽（新加字段）")
    private BigDecimal volumeWidth;

    @ApiModelProperty(value = "体积高（新加字段）")
    private BigDecimal volumeHeight;

    @ApiModelProperty(value = "需求体积")
    private Double volume;

    private String shipsFrom;

    private String profit;

    private String standard;

    private String skuvalue1;

    private String skuvalue2;

    private String skuvalue3;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getLowStock() {
        return lowStock;
    }

    public void setLowStock(Integer lowStock) {
        this.lowStock = lowStock;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public Integer getSale() {
        return sale;
    }

    public void setSale(Integer sale) {
        this.sale = sale;
    }

    public BigDecimal getPromotionPrice() {
        return promotionPrice;
    }

    public void setPromotionPrice(BigDecimal promotionPrice) {
        this.promotionPrice = promotionPrice;
    }

    public Integer getLockStock() {
        return lockStock;
    }

    public void setLockStock(Integer lockStock) {
        this.lockStock = lockStock;
    }

    public String getSpData() {
        return spData;
    }

    public void setSpData(String spData) {
        this.spData = spData;
    }

    public BigDecimal getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(BigDecimal minPrice) {
        this.minPrice = minPrice;
    }

    public BigDecimal getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(BigDecimal maxPrice) {
        this.maxPrice = maxPrice;
    }

    public Integer getMoq() {
        return moq;
    }

    public void setMoq(Integer moq) {
        this.moq = moq;
    }

    public Integer getMinMoq() {
        return minMoq;
    }

    public void setMinMoq(Integer minMoq) {
        this.minMoq = minMoq;
    }

    public Integer getMaxMoq() {
        return maxMoq;
    }

    public void setMaxMoq(Integer maxMoq) {
        this.maxMoq = maxMoq;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public BigDecimal getVolumeLenght() {
        return volumeLenght;
    }

    public void setVolumeLenght(BigDecimal volumeLenght) {
        this.volumeLenght = volumeLenght;
    }

    public BigDecimal getVolumeWidth() {
        return volumeWidth;
    }

    public void setVolumeWidth(BigDecimal volumeWidth) {
        this.volumeWidth = volumeWidth;
    }

    public BigDecimal getVolumeHeight() {
        return volumeHeight;
    }

    public void setVolumeHeight(BigDecimal volumeHeight) {
        this.volumeHeight = volumeHeight;
    }

    public Double getVolume() {
        return volume;
    }

    public void setVolume(Double volume) {
        this.volume = volume;
    }

    public String getShipsFrom() {
        return shipsFrom;
    }

    public void setShipsFrom(String shipsFrom) {
        this.shipsFrom = shipsFrom;
    }

    public String getProfit() {
        return profit;
    }

    public void setProfit(String profit) {
        this.profit = profit;
    }

    public String getStandard() {
        return standard;
    }

    public void setStandard(String standard) {
        this.standard = standard;
    }

    public String getSkuvalue1() {
        return skuvalue1;
    }

    public void setSkuvalue1(String skuvalue1) {
        this.skuvalue1 = skuvalue1;
    }

    public String getSkuvalue2() {
        return skuvalue2;
    }

    public void setSkuvalue2(String skuvalue2) {
        this.skuvalue2 = skuvalue2;
    }

    public String getSkuvalue3() {
        return skuvalue3;
    }

    public void setSkuvalue3(String skuvalue3) {
        this.skuvalue3 = skuvalue3;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", productId=").append(productId);
        sb.append(", skuCode=").append(skuCode);
        sb.append(", price=").append(price);
        sb.append(", stock=").append(stock);
        sb.append(", lowStock=").append(lowStock);
        sb.append(", pic=").append(pic);
        sb.append(", sale=").append(sale);
        sb.append(", promotionPrice=").append(promotionPrice);
        sb.append(", lockStock=").append(lockStock);
        sb.append(", spData=").append(spData);
        sb.append(", minPrice=").append(minPrice);
        sb.append(", maxPrice=").append(maxPrice);
        sb.append(", moq=").append(moq);
        sb.append(", minMoq=").append(minMoq);
        sb.append(", maxMoq=").append(maxMoq);
        sb.append(", weight=").append(weight);
        sb.append(", volumeLenght=").append(volumeLenght);
        sb.append(", volumeWidth=").append(volumeWidth);
        sb.append(", volumeHeight=").append(volumeHeight);
        sb.append(", volume=").append(volume);
        sb.append(", shipsFrom=").append(shipsFrom);
        sb.append(", profit=").append(profit);
        sb.append(", standard=").append(standard);
        sb.append(", skuvalue1=").append(skuvalue1);
        sb.append(", skuvalue2=").append(skuvalue2);
        sb.append(", skuvalue3=").append(skuvalue3);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}