package pers.mashengli.common.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSONObject;

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

    public static String doPost(String url, Map<String, Object> sParaTemp, int timeout) {
        CloseableHttpClient httpClient = null;
        HttpPost httpPost = null;
        String result = null;
        try {
            System.out.println("url:" + url + "\r\nparams:" + JSONObject.toJSONString(sParaTemp));
            httpClient = HttpClients.createDefault();
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(timeout)
                    .setConnectTimeout(timeout)
                    .setConnectionRequestTimeout(timeout)
                    .build();
            httpPost = new HttpPost(url);
            httpPost.setConfig(requestConfig);
            //设置参数
            List<NameValuePair> list = new ArrayList<>();
            for (Object o : sParaTemp.entrySet()) {
                Map.Entry<String, Object> elem = (Map.Entry<String, Object>) o;
                list.add(new BasicNameValuePair(elem.getKey(), String.valueOf(elem.getValue())));
            }
            if (list.size() > 0) {
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, "UTF-8");
                httpPost.setEntity(entity);
            }
            CloseableHttpResponse response = httpClient.execute(httpPost);
            if (response != null) {
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    result = EntityUtils.toString(resEntity, "UTF-8");
                    System.out.println("response:" + result);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }
}
