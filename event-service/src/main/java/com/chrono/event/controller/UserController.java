package com.chrono.event.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/events/user")
public class UserController {

    @GetMapping("/info")
    public Map<String, Object> getUserInfo(HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");
        
        Map<String, Object> response = new HashMap<>();
        response.put("userId", userId);
        response.put("message", "You have access to this protected resource");
        
        return response;
    }
} 