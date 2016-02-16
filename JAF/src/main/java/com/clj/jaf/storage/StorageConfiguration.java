package com.clj.jaf.storage;

import android.os.Build;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class StorageConfiguration {
    private int mChunkSize;
    private boolean mIsEncrypted;
    private byte[] mIvParameter;
    private byte[] mSecretKey;

    private StorageConfiguration(StorageConfiguration.Builder builder) {
        this.mChunkSize = builder._chunkSize;
        this.mIsEncrypted = builder._isEncrypted;
        this.mIvParameter = builder._ivParameter;
        this.mSecretKey = builder._secretKey;
    }

    public int getChuckSize() {
        return this.mChunkSize;
    }

    public boolean isEncrypted() {
        return this.mIsEncrypted;
    }

    public byte[] getSecretKey() {
        return this.mSecretKey;
    }

    public byte[] getIvParameter() {
        return this.mIvParameter;
    }

    public static class Builder {
        private int _chunkSize = 8192;
        private boolean _isEncrypted = false;
        private byte[] _ivParameter = null;
        private byte[] _secretKey = null;
        private static final String UTF_8 = "UTF-8";

        public Builder() {
        }

        public StorageConfiguration build() {
            return new StorageConfiguration(this);
        }

        public StorageConfiguration.Builder setChuckSize(int chunkSize) {
            this._chunkSize = chunkSize;
            return this;
        }

        public StorageConfiguration.Builder setEncryptContent(String ivx, String secretKey) {
            this._isEncrypted = true;

            try {
                this._ivParameter = ivx.getBytes("UTF-8");
            } catch (UnsupportedEncodingException var12) {
                Log.e("StorageConfiguration", "UnsupportedEncodingException", var12);
            }

            try {
                short e = 1000;
                short keyLength = 128;
                SecureRandom random = new SecureRandom();
                byte[] salt = new byte[16];
                random.nextBytes(salt);
                PBEKeySpec keySpec = new PBEKeySpec(secretKey.toCharArray(), salt, e, keyLength);
                SecretKeyFactory keyFactory = null;
                if (Build.VERSION.SDK_INT >= 19) {
                    keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1And8bit");
                } else {
                    keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
                }

                this._secretKey = keyFactory.generateSecret(keySpec).getEncoded();
            } catch (InvalidKeySpecException var10) {
                Log.e("StorageConfiguration", "InvalidKeySpecException", var10);
            } catch (NoSuchAlgorithmException var11) {
                Log.e("StorageConfiguration", "NoSuchAlgorithmException", var11);
            }

            return this;
        }
    }
}
