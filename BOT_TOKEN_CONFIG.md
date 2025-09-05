# Bot Token 配置指南

为了保护敏感信息，项目支持多种方式配置Telegram Bot Token，确保不会被提交到Git仓库。

## 方法一：环境变量（推荐）

### 步骤1：设置环境变量
```bash
# Windows (PowerShell)
$env:BOT_TOKEN = "YOUR_ACTUAL_BOT_TOKEN"

# Windows (CMD)
set BOT_TOKEN=YOUR_ACTUAL_BOT_TOKEN

# Linux/macOS
export BOT_TOKEN="YOUR_ACTUAL_BOT_TOKEN"
```

### 步骤2：运行应用
```bash
cd backend
mvn spring-boot:run
```

## 方法二：本地配置文件

### 步骤1：复制并配置
1. 复制 `.env.example` 为 `.env.local`
2. 编辑 `.env.local` 文件，将 `your_actual_bot_token_here` 替换为实际的Bot Token

### 步骤2：编辑application-local.yml
编辑 `backend/src/main/resources/application-local.yml` 文件：
```yaml
botToken: "YOUR_ACTUAL_BOT_TOKEN"
```

### 步骤3：运行应用（使用local profile）
```bash
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

## 方法三：使用提供的启动脚本

### 步骤1：编辑启动脚本
编辑 `run-local.bat` 或 `run-local.ps1`，将 `your_actual_bot_token_here` 替换为实际的Bot Token。

### 步骤2：运行脚本
```bash
# Windows
.\run-local.bat
# 或
.\run-local.ps1
```

## VSCode 调试配置

在VSCode中：
1. 编辑 `.vscode/launch.json` 中的 `BOT_TOKEN` 值
2. 选择 "Debug Log4MApplication (Local Profile)" 配置
3. 按F5开始调试

## 重要提醒

⚠️ **永远不要将真实的Bot Token提交到Git仓库！**

以下文件已被添加到 `.gitignore`：
- `.env.local`
- `.env.*.local`
- `application-local.yml`
- `application-dev.yml`

如果您意外提交了包含真实Token的文件，请：
1. 立即撤销提交
2. 在Telegram BotFather中重新生成新的Token
3. 更新本地配置

## 获取Telegram Bot Token

1. 在Telegram中联系 [@BotFather](https://t.me/BotFather)
2. 发送 `/newbot` 创建新机器人
3. 按照提示设置机器人名称和用户名
4. 复制提供的Token到配置文件中