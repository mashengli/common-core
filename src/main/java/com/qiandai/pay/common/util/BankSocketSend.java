package com.qiandai.pay.common.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import org.apache.commons.io.IOUtils;

public class BankSocketSend {

    private static final String BANK_IP = "127.0.0.1";
    private static final int BANK_PORT = 8080;
    private static final String BIND_IP = "127.0.0.1";

    /**
     * Socket 发送-接收
     *
     * @param Message 要发送的信息
     * @return str 接收的信息
     */
    public static String Socket(String Message) {
        String str = null;
        Socket s = null;
        OutputStream os = null;
        InputStream is = null;
        try {
            s = new Socket();
            SocketAddress socketAddress = new InetSocketAddress(BANK_IP, BANK_PORT);
            SocketAddress bindpoint = new InetSocketAddress(BIND_IP, (Integer) null);
            s.bind(bindpoint);
            s.setSoTimeout(60 * 1000);
            s.connect(socketAddress);
            os = s.getOutputStream();
            is = s.getInputStream();
            //发送
            os.write(Message.getBytes());
            os.flush();
            //接收
            byte[] resmsg = new byte[1024];
            int msglen = is.read(resmsg);
            str = new String(resmsg, 0, msglen);
        } catch (Exception ignored) {
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(os);
            IOUtils.closeQuietly(s);
        }
        return str;
    }


}
