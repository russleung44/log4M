<template>
  <div class="about-container">
    <a-card class="about-card">
      <template #title>
        <div class="card-title">
          <RocketOutlined />
          <span>关于 log4M</span>
        </div>
      </template>

      <div class="about-content">
        <!-- 项目简介 -->
        <section class="section">
          <h2>项目简介</h2>
          <p class="description">
            log4M 是一个功能完整的个人记账解决方案，结合了 Telegram Bot 的便捷性和 Web 管理后台的强大功能。
          </p>
          <a-alert
            message="设计理念"
            description="随手记，轻松查。通过自然语言在 Telegram 上快速记录每一笔收支，通过 Web 管理后台进行深度数据分析。"
            type="info"
            show-icon
            class="理念-alert"
          >
            <template #icon>
              <BulbOutlined />
            </template>
          </a-alert>
        </section>

        <!-- 核心特性 -->
        <section class="section">
          <h2>核心特性</h2>
          <a-row :gutter="16">
            <a-col :xs="24" :lg="12">
              <div class="feature-box">
                <h3><RobotOutlined /> Telegram Bot 端</h3>
                <ul class="feature-list">
                  <li><CheckCircleOutlined /> 自然语言记账</li>
                  <li><CheckCircleOutlined /> 智能规则匹配</li>
                  <li><CheckCircleOutlined /> 收支统计查询</li>
                  <li><CheckCircleOutlined /> 分类和标签管理</li>
                  <li><CheckCircleOutlined /> 账户管理</li>
                  <li><CheckCircleOutlined /> 账本管理</li>
                </ul>
              </div>
            </a-col>
            <a-col :xs="24" :lg="12">
              <div class="feature-box">
                <h3><DashboardOutlined /> Web 管理后台</h3>
                <ul class="feature-list">
                  <li><CheckCircleOutlined /> 数据统计仪表板</li>
                  <li><CheckCircleOutlined /> 账单管理（CRUD）</li>
                  <li><CheckCircleOutlined /> 分类管理（树形结构）</li>
                  <li><CheckCircleOutlined /> 规则管理和测试</li>
                  <li><CheckCircleOutlined /> 用户管理</li>
                  <li><CheckCircleOutlined /> 数据导出</li>
                </ul>
              </div>
            </a-col>
          </a-row>
        </section>

        <!-- Bot 使用指南 -->
        <section class="section">
          <h2><MessageOutlined /> Bot 使用指南</h2>

          <div class="guide-box">
            <h3>基础命令</h3>
            <a-table
              :columns="commandColumns"
              :data-source="commands"
              :pagination="false"
              size="small"
              class="command-table"
            >
              <template #bodyCell="{ column, record }">
                <template v-if="column.key === 'command'">
                  <a-tag color="blue">{{ record.command }}</a-tag>
                </template>
              </template>
            </a-table>
          </div>

          <div class="guide-box">
            <h3>记账示例</h3>
            <a-card class="example-card">
              <div class="example-item">
                <code>午餐 30</code>
                <span class="example-desc">基础格式</span>
              </div>
              <div class="example-item">
                <code>#餐饮 麦当劳 35</code>
                <span class="example-desc">带分类</span>
              </div>
              <div class="example-item">
                <code>房租 3000 5月1日</code>
                <span class="example-desc">带日期</span>
              </div>
              <div class="example-item">
                <code>+工资 10000</code>
                <span class="example-desc">收入（用+表示）</span>
              </div>
              <div class="example-item">
                <code>早餐 10，地铁 5，午餐 25</code>
                <span class="example-desc">多条记录</span>
              </div>
            </a-card>
          </div>
        </section>

        <!-- 技术栈 -->
        <section class="section">
          <h2><CodeOutlined /> 技术栈</h2>
          <a-row :gutter="[16, 16]">
            <a-col :xs="24" :md="12">
              <a-card title="后端" size="small" class="tech-card">
                <div class="tech-tags">
                  <a-tag color="orange">Java 21</a-tag>
                  <a-tag color="green">Spring Boot 3.4.4</a-tag>
                  <a-tag color="blue">MyBatis-Plus 3.5.11</a-tag>
                  <a-tag color="purple">H2 / MySQL</a-tag>
                  <a-tag color="cyan">java-telegram-bot-api 8.3.0</a-tag>
                  <a-tag color="pink">Hutool 5.8.36</a-tag>
                </div>
              </a-card>
            </a-col>
            <a-col :xs="24" :md="12">
              <a-card title="前端" size="small" class="tech-card">
                <div class="tech-tags">
                  <a-tag color="green">Vue 3.3.0</a-tag>
                  <a-tag color="blue">TypeScript 5.0.0</a-tag>
                  <a-tag color="purple">Vite 4.4.0</a-tag>
                  <a-tag color="red">Ant Design Vue 4.0.0</a-tag>
                  <a-tag color="cyan">Pinia 2.1.0</a-tag>
                  <a-tag color="orange">ECharts 5.4.0</a-tag>
                </div>
              </a-card>
            </a-col>
          </a-row>
        </section>

        <!-- 快速开始 -->
        <section class="section">
          <h2><ThunderboltOutlined /> 快速开始</h2>
          <a-card class="start-card">
            <h3>环境要求</h3>
            <ul>
              <li>Java 21+</li>
              <li>Node.js 18+</li>
              <li>Maven 3.6+</li>
              <li>Telegram Bot Token（从 @BotFather 获取）</li>
            </ul>

            <h3>本地运行</h3>
            <pre class="code-block"><code># 克隆项目
git clone https://github.com/your-username/log4M.git
cd log4M

# 启动后端
cd backend
mvn clean package
java -jar target/log4M.jar

# 启动前端
cd frontend-admin
npm install
npm run dev</code></pre>
          </a-card>
        </section>

        <!-- 开源协议 -->
        <section class="section">
          <h2><FileTextOutlined /> 开源协议</h2>
          <a-card class="license-card">
            <p>本项目采用 <a href="https://opensource.org/licenses/MIT" target="_blank">MIT</a> 许可证开源。</p>
            <p class="github-link">
              <GithubOutlined />
              <a href="https://github.com/your-username/log4M" target="_blank">GitHub 仓库</a>
            </p>
          </a-card>
        </section>

        <!-- 致谢 -->
        <section class="section">
          <h2><HeartOutlined /> 致谢</h2>
          <a-card class="thanks-card">
            <p>感谢以下开源项目：</p>
            <a-space direction="vertical" style="width: 100%">
              <a href="https://github.com/pengrad/java-telegram-bot-api" target="_blank">
                java-telegram-bot-api - Telegram Bot API for Java
              </a>
              <a href="https://baomidou.com/" target="_blank">
                MyBatis-Plus - 强大的 MyBatis 增强工具
              </a>
              <a href="https://antdv.com/" target="_blank">
                Ant Design Vue - 企业级 UI 组件库
              </a>
              <a href="https://echarts.apache.org/" target="_blank">
                ECharts - 强大的数据可视化库
              </a>
            </a-space>
          </a-card>
        </section>
      </div>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import {
  RocketOutlined,
  BulbOutlined,
  RobotOutlined,
  DashboardOutlined,
  CheckCircleOutlined,
  MessageOutlined,
  CodeOutlined,
  ThunderboltOutlined,
  FileTextOutlined,
  HeartOutlined,
  GithubOutlined
} from '@ant-design/icons-vue'

interface Command {
  command: string
  description: string
}

const commandColumns = ref([
  {
    title: '命令',
    dataIndex: 'command',
    key: 'command'
  },
  {
    title: '说明',
    dataIndex: 'description',
    key: 'description'
  }
])

const commands = ref<Command[]>([
  { command: '/start', description: '初始化 Bot，创建默认账户和分类' },
  { command: '/help', description: '查看帮助信息' },
  { command: '/bill [金额] [备注]', description: '快速记账' },
  { command: '/stat', description: '查看本月收支统计' },
  { command: '/stat [月份]', description: '查看指定月份统计' },
  { command: '/export', description: '导出账单数据' },
  { command: '/category', description: '分类管理' },
  { command: '/account', description: '账户管理' },
  { command: '/rule', description: '规则管理' },
  { command: '/tag', description: '标签管理' }
])
</script>

<style scoped>
.about-container {
  padding: 16px;
}

.about-card {
  .card-title {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 18px;
    font-weight: 600;
  }
}

.about-content {
  .section {
    margin-bottom: 32px;

    &:last-child {
      margin-bottom: 0;
    }

    h2 {
      font-size: 20px;
      font-weight: 600;
      margin-bottom: 16px;
      padding-bottom: 8px;
      border-bottom: 2px solid #f0f0f0;
      display: flex;
      align-items: center;
    }
  }
}

.description {
  font-size: 14px;
  color: #666;
  line-height: 1.8;
  margin-bottom: 16px;
}

.理念-alert {
  margin-top: 16px;
}

.feature-box {
  background: #fafafa;
  padding: 16px;
  border-radius: 8px;
  height: 100%;

  h3 {
    font-size: 16px;
    font-weight: 600;
    margin-bottom: 12px;
    display: flex;
    align-items: center;
  }
}

.feature-list {
  list-style: none;
  padding: 0;
  margin: 0;

  li {
    padding: 6px 0;
    color: #555;
  }
}

.guide-box {
  margin-bottom: 20px;

  &:last-child {
    margin-bottom: 0;
  }

  h3 {
    font-size: 16px;
    font-weight: 600;
    margin-bottom: 12px;
  }
}

.command-table {
  :deep(.ant-table-thead > tr > th) {
    background: #f5f5f5;
  }
}

.example-card {
  background: #f6ffed;
  border-color: #b7eb8f;
}

.example-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 0;
  border-bottom: 1px dashed #d9d9d9;

  &:last-child {
    border-bottom: none;
  }

  code {
    background: #fff;
    padding: 4px 8px;
    border-radius: 4px;
    font-family: 'Courier New', monospace;
    color: #d73a49;
  }

  .example-desc {
    color: #999;
    font-size: 12px;
  }
}

.tech-card {
  .tech-tags {
    display: flex;
    flex-wrap: wrap;
  }
}

.start-card {
  h3 {
    font-size: 16px;
    font-weight: 600;
    margin: 16px 0 8px;

    &:first-child {
      margin-top: 0;
    }
  }

  ul {
    padding-left: 20px;

    li {
      padding: 4px 0;
    }
  }
}

.code-block {
  background: #f5f5f5;
  padding: 12px;
  border-radius: 4px;
  overflow-x: auto;

  code {
    font-family: 'Courier New', monospace;
    font-size: 13px;
    line-height: 1.6;
    color: #333;
  }
}

.license-card {
  text-align: center;

  a {
    color: #1890ff;
  }
}

.github-link {
  margin-top: 12px;
  font-size: 16px;

  .anticon {
    margin-right: 8px;
  }

  a {
    font-size: 16px;
  }
}

.thanks-card {
  p {
    margin-bottom: 12px;
  }

  a {
    color: #1890ff;
    display: block;
    padding: 4px 0;
  }
}

@media (max-width: 768px) {
  .about-container {
    padding: 8px;
  }

  .example-item {
    flex-direction: column;
    align-items: flex-start;

    .example-desc {
      margin-top: 4px;
    }
  }
}
</style>
