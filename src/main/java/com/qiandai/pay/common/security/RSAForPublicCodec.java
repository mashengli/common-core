package com.qiandai.pay.common.security;

import java.io.FileInputStream;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

/**
 * RSA 非对称加密，持有公钥的乙方
 *
 * @author linling
 */
public class RSAForPublicCodec extends BasicCodec {

    private static final String ALGORITHM = "RSA";
    private static final String SIGN_ALGORITHM = "SHA1withRSA";

    public RSAForPublicCodec(String publicKey) {
        super();
        super.publicKey = publicKey;
    }

    /*
     * public static void main(String[] args) throws Exception {
     * CertificateFactory
     * certificatefactory=CertificateFactory.getInstance("X.509");
     * FileInputStream bais=new FileInputStream("d:/ump.cer"); X509Certificate
     * Cert = (X509Certificate)certificatefactory.generateCertificate(bais);
     * PublicKey pk = Cert.getPublicKey(); // BASE64Encoder bse=new
     * BASE64Encoder(); //
     * System.out.println("pk:"+Base64Utils.encoder(pk.getEncoded())); String
     * publickey = Base64Utils.encoder(pk.getEncoded()); RSAForPublicCodec
     * rsa_pub = new RSAForPublicCodec(publickey);
     * System.out.println(Base64Utils
     * .encoder(rsa_pub.encrypt("1234".getBytes())));
     *
     * }
     */
    public static String getpublickey(String filepath) throws Exception {
        CertificateFactory certificatefactory = CertificateFactory
                .getInstance("X.509");
        FileInputStream bais = new FileInputStream(filepath);
        X509Certificate Cert = (X509Certificate) certificatefactory
                .generateCertificate(bais);
        PublicKey pk = Cert.getPublicKey();
        return Base64Utils.encoder(pk.getEncoded());
    }

    @Override
    public byte[] encrypt(byte[] data) throws Exception {
        if (publicKey == null || "".equals(publicKey)) {
            throw new Exception("publicKey does not exist");
        }
        PublicKey rsaPublicKey = getRSAPublicKey(publicKey);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, rsaPublicKey);
        return cipher.doFinal(data);
    }

    @Override
    public byte[] decrypt(byte[] data) throws Exception {
        if (publicKey == null || "".equals(publicKey)) {
            throw new Exception("publicKey does not exist");
        }

        PublicKey rsaPublicKey = getRSAPublicKey(publicKey);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, rsaPublicKey);
        return cipher.doFinal(data);
    }

    private PublicKey getRSAPublicKey(String key) throws Exception {
        X509EncodedKeySpec x509EncoderKeySpec = new X509EncodedKeySpec(
                decoder(key));
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        return keyFactory.generatePublic(x509EncoderKeySpec);
    }

    /**
     * 使用公钥校验签名
     *
     * @param data
     * @param sign
     * @return
     * @throws Exception
     */
    public boolean verifySign(byte[] data, String sign) throws Exception {
        if (publicKey == null || "".equals(publicKey)) {
            throw new Exception("publicKey does not exist");
        }

        PublicKey rsaPublicKey = getRSAPublicKey(publicKey);
        Signature signature = Signature.getInstance(SIGN_ALGORITHM);
        signature.initVerify(rsaPublicKey);
        signature.update(data);
        return signature.verify(decoder(sign));
    }

}
