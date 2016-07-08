package com.qiandai.pay.common.util;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.commons.io.IOUtils;

/**
 * 加载属性
 */
public class PropertiesLoader {

    private static Properties prop;

    public static void load(String name) {
        String fileName = name;
        prop = new Properties();
        InputStream in = null;
        try {
            in = new FileInputStream(fileName);//
            InputStreamReader isr = new InputStreamReader(in, "UTF-8");
            prop.load(isr);
        } catch (Throwable t) {
            t.printStackTrace();
            try {
                in = new FileInputStream(fileName);
                prop.load(in);
            } catch (Throwable t1) {
                throw new RuntimeException("fail to load " + fileName);
            }
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    public static String getProperty(String name) {
        String value = prop.getProperty(name);
        if (value != null) {
            value = new String(value.getBytes());
        }
        return value;
    }

    public void setProperty(String key, String value) {
        prop.setProperty(key, value);
    }

}
