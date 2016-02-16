package com.clj.jaf.exception;

public class JNoSuchNameLayoutException extends JException {
    private static final long serialVersionUID = 2780151262388197741L;

    public JNoSuchNameLayoutException() {
    }

    public JNoSuchNameLayoutException(String detailMessage) {
        super(detailMessage);
    }
}
