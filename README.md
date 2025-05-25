# Chrono Explorer Backend API

## 📋 Table des matières

- [Vue d'ensemble](#vue-densemble)
- [Architecture](#architecture)
- [Technologies utilisées](#technologies-utilisées)
- [Installation et configuration](#installation-et-configuration)
- [Structure du projet](#structure-du-projet)
- [API Endpoints](#api-endpoints)
- [Modèles de données](#modèles-de-données)
- [Authentification](#authentification)
- [Collection Postman](#collection-postman)
- [Exemples d'utilisation](#exemples-dutilisation)

## 🌟 Vue d'ensemble

Chrono Explorer est une API REST pour la gestion d'événements historiques, de civilisations, de médias et de commentaires. L'application suit une architecture microservices avec Spring Boot et utilise une gateway pour router les requêtes.

### Fonctionnalités principales

- **Gestion des civilisations** : CRUD complet avec dates de début/fin
- **Gestion des événements** : Événements historiques liés aux civilisations
- **Gestion des médias** : Upload et gestion de fichiers multimédias
- **Système de commentaires** : Commentaires sur les événements
- **Authentification JWT** : Système d'authentification sécurisé
- **Endpoints publics et privés** : Accès différencié selon l'authentification
- **Événements enrichis** : Événements avec médias associés
- **Suppression en cascade** : Suppression automatique des entités liées

## 🏗️ Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Gateway       │    │  Event Service  │    │  Media Service  │
│   (Port 8080)   │────│   (Port 8081)   │────│   (Port 8082)   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         │              ┌─────────────────┐              │
         └──────────────│  Auth Service   │──────────────┘
                        │   (Port 8083)   │
                        └─────────────────┘
```

### Services

1. **Gateway** (8080) : Point d'entrée unique, routage des requêtes
2. **Event Service** (8081) : Gestion des événements et civilisations
3. **Media Service** (8082) : Gestion des médias et stockage
4. **Auth Service** (8083) : Authentification et gestion des utilisateurs

## 🛠️ Technologies utilisées

- **Java 21**
- **Spring Boot 3.4.5**
- **Spring Security** (JWT)
- **Spring Data JPA**
- **H2 Database** (développement)
- **MinIO** (stockage des fichiers)
- **Maven** (gestion des dépendances)
- **Lombok** (réduction du code boilerplate)

## ⚙️ Installation et configuration

### Prérequis

- Java 21+
- Maven 3.6+
- MinIO (pour le stockage des fichiers)

### Étapes d'installation

1. **Cloner le repository**
```bash
git clone <repository-url>
cd Backend-CE
```

2. **Compiler le projet**
```bash
mvn clean install
```

3. **Démarrer les services**
```bash
# Terminal 1 - Gateway
cd gateway
mvn spring-boot:run

# Terminal 2 - Event Service
cd event-service
mvn spring-boot:run

# Terminal 3 - Media Service
cd media-service
mvn spring-boot:run

# Terminal 4 - Auth Service
cd auth-service
mvn spring-boot:run
```

4. **Accéder à l'API**
- URL de base : `http://localhost:8080`
- Documentation Swagger : `http://localhost:8080/swagger-ui.html`

## 📁 Structure du projet

```
Backend-CE/
├── gateway/                 # Service Gateway (8080)
├── event-service/          # Service Événements (8081)
│   ├── src/main/java/com/chrono/event/
│   │   ├── controller/     # Contrôleurs REST
│   │   ├── service/        # Logique métier
│   │   ├── entity/         # Entités JPA
│   │   ├── dto/           # Data Transfer Objects
│   │   └── repository/     # Repositories JPA
├── media-service/          # Service Médias (8082)
├── auth-service/           # Service Authentification (8083)
├── postman_collection.json # Collection Postman
└── README.md              # Documentation
```

## 🔌 API Endpoints

### 🔓 Endpoints Publics

#### Événements
- `GET /api/events` - Tous les événements
- `GET /api/events/{id}` - Événement par ID
- `GET /api/events/civilization/{id}` - Événements par civilisation
- `GET /api/events/enriched` - Tous les événements enrichis (avec médias)
- `GET /api/events/enriched/{id}` - Événement enrichi par ID
- `GET /api/events/enriched/civilization/{id}` - Événements enrichis par civilisation

#### Civilisations
- `GET /api/civilizations` - Toutes les civilisations
- `GET /api/civilizations/{id}` - Civilisation par ID

#### Médias
- `GET /api/media` - Tous les médias
- `GET /api/media/event/{eventId}` - Médias par événement

#### Commentaires
- `GET /api/comments` - Tous les commentaires
- `GET /api/comments/event/{eventId}` - Commentaires par événement

### 🔐 Authentification

#### Inscription/Connexion
- `POST /api/auth/register` - Créer un compte
- `POST /api/auth/login` - Se connecter

### 🔒 Endpoints Protégés (JWT requis)

#### Gestion des utilisateurs
- `GET /api/users/me` - Profil utilisateur actuel
- `GET /api/users` - Tous les utilisateurs (Admin)
- `PUT /api/users/{id}` - Modifier un utilisateur (Admin)
- `DELETE /api/users/{id}` - Supprimer un utilisateur

#### Gestion des civilisations
- `POST /api/civilizations` - Créer une civilisation
- `PUT /api/civilizations/{id}` - Modifier une civilisation
- `DELETE /api/civilizations/{id}` - Supprimer une civilisation

#### Gestion des événements
- `POST /api/events` - Créer un événement
- `PUT /api/events/{id}` - Modifier un événement
- `DELETE /api/events/{id}` - Supprimer un événement

#### Gestion des médias
- `POST /api/media` - Ajouter un média (URL)
- `POST /api/media/upload` - Upload d'un fichier
- `PUT /api/media/{id}` - Modifier un média
- `DELETE /api/media/{id}` - Supprimer un média

#### Gestion des commentaires
- `POST /api/comments` - Ajouter un commentaire
- `PUT /api/comments/{id}` - Modifier un commentaire
- `DELETE /api/comments/{id}` - Supprimer un commentaire

## 📊 Modèles de données

### Civilization
```json
{
  "id": 1,
  "name": "Ancient Rome",
  "description": "The Roman Empire was one of the largest empires in ancient history",
  "startDate": "0753-04-21",
  "endDate": "1453-05-29"
}
```

### Event
```json
{
  "id": 1,
  "title": "Foundation of Rome",
  "description": "The legendary founding of Rome by Romulus",
  "date": "0753-04-21",
  "civilizationId": 1,
  "type": "POLITICAL",
  "verified": true
}
```

### Event Enrichi
```json
{
  "id": 1,
  "title": "Foundation of Rome",
  "description": "The legendary founding of Rome by Romulus",
  "date": "0753-04-21",
  "civilizationId": 1,
  "type": "POLITICAL",
  "verified": true,
  "medias": [
    {
      "id": 1,
      "url": "https://example.com/image.jpg",
      "title": "Rome Foundation",
      "description": "Artistic representation",
      "type": "IMAGE"
    }
  ]
}
```

### Media
```json
{
  "id": 1,
  "url": "https://example.com/image.jpg",
  "title": "Colisée romain",
  "description": "Photo du Colisée à Rome",
  "type": "IMAGE",
  "eventId": 1
}
```

### Comment
```json
{
  "id": 1,
  "content": "Very interesting historical event!",
  "authorEmail": "user@example.com",
  "eventId": 1,
  "createdAt": "2024-01-15T10:30:00"
}
```

### User
```json
{
  "id": 1,
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "role": "USER"
}
```

## 🔐 Authentification

### JWT Token
L'API utilise des tokens JWT pour l'authentification. Après connexion/inscription, vous recevez :

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "email": "user@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "role": "USER",
  "expiresIn": 86400000
}
```

### Utilisation du token
Ajoutez le token dans l'en-tête Authorization :
```
Authorization: Bearer <votre-token>
```

## 📮 Collection Postman

Une collection Postman complète est fournie (`postman_collection.json`) avec :

- **Variables d'environnement** : baseUrl, token, IDs
- **Scripts de test** : Sauvegarde automatique des IDs
- **Exemples de requêtes** : Tous les endpoints documentés
- **Authentification automatique** : Token géré automatiquement

### Variables Postman
- `baseUrl` : http://localhost:8080
- `token` : Token JWT (auto-généré)
- `civilizationId` : ID de civilisation (auto-généré)
- `eventId` : ID d'événement (auto-généré)
- `mediaId` : ID de média (auto-généré)
- `commentId` : ID de commentaire (auto-généré)

## 💡 Exemples d'utilisation

### 1. Créer une civilisation
```bash
curl -X POST http://localhost:8080/api/civilizations \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "name": "Ancient Egypt",
    "description": "Ancient Egyptian civilization",
    "startDate": "-3100-01-01",
    "endDate": "-0030-08-01"
  }'
```

### 2. Créer un événement
```bash
curl -X POST http://localhost:8080/api/events \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "title": "Construction of the Great Pyramid",
    "description": "Building of the Great Pyramid of Giza",
    "date": "-2580-01-01",
    "civilizationId": 1,
    "type": "CULTURAL"
  }'
```

### 3. Ajouter un média
```bash
curl -X POST http://localhost:8080/api/media \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "url": "https://example.com/pyramid.jpg",
    "title": "Great Pyramid",
    "description": "Photo of the Great Pyramid",
    "type": "IMAGE",
    "eventId": 1
  }'
```

### 4. Récupérer des événements enrichis
```bash
curl -X GET http://localhost:8080/api/events/enriched
```

## 🔄 Fonctionnalités avancées

### Suppression en cascade
- Supprimer une civilisation → supprime tous ses événements
- Supprimer un événement → supprime tous ses médias et commentaires

### Événements enrichis
Les endpoints `/enriched` retournent les événements avec leurs médias associés, optimisés pour l'affichage public.

### DTOs optimisés
- **CivilizationDTO** : Retourne `startDate` et `endDate` séparément
- **PublicEventEnrichedDTO** : Inclut seulement `civilizationId` et médias
- **CommentDTO** : Évite les références circulaires

## 🚀 Déploiement

### Variables d'environnement
```properties
# Database
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.username=sa
spring.datasource.password=

# JWT
jwt.secret=your-secret-key
jwt.expiration=86400000

# MinIO
minio.endpoint=http://localhost:9000
minio.access-key=minioadmin
minio.secret-key=minioadmin
```

### Ports par défaut
- Gateway : 8080
- Event Service : 8081
- Media Service : 8082
- Auth Service : 8083

## 📝 Notes importantes

1. **Base de données** : H2 en mémoire (données perdues au redémarrage)
2. **Stockage** : MinIO requis pour l'upload de fichiers
3. **CORS** : Configuré pour accepter toutes les origines en développement
4. **Validation** : Validation automatique des DTOs avec Bean Validation
5. **Logs** : Logs détaillés pour le debugging

## 🤝 Contribution

1. Fork le projet
2. Créer une branche feature (`git checkout -b feature/AmazingFeature`)
3. Commit les changements (`git commit -m 'Add some AmazingFeature'`)
4. Push vers la branche (`git push origin feature/AmazingFeature`)
5. Ouvrir une Pull Request

## 📄 Licence

Ce projet est sous licence MIT. Voir le fichier `LICENSE` pour plus de détails.
