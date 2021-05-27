package com.macro.mall.onebound.util;

import com.alibaba.fastjson.JSONObject;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * @author jack.luo
 * @date 2019/11/13
 */
public final class InvalidPid {

    private InvalidPid() {

    }

    /**
     * return invalid pid's json object
     *
     * @param pid
     * @param resason
     * @return
     */
    public static @NonNull JSONObject of(Long pid, String resason) {
        Objects.requireNonNull(pid);
        Objects.requireNonNull(resason);
        JSONObject jsonObject = new JSONObject();
        final String now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        jsonObject.put("secache_date", now);
        jsonObject.put("server_time", now);
        jsonObject.put("reason", resason);
        jsonObject.put("pid", pid);
        return jsonObject;
    }
}
