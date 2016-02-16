package com.clj.jaf.storage.security;

public enum CipherPaddingType {
    NoPadding("NoPadding"),
    PKCS5Padding("PKCS5Padding"),
    PKCS1Padding("PKCS1Padding"),
    OAEPWithSHA_1AndMGF1Padding("OAEPWithSHA-1AndMGF1Padding"),
    OAEPWithSHA_256AndMGF1Padding("OAEPWithSHA-256AndMGF1Padding");

    private String mName;

    private CipherPaddingType(String name) {
        this.mName = name;
    }

    public String getAlgorithmName() {
        return this.mName;
    }
}