package com.chrono.event.exception;

public class CivilizationNotFoundException extends RuntimeException {
    public CivilizationNotFoundException(String message) {
        super(message);
    }
    
    public CivilizationNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
} 