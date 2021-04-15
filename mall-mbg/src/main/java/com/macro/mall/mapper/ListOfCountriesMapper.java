package com.macro.mall.mapper;

import com.macro.mall.model.ListOfCountries;
import com.macro.mall.model.ListOfCountriesExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ListOfCountriesMapper {
    long countByExample(ListOfCountriesExample example);

    int deleteByExample(ListOfCountriesExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(ListOfCountries record);

    int insertSelective(ListOfCountries record);

    List<ListOfCountries> selectByExample(ListOfCountriesExample example);

    ListOfCountries selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") ListOfCountries record, @Param("example") ListOfCountriesExample example);

    int updateByExample(@Param("record") ListOfCountries record, @Param("example") ListOfCountriesExample example);

    int updateByPrimaryKeySelective(ListOfCountries record);

    int updateByPrimaryKey(ListOfCountries record);
}