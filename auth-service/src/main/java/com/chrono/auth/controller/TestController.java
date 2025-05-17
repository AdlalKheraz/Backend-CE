package com.chrono.auth.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/public")
    public ResponseEntity<String> publicEndpoint() {
        return ResponseEntity.ok("Cet endpoint est public");
    }

    @GetMapping("/protected")
    public ResponseEntity<String> protectedEndpoint() {
        return ResponseEntity.ok("Cet endpoint est protégé et nécessite une authentification");
    }

    @GetMapping("/cookie")
    public ResponseEntity<Map<String, String>> testCookie(HttpServletRequest request) {
        Map<String, String> response = new HashMap<>();
        
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwt".equals(cookie.getName())) {
                    response.put("cookieStatus", "JWT cookie présent");
                    return ResponseEntity.ok(response);
                }
            }
        }
        
        response.put("cookieStatus", "Aucun cookie JWT trouvé");
        return ResponseEntity.ok(response);
    }
} 