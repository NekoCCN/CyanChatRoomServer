# CyanChatRoomServer

English | [中文](README_CN.md)

A real-time chat server built with Java + Netty, supporting WebSocket communication, group chat, file transfer, and more.

A companion client: https://github.com/NekoCCN/CyanChatRoomClient

## Overview

A complete real-time chat server implementation with user system, friend relationships, group management, offline messages, and more.

Tech stack:
- Java 22
- Netty 4.2.2 - Network communication
- MyBatis 3.5.19 - Database operations
- PostgreSQL - Data storage
- HikariCP - Connection pool
- GSON - JSON processing

## Features

- User registration and login (passwords encrypted with BCrypt)
- Real-time messaging (WebSocket)
- Friend system (add friends, friend list)
- Group chat (create groups, member management, permission control)
- Offline messages (auto-stored when user is offline)
- File transfer (HTTP upload/download, max 100MB)
- End-to-end encryption support (public key management)

## Quick Start

### Prerequisites

You'll need:
- Java 22
- PostgreSQL
- Maven

### Database Setup

1. Create database:

```sql
CREATE DATABASE cyan_chat_server_db;
CREATE USER cyan_chat_server_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE cyan_chat_server_db TO cyan_chat_server_user;
```

2. Create tables (there are quite a few - users, groups, friends, messages, files, etc.)

Main tables:
- `users` - User information
- `groups` - Groups
- `group_members` - Group members
- `friendships` - Friend relationships
- `offline_messages` - Offline messages
- `files` - File metadata

Check the mapper files in source code for details, or use the full schema below.

<details>
<summary>Click to expand full schema SQL</summary>

```sql
CREATE TABLE users (
    id UUID PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    nickname VARCHAR(255),
    avatar_url VARCHAR(500),
    signature VARCHAR(500),
    public_key_bundle TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE groups (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    creator_id UUID NOT NULL REFERENCES users(id),
    join_mode VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE group_members (
    group_id UUID NOT NULL REFERENCES groups(id),
    user_id UUID NOT NULL REFERENCES users(id),
    role VARCHAR(50) NOT NULL,
    joined_at TIMESTAMP WITH TIME ZONE NOT NULL,
    PRIMARY KEY (group_id, user_id)
);

CREATE TABLE friendships (
    id UUID PRIMARY KEY,
    user_id_1 UUID NOT NULL REFERENCES users(id),
    user_id_2 UUID NOT NULL REFERENCES users(id),
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE files (
    id VARCHAR(255) PRIMARY KEY,
    uploader_id UUID NOT NULL REFERENCES users(id),
    original_name VARCHAR(500) NOT NULL,
    file_size BIGINT NOT NULL,
    mime_type VARCHAR(255),
    storage_path VARCHAR(1000) NOT NULL,
    status VARCHAR(50) NOT NULL,
    uploaded_at TIMESTAMP WITH TIME ZONE NOT NULL,
    expires_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE offline_messages (
    id UUID PRIMARY KEY,
    sender_id UUID NOT NULL REFERENCES users(id),
    recipient_type VARCHAR(50) NOT NULL,
    recipient_id UUID NOT NULL,
    content_type VARCHAR(50) NOT NULL,
    is_encrypted BOOLEAN NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE group_join_requests (
    id UUID PRIMARY KEY,
    group_id UUID NOT NULL REFERENCES groups(id),
    user_id UUID NOT NULL REFERENCES users(id),
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);
```

</details>

### Configuration

1. Update database connection in `src/main/resources/hikari.properties`

2. Create `config.json`:

```json
{
    "port": 8080,
    "business_thread_count": 50,
    "database_url": "jdbc:postgresql://localhost:5432/cyan_chat_server_db",
    "database_username": "cyan_chat_server_user",
    "database_password": "your_password",
    "heartbeat_interval_seconds": 60,
    "file_storage_path": "/var/cyan_chat_files",
    "file_cleanup_interval_minutes": 1440
}
```

### Running

```bash
# Build
mvn clean package

# Run
java -jar target/CyanChatRoomServer-1.0-jar-with-dependencies.jar config.json
```

The server will listen on the configured port (default 8080). Clients connect via `ws://localhost:8080/ws`.

## Project Structure

Uses DDD layered architecture:

```
src/main/java/cc/nekocc/cyanchatroomserver/
├── presentation/        # Presentation layer - Netty handlers and command dispatch
├── application/         # Application layer - Business service implementation
├── domain/             # Domain layer - Entities and repository interfaces
├── infrastructure/     # Infrastructure layer - Database, session management
└── protocol/           # Protocol definition
```

All messages are in JSON format:
```json
{
    "type": "MESSAGE_TYPE",
    "payload": { ... }
}
```

## Known Issues

- Session management uses in-memory `ConcurrentHashMap`, users need to reconnect after server restart
- Doesn't support multi-instance deployment (session state in memory)
- Files stored locally, can't do distributed deployment
- Lacks test cases
- Some classes are too big and need refactoring

## TODO

1. **Redis caching** - Move session state and hot data to Redis for multi-instance support
2. **Object storage** - Replace local filesystem with MinIO or S3
3. **Tests** - At least add some tests for core features
4. **Read receipts/Recall** - These basic features are missing
5. **Code refactoring** - Some classes are indeed too large

## Deployment

Can deploy with Docker, here's a sample `docker-compose.yml`:

```yaml
version: '3.8'

services:
  postgres:
    image: postgres:16
    environment:
      POSTGRES_DB: cyan_chat_server_db
      POSTGRES_USER: cyan_chat_server_user
      POSTGRES_PASSWORD: your_password
    volumes:
      - postgres_data:/var/lib/postgresql/data

  chat-server:
    build: .
    ports:
      - "8080:8080"
    volumes:
      - chat_files:/var/cyan_chat_files
    depends_on:
      - postgres

volumes:
  postgres_data:
  chat_files:
```

If using Nginx as reverse proxy, remember to configure WebSocket upgrade:

```nginx
location /ws {
    proxy_pass http://localhost:8080;
    proxy_http_version 1.1;
    proxy_set_header Upgrade $http_upgrade;
    proxy_set_header Connection "upgrade";
}
```

## Protocol Docs

Main message types:

**User:**
- `REGISTER_REQUEST` / `REGISTER_RESPONSE` - Register
- `LOGIN_REQUEST` - Login
- `UPDATE_PROFILE_REQUEST` - Update profile
- `CHANGE_PASSWORD_REQUEST` - Change password

**Messages:**
- `CHAT_MESSAGE` - Send message (private or group chat)

**Friends:**
- `SEND_FRIENDSHIP_REQUEST` - Send friend request
- `ACCEPT_FRIENDSHIP_REQUEST` - Accept friend
- `GET_FRIENDSHIP_LIST_REQUEST` - Get friend list

**Groups:**
- `CREATE_GROUP_REQUEST` - Create group
- `JOIN_GROUP_REQUEST` - Join group
- `GET_GROUP_MEMBERS_REQUEST` - Get group members

**Files:**
- `REQUEST_FILE_UPLOAD` - Request upload
- `POST /api/files/upload/{fileId}` - Upload file (HTTP)
- `GET /api/files/download/{fileId}` - Download file (HTTP)

See `MessageType.java` for more message types.

## License

MIT License

## Contributing

Issues and Pull Requests are welcome.


