package com.macro.mall.shopify.pojo.product;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OptionWrap {
    private List<String> lstImages;
    private List<Options> options;
    private List<Variants> variants;
}
