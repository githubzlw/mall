package com.macro.mall.onebound.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.importexpress.ali1688.model
 * @date:2020/3/16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemResultPage {


    private List<AliExpressItem> itemList;

    private int currPage;
    private int pageSize;
    private int totalPage;
    private int totalNum;


}
