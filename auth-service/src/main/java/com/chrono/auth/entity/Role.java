package com.chrono.auth.entity;

public enum Role {
    USER,
    ADMIN;
    
    public static Role fromString(String role) {
        try {
            return Role.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("No enum constant " + Role.class.getName() + "." + role);
        }
    }
}
