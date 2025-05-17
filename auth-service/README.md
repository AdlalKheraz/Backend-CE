# Service d'Authentification (Auth-Service)

Ce service gère l'authentification et l'autorisation des utilisateurs pour le système Chrono. Il fournit des API RESTful pour l'inscription et la connexion des utilisateurs, générant des JWT (JSON Web Tokens) pour sécuriser l'accès aux autres services.

## Fonctionnalités

- Inscription des utilisateurs
- Authentification des utilisateurs
- Génération de JWT
- Validation de JWT
- Gestion des rôles (USER, ADMIN)

## Architecture

Le service est construit avec les technologies suivantes :

- Java 21+
- Spring Boot 3.4.5
- Spring Security
- JWT (JSON Web Tokens)
- MySQL 8.0
- Hibernate/JPA
- Maven

## Structure du projet et fonctionnement des fichiers

### Fichiers principaux

| Fichier | Description | Fonctionnalité |
|---------|-------------|----------------|
| **AuthServiceApplication.java** | Point d'entrée de l'application | Démarre l'application Spring Boot |
| **application.properties** | Configuration de l'application | Définit les paramètres de connexion à la base de données, le port du serveur et les propriétés JWT |

### Package `entity`

| Fichier | Description | Fonctionnalité |
|---------|-------------|----------------|
| **User.java** | Entité JPA représentant un utilisateur | Définit le schéma de la table users avec annotations JPA |
| **Role.java** | Énumération des rôles disponibles | Définit les rôles USER et ADMIN |

### Package `repository`

| Fichier | Description | Fonctionnalité |
|---------|-------------|----------------|
| **UserRepository.java** | Interface JPA Repository | Gère les opérations CRUD pour l'entité User et définit la méthode findByEmail |

### Package `dto`

| Fichier | Description | Fonctionnalité |
|---------|-------------|----------------|
| **RegisterRequest.java** | DTO pour l'inscription | Contient les données d'inscription (email, password) |
| **AuthRequest.java** | DTO pour la connexion | Contient les données de connexion (email, password) |
| **AuthResponse.java** | DTO pour la réponse d'authentification | Contient le token JWT généré |

### Package `service`

| Fichier | Description | Fonctionnalité |
|---------|-------------|----------------|
| **AuthService.java** | Service principal d'authentification | Implémente la logique d'inscription et de connexion, crée les utilisateurs et génère les tokens JWT |

### Package `security`

| Fichier | Description | Fonctionnalité |
|---------|-------------|----------------|
| **JWTService.java** | Service de gestion des JWT | Génère et valide les tokens JWT |
| **JWTAuthenticationFilter.java** | Filtre d'authentification JWT | Intercepte les requêtes HTTP et valide les tokens JWT présents dans l'en-tête Authorization |
| **UserDetailsServiceImpl.java** | Implémentation de UserDetailsService | Charge les utilisateurs depuis la base de données pour l'authentification Spring Security |

### Package `config`

| Fichier | Description | Fonctionnalité |
|---------|-------------|----------------|
| **SecurityConfig.java** | Configuration de Spring Security | Configure les règles d'autorisation, les filtres de sécurité et le gestionnaire d'authentification |

### Package `controller`

| Fichier | Description | Fonctionnalité |
|---------|-------------|----------------|
| **AuthController.java** | Contrôleur REST | Expose les endpoints `/api/auth/register` et `/api/auth/login` |
| **TestController.java** | Contrôleur de test | Expose les endpoints `/api/test/public` et `/api/test/protected` pour tester la sécurité |

## Flux d'interaction entre les composants

### Inscription d'un utilisateur

1. **Client** → Envoie une requête POST à `/api/auth/register` avec email et password
2. **AuthController** → Reçoit la requête et appelle AuthService.register()
3. **AuthService** → 
   - Crée un nouvel objet User à partir des données
   - Encode le mot de passe avec PasswordEncoder
   - Sauvegarde l'utilisateur via UserRepository
   - Demande à JWTService de générer un token
   - Retourne un AuthResponse contenant le token
4. **AuthController** → Retourne la réponse au client

### Authentification d'un utilisateur

1. **Client** → Envoie une requête POST à `/api/auth/login` avec email et password
2. **AuthController** → Reçoit la requête et appelle AuthService.authenticate()
3. **AuthService** → 
   - Demande à AuthenticationManager de valider les identifiants
   - Récupère l'utilisateur via UserRepository
   - Demande à JWTService de générer un token
   - Retourne un AuthResponse contenant le token
4. **AuthController** → Retourne la réponse au client

### Validation du token JWT

1. **Client** → Envoie une requête HTTP avec un en-tête Authorization contenant le token JWT
2. **JWTAuthenticationFilter** → 
   - Intercepte la requête
   - Extrait le token de l'en-tête
   - Demande à JWTService d'extraire l'email du token
   - Demande à UserDetailsServiceImpl de charger l'utilisateur
   - Configure SecurityContext avec l'authentification
3. **Application** → Traite la requête avec l'utilisateur authentifié

## Configuration

Les principaux paramètres de configuration se trouvent dans le fichier `application.properties` :

```properties
# Base de données
spring.datasource.url=jdbc:mysql://localhost:3307/auth_db
spring.datasource.username=root
spring.datasource.password=root

# JWT
jwt.expiration=86400000  # 24 heures en millisecondes
```

## Modèle de données

### User

| Champ      | Type           | Description                           |
|------------|----------------|---------------------------------------|
| id         | Long           | Identifiant unique                    |
| email      | String         | Email de l'utilisateur (unique)       |
| password   | String         | Mot de passe hashé                    |
| role       | Enum (Role)    | Rôle (USER ou ADMIN)                  |
| createdAt  | LocalDateTime  | Date et heure de création             |
| updatedAt  | LocalDateTime  | Date et heure de dernière mise à jour |

## API Endpoints

### Endpoints d'authentification

#### Inscription

Enregistre un nouvel utilisateur dans le système.

```
POST /api/auth/register
```

**Corps de la requête :**

```json
{
  "email": "utilisateur@example.com",
  "password": "motdepasse123"
}
```

**Réponse :**

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

#### Connexion

Authentifie un utilisateur existant.

```
POST /api/auth/login
```

**Corps de la requête :**

```json
{
  "email": "utilisateur@example.com",
  "password": "motdepasse123"
}
```

**Réponse :**

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### Endpoints de test

#### Endpoint public

Endpoint de test public qui ne nécessite pas d'authentification.

```
GET /api/test/public
```

**Réponse :**

```
Cet endpoint est public
```

#### Endpoint protégé

Endpoint de test qui nécessite une authentification par token JWT.

```
GET /api/test/protected
```

**En-tête requis :**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Réponse :**

```
Cet endpoint est protégé et nécessite une authentification
```

## Sécurité

- Tous les endpoints, sauf `/api/auth/**` et `/api/test/public`, nécessitent une authentification
- Les mots de passe sont hashés avec BCrypt
- Les JWT générés ont une validité de 24 heures
- Les tokens JWT contiennent les informations de l'utilisateur (email, rôle)
- CORS est configuré pour permettre l'accès depuis toutes les origines

## Utilisation du JWT

Pour accéder aux endpoints protégés des différents services, ajoutez le token JWT dans l'en-tête HTTP :

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

## Déploiement

### Prérequis

- Java 21+
- MySQL 8.0 (port 3307)

### Installation

1. Assurez-vous que MySQL est en cours d'exécution :
   ```
   docker start mysql-auth
   ```

2. Lancez l'application :
   ```
   ./mvnw spring-boot:run
   ```

Le service sera accessible à l'adresse `http://localhost:8081`.

## Tests avec Postman

### Documentation de la collection Postman

Le fichier `postman-collection.json` contient une collection prête à l'emploi pour tester les API du service d'authentification. Voici comment elle est structurée :

#### Organisation de la collection

La collection est organisée en deux dossiers principaux :

1. **Authentication** - Contient les requêtes pour l'inscription et la connexion
   - **Register** - Crée un nouvel utilisateur et génère un token JWT
   - **Login** - Authentifie un utilisateur existant et génère un token JWT
   
2. **Tests** - Contient des exemples pour tester les endpoints de sécurité
   - **Public Endpoint** - Teste l'accès à l'endpoint public sans authentification
   - **Protected Endpoint** - Teste l'accès à l'endpoint protégé avec authentification JWT

#### Fonctionnalités automatisées

La collection contient des scripts de test automatisés pour les deux endpoints d'authentification :

- Les requêtes **Register** et **Login** incluent des scripts qui enregistrent automatiquement le token JWT dans la variable d'environnement `jwt_token` lorsque la connexion réussit
- La requête **Protected Endpoint** utilise automatiquement le token JWT stocké pour l'authentification

#### Comment utiliser la collection

1. **Configuration de l'environnement**
   - Importez le fichier `postman-collection.json` dans Postman
   - Créez un environnement Postman en cliquant sur "Environment" puis "Create Environment"
   - Nommez l'environnement (ex: "Auth Service")
   - Ajoutez une variable `jwt_token` (valeur initiale vide)
   - Cliquez sur "Save"
   - **Important**: Sélectionnez cet environnement dans la liste déroulante en haut à droite

2. **Exécution des tests étape par étape**
   - **Étape 1**: Testez l'endpoint public
     - Sélectionnez la requête "Public Endpoint" dans le dossier "Tests"
     - Cliquez sur "Send"
     - Vérifiez que vous recevez une réponse 200 OK

   - **Étape 2**: Inscrivez un nouvel utilisateur
     - Sélectionnez la requête "Register" dans le dossier "Authentication"
     - Modifiez l'email si nécessaire pour éviter les doublons
     - Cliquez sur "Send"
     - Vérifiez que vous recevez une réponse 200 OK avec un token
     - Le token est automatiquement sauvegardé dans la variable `jwt_token`

   - **Étape 3**: Testez l'endpoint protégé
     - Sélectionnez la requête "Protected Endpoint" dans le dossier "Tests"
     - Cliquez sur "Send"
     - Vérifiez que vous recevez une réponse 200 OK

   - **Étape 4**: Connectez-vous avec un utilisateur existant
     - Sélectionnez la requête "Login" dans le dossier "Authentication"
     - Assurez-vous que les identifiants correspondent à un utilisateur existant
     - Cliquez sur "Send"
     - Vérifiez que vous recevez une réponse 200 OK avec un token
     - Le token est automatiquement mis à jour dans la variable `jwt_token`

3. **Exécution de la collection complète**
   - Cliquez sur le nom de la collection "Chrono Auth Service"
   - Cliquez sur le bouton "Run" (flèche vers la droite)
   - Sélectionnez toutes les requêtes dans l'ordre: Public Endpoint, Register, Protected Endpoint, Login
   - Cliquez sur "Run Chrono Auth Service"
   - Vérifiez que tous les tests passent avec succès

### Résolution des problèmes courants

- **Erreur 403 Forbidden**: Assurez-vous que l'en-tête Content-Type est correctement défini à "application/json" pour les requêtes POST.
- **Erreur 401 Unauthorized**: Vérifiez que le token JWT est correctement inclus dans l'en-tête Authorization et qu'il est précédé de "Bearer ".
- **Variable d'environnement non définie**: Vérifiez que vous avez bien sélectionné l'environnement créé dans la liste déroulante en haut à droite de Postman.

## Intégration avec d'autres services

Pour intégrer ce service d'authentification avec d'autres microservices, ceux-ci doivent valider le JWT reçu dans leurs propres configurations de sécurité. Le token contient l'email de l'utilisateur et son rôle, permettant d'implémenter un contrôle d'accès basé sur les rôles. 