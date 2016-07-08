package com.qiandai.pay.common.security;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.crypto.Cipher;

/**
 * RSA 非对称加密，持有私钥的甲方
 *
 * @author linling
 */
public class RSAForPrivateCodec extends BasicCodec {
    private static final String ALGORITHM = "RSA";

    //rsa，签名算法可以是 md5withrsa 、 sha1withrsa 、 sha256withrsa 、 sha384withrsa 、 sha512withrsa
    private static final String SIGN_ALGORITHM = "SHA1withRSA";
    private static final int KEY_SIZE = 1024;

    public RSAForPrivateCodec() throws NoSuchAlgorithmException {
        super();
        initKey();
    }

    @Override
    public byte[] encrypt(byte[] data) throws Exception {
        PrivateKey rsaPrivateKey = getRSAPrivateKey();
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, rsaPrivateKey);
        return cipher.doFinal(data);
    }

    @Override
    public byte[] decrypt(byte[] data) throws Exception {
        PrivateKey rsaPrivateKey = getRSAPrivateKey();
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, rsaPrivateKey);
        return cipher.doFinal(data);
    }

    /**
     * 初始化私钥和公钥
     *
     * @throws NoSuchAlgorithmException
     */
    private void initKey() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
        keyPairGenerator.initialize(KEY_SIZE);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        PrivateKey rsaPrivateKey = keyPair.getPrivate();
        PublicKey rsaPublicKey = keyPair.getPublic();
        super.privateKey = encoder(rsaPrivateKey.getEncoded());
        super.publicKey = encoder(rsaPublicKey.getEncoded());
    }

    private PrivateKey getRSAPrivateKey() throws Exception {
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(decoder(super.privateKey));
        KeyFactory keyFacotry = KeyFactory.getInstance(ALGORITHM);
        return keyFacotry.generatePrivate(pkcs8EncodedKeySpec);
    }

    /**
     * 使用私钥 对数据进行签名
     *
     * @param data
     * @return
     * @throws Exception
     */
    public String sign(byte[] data) throws Exception {
        PrivateKey rsaPrivateKey = getRSAPrivateKey();
        Signature signature = Signature.getInstance(SIGN_ALGORITHM);
        signature.initSign(rsaPrivateKey);
        signature.update(data);
        return encoder(signature.sign());
    }
}
