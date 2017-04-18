package pers.mashengli.common.security;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

/**
 * Created by mashengli on 2016/8/8.
 */
public class RSAUtil {

    /**
     * 算法
     */
    private static final String ALGORITHM = "RSA";

    /**
     * 签名算法
     */
    private static final String SIGN_ALGORITHM = "SHA1withRSA";

    /**
     * 字符编码
     */
    private static final String CHARSET = "UTF-8";

    /**
     * 公钥名称
     */
    private static final String PUBLIC_KEY = "RSAPublicKey";

    /**
     * 私钥名称
     */
    private static final String PRIVATE_KEY = "RSAPrivateKey";

    /**
     * RSA密钥长度
     */
    private static final int KEY_SIZE = 1024;

    public static Map<String, Object> initKey() throws Exception {
        //实例化密钥生成器
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
        //初始化密钥生成器
        keyPairGenerator.initialize(KEY_SIZE);
        //生成密钥对
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        //公钥
        PublicKey publicKey = keyPair.getPublic();
        //私钥
        PrivateKey privateKey = keyPair.getPrivate();
        //将密钥存储在map中
        Map<String, Object> keyMap = new HashMap<>();
        keyMap.put(PUBLIC_KEY, publicKey);
        keyMap.put(PRIVATE_KEY, privateKey);
        return keyMap;
    }

    /**
     * 获取公钥
     *
     * @param keyMap
     * @return
     */
    public static String getPublicKey(Map<String, Object> keyMap) {
        Key key = (Key) keyMap.get(PUBLIC_KEY);
        return Base64Util.encodeBase64String(key.getEncoded());
    }

    /**
     * 获取私钥
     *
     * @param keyMap
     * @return
     */
    public static String getPrivateKey(Map<String, Object> keyMap) {
        Key key = (Key) keyMap.get(PRIVATE_KEY);
        return Base64Util.encodeBase64String(key.getEncoded());
    }

    /**
     * 使用公钥文件加密
     *
     * @param data          原文字符串
     * @param publicKeyCert 公钥文件
     * @return 公钥加密+Base64编码后的密文
     */
    public static String encryptByPublicKeyCert(String data, String publicKeyCert) throws Exception {
        PublicKey publicKey = KeyUtil.getPublicKeyByCert(publicKeyCert);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] result = cipher.doFinal(data.getBytes(CHARSET));
        return Base64Util.encodeBase64String(result);
    }

    /**
     * 使用公钥串进行加密
     *
     * @param data            原文字符串
     * @param base64PublicKey Base64编码的公钥字符串
     * @return 公钥加密+Base64编码后的字符串
     * @throws Exception
     */
    public static String encryptByPublicKey(String data, String base64PublicKey) throws Exception {
        PublicKey publicKey = KeyUtil.getPublicKey(base64PublicKey, ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] result = cipher.doFinal(data.getBytes(CHARSET));
        return Base64Util.encodeBase64String(result);
    }

    /**
     * 使用私钥文件解密
     *
     * @param encryptData    Base64编码的密文
     * @param privateKeyCert 私钥文件
     * @return 解密后的原文
     */
    public static String decryptByPrivateKeyCert(String encryptData, String privateKeyCert, String keyStorePassword, String keyPassword) throws Exception {
        PrivateKey privateKey = KeyUtil.getPrivateKeyByCert(privateKeyCert, keyStorePassword, keyPassword);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] source = Base64Util.decodeBase64(encryptData);
        byte[] result = cipher.doFinal(source);
        return new String(result, CHARSET);
    }

    /**
     * 使用私钥字符串解密
     *
     * @param encryptData      Base64编码的密文
     * @param base64PrivateKey Base64编码后的私钥串
     * @return 解密后的原文
     * @throws Exception
     */
    public static String decryptByPrivateKey(String encryptData, String base64PrivateKey) throws Exception {
        PrivateKey privateKey = KeyUtil.getPrivateKey(base64PrivateKey, ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] result = cipher.doFinal(Base64Util.decodeBase64(encryptData));
        return new String(result, CHARSET);
    }

    /**
     * 使用私钥签名
     *
     * @param data             原文
     * @param base64PrivateKey Base64编码的私钥串
     * @return ase64编码的签名字符串
     */
    public static String signByPrivateKey(String data, String base64PrivateKey) throws Exception {
        PrivateKey privateK = KeyUtil.getPrivateKey(base64PrivateKey, ALGORITHM);
        Signature signature = Signature.getInstance(SIGN_ALGORITHM);
        signature.initSign(privateK);
        signature.update(data.getBytes(CHARSET));
        return Base64Util.encodeBase64String(signature.sign());
    }

    /**
     * 使用私钥文件进行签名
     *
     * @param data           原文
     * @param privateKeyCert 私钥文件
     * @param keyStorePassword keyStore密码，可为空
     * @param privateKeyPassword   私钥密码，可为空
     * @return Base64编码的签名字符串
     * @throws Exception
     */
    public static String signByPrivateKeyCert(String data, String privateKeyCert, String keyStorePassword, String privateKeyPassword) throws Exception {
        PrivateKey key = KeyUtil.getPrivateKeyByCert(privateKeyCert, keyStorePassword, privateKeyPassword);
        Signature signature = Signature.getInstance(SIGN_ALGORITHM);
        signature.initSign(key);
        signature.update(data.getBytes(CHARSET));
        return Base64Util.encodeBase64String(signature.sign());
    }

    /**
     * 使用公钥串进行验签
     *
     * @param data            待验签的原文
     * @param base64PublicKey 公钥串
     * @param sign            Base64编码的签名字符串
     * @return
     * @throws Exception
     */
    public static boolean verifyByPublicKey(String data, String base64PublicKey, String sign) throws Exception {
        PublicKey publicKey = KeyUtil.getPublicKey(base64PublicKey, ALGORITHM);
        Signature signature = Signature.getInstance(SIGN_ALGORITHM);
        signature.initVerify(publicKey);
        signature.update(data.getBytes(CHARSET));
        return signature.verify(Base64Util.decodeBase64(sign));
    }

    /**
     * 使用公钥文件进行验签
     *
     * @param data          待验签的原文
     * @param publicKeyCert 公钥文件
     * @param sign          Base64编码的签名字符串
     * @return
     * @throws Exception
     */
    public static boolean verifyByPublicKeyCert(String data, String publicKeyCert, String sign) throws Exception {
        PublicKey publicKey = KeyUtil.getPublicKeyByCert(publicKeyCert);
        Signature signature = Signature.getInstance(SIGN_ALGORITHM);
        signature.initVerify(publicKey);
        signature.update(data.getBytes(CHARSET));
        return signature.verify(Base64Util.decodeBase64(sign));
    }

    public static void main(String[] args) throws Exception {
        Map<String, Object> keyMap = initKey();
        String privateKey = getPrivateKey(keyMap);
        String publicKey = getPublicKey(keyMap);
        System.out.println("\n私钥:" + privateKey);
        System.out.println("\n公钥:" + publicKey);

        ObjectOutputStream oos1 = new ObjectOutputStream(new FileOutputStream(("d://privatekey")));
        ObjectOutputStream oos2 = new ObjectOutputStream(new FileOutputStream(("d://publickey")));
        oos1.writeObject(keyMap.get(PRIVATE_KEY));
        oos1.flush();
        oos1.close();

        oos2.writeObject(keyMap.get(PUBLIC_KEY));
        oos2.flush();
        oos2.close();
        String data = "data=2016-08-08 13:57:00&payMoney=1000";
        System.out.println("明文:" + data);
        System.out.println("公钥加密密文:");
        String encodeStr = encryptByPublicKey(data, publicKey);
        System.out.println(encodeStr);
        System.out.println("私钥解密明文:");
        System.out.println(decryptByPrivateKey(encodeStr, privateKey));

        System.out.println("私钥签名:");
        String signStr = signByPrivateKey(data, privateKey);
        System.out.println(signStr);
        System.out.println("公钥验签:");
        System.out.println(verifyByPublicKey(data, publicKey, signStr));
    }

}
