import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    redirect: '/dashboard'
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('@/views/dashboard/index.vue'),
    meta: {
      title: '仪表板'
    }
  },
  {
    path: '/bills',
    name: 'BillManagement',
    component: () => import('@/views/bill/index.vue'),
    meta: {
      title: '账单管理'
    }
  },
  {
    path: '/categories',
    name: 'CategoryManagement',
    component: () => import('@/views/category/index.vue'),
    meta: {
      title: '分类管理'
    }
  },
  {
    path: '/rules',
    name: 'RuleManagement',
    component: () => import('@/views/rule/index.vue'),
    meta: {
      title: '规则管理'
    }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router