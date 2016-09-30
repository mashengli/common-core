package com.qiandai.pay.common.security;

import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mashengli on 2016/8/8.
 */
public class DSAUtil {
    public static final String ALGORITHM = "DSA";
    public static final String SIGN_ALGORITHM = "DSA";
    private static final String CHARSET = "UTF-8";

    /**
     * 公钥名称
     */
    private static final String PUBLIC_KEY = "DSAPublicKey";

    /**
     * 私钥名称
     */
    private static final String PRIVATE_KEY = "DSAPrivateKey";

    /**
     * DSA默认种子
     */
    private static final String DEFAULT_SEED = "$%^*%^()(HJG8awf34jas7ak2js434hdfAJ5KE4BMN12487asdLS123awKJWSNKL654@#$%#$^@EASDF2387bakmn983w";

    /**
     * 密钥长度
     */
    private static final int KEY_SIZE = 1024;

    /**
     * 按照默认种子初始化公私钥
     * @return
     * @throws Exception
     */
    public static Map<String, Object> initKey() throws Exception {
        return initKey(DEFAULT_SEED);
    }

    /**
     * 生成密钥
     * @param seed 种子
     * @return 密钥对象
     * @throws Exception
     */
    public static Map<String, Object> initKey(String seed) throws Exception {
        KeyPairGenerator keygen = KeyPairGenerator.getInstance(ALGORITHM);
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.setSeed(seed.getBytes());
        keygen.initialize(KEY_SIZE, secureRandom);

        KeyPair keys = keygen.genKeyPair();
        PrivateKey privateKey = keys.getPrivate();
        PublicKey publicKey = keys.getPublic();

        Map<String, Object> map = new HashMap<>();
        map.put(PUBLIC_KEY, publicKey);
        map.put(PRIVATE_KEY, privateKey);
        return map;
    }

    /**
     * 获取公钥
     * @param keyMap
     * @return
     */
    public static String getPublicKey(Map<String, Object> keyMap) {
        Key key=(Key) keyMap.get(PUBLIC_KEY);
        return Base64Util.encodeBase64String(key.getEncoded());
    }

    /**
     * 获取私钥
     * @param keyMap
     * @return
     */
    public static String getPrivateKey(Map<String, Object> keyMap) {
        Key key=(Key) keyMap.get(PRIVATE_KEY);
        return Base64Util.encodeBase64String(key.getEncoded());
    }

    /**
     * 用私钥对信息进行数字签名
     *
     * @param data          原文
     * @param base64PrivateKey base64加密的私钥串
     * @return
     * @throws Exception
     */
    public static String signByPrivateKey(String data, String base64PrivateKey) throws Exception {
        PrivateKey privateKey = KeyUtil.getPrivateKey(base64PrivateKey, ALGORITHM);

        //用私钥对信息进行数字签名
        Signature signature = Signature.getInstance(SIGN_ALGORITHM);
        signature.initSign(privateKey);
        signature.update(data.getBytes(CHARSET));
        return Base64Util.encodeBase64String(signature.sign());
    }

    /**
     * 使用私钥文件进行签名
     *
     * @param data           原文
     * @param privateKeyCert 私钥文件
     * @return Base64编码的签名字符串
     * @throws Exception
     */
    public static String signByPrivateKeyCert(String data, String privateKeyCert, String keyStorePassword, String privateKeyPassword) throws Exception {
        PrivateKey key = KeyUtil.getPrivateKeyByCert(privateKeyCert, keyStorePassword, privateKeyPassword);
        Signature signature = Signature.getInstance(ALGORITHM);
        signature.initSign(key);
        signature.update(data.getBytes(CHARSET));
        return Base64Util.encodeBase64String(signature.sign());
    }

    /**
     * 使用公钥串进行验签
     *
     * @param data         待验签的原文
     * @param base64PublicKey 公钥串
     * @param sign         Base64编码的签名字符串
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

        String data = "data=2016-08-08 13:57:00&payMoney=1000";
        System.out.println("明文:" + data);
//        System.out.println("公钥加密密文:");
//        String encodeStr = RSAUtil.encryptByPublicKey(data, publicKey);
//        System.out.println(encodeStr);
//        System.out.println("私钥解密明文:");
//        System.out.println(RSAUtil.decryptByPrivateKey(encodeStr, privateKey));

        System.out.println("私钥签名:");
        String signStr = signByPrivateKey(data, privateKey);
        System.out.println(signStr);
        System.out.println("公钥验签:");
        System.out.println(verifyByPublicKey(data, publicKey, signStr));
    }

}
