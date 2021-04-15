package com.macro.mall.mapper;

import com.macro.mall.model.TrafficFreightPort;
import com.macro.mall.model.TrafficFreightPortExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface TrafficFreightPortMapper {
    long countByExample(TrafficFreightPortExample example);

    int deleteByExample(TrafficFreightPortExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(TrafficFreightPort record);

    int insertSelective(TrafficFreightPort record);

    List<TrafficFreightPort> selectByExample(TrafficFreightPortExample example);

    TrafficFreightPort selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") TrafficFreightPort record, @Param("example") TrafficFreightPortExample example);

    int updateByExample(@Param("record") TrafficFreightPort record, @Param("example") TrafficFreightPortExample example);

    int updateByPrimaryKeySelective(TrafficFreightPort record);

    int updateByPrimaryKey(TrafficFreightPort record);
}