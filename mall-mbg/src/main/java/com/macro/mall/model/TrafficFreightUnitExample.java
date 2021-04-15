package com.macro.mall.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TrafficFreightUnitExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public TrafficFreightUnitExample() {
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

        public Criteria andModeOfTransportIsNull() {
            addCriterion("mode_of_transport is null");
            return (Criteria) this;
        }

        public Criteria andModeOfTransportIsNotNull() {
            addCriterion("mode_of_transport is not null");
            return (Criteria) this;
        }

        public Criteria andModeOfTransportEqualTo(String value) {
            addCriterion("mode_of_transport =", value, "modeOfTransport");
            return (Criteria) this;
        }

        public Criteria andModeOfTransportNotEqualTo(String value) {
            addCriterion("mode_of_transport <>", value, "modeOfTransport");
            return (Criteria) this;
        }

        public Criteria andModeOfTransportGreaterThan(String value) {
            addCriterion("mode_of_transport >", value, "modeOfTransport");
            return (Criteria) this;
        }

        public Criteria andModeOfTransportGreaterThanOrEqualTo(String value) {
            addCriterion("mode_of_transport >=", value, "modeOfTransport");
            return (Criteria) this;
        }

        public Criteria andModeOfTransportLessThan(String value) {
            addCriterion("mode_of_transport <", value, "modeOfTransport");
            return (Criteria) this;
        }

        public Criteria andModeOfTransportLessThanOrEqualTo(String value) {
            addCriterion("mode_of_transport <=", value, "modeOfTransport");
            return (Criteria) this;
        }

        public Criteria andModeOfTransportLike(String value) {
            addCriterion("mode_of_transport like", value, "modeOfTransport");
            return (Criteria) this;
        }

        public Criteria andModeOfTransportNotLike(String value) {
            addCriterion("mode_of_transport not like", value, "modeOfTransport");
            return (Criteria) this;
        }

        public Criteria andModeOfTransportIn(List<String> values) {
            addCriterion("mode_of_transport in", values, "modeOfTransport");
            return (Criteria) this;
        }

        public Criteria andModeOfTransportNotIn(List<String> values) {
            addCriterion("mode_of_transport not in", values, "modeOfTransport");
            return (Criteria) this;
        }

        public Criteria andModeOfTransportBetween(String value1, String value2) {
            addCriterion("mode_of_transport between", value1, value2, "modeOfTransport");
            return (Criteria) this;
        }

        public Criteria andModeOfTransportNotBetween(String value1, String value2) {
            addCriterion("mode_of_transport not between", value1, value2, "modeOfTransport");
            return (Criteria) this;
        }

        public Criteria andDeliveryTimeIsNull() {
            addCriterion("delivery_time is null");
            return (Criteria) this;
        }

        public Criteria andDeliveryTimeIsNotNull() {
            addCriterion("delivery_time is not null");
            return (Criteria) this;
        }

        public Criteria andDeliveryTimeEqualTo(String value) {
            addCriterion("delivery_time =", value, "deliveryTime");
            return (Criteria) this;
        }

        public Criteria andDeliveryTimeNotEqualTo(String value) {
            addCriterion("delivery_time <>", value, "deliveryTime");
            return (Criteria) this;
        }

        public Criteria andDeliveryTimeGreaterThan(String value) {
            addCriterion("delivery_time >", value, "deliveryTime");
            return (Criteria) this;
        }

        public Criteria andDeliveryTimeGreaterThanOrEqualTo(String value) {
            addCriterion("delivery_time >=", value, "deliveryTime");
            return (Criteria) this;
        }

        public Criteria andDeliveryTimeLessThan(String value) {
            addCriterion("delivery_time <", value, "deliveryTime");
            return (Criteria) this;
        }

        public Criteria andDeliveryTimeLessThanOrEqualTo(String value) {
            addCriterion("delivery_time <=", value, "deliveryTime");
            return (Criteria) this;
        }

        public Criteria andDeliveryTimeLike(String value) {
            addCriterion("delivery_time like", value, "deliveryTime");
            return (Criteria) this;
        }

        public Criteria andDeliveryTimeNotLike(String value) {
            addCriterion("delivery_time not like", value, "deliveryTime");
            return (Criteria) this;
        }

        public Criteria andDeliveryTimeIn(List<String> values) {
            addCriterion("delivery_time in", values, "deliveryTime");
            return (Criteria) this;
        }

        public Criteria andDeliveryTimeNotIn(List<String> values) {
            addCriterion("delivery_time not in", values, "deliveryTime");
            return (Criteria) this;
        }

        public Criteria andDeliveryTimeBetween(String value1, String value2) {
            addCriterion("delivery_time between", value1, value2, "deliveryTime");
            return (Criteria) this;
        }

        public Criteria andDeliveryTimeNotBetween(String value1, String value2) {
            addCriterion("delivery_time not between", value1, value2, "deliveryTime");
            return (Criteria) this;
        }

        public Criteria andCountryIdIsNull() {
            addCriterion("country_id is null");
            return (Criteria) this;
        }

        public Criteria andCountryIdIsNotNull() {
            addCriterion("country_id is not null");
            return (Criteria) this;
        }

        public Criteria andCountryIdEqualTo(Integer value) {
            addCriterion("country_id =", value, "countryId");
            return (Criteria) this;
        }

        public Criteria andCountryIdNotEqualTo(Integer value) {
            addCriterion("country_id <>", value, "countryId");
            return (Criteria) this;
        }

        public Criteria andCountryIdGreaterThan(Integer value) {
            addCriterion("country_id >", value, "countryId");
            return (Criteria) this;
        }

        public Criteria andCountryIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("country_id >=", value, "countryId");
            return (Criteria) this;
        }

        public Criteria andCountryIdLessThan(Integer value) {
            addCriterion("country_id <", value, "countryId");
            return (Criteria) this;
        }

        public Criteria andCountryIdLessThanOrEqualTo(Integer value) {
            addCriterion("country_id <=", value, "countryId");
            return (Criteria) this;
        }

        public Criteria andCountryIdIn(List<Integer> values) {
            addCriterion("country_id in", values, "countryId");
            return (Criteria) this;
        }

        public Criteria andCountryIdNotIn(List<Integer> values) {
            addCriterion("country_id not in", values, "countryId");
            return (Criteria) this;
        }

        public Criteria andCountryIdBetween(Integer value1, Integer value2) {
            addCriterion("country_id between", value1, value2, "countryId");
            return (Criteria) this;
        }

        public Criteria andCountryIdNotBetween(Integer value1, Integer value2) {
            addCriterion("country_id not between", value1, value2, "countryId");
            return (Criteria) this;
        }

        public Criteria andFirstHeavyIsNull() {
            addCriterion("first_heavy is null");
            return (Criteria) this;
        }

        public Criteria andFirstHeavyIsNotNull() {
            addCriterion("first_heavy is not null");
            return (Criteria) this;
        }

        public Criteria andFirstHeavyEqualTo(Double value) {
            addCriterion("first_heavy =", value, "firstHeavy");
            return (Criteria) this;
        }

        public Criteria andFirstHeavyNotEqualTo(Double value) {
            addCriterion("first_heavy <>", value, "firstHeavy");
            return (Criteria) this;
        }

        public Criteria andFirstHeavyGreaterThan(Double value) {
            addCriterion("first_heavy >", value, "firstHeavy");
            return (Criteria) this;
        }

        public Criteria andFirstHeavyGreaterThanOrEqualTo(Double value) {
            addCriterion("first_heavy >=", value, "firstHeavy");
            return (Criteria) this;
        }

        public Criteria andFirstHeavyLessThan(Double value) {
            addCriterion("first_heavy <", value, "firstHeavy");
            return (Criteria) this;
        }

        public Criteria andFirstHeavyLessThanOrEqualTo(Double value) {
            addCriterion("first_heavy <=", value, "firstHeavy");
            return (Criteria) this;
        }

        public Criteria andFirstHeavyIn(List<Double> values) {
            addCriterion("first_heavy in", values, "firstHeavy");
            return (Criteria) this;
        }

        public Criteria andFirstHeavyNotIn(List<Double> values) {
            addCriterion("first_heavy not in", values, "firstHeavy");
            return (Criteria) this;
        }

        public Criteria andFirstHeavyBetween(Double value1, Double value2) {
            addCriterion("first_heavy between", value1, value2, "firstHeavy");
            return (Criteria) this;
        }

        public Criteria andFirstHeavyNotBetween(Double value1, Double value2) {
            addCriterion("first_heavy not between", value1, value2, "firstHeavy");
            return (Criteria) this;
        }

        public Criteria andFirstHeavyPriceIsNull() {
            addCriterion("first_heavy_price is null");
            return (Criteria) this;
        }

        public Criteria andFirstHeavyPriceIsNotNull() {
            addCriterion("first_heavy_price is not null");
            return (Criteria) this;
        }

        public Criteria andFirstHeavyPriceEqualTo(BigDecimal value) {
            addCriterion("first_heavy_price =", value, "firstHeavyPrice");
            return (Criteria) this;
        }

        public Criteria andFirstHeavyPriceNotEqualTo(BigDecimal value) {
            addCriterion("first_heavy_price <>", value, "firstHeavyPrice");
            return (Criteria) this;
        }

        public Criteria andFirstHeavyPriceGreaterThan(BigDecimal value) {
            addCriterion("first_heavy_price >", value, "firstHeavyPrice");
            return (Criteria) this;
        }

        public Criteria andFirstHeavyPriceGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("first_heavy_price >=", value, "firstHeavyPrice");
            return (Criteria) this;
        }

        public Criteria andFirstHeavyPriceLessThan(BigDecimal value) {
            addCriterion("first_heavy_price <", value, "firstHeavyPrice");
            return (Criteria) this;
        }

        public Criteria andFirstHeavyPriceLessThanOrEqualTo(BigDecimal value) {
            addCriterion("first_heavy_price <=", value, "firstHeavyPrice");
            return (Criteria) this;
        }

        public Criteria andFirstHeavyPriceIn(List<BigDecimal> values) {
            addCriterion("first_heavy_price in", values, "firstHeavyPrice");
            return (Criteria) this;
        }

        public Criteria andFirstHeavyPriceNotIn(List<BigDecimal> values) {
            addCriterion("first_heavy_price not in", values, "firstHeavyPrice");
            return (Criteria) this;
        }

        public Criteria andFirstHeavyPriceBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("first_heavy_price between", value1, value2, "firstHeavyPrice");
            return (Criteria) this;
        }

        public Criteria andFirstHeavyPriceNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("first_heavy_price not between", value1, value2, "firstHeavyPrice");
            return (Criteria) this;
        }

        public Criteria andContinuedHeavyPriceIsNull() {
            addCriterion("continued_heavy_price is null");
            return (Criteria) this;
        }

        public Criteria andContinuedHeavyPriceIsNotNull() {
            addCriterion("continued_heavy_price is not null");
            return (Criteria) this;
        }

        public Criteria andContinuedHeavyPriceEqualTo(BigDecimal value) {
            addCriterion("continued_heavy_price =", value, "continuedHeavyPrice");
            return (Criteria) this;
        }

        public Criteria andContinuedHeavyPriceNotEqualTo(BigDecimal value) {
            addCriterion("continued_heavy_price <>", value, "continuedHeavyPrice");
            return (Criteria) this;
        }

        public Criteria andContinuedHeavyPriceGreaterThan(BigDecimal value) {
            addCriterion("continued_heavy_price >", value, "continuedHeavyPrice");
            return (Criteria) this;
        }

        public Criteria andContinuedHeavyPriceGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("continued_heavy_price >=", value, "continuedHeavyPrice");
            return (Criteria) this;
        }

        public Criteria andContinuedHeavyPriceLessThan(BigDecimal value) {
            addCriterion("continued_heavy_price <", value, "continuedHeavyPrice");
            return (Criteria) this;
        }

        public Criteria andContinuedHeavyPriceLessThanOrEqualTo(BigDecimal value) {
            addCriterion("continued_heavy_price <=", value, "continuedHeavyPrice");
            return (Criteria) this;
        }

        public Criteria andContinuedHeavyPriceIn(List<BigDecimal> values) {
            addCriterion("continued_heavy_price in", values, "continuedHeavyPrice");
            return (Criteria) this;
        }

        public Criteria andContinuedHeavyPriceNotIn(List<BigDecimal> values) {
            addCriterion("continued_heavy_price not in", values, "continuedHeavyPrice");
            return (Criteria) this;
        }

        public Criteria andContinuedHeavyPriceBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("continued_heavy_price between", value1, value2, "continuedHeavyPrice");
            return (Criteria) this;
        }

        public Criteria andContinuedHeavyPriceNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("continued_heavy_price not between", value1, value2, "continuedHeavyPrice");
            return (Criteria) this;
        }

        public Criteria andBigHeavyPriceIsNull() {
            addCriterion("big_heavy_price is null");
            return (Criteria) this;
        }

        public Criteria andBigHeavyPriceIsNotNull() {
            addCriterion("big_heavy_price is not null");
            return (Criteria) this;
        }

        public Criteria andBigHeavyPriceEqualTo(BigDecimal value) {
            addCriterion("big_heavy_price =", value, "bigHeavyPrice");
            return (Criteria) this;
        }

        public Criteria andBigHeavyPriceNotEqualTo(BigDecimal value) {
            addCriterion("big_heavy_price <>", value, "bigHeavyPrice");
            return (Criteria) this;
        }

        public Criteria andBigHeavyPriceGreaterThan(BigDecimal value) {
            addCriterion("big_heavy_price >", value, "bigHeavyPrice");
            return (Criteria) this;
        }

        public Criteria andBigHeavyPriceGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("big_heavy_price >=", value, "bigHeavyPrice");
            return (Criteria) this;
        }

        public Criteria andBigHeavyPriceLessThan(BigDecimal value) {
            addCriterion("big_heavy_price <", value, "bigHeavyPrice");
            return (Criteria) this;
        }

        public Criteria andBigHeavyPriceLessThanOrEqualTo(BigDecimal value) {
            addCriterion("big_heavy_price <=", value, "bigHeavyPrice");
            return (Criteria) this;
        }

        public Criteria andBigHeavyPriceIn(List<BigDecimal> values) {
            addCriterion("big_heavy_price in", values, "bigHeavyPrice");
            return (Criteria) this;
        }

        public Criteria andBigHeavyPriceNotIn(List<BigDecimal> values) {
            addCriterion("big_heavy_price not in", values, "bigHeavyPrice");
            return (Criteria) this;
        }

        public Criteria andBigHeavyPriceBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("big_heavy_price between", value1, value2, "bigHeavyPrice");
            return (Criteria) this;
        }

        public Criteria andBigHeavyPriceNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("big_heavy_price not between", value1, value2, "bigHeavyPrice");
            return (Criteria) this;
        }

        public Criteria andDefaultWeightOfSpecialIsNull() {
            addCriterion("default_weight_of_special is null");
            return (Criteria) this;
        }

        public Criteria andDefaultWeightOfSpecialIsNotNull() {
            addCriterion("default_weight_of_special is not null");
            return (Criteria) this;
        }

        public Criteria andDefaultWeightOfSpecialEqualTo(Double value) {
            addCriterion("default_weight_of_special =", value, "defaultWeightOfSpecial");
            return (Criteria) this;
        }

        public Criteria andDefaultWeightOfSpecialNotEqualTo(Double value) {
            addCriterion("default_weight_of_special <>", value, "defaultWeightOfSpecial");
            return (Criteria) this;
        }

        public Criteria andDefaultWeightOfSpecialGreaterThan(Double value) {
            addCriterion("default_weight_of_special >", value, "defaultWeightOfSpecial");
            return (Criteria) this;
        }

        public Criteria andDefaultWeightOfSpecialGreaterThanOrEqualTo(Double value) {
            addCriterion("default_weight_of_special >=", value, "defaultWeightOfSpecial");
            return (Criteria) this;
        }

        public Criteria andDefaultWeightOfSpecialLessThan(Double value) {
            addCriterion("default_weight_of_special <", value, "defaultWeightOfSpecial");
            return (Criteria) this;
        }

        public Criteria andDefaultWeightOfSpecialLessThanOrEqualTo(Double value) {
            addCriterion("default_weight_of_special <=", value, "defaultWeightOfSpecial");
            return (Criteria) this;
        }

        public Criteria andDefaultWeightOfSpecialIn(List<Double> values) {
            addCriterion("default_weight_of_special in", values, "defaultWeightOfSpecial");
            return (Criteria) this;
        }

        public Criteria andDefaultWeightOfSpecialNotIn(List<Double> values) {
            addCriterion("default_weight_of_special not in", values, "defaultWeightOfSpecial");
            return (Criteria) this;
        }

        public Criteria andDefaultWeightOfSpecialBetween(Double value1, Double value2) {
            addCriterion("default_weight_of_special between", value1, value2, "defaultWeightOfSpecial");
            return (Criteria) this;
        }

        public Criteria andDefaultWeightOfSpecialNotBetween(Double value1, Double value2) {
            addCriterion("default_weight_of_special not between", value1, value2, "defaultWeightOfSpecial");
            return (Criteria) this;
        }

        public Criteria andFirstHeavyPriceOfSpecialIsNull() {
            addCriterion("first_heavy_price_of_special is null");
            return (Criteria) this;
        }

        public Criteria andFirstHeavyPriceOfSpecialIsNotNull() {
            addCriterion("first_heavy_price_of_special is not null");
            return (Criteria) this;
        }

        public Criteria andFirstHeavyPriceOfSpecialEqualTo(BigDecimal value) {
            addCriterion("first_heavy_price_of_special =", value, "firstHeavyPriceOfSpecial");
            return (Criteria) this;
        }

        public Criteria andFirstHeavyPriceOfSpecialNotEqualTo(BigDecimal value) {
            addCriterion("first_heavy_price_of_special <>", value, "firstHeavyPriceOfSpecial");
            return (Criteria) this;
        }

        public Criteria andFirstHeavyPriceOfSpecialGreaterThan(BigDecimal value) {
            addCriterion("first_heavy_price_of_special >", value, "firstHeavyPriceOfSpecial");
            return (Criteria) this;
        }

        public Criteria andFirstHeavyPriceOfSpecialGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("first_heavy_price_of_special >=", value, "firstHeavyPriceOfSpecial");
            return (Criteria) this;
        }

        public Criteria andFirstHeavyPriceOfSpecialLessThan(BigDecimal value) {
            addCriterion("first_heavy_price_of_special <", value, "firstHeavyPriceOfSpecial");
            return (Criteria) this;
        }

        public Criteria andFirstHeavyPriceOfSpecialLessThanOrEqualTo(BigDecimal value) {
            addCriterion("first_heavy_price_of_special <=", value, "firstHeavyPriceOfSpecial");
            return (Criteria) this;
        }

        public Criteria andFirstHeavyPriceOfSpecialIn(List<BigDecimal> values) {
            addCriterion("first_heavy_price_of_special in", values, "firstHeavyPriceOfSpecial");
            return (Criteria) this;
        }

        public Criteria andFirstHeavyPriceOfSpecialNotIn(List<BigDecimal> values) {
            addCriterion("first_heavy_price_of_special not in", values, "firstHeavyPriceOfSpecial");
            return (Criteria) this;
        }

        public Criteria andFirstHeavyPriceOfSpecialBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("first_heavy_price_of_special between", value1, value2, "firstHeavyPriceOfSpecial");
            return (Criteria) this;
        }

        public Criteria andFirstHeavyPriceOfSpecialNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("first_heavy_price_of_special not between", value1, value2, "firstHeavyPriceOfSpecial");
            return (Criteria) this;
        }

        public Criteria andContinuedHeavyPriceOfSpecialIsNull() {
            addCriterion("continued_heavy_price_of_special is null");
            return (Criteria) this;
        }

        public Criteria andContinuedHeavyPriceOfSpecialIsNotNull() {
            addCriterion("continued_heavy_price_of_special is not null");
            return (Criteria) this;
        }

        public Criteria andContinuedHeavyPriceOfSpecialEqualTo(BigDecimal value) {
            addCriterion("continued_heavy_price_of_special =", value, "continuedHeavyPriceOfSpecial");
            return (Criteria) this;
        }

        public Criteria andContinuedHeavyPriceOfSpecialNotEqualTo(BigDecimal value) {
            addCriterion("continued_heavy_price_of_special <>", value, "continuedHeavyPriceOfSpecial");
            return (Criteria) this;
        }

        public Criteria andContinuedHeavyPriceOfSpecialGreaterThan(BigDecimal value) {
            addCriterion("continued_heavy_price_of_special >", value, "continuedHeavyPriceOfSpecial");
            return (Criteria) this;
        }

        public Criteria andContinuedHeavyPriceOfSpecialGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("continued_heavy_price_of_special >=", value, "continuedHeavyPriceOfSpecial");
            return (Criteria) this;
        }

        public Criteria andContinuedHeavyPriceOfSpecialLessThan(BigDecimal value) {
            addCriterion("continued_heavy_price_of_special <", value, "continuedHeavyPriceOfSpecial");
            return (Criteria) this;
        }

        public Criteria andContinuedHeavyPriceOfSpecialLessThanOrEqualTo(BigDecimal value) {
            addCriterion("continued_heavy_price_of_special <=", value, "continuedHeavyPriceOfSpecial");
            return (Criteria) this;
        }

        public Criteria andContinuedHeavyPriceOfSpecialIn(List<BigDecimal> values) {
            addCriterion("continued_heavy_price_of_special in", values, "continuedHeavyPriceOfSpecial");
            return (Criteria) this;
        }

        public Criteria andContinuedHeavyPriceOfSpecialNotIn(List<BigDecimal> values) {
            addCriterion("continued_heavy_price_of_special not in", values, "continuedHeavyPriceOfSpecial");
            return (Criteria) this;
        }

        public Criteria andContinuedHeavyPriceOfSpecialBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("continued_heavy_price_of_special between", value1, value2, "continuedHeavyPriceOfSpecial");
            return (Criteria) this;
        }

        public Criteria andContinuedHeavyPriceOfSpecialNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("continued_heavy_price_of_special not between", value1, value2, "continuedHeavyPriceOfSpecial");
            return (Criteria) this;
        }

        public Criteria andBigHeavyPriceOfSpecialIsNull() {
            addCriterion("big_heavy_price_of_special is null");
            return (Criteria) this;
        }

        public Criteria andBigHeavyPriceOfSpecialIsNotNull() {
            addCriterion("big_heavy_price_of_special is not null");
            return (Criteria) this;
        }

        public Criteria andBigHeavyPriceOfSpecialEqualTo(BigDecimal value) {
            addCriterion("big_heavy_price_of_special =", value, "bigHeavyPriceOfSpecial");
            return (Criteria) this;
        }

        public Criteria andBigHeavyPriceOfSpecialNotEqualTo(BigDecimal value) {
            addCriterion("big_heavy_price_of_special <>", value, "bigHeavyPriceOfSpecial");
            return (Criteria) this;
        }

        public Criteria andBigHeavyPriceOfSpecialGreaterThan(BigDecimal value) {
            addCriterion("big_heavy_price_of_special >", value, "bigHeavyPriceOfSpecial");
            return (Criteria) this;
        }

        public Criteria andBigHeavyPriceOfSpecialGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("big_heavy_price_of_special >=", value, "bigHeavyPriceOfSpecial");
            return (Criteria) this;
        }

        public Criteria andBigHeavyPriceOfSpecialLessThan(BigDecimal value) {
            addCriterion("big_heavy_price_of_special <", value, "bigHeavyPriceOfSpecial");
            return (Criteria) this;
        }

        public Criteria andBigHeavyPriceOfSpecialLessThanOrEqualTo(BigDecimal value) {
            addCriterion("big_heavy_price_of_special <=", value, "bigHeavyPriceOfSpecial");
            return (Criteria) this;
        }

        public Criteria andBigHeavyPriceOfSpecialIn(List<BigDecimal> values) {
            addCriterion("big_heavy_price_of_special in", values, "bigHeavyPriceOfSpecial");
            return (Criteria) this;
        }

        public Criteria andBigHeavyPriceOfSpecialNotIn(List<BigDecimal> values) {
            addCriterion("big_heavy_price_of_special not in", values, "bigHeavyPriceOfSpecial");
            return (Criteria) this;
        }

        public Criteria andBigHeavyPriceOfSpecialBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("big_heavy_price_of_special between", value1, value2, "bigHeavyPriceOfSpecial");
            return (Criteria) this;
        }

        public Criteria andBigHeavyPriceOfSpecialNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("big_heavy_price_of_special not between", value1, value2, "bigHeavyPriceOfSpecial");
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

        public Criteria andSplitIsNull() {
            addCriterion("split is null");
            return (Criteria) this;
        }

        public Criteria andSplitIsNotNull() {
            addCriterion("split is not null");
            return (Criteria) this;
        }

        public Criteria andSplitEqualTo(Integer value) {
            addCriterion("split =", value, "split");
            return (Criteria) this;
        }

        public Criteria andSplitNotEqualTo(Integer value) {
            addCriterion("split <>", value, "split");
            return (Criteria) this;
        }

        public Criteria andSplitGreaterThan(Integer value) {
            addCriterion("split >", value, "split");
            return (Criteria) this;
        }

        public Criteria andSplitGreaterThanOrEqualTo(Integer value) {
            addCriterion("split >=", value, "split");
            return (Criteria) this;
        }

        public Criteria andSplitLessThan(Integer value) {
            addCriterion("split <", value, "split");
            return (Criteria) this;
        }

        public Criteria andSplitLessThanOrEqualTo(Integer value) {
            addCriterion("split <=", value, "split");
            return (Criteria) this;
        }

        public Criteria andSplitIn(List<Integer> values) {
            addCriterion("split in", values, "split");
            return (Criteria) this;
        }

        public Criteria andSplitNotIn(List<Integer> values) {
            addCriterion("split not in", values, "split");
            return (Criteria) this;
        }

        public Criteria andSplitBetween(Integer value1, Integer value2) {
            addCriterion("split between", value1, value2, "split");
            return (Criteria) this;
        }

        public Criteria andSplitNotBetween(Integer value1, Integer value2) {
            addCriterion("split not between", value1, value2, "split");
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