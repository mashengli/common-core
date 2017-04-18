package pers.mashengli.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

/**
 * @author mashengli
 */
public class CurlUtil {

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url   发送请求的 URL
     * @param paramMap 请求参数
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, Map<String, Object> paramMap) throws Exception {
        List<String> cmds = new ArrayList<>();
        cmds.add("curl");
        cmds.add(url);
        cmds.add("-d");
        cmds.add(JSONObject.toJSONString(paramMap));
        String loggerStr = "";
        for (String cmd : cmds) {
            loggerStr += cmd;
        }
        System.out.println("command:" + loggerStr);
        StringBuilder result = new StringBuilder();
        ProcessBuilder pb = new ProcessBuilder(cmds);
        pb.redirectErrorStream(true);
        Process p;
        BufferedReader br = null;
        try {
            p = pb.start();
            String line;
            br = new BufferedReader(new InputStreamReader(p.getInputStream()));

            while ((line = br.readLine()) != null) {
                result.append(line);
            }
            br.close();
        } catch (Exception e) {
            //TODO
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ignored) {}
            }
        }
        String response = result.toString();
        response = response.substring(response.indexOf("{"), response.lastIndexOf("}") + 1);
        return response;
    }
}
