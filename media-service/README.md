# Media Service

Un microservice pour la gestion des médias (images et vidéos) associés à des événements.

## Description

Ce service fait partie de l'écosystème Chrono et permet de:
- Stocker des métadonnées de médias (URL et type)
- Lier des médias à des événements spécifiques
- Récupérer les médias associés à un événement donné

## Prérequis

- Java 21
- MySQL (sur le port 3309)
- Maven 3.x

## Base de données

Le service utilise MySQL avec les paramètres suivants:
- URL: `jdbc:mysql://localhost:3309/media_db`
- Utilisateur: `root`
- Mot de passe: `root`

Assurez-vous que la base de données `media_db` existe avant de démarrer l'application.

## Configuration

Le service fonctionne sur le port `8082`.

Toutes les configurations se trouvent dans le fichier `src/main/resources/application.properties`.

## Installation et démarrage

```bash
# Cloner le dépôt
git clone <repository-url>
cd media-service

# Compiler le projet
./mvnw clean install

# Démarrer l'application
./mvnw spring-boot:run
```

## API Endpoints

### 1. Ajouter un média

**POST** `/api/media`

Corps de la requête:
```json
{
    "url": "https://example.com/image.jpg",
    "type": "IMAGE",
    "eventId": 1
}
```

Types de médias disponibles: `IMAGE`, `VIDEO`

### 2. Obtenir les médias par événement

**GET** `/api/media/event/{eventId}`

Exemple: `/api/media/event/1`

## Tester avec Postman

Une collection Postman est disponible dans le fichier `media-service-postman-collection.json` à la racine du projet.

Pour l'utiliser:
1. Ouvrez Postman
2. Cliquez sur "Import"
3. Sélectionnez le fichier `media-service-postman-collection.json`

La collection contient des exemples de requêtes pour tous les endpoints disponibles.

## Structure du projet

```
src/main/java/com/chrono/media/
├── controller/      # Contrôleurs REST
├── service/         # Couche service (logique métier)
├── repository/      # Couche d'accès aux données
├── entity/          # Entités JPA
└── dto/             # Objets de transfert de données
```

## Technologies utilisées

- Spring Boot 3.4.5
- Spring Data JPA
- Spring Web
- MySQL
- Lombok
- Maven 