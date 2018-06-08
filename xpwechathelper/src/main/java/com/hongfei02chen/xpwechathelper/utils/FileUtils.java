package com.hongfei02chen.xpwechathelper.utils;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    public static File getSDCardDir() {
        try {
            if (android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment.getExternalStorageState())) {
                return android.os.Environment.getExternalStorageDirectory();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getSaveFilePath() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(new Date());
        File file = new File(getTmpDir(), "wechat_magic_message." + date);
        return file.toString();
    }

    public static boolean copyFile(File src, String destPath, String fileName) {

        boolean result = false;
        if ((src == null) || (destPath == null)) {
            return result;
        }
        File dest = new File(destPath + fileName);
        Log.e("xpose", " src:" + src.toString() + " dest:" + dest.toString());
        if (dest != null && dest.exists()) {
            dest.delete(); // delete file
        }
        try {
            dest.createNewFile();
            dest.setReadable(true, false);
            dest.setWritable(true, false);
            dest.setExecutable(true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileChannel srcChannel = null;
        FileChannel dstChannel = null;

        try {
            srcChannel = new FileInputStream(src).getChannel();
            dstChannel = new FileOutputStream(dest).getChannel();
            srcChannel.transferTo(0, srcChannel.size(), dstChannel);
            result = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return result;
        }
        try {
            srcChannel.close();
            dstChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}