package com.clj.jaf.exception;

public class JNoSuchCommandException extends JException {
    private static final long serialVersionUID = 1L;

    public JNoSuchCommandException() {
    }

    public JNoSuchCommandException(String detailMessage) {
        super(detailMessage);
    }
}
