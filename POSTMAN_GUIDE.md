# Chrono Explorer API Testing Guide

This guide helps you test the entire Chrono Explorer API using Postman, explaining the request order and how to properly configure them.

## Postman Collection

You can import the complete collection from the `Chrono-Explorer.postman_collection.json` file included in this repository.

## Prerequisites

1. Make sure all services are running:
   - Gateway Service (port 8080)
   - Auth Service (port 8081)
   - Media Service (port 8082)
   - Event Service (port 8083)
   - MySQL databases (via Docker)
   - MinIO (ports 9000 for API, 9001 for console)

2. Global parameters to configure in Postman:
   - `baseUrl`: http://localhost:8080
   - `enable_cookies`: true (in collection settings)

## Request Execution Order

⚠️ **IMPORTANT**: Requests must be executed in the order indicated as they depend on each other, especially for authentication.

### 1. Authentication Service

```mermaid
flowchart LR
    A[Register New User] --> B[Authenticate User]
    B --> C[Get User Profile]
```

#### 1.1. Register New User

```
POST {{baseUrl}}/api/auth/register
```

**Body** (JSON):
```json
{
  "email": "your_email@example.com",
  "password": "your_password",
  "firstName": "John",
  "lastName": "Doe"
}
```

**Expected result**: Status 200 OK

#### 1.2. Authenticate User

```
POST {{baseUrl}}/api/auth/login
```

**Body** (JSON):
```json
{
  "email": "your_email@example.com",
  "password": "your_password"
}
```

**Expected result**: 
- Status 200 OK
- Response body contains JWT token
- JWT token is automatically saved to the `token` variable

> 📌 **Important note**: The JWT token will be automatically used for all subsequent requests.

#### 1.3. Get User Profile

```
GET {{baseUrl}}/api/users/{{userId}}
```

**Expected result**: 
- Status 200 OK
- User details in JSON format

### 2. Civilization Service

#### 2.1. Create Civilization

```
POST {{baseUrl}}/api/civilizations
```

**Headers**:
- Content-Type: application/json

**Body** (JSON):
```json
{
  "name": "Roman Empire",
  "description": "The Roman Empire was one of the largest empires in world history.",
  "startDate": "0027-01-16",
  "endDate": "0476-09-04"
}
```

**Expected result**: 
- Status 201 Created
- Civilization ID in the response (keep this for later steps)

#### 2.2. Get All Civilizations

```
GET {{baseUrl}}/api/civilizations
```

**Expected result**:
- Status 200 OK
- List of civilizations in JSON format

### 3. Event Service

#### 3.1. Create Historical Event

```
POST {{baseUrl}}/api/events
```

**Headers**:
- Content-Type: application/json

**Body** (JSON):
```json
{
  "title": "Founding of Rome",
  "date": "0753-04-21",
  "description": "According to legend, Rome was founded by Romulus and Remus on the banks of the Tiber.",
  "civilizationId": {{civilizationId}}
}
```
> ⚠️ Replace `civilizationId` with the ID obtained in step 2.1

**Expected result**: 
- Status 201 Created
- Event ID in the response (keep this for later steps)

#### 3.2. Get All Historical Events

```
GET {{baseUrl}}/api/events
```

**Expected result**:
- Status 200 OK
- List of events in JSON format

#### 3.3. Get Events By Civilization

```
GET {{baseUrl}}/api/events/civilization/{{civilizationId}}
```
> ⚠️ Replace `civilizationId` with the ID obtained in step 2.1

**Expected result**:
- Status 200 OK
- List of events for the civilization in JSON format

### 4. Media Service

#### 4.1. Add Media by URL

```
POST {{baseUrl}}/api/media
```

**Headers**:
- Content-Type: application/json

**Body** (JSON):
```json
{
  "url": "https://upload.wikimedia.org/wikipedia/commons/d/d8/Ara_pacis_roma.JPG",
  "type": "IMAGE",
  "eventId": {{eventId}}
}
```
> ⚠️ Replace `eventId` with the ID obtained in step 3.1

**Expected result**:
- Status 201 Created
- Created media details in JSON format

#### 4.2. Upload Media File

```
POST {{baseUrl}}/api/media/upload
```

**Body** (form-data):
- `file`: Select an image or video file from your computer
- `type`: IMAGE (or VIDEO depending on file type)
- `eventId`: {{eventId}} (replace with the ID obtained in step 3.1)

> ⚠️ Do not set a Content-Type header, Postman will do this automatically

**Expected result**:
- Status 201 Created
- Created media details in JSON format with the generated URL pointing to MinIO

#### 4.3. Get Media For Event

```
GET {{baseUrl}}/api/media/event/{{eventId}}
```
> ⚠️ Replace `eventId` with the ID obtained in step 3.1

**Expected result**:
- Status 200 OK
- List of media for the event in JSON format

#### 4.4. Access Media File

```
GET {{baseUrl}}/api/media/files/{{filename}}
```
> ⚠️ Replace `filename` with the filename from the URL returned in step 4.2

**Expected result**:
- Status 200 OK
- The image or video file is displayed or downloaded

### 5. Comment Service

#### 5.1. Add Comment to Event

```
POST {{baseUrl}}/api/comments
```

**Headers**:
- Content-Type: application/json

**Body** (JSON):
```json
{
  "content": "What a fascinating era!",
  "eventId": {{eventId}}
}
```
> ⚠️ Replace `eventId` with the ID obtained in step 3.1

**Expected result**:
- Status 201 Created
- Created comment details in JSON format

#### 5.2. Get Comments For Event

```
GET {{baseUrl}}/api/comments/event/{{eventId}}
```
> ⚠️ Replace `eventId` with the ID obtained in step 3.1

**Expected result**:
- Status 200 OK
- List of comments for the event in JSON format

### 6. Security Testing

Various tests for security validation:

- **Access Events Without Authentication**: Should return 401 Unauthorized
- **Access Media Without Authentication**: Should return 401 Unauthorized
- **Create Civilization With Invalid Token**: Should return 401 Unauthorized

## Common Issues Troubleshooting

### 401 Unauthorized Error
- Check that you're properly logged in (step 1.2)
- Verify that cookies are enabled in the Postman collection
- Re-execute the login request to get a new token

### 404 Not Found Error
- Check that all services are running
- Verify you're using the correct URLs
- Check that the Gateway is properly configured

### 500 Internal Server Error
- Check the logs of the concerned service
- Verify that databases are accessible

## Complete Postman Collection

Here's the complete structure of the provided Postman collection:

```
Chrono Explorer API
├── Authentication Service
│   ├── Register New User
│   ├── Authenticate User
│   ├── Get User Profile
│   ├── Update User Profile
│   ├── Change User Role
│   └── Get All Users
├── Civilization Service
│   ├── Create Civilization
│   ├── Create Civilization (Direct to Service)
│   ├── Get All Civilizations
│   └── Update Civilization
├── Event Service
│   ├── Create Historical Event
│   ├── Get All Historical Events
│   └── Get Events By Civilization
├── Comment Service
│   ├── Add Comment to Event
│   └── Get Comments For Event
├── Media Service
│   ├── Add Media by URL
│   ├── Upload Media File
│   ├── Get Media For Event
│   └── Access Media File
└── Security Testing
    ├── Access Events Without Authentication
    ├── Access Media Without Authentication
    └── Create Civilization With Invalid Token
``` 