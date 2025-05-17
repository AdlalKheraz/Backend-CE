# Chrono Event Service

A Spring Boot microservice for managing historical civilizations, events, and comments.

## Overview

Chrono Event Service provides a RESTful API to track historical civilizations and their significant events. The service allows users to:

- Create, read, update, and delete civilizations
- Create and retrieve events associated with specific civilizations
- Add and retrieve comments on historical events

## Technologies

- Java 17
- Spring Boot 3.x
- Spring Data JPA
- MySQL
- Maven

## Setup

### Prerequisites

- JDK 17+
- Maven
- MySQL 8.0+

### Database Configuration

The application is configured to connect to a MySQL database:

```
Database URL: jdbc:mysql://localhost:3308/event_db
Username: root
Password: root
```

Ensure your MySQL server is running on port 3308 or update the configuration in `application.properties`.

### Building the Application

```bash
mvn clean install
```

### Running the Application

```bash
mvn spring-boot:run
```

The service will start on port 8082.

## API Documentation

### Civilizations

#### Create a Civilization

- **URL**: `/api/civilizations`
- **Method**: `POST`
- **Request Body**:
```json
{
  "name": "Egypt",
  "description": "Ancient civilization in North Africa",
  "startDate": "3000-01-01",
  "endDate": "0030-01-01"
}
```
- **Success Response**: 201 CREATED

#### Get All Civilizations

- **URL**: `/api/civilizations`
- **Method**: `GET`
- **Success Response**: 200 OK

#### Update a Civilization

- **URL**: `/api/civilizations/{id}`
- **Method**: `PUT`
- **Request Body**:
```json
{
  "name": "Ancient Egypt",
  "description": "Updated description",
  "startDate": "3100-01-01",
  "endDate": "0030-01-01"
}
```
- **Success Response**: 200 OK

#### Delete a Civilization

- **URL**: `/api/civilizations/{id}`
- **Method**: `DELETE`
- **Success Response**: 204 NO CONTENT

### Events

#### Create an Event

- **URL**: `/api/events`
- **Method**: `POST`
- **Request Body**:
```json
{
  "title": "Building of Pyramids",
  "description": "Construction of the Great Pyramids at Giza",
  "date": "2550-01-01",
  "civilizationId": 1,
  "type": "CULTURAL"
}
```
- **Available Event Types**: `POLITICAL`, `MILITARY`, `CULTURAL`, `SCIENTIFIC`, `OTHER`
- **Success Response**: 201 CREATED

#### Get All Events

- **URL**: `/api/events`
- **Method**: `GET`
- **Success Response**: 200 OK

#### Get Events By Civilization

- **URL**: `/api/events/civilization/{id}`
- **Method**: `GET`
- **Success Response**: 200 OK

### Comments

#### Create a Comment

- **URL**: `/api/comments`
- **Method**: `POST`
- **Request Body**:
```json
{
  "authorEmail": "user@example.com",
  "content": "Fascinating event!",
  "eventId": 1
}
```
- **Success Response**: 201 CREATED

#### Get Comments By Event

- **URL**: `/api/comments/event/{eventId}`
- **Method**: `GET`
- **Success Response**: 200 OK

## Testing with Postman

A Postman collection is provided in the repository (`chrono-event-service.postman_collection.json`). Import this collection into Postman to test all available endpoints.

The collection includes a variable `base_url` that is set to `http://localhost:8082` by default.

## Database Schema

The service uses a MySQL database with the following tables:

- `civilization`: Stores historical civilizations with start and end dates
- `event`: Stores historical events linked to civilizations
- `comment`: Stores user comments on historical events

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/add-new-endpoint`)
3. Commit your changes (`git commit -m 'Add new endpoint for timeline'`)
4. Push to the branch (`git push origin feature/add-new-endpoint`)
5. Open a Pull Request 