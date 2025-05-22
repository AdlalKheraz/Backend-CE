package com.chrono.event.config;

import org.springframework.context.annotation.Configuration;

/**
 * Configuration CORS désactivée car elle est gérée par la gateway.
 * Ne pas réactiver pour éviter les doublons d'en-têtes CORS.
 */
@Configuration
public class WebConfig {
    // Configuration CORS supprimée car gérée par la gateway
} 