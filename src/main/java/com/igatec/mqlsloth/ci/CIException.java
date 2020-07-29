package com.igatec.mqlsloth.ci;

public class CIException extends RuntimeException {

    public CIException() {
        super();
    }

    public CIException(String message) {
        super(message);
    }

    public CIException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public CIException(Throwable throwable) {
        super(throwable);
    }
}
