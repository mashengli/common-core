package com.qiandai.pay.common.util;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.commons.io.IOUtils;

public class SimpleHttpServer {

    public static class HttpProcessor {
        public ServerSocket serverSocket;
        public HttpServer httpServer;
        private InputStream inputStream;
        public OutputStream outputStream;
        private int contentLength = 0;
        private String boundary = null;

        private static final byte[] b = new byte[1024 * 4];

        public String method;
        public String http_url;

        public HttpProcessor(ServerSocket serverSocket, HttpServer httpServer) {
            super();
            this.serverSocket = serverSocket;
            this.httpServer = httpServer;
        }

        public void service() {
            Socket socket = null;
            try {
                socket = serverSocket.accept();
                inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();
                parseRequest();
                DataInputStream reader = new DataInputStream(inputStream);
                if ("GET".equalsIgnoreCase(method)) {
                    this.doGet(inputStream);
                } else if ("POST".equalsIgnoreCase(method)) {
                    this.doPost(reader);
                }
                outputStream.flush();
                inputStream = null;
                outputStream = null;
            } catch (Exception ignored) {
            } finally {
                IOUtils.closeQuietly(socket);
            }
        }

        public void parseRequest() throws Exception {
            String request = streamReadLine(inputStream);
            String[] tokens = request.split(" ");
            method = tokens[0].toUpperCase();
            http_url = tokens[1];
        }

        private String streamReadLine(InputStream inputStream) throws IOException, InterruptedException {
            String data;
            int readCount = 0;
            while (true) {
                inputStream.read(b, readCount, 1);
                readCount++;
                if (b[readCount - 1] == '\n') {
                    break;
                }
                if (b[readCount - 1] == '\r') {
                    continue;
                }
                if (b[readCount - 1] == -1) {
                    Thread.sleep(1);
                }
            }
            data = new String(b);
            return data;
        }

        private void doGet(InputStream reader) throws Exception {
            httpServer.doGet(this);
        }

        private void doPost(DataInputStream reader) throws Exception {
            String line = reader.readLine();
            while (line != null) {
                System.out.println(line);
                line = reader.readLine();
                if ("".equals(line)) {
                    break;
                } else if (line.contains("Content-Length")) {
                    this.contentLength = Integer.parseInt(line.substring(line.indexOf("Content-Length") + 16));
                } else if (line.contains("multipart/form-data")) {
                    this.doMultiPart(reader);
                    return;
                }
            }
            String dataLine;
            byte[] buf;
            int size = 0;
            if (this.contentLength != 0) {
                buf = new byte[this.contentLength];
                while (size < this.contentLength) {
                    int c = reader.read();
                    buf[size++] = (byte) c;
                }
                dataLine = new String(buf, 0, size);
                httpServer.doPost(this, dataLine);
            }
        }

        private void doMultiPart(DataInputStream reader) throws IOException {
            System.out.println("doMultiPart ......");
            String line = reader.readLine();
            while (line != null) {
                System.out.println(line);
                line = reader.readLine();
                if ("".equals(line)) {
                    break;
                } else if (line.contains("Content-Length")) {
                    this.contentLength = Integer.parseInt(line.substring(line.indexOf("Content-Length") + 16));
                    System.out.println("contentLength: " + this.contentLength);
                } else if (line.contains("boundary")) {
                    this.boundary = line.substring(line.indexOf("boundary") + 9);
                }
            }

            if (this.contentLength != 0) {
                byte[] buf = new byte[this.contentLength];
                int totalRead = 0;
                int size;
                while (totalRead < this.contentLength) {
                    size = reader.read(buf, totalRead, this.contentLength - totalRead);
                    totalRead += size;
                }
                String dataString = new String(buf, 0, totalRead);
                System.out.println("the data user posted:/n" + dataString);
                int pos = dataString.indexOf(boundary);
                pos = dataString.indexOf("/n", pos) + 1;
                pos = dataString.indexOf("/n", pos) + 1;
                pos = dataString.indexOf("/n", pos) + 1;
                pos = dataString.indexOf("/n", pos) + 1;
                int start = dataString.substring(0, pos).getBytes().length;
                pos = dataString.indexOf(boundary, pos) - 4;
                int end = dataString.substring(0, pos).getBytes().length;
                int fileNameBegin = dataString.indexOf("filename") + 10;
                int fileNameEnd = dataString.indexOf("/n", fileNameBegin);
                String fileName = dataString.substring(fileNameBegin, fileNameEnd);
                if (fileName.lastIndexOf("//") != -1) {
                    fileName = fileName.substring(fileName.lastIndexOf("//") + 1);
                }
                fileName = fileName.substring(0, fileName.length() - 2);
                httpServer.doMultiPart(this, buf, fileName, start, end);
            }
        }

        public void writeSuccess() throws IOException {
            outputStream.write("HTTP/1.0 200 OK \r\n".getBytes());
        }

        public void writeFailure() throws IOException {
            outputStream.write("HTTP/1.0 404 File not found \r\n".getBytes());//test真的会报错
        }

    }

    public abstract class HttpServer {
        protected int port;

        public HttpServer(int port) {
            this.port = port;
        }

        public void listen() throws Exception {
            ServerSocket serverSocket = new ServerSocket(this.port);
            System.out.println("开始监听端口：" + port);
            final HttpProcessor processor = new HttpProcessor(serverSocket, this);

            Thread thread = new Thread() {
                public void run() {
                    try {
                        while (true) {
                            processor.service();
                        }
                    } catch (Exception ignored) {
                    }
                }
            };
            thread.start();
            thread.sleep(1);
        }

        public abstract void doGet(HttpProcessor p);

        public abstract void doPost(HttpProcessor p, String date);

        public abstract void doMultiPart(HttpProcessor p, byte[] inputData, String filename, int start, int end);

    }

}
