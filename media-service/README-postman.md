# Media Service Postman Collection

Ce fichier contient une collection Postman pour tester l'API du service média.

## Instructions d'utilisation

1. Téléchargez et installez [Postman](https://www.postman.com/downloads/)
2. Importez le fichier `media-service-postman-collection.json` dans Postman:
   - Ouvrez Postman
   - Cliquez sur "Import" en haut à gauche
   - Sélectionnez le fichier `media-service-postman-collection.json`

## Endpoints disponibles

La collection contient les endpoints suivants:

### 1. Ajouter un média (Image) - POST
- URL: `http://localhost:8082/api/media`
- Ajoute un nouveau média de type image
- Corps de la requête:
```json
{
    "url": "https://example.com/image.jpg",
    "type": "IMAGE",
    "eventId": 1
}
```

### 2. Ajouter un média (Vidéo) - POST
- URL: `http://localhost:8082/api/media`
- Ajoute un nouveau média de type vidéo
- Corps de la requête:
```json
{
    "url": "https://example.com/video.mp4",
    "type": "VIDEO",
    "eventId": 1
}
```

### 3. Obtenir les médias par événement - GET
- URL: `http://localhost:8082/api/media/event/{eventId}`
- Récupère tous les médias associés à un événement spécifique
- Remplacez `{eventId}` par l'ID de l'événement (par exemple, 1)

## Prérequis

- L'application Media Service doit être en cours d'exécution sur le port 8082
- La base de données MySQL doit être accessible sur le port 3309 