package com.qiandai.pay.common.security;

import java.io.FileInputStream;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Enumeration;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by mashengli on 2016/8/8.
 */
public class KeyUtil {

    /**
     * 以base64编码的私钥字符串生成私钥
     * @param base64PrivateKey 私钥字符串
     * @param algorithm 算法
     * @return
     * @throws Exception
     */
    public static PrivateKey getPrivateKey(String base64PrivateKey, String algorithm) throws Exception {
        byte[] privateKeyByte = Base64Util.decodeBase64(base64PrivateKey);
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(privateKeyByte);
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        return keyFactory.generatePrivate(pkcs8EncodedKeySpec);
    }

    /**
     * 以公钥字符串生成公钥
     * @param base64PublicKey 公钥字符串
     * @param algorithm 算法
     * @return
     * @throws Exception
     */
    public static PublicKey getPublicKey(String base64PublicKey, String algorithm) throws Exception {
        byte[] publicKeyByte = Base64Util.decodeBase64(base64PublicKey);
        X509EncodedKeySpec x509EncoderKeySpec = new X509EncodedKeySpec(publicKeyByte);
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        return keyFactory.generatePublic(x509EncoderKeySpec);
    }

    /**
     * 从私钥文件中提取私钥keyStore
     * @param privateKeyCert 私钥证书
     * @param priKeyPassword 私钥密码, 可不传
     * @return
     * @throws Exception
     */
    public static PrivateKey getPrivateKeyByCert(String privateKeyCert, String keyStorePassword, String priKeyPassword) throws Exception {
        char[] keyStorePass;
        char[] privateKeyPass;
        if (StringUtils.isBlank(keyStorePassword)) {
            keyStorePass = null;
        } else {
            keyStorePass = keyStorePassword.toCharArray();
        }
        if (StringUtils.isBlank(priKeyPassword)) {
            privateKeyPass = null;
        } else {
            privateKeyPass = priKeyPassword.toCharArray();
        }
        FileInputStream fis = new FileInputStream(privateKeyCert);
        KeyStore ks = KeyStore.getInstance("PKCS12");
        ks.load(fis, keyStorePass);
        Enumeration en = ks.aliases();

        String alias = null;
        for (; en.hasMoreElements(); ) {
            alias = en.nextElement().toString();
        }
        return  (PrivateKey) ks.getKey(alias, privateKeyPass);
    }

    /**
     * 从证书中提取公钥
     * @param publicKeyCert 公钥证书
     * @return
     * @throws Exception
     */
    public static PublicKey getPublicKeyByCert(String publicKeyCert) throws Exception {
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        FileInputStream fis = new FileInputStream(publicKeyCert);
        X509Certificate certificate = (X509Certificate)certificateFactory.generateCertificate(fis);
        return certificate.getPublicKey();
    }
}
