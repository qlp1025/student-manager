import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userId = ref(localStorage.getItem('userId') || null)
  const username = ref(localStorage.getItem('username') || '')
  const realName = ref(localStorage.getItem('realName') || '')
  const roleKey = ref(localStorage.getItem('roleKey') || '')
  const avatar = ref(localStorage.getItem('avatar') || '')

  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => roleKey.value === 'admin')
  const isTeacher = computed(() => roleKey.value === 'teacher')
  const isStudent = computed(() => roleKey.value === 'student')

  function setUser(data) {
    token.value = data.token
    userId.value = data.userId
    username.value = data.username
    realName.value = data.realName
    roleKey.value = data.roleKey
    avatar.value = data.avatar || ''

    localStorage.setItem('token', data.token)
    localStorage.setItem('userId', data.userId)
    localStorage.setItem('username', data.username)
    localStorage.setItem('realName', data.realName)
    localStorage.setItem('roleKey', data.roleKey)
    localStorage.setItem('avatar', data.avatar || '')
  }

  function logout() {
    token.value = ''
    userId.value = null
    username.value = ''
    realName.value = ''
    roleKey.value = ''
    avatar.value = ''

    localStorage.clear()
    ElMessage.success('退出成功')
  }

  return {
    token, userId, username, realName, roleKey, avatar,
    isLoggedIn, isAdmin, isTeacher, isStudent,
    setUser, logout
  }
})
