# log4M

基于Telegram Bot的个人记账系统，现已集成Web端管理后台

## 项目概述

log4M 是一个功能完整的个人记账解决方案，包含：

- **Telegram Bot端**: 通过自然语言快速记账
- **Web管理后台**: 数据管理和可视化分析

## 功能特性

### Telegram Bot 功能
- 🤖 自然语言记账解析
- 📊 智能规则匹配
- 💰 收支统计查询
- 🏷️ 分类和标签管理
- 📅 多种日期格式支持

### Web管理后台功能
- 📈 数据统计仪表板
- 💳 账单管理（增删改查）
- 🗂️ 分类管理（树形结构）
- ⚙️ 规则管理和测试
- 👥 用户管理
- 🏦 账户管理

## 项目结构

```
log4M/
├── backend/                     # Spring Boot 后端
│   ├── src/main/java/com/tony/log4m/
│   │   ├── annotation/          # 自定义注解
│   │   ├── aop/                 # AOP切面处理
│   │   ├── base/                # 基础类
│   │   ├── bots/                # Telegram Bot 逻辑
│   │   ├── configs/             # 配置类
│   │   ├── controller/          # REST API 控制器
│   │   ├── convert/             # 对象转换器（MapStruct）
│   │   ├── enums/               # 枚举类
│   │   ├── exception/           # 异常处理
│   │   ├── external/            # 外部服务集成
│   │   ├── mapper/              # 数据访问层（MyBatis-Plus）
│   │   ├── models/              # 数据模型（entity/dto/vo）
│   │   ├── service/             # 业务逻辑层
│   │   ├── utils/               # 工具类
│   │   └── Log4MApplication.java # 应用启动类
│   └── src/main/resources/      # 配置文件和资源
├── frontend-admin/              # Vue 3 前端管理后台
│   ├── src/
│   │   ├── api/                 # API 接口封装
│   │   ├── components/          # 公共组件
│   │   ├── router/              # 路由配置
│   │   ├── stores/              # Pinia 状态管理
│   │   ├── types/               # TypeScript 类型定义
│   │   ├── utils/               # 工具函数
│   │   ├── views/               # 页面组件
│   │   ├── App.vue              # 根组件
│   │   └── main.ts              # 入口文件
│   ├── package.json             # 项目依赖配置
│   └── vite.config.ts           # 构建配置
├── docker-compose.yml           # Docker 编排
├── run-local.bat                # Windows本地运行脚本
├── run-local.ps1                # PowerShell本地运行脚本
└── README.md
```

## 技术栈

### 后端
- **框架**: Spring Boot 3.4.4
- **语言**: Java 21
- **数据库**: H2 (默认) / MySQL
- **ORM**: MyBatis-Plus 3.5.11
- **Bot API**: java-telegram-bot-api 8.3.0
- **工具库**: Hutool 5.8.36, MapStruct 1.6.3, Lombok
- **构建工具**: Maven 3.6+

### 前端
- **框架**: Vue 3 + TypeScript
- **构建工具**: Vite 4.4.0
- **UI库**: Ant Design Vue 4.0.0
- **状态管理**: Pinia 2.1.0
- **图表**: ECharts 5.4.0
- **HTTP客户端**: Axios 1.5.0
- **路由**: Vue Router 4.2.0

## 快速开始

### 环境要求

- Java 21+
- Node.js 18+
- Maven 3.6+

### 1. 启动后端服务

```bash
# 克隆项目
git clone https://github.com/your-username/log4M.git
cd log4M

# 方法一：使用脚本启动（推荐）
# Windows:
run-local.bat
# 或PowerShell:
./run-local.ps1

# 方法二：手动启动
cd backend
mvn clean package
java -jar target/log4M.jar
```

后端服务将在 http://localhost:9001 启动

### 2. 启动前端管理后台

```bash
# 进入前端目录
cd frontend-admin

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端服务将在 http://localhost:5173 启动

### 3. 使用 Docker Compose（推荐）

```bash
# 构建并启动所有服务
docker-compose up --build
```

- 后端API: http://localhost:9001
- 前端管理后台: http://localhost:80

## 配置说明

### 后端配置

编辑 `backend/src/main/resources/application.yml`：

```yaml
server:
  port: 9001  # 后端服务端口

spring:
  datasource:
    # 数据库配置
    url: jdbc:h2:file:../data/log4m;MODE=MySQL;AUTO_SERVER=true
    username: sa
    password: 

botToken: ${BOT_TOKEN:YOUR_BOT_TOKEN}  # Telegram Bot Token
```

### 前端配置

编辑 `frontend-admin/.env.development`：

```env
VITE_API_BASE_URL=http://localhost:9001/api
VITE_APP_TITLE=log4M 管理后台
```

## API 文档

### 主要接口

- **账单管理**: `/api/bills`
  - GET `/api/bills` - 分页查询账单
  - POST `/api/bills` - 创建账单
  - PUT `/api/bills/{id}` - 更新账单
  - DELETE `/api/bills/{id}` - 删除账单

- **分类管理**: `/api/categories`
  - GET `/api/categories/tree` - 获取分类树
  - POST `/api/categories` - 创建分类
  - PUT `/api/categories/{id}` - 更新分类
  - DELETE `/api/categories/{id}` - 删除分类

- **规则管理**: `/api/rules`
  - GET `/api/rules` - 分页查询规则
  - POST `/api/rules/test` - 测试规则匹配
  - POST `/api/rules` - 创建规则
  - PUT `/api/rules/{id}` - 更新规则
  - DELETE `/api/rules/{id}` - 删除规则

- **账户管理**: `/api/accounts`
  - GET `/api/accounts` - 查询账户列表
  - POST `/api/accounts` - 创建账户
  - PUT `/api/accounts/{id}` - 更新账户
  - DELETE `/api/accounts/{id}` - 删除账户

更多API详情请查看源代码中的Controller类。

## 部署指南

### 生产环境部署

1. **后端部署**:
   ```bash
   cd backend
   mvn clean package
   java -jar target/log4M.jar
   ```

2. **前端部署**:
   ```bash
   cd frontend-admin
   npm run build
   # 将 dist/ 目录内容部署到 Web 服务器
   ```

### Docker 部署

```bash
# 构建并启动所有服务
docker-compose up --build -d
```

## 开发指南

### 后端开发

1. 添加新的业务实体时，需要创建对应的：
   - Entity类（`models.entity`）
   - DTO类（`models.dto`）
   - Mapper接口（`mapper`）
   - Service类（`service`）
   - Controller类（`controller`）
   - Convert转换器（`convert`）

2. 所有API接口都应该：
   - 使用`/api`前缀
   - 返回统一的响应格式
   - 支持CORS跨域请求

### 前端开发

1. 添加新页面时的步骤：
   - 在`api/`中定义接口方法
   - 在`stores/`中创建状态管理
   - 在`views/`中创建页面组件
   - 在`router/`中配置路由

2. 代码规范：
   - 使用TypeScript严格模式
   - 遵循Vue 3 Composition API风格
   - 使用Pinia进行状态管理

## 贡献指南

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/amazing-feature`)
3. 提交更改 (`git commit -m 'Add some amazing feature'`)
4. 推送到分支 (`git push origin feature/amazing-feature`)
5. 开启 Pull Request

## 许可证

本项目采用 MIT 许可证。详情请见 [LICENSE](LICENSE) 文件。

## 联系方式

如有问题或建议，请提交 Issue 或发送邮件至 [your-email@example.com](mailto:your-email@example.com)。