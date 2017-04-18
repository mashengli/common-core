package pers.mashengli.common.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.IOUtils;

/**
 * @author mashengli
 */
public class ZipUtil {

    public static final String FILE_SUFFIX = ".zip";

    /**
     * 将源文件/文件夹生成指定格式的压缩文件,格式zip
     * @param resourcePath 源文件/文件夹
     * @param targetPath   目的压缩文件保存路径
     * @param targetZipName   目的压缩文件名称
     * @return void
     */
    public static boolean compressed(String resourcePath, String targetPath, String targetZipName) {
        ZipOutputStream out = null;
        try {
            File resourcesFile = new File(resourcePath);
            if (StringUtils.isBlank(targetZipName)) {
                targetZipName = resourcesFile.getName() + FILE_SUFFIX;
            } else {
                targetZipName = targetZipName + (targetZipName.endsWith(FILE_SUFFIX) ? "" : FILE_SUFFIX);
            }
            File targetFile = new File(targetPath);
            if (!targetFile.exists()) {
                targetFile.mkdir();
            }
            targetFile = new File(targetPath, targetZipName);
            if (!targetFile.exists()) {
                targetFile.createNewFile();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(targetFile);
            out = new ZipOutputStream(new BufferedOutputStream(fileOutputStream));

            createCompressedFile(out, resourcesFile, "");
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            IOUtils.closeQuietly(out);
        }
    }

    /**
     * 生成压缩文件.如果是文件夹，则使用递归，进行文件遍历、压缩;如果是文件，直接压缩
     * @param out  输出流
     * @param file 目标文件
     * @throws Exception
     */
    public static void createCompressedFile(ZipOutputStream out, File file, String dir) throws Exception {
        //如果当前的是文件夹，则进行进一步处理
        if (file.isDirectory()) {
            //得到文件列表信息
            File[] files = file.listFiles();
            //将文件夹添加到下一级打包目录
            out.putNextEntry(new ZipEntry(dir + "/"));
            dir = StringUtils.isBlank(dir) ? "" : dir + "/";

            //循环将文件夹中的文件打包
            if (files != null) {
                for (File file1 : files) {
                    createCompressedFile(out, file1, dir + file1.getName());//递归处理
                }
            }
        } else {   //当前的是文件，打包处理
            //文件输入流
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
                out.putNextEntry(new ZipEntry(dir + file.getName()));
                //进行写操作
                IOUtils.copy(fis, out);
                out.flush();
            } finally {
                IOUtils.closeQuietly(fis);
            }
        }
    }
}
