package com.clj.jaf.exception;

public class JException extends Exception {
    private static final long serialVersionUID = 1L;

    public JException() {
    }

    public JException(String detailMessage) {
        super(detailMessage);
    }
}
