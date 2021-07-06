package com.macro.mall.service;

import com.alibaba.fastjson.JSONObject;
import com.macro.mall.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.importexpress.aliexpress.service
 * @date:2020/3/16
 */
@Service
@Slf4j
public class XmsAliExpressCacheService {
    private static final String REDIS_KEYWORD_PRE = "aliexpress:keyword:";
    private static final String REDIS_PID_PRE = "aliexpress:pid:";
    private static final int REDIS_EXPIRE_DAYS = 1;
    private static final int REDIS_EXPIRE_DAYS_TWO = 1;
    private final StringRedisTemplate redisTemplate;

    public XmsAliExpressCacheService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    public JSONObject getItemByKeyword(Integer page, String keyword, String start_price, String end_price,
                                       String sort) {
        Objects.requireNonNull(page);
        Objects.requireNonNull(keyword);

        String value = this.redisTemplate.opsForValue().get(getRedisKeyInfo(page, keyword, start_price, end_price, sort));
        if (StringUtils.isNotEmpty(value)) {
            return JSONObject.parseObject(value);
        } else {
            return null;
        }
    }


    public void saveItemByKeyword(Integer page, String keyword, String start_price, String end_price,
                                  String sort, JSONObject jsonObject) {
        Objects.requireNonNull(page);
        Objects.requireNonNull(keyword);
        Objects.requireNonNull(jsonObject);
        this.redisTemplate.opsForValue().set(getRedisKeyInfo(page, keyword, start_price, end_price, sort),
                JSONObject.toJSONString(jsonObject), REDIS_EXPIRE_DAYS, TimeUnit.DAYS);
    }


    public Boolean deleteKeyword(Integer page, String keyword, String start_price, String end_price,
                                 String sort) {
        Objects.requireNonNull(page);
        Objects.requireNonNull(keyword);
        return this.redisTemplate.delete(getRedisKeyInfo(page, keyword, start_price, end_price, sort));
    }

    private String getRedisKeyInfo(Integer page, String keyword, String start_price, String end_price,
                                   String sort) {
        StringBuffer redisKey = new StringBuffer(REDIS_KEYWORD_PRE
                + StringUtil.checkAndChangeSpace(keyword, "_") + "_" + page);
        if (StringUtils.isNotBlank(start_price)) {
            redisKey.append("_" + StringUtil.checkAndChangeSpaceAndOther(start_price, "_"));
        }
        if (StringUtils.isNotBlank(end_price)) {
            redisKey.append("_" + StringUtil.checkAndChangeSpaceAndOther(end_price, "_"));
        }
        if (StringUtils.isNotBlank(sort)) {
            redisKey.append("_" + sort);
        }
        return redisKey.toString();
    }


    public void setItemInfo(String pid, JSONObject jsonObject) {
        Objects.requireNonNull(jsonObject);
        this.redisTemplate.opsForValue().set(REDIS_PID_PRE + pid,
                JSONObject.toJSONString(jsonObject), REDIS_EXPIRE_DAYS_TWO, TimeUnit.DAYS);
    }

    public void setItemInfoTime(String pid, JSONObject jsonObject, int expireTime) {
        Objects.requireNonNull(jsonObject);
        this.redisTemplate.opsForValue().set(REDIS_PID_PRE + pid,
                JSONObject.toJSONString(jsonObject), expireTime, TimeUnit.HOURS);
    }

    public void setItemInfoTimeSeconds(String pid, JSONObject jsonObject, int expireTime) {
        Objects.requireNonNull(jsonObject);
        this.redisTemplate.opsForValue().set(REDIS_PID_PRE + pid,
                JSONObject.toJSONString(jsonObject), expireTime, TimeUnit.SECONDS);
    }

    public JSONObject getItemInfo(String pid) {
        Objects.requireNonNull(pid);

        String value = this.redisTemplate.opsForValue().get(REDIS_PID_PRE + pid);
        if (StringUtils.isNotEmpty(value)) {
            return JSONObject.parseObject(value);
        } else {
            return null;
        }
    }

    public void deleteItemInfo(String pid){
        Objects.requireNonNull(pid);
        this.redisTemplate.delete(REDIS_PID_PRE + pid);
    }

}
