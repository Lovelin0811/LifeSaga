# LifeSaga

LifeSaga 是一个微信小程序项目，前端在 `miniprogram/`，当前主后端在 `back/`。

| 项 | 说明 |
|---|---|
| 前端 | 原生微信小程序 |
| 后端 | Java 21、Spring Boot、MyBatis、MySQL |
| 认证 | 微信登录 + JWT |
| 默认端口 | `3000` |
| 上传目录 | `back/uploads/` 运行时生成 |

## 当前目录

| 路径 | 作用 |
|---|---|
| `miniprogram/` | 小程序前端 |
| `back/` | DDD 重构后的后端 |
| `backend/` | 历史旧后端，仅保留参考，不作为当前部署目标 |
| `docs/` | 项目文档与使用说明 |

## 本地启动

| 步骤 | 操作 |
|---|---|
| 1 | 准备 MySQL，并执行 `back/src/main/resources/db/schema.sql` |
| 2 | 复制 `back/application-local.example.yml` 为 `back/application-local.yml` |
| 3 | 配置数据库、JWT、微信密钥、本地上传地址 |
| 4 | 执行 `cd back && mvn spring-boot:run` |
| 5 | 小程序侧在 `miniprogram/config.local.js` 指向 `http://127.0.0.1:3000` |

## 后端本地配置示例

```yml
spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/LifeSagaNew?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: your_username
    password: your_password

jwt:
  secret: your_32_byte_secret

wechat:
  app-id: your_app_id
  app-secret: your_app_secret

upload:
  public-base-url: http://127.0.0.1:3000

auth:
  dev-login-enabled: true
```

## 关键规则

| 项 | 当前规则 |
|---|---|
| 副本类型 | `LIFE`、`TRAVEL`、`STUDY`、`WORK`、`HEALTH`、`RELATIONSHIP`、`CREATIVE` |
| 副本状态 | `ACTIVE`、`COMPLETED` |
| 稀有度 | `COMMON`、`UNCOMMON`、`RARE`、`EPIC`、`LEGENDARY`、`MYTHIC` |
| 稀有度阈值 | `0-2 / 3-5 / 6-10 / 11-20 / 21-29 / 30+` |
| 图片上传限制 | 单文件 `20MB` |

## 上线前注意

| 项 | 要求 |
|---|---|
| `application-local.yml` | 仅本地保存，不提交远端 |
| `back/application-local.example.yml` | 唯一配置模板，本地与生产都从它派生 |
| JWT 密钥 | 使用强随机值，不与测试环境共用 |
| 微信密钥 | 仅服务端保存 |
| 公开副本 | 非拥有者只返回裁剪后的节点信息，不返回敏感定位信息 |
