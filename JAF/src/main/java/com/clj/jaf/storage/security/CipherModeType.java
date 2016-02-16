package com.clj.jaf.storage.security;

public enum CipherModeType {
    CBC("CBC"),
    ECB("ECB");

    private String mName;

    private CipherModeType(String name) {
        this.mName = name;
    }

    public String getAlgorithmName() {
        return this.mName;
    }
}
