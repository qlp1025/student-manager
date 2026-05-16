import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../stores/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/',
    component: () => import('../views/Layout.vue'),
    redirect: '/home',
    children: [
      {
        path: 'home',
        name: 'Home',
        component: () => import('../views/Home.vue'),
        meta: { title: '首页' }
      },
      {
        path: 'student',
        name: 'Student',
        component: () => import('../views/Student.vue'),
        meta: { title: '学生管理', roles: ['admin', 'teacher'] }
      },
      {
        path: 'class',
        name: 'Class',
        component: () => import('../views/Class.vue'),
        meta: { title: '班级管理', roles: ['admin'] }
      },
      {
        path: 'subject',
        name: 'Subject',
        component: () => import('../views/Subject.vue'),
        meta: { title: '科目管理', roles: ['admin', 'teacher'] }
      },
      {
        path: 'score',
        name: 'Score',
        component: () => import('../views/Score.vue'),
        meta: { title: '成绩管理', roles: ['admin', 'teacher', 'student'] }
      },
      {
        path: 'notice',
        name: 'Notice',
        component: () => import('../views/Notice.vue'),
        meta: { title: '公告管理', roles: ['admin'] }
      },
      {
        path: 'system/import',
        name: 'Import',
        component: () => import('../views/Import.vue'),
        meta: { title: '数据导入', roles: ['admin'] }
      },
      {
        path: 'system/user',
        name: 'SystemUser',
        component: () => import('../views/SystemUser.vue'),
        meta: { title: '用户管理', roles: ['admin'] }
      },
      {
        path: 'system/log',
        name: 'SystemLog',
        component: () => import('../views/SystemLog.vue'),
        meta: { title: '操作日志', roles: ['admin'] }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  const token = userStore.token

  if (to.path === '/login') {
    next()
  } else {
    if (token) {
      if (to.meta.roles) {
        const allowed = to.meta.roles.includes(userStore.roleKey)
        if (allowed) {
          next()
        } else {
          ElMessage.error('没有权限访问该页面')
          next('/home')
        }
      } else {
        next()
      }
    } else {
      next('/login')
    }
  }
})

export default router
