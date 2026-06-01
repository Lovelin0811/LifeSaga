# LifeSaga（人生副本）项目文档

## 1. 项目概述

**LifeSaga（人生副本）** 是一个人生记录类微信小程序，用 RPG「副本」概念标记人生重要时间线，通过添加「节点」记录精彩瞬间。包含游戏化元素如经验值 (XP)、等级 (Level)、稀有度 (Rarity) 和成就 (Achievement) 系统。

- **后端端口**：3000
- **API 基础路径**：`/api`
- **认证方式**：微信小程序登录 + JWT Bearer Token

---

## 2. 技术栈

| 层级 | 技术 | 版本 |
|------|------|------|
| 后端框架 | Spring Boot | 3.3.4 |
| 运行环境 | Java | 21 |
| 数据库 | MySQL | 8.x (utf8mb4) |
| 数据访问 | Spring JDBC (JdbcTemplate) | 纯手写 SQL，无 ORM |
| 认证 | jjwt | 0.12.6 (HS256) |
| 构建工具 | Maven | - |
| 前端 | 微信小程序原生框架 | - |

---

## 3. 项目目录结构

```
LifeSaga/
├── backend/
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/lovelin/lifesaga/
│       │   ├── LifesagaApplication.java
│       │   ├── config/
│       │   │   ├── DataSourceConfig.java        # 事务管理器
│       │   │   └── WebConfig.java               # CORS / Filter 注册 / 静态资源
│       │   ├── controller/
│       │   │   ├── AuthController.java           # 微信登录
│       │   │   ├── UserController.java           # 用户信息
│       │   │   ├── SagaController.java           # 副本 CRUD
│       │   │   ├── NodeController.java           # 节点 CRUD（嵌套路由）
│       │   │   ├── AchievementController.java    # 成就查询
│       │   │   ├── UploadController.java         # 文件上传
│       │   │   └── GlobalExceptionHandler.java   # 全局异常
│       │   ├── dto/
│       │   │   └── UserVO.java                  # 安全用户视图（脱敏）
│       │   ├── filter/
│       │   │   └── AuthFilter.java              # JWT 认证过滤器
│       │   ├── model/
│       │   │   ├── User.java
│       │   │   ├── Saga.java
│       │   │   ├── SagaNode.java
│       │   │   ├── Achievement.java
│       │   │   └── UserAchievement.java
│       │   ├── repository/
│       │   │   ├── UserRepository.java
│       │   │   ├── SagaRepository.java
│       │   │   ├── NodeRepository.java
│       │   │   ├── AchievementRepository.java
│       │   │   └── UserAchievementRepository.java
│       │   ├── service/
│       │   │   ├── AuthService.java
│       │   │   ├── UserService.java
│       │   │   ├── SagaService.java
│       │   │   ├── NodeService.java
│       │   │   └── AchievementService.java
│       │   └── util/
│       │       └── JwtUtil.java
│       └── resources/
│           ├── application.yml
│           ├── application.example.yml
│           └── db/schema.sql
│
├── miniprogram/
│   ├── app.js / app.json / app.wxss
│   ├── config.js
│   ├── utils/
│   │   ├── api.js           # 统一请求封装 + 业务 API
│   │   └── util.js          # 日期格式化、类型字典、稀有度配置
│   └── pages/
│       ├── home/            # 首页（副本列表）
│       ├── detail/          # 副本详情（时间线）
│       ├── create/          # 创建/编辑副本
│       ├── add-node/        # 添加/编辑节点
│       ├── node-detail/     # 节点详情
│       ├── discover/        # 广场（预留）
│       ├── achievements/    # 成就展示
│       ├── albums/          # 我的相册
│       ├── stats/           # 数据统计
│       └── profile/         # 个人中心
│
└── README.md
```

---

## 4. API 接口文档

### 统一规范

**响应格式**：
```json
{
  "code": 200,
  "data": { ... },
  "message": "success"
}
```

**状态码**：200 成功 / 400 业务错误 / 401 未登录 / 403 无权 / 500 服务器错误

**认证要求**：除 `/api/auth/**` 外，所有接口需携带 `Authorization: Bearer <token>`

---

### 4.1 认证模块

| 方法 | 路径 | 功能 | 请求体 | 响应 | 认证 |
|------|------|------|--------|------|------|
| POST | `/api/auth/wechat-login` | 微信登录 | `{"code":"wx.login()返回的code"}` | `{"token":"...","user":UserVO}` | 无 |

### 4.2 用户模块

| 方法 | 路径 | 功能 | 请求体 | 响应 | 认证 |
|------|------|------|--------|------|------|
| GET | `/api/users/me` | 获取当前用户 | - | UserVO | 是 |
| PUT | `/api/users/me` | 更新用户信息 | `{"nickname":"","avatarUrl":""}` | UserVO | 是 |

### 4.3 副本模块

| 方法 | 路径 | 功能 | 请求体 | 响应 | 权限 |
|------|------|------|--------|------|------|
| GET | `/api/sagas` | 我的副本列表 | `keyword`（可选） | Saga[] | 仅自己的 |
| POST | `/api/sagas` | 创建副本 | `{"name","type","coverUrl","description"}` | Saga | - |
| GET | `/api/sagas/public` | 公开副本列表 | `keyword`（可选） | Saga[] | 无 / 公开 |
| GET | `/api/sagas/{id}` | 副本详情（含节点） | - | `{"saga":Saga,"nodes":SagaNode[]}` | 仅自己的 |
| PUT | `/api/sagas/{id}` | 更新副本 | `{"name","type","coverUrl","description"}` | Saga | 仅自己的 |
| PUT | `/api/sagas/{id}/complete` | 完成副本 | - | Saga | 仅自己的 |
| DELETE | `/api/sagas/{id}` | 删除副本（级联删除节点） | - | `{"code":200}` | 仅自己的 |

### 4.4 节点模块

所有节点接口嵌套在副本路径：`/api/sagas/{sagaId}/nodes`

| 方法 | 路径 | 功能 | 请求体 | 响应 | 权限 |
|------|------|------|--------|------|------|
| GET | `/api/sagas/{sagaId}/nodes` | 节点列表 | - | SagaNode[] | 仅自己的 |
| POST | `/api/sagas/{sagaId}/nodes` | 创建节点 | `{"title","content","location","latitude","longitude","nodeTime","photos","milestone","sortOrder"}` | SagaNode | 仅自己的 |
| GET | `/api/sagas/{sagaId}/nodes/{id}` | 节点详情 | - | SagaNode | 仅自己的 |
| PUT | `/api/sagas/{sagaId}/nodes/{id}` | 更新节点 | 同创建 | SagaNode | 仅自己的 |
| PUT | `/api/sagas/{sagaId}/nodes/{id}/toggle-milestone` | 切换里程碑 | - | SagaNode | 仅自己的 |
| PUT | `/api/sagas/{sagaId}/nodes/{id}/favorite` | 切换收藏 | - | `{"favorited":bool}` | 仅自己的 |
| DELETE | `/api/sagas/{sagaId}/nodes/{id}` | 删除节点 | - | `{"code":200}` | 仅自己的 |

### 4.5 成就模块

| 方法 | 路径 | 功能 | 响应 | 认证 |
|------|------|------|------|------|
| GET | `/api/achievements` | 全部成就（含解锁状态） | Achievement[] | 是 |
| GET | `/api/achievements/my` | 我的成就 | Achievement[] | 是 |

### 4.6 文件上传

| 方法 | 路径 | 功能 | 参数 | 响应 | 限制 |
|------|------|------|------|------|------|
| POST | `/api/upload` | 上传图片 | multipart `file` | `{"url":"..."}` | 20MB / JPEG,PNG,WebP,GIF |

---

## 5. 数据库表结构

### 5.1 users

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | 自增 |
| openid | VARCHAR(64) UNIQUE | 微信 openid |
| nickname | VARCHAR(64) | 昵称 |
| avatar_url | VARCHAR(512) | 头像 URL |
| level | INT DEFAULT 1 | 等级 |
| xp | INT DEFAULT 0 | 经验值 |
| created_at | TIMESTAMP | |
| updated_at | TIMESTAMP | |

### 5.2 sagas

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | 自增 |
| user_id | BIGINT INDEX | 所属用户 |
| name | VARCHAR(128) | 副本名称 |
| type | VARCHAR(32) | life/travel/study/work/health/creative |
| cover_url | VARCHAR(512) | 封面图 |
| description | TEXT | 简介 |
| status | VARCHAR(16) INDEX | active/completed |
| is_public | TINYINT(1) | 是否公开 |
| node_count | INT DEFAULT 0 | 节点数 |
| rarity | VARCHAR(16) | 稀有度（自动计算） |
| started_at | TIMESTAMP | |
| ended_at | TIMESTAMP | |
| created_at | TIMESTAMP | |
| updated_at | TIMESTAMP | |

### 5.3 saga_nodes

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | 自增 |
| saga_id | BIGINT INDEX | 所属副本 |
| title | VARCHAR(256) | 标题 |
| content | TEXT | 内容（最多 5000 字） |
| location | VARCHAR(256) | 位置名称 |
| latitude | DECIMAL(10,7) | 纬度 |
| longitude | DECIMAL(10,7) | 经度 |
| node_time | TIMESTAMP INDEX | 节点时间 |
| photos | TEXT | JSON 数组 |
| is_milestone | TINYINT(1) | 是否里程碑 |
| sort_order | INT | 排序编号（节点列表优先按此排序） |
| created_at | TIMESTAMP | |
| updated_at | TIMESTAMP | |

### 5.4 achievements

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | 自增 |
| code | VARCHAR(64) UNIQUE | 成就编码 |
| name | VARCHAR(128) | 名称 |
| description | VARCHAR(512) | 描述 |
| icon | VARCHAR(64) | 图标 |
| rarity | VARCHAR(16) | 稀有度 |
| condition_type | VARCHAR(64) | 条件类型 |
| condition_value | INT | 条件值 |
| xp_reward | INT | XP 奖励 |

**预置成就**：

| code | 名称 | 条件 | 稀有度 | XP |
|------|------|------|--------|-----|
| first_saga | 冒险新手 | 创建第一个副本 | common | 10 |
| first_node | 记录者 | 添加第一个节点 | common | 10 |
| first_photo | 摄影师 | 添加第一张照片 | common | 10 |
| saga_types_count | 探险家 | 创建 3 个不同类型副本 | rare | 50 |
| completed_type_travel | 旅行家 | 完成 1 个旅行副本 | rare | 50 |
| completed_type_study | 学霸 | 完成 1 个学习副本 | rare | 50 |
| total_sagas | 收藏家 | 累计 10 个副本 | epic | 100 |
| has_legendary | 传说猎人 | 获得第一个传说副本 | epic | 100 |
| streak_days | 连续记录 | 连续 7 天添加节点 | epic | 80 |
| all_types_completed | 全满贯 | 所有类型副本各完成一个 | legendary | 200 |

### 5.5 user_achievements

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | 自增 |
| user_id | BIGINT | 用户 ID |
| achievement_id | BIGINT | 成就 ID |
| unlocked_at | TIMESTAMP | 解锁时间 |

UNIQUE(user_id, achievement_id)

---

## 6. 认证流程

```
小程序                        后端
  |                            |
  |-- wx.login() → code ------>|
  |                            |-- jscode2session → openid
  |                            |-- 查找/创建用户
  |                            |-- 生成 JWT (HS256, 7天有效)
  |<--- token + UserVO --------|
  |                            |
  |-- 存储 token 到 Storage ---|
  |                            |
  |-- API 请求 + Bearer token->|
  |                            |-- AuthFilter 验证 JWT
  |                            |-- 注入 userId → request
  |<--- 业务数据 ---------------|
```

**开发模式**：当 `AUTH_DEV_LOGIN_ENABLED=true` 且在 dev profile 下，使用 `dev_<code>` 模拟 openid，绕过微信 API 调用。

**自动重试**：小程序 API 层遇到 401 时自动 logout → login → 重试原请求。

---

## 7. 核心业务逻辑

### 7.1 稀有度计算

| 节点数 | 稀有度 |
|--------|--------|
| ≥ 30 | mythic（神话） |
| ≥ 21 | legendary（传说） |
| ≥ 11 | epic（史诗） |
| ≥ 6 | rare（稀有） |
| ≥ 3 | uncommon（优秀） |
| < 3 | common（普通） |

每次创建/删除节点时自动更新 `nodeCount` 和 `rarity`。

### 7.2 成就检查

**触发时机**：
- 创建副本时 → first_saga / saga_types_count / total_sagas / has_legendary / all_types_completed
- 完成副本时 → completed_type_* / all_types_completed
- 创建节点时 → first_node / first_photo / streak_days

### 7.3 完成副本流程

1. 用户在副本详情页点击「完成副本」。
2. 前端调用 `PUT /api/sagas/{id}/complete`。
3. 后端校验副本归属，写入 `status = completed` 和 `ended_at = now()`。
4. 后端同步检查完成类成就，并更新首页的已完成统计。
5. 详情页与首页都会显示最新状态。

**流程**：查成就定义 → 查是否已解锁 → 执行条件判断 → 插入 user_achievements → 增加 XP

### 7.4 权限控制（三层防御）

| 层级 | 措施 |
|------|------|
| Controller | verifyOwnership 校验 saga 归属，不匹配抛异常/返回 403 |
| Service | getById/update 校验 `node.sagaId == sagaId` |
| Repository | SQL WHERE 条件加 `AND saga_id = ?`（深度防御） |

### 7.5 文件上传安全

- 大小限制：20MB（Spring + 代码双层校验）
- ContentType 白名单：JPEG / PNG / WebP / GIF
- magic bytes 校验：验证文件头与声称类型一致
- 文件名：UUID 随机命名，不可预测
- 存储：按日期分目录 `yyyy/MM/dd/UUID.ext`

---

## 8. 小程序页面

### 8.1 TabBar 页面

| 标签 | 路径 | 功能 |
|------|------|------|
| 首页 | pages/home/home | 副本列表 + 统计 |
| 广场 | pages/discover/discover | 预留 |
| 创建 | pages/create/create | 创建/编辑副本 |
| 成就 | pages/achievements/achievements | 成就总览 |
| 我的 | pages/profile/profile | 个人中心 |

### 8.2 子页面

| 页面 | 路径 | 功能 |
|------|------|------|
| 副本详情 | pages/detail/detail | 时间线 + 操作面板 |
| 节点详情 | pages/node-detail/node-detail | 照片轮播 + 内容 + 操作 |
| 添加节点 | pages/add-node/add-node | 表单（时间/地点/照片/标题/描述/里程碑） |

### 8.3 设计系统

- **风格**：Warm Editorial + RPG 游戏感
- **主色**：珊瑚暖阳 `#E8725A` + 蜂蜜暖金 `#E5A44D`
- **副本类型**：生活 / 旅行 / 学习 / 工作 / 健身 / 创作（每种有独立 emoji + 色值）
- **稀有度体系**：common → uncommon → rare → epic → legendary → mythic

---

## 9. 配置项参考

| 配置项 | 环境变量 | 默认值 | 说明 |
|--------|----------|--------|------|
| server.port | - | 3000 | 服务端口 |
| datasource.url | - | jdbc:mysql://127.0.0.1:3306/lifesaga | 数据库连接 |
| datasource.username | `DB_USERNAME` | (空) | 数据库用户 |
| datasource.password | `DB_PASSWORD` | (空) | 数据库密码 |
| wechat.app-id | `WECHAT_APP_ID` | (空) | 微信 AppID |
| wechat.app-secret | `WECHAT_APP_SECRET` | (空) | 微信 AppSecret |
| jwt.secret | `JWT_SECRET` | (空) | JWT 密钥（≥32 字符） |
| jwt.expiration | - | 604800000 | Token 有效期（7天） |
| upload.dir | `UPLOAD_DIR` | ./uploads | 上传目录 |
| upload.public-base-url | `UPLOAD_PUBLIC_BASE_URL` | (空) | 图片公网访问前缀 |
| auth.dev-login-enabled | `AUTH_DEV_LOGIN_ENABLED` | false | 开发环境 mock 登录 |

---

## 10. 安全措施

| 措施 | 实现位置 |
|------|----------|
| JWT 认证 + 过期 | AuthFilter + JwtUtil |
| 用户归属校验 | SagaController / NodeController |
| 节点 sagaId 一致性校验 | NodeService + NodeRepository（三层防御） |
| SQL 注入防护 | 全参数化查询（JdbcTemplate + ? 占位符） |
| 用户信息脱敏 | UserVO 排除 openid |
| 文件类型校验 | MIME 白名单 + magic bytes |
| CORS 白名单 | WebConfig，限定具体域名 |
| 异常信息不泄露 | GlobalExceptionHandler 白名单机制 |
| 生产配置隔离 | local.properties 不提交仓库 |
| 密钥环境变量注入 | JWT / DB / WeChat 密钥均通过环境变量 |
