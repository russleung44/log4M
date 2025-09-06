<template>
  <div id="app">
    <a-config-provider :locale="locale">
      <div class="app-container">
        <div class="home-icon-container">
          <a-tooltip title="返回首页">
            <a-button 
              type="primary" 
              shape="circle" 
              size="large"
              @click="goHome"
              class="home-icon"
            >
              <template #icon>
                <HomeOutlined />
              </template>
            </a-button>
          </a-tooltip>
        </div>
        <router-view />
      </div>
    </a-config-provider>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { HomeOutlined } from '@ant-design/icons-vue'
import zhCN from 'ant-design-vue/es/locale/zh_CN'

const locale = ref(zhCN)
const router = useRouter()

const goHome = () => {
  router.push('/dashboard')
}
</script>

<style>
#app {
  min-height: 100vh;
}

.app-container {
  position: relative;
  min-height: 100vh;
}

.home-icon-container {
  position: fixed;
  top: 24px;
  left: 24px;
  z-index: 1000;
  transition: all 0.3s ease;
}

.home-icon {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  cursor: pointer;
  background-color: #1890ff;
  border-color: #1890ff;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.home-icon:hover {
  background-color: #40a9ff;
  border-color: #40a9ff;
  transform: scale(1.1);
}

.home-icon .anticon {
  font-size: 18px;
  color: white;
}

@media (max-width: 768px) {
  .home-icon-container {
    top: 16px;
    left: 16px;
  }
  
  .home-icon {
    width: 36px;
    height: 36px;
  }
  
  .home-icon .anticon {
    font-size: 16px;
  }
}
</style>