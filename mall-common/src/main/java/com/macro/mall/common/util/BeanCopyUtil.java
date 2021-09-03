package com.macro.mall.common.util;

import cn.hutool.core.bean.BeanUtil;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.macro.mall.portal.util
 * @date:2021-04-20
 */
public class BeanCopyUtil {
    /**
     * 深度Bean拷贝
     *
     * @param src
     * @param <T>
     * @return
     */
    @SneakyThrows
    public static <T> List<T> deepListCopy(List<T> src) {
        List<T> newList = new ArrayList<>();
        for (T t : src) {
            Object obj = t.getClass().newInstance();
            BeanUtil.copyProperties(t, obj);
            newList.add((T) obj);
        }
        return newList;
    }

}
