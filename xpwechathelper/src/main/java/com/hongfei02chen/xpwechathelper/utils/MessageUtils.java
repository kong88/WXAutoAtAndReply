package com.hongfei02chen.xpwechathelper.utils;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * created by chenhongfei on 2018/6/7
 */
public class MessageUtils {
    private static final String REGEX = "^激活\\$\\{([^\\{\\}]+)\\}";

    public static String parseActiveGroup(String content) {
        String group = "";
        Pattern pattern = Pattern.compile(REGEX);
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            if (matcher.groupCount() >= 1) {
                group = matcher.group(1);
            }
        }

        return group;
    }
}
