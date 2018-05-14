package com.hongfei02chen.wxautoatreply.utils;

import java.util.Collection;
import java.util.Map;

/**
 * created by chenhongfei on 2018/5/11
 */
public class CollectionUtils {
    /**
     * 判断集合是否为空
     *
     * @param obj
     * @return
     */
    public static boolean isEmpty(Collection obj) {
        if (obj == null || obj.isEmpty()) {
            return true;
        }

        return false;
    }

    public static boolean isEmpty(Map obj) {
        if (obj == null || obj.isEmpty()) {
            return true;
        }

        return false;
    }
}

