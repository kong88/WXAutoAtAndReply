package com.hongfei02chen.wxautoatreply.utils;

import android.util.Pair;

import java.util.LinkedList;

/**
 * 数据存储
 * created by chenhongfei on 2018/5/14
 */
public class DataUtils {
    public static LinkedList<Pair> mList = new LinkedList<>();

    public static void enQueue(String group, String nickname) {
        Pair pair = new Pair(nickname, group);
        mList.addLast(pair);
    }

    public static Pair deQueue() {
        if (mList.isEmpty()) {
            return null;
        }
        Pair p = mList.getFirst();

        return p;
    }
}
