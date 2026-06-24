# 人生副本（LifeSaga）

一个 RPG 概念的时间线记录微信小程序，把人生经历（恋爱、旅行、成长等）当作"副本"来记录和回顾。

## 技术栈

- **小程序前端**：`miniprogram/`（原生微信小程序）
- **后端**：Java 21 + Spring Boot 3.3 + JdbcTemplate + MySQL
- **认证**：微信登录 + JWT Bearer Token
- **端口**：3000

## 核心功能

- **微信一键登录**：扫码即登录，`wx.login` → code → 后端 jscode2session → JWT
- **副本管理**：创建/编辑人生副本，支持完成标记，RPG 稀有度体系（普通→稀有→传说→神话）
- **副本完成**：可在详情页把副本标记为已完成，并同步到首页统计
- **节点记录**：在副本时间线上添加节点，支持多图上传
- **节点交互**：收藏、里程碑切换、图片预览
- **成就系统**：记录人生里程碑
- **个人主页**：用户信息、相册与数据统计
- **我的相册**：聚合副本封面图和节点照片，多图节点会逐张展示
- **个人版发布**：小程序端暂隐藏广场与公开副本入口，保留后端公开能力便于后续恢复

## 页面结构

| 页面 | 路径 | 说明 |
|------|------|------|
| 首页 | `home` | 副本列表、统计（进行中/已完成/总节点） |
| 广场 | `discover` | 公开副本列表（已注册页面，个人版暂不暴露入口） |
| 详情页 | `detail` | 副本详情、节点时间线 |
| 添加节点 | `add-node` | 在副本中添加新节点 |
| 节点详情 | `node-detail` | 查看单个节点 |
| 创建副本 | `create` | 创建/编辑副本（tabBar 页） |
| 成就 | `achievements` | 成就列表 |
| 个人 | `profile` | 个人主页 |
| 相册 | `albums` | 我的相册（副本封面 + 节点照片） |
| 统计 | `stats` | 数据统计 |

详情页右上角菜单包含「编辑副本」「完成副本」「删除副本」。

## 目录结构

```
LifeSaga/
├── miniprogram/          # 微信小程序前端
│   ├── pages/            # 页面目录
│   ├── app.js            # 小程序入口
│   ├── config.js         # 配置（读 config.local.js）
│   └── config.local.js   # 本地配置（gitignored）
├── backend/              # Spring Boot 后端
│   └── src/main/
│       ├── java/com/lovelin/lifesaga/
│       └── resources/application.yml
└── uploads/              # 上传文件目录（运行时创建）
```

## 本地开发

### 后端启动

1. 准备 MySQL，创建数据库 `lifesaga`
2. 创建本地密钥文件 `backend/application-local.yml`（已加入 `.gitignore`），填写数据库、微信和 JWT 信息
3. 生产环境另外设置 `UPLOAD_PUBLIC_BASE_URL`，本地开发可在 `application-local.yml` 里开启 `auth.dev-login-enabled`
4. 启动：

```bash
cd backend
mvn spring-boot:run
```

默认地址：`http://127.0.0.1:3000`

### 小程序启动

1. 微信开发者工具导入 `miniprogram` 目录
2. 创建 `miniprogram/config.local.js`：

```js
module.exports = {
  API_BASE: 'http://127.0.0.1:3000'
};
```

3. 不创建时默认连接 `http://127.0.0.1:3000`
4. 本地联调时勾选"不校验合法域名"

## 设计风格

- Warm Editorial + RPG 游戏感
- 主色：珊瑚暖阳 `#E8725A` + 蜂蜜暖金 `#E5A44D`
- 稀有度：common → uncommon → rare → epic → legendary → mythic
