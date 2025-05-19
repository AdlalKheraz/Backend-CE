# Guide des Tests Postman pour l'Application Chrono

Ce document explique comment exécuter la collection Postman pour tester l'ensemble des fonctionnalités de l'application Chrono via ses microservices.

## Prérequis

Avant de commencer les tests, assurez-vous que :

1. Tous les microservices sont démarrés :
   - Gateway Service (port 8080)
   - Auth Service (port 8081)
   - Media Service (port 8082)
   - Event Service (port 8083)

2. Les bases de données sont accessibles :
   - auth_db (port 3307)
   - media_db (port 3309)
   - event_db (port 3308)

3. Vous avez importé la collection `postman_collection.json` dans Postman

## Déroulement des Tests

La collection est organisée de manière à suivre un flux d'utilisation typique de l'application. Suivez ces étapes dans l'ordre pour tester l'ensemble du système.

### 1. Service d'Authentification

#### 1.1 Register User
- **Objectif** : Créer un nouvel utilisateur
- **Résultat attendu** : Status 200 OK, un token JWT est retourné
- **Action automatique** : Le token est stocké dans la variable globale `{{token}}`

#### 1.2 Login User
- **Objectif** : Se connecter avec l'utilisateur créé précédemment
- **Résultat attendu** : Status 200 OK, un token JWT est retourné
- **Action automatique** : Le token est mis à jour dans la variable globale `{{token}}`

### 2. Gestion des Civilisations

#### 2.1 Create Civilization
- **Objectif** : Créer une civilisation (Égypte Antique)
- **Authentification** : Utilise le token JWT obtenu précédemment
- **Résultat attendu** : Status 201 Created, données de la civilisation créée
- **Action automatique** : L'ID de la civilisation est stocké dans la variable `{{civilizationId}}`

#### 2.2 Get All Civilizations
- **Objectif** : Récupérer la liste des civilisations
- **Authentification** : Token JWT
- **Résultat attendu** : Status 200 OK, liste contenant la civilisation créée

#### 2.3 Update Civilization
- **Objectif** : Mettre à jour les informations de la civilisation
- **Authentification** : Token JWT
- **Résultat attendu** : Status 200 OK, données mises à jour

### 3. Gestion des Événements

#### 3.1 Create Event
- **Objectif** : Créer un événement historique pour la civilisation
- **Authentification** : Token JWT
- **Dépendance** : Utilise `{{civilizationId}}` de l'étape 2.1
- **Résultat attendu** : Status 200 OK, données de l'événement
- **Action automatique** : L'ID de l'événement est stocké dans `{{eventId}}`

#### 3.2 Get All Events
- **Objectif** : Récupérer la liste de tous les événements
- **Authentification** : Token JWT
- **Résultat attendu** : Status 200 OK, liste contenant l'événement créé

#### 3.3 Get Events By Civilization
- **Objectif** : Récupérer les événements pour une civilisation spécifique
- **Authentification** : Token JWT
- **Dépendance** : Utilise `{{civilizationId}}`
- **Résultat attendu** : Status 200 OK, liste des événements de la civilisation

### 4. Gestion des Commentaires

#### 4.1 Create Comment
- **Objectif** : Ajouter un commentaire à un événement
- **Authentification** : Token JWT
- **Dépendance** : Utilise `{{eventId}}` de l'étape 3.1
- **Résultat attendu** : Status 200 OK, données du commentaire créé

#### 4.2 Get Comments By Event
- **Objectif** : Récupérer les commentaires pour un événement
- **Authentification** : Token JWT
- **Dépendance** : Utilise `{{eventId}}`
- **Résultat attendu** : Status 200 OK, liste contenant le commentaire créé

### 5. Gestion des Médias

#### 5.1 Add Media
- **Objectif** : Ajouter un média à un événement
- **Authentification** : Token JWT
- **Dépendance** : Utilise `{{eventId}}`
- **Résultat attendu** : Status 200 OK, données du média créé

#### 5.2 Get Media By Event
- **Objectif** : Récupérer les médias pour un événement
- **Authentification** : Token JWT
- **Dépendance** : Utilise `{{eventId}}`
- **Résultat attendu** : Status 200 OK, liste contenant le média ajouté

### 6. Tests de Sécurité (Accès Non Autorisé)

#### 6.1 Get Events Without Token
- **Objectif** : Tester l'accès sans token
- **Résultat attendu** : Status 401 Unauthorized

#### 6.2 Get Media Without Token
- **Objectif** : Tester l'accès sans token
- **Résultat attendu** : Status 401 Unauthorized

#### 6.3 Create Civilization With Invalid Token
- **Objectif** : Tester l'accès avec un token invalide
- **Résultat attendu** : Status 401 Unauthorized

## Diagnostic des Problèmes Courants

### Si les tests échouent

1. **Problèmes d'authentification (401 Unauthorized)** :
   - Vérifier que le service d'authentification fonctionne correctement
   - S'assurer que la clé secrète JWT est la même dans auth-service et gateway-service

2. **Erreurs 404 Not Found** :
   - Vérifier que les routes dans la gateway sont correctement configurées
   - Confirmer que les microservices sont en cours d'exécution

3. **Erreurs 500 Internal Server Error** :
   - Consulter les logs des microservices pour identifier le problème
   - Vérifier la connexion à la base de données

## Flux d'Exécution Recommandé

Pour un test complet du système, exécutez les requêtes dans l'ordre suivant :

1. Créer un utilisateur (1.1)
2. Se connecter (1.2) 
3. Créer une civilisation (2.1)
4. Vérifier la liste des civilisations (2.2)
5. Créer un événement (3.1)
6. Vérifier les événements (3.2 et 3.3)
7. Ajouter un commentaire (4.1)
8. Vérifier les commentaires (4.2)
9. Ajouter un média (5.1)
10. Vérifier les médias (5.2)
11. Tester les cas d'erreur (6.1, 6.2, 6.3)

Vous pouvez également utiliser la fonctionnalité "Run Collection" de Postman pour exécuter toute la collection en ordre automatiquement. 