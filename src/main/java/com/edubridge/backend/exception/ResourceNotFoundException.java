package com.edubridge.backend.exception;

/**
 * @author Shailesh
 **/
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
