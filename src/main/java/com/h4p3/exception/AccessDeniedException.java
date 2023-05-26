package com.h4p3.exception;

public class AccessDeniedException extends RuntimeException {

    /**
     * Constructs an <code>AccessDeniedException</code> with the specified message.
     * @param msg the detail message
     */
    public AccessDeniedException(String msg) {
        super(msg);
    }

    /**
     * Constructs an <code>AccessDeniedException</code> with the specified message and
     * root cause.
     * @param msg the detail message
     * @param cause root cause
     */
    public AccessDeniedException(String msg, Throwable cause) {
        super(msg, cause);
    }

}