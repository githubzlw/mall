package com.macro.mall.mapper;

import com.macro.mall.model.TrafficFreightFba;
import com.macro.mall.model.TrafficFreightFbaExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface TrafficFreightFbaMapper {
    long countByExample(TrafficFreightFbaExample example);

    int deleteByExample(TrafficFreightFbaExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(TrafficFreightFba record);

    int insertSelective(TrafficFreightFba record);

    List<TrafficFreightFba> selectByExample(TrafficFreightFbaExample example);

    TrafficFreightFba selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") TrafficFreightFba record, @Param("example") TrafficFreightFbaExample example);

    int updateByExample(@Param("record") TrafficFreightFba record, @Param("example") TrafficFreightFbaExample example);

    int updateByPrimaryKeySelective(TrafficFreightFba record);

    int updateByPrimaryKey(TrafficFreightFba record);
}