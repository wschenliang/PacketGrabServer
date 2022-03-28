package com.jiangnan.exception;

public class CaptureException extends RuntimeException {
    private static final long serialVersionUID = -7262273899953835542L;

    public CaptureException() {
        super();
    }

    public CaptureException(String message) {
        super(message);
    }

    public CaptureException(String message, Throwable cause) {
        super(message, cause);
    }

    public CaptureException(Throwable cause) {
        super(cause);
    }

    protected CaptureException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
