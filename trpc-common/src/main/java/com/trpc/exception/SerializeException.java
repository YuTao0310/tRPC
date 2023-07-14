package com.trpc.exception;

public class SerializeException extends RuntimeException {
    public SerializeException(String message) {
        super(message);
    }
}
