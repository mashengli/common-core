package com.qiandai.pay.common.util;

import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.TreeMap;

public class Util {

    /**
     * @param ht
     * @param linkmark 参数连接符号
     * @return
     */
    public static String HashTable2String(Hashtable ht, String linkmark) throws Exception {
        String retMsg = "";
        String key;
        try {
            Enumeration e1 = ht.keys();
            while (e1.hasMoreElements()) {
                key = (String) e1.nextElement();
                retMsg = retMsg + key + "=" + ht.get(key).toString() + linkmark;
            }
            retMsg = retMsg.substring(0, retMsg.length() - 1);
        } catch (Exception ignored) {
        }
        return retMsg;
    }

    public static String HashTable2String(Hashtable ht, String linkmark, boolean isUrlEncode) throws Exception {
        String retMsg = "";
        String key;
        Enumeration e1;
        try {
            if (isUrlEncode) {
                e1 = (Enumeration) ht.keySet();
                while (e1.hasMoreElements()) {
                    key = (String) e1.nextElement();
                    retMsg = retMsg + key + "="
                            + URLEncoder.encode(ht.get(key).toString(), "UTF-8")
                            + linkmark;
                }
                retMsg = retMsg.substring(0, retMsg.length() - 1);
            } else {
                e1 = (Enumeration) ht.keySet();
                while (e1.hasMoreElements()) {
                    key = (String) e1.nextElement();
                    retMsg = retMsg + key + "=" + ht.get(key).toString()
                            + linkmark;
                }
                retMsg = retMsg.substring(0, retMsg.length() - 1);
            }
        } catch (Exception ignored) {
        }
        return retMsg;
    }

    /**
     * @param map
     * @param linkMark
     * @return
     */
    public static String Map2String(TreeMap<String, String> map, String linkMark, boolean isUrlencode) throws Exception {
        StringBuilder retmsg = new StringBuilder();
        try {
            if (isUrlencode) {
                for (Entry<String, String> entry : map.entrySet()) {
                    retmsg.append("&").append(entry.getKey()).append("=")
                            .append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                }
            } else {
                for (Entry<String, String> entry : map.entrySet()) {
                    retmsg.append("&").append(entry.getKey())
                            .append("=").append(entry.getValue());
                }
            }

        } catch (Exception ignored) {
        }
        return retmsg.toString().substring(1);
    }

}
