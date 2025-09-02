#!/bin/bash

# log4M Frontend Admin 部署脚本

set -e

echo "🚀 开始部署 log4M Frontend Admin..."

# 检查 Node.js 和 npm
if ! command -v node &> /dev/null; then
    echo "❌ Node.js 未安装，请先安装 Node.js"
    exit 1
fi

if ! command -v npm &> /dev/null; then
    echo "❌ npm 未安装，请先安装 npm"
    exit 1
fi

# 进入前端目录
cd frontend-admin

echo "📦 安装依赖..."
npm install

echo "🔧 构建生产版本..."
npm run build

echo "📋 构建完成，文件位于 dist/ 目录"

# 可选：复制到 Web 服务器目录
# echo "📁 复制文件到 Web 服务器目录..."
# sudo cp -r dist/* /var/www/html/log4m-admin/

echo "✅ 部署完成！"
echo "📖 请参考 README.md 配置 Web 服务器"