# LifeSaga DDD Backend

该目录用于以模块化单体方式重构 LifeSaga 后端。

## 依赖方向

```text
interfaces -> application -> domain
infrastructure -----------> domain
```

- `domain`：领域模型、值对象、领域服务、领域事件、仓储接口，不依赖 Spring。
- `application`：用例编排、事务边界、命令和查询。
- `infrastructure`：数据库、第三方接口、认证和文件存储等技术实现。
- `interfaces`：HTTP Controller、请求对象和响应对象。

## 限界上下文

| 模块 | 职责 |
|---|---|
| `saga` | 副本、节点、里程碑、收藏和稀有度 |
| `achievement` | 成就规则、成就解锁和经验奖励 |
| `identity` | 微信登录、用户身份和个人资料 |
| `gallery` | 相册只读查询 |
| `discovery` | 公开副本只读查询 |
| `shared` | 少量跨模块共享能力 |

## 约束

- 不允许 Controller 直接访问仓储。
- 不允许基础设施对象进入领域层。
- 领域对象不直接作为 HTTP 请求或响应对象。
- 写操作通过领域模型维护业务规则。
- 相册、广场和统计等读模型可以直接使用专用查询实现。
