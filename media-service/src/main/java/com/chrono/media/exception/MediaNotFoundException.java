package com.chrono.media.exception;

public class MediaNotFoundException extends RuntimeException {
    public MediaNotFoundException(String message) {
        super(message);
    }
    
    public MediaNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
} 