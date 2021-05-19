package com.macro.mall.domain;

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
