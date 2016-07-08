package com.qiandai.pay.common.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5 单向加密
 *
 * @author linling
 */
public class MD5Codec extends BasicCodec {
    private static final String ALGORITHM = "MD5";

    @Override
    public byte[] encrypt(byte[] data) throws Exception {
        MessageDigest messageDigest = MessageDigest.getInstance(ALGORITHM);
        return messageDigest.digest(data);
    }

    public String encrypt(String data) throws Exception {
        byte[] bytedata = data.getBytes();
        return new String(encrypt(bytedata));
    }

    @Override
    public byte[] decrypt(byte[] data) throws Exception {
        return null;
    }

    /**
     * 返回MD5单向加密后的十六进制字符串
     * @param data
     * @return
     * @throws Exception
     */
    public String getEncryptForHex(byte[] data) throws Exception {
        byte[] digestData = encrypt(data);
        return parseByteArray2HexStr(digestData);
    }

    /**
     * get the md5 hash of a string
     * @param str
     * @return
     * @throws Exception
     */
    public static String md5(String str) throws Exception {
        if (str == null) {
            return null;
        }
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance(ALGORITHM);
            messageDigest.reset();
            messageDigest.update(str.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            return str;
        }

        byte[] byteArray = messageDigest.digest();
        StringBuilder md5StrBuff = new StringBuilder();
        for (byte aByteArray : byteArray) {
            if (Integer.toHexString(0xFF & aByteArray).length() == 1) {
                md5StrBuff.append("0").append(Integer.toHexString(0xFF & aByteArray));
            } else {
                md5StrBuff.append(Integer.toHexString(0xFF & aByteArray));
            }
        }
        return md5StrBuff.toString();
    }

}
