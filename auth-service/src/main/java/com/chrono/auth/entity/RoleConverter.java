package com.chrono.auth.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class RoleConverter implements AttributeConverter<Role, String> {

    @Override
    public String convertToDatabaseColumn(Role role) {
        return role == null ? null : role.name();
    }

    @Override
    public Role convertToEntityAttribute(String value) {
        if (value == null) {
            return null;
        }
        
        try {
            return Role.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            // Log the error and throw a more descriptive exception
            throw new IllegalArgumentException("Invalid role value: " + value, e);
        }
    }
} 