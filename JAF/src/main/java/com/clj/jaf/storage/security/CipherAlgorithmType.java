package com.clj.jaf.storage.security;

public enum CipherAlgorithmType {
    AES("AES"),
    DES("DES"),
    DESede("DESede"),
    RSA("RSA");

    private String mName;

    private CipherAlgorithmType(String name) {
        this.mName = name;
    }

    public String getAlgorithmName() {
        return this.mName;
    }
}