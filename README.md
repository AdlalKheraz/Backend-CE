# Chrono Explorer - Architecture Microservices

Ce projet implémente une architecture microservices complète pour l'application Chrono Explorer, construite avec Spring Boot 3.4.5 et Java 21.

## Table des matières
- [Architecture globale](#architecture-globale)
- [Services](#services)
- [Communication](#communication)
- [Sécurité & Authentification](#sécurité--authentification)
- [Configuration des bases de données](#configuration-des-bases-de-données)
- [Installation & Démarrage](#installation--démarrage)
- [Tests avec Postman](#tests-avec-postman)
- [Développement](#développement)

## Architecture globale

L'application est divisée en microservices indépendants qui communiquent via HTTP.

```mermaid
graph TB
    Client[Frontend Angular<br>localhost:4200] -->|Toutes les requêtes| Gateway
    Gateway[API Gateway<br>localhost:8080] -->|/api/auth/**| Auth
    Gateway -->|/api/media/**| Media
    Gateway -->|/api/event/**| Event
    
    Auth[Auth Service<br>localhost:8081]
    Media[Media Service<br>localhost:8082]
    Event[Event Service<br>localhost:8083]
    
    Auth -->|MySQL| AuthDB[(Auth DB<br>Port 3307)]
    Media -->|MySQL| MediaDB[(Media DB<br>Port 3309)]
    Event -->|MySQL| EventDB[(Event DB<br>Port 3308)]

    classDef service fill:#90CAF9,stroke:#1565C0,stroke-width:2px
    classDef database fill:#AED581,stroke:#33691E,stroke-width:2px
    classDef client fill:#FFD54F,stroke:#FF6F00,stroke-width:2px
    
    class Auth,Media,Event service
    class AuthDB,MediaDB,EventDB database
    class Client client
```

## Services

### Gateway Service (Port 8080)
- **Rôle**: Point d'entrée unique pour toutes les requêtes
- **Technologie**: Spring Cloud Gateway
- **Configuration**: Redirection des requêtes vers les services appropriés
- **CORS**: Configure pour accepter les requêtes du frontend Angular (localhost:4200)

### Auth Service (Port 8081)
- **Rôle**: Gestion des utilisateurs et authentification
- **Endpoints**:
  - `/api/auth/register`: Enregistrement d'un nouvel utilisateur
  - `/api/auth/login`: Connexion avec génération de JWT
  - `/api/auth/check`: Vérification d'un token JWT
  - `/api/auth/logout`: Déconnexion (suppression du cookie)
- **Sécurité**: Génération et validation des JWT, gestion des cookies HttpOnly

### Media Service (Port 8082)
- **Rôle**: Gestion des médias (images, vidéos YouTube)
- **Endpoints**:
  - `/api/media`: Ajout d'un nouveau média
  - `/api/media/event/{eventId}`: Récupération des médias pour un événement
- **Sécurité**: Intercepte et valide le JWT dans les cookies

### Event Service (Port 8083)
- **Rôle**: Gestion des événements historiques et commentaires
- **Endpoints**:
  - Gestion des événements historiques
  - Gestion des commentaires associés
  - Gestion des civilisations
- **Sécurité**: Intercepte et valide le JWT dans les cookies

## Communication

```mermaid
sequenceDiagram
    participant Frontend as Angular Frontend
    participant Gateway as API Gateway
    participant Auth as Auth Service
    participant Media as Media Service
    
    Frontend->>Gateway: 1. POST /api/auth/login
    Gateway->>Auth: 2. Redirection
    Auth->>Auth: 3. Validation & génération JWT
    Auth-->>Frontend: 4. Réponse + Cookie JWT HttpOnly
    
    Frontend->>Gateway: 5. GET /api/media/event/123
    Gateway->>Media: 6. Redirection avec Cookie JWT
    Media->>Media: 7. Vérification JWT
    Media-->>Frontend: 8. Données du média
```

### Principes clés:
1. **Découplage total**: Aucune dépendance directe entre services
2. **Références par ID**: Les services stockent des références (eventId) sans connaître les détails
3. **JWT dans cookies HttpOnly**: Sécurité renforcée contre les attaques XSS

## Sécurité & Authentification

### Flux d'authentification

```mermaid
flowchart TD
    A[Utilisateur] -->|Login| B{Auth Service}
    B -->|Génère JWT| C[Cookie HttpOnly]
    C -->|Stocké dans navigateur| D[Requêtes futures]
    D -->|Inclut JWT| E{Gateway}
    E -->|Forward| F{Services}
    F -->|Vérifie JWT| G[Accès autorisé]
    F -->|JWT invalide| H[401 Unauthorized]
```

### JWT (JSON Web Token)
- **Clé secrète partagée**: Tous les services utilisent la même clé JWT pour la validation
- **Stockage sécurisé**: Token stocké uniquement dans un cookie HttpOnly
- **Validation côté serveur**: Chaque service valide le token indépendamment

## Configuration des bases de données

Le projet utilise Docker pour fournir les bases de données MySQL:

```mermaid
graph LR
    Docker[Docker-Compose] --> AuthDB[(Auth DB<br>localhost:3307)]
    Docker --> MediaDB[(Media DB<br>localhost:3309)]
    Docker --> EventDB[(Event DB<br>localhost:3308)]
```

### Configuration Docker
Le fichier `docker-compose.yml` définit trois conteneurs MySQL:
- **mysql-auth**: Port 3307, DB: auth_db
- **mysql-event**: Port 3308, DB: event_db
- **mysql-media**: Port 3309, DB: media_db

Chaque service se connecte à sa propre base de données avec les identifiants:
- Username: chrono
- Password: chrono_pass

## Installation & Démarrage

### Prérequis
- Java 21
- Maven
- Docker & Docker Compose
- Node.js & Angular CLI (pour le frontend)

### Étapes de démarrage

1. **Lancer les bases de données**:
```bash
docker-compose up -d
```

2. **Compiler et démarrer les services** (dans des terminaux séparés):
```bash
   # Auth Service
cd auth-service
   mvn spring-boot:run

   # Media Service
cd media-service
   mvn spring-boot:run

   # Event Service
cd event-service
   mvn spring-boot:run
   
   # Gateway Service
   cd gateway-service
   mvn spring-boot:run
   ```

3. **Vérifier le fonctionnement**:
   - Gateway: http://localhost:8080
   - Auth Service: http://localhost:8081
   - Media Service: http://localhost:8082
   - Event Service: http://localhost:8083

## Tests avec Postman

Voir le fichier [POSTMAN_GUIDE.md](./POSTMAN_GUIDE.md) pour un guide détaillé des tests API et les collections Postman.

## Développement

### Structure du projet
```
BACKEND-CE/
├── auth-service/       # Service d'authentification
├── event-service/      # Service de gestion des événements
├── gateway-service/    # API Gateway
├── media-service/      # Service de gestion des médias
├── docker-compose.yml  # Configuration Docker
├── README.md           # Ce fichier
└── POSTMAN_GUIDE.md    # Guide Postman
```

### Modification des services

Lors de la modification ou de l'extension des services, assurez-vous de:
1. Maintenir la même clé JWT dans tous les services
2. Respecter les conventions de nommage des routes
3. Implémenter la validation JWT pour les nouveaux endpoints
4. Configurer correctement les CORS si nécessaire
