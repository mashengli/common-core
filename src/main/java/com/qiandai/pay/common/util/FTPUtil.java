package com.qiandai.pay.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.poi.util.IOUtils;

/**
 * @author mashengli
 */
public class FTPUtil {

    public static final String FTP_FILE_SEPARATOR = "/";

    static String SERVER_CHARSET = "ISO-8859-1";

    private static long count = 0;

    /**
     * ftp最大目录层数为10
     */
    private static int FTP_LEVEL = 0;

    /**
     * 用户FTP账号登录
     *
     * @param url      FTP地址
     * @param port     FTP端口
     * @param username 用户名
     * @param password 密 码
     * @return true/false 成功/失败
     * @throws IOException
     */
    public static FTPClient login(String url, int port, String username, String password) throws IOException {
        FTPClient ftp = new FTPClient();
        int reply;
        ftp.connect(url, port);
        ftp.enterLocalPassiveMode();// 设置被动模式
        FTPClientConfig conf = new FTPClientConfig(FTPClientConfig.SYST_NT);
        conf.setServerLanguageCode("zh");
        ftp.login(username, password);
        reply = ftp.getReplyCode();
        FTP_LEVEL = 0;
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftp.disconnect();
            System.out.println(">>>>>>>>>>>>>>>>连接服务器失败!");
            return null;
        }
        System.out.println(">>>>>>>>>>>>>>>>>登陆服务器成功!");
        return ftp;
    }

    /**
     * 释放FTP
     */
    public static void close(FTPClient ftp) {
        FTP_LEVEL = 0;
        if (ftp != null) {
            if (ftp.isAvailable()) {
                try {
                    // 退出FTP
                    ftp.logout();
                } catch (IOException e) {
                    System.out.println("FTP登录退出异常:" + e.getMessage());
                }
            }
            if (ftp.isConnected()) {
                try {
                    // 断开连接
                    ftp.disconnect();
                } catch (IOException e) {
                    System.out.println("FTP断开连接异常:" + e.getMessage());
                }
            }
        }
    }

    private static boolean createAndCdDir(FTPClient ftp, String directory) throws IOException {
        if (StringUtils.isBlank(directory)) {
            return true;
        }
        String[] dirs = directory.split(FTP_FILE_SEPARATOR);
        for (String dir : dirs) {
            //目录不为空，防止出现//分割的目录路径
            if (!StringUtils.isBlank(dir)) {
                //目录不存在则创建
                dir = new String(dir.getBytes(), SERVER_CHARSET);
                if (!ftp.changeWorkingDirectory(dir)) {
                    ftp.makeDirectory(dir);
                    ftp.changeWorkingDirectory(dir);
                    FTP_LEVEL ++;
                }
            }
        }
        return true;
    }

    private static boolean changeToChildDirectory(FTPClient ftpClient, String dir) throws IOException {
        if (StringUtils.isBlank(dir)) {
            return true;
        }
        String[] paths = dir.split(FTPUtil.FTP_FILE_SEPARATOR);
        for (String path : paths) {
            if (StringUtils.isNotEmpty(path)) {
                if (ftpClient.changeWorkingDirectory(path) || ftpClient.changeWorkingDirectory(new String(path.getBytes(), SERVER_CHARSET))) {
                    FTP_LEVEL++;
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean changeToRootDirectory(FTPClient ftpClient) throws IOException {
        if (ftpClient != null && ftpClient.isConnected()) {
            while (FTP_LEVEL > 0) {
                if (ftpClient.changeToParentDirectory()) {
                    FTP_LEVEL --;
                }
            }
        }
        return true;
    }

    private static boolean changeToAbsoluteDirectory(FTPClient ftpClient, String dir) {
        try {
            changeToRootDirectory(ftpClient);
        } catch (Exception ignored) {}
        try {
            return changeToChildDirectory(ftpClient, dir);
        } catch (IOException e) {
            System.out.println("切换目录异常");
        }
        return false;
    }

    private static boolean isFileNameExists(FTPClient ftpClient, String remoteFileName) throws IOException {
        String[] fileNames = ftpClient.listNames();
        if (fileNames != null) {
            for (String fileName : fileNames) {
                if (new String(fileName.getBytes(SERVER_CHARSET)).equals(remoteFileName)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取文件名列表
     * @param url ftp地址
     * @param port ftp端口
     * @param username ftp登录名
     * @param password ftp密码
     * @param dir ftp路径
     * @return
     */
    public static String[] listNames(String url, int port, String username, String password, String dir) {
        FTPClient ftp = null;
        String[] names = null;
        try {
            ftp = login(url, port, username, password);
            if (ftp == null || !ftp.isConnected()) {
                System.out.println("无法登录");
                return null;
            }
            if (changeToChildDirectory(ftp, dir)) {
                String[] listNames = ftp.listNames();
                if (listNames != null && listNames.length > 0) {
                    names = new String[listNames.length];
                    for (int i = 0; i < listNames.length; i++) {
                        names[i] = new String(listNames[i].getBytes(SERVER_CHARSET));
                    }
                }
            }
            return names;
        } catch (Exception e) {
            System.out.println("访问ftp异常");
        } finally {
            close(ftp);
        }
        return null;
    }

    /**
     * FTP单文件上传
     *
     * @param url       FTP地址
     * @param port      FTP端口
     * @param username  FTP用户名
     * @param password  FTP密码
     * @param localPath  上传文件路径
     * @param remotePath 指定的FTP文件路径
     * @return
     */
    public static boolean uploadFile(String url, int port, String username, String password,
                              String localPath, String remotePath) {
        if (StringUtils.isBlank(localPath) || StringUtils.isBlank(remotePath)) {
            System.out.println("文件路径为空不能上传");
            return false;
        }
        String remoteDir = remotePath.substring(0, remotePath.lastIndexOf(FTP_FILE_SEPARATOR) + 1);
        String remoteFileName = remotePath.substring(remotePath.lastIndexOf(FTP_FILE_SEPARATOR) + 1);
        InputStream input = null;
        FTPClient ftp = null;
        try {
            ftp = login(url, port, username, password);
            if (ftp == null || !ftp.isConnected()) {
                System.out.println("无法登录，上传失败");
                return false;
            }
            System.out.println("用户登录成功，准备开始上传文件...");
            // 设置PassiveMode传输
            ftp.enterLocalPassiveMode();
            // 设置FTP文件类型为二进制，如果缺省该句 传输txt正常 但图片和其他格式的文件传输出现乱码
            ftp.setFileType(FTP.BINARY_FILE_TYPE);
            //创建远程文件目录
            if (!createAndCdDir(ftp, remoteDir)) {
                System.out.println("上传失败，原因是无法创建文件夹");
                return false;
            }
            //执行文件上传
            input = new FileInputStream(new File(localPath));
            boolean success = ftp.storeFile(new String(remoteFileName.getBytes(), SERVER_CHARSET), input);
            System.out.println("保存标识>>>" + success + "文件名称:" + localPath + (success ? "上传成功!" :
                    "上传失败!"));
            return success;
        } catch (IOException e) {
            System.out.println("访问ftp异常:" + e);
        } finally {
            IOUtils.closeQuietly(input);
            close(ftp);
        }
        return false;
    }

    /**
     * 下载文件到本地:
     * 若下载远程文件夹，请保证路径以分隔符"/"结尾否则将视为下载单文件
     * @param url ftp连接
     * @param port ftp端口号
     * @param username ftp登录名
     * @param password ftp登录密码
     * @param remotePath 远程文件路径
     * @param localPath 要下载的本地路径
     * @param fileSuffix 要下载的文件后缀，若为空则下载所有文件
     * @return
     */
    public static boolean retrieveFile(String url, int port, String username, String password,
                                      String remotePath, String localPath, String fileSuffix) {
        if (StringUtils.isBlank(localPath) || StringUtils.isBlank(remotePath)) {
            System.out.println("文件路径为空不能上传");
            return false;
        }
        FTPClient ftp = null;
        try {
            ftp = login(url, port, username, password);
            if (ftp == null || !ftp.isConnected()) {
                System.out.println("无法登录，上传失败");
                return false;
            }
            System.out.println("用户登录成功，准备开始下载文件...");
            ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
            if (!remotePath.endsWith(FTP_FILE_SEPARATOR)) {
                retrieveSingleFile(ftp, remotePath, localPath);
            } else {
                retrieveDir(ftp, remotePath, localPath, fileSuffix);
            }
            return true;
        } catch (IOException e) {
            System.out.println("访问ftp异常");
            return false;
        } finally {
            close(ftp);
        }
    }

    private static void retrieveDir(FTPClient ftp, String remotePath, String localPath, String fileSuffix) {
        try {
            changeToAbsoluteDirectory(ftp, remotePath);
            FTPFile[] files = ftp.listFiles();
            if (files != null) {
                for (FTPFile file : files) {
                    String fileName = new String(file.getName().getBytes(SERVER_CHARSET));
                    if (file.isDirectory()) {
                        retrieveDir(ftp, remotePath + fileName + FTP_FILE_SEPARATOR, localPath + fileName + FTP_FILE_SEPARATOR, fileSuffix);
                    } else if (StringUtils.isBlank(fileSuffix) || (!StringUtils.isBlank(fileSuffix) && fileName.endsWith(fileSuffix))) {
                        File localFile = new File(localPath + FTP_FILE_SEPARATOR + fileName);
                        if (!localFile.getParentFile().exists()) {
                            localFile.getParentFile().mkdirs();
                        }
                        OutputStream os = null;
                        try {
                            os = new FileOutputStream(localFile);
                            ftp.retrieveFile(file.getName(), os);
                        } finally {
                            IOUtils.closeQuietly(os);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("访问ftp异常");
        }
    }

    private static boolean retrieveSingleFile(FTPClient ftp, String remotePath, String localPath) {
        String remoteDir = remotePath.substring(0, remotePath.lastIndexOf(FTP_FILE_SEPARATOR) + 1);
        String remoteFileName = remotePath.substring(remotePath.lastIndexOf(FTP_FILE_SEPARATOR) + 1);
        if (localPath.endsWith(FTPUtil.FTP_FILE_SEPARATOR)) {
            localPath = localPath + remoteFileName;
        }
        OutputStream os = null;
        try {
            if (changeToChildDirectory(ftp, remoteDir)) {
                FTPFile[] files = ftp.listFiles();
                for (FTPFile ftpFile : files) {
                    String ftpFileName = new String(ftpFile.getName().getBytes(SERVER_CHARSET));
                    if (ftpFileName.equals(remoteFileName)) {
                        os = new FileOutputStream(new File(localPath));
                        ftp.retrieveFile(ftpFileName, os);
                        os.flush();
                    }
                }
                return true;
            }
            return false;
        } catch (IOException e) {
            System.out.println("访问ftp异常:");
        } finally {
            IOUtils.closeQuietly(os);
        }
        return false;
    }

    /**
     * 下载FTP单文件
     * @param url ftp地址
     * @param port 端口
     * @param username ftp登录用户名
     * @param password ftp登录密码
     * @param remotePath 远程文件全路径,不支持下载文件夹或多文件
     * @param response http请求
     * @return
     */
    public static void downloadFile(String url, int port, String username, String password,
                                String remotePath, HttpServletResponse response) throws Exception {
        if (StringUtils.isBlank(remotePath)) {
            throw new Exception("文件路径为空");
        }
        String remoteDir = remotePath.substring(0, remotePath.lastIndexOf(FTP_FILE_SEPARATOR) + 1);
        String remoteFileName = remotePath.substring(remotePath.lastIndexOf(FTP_FILE_SEPARATOR) + 1);
        OutputStream os = null;
        FTPClient ftp = null;
        try {
            ftp = login(url, port, username, password);
            if (ftp == null || !ftp.isConnected()) {
                throw new IOException("无法登录");
            }
            System.out.println("用户登录成功，准备开始下载文件...");
            if (!changeToChildDirectory(ftp, remoteDir)) {
                throw new Exception("文件不存在");
            }
            FTPFile[] ftpFiles = ftp.listFiles();
            boolean status = false;
            for (FTPFile file : ftpFiles) {
                String fileName = new String(file.getName().getBytes(SERVER_CHARSET));
                if (fileName.equals(remoteFileName)) {
                    status = true;
                    os = response.getOutputStream();
                    ftp.retrieveFile(file.getName(), os);
                    os.flush();
                }
            }
            if (!status) {
                throw new Exception("文件不存在");
            }
        } catch (Exception e) {
            System.out.println("访问ftp异常:");
            throw e;
        } finally {
            IOUtils.closeQuietly(os);
            close(ftp);
        }
    }

    /**
     * 判断文件是否存在
     * @param url ftp地址
     * @param port ftp端口
     * @param username ftp登录名
     * @param password ftp登录密码
     * @param remotePath 文件全路径
     * @return
     */
    public static boolean isFileExists(String url, int port, String username, String password,
                                       String remotePath) throws Exception {
        if (StringUtils.isBlank(remotePath)) {
            return false;
        }
        String remoteDir = remotePath.substring(0, remotePath.lastIndexOf(FTP_FILE_SEPARATOR) + 1);
        String remoteFileName = remotePath.substring(remotePath.lastIndexOf(FTP_FILE_SEPARATOR) + 1);
        FTPClient ftp = null;
        try {
            ftp = login(url, port, username, password);
            if (ftp == null || !ftp.isConnected()) {
                System.out.println("无法登录");
                throw new Exception("无法登录");
            }
            System.out.println("用户登录成功，准备解析文件...");
            boolean changeSuccess = changeToChildDirectory(ftp, remoteDir);
            if (!changeSuccess) {
                return false;
            }
            return isFileNameExists(ftp, remoteFileName);
        } catch (IOException e) {
            System.out.println("访问ftp异常:");
        } finally {
            close(ftp);
        }
        return false;
    }

    /**
     * 在指定文件夹内统计指定后缀的文件数量
     * @param url ftp地址
     * @param port ftp端口
     * @param username ftp登录名
     * @param password ftp密码
     * @param remotePath 远程路径
     * @param fileSuffix 文件后缀，为空表示所有非文件夹文件
     * @return
     * @throws Exception
     */
    public static long countFiles(String url, int port, String username, String password,
                                  String remotePath, String fileSuffix) throws Exception {
        if (StringUtils.isBlank(remotePath)) {
            return 0;
        }
        FTPClient ftp = null;
        try {
            ftp = login(url, port, username, password);
            if (ftp == null || !ftp.isConnected()) {
                System.out.println("无法登录");
                throw new Exception("无法登录");
            }
            System.out.println("用户登录成功，准备开始解析文件...");
            count = 0L;
            countFiles(ftp, remotePath, fileSuffix);
            return count;
        } finally {
            close(ftp);
        }
    }

    /**
     * 删除远程文件
     * @param url ftp地址
     * @param port ftp端口
     * @param username ftp登录名
     * @param password ftp密码
     * @param remotePath 远程路径
     * @return
     */
    public static boolean deleteFile(String url, int port, String username, String password, String remotePath) {
        if (StringUtils.isBlank(remotePath)) {
            return true;
        }
        String remoteDir = remotePath.substring(0, remotePath.lastIndexOf(FTP_FILE_SEPARATOR) + 1);
        String remoteFileName = remotePath.substring(remotePath.lastIndexOf(FTP_FILE_SEPARATOR) + 1);
        FTPClient ftp = null;
        try {
            ftp = login(url, port, username, password);
            if (ftp == null || !ftp.isConnected()) {
                System.out.println("无法登录");
                return false;
            }
            System.out.println("用户登录成功，准备开始删除文件...");
            if (changeToChildDirectory(ftp, remoteDir)) {
                if (!StringUtils.isBlank(remoteFileName)) {
                    return ftp.deleteFile(new String(remoteFileName.getBytes(), SERVER_CHARSET));
                } else {
                    FTPFile[] ftpFiles = ftp.listFiles();
                    boolean result = true;
                    for (FTPFile file  :ftpFiles) {
                        if (!ftp.deleteFile(file.getName())) {
                            result = false;
                        }
                    }
                    if (ftp.changeToParentDirectory()) {
                        remoteDir = remoteDir.substring(0, remoteDir.lastIndexOf(FTP_FILE_SEPARATOR));
                        ftp.removeDirectory(remoteDir.substring(remoteDir.lastIndexOf(FTP_FILE_SEPARATOR) + 1));
                    }
                    return result;
                }
            }
            return false;
        } catch (Exception e) {
            System.out.println("访问ftp异常");
        } finally {
            close(ftp);
        }
        return false;
    }

    private static void countFiles(FTPClient ftpClient, String remotePath, String fileSuffix) {
        if (changeToAbsoluteDirectory(ftpClient, remotePath)) {
            try {
                FTPFile[] files = ftpClient.listFiles();
                if (files != null) {
                    for (FTPFile file : files) {
                        try {
                            if (file.isDirectory()) {
                                countFiles(ftpClient, remotePath + new String(file.getName().getBytes(SERVER_CHARSET)) + FTP_FILE_SEPARATOR, fileSuffix);
                            } else {
                                String fileName = new String(file.getName().getBytes(SERVER_CHARSET));
                                if (StringUtils.isBlank(fileSuffix) || fileName.endsWith(fileSuffix)) {
                                    count++;
                                }
                            }
                        } catch (UnsupportedEncodingException e) {
                            System.out.println("无法识别的文字编码");
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("ftp解析异常");
            }
        }
    }

}
