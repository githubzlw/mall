package com.macro.mall.mapper;

import com.macro.mall.model.TrafficFreightUnit;
import com.macro.mall.model.TrafficFreightUnitExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface TrafficFreightUnitMapper {
    long countByExample(TrafficFreightUnitExample example);

    int deleteByExample(TrafficFreightUnitExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(TrafficFreightUnit record);

    int insertSelective(TrafficFreightUnit record);

    List<TrafficFreightUnit> selectByExample(TrafficFreightUnitExample example);

    TrafficFreightUnit selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") TrafficFreightUnit record, @Param("example") TrafficFreightUnitExample example);

    int updateByExample(@Param("record") TrafficFreightUnit record, @Param("example") TrafficFreightUnitExample example);

    int updateByPrimaryKeySelective(TrafficFreightUnit record);

    int updateByPrimaryKey(TrafficFreightUnit record);
}