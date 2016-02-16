package com.clj.jaf.storage.security;

public class CipherTransformationType {
    public static final String AES_CBC_NoPadding;
    public static final String AES_CBC_PKCS5Padding;
    public static final String AES_ECB_NoPadding;
    public static final String AES_ECB_PKCS5Padding;
    public static final String DES_CBC_NoPadding;
    public static final String DES_CBC_PKCS5Padding;
    public static final String DES_ECB_NoPadding;
    public static final String DES_ECB_PKCS5Padding;
    public static final String DESede_CBC_NoPadding;
    public static final String DESede_CBC_PKCS5Padding;
    public static final String DESede_ECB_NoPadding;
    public static final String DESede_ECB_PKCS5Padding;
    public static final String RSA_ECB_PKCS1Padding;
    public static final String RSA_ECB_OAEPWithSHA_1AndMGF1Padding;
    public static final String RSA_ECB_OAEPWithSHA_256AndMGF1Padding;

    static {
        AES_CBC_NoPadding = CipherAlgorithmType.AES + "/" + CipherModeType.CBC + "/" + CipherPaddingType.NoPadding;
        AES_CBC_PKCS5Padding = CipherAlgorithmType.AES + "/" + CipherModeType.CBC + "/" + CipherPaddingType.PKCS5Padding;
        AES_ECB_NoPadding = CipherAlgorithmType.AES + "/" + CipherModeType.ECB + "/" + CipherPaddingType.NoPadding;
        AES_ECB_PKCS5Padding = CipherAlgorithmType.AES + "/" + CipherModeType.ECB + "/" + CipherPaddingType.PKCS5Padding;
        DES_CBC_NoPadding = CipherAlgorithmType.DES + "/" + CipherModeType.CBC + "/" + CipherPaddingType.NoPadding;
        DES_CBC_PKCS5Padding = CipherAlgorithmType.DES + "/" + CipherModeType.CBC + "/" + CipherPaddingType.PKCS5Padding;
        DES_ECB_NoPadding = CipherAlgorithmType.DES + "/" + CipherModeType.ECB + "/" + CipherPaddingType.NoPadding;
        DES_ECB_PKCS5Padding = CipherAlgorithmType.DES + "/" + CipherModeType.ECB + "/" + CipherPaddingType.PKCS5Padding;
        DESede_CBC_NoPadding = CipherAlgorithmType.DESede + "/" + CipherModeType.CBC + "/" + CipherPaddingType.NoPadding;
        DESede_CBC_PKCS5Padding = CipherAlgorithmType.DESede + "/" + CipherModeType.CBC + "/" + CipherPaddingType.PKCS5Padding;
        DESede_ECB_NoPadding = CipherAlgorithmType.DESede + "/" + CipherModeType.ECB + "/" + CipherPaddingType.NoPadding;
        DESede_ECB_PKCS5Padding = CipherAlgorithmType.DESede + "/" + CipherModeType.ECB + "/" + CipherPaddingType.PKCS5Padding;
        RSA_ECB_PKCS1Padding = CipherAlgorithmType.RSA + "/" + CipherModeType.ECB + "/" + CipherPaddingType.PKCS1Padding;
        RSA_ECB_OAEPWithSHA_1AndMGF1Padding = CipherAlgorithmType.RSA + "/" + CipherModeType.ECB + "/" + CipherPaddingType.OAEPWithSHA_1AndMGF1Padding;
        RSA_ECB_OAEPWithSHA_256AndMGF1Padding = CipherAlgorithmType.RSA + "/" + CipherModeType.ECB + "/" + CipherPaddingType.OAEPWithSHA_256AndMGF1Padding;
    }

    public CipherTransformationType() {
    }
}