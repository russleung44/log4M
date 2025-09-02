@echo off
REM log4M Frontend Admin Windows 部署脚本

echo 🚀 开始部署 log4M Frontend Admin...

REM 检查 Node.js
where node >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo ❌ Node.js 未安装，请先安装 Node.js
    pause
    exit /b 1
)

REM 检查 npm
where npm >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo ❌ npm 未安装，请先安装 npm
    pause
    exit /b 1
)

REM 进入前端目录
cd frontend-admin

echo 📦 安装依赖...
npm install
if %ERRORLEVEL% NEQ 0 (
    echo ❌ 依赖安装失败
    pause
    exit /b 1
)

echo 🔧 构建生产版本...
npm run build
if %ERRORLEVEL% NEQ 0 (
    echo ❌ 构建失败
    pause
    exit /b 1
)

echo 📋 构建完成，文件位于 dist\ 目录

echo ✅ 部署完成！
echo 📖 请参考 README.md 配置 Web 服务器

pause