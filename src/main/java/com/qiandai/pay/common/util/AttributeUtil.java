package com.qiandai.pay.common.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class AttributeUtil {
    private static final String SP = ";";
    private static final String SSP = ":";
    private static final String R_SP = "#3A";
    private static final String R_SSP = "#3B";

    public static String toString(Map<String, String> attrs) {
        StringBuilder sb = new StringBuilder();
        if ((null != attrs) && (!attrs.isEmpty())) {
            sb.append(";");
            List<String> list = new ArrayList<>(attrs.keySet());
            Collections.sort(list);
            for (String key : list) {
                String val = (String) attrs.get(key);
                if (StringUtils.isNotEmpty(val)) {
                    sb.append(encode(key)).append(":").append(encode(val)).append(";");
                }
            }
        }
        return sb.toString();
    }

    public static Map<String, String> fromString(String str) {
        Map<String, String> attrs = new HashMap<>();
        if (StringUtils.isNotBlank(str)) {
            String[] arr = str.split(";");
            for (String kv : arr) {
                if (StringUtils.isNotBlank(kv)) {
                    String[] ar = kv.split(":");
                    if ((ar.length == 2)) {
                        String key = decode(ar[0]);
                        String val = decode(ar[1]);
                        if (StringUtils.isNotEmpty(val)) {
                            attrs.put(key, val);
                        }
                    }
                }
            }
        }
        return attrs;
    }

    public static Map<String, String> toMap(String str) {
        Map<String, String> attrs = new HashMap<>();
        if (StringUtils.isNotBlank(str)) {
            String[] arr = StringUtils.split(str, ";");
            for (String kv : arr) {
                if (StringUtils.isNotBlank(kv)) {
                    String[] ar = kv.split(":");
                    if (ar.length == 2) {
                        String k = decode(ar[0]);
                        String v = decode(ar[1]);
                        if ((StringUtils.isNotBlank(k)) && (StringUtils.isNotBlank(v))) {
                            attrs.put(k, v);
                        }
                    }
                }
            }
        }
        return attrs;
    }

    private static String encode(String val) {
        return StringUtils.replace(StringUtils.replace(val, ";", "#3A"), ":", "#3B");
    }

    private static String decode(String val) {
        return StringUtils.replace(StringUtils.replace(val, "#3A", ";"), "#3B", ":");
    }

    @SafeVarargs
    public static <T> T getEnum(T value, T... enums) {
        if (value == null) {
            return null;
        }
        for (T t : enums) {
            if (t.equals(value)) {
                return value;
            }
        }
        return enums[0];
    }

    public static String replacePlaceHolderWithMapValue(String msg, Map<String, Object> params) {
        Pattern placeHolderPattern = Pattern.compile("\\$\\{([\\w\\d]+)\\}");
        Matcher m = placeHolderPattern.matcher(msg);
        Map<String, String> relaceList = new HashMap<>();
        while (m.find()) {
            String placeHolder = m.group(0);
            String key = m.group(1);

            Object value = params.get(key);
            String errorValue;
            if (value != null) {
                errorValue = value.toString();
            } else {
                errorValue = key;
            }
            relaceList.put(placeHolder, errorValue);
        }
        for (Map.Entry<String, String> keyValue : relaceList.entrySet()) {
            msg = msg.replace((CharSequence) keyValue.getKey(), (CharSequence) keyValue.getValue());
        }
        return msg;
    }

    public static String getList2String(List<Long> list) {
        StringBuilder sb = new StringBuilder();
        if (list != null) {
            for (Long item : list) {
                sb.append(item);
                sb.append(":");
            }
        }
        return sb.toString();
    }

    public static List<Long> getString2List(String speDes) {
        if (speDes == null) {
            return Collections.emptyList();
        }
        String[] speIdsstr = speDes.split(":");
        List<Long> speIds = new ArrayList<>();
        for (String speIdStr : speIdsstr) {
            speIds.add(Long.parseLong(speIdStr));
        }
        return speIds;
    }
}
