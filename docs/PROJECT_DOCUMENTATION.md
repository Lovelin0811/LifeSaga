# LifeSaga 项目文档

## 项目结构

| 路径 | 说明 |
|---|---|
| `back/src/main/java/com/lovelin/lifesaga/identity` | 用户与微信登录 |
| `back/src/main/java/com/lovelin/lifesaga/saga` | 副本与节点核心业务 |
| `back/src/main/java/com/lovelin/lifesaga/achievement` | 成就系统 |
| `back/src/main/java/com/lovelin/lifesaga/shared` | 跨模块配置、上传、认证、异常处理 |
| `back/src/main/resources/db/schema.sql` | 当前建表脚本 |
| `miniprogram/` | 小程序前端 |
| `backend/` | 历史旧实现，不作为当前发布版本 |

## 后端技术选型

| 项 | 说明 |
|---|---|
| 应用框架 | Spring Boot |
| 数据访问 | MyBatis |
| 数据库 | MySQL 8.x |
| 认证 | JWT Bearer Token |
| 架构方式 | 模块化单体 + DDD 分层 |

## saga 模块分层

| 层 | 说明 |
|---|---|
| `domain/model` | 聚合、实体、值对象、枚举 |
| `domain/repository` | 领域仓储接口 |
| `application/command` | 用例输入命令 |
| `application/service` | 用例编排 |
| `infrastructure/persistence` | MyBatis 仓储实现、Record、Mapper |
| `interfaces/rest` | Controller、请求响应对象 |

## API 约定

| 项 | 说明 |
|---|---|
| 基础路径 | `/api` |
| 公共返回 | `{"code":200,"data":...,"message":"success"}` |
| 认证豁免 | `/api/auth/**`、`/api/sagas/public` |
| 错误语义 | `400` 参数错误、`401` 未登录、`403` 无权、`404` 不存在、`500` 服务端错误 |

## 当前业务规则

| 项 | 规则 |
|---|---|
| 副本名称 | 去首尾空格后不能为空，最多 20 个 Unicode 字符 |
| 副本类型 | `LIFE`、`TRAVEL`、`STUDY`、`WORK`、`HEALTH`、`CREATIVE` |
| 副本完成 | 至少存在 1 个节点才能完成 |
| 节点新增 | 新增后副本状态重置为 `ACTIVE`，完成时间清空 |
| 节点删除 | 删除后同步回退节点数和稀有度 |
| 公开副本详情 | 非拥有者不可获得定位、时间、描述等敏感节点字段 |

## 数据库要点

| 表 | 说明 |
|---|---|
| `users` | 用户 |
| `sagas` | 副本 |
| `saga_nodes` | 节点 |
| `node_favorites` | 节点收藏 |
| `achievements` | 成就定义 |
| `user_achievements` | 用户成就 |

## 完整性约束

| 关系 | 约束 |
|---|---|
| `sagas.user_id -> users.id` | 级联删除 |
| `saga_nodes.saga_id -> sagas.id` | 级联删除 |
| `node_favorites.user_id -> users.id` | 级联删除 |
| `node_favorites.node_id -> saga_nodes.id` | 级联删除 |
| `user_achievements.user_id -> users.id` | 级联删除 |
| `user_achievements.achievement_id -> achievements.id` | 级联删除 |
