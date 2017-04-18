package pers.mashengli.common.security;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by mashengli on 2016/8/23.
 */
public class AESUtil {
    private String secretKey;

    private static final String ALGORITHM = "AES";
    private static final int KEY_SIZE = 128;
    private static final String CHARSET = "UTF-8";

    public AESUtil() throws NoSuchAlgorithmException {
        initKey();
    }

    public AESUtil(String secretKey) {
        this.secretKey = secretKey;
    }

    private void initKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
        SecureRandom secureRandom = new SecureRandom();
        keyGenerator.init(KEY_SIZE, secureRandom);
        SecretKey aesKey = keyGenerator.generateKey();
        this.secretKey = Base64Util.encodeBase64String(aesKey.getEncoded());
    }

    public String getSecretKey() {
        return secretKey;
    }

    /**
     * 加密
     * @param data 加密数据
     * @param base64AESKey aes密钥
     * @return base64编码后的密文
     * @throws Exception
     */
    public static String encrypt(String data, String base64AESKey) throws Exception {
        if (StringUtils.isBlank(base64AESKey)) {
            throw new Exception("scretKey need to exists");
        }

        SecretKeySpec secretKeySpec = new SecretKeySpec(Base64Util.decodeBase64(base64AESKey), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        return Base64Util.encodeBase64String(cipher.doFinal(data.getBytes(CHARSET)));
    }

    /**
     * 加密
     * @param data 加密数据
     * @param base64AESKey 密钥
     * @return 密文
     * @throws Exception
     */
    public static byte[] encrypt(byte[] data, String base64AESKey) throws Exception {
        if (StringUtils.isBlank(base64AESKey)) {
            throw new Exception("scretKey need to exists");
        }

        SecretKeySpec secretKeySpec = new SecretKeySpec(Base64Util.decodeBase64(base64AESKey), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        return cipher.doFinal(data);
    }

    /**
     * 解密
     * @param encryptData base64编码的密文
     * @param base64AESKey 密钥
     * @return 原文
     * @throws Exception
     */
    public static String decrypt(String encryptData, String base64AESKey) throws Exception {
        if (StringUtils.isBlank(base64AESKey)) {
            throw new Exception("scretKey need to exists");
        }

        SecretKeySpec secretKeySpec = new SecretKeySpec(Base64Util.decodeBase64(base64AESKey), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        return new String(cipher.doFinal(Base64Util.decodeBase64(encryptData)), CHARSET);
    }

    /**
     * 解密
     * @param encryptData 密文
     * @param base64AESKey 密钥
     * @return 原文
     * @throws Exception
     */
    public static byte[] decrypt(byte[] encryptData, String base64AESKey) throws Exception {
        if (StringUtils.isBlank(base64AESKey)) {
            throw new Exception("scretKey need to exists");
        }

        SecretKeySpec secretKeySpec = new SecretKeySpec(Base64Util.decodeBase64(base64AESKey), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        return cipher.doFinal(encryptData);
    }

    public static void main(String[] args) {
        try {
            AESUtil aesUtil = new AESUtil();
            System.out.println("密钥：" + aesUtil.getSecretKey());

            String message = "测试";
            System.out.println("原文：" + message);
            String encode = AESUtil.encrypt(message, aesUtil.getSecretKey());
            System.out.println("密文：" + encode);
            System.out.println("解密：" + AESUtil.decrypt(encode, aesUtil.getSecretKey()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
