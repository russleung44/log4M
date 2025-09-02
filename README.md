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
├── src/main/java/               # Spring Boot 后端
│   └── com/tony/log4m/
│       ├── bots/                # Telegram Bot 逻辑
│       ├── controller/          # REST API 控制器
│       ├── service/             # 业务逻辑层
│       ├── mapper/              # 数据访问层
│       └── pojo/                # 数据传输对象
├── frontend-admin/              # Vue 3 前端管理后台
│   ├── src/
│   │   ├── api/                 # API 接口封装
│   │   ├── stores/              # Pinia 状态管理
│   │   ├── views/               # 页面组件
│   │   └── components/          # 公共组件
│   └── package.json
├── deploy-frontend.sh           # 前端部署脚本
├── docker-compose.yml           # Docker 编排
└── README.md
```

## 技术栈

### 后端
- **框架**: Spring Boot 3.4.4
- **数据库**: H2/MySQL
- **ORM**: MyBatis-Plus
- **Bot API**: Telegram Bot API
- **工具库**: Hutool, MapStruct

### 前端
- **框架**: Vue 3 + TypeScript
- **构建工具**: Vite
- **UI库**: Ant Design Vue
- **状态管理**: Pinia
- **图表**: ECharts
- **HTTP客户端**: Axios

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

# 构建并运行
mvn clean package
java -jar target/log4M-0.0.1-SNAPSHOT.jar
```

后端服务将在 http://localhost:8080 启动

### 2. 启动前端管理后台

```bash
# 进入前端目录
cd frontend-admin

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端服务将在 http://localhost:3000 启动

### 3. 使用 Docker Compose（推荐）

```bash
# 构建并启动所有服务
docker-compose up --build
```

- 后端API: http://localhost:8080
- 前端管理后台: http://localhost:80

## 配置说明

### 后端配置

编辑 `src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    # 数据库配置
    url: jdbc:h2:file:./data/log4m
    username: sa
    password: 

bot:
  # Telegram Bot Token
  token: "YOUR_BOT_TOKEN"
```

### 前端配置

编辑 `frontend-admin/.env.development`：

```env
VITE_API_BASE_URL=http://localhost:8080/api
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

- **规则管理**: `/api/rules`
  - GET `/api/rules` - 分页查询规则
  - POST `/api/rules/test` - 测试规则匹配

更多API详情请查看源代码中的Controller类。

## 部署指南

### 生产环境部署

1. **后端部署**:
   ```bash
   mvn clean package -Pprod
   java -jar target/log4M-0.0.1-SNAPSHOT.jar
   ```

2. **前端部署**:
   ```bash
   cd frontend-admin
   npm run build
   # 将 dist/ 目录内容部署到 Web 服务器
   ```

3. **使用部署脚本**:
   ```bash
   # Linux/Mac
   ./deploy-frontend.sh
   
   # Windows
   deploy-frontend.bat
   ```

### Docker 部署

```bash
# 构建镜像
docker build -t log4m-backend .
docker build -f Dockerfile.frontend -t log4m-frontend .

# 运行容器
docker-compose up -d
```

## 开发指南

### 后端开发

1. 添加新的业务实体时，需要创建对应的：
   - Entity类（`pojo.entity`）
   - DTO类（`pojo.dto`）
   - Mapper接口（`mapper`）
   - Service类（`service`）
   - Controller类（`controller`）

2. 所有API接口都应该：
   - 使用`/api`前缀
   - 返回统一的`ResultVO`格式
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