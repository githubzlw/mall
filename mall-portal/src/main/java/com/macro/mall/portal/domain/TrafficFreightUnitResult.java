package com.macro.mall.portal.domain;

import lombok.Data;

import java.util.List;

@Data
public class TrafficFreightUnitResult {

    /**
     * 普通运输列表
     */
    private List<TrafficFreightUnitShort> unitList;

    /**
     * 海运运输列表
     */
    private List<TrafficFreightUnitShort> seaList;

    /**
     * 附加费
     */
    private double surcharge;

}