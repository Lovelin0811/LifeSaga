# LifeSaga 生产发布检查清单

| 项 | 要求 | 当前说明 |
|---|---|---|
| 发布目标 | 只发布 `back/` | `backend/` 是历史实现，不应继续发布 |
| 打包产物 | `back/target/lifesaga-ddd-0.0.1-SNAPSHOT.jar` | 需要更新现有发布脚本 |
| 数据库 | 使用 `LifeSagaNew` | 见 `back/src/main/resources/db/schema.sql` |
| 配置文件 | 基于 `back/local.properties.example` 或 `back/application-prod.example.yml` 补齐 | 你当前服务器是 `local.properties` 方式 |
| JWT | 使用生产专用强随机密钥 | 不得与测试环境共用 |
| 微信密钥 | 使用生产小程序对应密钥 | 必填 |
| 上传目录 | 指向服务器持久目录 | 例如 `/opt/lifesaga/uploads` |
| 上传公网地址 | 指向正式域名 | 例如 `https://lovelin.com.cn` |

## 发布前核对

| 检查项 | 通过标准 |
|---|---|
| 生产脚本是否仍指向 `backend/` | 必须改成 `back/` |
| 小程序接口地址 | 仍为 `https://lovelin.com.cn` |
| 线上数据库是否已有旧库 `lifesaga` | 若有，先执行迁移脚本 |
| 线上数据库是否已有 `LifeSagaNew` | 若无，先创建并导入 |
| 线上上传目录权限 | Java 进程可读写 |

## 数据库迁移顺序

| 步骤 | 动作 |
|---|---|
| 1 | 备份现网数据库 `lifesaga` |
| 2 | 执行 `back/src/main/resources/db/migration/2026-06-30_migrate_lifesaga_to_lifesaganew.sql` |
| 3 | 校验 `LifeSagaNew` 中用户、副本、节点、成就数量 |
| 4 | 确认 `type/status/rarity` 已转为大写 |
| 5 | 确认外键已创建成功 |

## 发布后冒烟

| 场景 | 通过标准 |
|---|---|
| 微信登录 | 可拿到 token，`/api/users/me` 返回 200 |
| 我的副本 | `GET /api/sagas` 返回正常 |
| 创建副本 | `POST /api/sagas` 成功，类型为大写枚举 |
| 添加节点 | 节点保存成功，副本 `nodeCount` 正常增加 |
| 上传图片 | `POST /api/upload` 成功并可访问返回 URL |
| 公开副本详情 | 自己看完整；别人看不到敏感定位与时间字段 |

## 当前已知阻塞

| 阻塞项 | 说明 |
|---|---|
| 发布脚本仍指向旧 `backend/` | 这是当前最直接的生产阻塞 |
| 仓库内只有完整建表和新增迁移草案 | 真正执行前仍应先备份生产库 |
