package com.macro.mall.domain;

import lombok.Data;

import java.util.List;

/**
 * @author luohao
 * @date 2019/2/28
 */
@Data
public class Images {

    private long id;
    private long product_id;
    private int position;
    private String created_at;
    private String updated_at;
    private String alt;
    private int width;
    private int height;
    private String src;
    private List<String> variant_ids;
    private String admin_graphql_api_id;

}
