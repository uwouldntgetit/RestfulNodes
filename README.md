# Node Management REST API

REST API built with Spring Boot for managing node relationships with value propagation.

## Prerequisites
- Docker
- Docker Compose

## Quick Start

1. **Clone the repository:**
```bash
git clone https://github.com/uwouldntgetit/RestfulNodes
cd RESTMongo
```

2. **Build and run:**
```bash
docker-compose up --build
```

3. **API available at:** `http://localhost:8080/nodes`

## API Endpoints

### GET /nodes
Returns all nodes.

### GET /nodes/:id
Returns a specific node

### POST /nodes
Creates nodes.
```json
{
  "value": 30,
  "parents": ["A", "B"]
}
```

### PATCH /nodes/:id
Updates node value (propagates to connected nodes).
```json
{
  "value": 15
}
```

## Value Propagation Example
- Create A=1, B=13
- Create C=30 with parents A, B
- Update C to 15 (ratio = 0.5)
- Result: A=0.5, B=6.5, C=15

## Stop the application
```bash
docker-compose down
```

## Architecture
- **Controller**: REST endpoints
- **Service**: Business logic & propagation
- **Repository**: MongoDB access
```