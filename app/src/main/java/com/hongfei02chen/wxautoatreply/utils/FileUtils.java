package com.hongfei02chen.wxautoatreply.utils;

import java.io.File;
import java.io.FileOutputStream;

/**
 * created by chenhongfei on 2018/5/14
 */
public class FileUtils {
    public static void appendFile(String filePath, String content) {
        try {
            FileOutputStream fos = new FileOutputStream(filePath, true);
            fos.write(content.getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static File getTmpDir() {
        try {
            if (android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment.getExternalStorageState())) {
                File cacheDir = new File(android.os.Environment.getExternalStorageDirectory(), "/autoAtAndReply");
                if (!cacheDir.exists()) {
                    cacheDir.mkdirs();
                }

                return cacheDir;
            }
        } catch (Exception e) {
           e.printStackTrace();
        }
        return null;
    }

    public static String getSaveFilePath() {
        File file = new File(getTmpDir(), "group_nickname.txt");
        return  file.toString();
    }
}