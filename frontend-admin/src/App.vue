<template>
  <div id="app">
    <a-config-provider :locale="locale">
      <div class="app-container">
        <!-- 侧边栏 -->
        <a-layout class="app-layout">
          <a-layout-sider 
            v-model:collapsed="collapsed" 
            :trigger="null" 
            collapsible 
            class="sidebar"
            width="200"
          >
            <div class="logo" @click="goHome">
              <h2 v-if="!collapsed">log4M</h2>
              <h2 v-else>L</h2>
            </div>
            <a-menu 
              v-model:selectedKeys="selectedKeys" 
              mode="inline" 
              theme="dark"
              @click="handleMenuClick"
            >
              <a-menu-item key="/dashboard">
                <template #icon>
                  <DashboardOutlined />
                </template>
                <span>仪表板</span>
              </a-menu-item>
              <a-menu-item key="/bills">
                <template #icon>
                  <UnorderedListOutlined />
                </template>
                <span>账单管理</span>
              </a-menu-item>
              <a-menu-item key="/categories">
                <template #icon>
                  <AppstoreOutlined />
                </template>
                <span>分类管理</span>
              </a-menu-item>
              <a-menu-item key="/rules">
                <template #icon>
                  <SettingOutlined />
                </template>
                <span>规则管理</span>
              </a-menu-item>
            </a-menu>
          </a-layout-sider>
          
          <a-layout>
            <!-- 顶部导航 -->
            <a-layout-header class="header">
              <menu-unfold-outlined
                v-if="collapsed"
                class="trigger"
                @click="toggleSidebar"
              />
              <menu-fold-outlined
                v-else
                class="trigger"
                @click="toggleSidebar"
              />
            </a-layout-header>
            
            <!-- 主要内容区域 -->
            <a-layout-content class="content">
              <router-view />
            </a-layout-content>
          </a-layout>
        </a-layout>
      </div>
    </a-config-provider>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { 
  HomeOutlined, 
  DashboardOutlined,
  UnorderedListOutlined,
  AppstoreOutlined,
  SettingOutlined,
  MenuUnfoldOutlined,
  MenuFoldOutlined
} from '@ant-design/icons-vue'
import zhCN from 'ant-design-vue/es/locale/zh_CN'

const locale = ref(zhCN)
const router = useRouter()
const route = useRoute()
const collapsed = ref(false)
const selectedKeys = ref<string[]>([route.path])

// 监听路由变化，更新选中的菜单项
watch(
  () => route.path,
  (newPath) => {
    selectedKeys.value = [newPath]
  }
)

const goHome = () => {
  router.push('/dashboard')
}

const toggleSidebar = () => {
  collapsed.value = !collapsed.value
}

const handleMenuClick = ({ key }: { key: string }) => {
  router.push(key)
}
</script>

<style>
#app {
  min-height: 100vh;
}

.app-layout {
  min-height: 100vh;
}

.sidebar {
  position: fixed;
  height: 100vh;
  left: 0;
  top: 0;
}

.logo {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: bold;
  border-bottom: 1px solid #3a3a3a;
  cursor: pointer;
  transition: background-color 0.3s;
}

.logo:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

.header {
  background: #fff;
  padding: 0 16px;
  display: flex;
  align-items: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 100;
}

.trigger {
  font-size: 18px;
  line-height: 64px;
  padding: 0 24px;
  cursor: pointer;
  transition: color 0.3s;
}

.trigger:hover {
  color: #1890ff;
}

.content {
  margin: 24px 16px;
  padding: 24px;
  background: #f0f2f5;
  min-height: calc(100vh - 112px);
  border-radius: 8px;
}

@media (max-width: 768px) {
  .content {
    margin: 16px 8px;
    padding: 16px;
  }
}
</style>