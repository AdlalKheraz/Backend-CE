package com.chrono.event.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

@Component
public class JwtUtil {
    
    private final Algorithm algorithm;
    private final JWTVerifier verifier;
    
    public JwtUtil(@Value("${jwt.secret}") String jwtSecret) {
        this.algorithm = Algorithm.HMAC256(jwtSecret);
        this.verifier = JWT.require(algorithm)
                .build();
    }
    
    public DecodedJWT validateToken(String token) {
        try {
            return verifier.verify(token);
        } catch (JWTVerificationException e) {
            throw new RuntimeException("Invalid JWT token", e);
        }
    }
    
    public String getUserIdFromToken(String token) {
        DecodedJWT jwt = validateToken(token);
        return jwt.getSubject();
    }
} 