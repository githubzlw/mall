package com.macro.mall.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PmsSkuStockExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public PmsSkuStockExample() {
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

        public Criteria andIdEqualTo(Long value) {
            addCriterion("id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(Long value) {
            addCriterion("id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(Long value) {
            addCriterion("id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(Long value) {
            addCriterion("id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(Long value) {
            addCriterion("id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(Long value) {
            addCriterion("id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<Long> values) {
            addCriterion("id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<Long> values) {
            addCriterion("id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(Long value1, Long value2) {
            addCriterion("id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(Long value1, Long value2) {
            addCriterion("id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andProductIdIsNull() {
            addCriterion("product_id is null");
            return (Criteria) this;
        }

        public Criteria andProductIdIsNotNull() {
            addCriterion("product_id is not null");
            return (Criteria) this;
        }

        public Criteria andProductIdEqualTo(Long value) {
            addCriterion("product_id =", value, "productId");
            return (Criteria) this;
        }

        public Criteria andProductIdNotEqualTo(Long value) {
            addCriterion("product_id <>", value, "productId");
            return (Criteria) this;
        }

        public Criteria andProductIdGreaterThan(Long value) {
            addCriterion("product_id >", value, "productId");
            return (Criteria) this;
        }

        public Criteria andProductIdGreaterThanOrEqualTo(Long value) {
            addCriterion("product_id >=", value, "productId");
            return (Criteria) this;
        }

        public Criteria andProductIdLessThan(Long value) {
            addCriterion("product_id <", value, "productId");
            return (Criteria) this;
        }

        public Criteria andProductIdLessThanOrEqualTo(Long value) {
            addCriterion("product_id <=", value, "productId");
            return (Criteria) this;
        }

        public Criteria andProductIdIn(List<Long> values) {
            addCriterion("product_id in", values, "productId");
            return (Criteria) this;
        }

        public Criteria andProductIdNotIn(List<Long> values) {
            addCriterion("product_id not in", values, "productId");
            return (Criteria) this;
        }

        public Criteria andProductIdBetween(Long value1, Long value2) {
            addCriterion("product_id between", value1, value2, "productId");
            return (Criteria) this;
        }

        public Criteria andProductIdNotBetween(Long value1, Long value2) {
            addCriterion("product_id not between", value1, value2, "productId");
            return (Criteria) this;
        }

        public Criteria andSkuCodeIsNull() {
            addCriterion("sku_code is null");
            return (Criteria) this;
        }

        public Criteria andSkuCodeIsNotNull() {
            addCriterion("sku_code is not null");
            return (Criteria) this;
        }

        public Criteria andSkuCodeEqualTo(String value) {
            addCriterion("sku_code =", value, "skuCode");
            return (Criteria) this;
        }

        public Criteria andSkuCodeNotEqualTo(String value) {
            addCriterion("sku_code <>", value, "skuCode");
            return (Criteria) this;
        }

        public Criteria andSkuCodeGreaterThan(String value) {
            addCriterion("sku_code >", value, "skuCode");
            return (Criteria) this;
        }

        public Criteria andSkuCodeGreaterThanOrEqualTo(String value) {
            addCriterion("sku_code >=", value, "skuCode");
            return (Criteria) this;
        }

        public Criteria andSkuCodeLessThan(String value) {
            addCriterion("sku_code <", value, "skuCode");
            return (Criteria) this;
        }

        public Criteria andSkuCodeLessThanOrEqualTo(String value) {
            addCriterion("sku_code <=", value, "skuCode");
            return (Criteria) this;
        }

        public Criteria andSkuCodeLike(String value) {
            addCriterion("sku_code like", value, "skuCode");
            return (Criteria) this;
        }

        public Criteria andSkuCodeNotLike(String value) {
            addCriterion("sku_code not like", value, "skuCode");
            return (Criteria) this;
        }

        public Criteria andSkuCodeIn(List<String> values) {
            addCriterion("sku_code in", values, "skuCode");
            return (Criteria) this;
        }

        public Criteria andSkuCodeNotIn(List<String> values) {
            addCriterion("sku_code not in", values, "skuCode");
            return (Criteria) this;
        }

        public Criteria andSkuCodeBetween(String value1, String value2) {
            addCriterion("sku_code between", value1, value2, "skuCode");
            return (Criteria) this;
        }

        public Criteria andSkuCodeNotBetween(String value1, String value2) {
            addCriterion("sku_code not between", value1, value2, "skuCode");
            return (Criteria) this;
        }

        public Criteria andPriceIsNull() {
            addCriterion("price is null");
            return (Criteria) this;
        }

        public Criteria andPriceIsNotNull() {
            addCriterion("price is not null");
            return (Criteria) this;
        }

        public Criteria andPriceEqualTo(BigDecimal value) {
            addCriterion("price =", value, "price");
            return (Criteria) this;
        }

        public Criteria andPriceNotEqualTo(BigDecimal value) {
            addCriterion("price <>", value, "price");
            return (Criteria) this;
        }

        public Criteria andPriceGreaterThan(BigDecimal value) {
            addCriterion("price >", value, "price");
            return (Criteria) this;
        }

        public Criteria andPriceGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("price >=", value, "price");
            return (Criteria) this;
        }

        public Criteria andPriceLessThan(BigDecimal value) {
            addCriterion("price <", value, "price");
            return (Criteria) this;
        }

        public Criteria andPriceLessThanOrEqualTo(BigDecimal value) {
            addCriterion("price <=", value, "price");
            return (Criteria) this;
        }

        public Criteria andPriceIn(List<BigDecimal> values) {
            addCriterion("price in", values, "price");
            return (Criteria) this;
        }

        public Criteria andPriceNotIn(List<BigDecimal> values) {
            addCriterion("price not in", values, "price");
            return (Criteria) this;
        }

        public Criteria andPriceBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("price between", value1, value2, "price");
            return (Criteria) this;
        }

        public Criteria andPriceNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("price not between", value1, value2, "price");
            return (Criteria) this;
        }

        public Criteria andStockIsNull() {
            addCriterion("stock is null");
            return (Criteria) this;
        }

        public Criteria andStockIsNotNull() {
            addCriterion("stock is not null");
            return (Criteria) this;
        }

        public Criteria andStockEqualTo(Integer value) {
            addCriterion("stock =", value, "stock");
            return (Criteria) this;
        }

        public Criteria andStockNotEqualTo(Integer value) {
            addCriterion("stock <>", value, "stock");
            return (Criteria) this;
        }

        public Criteria andStockGreaterThan(Integer value) {
            addCriterion("stock >", value, "stock");
            return (Criteria) this;
        }

        public Criteria andStockGreaterThanOrEqualTo(Integer value) {
            addCriterion("stock >=", value, "stock");
            return (Criteria) this;
        }

        public Criteria andStockLessThan(Integer value) {
            addCriterion("stock <", value, "stock");
            return (Criteria) this;
        }

        public Criteria andStockLessThanOrEqualTo(Integer value) {
            addCriterion("stock <=", value, "stock");
            return (Criteria) this;
        }

        public Criteria andStockIn(List<Integer> values) {
            addCriterion("stock in", values, "stock");
            return (Criteria) this;
        }

        public Criteria andStockNotIn(List<Integer> values) {
            addCriterion("stock not in", values, "stock");
            return (Criteria) this;
        }

        public Criteria andStockBetween(Integer value1, Integer value2) {
            addCriterion("stock between", value1, value2, "stock");
            return (Criteria) this;
        }

        public Criteria andStockNotBetween(Integer value1, Integer value2) {
            addCriterion("stock not between", value1, value2, "stock");
            return (Criteria) this;
        }

        public Criteria andLowStockIsNull() {
            addCriterion("low_stock is null");
            return (Criteria) this;
        }

        public Criteria andLowStockIsNotNull() {
            addCriterion("low_stock is not null");
            return (Criteria) this;
        }

        public Criteria andLowStockEqualTo(Integer value) {
            addCriterion("low_stock =", value, "lowStock");
            return (Criteria) this;
        }

        public Criteria andLowStockNotEqualTo(Integer value) {
            addCriterion("low_stock <>", value, "lowStock");
            return (Criteria) this;
        }

        public Criteria andLowStockGreaterThan(Integer value) {
            addCriterion("low_stock >", value, "lowStock");
            return (Criteria) this;
        }

        public Criteria andLowStockGreaterThanOrEqualTo(Integer value) {
            addCriterion("low_stock >=", value, "lowStock");
            return (Criteria) this;
        }

        public Criteria andLowStockLessThan(Integer value) {
            addCriterion("low_stock <", value, "lowStock");
            return (Criteria) this;
        }

        public Criteria andLowStockLessThanOrEqualTo(Integer value) {
            addCriterion("low_stock <=", value, "lowStock");
            return (Criteria) this;
        }

        public Criteria andLowStockIn(List<Integer> values) {
            addCriterion("low_stock in", values, "lowStock");
            return (Criteria) this;
        }

        public Criteria andLowStockNotIn(List<Integer> values) {
            addCriterion("low_stock not in", values, "lowStock");
            return (Criteria) this;
        }

        public Criteria andLowStockBetween(Integer value1, Integer value2) {
            addCriterion("low_stock between", value1, value2, "lowStock");
            return (Criteria) this;
        }

        public Criteria andLowStockNotBetween(Integer value1, Integer value2) {
            addCriterion("low_stock not between", value1, value2, "lowStock");
            return (Criteria) this;
        }

        public Criteria andPicIsNull() {
            addCriterion("pic is null");
            return (Criteria) this;
        }

        public Criteria andPicIsNotNull() {
            addCriterion("pic is not null");
            return (Criteria) this;
        }

        public Criteria andPicEqualTo(String value) {
            addCriterion("pic =", value, "pic");
            return (Criteria) this;
        }

        public Criteria andPicNotEqualTo(String value) {
            addCriterion("pic <>", value, "pic");
            return (Criteria) this;
        }

        public Criteria andPicGreaterThan(String value) {
            addCriterion("pic >", value, "pic");
            return (Criteria) this;
        }

        public Criteria andPicGreaterThanOrEqualTo(String value) {
            addCriterion("pic >=", value, "pic");
            return (Criteria) this;
        }

        public Criteria andPicLessThan(String value) {
            addCriterion("pic <", value, "pic");
            return (Criteria) this;
        }

        public Criteria andPicLessThanOrEqualTo(String value) {
            addCriterion("pic <=", value, "pic");
            return (Criteria) this;
        }

        public Criteria andPicLike(String value) {
            addCriterion("pic like", value, "pic");
            return (Criteria) this;
        }

        public Criteria andPicNotLike(String value) {
            addCriterion("pic not like", value, "pic");
            return (Criteria) this;
        }

        public Criteria andPicIn(List<String> values) {
            addCriterion("pic in", values, "pic");
            return (Criteria) this;
        }

        public Criteria andPicNotIn(List<String> values) {
            addCriterion("pic not in", values, "pic");
            return (Criteria) this;
        }

        public Criteria andPicBetween(String value1, String value2) {
            addCriterion("pic between", value1, value2, "pic");
            return (Criteria) this;
        }

        public Criteria andPicNotBetween(String value1, String value2) {
            addCriterion("pic not between", value1, value2, "pic");
            return (Criteria) this;
        }

        public Criteria andSaleIsNull() {
            addCriterion("sale is null");
            return (Criteria) this;
        }

        public Criteria andSaleIsNotNull() {
            addCriterion("sale is not null");
            return (Criteria) this;
        }

        public Criteria andSaleEqualTo(Integer value) {
            addCriterion("sale =", value, "sale");
            return (Criteria) this;
        }

        public Criteria andSaleNotEqualTo(Integer value) {
            addCriterion("sale <>", value, "sale");
            return (Criteria) this;
        }

        public Criteria andSaleGreaterThan(Integer value) {
            addCriterion("sale >", value, "sale");
            return (Criteria) this;
        }

        public Criteria andSaleGreaterThanOrEqualTo(Integer value) {
            addCriterion("sale >=", value, "sale");
            return (Criteria) this;
        }

        public Criteria andSaleLessThan(Integer value) {
            addCriterion("sale <", value, "sale");
            return (Criteria) this;
        }

        public Criteria andSaleLessThanOrEqualTo(Integer value) {
            addCriterion("sale <=", value, "sale");
            return (Criteria) this;
        }

        public Criteria andSaleIn(List<Integer> values) {
            addCriterion("sale in", values, "sale");
            return (Criteria) this;
        }

        public Criteria andSaleNotIn(List<Integer> values) {
            addCriterion("sale not in", values, "sale");
            return (Criteria) this;
        }

        public Criteria andSaleBetween(Integer value1, Integer value2) {
            addCriterion("sale between", value1, value2, "sale");
            return (Criteria) this;
        }

        public Criteria andSaleNotBetween(Integer value1, Integer value2) {
            addCriterion("sale not between", value1, value2, "sale");
            return (Criteria) this;
        }

        public Criteria andPromotionPriceIsNull() {
            addCriterion("promotion_price is null");
            return (Criteria) this;
        }

        public Criteria andPromotionPriceIsNotNull() {
            addCriterion("promotion_price is not null");
            return (Criteria) this;
        }

        public Criteria andPromotionPriceEqualTo(BigDecimal value) {
            addCriterion("promotion_price =", value, "promotionPrice");
            return (Criteria) this;
        }

        public Criteria andPromotionPriceNotEqualTo(BigDecimal value) {
            addCriterion("promotion_price <>", value, "promotionPrice");
            return (Criteria) this;
        }

        public Criteria andPromotionPriceGreaterThan(BigDecimal value) {
            addCriterion("promotion_price >", value, "promotionPrice");
            return (Criteria) this;
        }

        public Criteria andPromotionPriceGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("promotion_price >=", value, "promotionPrice");
            return (Criteria) this;
        }

        public Criteria andPromotionPriceLessThan(BigDecimal value) {
            addCriterion("promotion_price <", value, "promotionPrice");
            return (Criteria) this;
        }

        public Criteria andPromotionPriceLessThanOrEqualTo(BigDecimal value) {
            addCriterion("promotion_price <=", value, "promotionPrice");
            return (Criteria) this;
        }

        public Criteria andPromotionPriceIn(List<BigDecimal> values) {
            addCriterion("promotion_price in", values, "promotionPrice");
            return (Criteria) this;
        }

        public Criteria andPromotionPriceNotIn(List<BigDecimal> values) {
            addCriterion("promotion_price not in", values, "promotionPrice");
            return (Criteria) this;
        }

        public Criteria andPromotionPriceBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("promotion_price between", value1, value2, "promotionPrice");
            return (Criteria) this;
        }

        public Criteria andPromotionPriceNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("promotion_price not between", value1, value2, "promotionPrice");
            return (Criteria) this;
        }

        public Criteria andLockStockIsNull() {
            addCriterion("lock_stock is null");
            return (Criteria) this;
        }

        public Criteria andLockStockIsNotNull() {
            addCriterion("lock_stock is not null");
            return (Criteria) this;
        }

        public Criteria andLockStockEqualTo(Integer value) {
            addCriterion("lock_stock =", value, "lockStock");
            return (Criteria) this;
        }

        public Criteria andLockStockNotEqualTo(Integer value) {
            addCriterion("lock_stock <>", value, "lockStock");
            return (Criteria) this;
        }

        public Criteria andLockStockGreaterThan(Integer value) {
            addCriterion("lock_stock >", value, "lockStock");
            return (Criteria) this;
        }

        public Criteria andLockStockGreaterThanOrEqualTo(Integer value) {
            addCriterion("lock_stock >=", value, "lockStock");
            return (Criteria) this;
        }

        public Criteria andLockStockLessThan(Integer value) {
            addCriterion("lock_stock <", value, "lockStock");
            return (Criteria) this;
        }

        public Criteria andLockStockLessThanOrEqualTo(Integer value) {
            addCriterion("lock_stock <=", value, "lockStock");
            return (Criteria) this;
        }

        public Criteria andLockStockIn(List<Integer> values) {
            addCriterion("lock_stock in", values, "lockStock");
            return (Criteria) this;
        }

        public Criteria andLockStockNotIn(List<Integer> values) {
            addCriterion("lock_stock not in", values, "lockStock");
            return (Criteria) this;
        }

        public Criteria andLockStockBetween(Integer value1, Integer value2) {
            addCriterion("lock_stock between", value1, value2, "lockStock");
            return (Criteria) this;
        }

        public Criteria andLockStockNotBetween(Integer value1, Integer value2) {
            addCriterion("lock_stock not between", value1, value2, "lockStock");
            return (Criteria) this;
        }

        public Criteria andSpDataIsNull() {
            addCriterion("sp_data is null");
            return (Criteria) this;
        }

        public Criteria andSpDataIsNotNull() {
            addCriterion("sp_data is not null");
            return (Criteria) this;
        }

        public Criteria andSpDataEqualTo(String value) {
            addCriterion("sp_data =", value, "spData");
            return (Criteria) this;
        }

        public Criteria andSpDataNotEqualTo(String value) {
            addCriterion("sp_data <>", value, "spData");
            return (Criteria) this;
        }

        public Criteria andSpDataGreaterThan(String value) {
            addCriterion("sp_data >", value, "spData");
            return (Criteria) this;
        }

        public Criteria andSpDataGreaterThanOrEqualTo(String value) {
            addCriterion("sp_data >=", value, "spData");
            return (Criteria) this;
        }

        public Criteria andSpDataLessThan(String value) {
            addCriterion("sp_data <", value, "spData");
            return (Criteria) this;
        }

        public Criteria andSpDataLessThanOrEqualTo(String value) {
            addCriterion("sp_data <=", value, "spData");
            return (Criteria) this;
        }

        public Criteria andSpDataLike(String value) {
            addCriterion("sp_data like", value, "spData");
            return (Criteria) this;
        }

        public Criteria andSpDataNotLike(String value) {
            addCriterion("sp_data not like", value, "spData");
            return (Criteria) this;
        }

        public Criteria andSpDataIn(List<String> values) {
            addCriterion("sp_data in", values, "spData");
            return (Criteria) this;
        }

        public Criteria andSpDataNotIn(List<String> values) {
            addCriterion("sp_data not in", values, "spData");
            return (Criteria) this;
        }

        public Criteria andSpDataBetween(String value1, String value2) {
            addCriterion("sp_data between", value1, value2, "spData");
            return (Criteria) this;
        }

        public Criteria andSpDataNotBetween(String value1, String value2) {
            addCriterion("sp_data not between", value1, value2, "spData");
            return (Criteria) this;
        }

        public Criteria andMinPriceIsNull() {
            addCriterion("min_price is null");
            return (Criteria) this;
        }

        public Criteria andMinPriceIsNotNull() {
            addCriterion("min_price is not null");
            return (Criteria) this;
        }

        public Criteria andMinPriceEqualTo(BigDecimal value) {
            addCriterion("min_price =", value, "minPrice");
            return (Criteria) this;
        }

        public Criteria andMinPriceNotEqualTo(BigDecimal value) {
            addCriterion("min_price <>", value, "minPrice");
            return (Criteria) this;
        }

        public Criteria andMinPriceGreaterThan(BigDecimal value) {
            addCriterion("min_price >", value, "minPrice");
            return (Criteria) this;
        }

        public Criteria andMinPriceGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("min_price >=", value, "minPrice");
            return (Criteria) this;
        }

        public Criteria andMinPriceLessThan(BigDecimal value) {
            addCriterion("min_price <", value, "minPrice");
            return (Criteria) this;
        }

        public Criteria andMinPriceLessThanOrEqualTo(BigDecimal value) {
            addCriterion("min_price <=", value, "minPrice");
            return (Criteria) this;
        }

        public Criteria andMinPriceIn(List<BigDecimal> values) {
            addCriterion("min_price in", values, "minPrice");
            return (Criteria) this;
        }

        public Criteria andMinPriceNotIn(List<BigDecimal> values) {
            addCriterion("min_price not in", values, "minPrice");
            return (Criteria) this;
        }

        public Criteria andMinPriceBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("min_price between", value1, value2, "minPrice");
            return (Criteria) this;
        }

        public Criteria andMinPriceNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("min_price not between", value1, value2, "minPrice");
            return (Criteria) this;
        }

        public Criteria andMaxPriceIsNull() {
            addCriterion("max_price is null");
            return (Criteria) this;
        }

        public Criteria andMaxPriceIsNotNull() {
            addCriterion("max_price is not null");
            return (Criteria) this;
        }

        public Criteria andMaxPriceEqualTo(BigDecimal value) {
            addCriterion("max_price =", value, "maxPrice");
            return (Criteria) this;
        }

        public Criteria andMaxPriceNotEqualTo(BigDecimal value) {
            addCriterion("max_price <>", value, "maxPrice");
            return (Criteria) this;
        }

        public Criteria andMaxPriceGreaterThan(BigDecimal value) {
            addCriterion("max_price >", value, "maxPrice");
            return (Criteria) this;
        }

        public Criteria andMaxPriceGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("max_price >=", value, "maxPrice");
            return (Criteria) this;
        }

        public Criteria andMaxPriceLessThan(BigDecimal value) {
            addCriterion("max_price <", value, "maxPrice");
            return (Criteria) this;
        }

        public Criteria andMaxPriceLessThanOrEqualTo(BigDecimal value) {
            addCriterion("max_price <=", value, "maxPrice");
            return (Criteria) this;
        }

        public Criteria andMaxPriceIn(List<BigDecimal> values) {
            addCriterion("max_price in", values, "maxPrice");
            return (Criteria) this;
        }

        public Criteria andMaxPriceNotIn(List<BigDecimal> values) {
            addCriterion("max_price not in", values, "maxPrice");
            return (Criteria) this;
        }

        public Criteria andMaxPriceBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("max_price between", value1, value2, "maxPrice");
            return (Criteria) this;
        }

        public Criteria andMaxPriceNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("max_price not between", value1, value2, "maxPrice");
            return (Criteria) this;
        }

        public Criteria andMoqIsNull() {
            addCriterion("moq is null");
            return (Criteria) this;
        }

        public Criteria andMoqIsNotNull() {
            addCriterion("moq is not null");
            return (Criteria) this;
        }

        public Criteria andMoqEqualTo(Integer value) {
            addCriterion("moq =", value, "moq");
            return (Criteria) this;
        }

        public Criteria andMoqNotEqualTo(Integer value) {
            addCriterion("moq <>", value, "moq");
            return (Criteria) this;
        }

        public Criteria andMoqGreaterThan(Integer value) {
            addCriterion("moq >", value, "moq");
            return (Criteria) this;
        }

        public Criteria andMoqGreaterThanOrEqualTo(Integer value) {
            addCriterion("moq >=", value, "moq");
            return (Criteria) this;
        }

        public Criteria andMoqLessThan(Integer value) {
            addCriterion("moq <", value, "moq");
            return (Criteria) this;
        }

        public Criteria andMoqLessThanOrEqualTo(Integer value) {
            addCriterion("moq <=", value, "moq");
            return (Criteria) this;
        }

        public Criteria andMoqIn(List<Integer> values) {
            addCriterion("moq in", values, "moq");
            return (Criteria) this;
        }

        public Criteria andMoqNotIn(List<Integer> values) {
            addCriterion("moq not in", values, "moq");
            return (Criteria) this;
        }

        public Criteria andMoqBetween(Integer value1, Integer value2) {
            addCriterion("moq between", value1, value2, "moq");
            return (Criteria) this;
        }

        public Criteria andMoqNotBetween(Integer value1, Integer value2) {
            addCriterion("moq not between", value1, value2, "moq");
            return (Criteria) this;
        }

        public Criteria andMinMoqIsNull() {
            addCriterion("min_moq is null");
            return (Criteria) this;
        }

        public Criteria andMinMoqIsNotNull() {
            addCriterion("min_moq is not null");
            return (Criteria) this;
        }

        public Criteria andMinMoqEqualTo(Integer value) {
            addCriterion("min_moq =", value, "minMoq");
            return (Criteria) this;
        }

        public Criteria andMinMoqNotEqualTo(Integer value) {
            addCriterion("min_moq <>", value, "minMoq");
            return (Criteria) this;
        }

        public Criteria andMinMoqGreaterThan(Integer value) {
            addCriterion("min_moq >", value, "minMoq");
            return (Criteria) this;
        }

        public Criteria andMinMoqGreaterThanOrEqualTo(Integer value) {
            addCriterion("min_moq >=", value, "minMoq");
            return (Criteria) this;
        }

        public Criteria andMinMoqLessThan(Integer value) {
            addCriterion("min_moq <", value, "minMoq");
            return (Criteria) this;
        }

        public Criteria andMinMoqLessThanOrEqualTo(Integer value) {
            addCriterion("min_moq <=", value, "minMoq");
            return (Criteria) this;
        }

        public Criteria andMinMoqIn(List<Integer> values) {
            addCriterion("min_moq in", values, "minMoq");
            return (Criteria) this;
        }

        public Criteria andMinMoqNotIn(List<Integer> values) {
            addCriterion("min_moq not in", values, "minMoq");
            return (Criteria) this;
        }

        public Criteria andMinMoqBetween(Integer value1, Integer value2) {
            addCriterion("min_moq between", value1, value2, "minMoq");
            return (Criteria) this;
        }

        public Criteria andMinMoqNotBetween(Integer value1, Integer value2) {
            addCriterion("min_moq not between", value1, value2, "minMoq");
            return (Criteria) this;
        }

        public Criteria andMaxMoqIsNull() {
            addCriterion("max_moq is null");
            return (Criteria) this;
        }

        public Criteria andMaxMoqIsNotNull() {
            addCriterion("max_moq is not null");
            return (Criteria) this;
        }

        public Criteria andMaxMoqEqualTo(Integer value) {
            addCriterion("max_moq =", value, "maxMoq");
            return (Criteria) this;
        }

        public Criteria andMaxMoqNotEqualTo(Integer value) {
            addCriterion("max_moq <>", value, "maxMoq");
            return (Criteria) this;
        }

        public Criteria andMaxMoqGreaterThan(Integer value) {
            addCriterion("max_moq >", value, "maxMoq");
            return (Criteria) this;
        }

        public Criteria andMaxMoqGreaterThanOrEqualTo(Integer value) {
            addCriterion("max_moq >=", value, "maxMoq");
            return (Criteria) this;
        }

        public Criteria andMaxMoqLessThan(Integer value) {
            addCriterion("max_moq <", value, "maxMoq");
            return (Criteria) this;
        }

        public Criteria andMaxMoqLessThanOrEqualTo(Integer value) {
            addCriterion("max_moq <=", value, "maxMoq");
            return (Criteria) this;
        }

        public Criteria andMaxMoqIn(List<Integer> values) {
            addCriterion("max_moq in", values, "maxMoq");
            return (Criteria) this;
        }

        public Criteria andMaxMoqNotIn(List<Integer> values) {
            addCriterion("max_moq not in", values, "maxMoq");
            return (Criteria) this;
        }

        public Criteria andMaxMoqBetween(Integer value1, Integer value2) {
            addCriterion("max_moq between", value1, value2, "maxMoq");
            return (Criteria) this;
        }

        public Criteria andMaxMoqNotBetween(Integer value1, Integer value2) {
            addCriterion("max_moq not between", value1, value2, "maxMoq");
            return (Criteria) this;
        }

        public Criteria andWeightIsNull() {
            addCriterion("weight is null");
            return (Criteria) this;
        }

        public Criteria andWeightIsNotNull() {
            addCriterion("weight is not null");
            return (Criteria) this;
        }

        public Criteria andWeightEqualTo(BigDecimal value) {
            addCriterion("weight =", value, "weight");
            return (Criteria) this;
        }

        public Criteria andWeightNotEqualTo(BigDecimal value) {
            addCriterion("weight <>", value, "weight");
            return (Criteria) this;
        }

        public Criteria andWeightGreaterThan(BigDecimal value) {
            addCriterion("weight >", value, "weight");
            return (Criteria) this;
        }

        public Criteria andWeightGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("weight >=", value, "weight");
            return (Criteria) this;
        }

        public Criteria andWeightLessThan(BigDecimal value) {
            addCriterion("weight <", value, "weight");
            return (Criteria) this;
        }

        public Criteria andWeightLessThanOrEqualTo(BigDecimal value) {
            addCriterion("weight <=", value, "weight");
            return (Criteria) this;
        }

        public Criteria andWeightIn(List<BigDecimal> values) {
            addCriterion("weight in", values, "weight");
            return (Criteria) this;
        }

        public Criteria andWeightNotIn(List<BigDecimal> values) {
            addCriterion("weight not in", values, "weight");
            return (Criteria) this;
        }

        public Criteria andWeightBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("weight between", value1, value2, "weight");
            return (Criteria) this;
        }

        public Criteria andWeightNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("weight not between", value1, value2, "weight");
            return (Criteria) this;
        }

        public Criteria andVolumeLenghtIsNull() {
            addCriterion("volume_lenght is null");
            return (Criteria) this;
        }

        public Criteria andVolumeLenghtIsNotNull() {
            addCriterion("volume_lenght is not null");
            return (Criteria) this;
        }

        public Criteria andVolumeLenghtEqualTo(Integer value) {
            addCriterion("volume_lenght =", value, "volumeLenght");
            return (Criteria) this;
        }

        public Criteria andVolumeLenghtNotEqualTo(Integer value) {
            addCriterion("volume_lenght <>", value, "volumeLenght");
            return (Criteria) this;
        }

        public Criteria andVolumeLenghtGreaterThan(Integer value) {
            addCriterion("volume_lenght >", value, "volumeLenght");
            return (Criteria) this;
        }

        public Criteria andVolumeLenghtGreaterThanOrEqualTo(Integer value) {
            addCriterion("volume_lenght >=", value, "volumeLenght");
            return (Criteria) this;
        }

        public Criteria andVolumeLenghtLessThan(Integer value) {
            addCriterion("volume_lenght <", value, "volumeLenght");
            return (Criteria) this;
        }

        public Criteria andVolumeLenghtLessThanOrEqualTo(Integer value) {
            addCriterion("volume_lenght <=", value, "volumeLenght");
            return (Criteria) this;
        }

        public Criteria andVolumeLenghtIn(List<Integer> values) {
            addCriterion("volume_lenght in", values, "volumeLenght");
            return (Criteria) this;
        }

        public Criteria andVolumeLenghtNotIn(List<Integer> values) {
            addCriterion("volume_lenght not in", values, "volumeLenght");
            return (Criteria) this;
        }

        public Criteria andVolumeLenghtBetween(Integer value1, Integer value2) {
            addCriterion("volume_lenght between", value1, value2, "volumeLenght");
            return (Criteria) this;
        }

        public Criteria andVolumeLenghtNotBetween(Integer value1, Integer value2) {
            addCriterion("volume_lenght not between", value1, value2, "volumeLenght");
            return (Criteria) this;
        }

        public Criteria andVolumeWidthIsNull() {
            addCriterion("volume_width is null");
            return (Criteria) this;
        }

        public Criteria andVolumeWidthIsNotNull() {
            addCriterion("volume_width is not null");
            return (Criteria) this;
        }

        public Criteria andVolumeWidthEqualTo(Integer value) {
            addCriterion("volume_width =", value, "volumeWidth");
            return (Criteria) this;
        }

        public Criteria andVolumeWidthNotEqualTo(Integer value) {
            addCriterion("volume_width <>", value, "volumeWidth");
            return (Criteria) this;
        }

        public Criteria andVolumeWidthGreaterThan(Integer value) {
            addCriterion("volume_width >", value, "volumeWidth");
            return (Criteria) this;
        }

        public Criteria andVolumeWidthGreaterThanOrEqualTo(Integer value) {
            addCriterion("volume_width >=", value, "volumeWidth");
            return (Criteria) this;
        }

        public Criteria andVolumeWidthLessThan(Integer value) {
            addCriterion("volume_width <", value, "volumeWidth");
            return (Criteria) this;
        }

        public Criteria andVolumeWidthLessThanOrEqualTo(Integer value) {
            addCriterion("volume_width <=", value, "volumeWidth");
            return (Criteria) this;
        }

        public Criteria andVolumeWidthIn(List<Integer> values) {
            addCriterion("volume_width in", values, "volumeWidth");
            return (Criteria) this;
        }

        public Criteria andVolumeWidthNotIn(List<Integer> values) {
            addCriterion("volume_width not in", values, "volumeWidth");
            return (Criteria) this;
        }

        public Criteria andVolumeWidthBetween(Integer value1, Integer value2) {
            addCriterion("volume_width between", value1, value2, "volumeWidth");
            return (Criteria) this;
        }

        public Criteria andVolumeWidthNotBetween(Integer value1, Integer value2) {
            addCriterion("volume_width not between", value1, value2, "volumeWidth");
            return (Criteria) this;
        }

        public Criteria andVolumeHeightIsNull() {
            addCriterion("volume_height is null");
            return (Criteria) this;
        }

        public Criteria andVolumeHeightIsNotNull() {
            addCriterion("volume_height is not null");
            return (Criteria) this;
        }

        public Criteria andVolumeHeightEqualTo(Integer value) {
            addCriterion("volume_height =", value, "volumeHeight");
            return (Criteria) this;
        }

        public Criteria andVolumeHeightNotEqualTo(Integer value) {
            addCriterion("volume_height <>", value, "volumeHeight");
            return (Criteria) this;
        }

        public Criteria andVolumeHeightGreaterThan(Integer value) {
            addCriterion("volume_height >", value, "volumeHeight");
            return (Criteria) this;
        }

        public Criteria andVolumeHeightGreaterThanOrEqualTo(Integer value) {
            addCriterion("volume_height >=", value, "volumeHeight");
            return (Criteria) this;
        }

        public Criteria andVolumeHeightLessThan(Integer value) {
            addCriterion("volume_height <", value, "volumeHeight");
            return (Criteria) this;
        }

        public Criteria andVolumeHeightLessThanOrEqualTo(Integer value) {
            addCriterion("volume_height <=", value, "volumeHeight");
            return (Criteria) this;
        }

        public Criteria andVolumeHeightIn(List<Integer> values) {
            addCriterion("volume_height in", values, "volumeHeight");
            return (Criteria) this;
        }

        public Criteria andVolumeHeightNotIn(List<Integer> values) {
            addCriterion("volume_height not in", values, "volumeHeight");
            return (Criteria) this;
        }

        public Criteria andVolumeHeightBetween(Integer value1, Integer value2) {
            addCriterion("volume_height between", value1, value2, "volumeHeight");
            return (Criteria) this;
        }

        public Criteria andVolumeHeightNotBetween(Integer value1, Integer value2) {
            addCriterion("volume_height not between", value1, value2, "volumeHeight");
            return (Criteria) this;
        }

        public Criteria andVolumeIsNull() {
            addCriterion("volume is null");
            return (Criteria) this;
        }

        public Criteria andVolumeIsNotNull() {
            addCriterion("volume is not null");
            return (Criteria) this;
        }

        public Criteria andVolumeEqualTo(Double value) {
            addCriterion("volume =", value, "volume");
            return (Criteria) this;
        }

        public Criteria andVolumeNotEqualTo(Double value) {
            addCriterion("volume <>", value, "volume");
            return (Criteria) this;
        }

        public Criteria andVolumeGreaterThan(Double value) {
            addCriterion("volume >", value, "volume");
            return (Criteria) this;
        }

        public Criteria andVolumeGreaterThanOrEqualTo(Double value) {
            addCriterion("volume >=", value, "volume");
            return (Criteria) this;
        }

        public Criteria andVolumeLessThan(Double value) {
            addCriterion("volume <", value, "volume");
            return (Criteria) this;
        }

        public Criteria andVolumeLessThanOrEqualTo(Double value) {
            addCriterion("volume <=", value, "volume");
            return (Criteria) this;
        }

        public Criteria andVolumeIn(List<Double> values) {
            addCriterion("volume in", values, "volume");
            return (Criteria) this;
        }

        public Criteria andVolumeNotIn(List<Double> values) {
            addCriterion("volume not in", values, "volume");
            return (Criteria) this;
        }

        public Criteria andVolumeBetween(Double value1, Double value2) {
            addCriterion("volume between", value1, value2, "volume");
            return (Criteria) this;
        }

        public Criteria andVolumeNotBetween(Double value1, Double value2) {
            addCriterion("volume not between", value1, value2, "volume");
            return (Criteria) this;
        }

        public Criteria andShipsFromIsNull() {
            addCriterion("ships_from is null");
            return (Criteria) this;
        }

        public Criteria andShipsFromIsNotNull() {
            addCriterion("ships_from is not null");
            return (Criteria) this;
        }

        public Criteria andShipsFromEqualTo(String value) {
            addCriterion("ships_from =", value, "shipsFrom");
            return (Criteria) this;
        }

        public Criteria andShipsFromNotEqualTo(String value) {
            addCriterion("ships_from <>", value, "shipsFrom");
            return (Criteria) this;
        }

        public Criteria andShipsFromGreaterThan(String value) {
            addCriterion("ships_from >", value, "shipsFrom");
            return (Criteria) this;
        }

        public Criteria andShipsFromGreaterThanOrEqualTo(String value) {
            addCriterion("ships_from >=", value, "shipsFrom");
            return (Criteria) this;
        }

        public Criteria andShipsFromLessThan(String value) {
            addCriterion("ships_from <", value, "shipsFrom");
            return (Criteria) this;
        }

        public Criteria andShipsFromLessThanOrEqualTo(String value) {
            addCriterion("ships_from <=", value, "shipsFrom");
            return (Criteria) this;
        }

        public Criteria andShipsFromLike(String value) {
            addCriterion("ships_from like", value, "shipsFrom");
            return (Criteria) this;
        }

        public Criteria andShipsFromNotLike(String value) {
            addCriterion("ships_from not like", value, "shipsFrom");
            return (Criteria) this;
        }

        public Criteria andShipsFromIn(List<String> values) {
            addCriterion("ships_from in", values, "shipsFrom");
            return (Criteria) this;
        }

        public Criteria andShipsFromNotIn(List<String> values) {
            addCriterion("ships_from not in", values, "shipsFrom");
            return (Criteria) this;
        }

        public Criteria andShipsFromBetween(String value1, String value2) {
            addCriterion("ships_from between", value1, value2, "shipsFrom");
            return (Criteria) this;
        }

        public Criteria andShipsFromNotBetween(String value1, String value2) {
            addCriterion("ships_from not between", value1, value2, "shipsFrom");
            return (Criteria) this;
        }

        public Criteria andProfitIsNull() {
            addCriterion("profit is null");
            return (Criteria) this;
        }

        public Criteria andProfitIsNotNull() {
            addCriterion("profit is not null");
            return (Criteria) this;
        }

        public Criteria andProfitEqualTo(String value) {
            addCriterion("profit =", value, "profit");
            return (Criteria) this;
        }

        public Criteria andProfitNotEqualTo(String value) {
            addCriterion("profit <>", value, "profit");
            return (Criteria) this;
        }

        public Criteria andProfitGreaterThan(String value) {
            addCriterion("profit >", value, "profit");
            return (Criteria) this;
        }

        public Criteria andProfitGreaterThanOrEqualTo(String value) {
            addCriterion("profit >=", value, "profit");
            return (Criteria) this;
        }

        public Criteria andProfitLessThan(String value) {
            addCriterion("profit <", value, "profit");
            return (Criteria) this;
        }

        public Criteria andProfitLessThanOrEqualTo(String value) {
            addCriterion("profit <=", value, "profit");
            return (Criteria) this;
        }

        public Criteria andProfitLike(String value) {
            addCriterion("profit like", value, "profit");
            return (Criteria) this;
        }

        public Criteria andProfitNotLike(String value) {
            addCriterion("profit not like", value, "profit");
            return (Criteria) this;
        }

        public Criteria andProfitIn(List<String> values) {
            addCriterion("profit in", values, "profit");
            return (Criteria) this;
        }

        public Criteria andProfitNotIn(List<String> values) {
            addCriterion("profit not in", values, "profit");
            return (Criteria) this;
        }

        public Criteria andProfitBetween(String value1, String value2) {
            addCriterion("profit between", value1, value2, "profit");
            return (Criteria) this;
        }

        public Criteria andProfitNotBetween(String value1, String value2) {
            addCriterion("profit not between", value1, value2, "profit");
            return (Criteria) this;
        }

        public Criteria andStandardIsNull() {
            addCriterion("standard is null");
            return (Criteria) this;
        }

        public Criteria andStandardIsNotNull() {
            addCriterion("standard is not null");
            return (Criteria) this;
        }

        public Criteria andStandardEqualTo(String value) {
            addCriterion("standard =", value, "standard");
            return (Criteria) this;
        }

        public Criteria andStandardNotEqualTo(String value) {
            addCriterion("standard <>", value, "standard");
            return (Criteria) this;
        }

        public Criteria andStandardGreaterThan(String value) {
            addCriterion("standard >", value, "standard");
            return (Criteria) this;
        }

        public Criteria andStandardGreaterThanOrEqualTo(String value) {
            addCriterion("standard >=", value, "standard");
            return (Criteria) this;
        }

        public Criteria andStandardLessThan(String value) {
            addCriterion("standard <", value, "standard");
            return (Criteria) this;
        }

        public Criteria andStandardLessThanOrEqualTo(String value) {
            addCriterion("standard <=", value, "standard");
            return (Criteria) this;
        }

        public Criteria andStandardLike(String value) {
            addCriterion("standard like", value, "standard");
            return (Criteria) this;
        }

        public Criteria andStandardNotLike(String value) {
            addCriterion("standard not like", value, "standard");
            return (Criteria) this;
        }

        public Criteria andStandardIn(List<String> values) {
            addCriterion("standard in", values, "standard");
            return (Criteria) this;
        }

        public Criteria andStandardNotIn(List<String> values) {
            addCriterion("standard not in", values, "standard");
            return (Criteria) this;
        }

        public Criteria andStandardBetween(String value1, String value2) {
            addCriterion("standard between", value1, value2, "standard");
            return (Criteria) this;
        }

        public Criteria andStandardNotBetween(String value1, String value2) {
            addCriterion("standard not between", value1, value2, "standard");
            return (Criteria) this;
        }

        public Criteria andSkuvalue1IsNull() {
            addCriterion("skuValue1 is null");
            return (Criteria) this;
        }

        public Criteria andSkuvalue1IsNotNull() {
            addCriterion("skuValue1 is not null");
            return (Criteria) this;
        }

        public Criteria andSkuvalue1EqualTo(String value) {
            addCriterion("skuValue1 =", value, "skuvalue1");
            return (Criteria) this;
        }

        public Criteria andSkuvalue1NotEqualTo(String value) {
            addCriterion("skuValue1 <>", value, "skuvalue1");
            return (Criteria) this;
        }

        public Criteria andSkuvalue1GreaterThan(String value) {
            addCriterion("skuValue1 >", value, "skuvalue1");
            return (Criteria) this;
        }

        public Criteria andSkuvalue1GreaterThanOrEqualTo(String value) {
            addCriterion("skuValue1 >=", value, "skuvalue1");
            return (Criteria) this;
        }

        public Criteria andSkuvalue1LessThan(String value) {
            addCriterion("skuValue1 <", value, "skuvalue1");
            return (Criteria) this;
        }

        public Criteria andSkuvalue1LessThanOrEqualTo(String value) {
            addCriterion("skuValue1 <=", value, "skuvalue1");
            return (Criteria) this;
        }

        public Criteria andSkuvalue1Like(String value) {
            addCriterion("skuValue1 like", value, "skuvalue1");
            return (Criteria) this;
        }

        public Criteria andSkuvalue1NotLike(String value) {
            addCriterion("skuValue1 not like", value, "skuvalue1");
            return (Criteria) this;
        }

        public Criteria andSkuvalue1In(List<String> values) {
            addCriterion("skuValue1 in", values, "skuvalue1");
            return (Criteria) this;
        }

        public Criteria andSkuvalue1NotIn(List<String> values) {
            addCriterion("skuValue1 not in", values, "skuvalue1");
            return (Criteria) this;
        }

        public Criteria andSkuvalue1Between(String value1, String value2) {
            addCriterion("skuValue1 between", value1, value2, "skuvalue1");
            return (Criteria) this;
        }

        public Criteria andSkuvalue1NotBetween(String value1, String value2) {
            addCriterion("skuValue1 not between", value1, value2, "skuvalue1");
            return (Criteria) this;
        }

        public Criteria andSkuvalue2IsNull() {
            addCriterion("skuValue2 is null");
            return (Criteria) this;
        }

        public Criteria andSkuvalue2IsNotNull() {
            addCriterion("skuValue2 is not null");
            return (Criteria) this;
        }

        public Criteria andSkuvalue2EqualTo(String value) {
            addCriterion("skuValue2 =", value, "skuvalue2");
            return (Criteria) this;
        }

        public Criteria andSkuvalue2NotEqualTo(String value) {
            addCriterion("skuValue2 <>", value, "skuvalue2");
            return (Criteria) this;
        }

        public Criteria andSkuvalue2GreaterThan(String value) {
            addCriterion("skuValue2 >", value, "skuvalue2");
            return (Criteria) this;
        }

        public Criteria andSkuvalue2GreaterThanOrEqualTo(String value) {
            addCriterion("skuValue2 >=", value, "skuvalue2");
            return (Criteria) this;
        }

        public Criteria andSkuvalue2LessThan(String value) {
            addCriterion("skuValue2 <", value, "skuvalue2");
            return (Criteria) this;
        }

        public Criteria andSkuvalue2LessThanOrEqualTo(String value) {
            addCriterion("skuValue2 <=", value, "skuvalue2");
            return (Criteria) this;
        }

        public Criteria andSkuvalue2Like(String value) {
            addCriterion("skuValue2 like", value, "skuvalue2");
            return (Criteria) this;
        }

        public Criteria andSkuvalue2NotLike(String value) {
            addCriterion("skuValue2 not like", value, "skuvalue2");
            return (Criteria) this;
        }

        public Criteria andSkuvalue2In(List<String> values) {
            addCriterion("skuValue2 in", values, "skuvalue2");
            return (Criteria) this;
        }

        public Criteria andSkuvalue2NotIn(List<String> values) {
            addCriterion("skuValue2 not in", values, "skuvalue2");
            return (Criteria) this;
        }

        public Criteria andSkuvalue2Between(String value1, String value2) {
            addCriterion("skuValue2 between", value1, value2, "skuvalue2");
            return (Criteria) this;
        }

        public Criteria andSkuvalue2NotBetween(String value1, String value2) {
            addCriterion("skuValue2 not between", value1, value2, "skuvalue2");
            return (Criteria) this;
        }

        public Criteria andSkuvalue3IsNull() {
            addCriterion("skuValue3 is null");
            return (Criteria) this;
        }

        public Criteria andSkuvalue3IsNotNull() {
            addCriterion("skuValue3 is not null");
            return (Criteria) this;
        }

        public Criteria andSkuvalue3EqualTo(String value) {
            addCriterion("skuValue3 =", value, "skuvalue3");
            return (Criteria) this;
        }

        public Criteria andSkuvalue3NotEqualTo(String value) {
            addCriterion("skuValue3 <>", value, "skuvalue3");
            return (Criteria) this;
        }

        public Criteria andSkuvalue3GreaterThan(String value) {
            addCriterion("skuValue3 >", value, "skuvalue3");
            return (Criteria) this;
        }

        public Criteria andSkuvalue3GreaterThanOrEqualTo(String value) {
            addCriterion("skuValue3 >=", value, "skuvalue3");
            return (Criteria) this;
        }

        public Criteria andSkuvalue3LessThan(String value) {
            addCriterion("skuValue3 <", value, "skuvalue3");
            return (Criteria) this;
        }

        public Criteria andSkuvalue3LessThanOrEqualTo(String value) {
            addCriterion("skuValue3 <=", value, "skuvalue3");
            return (Criteria) this;
        }

        public Criteria andSkuvalue3Like(String value) {
            addCriterion("skuValue3 like", value, "skuvalue3");
            return (Criteria) this;
        }

        public Criteria andSkuvalue3NotLike(String value) {
            addCriterion("skuValue3 not like", value, "skuvalue3");
            return (Criteria) this;
        }

        public Criteria andSkuvalue3In(List<String> values) {
            addCriterion("skuValue3 in", values, "skuvalue3");
            return (Criteria) this;
        }

        public Criteria andSkuvalue3NotIn(List<String> values) {
            addCriterion("skuValue3 not in", values, "skuvalue3");
            return (Criteria) this;
        }

        public Criteria andSkuvalue3Between(String value1, String value2) {
            addCriterion("skuValue3 between", value1, value2, "skuvalue3");
            return (Criteria) this;
        }

        public Criteria andSkuvalue3NotBetween(String value1, String value2) {
            addCriterion("skuValue3 not between", value1, value2, "skuvalue3");
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