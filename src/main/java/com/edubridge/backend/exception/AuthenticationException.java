package com.edubridge.backend.exception;

/**
 * @author Shailesh
 **/
public class AuthenticationException extends RuntimeException {
    public AuthenticationException(String message) {
        super(message);
    }
}
