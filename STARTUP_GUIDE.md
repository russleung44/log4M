# log4M 前后端启动指南

## 📋 配置概览

### 后端服务 (Spring Boot)
- **端口**: 9001
- **API基础路径**: `/api`
- **完整地址**: `http://localhost:9001/api`

### 前端服务 (Vue 3 + Vite)
- **端口**: 3000
- **代理配置**: `/api` -> `http://localhost:9001/api`
- **完整地址**: `http://localhost:3000`

## 🚀 启动步骤

### 方法一：使用VSCode任务（推荐）

1. **打开VSCode工作区**：
   ```bash
   code log4M.code-workspace
   ```

2. **启动全栈服务**：
   - 按 `Ctrl+Shift+P` 打开命令面板
   - 输入 "Tasks: Run Task"
   - 选择 "Start Full Stack" 任务
   - 或者按 `Ctrl+Shift+P` → "Tasks: Run Build Task"

### 方法二：分别启动

#### 启动后端服务
```bash
# 方式1: 使用环境变量
$env:BOT_TOKEN = "your_actual_bot_token_here"
cd backend
mvn spring-boot:run

# 方式2: 使用local配置
cd backend
mvn spring-boot:run -Dspring.profiles.active=local
```

#### 启动前端服务
```bash
cd frontend-admin
npm run dev
```

## 🔧 配置文件说明

### 前端配置更改 ✅
- **`.env`**: API基础URL更新为 `http://localhost:9001/api`
- **`vite.config.ts`**: 代理目标更新为 `http://localhost:9001`

### 后端配置
- **端口**: 在 `application.yml` 中配置为 9001
- **Bot Token**: 使用环境变量或 `application-local.yml`

## 📡 API测试

启动服务后，可以通过以下方式测试：

### 直接访问后端API
```bash
curl http://localhost:9001/api/health
```

### 通过前端代理访问
```bash
curl http://localhost:3000/api/health
```

## 🛠️ 开发工具

### VSCode集成
- **调试配置**: 已更新 `launch.json` 支持环境变量
- **任务配置**: 新增 `tasks.json` 支持一键启动
- **工作区**: 使用 `log4M.code-workspace` 获得最佳体验

### 浏览器访问
- **前端应用**: http://localhost:3000
- **后端API**: http://localhost:9001/api
- **H2数据库控制台**: http://localhost:9001/h2

## ⚠️ 注意事项

1. **Bot Token配置**: 确保配置了有效的Telegram Bot Token
2. **端口冲突**: 确保9001和3000端口未被占用
3. **依赖安装**: 首次运行前端需要执行 `npm install`
4. **Java版本**: 确保使用Java 21

## 🐛 常见问题

### 前端无法连接后端
- 检查后端是否在9001端口正常运行
- 检查 `.env` 文件中的API地址配置
- 查看浏览器控制台的网络错误信息

### CORS问题
- 后端已配置CORS支持前端跨域访问
- 如有问题，检查 `CorsConfig.java` 配置

### Bot相关错误
- Telegram Bot错误不影响Web API功能
- 配置正确的Bot Token可解决相关错误