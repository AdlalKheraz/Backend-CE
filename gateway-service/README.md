# Gateway Service – Chrono Explorer

## Description

Ce service sert de **porte d'entrée unique** vers tous les microservices du projet :
- `auth-service` (port `8081`)
- `media-service` (port `8082`)
- `event-service` (port `8083`)

La Gateway est configurée avec **Spring Cloud Gateway**, sans sécurité intégrée, car la vérification JWT est gérée dans `auth-service`.

---

## Tech Stack

- Java 21
- Spring Boot 3.4
- Spring Cloud Gateway
- Spring Web
- Actuator (pour le monitoring)
- Devtools (hot reload)

---

## Démarrage

### Port de la Gateway :
```properties
server.port=8080
```

### Redirection des routes :
| Service        | Route                | Destination            |
|----------------|----------------------|-------------------------|
| Auth           | `/api/auth/**`       | `http://localhost:8081` |
| Media          | `/api/media/**`      | `http://localhost:8082` |
| Event          | `/api/event/**`      | `http://localhost:8083` |

---

## CORS Configuration

```java
// Configuration reactive avec CorsWebFilter
@Bean
public CorsWebFilter corsWebFilter() {
    CorsConfiguration config = new CorsConfiguration();
    config.addAllowedOrigin("http://localhost:4200"); // Frontend Angular
    config.addAllowedHeader("*");
    config.addAllowedMethod("*");
    config.setAllowCredentials(true); // Permet les cookies
    
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    
    return new CorsWebFilter(source);
}
```

---

## Fonctionnement attendu

- Le frontend (Angular) appelle **uniquement** `http://localhost:8080/api/...`
- La Gateway route vers le bon microservice
- Les cookies HTTP-Only gérés par `auth-service` sont transmis automatiquement

---

## Exemple

```http
GET http://localhost:8080/api/media/all
Authorization: (aucun, les cookies gèrent)
```

---

## Recommandations

- Ne pas exposer les microservices directement (utiliser la Gateway toujours)
- Les tokens sont lus en `HttpOnly cookies`, donc pas accessibles en JS
- `auth-service` doit fournir une route `/auth/check` pour vérifier l'état d'un utilisateur connecté

---

## À venir (optionnel)

- Ajout d'un circuit breaker / fallback
- Ajout d'une route par défaut en cas de 404

---

