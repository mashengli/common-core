package pers.mashengli.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.io.IOUtils;

/**
 * @author mashengli
 */
public class HttpClientUtil {

    /**
     * 向指定URL发送GET方法的请求
     * @param url 发送请求的URL
     * @param sParaTemp 请求参数
     * @param charset 编码
     * @param timeout 超时时间，单位:毫秒
     * @return result 所代表远程资源的响应结果
     */
    public static String sendGet(String url, Map<String, Object> sParaTemp, String charset, int timeout) {
        String result = "";
        BufferedReader in = null;
        try {
            GetMethod getMethod = new GetMethod(url);
            String urlNameString = "";
            if (!sParaTemp.isEmpty()) {
                urlNameString = "?";
            }
            Set<Map.Entry<String, Object>> entrySet = sParaTemp.entrySet();
            for (Map.Entry<String, Object> entry : entrySet) {
                urlNameString = urlNameString + entry.getKey() + "=" + entry.getValue() + "&";
            }
            if (url.endsWith("&")) {
                urlNameString = urlNameString.substring(0, urlNameString.lastIndexOf("&"));
            }

            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("Accept-Charset", charset);
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            connection.setReadTimeout(timeout);
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(connection.getInputStream(), charset));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
        } finally {
            IOUtils.closeQuietly(in);
        }
        return result;
    }

    public static String doPost(String url, Map<String, Object> sParaTemp) {
        HttpClient httpClient = new HttpClient();

        PostMethod post = new PostMethod(url);
        post.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        List<String> keys = new ArrayList<String>(sParaTemp.keySet());
        NameValuePair[] param = new NameValuePair[keys.size() + 1];
        for (int i = 0; i < keys.size(); i++) {
            String name = keys.get(i);
            Object object = sParaTemp.get(name);
            String value = "";
            if (object != null) {
                value = String.valueOf(sParaTemp.get(name));
            }
            //添加参数
            param[i] = new NameValuePair(name, value);
            post.setParameter(param[i].getName(), param[i].getValue());
        }
        String response = null;
        try {
            httpClient.executeMethod(post);
            response = post.getResponseBodyAsString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        post.releaseConnection();
        return response;
    }
}
