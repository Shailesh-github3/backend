package com.edubridge.backend.exception;

/**
 * @author Shailesh
 **/
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
