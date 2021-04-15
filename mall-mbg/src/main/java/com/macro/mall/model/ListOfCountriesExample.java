package com.macro.mall.model;

import java.util.ArrayList;
import java.util.List;

public class ListOfCountriesExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public ListOfCountriesExample() {
        oredCriteria = new ArrayList<>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andIdIsNull() {
            addCriterion("id is null");
            return (Criteria) this;
        }

        public Criteria andIdIsNotNull() {
            addCriterion("id is not null");
            return (Criteria) this;
        }

        public Criteria andIdEqualTo(Integer value) {
            addCriterion("id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(Integer value) {
            addCriterion("id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(Integer value) {
            addCriterion("id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(Integer value) {
            addCriterion("id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(Integer value) {
            addCriterion("id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<Integer> values) {
            addCriterion("id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<Integer> values) {
            addCriterion("id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(Integer value1, Integer value2) {
            addCriterion("id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(Integer value1, Integer value2) {
            addCriterion("id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andEnglishNameOfCountryIsNull() {
            addCriterion("english_name_of_country is null");
            return (Criteria) this;
        }

        public Criteria andEnglishNameOfCountryIsNotNull() {
            addCriterion("english_name_of_country is not null");
            return (Criteria) this;
        }

        public Criteria andEnglishNameOfCountryEqualTo(String value) {
            addCriterion("english_name_of_country =", value, "englishNameOfCountry");
            return (Criteria) this;
        }

        public Criteria andEnglishNameOfCountryNotEqualTo(String value) {
            addCriterion("english_name_of_country <>", value, "englishNameOfCountry");
            return (Criteria) this;
        }

        public Criteria andEnglishNameOfCountryGreaterThan(String value) {
            addCriterion("english_name_of_country >", value, "englishNameOfCountry");
            return (Criteria) this;
        }

        public Criteria andEnglishNameOfCountryGreaterThanOrEqualTo(String value) {
            addCriterion("english_name_of_country >=", value, "englishNameOfCountry");
            return (Criteria) this;
        }

        public Criteria andEnglishNameOfCountryLessThan(String value) {
            addCriterion("english_name_of_country <", value, "englishNameOfCountry");
            return (Criteria) this;
        }

        public Criteria andEnglishNameOfCountryLessThanOrEqualTo(String value) {
            addCriterion("english_name_of_country <=", value, "englishNameOfCountry");
            return (Criteria) this;
        }

        public Criteria andEnglishNameOfCountryLike(String value) {
            addCriterion("english_name_of_country like", value, "englishNameOfCountry");
            return (Criteria) this;
        }

        public Criteria andEnglishNameOfCountryNotLike(String value) {
            addCriterion("english_name_of_country not like", value, "englishNameOfCountry");
            return (Criteria) this;
        }

        public Criteria andEnglishNameOfCountryIn(List<String> values) {
            addCriterion("english_name_of_country in", values, "englishNameOfCountry");
            return (Criteria) this;
        }

        public Criteria andEnglishNameOfCountryNotIn(List<String> values) {
            addCriterion("english_name_of_country not in", values, "englishNameOfCountry");
            return (Criteria) this;
        }

        public Criteria andEnglishNameOfCountryBetween(String value1, String value2) {
            addCriterion("english_name_of_country between", value1, value2, "englishNameOfCountry");
            return (Criteria) this;
        }

        public Criteria andEnglishNameOfCountryNotBetween(String value1, String value2) {
            addCriterion("english_name_of_country not between", value1, value2, "englishNameOfCountry");
            return (Criteria) this;
        }

        public Criteria andChineseNameOfCountryIsNull() {
            addCriterion("chinese_name_of_country is null");
            return (Criteria) this;
        }

        public Criteria andChineseNameOfCountryIsNotNull() {
            addCriterion("chinese_name_of_country is not null");
            return (Criteria) this;
        }

        public Criteria andChineseNameOfCountryEqualTo(String value) {
            addCriterion("chinese_name_of_country =", value, "chineseNameOfCountry");
            return (Criteria) this;
        }

        public Criteria andChineseNameOfCountryNotEqualTo(String value) {
            addCriterion("chinese_name_of_country <>", value, "chineseNameOfCountry");
            return (Criteria) this;
        }

        public Criteria andChineseNameOfCountryGreaterThan(String value) {
            addCriterion("chinese_name_of_country >", value, "chineseNameOfCountry");
            return (Criteria) this;
        }

        public Criteria andChineseNameOfCountryGreaterThanOrEqualTo(String value) {
            addCriterion("chinese_name_of_country >=", value, "chineseNameOfCountry");
            return (Criteria) this;
        }

        public Criteria andChineseNameOfCountryLessThan(String value) {
            addCriterion("chinese_name_of_country <", value, "chineseNameOfCountry");
            return (Criteria) this;
        }

        public Criteria andChineseNameOfCountryLessThanOrEqualTo(String value) {
            addCriterion("chinese_name_of_country <=", value, "chineseNameOfCountry");
            return (Criteria) this;
        }

        public Criteria andChineseNameOfCountryLike(String value) {
            addCriterion("chinese_name_of_country like", value, "chineseNameOfCountry");
            return (Criteria) this;
        }

        public Criteria andChineseNameOfCountryNotLike(String value) {
            addCriterion("chinese_name_of_country not like", value, "chineseNameOfCountry");
            return (Criteria) this;
        }

        public Criteria andChineseNameOfCountryIn(List<String> values) {
            addCriterion("chinese_name_of_country in", values, "chineseNameOfCountry");
            return (Criteria) this;
        }

        public Criteria andChineseNameOfCountryNotIn(List<String> values) {
            addCriterion("chinese_name_of_country not in", values, "chineseNameOfCountry");
            return (Criteria) this;
        }

        public Criteria andChineseNameOfCountryBetween(String value1, String value2) {
            addCriterion("chinese_name_of_country between", value1, value2, "chineseNameOfCountry");
            return (Criteria) this;
        }

        public Criteria andChineseNameOfCountryNotBetween(String value1, String value2) {
            addCriterion("chinese_name_of_country not between", value1, value2, "chineseNameOfCountry");
            return (Criteria) this;
        }

        public Criteria andCountriesInCodeIsNull() {
            addCriterion("countries_in_code is null");
            return (Criteria) this;
        }

        public Criteria andCountriesInCodeIsNotNull() {
            addCriterion("countries_in_code is not null");
            return (Criteria) this;
        }

        public Criteria andCountriesInCodeEqualTo(String value) {
            addCriterion("countries_in_code =", value, "countriesInCode");
            return (Criteria) this;
        }

        public Criteria andCountriesInCodeNotEqualTo(String value) {
            addCriterion("countries_in_code <>", value, "countriesInCode");
            return (Criteria) this;
        }

        public Criteria andCountriesInCodeGreaterThan(String value) {
            addCriterion("countries_in_code >", value, "countriesInCode");
            return (Criteria) this;
        }

        public Criteria andCountriesInCodeGreaterThanOrEqualTo(String value) {
            addCriterion("countries_in_code >=", value, "countriesInCode");
            return (Criteria) this;
        }

        public Criteria andCountriesInCodeLessThan(String value) {
            addCriterion("countries_in_code <", value, "countriesInCode");
            return (Criteria) this;
        }

        public Criteria andCountriesInCodeLessThanOrEqualTo(String value) {
            addCriterion("countries_in_code <=", value, "countriesInCode");
            return (Criteria) this;
        }

        public Criteria andCountriesInCodeLike(String value) {
            addCriterion("countries_in_code like", value, "countriesInCode");
            return (Criteria) this;
        }

        public Criteria andCountriesInCodeNotLike(String value) {
            addCriterion("countries_in_code not like", value, "countriesInCode");
            return (Criteria) this;
        }

        public Criteria andCountriesInCodeIn(List<String> values) {
            addCriterion("countries_in_code in", values, "countriesInCode");
            return (Criteria) this;
        }

        public Criteria andCountriesInCodeNotIn(List<String> values) {
            addCriterion("countries_in_code not in", values, "countriesInCode");
            return (Criteria) this;
        }

        public Criteria andCountriesInCodeBetween(String value1, String value2) {
            addCriterion("countries_in_code between", value1, value2, "countriesInCode");
            return (Criteria) this;
        }

        public Criteria andCountriesInCodeNotBetween(String value1, String value2) {
            addCriterion("countries_in_code not between", value1, value2, "countriesInCode");
            return (Criteria) this;
        }

        public Criteria andAreaNumIsNull() {
            addCriterion("area_num is null");
            return (Criteria) this;
        }

        public Criteria andAreaNumIsNotNull() {
            addCriterion("area_num is not null");
            return (Criteria) this;
        }

        public Criteria andAreaNumEqualTo(Integer value) {
            addCriterion("area_num =", value, "areaNum");
            return (Criteria) this;
        }

        public Criteria andAreaNumNotEqualTo(Integer value) {
            addCriterion("area_num <>", value, "areaNum");
            return (Criteria) this;
        }

        public Criteria andAreaNumGreaterThan(Integer value) {
            addCriterion("area_num >", value, "areaNum");
            return (Criteria) this;
        }

        public Criteria andAreaNumGreaterThanOrEqualTo(Integer value) {
            addCriterion("area_num >=", value, "areaNum");
            return (Criteria) this;
        }

        public Criteria andAreaNumLessThan(Integer value) {
            addCriterion("area_num <", value, "areaNum");
            return (Criteria) this;
        }

        public Criteria andAreaNumLessThanOrEqualTo(Integer value) {
            addCriterion("area_num <=", value, "areaNum");
            return (Criteria) this;
        }

        public Criteria andAreaNumIn(List<Integer> values) {
            addCriterion("area_num in", values, "areaNum");
            return (Criteria) this;
        }

        public Criteria andAreaNumNotIn(List<Integer> values) {
            addCriterion("area_num not in", values, "areaNum");
            return (Criteria) this;
        }

        public Criteria andAreaNumBetween(Integer value1, Integer value2) {
            addCriterion("area_num between", value1, value2, "areaNum");
            return (Criteria) this;
        }

        public Criteria andAreaNumNotBetween(Integer value1, Integer value2) {
            addCriterion("area_num not between", value1, value2, "areaNum");
            return (Criteria) this;
        }

        public Criteria andAreaNameIsNull() {
            addCriterion("area_name is null");
            return (Criteria) this;
        }

        public Criteria andAreaNameIsNotNull() {
            addCriterion("area_name is not null");
            return (Criteria) this;
        }

        public Criteria andAreaNameEqualTo(String value) {
            addCriterion("area_name =", value, "areaName");
            return (Criteria) this;
        }

        public Criteria andAreaNameNotEqualTo(String value) {
            addCriterion("area_name <>", value, "areaName");
            return (Criteria) this;
        }

        public Criteria andAreaNameGreaterThan(String value) {
            addCriterion("area_name >", value, "areaName");
            return (Criteria) this;
        }

        public Criteria andAreaNameGreaterThanOrEqualTo(String value) {
            addCriterion("area_name >=", value, "areaName");
            return (Criteria) this;
        }

        public Criteria andAreaNameLessThan(String value) {
            addCriterion("area_name <", value, "areaName");
            return (Criteria) this;
        }

        public Criteria andAreaNameLessThanOrEqualTo(String value) {
            addCriterion("area_name <=", value, "areaName");
            return (Criteria) this;
        }

        public Criteria andAreaNameLike(String value) {
            addCriterion("area_name like", value, "areaName");
            return (Criteria) this;
        }

        public Criteria andAreaNameNotLike(String value) {
            addCriterion("area_name not like", value, "areaName");
            return (Criteria) this;
        }

        public Criteria andAreaNameIn(List<String> values) {
            addCriterion("area_name in", values, "areaName");
            return (Criteria) this;
        }

        public Criteria andAreaNameNotIn(List<String> values) {
            addCriterion("area_name not in", values, "areaName");
            return (Criteria) this;
        }

        public Criteria andAreaNameBetween(String value1, String value2) {
            addCriterion("area_name between", value1, value2, "areaName");
            return (Criteria) this;
        }

        public Criteria andAreaNameNotBetween(String value1, String value2) {
            addCriterion("area_name not between", value1, value2, "areaName");
            return (Criteria) this;
        }

        public Criteria andAfricaFlagIsNull() {
            addCriterion("africa_flag is null");
            return (Criteria) this;
        }

        public Criteria andAfricaFlagIsNotNull() {
            addCriterion("africa_flag is not null");
            return (Criteria) this;
        }

        public Criteria andAfricaFlagEqualTo(Integer value) {
            addCriterion("africa_flag =", value, "africaFlag");
            return (Criteria) this;
        }

        public Criteria andAfricaFlagNotEqualTo(Integer value) {
            addCriterion("africa_flag <>", value, "africaFlag");
            return (Criteria) this;
        }

        public Criteria andAfricaFlagGreaterThan(Integer value) {
            addCriterion("africa_flag >", value, "africaFlag");
            return (Criteria) this;
        }

        public Criteria andAfricaFlagGreaterThanOrEqualTo(Integer value) {
            addCriterion("africa_flag >=", value, "africaFlag");
            return (Criteria) this;
        }

        public Criteria andAfricaFlagLessThan(Integer value) {
            addCriterion("africa_flag <", value, "africaFlag");
            return (Criteria) this;
        }

        public Criteria andAfricaFlagLessThanOrEqualTo(Integer value) {
            addCriterion("africa_flag <=", value, "africaFlag");
            return (Criteria) this;
        }

        public Criteria andAfricaFlagIn(List<Integer> values) {
            addCriterion("africa_flag in", values, "africaFlag");
            return (Criteria) this;
        }

        public Criteria andAfricaFlagNotIn(List<Integer> values) {
            addCriterion("africa_flag not in", values, "africaFlag");
            return (Criteria) this;
        }

        public Criteria andAfricaFlagBetween(Integer value1, Integer value2) {
            addCriterion("africa_flag between", value1, value2, "africaFlag");
            return (Criteria) this;
        }

        public Criteria andAfricaFlagNotBetween(Integer value1, Integer value2) {
            addCriterion("africa_flag not between", value1, value2, "africaFlag");
            return (Criteria) this;
        }

        public Criteria andCifFlagIsNull() {
            addCriterion("cif_flag is null");
            return (Criteria) this;
        }

        public Criteria andCifFlagIsNotNull() {
            addCriterion("cif_flag is not null");
            return (Criteria) this;
        }

        public Criteria andCifFlagEqualTo(Integer value) {
            addCriterion("cif_flag =", value, "cifFlag");
            return (Criteria) this;
        }

        public Criteria andCifFlagNotEqualTo(Integer value) {
            addCriterion("cif_flag <>", value, "cifFlag");
            return (Criteria) this;
        }

        public Criteria andCifFlagGreaterThan(Integer value) {
            addCriterion("cif_flag >", value, "cifFlag");
            return (Criteria) this;
        }

        public Criteria andCifFlagGreaterThanOrEqualTo(Integer value) {
            addCriterion("cif_flag >=", value, "cifFlag");
            return (Criteria) this;
        }

        public Criteria andCifFlagLessThan(Integer value) {
            addCriterion("cif_flag <", value, "cifFlag");
            return (Criteria) this;
        }

        public Criteria andCifFlagLessThanOrEqualTo(Integer value) {
            addCriterion("cif_flag <=", value, "cifFlag");
            return (Criteria) this;
        }

        public Criteria andCifFlagIn(List<Integer> values) {
            addCriterion("cif_flag in", values, "cifFlag");
            return (Criteria) this;
        }

        public Criteria andCifFlagNotIn(List<Integer> values) {
            addCriterion("cif_flag not in", values, "cifFlag");
            return (Criteria) this;
        }

        public Criteria andCifFlagBetween(Integer value1, Integer value2) {
            addCriterion("cif_flag between", value1, value2, "cifFlag");
            return (Criteria) this;
        }

        public Criteria andCifFlagNotBetween(Integer value1, Integer value2) {
            addCriterion("cif_flag not between", value1, value2, "cifFlag");
            return (Criteria) this;
        }

        public Criteria andDelIsNull() {
            addCriterion("del is null");
            return (Criteria) this;
        }

        public Criteria andDelIsNotNull() {
            addCriterion("del is not null");
            return (Criteria) this;
        }

        public Criteria andDelEqualTo(Integer value) {
            addCriterion("del =", value, "del");
            return (Criteria) this;
        }

        public Criteria andDelNotEqualTo(Integer value) {
            addCriterion("del <>", value, "del");
            return (Criteria) this;
        }

        public Criteria andDelGreaterThan(Integer value) {
            addCriterion("del >", value, "del");
            return (Criteria) this;
        }

        public Criteria andDelGreaterThanOrEqualTo(Integer value) {
            addCriterion("del >=", value, "del");
            return (Criteria) this;
        }

        public Criteria andDelLessThan(Integer value) {
            addCriterion("del <", value, "del");
            return (Criteria) this;
        }

        public Criteria andDelLessThanOrEqualTo(Integer value) {
            addCriterion("del <=", value, "del");
            return (Criteria) this;
        }

        public Criteria andDelIn(List<Integer> values) {
            addCriterion("del in", values, "del");
            return (Criteria) this;
        }

        public Criteria andDelNotIn(List<Integer> values) {
            addCriterion("del not in", values, "del");
            return (Criteria) this;
        }

        public Criteria andDelBetween(Integer value1, Integer value2) {
            addCriterion("del between", value1, value2, "del");
            return (Criteria) this;
        }

        public Criteria andDelNotBetween(Integer value1, Integer value2) {
            addCriterion("del not between", value1, value2, "del");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {
        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}