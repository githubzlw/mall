package com.macro.mall.onebound.util;

import com.google.common.base.CharMatcher;
import org.apache.logging.log4j.util.Strings;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.importexpress.ali1688.util
 * @date:2020/3/17
 */
public class StringUtil {

    public static String checkAndChangeSpace(String keyword, String replaceStr) {
        if (Strings.isNotBlank(keyword)) {
            String trimStr = CharMatcher.whitespace().trimAndCollapseFrom(keyword, ' ');
            return trimStr.replace(" ", replaceStr);
        }
        return keyword;
    }

    public static String checkAndChangeSpaceAndOther(String keyword, String replaceStr) {
        if(keyword.contains(".")){
            keyword = keyword.replace(".","_");
        }
        return checkAndChangeSpace(keyword, replaceStr);
    }
}
