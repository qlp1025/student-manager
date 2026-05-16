<template>
  <div class="home-container">
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <div class="stat-card blue">
          <div class="stat-icon"><el-icon><User /></el-icon></div>
          <div class="stat-info">
            <p class="stat-title">学生总数</p>
            <p class="stat-value">{{ stats.studentCount }}</p>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card green">
          <div class="stat-icon"><el-icon><OfficeBuilding /></el-icon></div>
          <div class="stat-info">
            <p class="stat-title">班级数量</p>
            <p class="stat-value">{{ stats.classCount }}</p>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card orange">
          <div class="stat-icon"><el-icon><Reading /></el-icon></div>
          <div class="stat-info">
            <p class="stat-title">科目数量</p>
            <p class="stat-value">{{ stats.subjectCount }}</p>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card purple">
          <div class="stat-icon"><el-icon><Bell /></el-icon></div>
          <div class="stat-info">
            <p class="stat-title">公告数量</p>
            <p class="stat-value">{{ stats.noticeCount }}</p>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :span="16">
        <div class="card notice-card">
          <div class="card-header">
            <h3>最新公告</h3>
          </div>
          <div class="card-body">
            <el-carousel height="280px" v-if="notices.length > 0" :interval="5000">
              <el-carousel-item v-for="notice in notices" :key="notice.id">
                <div class="notice-item" @click="showNoticeDetail(notice)">
                  <h4>{{ notice.title }}</h4>
                  <p class="notice-content">{{ notice.content }}</p>
                  <div class="notice-meta">
                    <span><el-icon><User /></el-icon> {{ notice.publisher }}</span>
                    <span><el-icon><Clock /></el-icon> {{ formatTime(notice.publishTime) }}</span>
                  </div>
                </div>
              </el-carousel-item>
            </el-carousel>
            <el-empty v-else description="暂无公告" />
          </div>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="card quick-card">
          <div class="card-header">
            <h3>快捷入口</h3>
          </div>
          <div class="card-body quick-links">
            <div class="quick-item" @click="$router.push('/student')" v-if="userStore.isAdmin || userStore.isTeacher">
              <el-icon><User /></el-icon>
              <span>学生管理</span>
            </div>
            <div class="quick-item" @click="$router.push('/score')">
              <el-icon><DataLine /></el-icon>
              <span>成绩查询</span>
            </div>
            <div class="quick-item" @click="$router.push('/notice')" v-if="userStore.isAdmin">
              <el-icon><Bell /></el-icon>
              <span>发布公告</span>
            </div>
            <div class="quick-item" @click="$router.push('/class')" v-if="userStore.isAdmin">
              <el-icon><OfficeBuilding /></el-icon>
              <span>班级管理</span>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="24">
        <div class="card welcome-card">
          <div class="welcome-content">
            <h2>欢迎使用学生管理系统</h2>
            <p>您好，{{ userStore.realName || userStore.username }}！{{ getWelcomeMsg() }}</p>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../stores/user'
import { studentApi, classApi, subjectApi, noticeApi } from '../api/student'

const userStore = useUserStore()

const stats = ref({
  studentCount: 0,
  classCount: 0,
  subjectCount: 0,
  noticeCount: 0
})

const notices = ref([])

onMounted(async () => {
  await loadStats()
  await loadNotices()
})

const loadStats = async () => {
  try {
    const [studentRes, classRes, subjectRes, noticeRes] = await Promise.all([
      studentApi.count(),
      classApi.list(),
      subjectApi.list(),
      noticeApi.list()
    ])
    stats.value = {
      studentCount: studentRes.data || 0,
      classCount: classRes.data?.length || 0,
      subjectCount: subjectRes.data?.length || 0,
      noticeCount: noticeRes.data?.length || 0
    }
  } catch (error) {
    console.error(error)
  }
}

const loadNotices = async () => {
  try {
    const res = await noticeApi.published()
    notices.value = res.data || []
  } catch (error) {
    console.error(error)
  }
}

const formatTime = (time) => {
  if (!time) return ''
  return new Date(time).toLocaleString('zh-CN')
}

const getWelcomeMsg = () => {
  const hour = new Date().getHours()
  if (hour < 12) return '上午好！今天也要加油哦～'
  if (hour < 18) return '下午好！工作学习顺利～'
  return '晚上好！注意休息～'
}

const showNoticeDetail = (notice) => {
  ElMessage.info(`公告内容：${notice.content}`)
}
</script>

<style scoped>
.home-container {
  padding: 0;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  padding: 24px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  gap: 20px;
  transition: transform 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
}

.stat-card.blue { background: linear-gradient(135deg, #667eea, #764ba2); }
.stat-card.green { background: linear-gradient(135deg, #11998e, #38ef7d); }
.stat-card.orange { background: linear-gradient(135deg, #f093fb, #f5576c); }
.stat-card.purple { background: linear-gradient(135deg, #4facfe, #00f2fe); }

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: #fff;
}

.stat-info {
  color: #fff;
}

.stat-title {
  font-size: 14px;
  opacity: 0.9;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 32px;
  font-weight: 600;
}

.card {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.05);
  overflow: hidden;
}

.card-header {
  padding: 16px 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
}

.card-header h3 {
  color: #fff;
  font-size: 16px;
  font-weight: 500;
}

.card-body {
  padding: 20px;
}

.notice-item {
  padding: 20px;
  background: rgba(255, 255, 255, 0.03);
  border-radius: 8px;
  height: 100%;
  cursor: pointer;
  transition: all 0.3s;
}

.notice-item:hover {
  background: rgba(64, 158, 255, 0.1);
}

.notice-item h4 {
  color: #409EFF;
  font-size: 18px;
  margin-bottom: 12px;
}

.notice-content {
  color: #909399;
  font-size: 14px;
  line-height: 1.8;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 4;
  -webkit-box-orient: vertical;
  height: 100px;
}

.notice-meta {
  display: flex;
  gap: 20px;
  margin-top: 16px;
  color: #606266;
  font-size: 12px;
}

.notice-meta span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.quick-card .card-body {
  padding: 10px;
}

.quick-links {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
}

.quick-item {
  padding: 30px 20px;
  background: rgba(255, 255, 255, 0.03);
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  transition: all 0.3s;
  color: #909399;
}

.quick-item:hover {
  background: rgba(64, 158, 255, 0.15);
  color: #409EFF;
  transform: scale(1.02);
}

.quick-item .el-icon {
  font-size: 32px;
}

.quick-item span {
  font-size: 14px;
}

.welcome-card {
  background: linear-gradient(135deg, rgba(64, 158, 255, 0.2), rgba(0, 242, 254, 0.1));
}

.welcome-content {
  padding: 40px;
  text-align: center;
}

.welcome-content h2 {
  color: #409EFF;
  font-size: 24px;
  margin-bottom: 12px;
}

.welcome-content p {
  color: #909399;
  font-size: 14px;
}
</style>
