package com.hongfei02chen.xpwechathelper.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

/**
 * created by chenhongfei on 2018/5/31
 */
public class PropertiesUtils {


    private static Properties getProperties(String fileName) {
        Properties props = new Properties();
        FileInputStream fis = null;
        try {
            File file = new File(fileName);
            if (!file.exists()) {
                File path = new File(file.getParent());
                if (!path.exists()) {
                    path.mkdirs();
                }
                file.createNewFile();
            }
            fis = new FileInputStream(file);
            props.load(fis);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return props;
    }

    public static String getValue(String fileName, String key, String defaultValue) {
        return getProperties(fileName).getProperty(key, defaultValue);
    }

    public static void putValue(String fileName, String key, String value) {
        try {
            Properties properties = getProperties(fileName);
            OutputStream os = new FileOutputStream(fileName);
            properties.setProperty(key, value);
            properties.store(os, "Update value");
            os.flush();
            os.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
