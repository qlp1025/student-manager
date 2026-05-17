<template>
  <el-container class="layout-container">
    <el-aside :width="isCollapse ? '64px' : '220px'" class="sidebar">
      <div class="logo">
        <el-icon size="28"><School /></el-icon>
        <span v-if="!isCollapse">学生管理</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        class="sidebar-menu"
        background-color="#1a1a2e"
        text-color="#909399"
        active-text-color="#409EFF"
        :router="true"
        :collapse="isCollapse"
        :collapse-transition="false"
      >
        <el-menu-item index="/home">
          <el-icon><HomeFilled /></el-icon>
          <span>首页</span>
        </el-menu-item>
        <el-menu-item index="/student" v-if="userStore.isAdmin || userStore.isTeacher">
          <el-icon><User /></el-icon>
          <span>学生管理</span>
        </el-menu-item>
        <el-menu-item index="/class" v-if="userStore.isAdmin">
          <el-icon><OfficeBuilding /></el-icon>
          <span>班级管理</span>
        </el-menu-item>
        <el-menu-item index="/subject" v-if="userStore.isAdmin || userStore.isTeacher">
          <el-icon><Reading /></el-icon>
          <span>科目管理</span>
        </el-menu-item>
        <el-menu-item index="/score">
          <el-icon><DataLine /></el-icon>
          <span>成绩管理</span>
        </el-menu-item>
        <el-menu-item index="/notice" v-if="userStore.isAdmin">
          <el-icon><Bell /></el-icon>
          <span>公告管理</span>
        </el-menu-item>
        <el-menu-item index="/statistics">
          <el-icon><DataAnalysis /></el-icon>
          <span>成绩统计</span>
        </el-menu-item>
        <el-menu-item index="/exam" v-if="userStore.isAdmin">
          <el-icon><Document /></el-icon>
          <span>试题管理</span>
        </el-menu-item>
        <el-sub-menu index="system" v-if="userStore.isAdmin">
          <template #title>
            <el-icon><Setting /></el-icon>
            <span>系统管理</span>
          </template>
          <el-menu-item index="/system/import">数据导入</el-menu-item>
          <el-menu-item index="/system/user">用户管理</el-menu-item>
          <el-menu-item index="/system/log">操作日志</el-menu-item>
          <el-menu-item index="/system/database">数据库查询</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="header">
        <div class="header-left">
          <el-button text @click="isCollapse = !isCollapse">
            <el-icon size="20"><Fold v-if="!isCollapse" /><Expand v-else /></el-icon>
          </el-button>
          <el-breadcrumb separator="/" style="margin-left: 10px">
            <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="$route.meta.title !== '首页'">
              {{ $route.meta.title }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="36" :icon="UserFilled" />
              <span class="username">{{ userStore.realName || userStore.username }}</span>
              <el-tag size="small" :type="getRoleTagType()">{{ getRoleName() }}</el-tag>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="password">修改密码</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="main-content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { Fold, Expand } from '@element-plus/icons-vue'
import { useUserStore } from '../stores/user'
import { userApi } from '../api/student'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const isCollapse = ref(false)

const activeMenu = computed(() => route.path)

const getRoleName = () => {
  const map = { admin: '管理员', teacher: '教师', student: '学生' }
  return map[userStore.roleKey] || ''
}

const getRoleTagType = () => {
  const map = { admin: 'danger', teacher: 'warning', student: 'success' }
  return map[userStore.roleKey] || 'info'
}

const handleCommand = async (command) => {
  if (command === 'logout') {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      type: 'warning'
    })
    userStore.logout()
    router.push('/login')
  } else if (command === 'password') {
    showPasswordDialog()
  }
}

const showPasswordDialog = () => {
  ElMessageBox.prompt('请输入新密码', '修改密码', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputType: 'password'
  }).then(async ({ value }) => {
    await userApi.updatePassword(userStore.userId, value)
  }).catch(() => {})
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.sidebar {
  background: #1a1a2e;
  border-right: 1px solid rgba(255, 255, 255, 0.05);
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  color: #409EFF;
  font-size: 18px;
  font-weight: 600;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
}

.logo span {
  white-space: nowrap;
}

.sidebar-menu {
  border-right: none;
  background: transparent;
}

.sidebar-menu:not(.el-menu--collapse) {
  width: 220px;
}

.sidebar-menu .el-menu-item {
  height: 50px;
  line-height: 50px;
}

.sidebar-menu .el-menu-item:hover {
  background: rgba(64, 158, 255, 0.1);
}

.sidebar-menu .el-menu-item.is-active {
  background: rgba(64, 158, 255, 0.15);
}

.sidebar-menu.el-menu--collapse .el-menu-item span {
  display: none;
}

.header {
  background: #1a1a2e;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.header-left .el-breadcrumb {
  color: #909399;
}

.header-left .el-breadcrumb .el-breadcrumb__item:last-child {
  color: #fff;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  color: #fff;
}

.username {
  font-size: 14px;
}

.main-content {
  background: #0f0f1a;
  padding: 20px;
  overflow-y: auto;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
