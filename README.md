# log4M

<div align="center">

一个基于 Telegram Bot 的个人记账系统，现已集成 Web 端管理后台

[![Java](https://img.shields.io/badge/Java-21-orange)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.4-brightgreen)](https://spring.io/projects/spring-boot)
[![Vue](https://img.shields.io/badge/Vue-3.3.0-42b883)](https://vuejs.org/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

</div>

---

## 项目简介

log4M 是一个功能完整的个人记账解决方案，结合了 Telegram Bot 的便捷性和 Web 管理后台的强大功能。

**设计理念：** 随手记，轻松查。通过自然语言在 Telegram 上快速记录每一笔收支，通过 Web 管理后台进行深度数据分析。

---

## 核心特性

### Telegram Bot 端

| 功能 | 描述 |
|------|------|
| 自然语言记账 | 支持多种格式的智能解析，如 "午餐 30"、"交通-5月1日 15元" |
| 智能规则匹配 | 自定义正则规则，自动识别分类和标签 |
| 收支统计查询 | 快速查询日/周/月/年收支情况 |
| 分类管理 | 支持树形分类结构，多级分类 |
| 标签管理 | 灵活打标签，便于筛选统计 |
| 账户管理 | 支持多账户（微信/支付宝/银行卡等） |
| 账本管理 | 支持多账本隔离（生活/旅行/工作等） |

### Web 管理后台

| 功能 | 描述 |
|------|------|
| 数据仪表板 | 收支趋势图表、分类占比分析 |
| 账单管理 | 账单的增删改查，支持分页筛选和导出 |
| 分类管理 | 树形结构的分类可视化编辑 |
| 规则管理 | 规则的创建、编辑和测试 |
| 用户管理 | 用户信息和权限管理 |
| 数据导出 | 支持 Excel 格式导出账单数据 |

---

## 快速开始

### 环境要求

- **Java**: 21+
- **Node.js**: 18+
- **Maven**: 3.6+
- **Telegram Bot Token**: [@BotFather](https://t.me/botfather) 获取

### 方式一：本地运行

#### 1. 克隆项目

```bash
git clone https://github.com/your-username/log4M.git
cd log4M
```

#### 2. 配置 Bot Token

编辑 `backend/src/main/resources/application.yml`:

```yaml
botToken: YOUR_BOT_TOKEN  # 替换为你的 Telegram Bot Token
```

#### 3. 启动后端服务

**Windows 用户：**

```bash
# 方式1: 使用批处理脚本
run-local.bat

# 方式2: 使用 PowerShell
.\run-local.ps1
```

**Linux/Mac 用户：**

```bash
cd backend
mvn clean package
java -jar target/log4M.jar
```

后端服务将在 `http://localhost:9001` 启动

#### 4. 启动前端管理后台

```bash
cd frontend-admin
npm install
npm run dev
```

前端服务将在 `http://localhost:5173` 启动

---

### 方式二：Docker 部署（推荐）

```bash
# 构建并启动所有服务
docker-compose up --build
```

访问地址：
- 后端 API: http://localhost:9001
- 前端管理后台: http://localhost:80
- H2 数据库控制台: http://localhost:9001/h2

---

## Bot 使用指南

### 基础命令

| 命令 | 说明 |
|------|------|
| `/start` | 初始化 Bot，创建默认账户和分类 |
| `/help` | 查看帮助信息 |
| `/bill [金额] [备注]` | 快速记账 |
| `/stat` | 查看本月收支统计 |
| `/stat [月份]` | 查看指定月份统计 |
| `/export` | 导出账单数据 |

### 记账示例

```
# 基础格式
午餐 30

# 带分类
#餐饮 麦当劳 35

# 带日期
房租 3000 5月1日

# 收入（用+或收入前缀）
+工资 10000
收入 奖金 5000

# 多条记录
早餐 10，地铁 5，午餐 25
```

### 管理命令

| 命令 | 说明 |
|------|------|
| `/category` | 分类管理 |
| `/account` | 账户管理 |
| `/rule` | 规则管理 |
| `/tag` | 标签管理 |

---

## 项目结构

```
log4M/
├── backend/                     # Spring Boot 后端
│   ├── src/main/java/com/tony/log4m/
│   │   ├── annotation/          # 自定义注解
│   │   ├── aop/                 # AOP切面
│   │   ├── base/                # 基础类
│   │   ├── bots/                # Telegram Bot 核心逻辑
│   │   │   ├── commands/        # 命令处理器
│   │   │   ├── handlers/        # 消息处理器
│   │   │   └── enums/           # 命令枚举
│   │   ├── config/              # 配置类
│   │   ├── controller/          # REST API 控制器
│   │   ├── convert/             # MapStruct 转换器
│   │   ├── enums/               # 业务枚举
│   │   ├── exception/           # 异常处理
│   │   ├── external/            # 外部服务
│   │   ├── mapper/              # MyBatis-Plus Mapper
│   │   ├── models/              # Entity/DTO/VO
│   │   ├── service/             # 业务逻辑层
│   │   └── utils/               # 工具类
│   └── src/main/resources/      # 配置文件
├── frontend-admin/              # Vue 3 前端管理后台
│   ├── src/
│   │   ├── api/                 # API 接口封装
│   │   ├── components/          # 公共组件
│   │   ├── router/              # 路由配置
│   │   ├── stores/              # Pinia 状态管理
│   │   ├── types/               # TypeScript 类型
│   │   ├── utils/               # 工具函数
│   │   └── views/               # 页面组件
│   └── vite.config.ts           # Vite 构建配置
├── docker-compose.yml           # Docker 编排
├── run-local.bat                # Windows 启动脚本
└── README.md
```

---

## 技术栈

### 后端

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.4.4 | 基础框架 |
| Java | 21 | 编程语言 |
| MyBatis-Plus | 3.5.11 | ORM 框架 |
| H2 / MySQL | - | 数据库 |
| java-telegram-bot-api | 8.3.0 | Telegram Bot API |
| Hutool | 5.8.36 | Java 工具库 |
| MapStruct | 1.6.3 | 对象映射 |
| FastJSON2 | 2.0.57 | JSON 处理 |
| FastExcel | 1.1.0 | Excel 处理 |

### 前端

| 技术 | 版本 | 说明 |
|------|------|------|
| Vue | 3.3.0 | 前端框架 |
| TypeScript | 5.0.0 | 类型系统 |
| Vite | 4.4.0 | 构建工具 |
| Ant Design Vue | 4.0.0 | UI 组件库 |
| Pinia | 2.1.0 | 状态管理 |
| Vue Router | 4.2.0 | 路由管理 |
| ECharts | 5.4.0 | 图表库 |
| Axios | 1.5.0 | HTTP 客户端 |
| Day.js | 1.11.0 | 日期处理 |

---

## 配置说明

### 后端配置 (application.yml)

```yaml
server:
  port: 9001  # 服务端口

spring:
  application:
    name: log4M
  datasource:
    # H2 嵌入式数据库（默认）
    url: jdbc:h2:file:../data/log4m;MODE=MySQL;AUTO_SERVER=true
    username: sa
    password:
    # 如需使用 MySQL，请修改为:
    # url: jdbc:mysql://localhost:3306/log4m?useUnicode=true&characterEncoding=utf8
    # username: root
    # password: your_password

# Telegram Bot 配置
botToken: ${BOT_TOKEN:YOUR_BOT_TOKEN}  # 环境变量或直接填写

# 日志配置
logging:
  level:
    com.tony.log4m: debug
```

### 前端配置 (.env.development)

```env
VITE_API_BASE_URL=http://localhost:9001/api
VITE_APP_TITLE=log4M 管理后台
```

---

## 开发指南

### 后端开发规范

1. **新增业务实体**时，按以下顺序创建：
   - `Entity` → `DTO` → `Mapper` → `Service` → `Controller` → `Convert`

2. **API 接口规范**：
   - 使用 `/api` 前缀
   - 返回统一的 `Result<T>` 响应格式
   - 支持跨域请求

3. **代码风格**：
   - 使用 Lombok 简化代码
   - 使用 MapStruct 进行对象转换
   - 业务逻辑放在 Service 层

### 前端开发规范

1. **新增页面**时：
   - 在 `api/` 中定义接口
   - 在 `stores/` 中创建状态管理
   - 在 `views/` 中创建页面组件
   - 在 `router/` 中配置路由

2. **代码风格**：
   - 使用 TypeScript 严格模式
   - 遵循 Vue 3 Composition API 风格
   - 使用 Pinia 进行状态管理

---

## 部署指南

### 生产环境部署

**后端：**

```bash
cd backend
mvn clean package -DskipTests
java -jar target/log4M.jar --spring.profiles.active=prod
```

**前端：**

```bash
cd frontend-admin
npm run build
# 将 dist/ 目录部署到 Nginx 或其他 Web 服务器
```

### Docker 部署

```bash
# 构建镜像
docker-compose build

# 后台运行
docker-compose up -d

# 查看日志
docker-compose logs -f

# 停止服务
docker-compose down
```

---

## 常见问题

### Q: Bot 无响应？

A: 检查以下几点：
1. Bot Token 是否正确配置
2. 后端服务是否正常启动
3. 是否已在 Bot 中发送过 `/start` 命令进行初始化
4. 检查后端日志是否有报错信息

### Q: 如何切换到 MySQL 数据库？

A: 修改 `application.yml` 中的数据源配置，并添加 MySQL 依赖。

### Q: 如何自定义记账规则？

A: 在 Web 管理后台的「规则管理」页面，或通过 Bot 的 `/rule` 命令添加正则表达式规则。

---

## 路线图

- [ ] 支持语音记账
- [ ] 支持图片识别小票
- [ ] 移动端适配
- [ ] 多语言支持
- [ ] 数据备份与恢复
- [ ] 预算管理功能

---

## 贡献指南

欢迎任何形式的贡献！

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

---

## 许可证

本项目采用 [MIT](LICENSE) 许可证。

---

## 致谢

- [java-telegram-bot-api](https://github.com/pengrad/java-telegram-bot-api) - Telegram Bot API for Java
- [MyBatis-Plus](https://baomidou.com/) - 强大的 MyBatis 增强工具
- [Ant Design Vue](https://antdv.com/) - 企业级 UI 组件库
- [ECharts](https://echarts.apache.org/) - 强大的数据可视化库

---

<div align="center">

**如果这个项目对你有帮助，请给一个 Star**

Made with [emoji:heart] by [Tony](https://github.com/your-username)

</div>
