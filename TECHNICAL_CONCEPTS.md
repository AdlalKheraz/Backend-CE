# Chrono Explorer - Concepts Techniques

## 📋 Table des matières

- [Architecture et Patterns](#architecture-et-patterns)
- [Entités JPA](#entités-jpa)
- [Data Transfer Objects (DTOs)](#data-transfer-objects-dtos)
- [Dépendances Maven](#dépendances-maven)
- [Authentification JWT](#authentification-jwt)
- [Validation des données](#validation-des-données)
- [Communication inter-services](#communication-inter-services)
- [Gestion des erreurs](#gestion-des-erreurs)
- [Patterns utilisés](#patterns-utilisés)

## 🏗️ Architecture et Patterns

### Architecture Microservices

L'application suit une **architecture microservices** avec les principes suivants :

#### Séparation des responsabilités
```
┌─────────────────┐
│   Gateway       │ ← Point d'entrée unique (API Gateway Pattern)
└─────────────────┘
         │
    ┌────┴────┐
    │         │
┌───▼───┐ ┌──▼──┐
│ Auth  │ │Event│ ← Services métier indépendants
└───────┘ └─────┘
```

#### Avantages
- **Scalabilité** : Chaque service peut être déployé indépendamment
- **Résilience** : Panne d'un service n'affecte pas les autres
- **Technologie** : Chaque service peut utiliser sa propre stack
- **Équipes** : Développement parallèle par équipes spécialisées

### Pattern Repository
```java
@Repository
public interface CivilizationRepository extends JpaRepository<Civilization, Long> {
    // Spring Data génère automatiquement les implémentations
}
```

### Pattern Service Layer
```java
@Service
public class CivilizationServiceImpl implements CivilizationService {
    // Logique métier séparée des contrôleurs
}
```

## 🗃️ Entités JPA

### Concept des Entités

Les **entités** représentent les tables de base de données en Java (ORM - Object-Relational Mapping).

#### Exemple : Civilization Entity
```java
@Entity                                    // Marque la classe comme entité JPA
@Getter @Setter                           // Lombok génère getters/setters
@NoArgsConstructor @AllArgsConstructor    // Lombok génère constructeurs
@Builder                                  // Lombok génère pattern Builder
public class Civilization {
    
    @Id                                   // Clé primaire
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Auto-increment
    private Long id;
    
    private String name;                  // Colonne VARCHAR
    private String description;           // Colonne VARCHAR
    private LocalDate startDate;          // Colonne DATE
    private LocalDate endDate;            // Colonne DATE
    
    @OneToMany(mappedBy = "civilization", cascade = CascadeType.REMOVE)
    private List<Event> events;           // Relation 1:N avec cascade
}
```

#### Annotations JPA expliquées

| Annotation | Rôle |
|------------|------|
| `@Entity` | Marque la classe comme entité persistante |
| `@Id` | Définit la clé primaire |
| `@GeneratedValue` | Génération automatique de l'ID |
| `@OneToMany` | Relation un-à-plusieurs |
| `@ManyToOne` | Relation plusieurs-à-un |
| `@CascadeType.REMOVE` | Suppression en cascade |

#### Relations entre entités
```java
// Dans Event.java
@ManyToOne
private Civilization civilization;  // Plusieurs événements → une civilisation

// Dans Civilization.java  
@OneToMany(mappedBy = "civilization", cascade = CascadeType.REMOVE)
private List<Event> events;        // Une civilisation → plusieurs événements
```

## 📦 Data Transfer Objects (DTOs)

### Concept des DTOs

Les **DTOs** sont des objets de transfert de données qui :
- Exposent seulement les données nécessaires
- Évitent les références circulaires
- Optimisent les performances réseau
- Découplent l'API des entités internes

#### Problème sans DTO
```java
// ❌ Problème : référence circulaire
Civilization → List<Event> → Civilization → List<Event> → ...
```

#### Solution avec DTO
```java
// ✅ Solution : DTO sans références circulaires
@Data
@Builder
public class CivilizationDTO {
    private Long id;
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    // Pas de liste d'événements = pas de référence circulaire
}
```

### Types de DTOs utilisés

#### 1. CivilizationDTO
```java
// Réponse optimisée pour les civilisations
{
  "id": 1,
  "name": "Ancient Rome",
  "startDate": "0753-04-21",
  "endDate": "1453-05-29"
}
```

#### 2. PublicEventEnrichedDTO
```java
// Événement avec médias pour affichage public
{
  "id": 1,
  "title": "Foundation of Rome",
  "civilizationId": 1,        // Seulement l'ID, pas l'objet complet
  "medias": [...]             // Médias associés
}
```

#### 3. CommentDTO
```java
// Commentaire sans référence à l'événement complet
{
  "id": 1,
  "content": "Great event!",
  "eventId": 1,               // Seulement l'ID
  "authorEmail": "user@example.com"
}
```

### Conversion Entity ↔ DTO
```java
private CivilizationDTO convertToDTO(Civilization civilization) {
    return CivilizationDTO.builder()
            .id(civilization.getId())
            .name(civilization.getName())
            .description(civilization.getDescription())
            .startDate(civilization.getStartDate())
            .endDate(civilization.getEndDate())
            .build();
}
```

## 📚 Dépendances Maven

### Structure des dépendances

#### 1. Spring Boot Starters
```xml
<!-- Web MVC + REST -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- JPA + Hibernate -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<!-- Validation Bean -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>

<!-- Security + JWT -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

#### 2. Base de données
```xml
<!-- H2 Database (développement) -->
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
```

#### 3. Utilitaires
```xml
<!-- Lombok (réduction code boilerplate) -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>

<!-- JWT -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.11.5</version>
</dependency>
```

#### 4. Stockage fichiers
```xml
<!-- MinIO pour stockage -->
<dependency>
    <groupId>io.minio</groupId>
    <artifactId>minio</artifactId>
    <version>8.5.9</version>
</dependency>
```

### Rôle de chaque dépendance

| Dépendance | Rôle |
|------------|------|
| `spring-boot-starter-web` | Serveur web, REST, JSON |
| `spring-boot-starter-data-jpa` | ORM, repositories |
| `spring-boot-starter-security` | Authentification, autorisation |
| `spring-boot-starter-validation` | Validation des DTOs |
| `lombok` | Génération automatique de code |
| `jjwt` | Création/validation tokens JWT |
| `minio` | Stockage de fichiers |

## 🔐 Authentification JWT

### Concept JWT (JSON Web Token)

Un **JWT** est un token sécurisé composé de 3 parties :
```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIiwiaWF0IjoxNjQwOTk1MjAwLCJleHAiOjE2NDA5OTg4MDB9.signature
│────────── Header ──────────│────────────── Payload ────────────│─ Signature ─│
```

#### Structure JWT
1. **Header** : Algorithme de signature
2. **Payload** : Données utilisateur (claims)
3. **Signature** : Vérification d'intégrité

### Génération du Token

#### Service de génération
```java
@Service
public class JwtService {
    
    @Value("${jwt.secret}")
    private String secretKey;
    
    @Value("${jwt.expiration}")
    private long jwtExpiration;
    
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }
    
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)                           // Données utilisateur
                .setSubject(subject)                         // Email utilisateur
                .setIssuedAt(new Date(System.currentTimeMillis()))  // Date création
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration)) // Expiration
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)  // Signature
                .compact();
    }
    
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
```

#### Processus de génération
1. **Collecte des claims** : Email, rôle, etc.
2. **Définition expiration** : 24h par défaut
3. **Signature** : Avec clé secrète HMAC-SHA256
4. **Encodage Base64** : Format final du token

### Validation du Token

#### Service de validation
```java
public boolean isTokenValid(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
}

public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
}

private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
}

public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
}

private Claims extractAllClaims(String token) {
    return Jwts.parserBuilder()
            .setSigningKey(getSignInKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
}
```

#### Étapes de validation
1. **Extraction du token** : Depuis header Authorization
2. **Vérification signature** : Avec clé secrète
3. **Vérification expiration** : Token non expiré
4. **Vérification utilisateur** : Email correspond

### Filtre JWT

#### JwtAuthenticationFilter
```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;
        
        // 1. Vérifier présence du token
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // 2. Extraire le token
        jwt = authHeader.substring(7);
        userEmail = jwtService.extractUsername(jwt);
        
        // 3. Valider et authentifier
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
            
            if (jwtService.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = 
                    new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        
        filterChain.doFilter(request, response);
    }
}
```

### Configuration Security

#### SecurityConfig
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()     // Endpoints publics
                .requestMatchers(HttpMethod.GET, "/api/**").permitAll()  // GET publics
                .anyRequest().authenticated()                    // Reste authentifié
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)  // Pas de session
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}
```

## ✅ Validation des données

### Bean Validation

#### Annotations de validation
```java
@Data
public class MediaRequest {
    @NotBlank(message = "L'URL est obligatoire")
    private String url;
    
    @Size(max = 255, message = "Le titre ne peut dépasser 255 caractères")
    private String title;
    
    @Size(max = 1000, message = "La description ne peut dépasser 1000 caractères")
    private String description;
    
    @NotNull(message = "Le type est obligatoire")
    private MediaType type;
    
    @NotNull(message = "L'ID de l'événement est obligatoire")
    @Positive(message = "L'ID de l'événement doit être positif")
    private Long eventId;
}
```

#### Annotations courantes

| Annotation | Rôle |
|------------|------|
| `@NotNull` | Valeur non nulle |
| `@NotBlank` | String non vide |
| `@Size` | Taille min/max |
| `@Positive` | Nombre positif |
| `@Email` | Format email valide |
| `@Pattern` | Expression régulière |

#### Validation dans les contrôleurs
```java
@PostMapping
public ResponseEntity<MediaResponse> addMedia(@Valid @RequestBody MediaRequest request) {
    // @Valid déclenche la validation automatique
    MediaResponse response = mediaService.addMedia(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
}
```

### Gestion des erreurs de validation

#### GlobalExceptionHandler
```java
@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        
        Map<String, Object> response = new HashMap<>();
        Map<String, String> errors = new HashMap<>();
        
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage())
        );
        
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("message", "Erreurs de validation");
        response.put("errors", errors);
        response.put("status", HttpStatus.BAD_REQUEST.value());
        
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
```

## 🔗 Communication inter-services

### RestTemplate

#### Configuration
```java
@Configuration
public class RestTemplateConfig {
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
```

#### Service client
```java
@Service
public class MediaClientService {
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Value("${media-service.url:http://media-service:8082}")
    private String mediaServiceUrl;
    
    public List<MediaDTO> getMediaByEventId(Long eventId) {
        try {
            String url = mediaServiceUrl + "/media/event/" + eventId;
            ResponseEntity<MediaDTO[]> response = restTemplate.getForEntity(url, MediaDTO[].class);
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return Arrays.asList(response.getBody());
            }
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des médias pour l'événement {}: {}", 
                     eventId, e.getMessage());
        }
        
        return Collections.emptyList();
    }
}
```

### Gestion des erreurs réseau

#### Stratégies de résilience
```java
public void deleteMediaByEventId(Long eventId) {
    try {
        String url = mediaServiceUrl + "/media/event/" + eventId;
        restTemplate.delete(url);
        log.info("Médias supprimés pour l'événement: {}", eventId);
    } catch (ResourceAccessException e) {
        log.warn("Service média indisponible lors de la suppression pour l'événement: {}", eventId);
        // Continue sans bloquer la suppression de l'événement
    } catch (Exception e) {
        log.error("Erreur lors de la suppression des médias pour l'événement {}: {}", 
                 eventId, e.getMessage());
        // Continue sans bloquer
    }
}
```

## 🚨 Gestion des erreurs

### Exceptions personnalisées

#### Définition
```java
public class CivilizationNotFoundException extends RuntimeException {
    public CivilizationNotFoundException(String message) {
        super(message);
    }
    
    public CivilizationNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

#### Utilisation
```java
@Override
public Civilization getById(Long id) {
    return civilizationRepository.findById(id)
            .orElseThrow(() -> new CivilizationNotFoundException(
                "Civilisation non trouvée avec l'ID: " + id));
}
```

### Handler global

#### GlobalExceptionHandler complet
```java
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    @ExceptionHandler(CivilizationNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleCivilizationNotFound(
            CivilizationNotFoundException ex) {
        
        log.error("Civilisation non trouvée: {}", ex.getMessage());
        
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("message", ex.getMessage());
        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("error", "Civilization Not Found");
        
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        log.error("Erreur inattendue: {}", ex.getMessage(), ex);
        
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("message", "Une erreur inattendue s'est produite");
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.put("error", "Internal Server Error");
        
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
```

## 🎯 Patterns utilisés

### 1. Repository Pattern
```java
// Abstraction de la couche de données
public interface CivilizationRepository extends JpaRepository<Civilization, Long> {
    // Spring Data génère automatiquement les implémentations
}
```

### 2. Service Layer Pattern
```java
// Séparation logique métier / contrôleur
@Service
public class CivilizationServiceImpl implements CivilizationService {
    // Logique métier centralisée
}
```

### 3. DTO Pattern
```java
// Objets de transfert optimisés
public class CivilizationDTO {
    // Seulement les données nécessaires
}
```

### 4. Builder Pattern (Lombok)
```java
@Builder
public class Civilization {
    // Lombok génère le pattern Builder
}

// Utilisation
Civilization civ = Civilization.builder()
    .name("Rome")
    .description("Empire romain")
    .build();
```

### 5. Dependency Injection
```java
@Service
public class CivilizationServiceImpl {
    
    private final CivilizationRepository repository;  // Final = immutable
    
    // Constructor injection (recommandé)
    public CivilizationServiceImpl(CivilizationRepository repository) {
        this.repository = repository;
    }
}
```

### 6. Factory Pattern (Spring)
```java
@Configuration
public class AppConfig {
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();  // Factory method
    }
}
```

## 📊 Bonnes pratiques appliquées

### 1. Immutabilité
```java
// Utilisation de final pour les dépendances
private final CivilizationRepository repository;
```

### 2. Fail-fast
```java
// Validation immédiate
.orElseThrow(() -> new CivilizationNotFoundException("..."));
```

### 3. Logging structuré
```java
@Slf4j
public class CivilizationServiceImpl {
    
    public Civilization create(Civilization civilization) {
        log.info("Création d'une nouvelle civilisation: {}", civilization.getName());
        // ...
        log.info("Civilisation créée avec succès avec l'ID: {}", saved.getId());
    }
}
```

### 4. Séparation des préoccupations
- **Controller** : Gestion HTTP
- **Service** : Logique métier
- **Repository** : Accès données
- **DTO** : Transfert données

### 5. Configuration externalisée
```properties
# application.properties
jwt.secret=${JWT_SECRET:default-secret-key}
jwt.expiration=${JWT_EXPIRATION:86400000}
media-service.url=${MEDIA_SERVICE_URL:http://localhost:8082}
```

Cette documentation technique couvre tous les concepts fondamentaux utilisés dans le projet Chrono Explorer, permettant une compréhension approfondie de l'architecture et des choix techniques. 