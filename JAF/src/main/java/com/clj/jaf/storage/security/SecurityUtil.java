package com.clj.jaf.storage.security;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class SecurityUtil {
    public SecurityUtil() {
    }

    public static byte[] encrypt(byte[] content, int encryptionMode, byte[] secretKey, byte[] ivx) {
        if (secretKey.length == 16 && ivx.length == 16) {
            try {
                SecretKeySpec e = new SecretKeySpec(secretKey, CipherAlgorithmType.AES.getAlgorithmName());
                IvParameterSpec IV = new IvParameterSpec(ivx);
                String transformation = CipherTransformationType.AES_CBC_PKCS5Padding;
                Cipher decipher = Cipher.getInstance(transformation);
                decipher.init(encryptionMode, e, IV);
                byte[] plainText = decipher.doFinal(content);
                return plainText;
            } catch (NoSuchAlgorithmException var9) {
                throw new RuntimeException("Failed to encrypt/descrypt - Unknown Algorithm", var9);
            } catch (NoSuchPaddingException var10) {
                throw new RuntimeException("Failed to encrypt/descrypt- Unknown Padding", var10);
            } catch (InvalidKeyException var11) {
                throw new RuntimeException("Failed to encrypt/descrypt - Invalid Key", var11);
            } catch (InvalidAlgorithmParameterException var12) {
                throw new RuntimeException("Failed to encrypt/descrypt - Invalid Algorithm Parameter", var12);
            } catch (IllegalBlockSizeException var13) {
                throw new RuntimeException("Failed to encrypt/descrypt", var13);
            } catch (BadPaddingException var14) {
                throw new RuntimeException("Failed to encrypt/descrypt", var14);
            }
        } else {
            throw new RuntimeException("Set the encryption parameters correctly. The must be 16 length long each");
        }
    }

    public String xor(String msg, String key) {
        try {
            String UTF_8 = "UTF-8";
            byte[] msgArray = msg.getBytes("UTF-8");
            byte[] keyArray = key.getBytes("UTF-8");
            byte[] out = new byte[msgArray.length];

            for (int i = 0; i < msgArray.length; ++i) {
                out[i] = (byte) (msgArray[i] ^ keyArray[i % keyArray.length]);
            }

            return new String(out, "UTF-8");
        } catch (UnsupportedEncodingException var8) {
            return null;
        }
    }
}
