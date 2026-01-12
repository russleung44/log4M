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
              <a-menu-item key="/about">
                <template #icon>
                  <InfoCircleOutlined />
                </template>
                <span>关于</span>
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
  InfoCircleOutlined,
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