package com.macro.mall.portal.domain;

import lombok.Builder;
import lombok.Data;

/**
 * @Author jack.luo
 * @create 2020/5/29 11:40
 * Description
 */
@Data
@Builder
public class ConfigValuesBean {
    private String googleClientId;
    private String facebookClientId;
    private String facebookClientSecret;
}
