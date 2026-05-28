# 人生副本（LifeSaga）

一个 RPG 概念的时间线记录微信小程序，把人生经历（恋爱、旅行、成长等）当作"副本"来记录和回顾。

## 技术栈

- **小程序前端**：`miniprogram/`（原生微信小程序）
- **后端**：Java 21 + Spring Boot 3.3 + JdbcTemplate + MySQL
- **认证**：微信登录 + JWT Bearer Token
- **端口**：3000

## 核心功能

- **微信一键登录**：扫码即登录，`wx.login` → code → 后端 jscode2session → JWT
- **副本管理**：创建/编辑人生副本，RPG 稀有度体系（普通→稀有→传说→神话）
- **节点记录**：在副本时间线上添加节点，支持图片上传
- **成就系统**：记录人生里程碑
- **个人主页**：用户信息与数据统计
- **发现页**：浏览和发现副本

## 页面结构

| 页面 | 路径 | 说明 |
|------|------|------|
| 首页 | `home` | 副本列表、统计（进行中/已完成/总节点） |
| 详情页 | `detail` | 副本详情、节点时间线 |
| 添加节点 | `add-node` | 在副本中添加新节点 |
| 节点详情 | `node-detail` | 查看单个节点 |
| 创建副本 | `create` | 创建/编辑副本（tabBar 页） |
| 成就 | `achievements` | 成就列表 |
| 发现 | `discover` | 发现页 |
| 个人 | `profile` | 个人主页 |

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
2. 配置环境变量（或修改 `application.yml`）：
   - `DB_USERNAME` / `DB_PASSWORD`
   - `JWT_SECRET`（≥32字符）
   - `WECHAT_APP_ID` / `WECHAT_APP_SECRET`（为空时走 dev 模式）
3. 启动：

```bash
cd backend
./mvnw spring-boot:run
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
