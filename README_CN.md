# CyanChatRoomServer

[English](README.md) | 中文

一个基于 Java + Netty 的实时聊天服务器，支持 WebSocket 通信、群组聊天、文件传输等功能。

一个配套的服务端：https://github.com/NekoCCN/CyanChatRoomClient

## 简介

一个完整的实时聊天服务器实现，包括用户系统、好友关系、群组管理、离线消息等功能。

技术栈：
- Java 22
- Netty 4.2.2 - 网络通信
- MyBatis 3.5.19 - 数据库操作
- PostgreSQL - 数据存储
- HikariCP - 连接池
- GSON - JSON 处理

## 主要功能

- 用户注册和登录（密码用 BCrypt 加密）
- 实时消息收发（WebSocket）
- 好友系统（添加好友、好友列表）
- 群组聊天（创建群组、成员管理、权限控制）
- 离线消息（用户不在线时自动存储）
- 文件传输（HTTP 上传下载，最大 100MB）
- 端到端加密支持（公钥管理）

## 快速开始

### 准备工作

需要先安装：
- Java 22
- PostgreSQL
- Maven

### 数据库设置

1. 创建数据库：

```sql
CREATE DATABASE cyan_chat_server_db;
CREATE USER cyan_chat_server_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE cyan_chat_server_db TO cyan_chat_server_user;
```

2. 建表（表结构比较多，包括用户、群组、好友、消息、文件等表）

主要的表：
- `users` - 用户信息
- `groups` - 群组
- `group_members` - 群成员
- `friendships` - 好友关系
- `offline_messages` - 离线消息
- `files` - 文件元数据

具体建表 SQL 可以查看源码中的 mapper 文件，或者参考下面的完整建表语句。

<details>
<summary>点击展开完整建表 SQL</summary>

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

### 配置

1. 修改 `src/main/resources/hikari.properties` 中的数据库连接信息

2. 创建 `config.json` 配置文件：

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

### 运行

```bash
# 编译打包
mvn clean package

# 运行
java -jar target/CyanChatRoomServer-1.0-jar-with-dependencies.jar config.json
```

启动后会监听配置的端口（默认 8080），客户端通过 `ws://localhost:8080/ws` 连接。

## 项目结构

采用 DDD 分层架构：

```
src/main/java/cc/nekocc/cyanchatroomserver/
├── presentation/        # 表现层 - Netty 处理器和命令分发
├── application/         # 应用层 - 业务服务实现
├── domain/             # 领域层 - 实体和仓储接口
├── infrastructure/     # 基础设施层 - 数据库、会话管理
└── protocol/           # 协议定义
```

所有消息都是 JSON 格式：
```json
{
    "type": "MESSAGE_TYPE",
    "payload": { ... }
}
```

## 已知问题

- 会话管理用的是内存 `ConcurrentHashMap`，服务器重启后用户要重新连接
- 不支持多实例部署（会话状态在内存里）
- 文件存储在本地，没法分布式部署
- 缺少测试用例
- 有些类写得比较大，需要重构

## TODO

1. **Redis 缓存** - 把会话状态和热点数据放到 Redis，这样就能多实例部署了
2. **对象存储** - 用 MinIO 或 S3 替代本地文件系统
3. **补测试** - 至少加一些核心功能的测试
4. **消息已读/撤回** - 现在还不支持这些基础功能
5. **代码重构** - 有些类确实写得太大了

## 部署

可以用 Docker 部署，参考这个 `docker-compose.yml`：

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

如果用 Nginx 做反向代理，记得配置 WebSocket 升级：

```nginx
location /ws {
    proxy_pass http://localhost:8080;
    proxy_http_version 1.1;
    proxy_set_header Upgrade $http_upgrade;
    proxy_set_header Connection "upgrade";
}
```

## 协议文档

主要的消息类型：

**用户相关：**
- `REGISTER_REQUEST` / `REGISTER_RESPONSE` - 注册
- `LOGIN_REQUEST` - 登录
- `UPDATE_PROFILE_REQUEST` - 更新资料
- `CHANGE_PASSWORD_REQUEST` - 改密码

**消息相关：**
- `CHAT_MESSAGE` - 发送消息（单聊或群聊）

**好友相关：**
- `SEND_FRIENDSHIP_REQUEST` - 发送好友请求
- `ACCEPT_FRIENDSHIP_REQUEST` - 接受好友
- `GET_FRIENDSHIP_LIST_REQUEST` - 获取好友列表

**群组相关：**
- `CREATE_GROUP_REQUEST` - 创建群组
- `JOIN_GROUP_REQUEST` - 加入群组
- `GET_GROUP_MEMBERS_REQUEST` - 获取群成员

**文件相关：**
- `REQUEST_FILE_UPLOAD` - 请求上传
- `POST /api/files/upload/{fileId}` - 上传文件（HTTP）
- `GET /api/files/download/{fileId}` - 下载文件（HTTP）

更多消息类型可以查看 `MessageType.java`。

## 许可证

MIT License

## 贡献

欢迎提交 Issue 和 Pull Request。

