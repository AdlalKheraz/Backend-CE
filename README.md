# Chrono Explorer Backend

**Chrono Explorer** est une application éducative basée sur une architecture **microservices** avec **Spring Boot**, qui permet aux utilisateurs de consulter, commenter et explorer des événements historiques rattachés à des civilisations.

---

## Table des matières

- [Architecture du projet](#architecture-du-projet)
- [Flux Fonctionnels](#flux-fonctionnels)
- [Services et responsabilités](#services-et-responsabilités)
- [Technologies](#technologies)
- [Lancement du projet](#lancement-du-projet)
- [Structure des bases de données](#structure-des-bases-de-données)
- [Diagramme des services](#diagramme-des-services)
- [Auteurs](#auteurs)

---

## Architecture du projet

L’application est composée de **3 microservices** indépendants :

- **auth-service** : gestion de l’authentification et des utilisateurs
- **media-service** : gestion des liens vers des médias (images, vidéos, etc.)
- **event-service** : gestion des événements, des civilisations et des commentaires

Chaque service dispose de **sa propre base de données MySQL** et communique via **REST**.

---

## Flux Fonctionnels

### 1. Inscription / Connexion
- L’utilisateur s’inscrit avec un email et un mot de passe.
- Un token JWT est généré à la connexion.

### 2. Consultation des civilisations
- L’utilisateur peut récupérer toutes les civilisations disponibles.

### 3. Affichage d’une frise chronologique
- Une frise est générée automatiquement à partir de la période de la civilisation.

### 4. Exploration des événements
- Les événements sont consultables et rattachés à une civilisation.
- Chaque événement peut contenir des liens médias.

### 5. Commentaires
- Les utilisateurs peuvent commenter des **événements uniquement**.

---

## Services et responsabilités

### Auth Service
- Authentification (JWT)
- Inscription, login
- Gestion des rôles utilisateurs

### Media Service
- Sauvegarde d’**URL de médias externes** (image ou vidéo)
- Aucun upload de fichier local

### Event Service
- Gestion des entités :
  - **Civilization** : nom, description, date de début et de fin
  - **Event** : date, titre, description, lien avec une civilisation, liste de médias
  - **Comment** : commentaire rattaché à un événement, associé à un utilisateur

---

## Technologies

- Java 21
- Spring Boot 3.4
- Spring Data JPA
- Spring Security
- JWT
- Docker (pour les bases MySQL uniquement)
- Lombok
- MapStruct
- H2 (tests)

---

## Lancement du projet

1. Lancer les containers Docker pour les bases de données :
```bash
docker-compose up -d
```

2. Démarrer chaque service avec Maven ou depuis votre IDE :
```bash
cd auth-service
./mvnw spring-boot:run

cd media-service
./mvnw spring-boot:run

cd event-service
./mvnw spring-boot:run
```

---

## Structure des bases de données

Chaque service utilise une base de données séparée.

### auth_db
- `users`: id, email, password, role, created_at, updated_at

### media_db
- `media`: id, url, type (IMAGE|YOUTUBE), event_id

### event_db
- `civilizations`: id, name, description, start_date, end_date
- `events`: id, title, date, description, civilization_id
- `comments`: id, content, created_at, user_id, event_id

---

## Diagramme des services

```mermaid
graph TD
    subgraph Gateway (optionnel)
        G[API Gateway]
    end

    G --> AUTH[Auth Service]
    G --> MEDIA[Media Service]
    G --> EVENT[Event Service]

    AUTH --> DB_AUTH[(auth_db)]
    MEDIA --> DB_MEDIA[(media_db)]
    EVENT --> DB_EVENT[(event_db)]

    EVENT --> CIVILIZATION[Table: Civilization]
    EVENT --> COMMENT[Table: Comment]
    EVENT --> EVENT_TABLE[Table: Event]
```
