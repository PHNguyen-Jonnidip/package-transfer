package com.nguyenphuong.PackageTransfer.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

public class CryptoUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(CryptoUtils.class);

  private static final byte[] PBE_SALT = {
      (byte) 0xA9, (byte) 0x9B, (byte) 0xC8, (byte) 0x32,
      (byte) 0x56, (byte) 0x35, (byte) 0xE3, (byte) 0x03
  };
  private static final int ITERATION_COUNT = 19;

  private static String ENCRYPTION_KEY;

  @Autowired
  public void setEncryptionKey(@Value("${crypto.credential.encryption-key}") String encryptionKey) {
    CryptoUtils.ENCRYPTION_KEY = encryptionKey;
  }

  private static String SALT;

  @Autowired
  public void setSalt(@Value("${crypto.credential.salt}") String salt) {
    CryptoUtils.SALT = salt;
  }

  public static String encrypt(String credential) {
    if (null == credential || credential.isEmpty()) {
      return null;
    }
    KeySpec keySpec = new PBEKeySpec(ENCRYPTION_KEY.toCharArray(), PBE_SALT, ITERATION_COUNT);

    try {
      SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);
      AlgorithmParameterSpec paramSpec = new PBEParameterSpec(PBE_SALT, ITERATION_COUNT);

      Cipher encodeCipher = Cipher.getInstance(key.getAlgorithm());
      encodeCipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
      String charSet = "UTF-8";
      String credentialWithSalt = SALT.concat(credential);
      byte[] in = credentialWithSalt.getBytes(charSet);
      byte[] out = encodeCipher.doFinal(in);
      return new String(Base64.getEncoder().encode(out));
    } catch (InvalidKeySpecException | NoSuchAlgorithmException | NoSuchPaddingException
             | InvalidKeyException | InvalidAlgorithmParameterException |
             UnsupportedEncodingException
             | IllegalBlockSizeException | BadPaddingException e) {
      LOGGER.error("Error when encrypt credential");
      throw new IllegalArgumentException("Error when encrypt credential", e);
    }
  }

  public static String decrypt(String credential) {
    if (null == credential) {
      return null;
    }

    KeySpec keySpec = new PBEKeySpec(ENCRYPTION_KEY.toCharArray(), PBE_SALT, ITERATION_COUNT);
    try {
      SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);
      AlgorithmParameterSpec paramSpec = new PBEParameterSpec(PBE_SALT, ITERATION_COUNT);
      Cipher decodeCipher = Cipher.getInstance(key.getAlgorithm());
      decodeCipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
      byte[] enc = Base64.getDecoder().decode(credential);
      byte[] utf8 = decodeCipher.doFinal(enc);
      String charSet = "UTF-8";
      String decodeWithSalt = new String(utf8, charSet);
      return StringUtils.substring(decodeWithSalt, SALT.length(), decodeWithSalt.length());
    } catch (InvalidKeySpecException | NoSuchAlgorithmException | NoSuchPaddingException
             | InvalidKeyException | InvalidAlgorithmParameterException |
             UnsupportedEncodingException
             | IllegalBlockSizeException | BadPaddingException | IllegalArgumentException e) {
      LOGGER.error("FATAL error, can not decode encrypted value");
      return credential;
    }
  }

  public static String hashing(String originalString) throws NoSuchAlgorithmException {
    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    byte[] encodedClientSecret = digest.digest(originalString.getBytes(StandardCharsets.UTF_8));
    StringBuilder hexString = new StringBuilder(2 * encodedClientSecret.length);
    for (byte b : encodedClientSecret) {
      String hex = Integer.toHexString(0xff & b);
      if (hex.length() == 1) {
        hexString.append('0');
      }
      hexString.append(hex);
    }
    return hexString.toString();
  }
}
