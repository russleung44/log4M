# log4M Frontend Admin

log4M 个人记账系统的前端管理后台，基于 Vue 3 + TypeScript + Ant Design Vue 构建。

## 功能特性

- 📊 **仪表板** - 数据统计和可视化分析
- 💰 **账单管理** - 账单的增删改查和批量操作
- 🏷️ **分类管理** - 树形分类结构管理
- 📋 **规则管理** - 记账规则的配置和测试
- 👥 **用户管理** - Telegram 用户信息管理
- 💳 **账户管理** - 账户信息和余额管理

## 技术栈

- **前端框架**: Vue 3 + TypeScript
- **构建工具**: Vite
- **UI组件库**: Ant Design Vue
- **状态管理**: Pinia
- **路由**: Vue Router
- **HTTP客户端**: Axios
- **图表库**: ECharts
- **日期处理**: Day.js

## 项目结构

```
frontend-admin/
├── src/
│   ├── api/                    # API接口定义
│   │   ├── bill.ts            # 账单API
│   │   ├── category.ts        # 分类API
│   │   ├── rule.ts           # 规则API
│   │   ├── user.ts           # 用户API
│   │   └── account.ts        # 账户API
│   ├── components/            # 公共组件（待开发）
│   ├── stores/               # Pinia状态管理
│   │   ├── bill.ts          # 账单状态
│   │   ├── category.ts      # 分类状态
│   │   ├── rule.ts          # 规则状态
│   │   ├── user.ts          # 用户状态
│   │   └── account.ts       # 账户状态
│   ├── views/                # 页面组件
│   │   └── dashboard/        # 仪表板
│   ├── router/              # 路由配置
│   ├── utils/               # 工具函数
│   │   └── http.ts          # HTTP客户端
│   ├── types/               # TypeScript类型定义
│   ├── App.vue              # 根组件
│   └── main.ts              # 入口文件
├── public/
├── package.json
├── vite.config.ts
└── tsconfig.json
```

## 开发环境设置

### 前置要求

- Node.js >= 18
- npm 或 pnpm

### 安装依赖

```bash
cd frontend-admin
npm install
# 或
pnpm install
```

### 环境配置

复制并编辑环境变量文件：

```bash
cp .env.example .env.development
```

修改 `.env.development` 中的配置：

```env
VITE_API_BASE_URL=http://localhost:8080/api
VITE_APP_TITLE=log4M 管理后台 (开发环境)
```

### 启动开发服务器

```bash
npm run dev
# 或
pnpm dev
```

开发服务器将在 http://localhost:3000 启动。

### 构建生产版本

```bash
npm run build
# 或
pnpm build
```

构建后的文件将输出到 `dist/` 目录。

## API 对接

前端通过 REST API 与后端服务通信，所有 API 接口都位于 `/api` 路径下。

### 主要接口

- **账单管理**: `/api/bills`
- **分类管理**: `/api/categories`
- **规则管理**: `/api/rules`
- **用户管理**: `/api/users`
- **账户管理**: `/api/accounts`

### API 响应格式

```typescript
interface ApiResponse<T> {
  code: number
  message: string
  data: T
}
```

## 开发进度

### 已完成

- ✅ 项目基础架构搭建
- ✅ TypeScript 类型定义
- ✅ HTTP 客户端封装
- ✅ API 服务层实现
- ✅ Pinia 状态管理
- ✅ 基础路由配置
- ✅ 仪表板页面框架

### 开发中

- 🚧 账单管理页面
- 🚧 分类管理页面
- 🚧 规则管理页面
- 🚧 公共组件库
- 🚧 数据可视化图表

### 待开发

- ⏳ 用户管理页面
- ⏳ 账户管理页面
- ⏳ 系统设置页面
- ⏳ 数据导入导出
- ⏳ 权限控制
- ⏳ 主题切换

## 部署说明

### Nginx 配置示例

```nginx
server {
    listen 80;
    server_name your-domain.com;
    root /var/www/html/log4m-admin;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
}
```

### Docker 部署

```dockerfile
FROM node:18-alpine as builder
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=builder /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
```

## 贡献指南

1. Fork 项目
2. 创建特性分支 (`git checkout -b feature/amazing-feature`)
3. 提交更改 (`git commit -m 'Add some amazing feature'`)
4. 推送到分支 (`git push origin feature/amazing-feature`)
5. 开启 Pull Request

## 许可证

[MIT License](../LICENSE)