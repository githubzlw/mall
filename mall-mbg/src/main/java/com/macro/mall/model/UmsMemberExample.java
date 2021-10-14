package com.macro.mall.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class UmsMemberExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public UmsMemberExample() {
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

        protected void addCriterionForJDBCDate(String condition, Date value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            addCriterion(condition, new java.sql.Date(value.getTime()), property);
        }

        protected void addCriterionForJDBCDate(String condition, List<Date> values, String property) {
            if (values == null || values.size() == 0) {
                throw new RuntimeException("Value list for " + property + " cannot be null or empty");
            }
            List<java.sql.Date> dateList = new ArrayList<>();
            Iterator<Date> iter = values.iterator();
            while (iter.hasNext()) {
                dateList.add(new java.sql.Date(iter.next().getTime()));
            }
            addCriterion(condition, dateList, property);
        }

        protected void addCriterionForJDBCDate(String condition, Date value1, Date value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            addCriterion(condition, new java.sql.Date(value1.getTime()), new java.sql.Date(value2.getTime()), property);
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

        public Criteria andMemberLevelIdIsNull() {
            addCriterion("member_level_id is null");
            return (Criteria) this;
        }

        public Criteria andMemberLevelIdIsNotNull() {
            addCriterion("member_level_id is not null");
            return (Criteria) this;
        }

        public Criteria andMemberLevelIdEqualTo(Long value) {
            addCriterion("member_level_id =", value, "memberLevelId");
            return (Criteria) this;
        }

        public Criteria andMemberLevelIdNotEqualTo(Long value) {
            addCriterion("member_level_id <>", value, "memberLevelId");
            return (Criteria) this;
        }

        public Criteria andMemberLevelIdGreaterThan(Long value) {
            addCriterion("member_level_id >", value, "memberLevelId");
            return (Criteria) this;
        }

        public Criteria andMemberLevelIdGreaterThanOrEqualTo(Long value) {
            addCriterion("member_level_id >=", value, "memberLevelId");
            return (Criteria) this;
        }

        public Criteria andMemberLevelIdLessThan(Long value) {
            addCriterion("member_level_id <", value, "memberLevelId");
            return (Criteria) this;
        }

        public Criteria andMemberLevelIdLessThanOrEqualTo(Long value) {
            addCriterion("member_level_id <=", value, "memberLevelId");
            return (Criteria) this;
        }

        public Criteria andMemberLevelIdIn(List<Long> values) {
            addCriterion("member_level_id in", values, "memberLevelId");
            return (Criteria) this;
        }

        public Criteria andMemberLevelIdNotIn(List<Long> values) {
            addCriterion("member_level_id not in", values, "memberLevelId");
            return (Criteria) this;
        }

        public Criteria andMemberLevelIdBetween(Long value1, Long value2) {
            addCriterion("member_level_id between", value1, value2, "memberLevelId");
            return (Criteria) this;
        }

        public Criteria andMemberLevelIdNotBetween(Long value1, Long value2) {
            addCriterion("member_level_id not between", value1, value2, "memberLevelId");
            return (Criteria) this;
        }

        public Criteria andUsernameIsNull() {
            addCriterion("username is null");
            return (Criteria) this;
        }

        public Criteria andUsernameIsNotNull() {
            addCriterion("username is not null");
            return (Criteria) this;
        }

        public Criteria andUsernameEqualTo(String value) {
            addCriterion("username =", value, "username");
            return (Criteria) this;
        }

        public Criteria andUsernameNotEqualTo(String value) {
            addCriterion("username <>", value, "username");
            return (Criteria) this;
        }

        public Criteria andUsernameGreaterThan(String value) {
            addCriterion("username >", value, "username");
            return (Criteria) this;
        }

        public Criteria andUsernameGreaterThanOrEqualTo(String value) {
            addCriterion("username >=", value, "username");
            return (Criteria) this;
        }

        public Criteria andUsernameLessThan(String value) {
            addCriterion("username <", value, "username");
            return (Criteria) this;
        }

        public Criteria andUsernameLessThanOrEqualTo(String value) {
            addCriterion("username <=", value, "username");
            return (Criteria) this;
        }

        public Criteria andUsernameLike(String value) {
            addCriterion("username like", value, "username");
            return (Criteria) this;
        }

        public Criteria andUsernameNotLike(String value) {
            addCriterion("username not like", value, "username");
            return (Criteria) this;
        }

        public Criteria andUsernameIn(List<String> values) {
            addCriterion("username in", values, "username");
            return (Criteria) this;
        }

        public Criteria andUsernameNotIn(List<String> values) {
            addCriterion("username not in", values, "username");
            return (Criteria) this;
        }

        public Criteria andUsernameBetween(String value1, String value2) {
            addCriterion("username between", value1, value2, "username");
            return (Criteria) this;
        }

        public Criteria andUsernameNotBetween(String value1, String value2) {
            addCriterion("username not between", value1, value2, "username");
            return (Criteria) this;
        }

        public Criteria andPasswordIsNull() {
            addCriterion("password is null");
            return (Criteria) this;
        }

        public Criteria andPasswordIsNotNull() {
            addCriterion("password is not null");
            return (Criteria) this;
        }

        public Criteria andPasswordEqualTo(String value) {
            addCriterion("password =", value, "password");
            return (Criteria) this;
        }

        public Criteria andPasswordNotEqualTo(String value) {
            addCriterion("password <>", value, "password");
            return (Criteria) this;
        }

        public Criteria andPasswordGreaterThan(String value) {
            addCriterion("password >", value, "password");
            return (Criteria) this;
        }

        public Criteria andPasswordGreaterThanOrEqualTo(String value) {
            addCriterion("password >=", value, "password");
            return (Criteria) this;
        }

        public Criteria andPasswordLessThan(String value) {
            addCriterion("password <", value, "password");
            return (Criteria) this;
        }

        public Criteria andPasswordLessThanOrEqualTo(String value) {
            addCriterion("password <=", value, "password");
            return (Criteria) this;
        }

        public Criteria andPasswordLike(String value) {
            addCriterion("password like", value, "password");
            return (Criteria) this;
        }

        public Criteria andPasswordNotLike(String value) {
            addCriterion("password not like", value, "password");
            return (Criteria) this;
        }

        public Criteria andPasswordIn(List<String> values) {
            addCriterion("password in", values, "password");
            return (Criteria) this;
        }

        public Criteria andPasswordNotIn(List<String> values) {
            addCriterion("password not in", values, "password");
            return (Criteria) this;
        }

        public Criteria andPasswordBetween(String value1, String value2) {
            addCriterion("password between", value1, value2, "password");
            return (Criteria) this;
        }

        public Criteria andPasswordNotBetween(String value1, String value2) {
            addCriterion("password not between", value1, value2, "password");
            return (Criteria) this;
        }

        public Criteria andNicknameIsNull() {
            addCriterion("nickname is null");
            return (Criteria) this;
        }

        public Criteria andNicknameIsNotNull() {
            addCriterion("nickname is not null");
            return (Criteria) this;
        }

        public Criteria andNicknameEqualTo(String value) {
            addCriterion("nickname =", value, "nickname");
            return (Criteria) this;
        }

        public Criteria andNicknameNotEqualTo(String value) {
            addCriterion("nickname <>", value, "nickname");
            return (Criteria) this;
        }

        public Criteria andNicknameGreaterThan(String value) {
            addCriterion("nickname >", value, "nickname");
            return (Criteria) this;
        }

        public Criteria andNicknameGreaterThanOrEqualTo(String value) {
            addCriterion("nickname >=", value, "nickname");
            return (Criteria) this;
        }

        public Criteria andNicknameLessThan(String value) {
            addCriterion("nickname <", value, "nickname");
            return (Criteria) this;
        }

        public Criteria andNicknameLessThanOrEqualTo(String value) {
            addCriterion("nickname <=", value, "nickname");
            return (Criteria) this;
        }

        public Criteria andNicknameLike(String value) {
            addCriterion("nickname like", value, "nickname");
            return (Criteria) this;
        }

        public Criteria andNicknameNotLike(String value) {
            addCriterion("nickname not like", value, "nickname");
            return (Criteria) this;
        }

        public Criteria andNicknameIn(List<String> values) {
            addCriterion("nickname in", values, "nickname");
            return (Criteria) this;
        }

        public Criteria andNicknameNotIn(List<String> values) {
            addCriterion("nickname not in", values, "nickname");
            return (Criteria) this;
        }

        public Criteria andNicknameBetween(String value1, String value2) {
            addCriterion("nickname between", value1, value2, "nickname");
            return (Criteria) this;
        }

        public Criteria andNicknameNotBetween(String value1, String value2) {
            addCriterion("nickname not between", value1, value2, "nickname");
            return (Criteria) this;
        }

        public Criteria andPhoneIsNull() {
            addCriterion("phone is null");
            return (Criteria) this;
        }

        public Criteria andPhoneIsNotNull() {
            addCriterion("phone is not null");
            return (Criteria) this;
        }

        public Criteria andPhoneEqualTo(String value) {
            addCriterion("phone =", value, "phone");
            return (Criteria) this;
        }

        public Criteria andPhoneNotEqualTo(String value) {
            addCriterion("phone <>", value, "phone");
            return (Criteria) this;
        }

        public Criteria andPhoneGreaterThan(String value) {
            addCriterion("phone >", value, "phone");
            return (Criteria) this;
        }

        public Criteria andPhoneGreaterThanOrEqualTo(String value) {
            addCriterion("phone >=", value, "phone");
            return (Criteria) this;
        }

        public Criteria andPhoneLessThan(String value) {
            addCriterion("phone <", value, "phone");
            return (Criteria) this;
        }

        public Criteria andPhoneLessThanOrEqualTo(String value) {
            addCriterion("phone <=", value, "phone");
            return (Criteria) this;
        }

        public Criteria andPhoneLike(String value) {
            addCriterion("phone like", value, "phone");
            return (Criteria) this;
        }

        public Criteria andPhoneNotLike(String value) {
            addCriterion("phone not like", value, "phone");
            return (Criteria) this;
        }

        public Criteria andPhoneIn(List<String> values) {
            addCriterion("phone in", values, "phone");
            return (Criteria) this;
        }

        public Criteria andPhoneNotIn(List<String> values) {
            addCriterion("phone not in", values, "phone");
            return (Criteria) this;
        }

        public Criteria andPhoneBetween(String value1, String value2) {
            addCriterion("phone between", value1, value2, "phone");
            return (Criteria) this;
        }

        public Criteria andPhoneNotBetween(String value1, String value2) {
            addCriterion("phone not between", value1, value2, "phone");
            return (Criteria) this;
        }

        public Criteria andStatusIsNull() {
            addCriterion("status is null");
            return (Criteria) this;
        }

        public Criteria andStatusIsNotNull() {
            addCriterion("status is not null");
            return (Criteria) this;
        }

        public Criteria andStatusEqualTo(Integer value) {
            addCriterion("status =", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotEqualTo(Integer value) {
            addCriterion("status <>", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusGreaterThan(Integer value) {
            addCriterion("status >", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusGreaterThanOrEqualTo(Integer value) {
            addCriterion("status >=", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusLessThan(Integer value) {
            addCriterion("status <", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusLessThanOrEqualTo(Integer value) {
            addCriterion("status <=", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusIn(List<Integer> values) {
            addCriterion("status in", values, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotIn(List<Integer> values) {
            addCriterion("status not in", values, "status");
            return (Criteria) this;
        }

        public Criteria andStatusBetween(Integer value1, Integer value2) {
            addCriterion("status between", value1, value2, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotBetween(Integer value1, Integer value2) {
            addCriterion("status not between", value1, value2, "status");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIsNull() {
            addCriterion("create_time is null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIsNotNull() {
            addCriterion("create_time is not null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeEqualTo(Date value) {
            addCriterion("create_time =", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotEqualTo(Date value) {
            addCriterion("create_time <>", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThan(Date value) {
            addCriterion("create_time >", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("create_time >=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThan(Date value) {
            addCriterion("create_time <", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThanOrEqualTo(Date value) {
            addCriterion("create_time <=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIn(List<Date> values) {
            addCriterion("create_time in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotIn(List<Date> values) {
            addCriterion("create_time not in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeBetween(Date value1, Date value2) {
            addCriterion("create_time between", value1, value2, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotBetween(Date value1, Date value2) {
            addCriterion("create_time not between", value1, value2, "createTime");
            return (Criteria) this;
        }

        public Criteria andIconIsNull() {
            addCriterion("icon is null");
            return (Criteria) this;
        }

        public Criteria andIconIsNotNull() {
            addCriterion("icon is not null");
            return (Criteria) this;
        }

        public Criteria andIconEqualTo(String value) {
            addCriterion("icon =", value, "icon");
            return (Criteria) this;
        }

        public Criteria andIconNotEqualTo(String value) {
            addCriterion("icon <>", value, "icon");
            return (Criteria) this;
        }

        public Criteria andIconGreaterThan(String value) {
            addCriterion("icon >", value, "icon");
            return (Criteria) this;
        }

        public Criteria andIconGreaterThanOrEqualTo(String value) {
            addCriterion("icon >=", value, "icon");
            return (Criteria) this;
        }

        public Criteria andIconLessThan(String value) {
            addCriterion("icon <", value, "icon");
            return (Criteria) this;
        }

        public Criteria andIconLessThanOrEqualTo(String value) {
            addCriterion("icon <=", value, "icon");
            return (Criteria) this;
        }

        public Criteria andIconLike(String value) {
            addCriterion("icon like", value, "icon");
            return (Criteria) this;
        }

        public Criteria andIconNotLike(String value) {
            addCriterion("icon not like", value, "icon");
            return (Criteria) this;
        }

        public Criteria andIconIn(List<String> values) {
            addCriterion("icon in", values, "icon");
            return (Criteria) this;
        }

        public Criteria andIconNotIn(List<String> values) {
            addCriterion("icon not in", values, "icon");
            return (Criteria) this;
        }

        public Criteria andIconBetween(String value1, String value2) {
            addCriterion("icon between", value1, value2, "icon");
            return (Criteria) this;
        }

        public Criteria andIconNotBetween(String value1, String value2) {
            addCriterion("icon not between", value1, value2, "icon");
            return (Criteria) this;
        }

        public Criteria andGenderIsNull() {
            addCriterion("gender is null");
            return (Criteria) this;
        }

        public Criteria andGenderIsNotNull() {
            addCriterion("gender is not null");
            return (Criteria) this;
        }

        public Criteria andGenderEqualTo(Integer value) {
            addCriterion("gender =", value, "gender");
            return (Criteria) this;
        }

        public Criteria andGenderNotEqualTo(Integer value) {
            addCriterion("gender <>", value, "gender");
            return (Criteria) this;
        }

        public Criteria andGenderGreaterThan(Integer value) {
            addCriterion("gender >", value, "gender");
            return (Criteria) this;
        }

        public Criteria andGenderGreaterThanOrEqualTo(Integer value) {
            addCriterion("gender >=", value, "gender");
            return (Criteria) this;
        }

        public Criteria andGenderLessThan(Integer value) {
            addCriterion("gender <", value, "gender");
            return (Criteria) this;
        }

        public Criteria andGenderLessThanOrEqualTo(Integer value) {
            addCriterion("gender <=", value, "gender");
            return (Criteria) this;
        }

        public Criteria andGenderIn(List<Integer> values) {
            addCriterion("gender in", values, "gender");
            return (Criteria) this;
        }

        public Criteria andGenderNotIn(List<Integer> values) {
            addCriterion("gender not in", values, "gender");
            return (Criteria) this;
        }

        public Criteria andGenderBetween(Integer value1, Integer value2) {
            addCriterion("gender between", value1, value2, "gender");
            return (Criteria) this;
        }

        public Criteria andGenderNotBetween(Integer value1, Integer value2) {
            addCriterion("gender not between", value1, value2, "gender");
            return (Criteria) this;
        }

        public Criteria andBirthdayIsNull() {
            addCriterion("birthday is null");
            return (Criteria) this;
        }

        public Criteria andBirthdayIsNotNull() {
            addCriterion("birthday is not null");
            return (Criteria) this;
        }

        public Criteria andBirthdayEqualTo(Date value) {
            addCriterionForJDBCDate("birthday =", value, "birthday");
            return (Criteria) this;
        }

        public Criteria andBirthdayNotEqualTo(Date value) {
            addCriterionForJDBCDate("birthday <>", value, "birthday");
            return (Criteria) this;
        }

        public Criteria andBirthdayGreaterThan(Date value) {
            addCriterionForJDBCDate("birthday >", value, "birthday");
            return (Criteria) this;
        }

        public Criteria andBirthdayGreaterThanOrEqualTo(Date value) {
            addCriterionForJDBCDate("birthday >=", value, "birthday");
            return (Criteria) this;
        }

        public Criteria andBirthdayLessThan(Date value) {
            addCriterionForJDBCDate("birthday <", value, "birthday");
            return (Criteria) this;
        }

        public Criteria andBirthdayLessThanOrEqualTo(Date value) {
            addCriterionForJDBCDate("birthday <=", value, "birthday");
            return (Criteria) this;
        }

        public Criteria andBirthdayIn(List<Date> values) {
            addCriterionForJDBCDate("birthday in", values, "birthday");
            return (Criteria) this;
        }

        public Criteria andBirthdayNotIn(List<Date> values) {
            addCriterionForJDBCDate("birthday not in", values, "birthday");
            return (Criteria) this;
        }

        public Criteria andBirthdayBetween(Date value1, Date value2) {
            addCriterionForJDBCDate("birthday between", value1, value2, "birthday");
            return (Criteria) this;
        }

        public Criteria andBirthdayNotBetween(Date value1, Date value2) {
            addCriterionForJDBCDate("birthday not between", value1, value2, "birthday");
            return (Criteria) this;
        }

        public Criteria andCityIsNull() {
            addCriterion("city is null");
            return (Criteria) this;
        }

        public Criteria andCityIsNotNull() {
            addCriterion("city is not null");
            return (Criteria) this;
        }

        public Criteria andCityEqualTo(String value) {
            addCriterion("city =", value, "city");
            return (Criteria) this;
        }

        public Criteria andCityNotEqualTo(String value) {
            addCriterion("city <>", value, "city");
            return (Criteria) this;
        }

        public Criteria andCityGreaterThan(String value) {
            addCriterion("city >", value, "city");
            return (Criteria) this;
        }

        public Criteria andCityGreaterThanOrEqualTo(String value) {
            addCriterion("city >=", value, "city");
            return (Criteria) this;
        }

        public Criteria andCityLessThan(String value) {
            addCriterion("city <", value, "city");
            return (Criteria) this;
        }

        public Criteria andCityLessThanOrEqualTo(String value) {
            addCriterion("city <=", value, "city");
            return (Criteria) this;
        }

        public Criteria andCityLike(String value) {
            addCriterion("city like", value, "city");
            return (Criteria) this;
        }

        public Criteria andCityNotLike(String value) {
            addCriterion("city not like", value, "city");
            return (Criteria) this;
        }

        public Criteria andCityIn(List<String> values) {
            addCriterion("city in", values, "city");
            return (Criteria) this;
        }

        public Criteria andCityNotIn(List<String> values) {
            addCriterion("city not in", values, "city");
            return (Criteria) this;
        }

        public Criteria andCityBetween(String value1, String value2) {
            addCriterion("city between", value1, value2, "city");
            return (Criteria) this;
        }

        public Criteria andCityNotBetween(String value1, String value2) {
            addCriterion("city not between", value1, value2, "city");
            return (Criteria) this;
        }

        public Criteria andJobIsNull() {
            addCriterion("job is null");
            return (Criteria) this;
        }

        public Criteria andJobIsNotNull() {
            addCriterion("job is not null");
            return (Criteria) this;
        }

        public Criteria andJobEqualTo(String value) {
            addCriterion("job =", value, "job");
            return (Criteria) this;
        }

        public Criteria andJobNotEqualTo(String value) {
            addCriterion("job <>", value, "job");
            return (Criteria) this;
        }

        public Criteria andJobGreaterThan(String value) {
            addCriterion("job >", value, "job");
            return (Criteria) this;
        }

        public Criteria andJobGreaterThanOrEqualTo(String value) {
            addCriterion("job >=", value, "job");
            return (Criteria) this;
        }

        public Criteria andJobLessThan(String value) {
            addCriterion("job <", value, "job");
            return (Criteria) this;
        }

        public Criteria andJobLessThanOrEqualTo(String value) {
            addCriterion("job <=", value, "job");
            return (Criteria) this;
        }

        public Criteria andJobLike(String value) {
            addCriterion("job like", value, "job");
            return (Criteria) this;
        }

        public Criteria andJobNotLike(String value) {
            addCriterion("job not like", value, "job");
            return (Criteria) this;
        }

        public Criteria andJobIn(List<String> values) {
            addCriterion("job in", values, "job");
            return (Criteria) this;
        }

        public Criteria andJobNotIn(List<String> values) {
            addCriterion("job not in", values, "job");
            return (Criteria) this;
        }

        public Criteria andJobBetween(String value1, String value2) {
            addCriterion("job between", value1, value2, "job");
            return (Criteria) this;
        }

        public Criteria andJobNotBetween(String value1, String value2) {
            addCriterion("job not between", value1, value2, "job");
            return (Criteria) this;
        }

        public Criteria andPersonalizedSignatureIsNull() {
            addCriterion("personalized_signature is null");
            return (Criteria) this;
        }

        public Criteria andPersonalizedSignatureIsNotNull() {
            addCriterion("personalized_signature is not null");
            return (Criteria) this;
        }

        public Criteria andPersonalizedSignatureEqualTo(String value) {
            addCriterion("personalized_signature =", value, "personalizedSignature");
            return (Criteria) this;
        }

        public Criteria andPersonalizedSignatureNotEqualTo(String value) {
            addCriterion("personalized_signature <>", value, "personalizedSignature");
            return (Criteria) this;
        }

        public Criteria andPersonalizedSignatureGreaterThan(String value) {
            addCriterion("personalized_signature >", value, "personalizedSignature");
            return (Criteria) this;
        }

        public Criteria andPersonalizedSignatureGreaterThanOrEqualTo(String value) {
            addCriterion("personalized_signature >=", value, "personalizedSignature");
            return (Criteria) this;
        }

        public Criteria andPersonalizedSignatureLessThan(String value) {
            addCriterion("personalized_signature <", value, "personalizedSignature");
            return (Criteria) this;
        }

        public Criteria andPersonalizedSignatureLessThanOrEqualTo(String value) {
            addCriterion("personalized_signature <=", value, "personalizedSignature");
            return (Criteria) this;
        }

        public Criteria andPersonalizedSignatureLike(String value) {
            addCriterion("personalized_signature like", value, "personalizedSignature");
            return (Criteria) this;
        }

        public Criteria andPersonalizedSignatureNotLike(String value) {
            addCriterion("personalized_signature not like", value, "personalizedSignature");
            return (Criteria) this;
        }

        public Criteria andPersonalizedSignatureIn(List<String> values) {
            addCriterion("personalized_signature in", values, "personalizedSignature");
            return (Criteria) this;
        }

        public Criteria andPersonalizedSignatureNotIn(List<String> values) {
            addCriterion("personalized_signature not in", values, "personalizedSignature");
            return (Criteria) this;
        }

        public Criteria andPersonalizedSignatureBetween(String value1, String value2) {
            addCriterion("personalized_signature between", value1, value2, "personalizedSignature");
            return (Criteria) this;
        }

        public Criteria andPersonalizedSignatureNotBetween(String value1, String value2) {
            addCriterion("personalized_signature not between", value1, value2, "personalizedSignature");
            return (Criteria) this;
        }

        public Criteria andSourceTypeIsNull() {
            addCriterion("source_type is null");
            return (Criteria) this;
        }

        public Criteria andSourceTypeIsNotNull() {
            addCriterion("source_type is not null");
            return (Criteria) this;
        }

        public Criteria andSourceTypeEqualTo(Integer value) {
            addCriterion("source_type =", value, "sourceType");
            return (Criteria) this;
        }

        public Criteria andSourceTypeNotEqualTo(Integer value) {
            addCriterion("source_type <>", value, "sourceType");
            return (Criteria) this;
        }

        public Criteria andSourceTypeGreaterThan(Integer value) {
            addCriterion("source_type >", value, "sourceType");
            return (Criteria) this;
        }

        public Criteria andSourceTypeGreaterThanOrEqualTo(Integer value) {
            addCriterion("source_type >=", value, "sourceType");
            return (Criteria) this;
        }

        public Criteria andSourceTypeLessThan(Integer value) {
            addCriterion("source_type <", value, "sourceType");
            return (Criteria) this;
        }

        public Criteria andSourceTypeLessThanOrEqualTo(Integer value) {
            addCriterion("source_type <=", value, "sourceType");
            return (Criteria) this;
        }

        public Criteria andSourceTypeIn(List<Integer> values) {
            addCriterion("source_type in", values, "sourceType");
            return (Criteria) this;
        }

        public Criteria andSourceTypeNotIn(List<Integer> values) {
            addCriterion("source_type not in", values, "sourceType");
            return (Criteria) this;
        }

        public Criteria andSourceTypeBetween(Integer value1, Integer value2) {
            addCriterion("source_type between", value1, value2, "sourceType");
            return (Criteria) this;
        }

        public Criteria andSourceTypeNotBetween(Integer value1, Integer value2) {
            addCriterion("source_type not between", value1, value2, "sourceType");
            return (Criteria) this;
        }

        public Criteria andIntegrationIsNull() {
            addCriterion("integration is null");
            return (Criteria) this;
        }

        public Criteria andIntegrationIsNotNull() {
            addCriterion("integration is not null");
            return (Criteria) this;
        }

        public Criteria andIntegrationEqualTo(Integer value) {
            addCriterion("integration =", value, "integration");
            return (Criteria) this;
        }

        public Criteria andIntegrationNotEqualTo(Integer value) {
            addCriterion("integration <>", value, "integration");
            return (Criteria) this;
        }

        public Criteria andIntegrationGreaterThan(Integer value) {
            addCriterion("integration >", value, "integration");
            return (Criteria) this;
        }

        public Criteria andIntegrationGreaterThanOrEqualTo(Integer value) {
            addCriterion("integration >=", value, "integration");
            return (Criteria) this;
        }

        public Criteria andIntegrationLessThan(Integer value) {
            addCriterion("integration <", value, "integration");
            return (Criteria) this;
        }

        public Criteria andIntegrationLessThanOrEqualTo(Integer value) {
            addCriterion("integration <=", value, "integration");
            return (Criteria) this;
        }

        public Criteria andIntegrationIn(List<Integer> values) {
            addCriterion("integration in", values, "integration");
            return (Criteria) this;
        }

        public Criteria andIntegrationNotIn(List<Integer> values) {
            addCriterion("integration not in", values, "integration");
            return (Criteria) this;
        }

        public Criteria andIntegrationBetween(Integer value1, Integer value2) {
            addCriterion("integration between", value1, value2, "integration");
            return (Criteria) this;
        }

        public Criteria andIntegrationNotBetween(Integer value1, Integer value2) {
            addCriterion("integration not between", value1, value2, "integration");
            return (Criteria) this;
        }

        public Criteria andGrowthIsNull() {
            addCriterion("growth is null");
            return (Criteria) this;
        }

        public Criteria andGrowthIsNotNull() {
            addCriterion("growth is not null");
            return (Criteria) this;
        }

        public Criteria andGrowthEqualTo(Integer value) {
            addCriterion("growth =", value, "growth");
            return (Criteria) this;
        }

        public Criteria andGrowthNotEqualTo(Integer value) {
            addCriterion("growth <>", value, "growth");
            return (Criteria) this;
        }

        public Criteria andGrowthGreaterThan(Integer value) {
            addCriterion("growth >", value, "growth");
            return (Criteria) this;
        }

        public Criteria andGrowthGreaterThanOrEqualTo(Integer value) {
            addCriterion("growth >=", value, "growth");
            return (Criteria) this;
        }

        public Criteria andGrowthLessThan(Integer value) {
            addCriterion("growth <", value, "growth");
            return (Criteria) this;
        }

        public Criteria andGrowthLessThanOrEqualTo(Integer value) {
            addCriterion("growth <=", value, "growth");
            return (Criteria) this;
        }

        public Criteria andGrowthIn(List<Integer> values) {
            addCriterion("growth in", values, "growth");
            return (Criteria) this;
        }

        public Criteria andGrowthNotIn(List<Integer> values) {
            addCriterion("growth not in", values, "growth");
            return (Criteria) this;
        }

        public Criteria andGrowthBetween(Integer value1, Integer value2) {
            addCriterion("growth between", value1, value2, "growth");
            return (Criteria) this;
        }

        public Criteria andGrowthNotBetween(Integer value1, Integer value2) {
            addCriterion("growth not between", value1, value2, "growth");
            return (Criteria) this;
        }

        public Criteria andLuckeyCountIsNull() {
            addCriterion("luckey_count is null");
            return (Criteria) this;
        }

        public Criteria andLuckeyCountIsNotNull() {
            addCriterion("luckey_count is not null");
            return (Criteria) this;
        }

        public Criteria andLuckeyCountEqualTo(Integer value) {
            addCriterion("luckey_count =", value, "luckeyCount");
            return (Criteria) this;
        }

        public Criteria andLuckeyCountNotEqualTo(Integer value) {
            addCriterion("luckey_count <>", value, "luckeyCount");
            return (Criteria) this;
        }

        public Criteria andLuckeyCountGreaterThan(Integer value) {
            addCriterion("luckey_count >", value, "luckeyCount");
            return (Criteria) this;
        }

        public Criteria andLuckeyCountGreaterThanOrEqualTo(Integer value) {
            addCriterion("luckey_count >=", value, "luckeyCount");
            return (Criteria) this;
        }

        public Criteria andLuckeyCountLessThan(Integer value) {
            addCriterion("luckey_count <", value, "luckeyCount");
            return (Criteria) this;
        }

        public Criteria andLuckeyCountLessThanOrEqualTo(Integer value) {
            addCriterion("luckey_count <=", value, "luckeyCount");
            return (Criteria) this;
        }

        public Criteria andLuckeyCountIn(List<Integer> values) {
            addCriterion("luckey_count in", values, "luckeyCount");
            return (Criteria) this;
        }

        public Criteria andLuckeyCountNotIn(List<Integer> values) {
            addCriterion("luckey_count not in", values, "luckeyCount");
            return (Criteria) this;
        }

        public Criteria andLuckeyCountBetween(Integer value1, Integer value2) {
            addCriterion("luckey_count between", value1, value2, "luckeyCount");
            return (Criteria) this;
        }

        public Criteria andLuckeyCountNotBetween(Integer value1, Integer value2) {
            addCriterion("luckey_count not between", value1, value2, "luckeyCount");
            return (Criteria) this;
        }

        public Criteria andHistoryIntegrationIsNull() {
            addCriterion("history_integration is null");
            return (Criteria) this;
        }

        public Criteria andHistoryIntegrationIsNotNull() {
            addCriterion("history_integration is not null");
            return (Criteria) this;
        }

        public Criteria andHistoryIntegrationEqualTo(Integer value) {
            addCriterion("history_integration =", value, "historyIntegration");
            return (Criteria) this;
        }

        public Criteria andHistoryIntegrationNotEqualTo(Integer value) {
            addCriterion("history_integration <>", value, "historyIntegration");
            return (Criteria) this;
        }

        public Criteria andHistoryIntegrationGreaterThan(Integer value) {
            addCriterion("history_integration >", value, "historyIntegration");
            return (Criteria) this;
        }

        public Criteria andHistoryIntegrationGreaterThanOrEqualTo(Integer value) {
            addCriterion("history_integration >=", value, "historyIntegration");
            return (Criteria) this;
        }

        public Criteria andHistoryIntegrationLessThan(Integer value) {
            addCriterion("history_integration <", value, "historyIntegration");
            return (Criteria) this;
        }

        public Criteria andHistoryIntegrationLessThanOrEqualTo(Integer value) {
            addCriterion("history_integration <=", value, "historyIntegration");
            return (Criteria) this;
        }

        public Criteria andHistoryIntegrationIn(List<Integer> values) {
            addCriterion("history_integration in", values, "historyIntegration");
            return (Criteria) this;
        }

        public Criteria andHistoryIntegrationNotIn(List<Integer> values) {
            addCriterion("history_integration not in", values, "historyIntegration");
            return (Criteria) this;
        }

        public Criteria andHistoryIntegrationBetween(Integer value1, Integer value2) {
            addCriterion("history_integration between", value1, value2, "historyIntegration");
            return (Criteria) this;
        }

        public Criteria andHistoryIntegrationNotBetween(Integer value1, Integer value2) {
            addCriterion("history_integration not between", value1, value2, "historyIntegration");
            return (Criteria) this;
        }

        public Criteria andOrganizationnameIsNull() {
            addCriterion("organizationname is null");
            return (Criteria) this;
        }

        public Criteria andOrganizationnameIsNotNull() {
            addCriterion("organizationname is not null");
            return (Criteria) this;
        }

        public Criteria andOrganizationnameEqualTo(String value) {
            addCriterion("organizationname =", value, "organizationname");
            return (Criteria) this;
        }

        public Criteria andOrganizationnameNotEqualTo(String value) {
            addCriterion("organizationname <>", value, "organizationname");
            return (Criteria) this;
        }

        public Criteria andOrganizationnameGreaterThan(String value) {
            addCriterion("organizationname >", value, "organizationname");
            return (Criteria) this;
        }

        public Criteria andOrganizationnameGreaterThanOrEqualTo(String value) {
            addCriterion("organizationname >=", value, "organizationname");
            return (Criteria) this;
        }

        public Criteria andOrganizationnameLessThan(String value) {
            addCriterion("organizationname <", value, "organizationname");
            return (Criteria) this;
        }

        public Criteria andOrganizationnameLessThanOrEqualTo(String value) {
            addCriterion("organizationname <=", value, "organizationname");
            return (Criteria) this;
        }

        public Criteria andOrganizationnameLike(String value) {
            addCriterion("organizationname like", value, "organizationname");
            return (Criteria) this;
        }

        public Criteria andOrganizationnameNotLike(String value) {
            addCriterion("organizationname not like", value, "organizationname");
            return (Criteria) this;
        }

        public Criteria andOrganizationnameIn(List<String> values) {
            addCriterion("organizationname in", values, "organizationname");
            return (Criteria) this;
        }

        public Criteria andOrganizationnameNotIn(List<String> values) {
            addCriterion("organizationname not in", values, "organizationname");
            return (Criteria) this;
        }

        public Criteria andOrganizationnameBetween(String value1, String value2) {
            addCriterion("organizationname between", value1, value2, "organizationname");
            return (Criteria) this;
        }

        public Criteria andOrganizationnameNotBetween(String value1, String value2) {
            addCriterion("organizationname not between", value1, value2, "organizationname");
            return (Criteria) this;
        }

        public Criteria andMonthlyOrdersIsNull() {
            addCriterion("monthly_orders is null");
            return (Criteria) this;
        }

        public Criteria andMonthlyOrdersIsNotNull() {
            addCriterion("monthly_orders is not null");
            return (Criteria) this;
        }

        public Criteria andMonthlyOrdersEqualTo(String value) {
            addCriterion("monthly_orders =", value, "monthlyOrders");
            return (Criteria) this;
        }

        public Criteria andMonthlyOrdersNotEqualTo(String value) {
            addCriterion("monthly_orders <>", value, "monthlyOrders");
            return (Criteria) this;
        }

        public Criteria andMonthlyOrdersGreaterThan(String value) {
            addCriterion("monthly_orders >", value, "monthlyOrders");
            return (Criteria) this;
        }

        public Criteria andMonthlyOrdersGreaterThanOrEqualTo(String value) {
            addCriterion("monthly_orders >=", value, "monthlyOrders");
            return (Criteria) this;
        }

        public Criteria andMonthlyOrdersLessThan(String value) {
            addCriterion("monthly_orders <", value, "monthlyOrders");
            return (Criteria) this;
        }

        public Criteria andMonthlyOrdersLessThanOrEqualTo(String value) {
            addCriterion("monthly_orders <=", value, "monthlyOrders");
            return (Criteria) this;
        }

        public Criteria andMonthlyOrdersLike(String value) {
            addCriterion("monthly_orders like", value, "monthlyOrders");
            return (Criteria) this;
        }

        public Criteria andMonthlyOrdersNotLike(String value) {
            addCriterion("monthly_orders not like", value, "monthlyOrders");
            return (Criteria) this;
        }

        public Criteria andMonthlyOrdersIn(List<String> values) {
            addCriterion("monthly_orders in", values, "monthlyOrders");
            return (Criteria) this;
        }

        public Criteria andMonthlyOrdersNotIn(List<String> values) {
            addCriterion("monthly_orders not in", values, "monthlyOrders");
            return (Criteria) this;
        }

        public Criteria andMonthlyOrdersBetween(String value1, String value2) {
            addCriterion("monthly_orders between", value1, value2, "monthlyOrders");
            return (Criteria) this;
        }

        public Criteria andMonthlyOrdersNotBetween(String value1, String value2) {
            addCriterion("monthly_orders not between", value1, value2, "monthlyOrders");
            return (Criteria) this;
        }

        public Criteria andLoginTypeIsNull() {
            addCriterion("login_type is null");
            return (Criteria) this;
        }

        public Criteria andLoginTypeIsNotNull() {
            addCriterion("login_type is not null");
            return (Criteria) this;
        }

        public Criteria andLoginTypeEqualTo(Integer value) {
            addCriterion("login_type =", value, "loginType");
            return (Criteria) this;
        }

        public Criteria andLoginTypeNotEqualTo(Integer value) {
            addCriterion("login_type <>", value, "loginType");
            return (Criteria) this;
        }

        public Criteria andLoginTypeGreaterThan(Integer value) {
            addCriterion("login_type >", value, "loginType");
            return (Criteria) this;
        }

        public Criteria andLoginTypeGreaterThanOrEqualTo(Integer value) {
            addCriterion("login_type >=", value, "loginType");
            return (Criteria) this;
        }

        public Criteria andLoginTypeLessThan(Integer value) {
            addCriterion("login_type <", value, "loginType");
            return (Criteria) this;
        }

        public Criteria andLoginTypeLessThanOrEqualTo(Integer value) {
            addCriterion("login_type <=", value, "loginType");
            return (Criteria) this;
        }

        public Criteria andLoginTypeIn(List<Integer> values) {
            addCriterion("login_type in", values, "loginType");
            return (Criteria) this;
        }

        public Criteria andLoginTypeNotIn(List<Integer> values) {
            addCriterion("login_type not in", values, "loginType");
            return (Criteria) this;
        }

        public Criteria andLoginTypeBetween(Integer value1, Integer value2) {
            addCriterion("login_type between", value1, value2, "loginType");
            return (Criteria) this;
        }

        public Criteria andLoginTypeNotBetween(Integer value1, Integer value2) {
            addCriterion("login_type not between", value1, value2, "loginType");
            return (Criteria) this;
        }

        public Criteria andBalanceIsNull() {
            addCriterion("balance is null");
            return (Criteria) this;
        }

        public Criteria andBalanceIsNotNull() {
            addCriterion("balance is not null");
            return (Criteria) this;
        }

        public Criteria andBalanceEqualTo(Double value) {
            addCriterion("balance =", value, "balance");
            return (Criteria) this;
        }

        public Criteria andBalanceNotEqualTo(Double value) {
            addCriterion("balance <>", value, "balance");
            return (Criteria) this;
        }

        public Criteria andBalanceGreaterThan(Double value) {
            addCriterion("balance >", value, "balance");
            return (Criteria) this;
        }

        public Criteria andBalanceGreaterThanOrEqualTo(Double value) {
            addCriterion("balance >=", value, "balance");
            return (Criteria) this;
        }

        public Criteria andBalanceLessThan(Double value) {
            addCriterion("balance <", value, "balance");
            return (Criteria) this;
        }

        public Criteria andBalanceLessThanOrEqualTo(Double value) {
            addCriterion("balance <=", value, "balance");
            return (Criteria) this;
        }

        public Criteria andBalanceIn(List<Double> values) {
            addCriterion("balance in", values, "balance");
            return (Criteria) this;
        }

        public Criteria andBalanceNotIn(List<Double> values) {
            addCriterion("balance not in", values, "balance");
            return (Criteria) this;
        }

        public Criteria andBalanceBetween(Double value1, Double value2) {
            addCriterion("balance between", value1, value2, "balance");
            return (Criteria) this;
        }

        public Criteria andBalanceNotBetween(Double value1, Double value2) {
            addCriterion("balance not between", value1, value2, "balance");
            return (Criteria) this;
        }

        public Criteria andShopifyNameIsNull() {
            addCriterion("shopify_name is null");
            return (Criteria) this;
        }

        public Criteria andShopifyNameIsNotNull() {
            addCriterion("shopify_name is not null");
            return (Criteria) this;
        }

        public Criteria andShopifyNameEqualTo(String value) {
            addCriterion("shopify_name =", value, "shopifyName");
            return (Criteria) this;
        }

        public Criteria andShopifyNameNotEqualTo(String value) {
            addCriterion("shopify_name <>", value, "shopifyName");
            return (Criteria) this;
        }

        public Criteria andShopifyNameGreaterThan(String value) {
            addCriterion("shopify_name >", value, "shopifyName");
            return (Criteria) this;
        }

        public Criteria andShopifyNameGreaterThanOrEqualTo(String value) {
            addCriterion("shopify_name >=", value, "shopifyName");
            return (Criteria) this;
        }

        public Criteria andShopifyNameLessThan(String value) {
            addCriterion("shopify_name <", value, "shopifyName");
            return (Criteria) this;
        }

        public Criteria andShopifyNameLessThanOrEqualTo(String value) {
            addCriterion("shopify_name <=", value, "shopifyName");
            return (Criteria) this;
        }

        public Criteria andShopifyNameLike(String value) {
            addCriterion("shopify_name like", value, "shopifyName");
            return (Criteria) this;
        }

        public Criteria andShopifyNameNotLike(String value) {
            addCriterion("shopify_name not like", value, "shopifyName");
            return (Criteria) this;
        }

        public Criteria andShopifyNameIn(List<String> values) {
            addCriterion("shopify_name in", values, "shopifyName");
            return (Criteria) this;
        }

        public Criteria andShopifyNameNotIn(List<String> values) {
            addCriterion("shopify_name not in", values, "shopifyName");
            return (Criteria) this;
        }

        public Criteria andShopifyNameBetween(String value1, String value2) {
            addCriterion("shopify_name between", value1, value2, "shopifyName");
            return (Criteria) this;
        }

        public Criteria andShopifyNameNotBetween(String value1, String value2) {
            addCriterion("shopify_name not between", value1, value2, "shopifyName");
            return (Criteria) this;
        }

        public Criteria andShopifyFlagIsNull() {
            addCriterion("shopify_flag is null");
            return (Criteria) this;
        }

        public Criteria andShopifyFlagIsNotNull() {
            addCriterion("shopify_flag is not null");
            return (Criteria) this;
        }

        public Criteria andShopifyFlagEqualTo(Integer value) {
            addCriterion("shopify_flag =", value, "shopifyFlag");
            return (Criteria) this;
        }

        public Criteria andShopifyFlagNotEqualTo(Integer value) {
            addCriterion("shopify_flag <>", value, "shopifyFlag");
            return (Criteria) this;
        }

        public Criteria andShopifyFlagGreaterThan(Integer value) {
            addCriterion("shopify_flag >", value, "shopifyFlag");
            return (Criteria) this;
        }

        public Criteria andShopifyFlagGreaterThanOrEqualTo(Integer value) {
            addCriterion("shopify_flag >=", value, "shopifyFlag");
            return (Criteria) this;
        }

        public Criteria andShopifyFlagLessThan(Integer value) {
            addCriterion("shopify_flag <", value, "shopifyFlag");
            return (Criteria) this;
        }

        public Criteria andShopifyFlagLessThanOrEqualTo(Integer value) {
            addCriterion("shopify_flag <=", value, "shopifyFlag");
            return (Criteria) this;
        }

        public Criteria andShopifyFlagIn(List<Integer> values) {
            addCriterion("shopify_flag in", values, "shopifyFlag");
            return (Criteria) this;
        }

        public Criteria andShopifyFlagNotIn(List<Integer> values) {
            addCriterion("shopify_flag not in", values, "shopifyFlag");
            return (Criteria) this;
        }

        public Criteria andShopifyFlagBetween(Integer value1, Integer value2) {
            addCriterion("shopify_flag between", value1, value2, "shopifyFlag");
            return (Criteria) this;
        }

        public Criteria andShopifyFlagNotBetween(Integer value1, Integer value2) {
            addCriterion("shopify_flag not between", value1, value2, "shopifyFlag");
            return (Criteria) this;
        }

        public Criteria andGuidedFlagIsNull() {
            addCriterion("guided_flag is null");
            return (Criteria) this;
        }

        public Criteria andGuidedFlagIsNotNull() {
            addCriterion("guided_flag is not null");
            return (Criteria) this;
        }

        public Criteria andGuidedFlagEqualTo(Integer value) {
            addCriterion("guided_flag =", value, "guidedFlag");
            return (Criteria) this;
        }

        public Criteria andGuidedFlagNotEqualTo(Integer value) {
            addCriterion("guided_flag <>", value, "guidedFlag");
            return (Criteria) this;
        }

        public Criteria andGuidedFlagGreaterThan(Integer value) {
            addCriterion("guided_flag >", value, "guidedFlag");
            return (Criteria) this;
        }

        public Criteria andGuidedFlagGreaterThanOrEqualTo(Integer value) {
            addCriterion("guided_flag >=", value, "guidedFlag");
            return (Criteria) this;
        }

        public Criteria andGuidedFlagLessThan(Integer value) {
            addCriterion("guided_flag <", value, "guidedFlag");
            return (Criteria) this;
        }

        public Criteria andGuidedFlagLessThanOrEqualTo(Integer value) {
            addCriterion("guided_flag <=", value, "guidedFlag");
            return (Criteria) this;
        }

        public Criteria andGuidedFlagIn(List<Integer> values) {
            addCriterion("guided_flag in", values, "guidedFlag");
            return (Criteria) this;
        }

        public Criteria andGuidedFlagNotIn(List<Integer> values) {
            addCriterion("guided_flag not in", values, "guidedFlag");
            return (Criteria) this;
        }

        public Criteria andGuidedFlagBetween(Integer value1, Integer value2) {
            addCriterion("guided_flag between", value1, value2, "guidedFlag");
            return (Criteria) this;
        }

        public Criteria andGuidedFlagNotBetween(Integer value1, Integer value2) {
            addCriterion("guided_flag not between", value1, value2, "guidedFlag");
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

        public Criteria andSourcingTypeOfShippingIsNull() {
            addCriterion("sourcing_type_of_shipping is null");
            return (Criteria) this;
        }

        public Criteria andSourcingTypeOfShippingIsNotNull() {
            addCriterion("sourcing_type_of_shipping is not null");
            return (Criteria) this;
        }

        public Criteria andSourcingTypeOfShippingEqualTo(Integer value) {
            addCriterion("sourcing_type_of_shipping =", value, "sourcingTypeOfShipping");
            return (Criteria) this;
        }

        public Criteria andSourcingTypeOfShippingNotEqualTo(Integer value) {
            addCriterion("sourcing_type_of_shipping <>", value, "sourcingTypeOfShipping");
            return (Criteria) this;
        }

        public Criteria andSourcingTypeOfShippingGreaterThan(Integer value) {
            addCriterion("sourcing_type_of_shipping >", value, "sourcingTypeOfShipping");
            return (Criteria) this;
        }

        public Criteria andSourcingTypeOfShippingGreaterThanOrEqualTo(Integer value) {
            addCriterion("sourcing_type_of_shipping >=", value, "sourcingTypeOfShipping");
            return (Criteria) this;
        }

        public Criteria andSourcingTypeOfShippingLessThan(Integer value) {
            addCriterion("sourcing_type_of_shipping <", value, "sourcingTypeOfShipping");
            return (Criteria) this;
        }

        public Criteria andSourcingTypeOfShippingLessThanOrEqualTo(Integer value) {
            addCriterion("sourcing_type_of_shipping <=", value, "sourcingTypeOfShipping");
            return (Criteria) this;
        }

        public Criteria andSourcingTypeOfShippingIn(List<Integer> values) {
            addCriterion("sourcing_type_of_shipping in", values, "sourcingTypeOfShipping");
            return (Criteria) this;
        }

        public Criteria andSourcingTypeOfShippingNotIn(List<Integer> values) {
            addCriterion("sourcing_type_of_shipping not in", values, "sourcingTypeOfShipping");
            return (Criteria) this;
        }

        public Criteria andSourcingTypeOfShippingBetween(Integer value1, Integer value2) {
            addCriterion("sourcing_type_of_shipping between", value1, value2, "sourcingTypeOfShipping");
            return (Criteria) this;
        }

        public Criteria andSourcingTypeOfShippingNotBetween(Integer value1, Integer value2) {
            addCriterion("sourcing_type_of_shipping not between", value1, value2, "sourcingTypeOfShipping");
            return (Criteria) this;
        }

        public Criteria andSourcingChooseTypeIsNull() {
            addCriterion("sourcing_choose_type is null");
            return (Criteria) this;
        }

        public Criteria andSourcingChooseTypeIsNotNull() {
            addCriterion("sourcing_choose_type is not null");
            return (Criteria) this;
        }

        public Criteria andSourcingChooseTypeEqualTo(Integer value) {
            addCriterion("sourcing_choose_type =", value, "sourcingChooseType");
            return (Criteria) this;
        }

        public Criteria andSourcingChooseTypeNotEqualTo(Integer value) {
            addCriterion("sourcing_choose_type <>", value, "sourcingChooseType");
            return (Criteria) this;
        }

        public Criteria andSourcingChooseTypeGreaterThan(Integer value) {
            addCriterion("sourcing_choose_type >", value, "sourcingChooseType");
            return (Criteria) this;
        }

        public Criteria andSourcingChooseTypeGreaterThanOrEqualTo(Integer value) {
            addCriterion("sourcing_choose_type >=", value, "sourcingChooseType");
            return (Criteria) this;
        }

        public Criteria andSourcingChooseTypeLessThan(Integer value) {
            addCriterion("sourcing_choose_type <", value, "sourcingChooseType");
            return (Criteria) this;
        }

        public Criteria andSourcingChooseTypeLessThanOrEqualTo(Integer value) {
            addCriterion("sourcing_choose_type <=", value, "sourcingChooseType");
            return (Criteria) this;
        }

        public Criteria andSourcingChooseTypeIn(List<Integer> values) {
            addCriterion("sourcing_choose_type in", values, "sourcingChooseType");
            return (Criteria) this;
        }

        public Criteria andSourcingChooseTypeNotIn(List<Integer> values) {
            addCriterion("sourcing_choose_type not in", values, "sourcingChooseType");
            return (Criteria) this;
        }

        public Criteria andSourcingChooseTypeBetween(Integer value1, Integer value2) {
            addCriterion("sourcing_choose_type between", value1, value2, "sourcingChooseType");
            return (Criteria) this;
        }

        public Criteria andSourcingChooseTypeNotBetween(Integer value1, Integer value2) {
            addCriterion("sourcing_choose_type not between", value1, value2, "sourcingChooseType");
            return (Criteria) this;
        }

        public Criteria andSourcingCountryNameIsNull() {
            addCriterion("sourcing_country_name is null");
            return (Criteria) this;
        }

        public Criteria andSourcingCountryNameIsNotNull() {
            addCriterion("sourcing_country_name is not null");
            return (Criteria) this;
        }

        public Criteria andSourcingCountryNameEqualTo(String value) {
            addCriterion("sourcing_country_name =", value, "sourcingCountryName");
            return (Criteria) this;
        }

        public Criteria andSourcingCountryNameNotEqualTo(String value) {
            addCriterion("sourcing_country_name <>", value, "sourcingCountryName");
            return (Criteria) this;
        }

        public Criteria andSourcingCountryNameGreaterThan(String value) {
            addCriterion("sourcing_country_name >", value, "sourcingCountryName");
            return (Criteria) this;
        }

        public Criteria andSourcingCountryNameGreaterThanOrEqualTo(String value) {
            addCriterion("sourcing_country_name >=", value, "sourcingCountryName");
            return (Criteria) this;
        }

        public Criteria andSourcingCountryNameLessThan(String value) {
            addCriterion("sourcing_country_name <", value, "sourcingCountryName");
            return (Criteria) this;
        }

        public Criteria andSourcingCountryNameLessThanOrEqualTo(String value) {
            addCriterion("sourcing_country_name <=", value, "sourcingCountryName");
            return (Criteria) this;
        }

        public Criteria andSourcingCountryNameLike(String value) {
            addCriterion("sourcing_country_name like", value, "sourcingCountryName");
            return (Criteria) this;
        }

        public Criteria andSourcingCountryNameNotLike(String value) {
            addCriterion("sourcing_country_name not like", value, "sourcingCountryName");
            return (Criteria) this;
        }

        public Criteria andSourcingCountryNameIn(List<String> values) {
            addCriterion("sourcing_country_name in", values, "sourcingCountryName");
            return (Criteria) this;
        }

        public Criteria andSourcingCountryNameNotIn(List<String> values) {
            addCriterion("sourcing_country_name not in", values, "sourcingCountryName");
            return (Criteria) this;
        }

        public Criteria andSourcingCountryNameBetween(String value1, String value2) {
            addCriterion("sourcing_country_name between", value1, value2, "sourcingCountryName");
            return (Criteria) this;
        }

        public Criteria andSourcingCountryNameNotBetween(String value1, String value2) {
            addCriterion("sourcing_country_name not between", value1, value2, "sourcingCountryName");
            return (Criteria) this;
        }

        public Criteria andSourcingCountryIdIsNull() {
            addCriterion("sourcing_country_id is null");
            return (Criteria) this;
        }

        public Criteria andSourcingCountryIdIsNotNull() {
            addCriterion("sourcing_country_id is not null");
            return (Criteria) this;
        }

        public Criteria andSourcingCountryIdEqualTo(Integer value) {
            addCriterion("sourcing_country_id =", value, "sourcingCountryId");
            return (Criteria) this;
        }

        public Criteria andSourcingCountryIdNotEqualTo(Integer value) {
            addCriterion("sourcing_country_id <>", value, "sourcingCountryId");
            return (Criteria) this;
        }

        public Criteria andSourcingCountryIdGreaterThan(Integer value) {
            addCriterion("sourcing_country_id >", value, "sourcingCountryId");
            return (Criteria) this;
        }

        public Criteria andSourcingCountryIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("sourcing_country_id >=", value, "sourcingCountryId");
            return (Criteria) this;
        }

        public Criteria andSourcingCountryIdLessThan(Integer value) {
            addCriterion("sourcing_country_id <", value, "sourcingCountryId");
            return (Criteria) this;
        }

        public Criteria andSourcingCountryIdLessThanOrEqualTo(Integer value) {
            addCriterion("sourcing_country_id <=", value, "sourcingCountryId");
            return (Criteria) this;
        }

        public Criteria andSourcingCountryIdIn(List<Integer> values) {
            addCriterion("sourcing_country_id in", values, "sourcingCountryId");
            return (Criteria) this;
        }

        public Criteria andSourcingCountryIdNotIn(List<Integer> values) {
            addCriterion("sourcing_country_id not in", values, "sourcingCountryId");
            return (Criteria) this;
        }

        public Criteria andSourcingCountryIdBetween(Integer value1, Integer value2) {
            addCriterion("sourcing_country_id between", value1, value2, "sourcingCountryId");
            return (Criteria) this;
        }

        public Criteria andSourcingCountryIdNotBetween(Integer value1, Integer value2) {
            addCriterion("sourcing_country_id not between", value1, value2, "sourcingCountryId");
            return (Criteria) this;
        }

        public Criteria andSourcingStateNameIsNull() {
            addCriterion("sourcing_state_name is null");
            return (Criteria) this;
        }

        public Criteria andSourcingStateNameIsNotNull() {
            addCriterion("sourcing_state_name is not null");
            return (Criteria) this;
        }

        public Criteria andSourcingStateNameEqualTo(String value) {
            addCriterion("sourcing_state_name =", value, "sourcingStateName");
            return (Criteria) this;
        }

        public Criteria andSourcingStateNameNotEqualTo(String value) {
            addCriterion("sourcing_state_name <>", value, "sourcingStateName");
            return (Criteria) this;
        }

        public Criteria andSourcingStateNameGreaterThan(String value) {
            addCriterion("sourcing_state_name >", value, "sourcingStateName");
            return (Criteria) this;
        }

        public Criteria andSourcingStateNameGreaterThanOrEqualTo(String value) {
            addCriterion("sourcing_state_name >=", value, "sourcingStateName");
            return (Criteria) this;
        }

        public Criteria andSourcingStateNameLessThan(String value) {
            addCriterion("sourcing_state_name <", value, "sourcingStateName");
            return (Criteria) this;
        }

        public Criteria andSourcingStateNameLessThanOrEqualTo(String value) {
            addCriterion("sourcing_state_name <=", value, "sourcingStateName");
            return (Criteria) this;
        }

        public Criteria andSourcingStateNameLike(String value) {
            addCriterion("sourcing_state_name like", value, "sourcingStateName");
            return (Criteria) this;
        }

        public Criteria andSourcingStateNameNotLike(String value) {
            addCriterion("sourcing_state_name not like", value, "sourcingStateName");
            return (Criteria) this;
        }

        public Criteria andSourcingStateNameIn(List<String> values) {
            addCriterion("sourcing_state_name in", values, "sourcingStateName");
            return (Criteria) this;
        }

        public Criteria andSourcingStateNameNotIn(List<String> values) {
            addCriterion("sourcing_state_name not in", values, "sourcingStateName");
            return (Criteria) this;
        }

        public Criteria andSourcingStateNameBetween(String value1, String value2) {
            addCriterion("sourcing_state_name between", value1, value2, "sourcingStateName");
            return (Criteria) this;
        }

        public Criteria andSourcingStateNameNotBetween(String value1, String value2) {
            addCriterion("sourcing_state_name not between", value1, value2, "sourcingStateName");
            return (Criteria) this;
        }

        public Criteria andSourcingCustomTypeIsNull() {
            addCriterion("sourcing_custom_type is null");
            return (Criteria) this;
        }

        public Criteria andSourcingCustomTypeIsNotNull() {
            addCriterion("sourcing_custom_type is not null");
            return (Criteria) this;
        }

        public Criteria andSourcingCustomTypeEqualTo(String value) {
            addCriterion("sourcing_custom_type =", value, "sourcingCustomType");
            return (Criteria) this;
        }

        public Criteria andSourcingCustomTypeNotEqualTo(String value) {
            addCriterion("sourcing_custom_type <>", value, "sourcingCustomType");
            return (Criteria) this;
        }

        public Criteria andSourcingCustomTypeGreaterThan(String value) {
            addCriterion("sourcing_custom_type >", value, "sourcingCustomType");
            return (Criteria) this;
        }

        public Criteria andSourcingCustomTypeGreaterThanOrEqualTo(String value) {
            addCriterion("sourcing_custom_type >=", value, "sourcingCustomType");
            return (Criteria) this;
        }

        public Criteria andSourcingCustomTypeLessThan(String value) {
            addCriterion("sourcing_custom_type <", value, "sourcingCustomType");
            return (Criteria) this;
        }

        public Criteria andSourcingCustomTypeLessThanOrEqualTo(String value) {
            addCriterion("sourcing_custom_type <=", value, "sourcingCustomType");
            return (Criteria) this;
        }

        public Criteria andSourcingCustomTypeLike(String value) {
            addCriterion("sourcing_custom_type like", value, "sourcingCustomType");
            return (Criteria) this;
        }

        public Criteria andSourcingCustomTypeNotLike(String value) {
            addCriterion("sourcing_custom_type not like", value, "sourcingCustomType");
            return (Criteria) this;
        }

        public Criteria andSourcingCustomTypeIn(List<String> values) {
            addCriterion("sourcing_custom_type in", values, "sourcingCustomType");
            return (Criteria) this;
        }

        public Criteria andSourcingCustomTypeNotIn(List<String> values) {
            addCriterion("sourcing_custom_type not in", values, "sourcingCustomType");
            return (Criteria) this;
        }

        public Criteria andSourcingCustomTypeBetween(String value1, String value2) {
            addCriterion("sourcing_custom_type between", value1, value2, "sourcingCustomType");
            return (Criteria) this;
        }

        public Criteria andSourcingCustomTypeNotBetween(String value1, String value2) {
            addCriterion("sourcing_custom_type not between", value1, value2, "sourcingCustomType");
            return (Criteria) this;
        }

        public Criteria andSourcingOrderQuantityIsNull() {
            addCriterion("sourcing_order_quantity is null");
            return (Criteria) this;
        }

        public Criteria andSourcingOrderQuantityIsNotNull() {
            addCriterion("sourcing_order_quantity is not null");
            return (Criteria) this;
        }

        public Criteria andSourcingOrderQuantityEqualTo(Integer value) {
            addCriterion("sourcing_order_quantity =", value, "sourcingOrderQuantity");
            return (Criteria) this;
        }

        public Criteria andSourcingOrderQuantityNotEqualTo(Integer value) {
            addCriterion("sourcing_order_quantity <>", value, "sourcingOrderQuantity");
            return (Criteria) this;
        }

        public Criteria andSourcingOrderQuantityGreaterThan(Integer value) {
            addCriterion("sourcing_order_quantity >", value, "sourcingOrderQuantity");
            return (Criteria) this;
        }

        public Criteria andSourcingOrderQuantityGreaterThanOrEqualTo(Integer value) {
            addCriterion("sourcing_order_quantity >=", value, "sourcingOrderQuantity");
            return (Criteria) this;
        }

        public Criteria andSourcingOrderQuantityLessThan(Integer value) {
            addCriterion("sourcing_order_quantity <", value, "sourcingOrderQuantity");
            return (Criteria) this;
        }

        public Criteria andSourcingOrderQuantityLessThanOrEqualTo(Integer value) {
            addCriterion("sourcing_order_quantity <=", value, "sourcingOrderQuantity");
            return (Criteria) this;
        }

        public Criteria andSourcingOrderQuantityIn(List<Integer> values) {
            addCriterion("sourcing_order_quantity in", values, "sourcingOrderQuantity");
            return (Criteria) this;
        }

        public Criteria andSourcingOrderQuantityNotIn(List<Integer> values) {
            addCriterion("sourcing_order_quantity not in", values, "sourcingOrderQuantity");
            return (Criteria) this;
        }

        public Criteria andSourcingOrderQuantityBetween(Integer value1, Integer value2) {
            addCriterion("sourcing_order_quantity between", value1, value2, "sourcingOrderQuantity");
            return (Criteria) this;
        }

        public Criteria andSourcingOrderQuantityNotBetween(Integer value1, Integer value2) {
            addCriterion("sourcing_order_quantity not between", value1, value2, "sourcingOrderQuantity");
            return (Criteria) this;
        }

        public Criteria andSourcingRemarkIsNull() {
            addCriterion("sourcing_remark is null");
            return (Criteria) this;
        }

        public Criteria andSourcingRemarkIsNotNull() {
            addCriterion("sourcing_remark is not null");
            return (Criteria) this;
        }

        public Criteria andSourcingRemarkEqualTo(String value) {
            addCriterion("sourcing_remark =", value, "sourcingRemark");
            return (Criteria) this;
        }

        public Criteria andSourcingRemarkNotEqualTo(String value) {
            addCriterion("sourcing_remark <>", value, "sourcingRemark");
            return (Criteria) this;
        }

        public Criteria andSourcingRemarkGreaterThan(String value) {
            addCriterion("sourcing_remark >", value, "sourcingRemark");
            return (Criteria) this;
        }

        public Criteria andSourcingRemarkGreaterThanOrEqualTo(String value) {
            addCriterion("sourcing_remark >=", value, "sourcingRemark");
            return (Criteria) this;
        }

        public Criteria andSourcingRemarkLessThan(String value) {
            addCriterion("sourcing_remark <", value, "sourcingRemark");
            return (Criteria) this;
        }

        public Criteria andSourcingRemarkLessThanOrEqualTo(String value) {
            addCriterion("sourcing_remark <=", value, "sourcingRemark");
            return (Criteria) this;
        }

        public Criteria andSourcingRemarkLike(String value) {
            addCriterion("sourcing_remark like", value, "sourcingRemark");
            return (Criteria) this;
        }

        public Criteria andSourcingRemarkNotLike(String value) {
            addCriterion("sourcing_remark not like", value, "sourcingRemark");
            return (Criteria) this;
        }

        public Criteria andSourcingRemarkIn(List<String> values) {
            addCriterion("sourcing_remark in", values, "sourcingRemark");
            return (Criteria) this;
        }

        public Criteria andSourcingRemarkNotIn(List<String> values) {
            addCriterion("sourcing_remark not in", values, "sourcingRemark");
            return (Criteria) this;
        }

        public Criteria andSourcingRemarkBetween(String value1, String value2) {
            addCriterion("sourcing_remark between", value1, value2, "sourcingRemark");
            return (Criteria) this;
        }

        public Criteria andSourcingRemarkNotBetween(String value1, String value2) {
            addCriterion("sourcing_remark not between", value1, value2, "sourcingRemark");
            return (Criteria) this;
        }

        public Criteria andSourcingPrcFlagIsNull() {
            addCriterion("sourcing_prc_flag is null");
            return (Criteria) this;
        }

        public Criteria andSourcingPrcFlagIsNotNull() {
            addCriterion("sourcing_prc_flag is not null");
            return (Criteria) this;
        }

        public Criteria andSourcingPrcFlagEqualTo(Integer value) {
            addCriterion("sourcing_prc_flag =", value, "sourcingPrcFlag");
            return (Criteria) this;
        }

        public Criteria andSourcingPrcFlagNotEqualTo(Integer value) {
            addCriterion("sourcing_prc_flag <>", value, "sourcingPrcFlag");
            return (Criteria) this;
        }

        public Criteria andSourcingPrcFlagGreaterThan(Integer value) {
            addCriterion("sourcing_prc_flag >", value, "sourcingPrcFlag");
            return (Criteria) this;
        }

        public Criteria andSourcingPrcFlagGreaterThanOrEqualTo(Integer value) {
            addCriterion("sourcing_prc_flag >=", value, "sourcingPrcFlag");
            return (Criteria) this;
        }

        public Criteria andSourcingPrcFlagLessThan(Integer value) {
            addCriterion("sourcing_prc_flag <", value, "sourcingPrcFlag");
            return (Criteria) this;
        }

        public Criteria andSourcingPrcFlagLessThanOrEqualTo(Integer value) {
            addCriterion("sourcing_prc_flag <=", value, "sourcingPrcFlag");
            return (Criteria) this;
        }

        public Criteria andSourcingPrcFlagIn(List<Integer> values) {
            addCriterion("sourcing_prc_flag in", values, "sourcingPrcFlag");
            return (Criteria) this;
        }

        public Criteria andSourcingPrcFlagNotIn(List<Integer> values) {
            addCriterion("sourcing_prc_flag not in", values, "sourcingPrcFlag");
            return (Criteria) this;
        }

        public Criteria andSourcingPrcFlagBetween(Integer value1, Integer value2) {
            addCriterion("sourcing_prc_flag between", value1, value2, "sourcingPrcFlag");
            return (Criteria) this;
        }

        public Criteria andSourcingPrcFlagNotBetween(Integer value1, Integer value2) {
            addCriterion("sourcing_prc_flag not between", value1, value2, "sourcingPrcFlag");
            return (Criteria) this;
        }

        public Criteria andSourcingPricePsIsNull() {
            addCriterion("sourcing_price_ps is null");
            return (Criteria) this;
        }

        public Criteria andSourcingPricePsIsNotNull() {
            addCriterion("sourcing_price_ps is not null");
            return (Criteria) this;
        }

        public Criteria andSourcingPricePsEqualTo(String value) {
            addCriterion("sourcing_price_ps =", value, "sourcingPricePs");
            return (Criteria) this;
        }

        public Criteria andSourcingPricePsNotEqualTo(String value) {
            addCriterion("sourcing_price_ps <>", value, "sourcingPricePs");
            return (Criteria) this;
        }

        public Criteria andSourcingPricePsGreaterThan(String value) {
            addCriterion("sourcing_price_ps >", value, "sourcingPricePs");
            return (Criteria) this;
        }

        public Criteria andSourcingPricePsGreaterThanOrEqualTo(String value) {
            addCriterion("sourcing_price_ps >=", value, "sourcingPricePs");
            return (Criteria) this;
        }

        public Criteria andSourcingPricePsLessThan(String value) {
            addCriterion("sourcing_price_ps <", value, "sourcingPricePs");
            return (Criteria) this;
        }

        public Criteria andSourcingPricePsLessThanOrEqualTo(String value) {
            addCriterion("sourcing_price_ps <=", value, "sourcingPricePs");
            return (Criteria) this;
        }

        public Criteria andSourcingPricePsLike(String value) {
            addCriterion("sourcing_price_ps like", value, "sourcingPricePs");
            return (Criteria) this;
        }

        public Criteria andSourcingPricePsNotLike(String value) {
            addCriterion("sourcing_price_ps not like", value, "sourcingPricePs");
            return (Criteria) this;
        }

        public Criteria andSourcingPricePsIn(List<String> values) {
            addCriterion("sourcing_price_ps in", values, "sourcingPricePs");
            return (Criteria) this;
        }

        public Criteria andSourcingPricePsNotIn(List<String> values) {
            addCriterion("sourcing_price_ps not in", values, "sourcingPricePs");
            return (Criteria) this;
        }

        public Criteria andSourcingPricePsBetween(String value1, String value2) {
            addCriterion("sourcing_price_ps between", value1, value2, "sourcingPricePs");
            return (Criteria) this;
        }

        public Criteria andSourcingPricePsNotBetween(String value1, String value2) {
            addCriterion("sourcing_price_ps not between", value1, value2, "sourcingPricePs");
            return (Criteria) this;
        }

        public Criteria andSourcingCifPortIsNull() {
            addCriterion("sourcing_cif_port is null");
            return (Criteria) this;
        }

        public Criteria andSourcingCifPortIsNotNull() {
            addCriterion("sourcing_cif_port is not null");
            return (Criteria) this;
        }

        public Criteria andSourcingCifPortEqualTo(String value) {
            addCriterion("sourcing_cif_port =", value, "sourcingCifPort");
            return (Criteria) this;
        }

        public Criteria andSourcingCifPortNotEqualTo(String value) {
            addCriterion("sourcing_cif_port <>", value, "sourcingCifPort");
            return (Criteria) this;
        }

        public Criteria andSourcingCifPortGreaterThan(String value) {
            addCriterion("sourcing_cif_port >", value, "sourcingCifPort");
            return (Criteria) this;
        }

        public Criteria andSourcingCifPortGreaterThanOrEqualTo(String value) {
            addCriterion("sourcing_cif_port >=", value, "sourcingCifPort");
            return (Criteria) this;
        }

        public Criteria andSourcingCifPortLessThan(String value) {
            addCriterion("sourcing_cif_port <", value, "sourcingCifPort");
            return (Criteria) this;
        }

        public Criteria andSourcingCifPortLessThanOrEqualTo(String value) {
            addCriterion("sourcing_cif_port <=", value, "sourcingCifPort");
            return (Criteria) this;
        }

        public Criteria andSourcingCifPortLike(String value) {
            addCriterion("sourcing_cif_port like", value, "sourcingCifPort");
            return (Criteria) this;
        }

        public Criteria andSourcingCifPortNotLike(String value) {
            addCriterion("sourcing_cif_port not like", value, "sourcingCifPort");
            return (Criteria) this;
        }

        public Criteria andSourcingCifPortIn(List<String> values) {
            addCriterion("sourcing_cif_port in", values, "sourcingCifPort");
            return (Criteria) this;
        }

        public Criteria andSourcingCifPortNotIn(List<String> values) {
            addCriterion("sourcing_cif_port not in", values, "sourcingCifPort");
            return (Criteria) this;
        }

        public Criteria andSourcingCifPortBetween(String value1, String value2) {
            addCriterion("sourcing_cif_port between", value1, value2, "sourcingCifPort");
            return (Criteria) this;
        }

        public Criteria andSourcingCifPortNotBetween(String value1, String value2) {
            addCriterion("sourcing_cif_port not between", value1, value2, "sourcingCifPort");
            return (Criteria) this;
        }

        public Criteria andSourcingFbaWarehouseIsNull() {
            addCriterion("sourcing_fba_warehouse is null");
            return (Criteria) this;
        }

        public Criteria andSourcingFbaWarehouseIsNotNull() {
            addCriterion("sourcing_fba_warehouse is not null");
            return (Criteria) this;
        }

        public Criteria andSourcingFbaWarehouseEqualTo(String value) {
            addCriterion("sourcing_fba_warehouse =", value, "sourcingFbaWarehouse");
            return (Criteria) this;
        }

        public Criteria andSourcingFbaWarehouseNotEqualTo(String value) {
            addCriterion("sourcing_fba_warehouse <>", value, "sourcingFbaWarehouse");
            return (Criteria) this;
        }

        public Criteria andSourcingFbaWarehouseGreaterThan(String value) {
            addCriterion("sourcing_fba_warehouse >", value, "sourcingFbaWarehouse");
            return (Criteria) this;
        }

        public Criteria andSourcingFbaWarehouseGreaterThanOrEqualTo(String value) {
            addCriterion("sourcing_fba_warehouse >=", value, "sourcingFbaWarehouse");
            return (Criteria) this;
        }

        public Criteria andSourcingFbaWarehouseLessThan(String value) {
            addCriterion("sourcing_fba_warehouse <", value, "sourcingFbaWarehouse");
            return (Criteria) this;
        }

        public Criteria andSourcingFbaWarehouseLessThanOrEqualTo(String value) {
            addCriterion("sourcing_fba_warehouse <=", value, "sourcingFbaWarehouse");
            return (Criteria) this;
        }

        public Criteria andSourcingFbaWarehouseLike(String value) {
            addCriterion("sourcing_fba_warehouse like", value, "sourcingFbaWarehouse");
            return (Criteria) this;
        }

        public Criteria andSourcingFbaWarehouseNotLike(String value) {
            addCriterion("sourcing_fba_warehouse not like", value, "sourcingFbaWarehouse");
            return (Criteria) this;
        }

        public Criteria andSourcingFbaWarehouseIn(List<String> values) {
            addCriterion("sourcing_fba_warehouse in", values, "sourcingFbaWarehouse");
            return (Criteria) this;
        }

        public Criteria andSourcingFbaWarehouseNotIn(List<String> values) {
            addCriterion("sourcing_fba_warehouse not in", values, "sourcingFbaWarehouse");
            return (Criteria) this;
        }

        public Criteria andSourcingFbaWarehouseBetween(String value1, String value2) {
            addCriterion("sourcing_fba_warehouse between", value1, value2, "sourcingFbaWarehouse");
            return (Criteria) this;
        }

        public Criteria andSourcingFbaWarehouseNotBetween(String value1, String value2) {
            addCriterion("sourcing_fba_warehouse not between", value1, value2, "sourcingFbaWarehouse");
            return (Criteria) this;
        }

        public Criteria andLogoUrlIsNull() {
            addCriterion("logo_url is null");
            return (Criteria) this;
        }

        public Criteria andLogoUrlIsNotNull() {
            addCriterion("logo_url is not null");
            return (Criteria) this;
        }

        public Criteria andLogoUrlEqualTo(String value) {
            addCriterion("logo_url =", value, "logoUrl");
            return (Criteria) this;
        }

        public Criteria andLogoUrlNotEqualTo(String value) {
            addCriterion("logo_url <>", value, "logoUrl");
            return (Criteria) this;
        }

        public Criteria andLogoUrlGreaterThan(String value) {
            addCriterion("logo_url >", value, "logoUrl");
            return (Criteria) this;
        }

        public Criteria andLogoUrlGreaterThanOrEqualTo(String value) {
            addCriterion("logo_url >=", value, "logoUrl");
            return (Criteria) this;
        }

        public Criteria andLogoUrlLessThan(String value) {
            addCriterion("logo_url <", value, "logoUrl");
            return (Criteria) this;
        }

        public Criteria andLogoUrlLessThanOrEqualTo(String value) {
            addCriterion("logo_url <=", value, "logoUrl");
            return (Criteria) this;
        }

        public Criteria andLogoUrlLike(String value) {
            addCriterion("logo_url like", value, "logoUrl");
            return (Criteria) this;
        }

        public Criteria andLogoUrlNotLike(String value) {
            addCriterion("logo_url not like", value, "logoUrl");
            return (Criteria) this;
        }

        public Criteria andLogoUrlIn(List<String> values) {
            addCriterion("logo_url in", values, "logoUrl");
            return (Criteria) this;
        }

        public Criteria andLogoUrlNotIn(List<String> values) {
            addCriterion("logo_url not in", values, "logoUrl");
            return (Criteria) this;
        }

        public Criteria andLogoUrlBetween(String value1, String value2) {
            addCriterion("logo_url between", value1, value2, "logoUrl");
            return (Criteria) this;
        }

        public Criteria andLogoUrlNotBetween(String value1, String value2) {
            addCriterion("logo_url not between", value1, value2, "logoUrl");
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