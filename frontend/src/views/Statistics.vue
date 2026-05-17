<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>成绩统计分析</span>
        </div>
      </template>

      <el-tabs v-model="activeTab" @tab-change="onTabChange">
        <!-- 学生成绩历次变化 -->
        <el-tab-pane label="学生成绩历次变化" name="student">
          <div class="filter-bar">
            <el-select v-model="studentQuery.classId" placeholder="选择班级" clearable @change="onStudentClassChange" style="width: 150px">
              <el-option v-for="c in classList" :key="c.id" :label="c.className" :value="c.id" />
            </el-select>
            <el-select v-model="studentQuery.studentId" placeholder="选择学生" clearable filterable style="width: 200px; margin-left: 10px" @change="loadStudentStats">
              <el-option v-for="s in filteredStudentList" :key="s.id" :label="`${s.name} (${s.studentNo})`" :value="s.id" />
            </el-select>
            <el-select v-model="studentQuery.subjectIds" multiple placeholder="选择科目" clearable style="width: 280px; margin-left: 10px" @change="loadStudentStats">
              <el-option v-for="s in subjectList" :key="s.id" :label="s.subjectName" :value="s.id" />
            </el-select>
          </div>
          <div class="chart-container" v-loading="studentLoading">
            <div v-if="!studentQuery.studentId" class="empty-tip">请选择学生</div>
            <div v-else-if="studentChartData.length === 0" class="empty-tip">暂无成绩数据</div>
            <v-chart v-else :option="studentChartOption" style="height: 500px" autoresize />
          </div>
        </el-tab-pane>

        <!-- 班级成绩历次变化 -->
        <el-tab-pane label="班级成绩历次变化" name="class">
          <div class="filter-bar">
            <el-select v-model="classQuery.classId" placeholder="选择班级" clearable @change="loadClassStats" style="width: 150px">
              <el-option v-for="c in classList" :key="c.id" :label="c.className" :value="c.id" />
            </el-select>
            <el-select v-model="classQuery.subjectIds" multiple placeholder="选择科目" clearable style="width: 280px; margin-left: 10px" @change="loadClassStats">
              <el-option v-for="s in subjectList" :key="s.id" :label="s.subjectName" :value="s.id" />
            </el-select>
            <el-radio-group v-model="classQuery.statType" @change="loadClassStats" style="margin-left: 10px">
              <el-radio label="subject">按科目</el-radio>
              <el-radio label="total">总分平均</el-radio>
            </el-radio-group>
          </div>
          <div class="chart-container" v-loading="classLoading">
            <div v-if="!classQuery.classId" class="empty-tip">请选择班级</div>
            <div v-else-if="classChartData.length === 0" class="empty-tip">暂无成绩数据</div>
            <v-chart v-else :option="classChartOption" style="height: 500px" autoresize />
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart } from 'echarts/charts'
import { TitleComponent, TooltipComponent, LegendComponent, GridComponent } from 'echarts/components'
import VChart from 'vue-echarts'
import { classApi, subjectApi, studentApi, scoreApi } from '../api/student'
import { useUserStore } from '../stores/user'

use([CanvasRenderer, LineChart, TitleComponent, TooltipComponent, LegendComponent, GridComponent])

const userStore = useUserStore()
const activeTab = ref('student')
const classList = ref([])
const subjectList = ref([])
const allStudentList = ref([])
const studentLoading = ref(false)
const classLoading = ref(false)

const studentQuery = reactive({
  classId: null,
  studentId: null,
  subjectIds: []
})

const classQuery = reactive({
  classId: null,
  subjectIds: [],
  statType: 'subject'
})

const filteredStudentList = computed(() => {
  if (!studentQuery.classId) return allStudentList.value
  return allStudentList.value.filter(s => s.classId === studentQuery.classId)
})

const studentChartData = ref([])
const classChartData = ref([])

const studentChartOption = computed(() => {
  if (studentChartData.value.length === 0) return {}

  const dates = [...new Set(studentChartData.value.map(d => d.examDate))].sort()
  const subjects = [...new Set(studentChartData.value.map(d => d.subjectName))]

  const series = subjects.map(subjectName => {
    const subjectData = subjects.indexOf(subjectName)
    return {
      name: subjectName,
      type: 'line',
      smooth: true,
      data: dates.map(date => {
        const record = studentChartData.value.find(d => d.examDate === date && d.subjectName === subjectName)
        return record ? record.score : null
      }),
      connectNulls: false
    }
  })

  return {
    title: { text: '学生成绩历次变化', left: 'center' },
    tooltip: { trigger: 'axis' },
    legend: { bottom: 10, data: subjects },
    grid: { left: '10%', right: '10%', bottom: '20%', top: '15%' },
    xAxis: { type: 'category', data: dates, name: '考试日期' },
    yAxis: { type: 'value', name: '分数', min: 0, max: 100 },
    series
  }
})

const classChartOption = computed(() => {
  if (classChartData.value.length === 0) return {}

  const dates = [...new Set(classChartData.value.map(d => d.examDate))].sort()

  if (classQuery.statType === 'total') {
    const avgByDate = dates.map(date => {
      const records = classChartData.value.filter(d => d.examDate === date)
      const avg = records.length > 0 ? records.reduce((sum, r) => sum + r.score, 0) / records.length : null
      return { date, avg: Number(avg.toFixed(2)) }
    })

    return {
      title: { text: '班级总分平均分历次变化', left: 'center' },
      tooltip: { trigger: 'axis' },
      grid: { left: '10%', right: '10%', bottom: '15%', top: '15%' },
      xAxis: { type: 'category', data: dates, name: '考试日期' },
      yAxis: { type: 'value', name: '平均分', min: 0, max: 100 },
      series: [{
        name: '总分平均分',
        type: 'line',
        smooth: true,
        data: avgByDate.map(d => d.avg),
        areaStyle: { opacity: 0.2 },
        lineStyle: { width: 3 },
        itemStyle: { color: '#409EFF' }
      }]
    }
  }

  const subjects = [...new Set(classChartData.value.map(d => d.subjectName))]

  const series = subjects.map(subjectName => {
    return {
      name: subjectName,
      type: 'line',
      smooth: true,
      data: dates.map(date => {
        const record = classChartData.value.find(d => d.examDate === date && d.subjectName === subjectName)
        return record ? Number(record.score.toFixed(2)) : null
      }),
      connectNulls: false
    }
  })

  return {
    title: { text: '班级各科平均分历次变化', left: 'center' },
    tooltip: { trigger: 'axis' },
    legend: { bottom: 10, data: subjects },
    grid: { left: '10%', right: '10%', bottom: '20%', top: '15%' },
    xAxis: { type: 'category', data: dates, name: '考试日期' },
    yAxis: { type: 'value', name: '平均分', min: 0, max: 100 },
    series
  }
})

onMounted(async () => {
  await Promise.all([loadClasses(), loadSubjects(), loadStudents()])
})

const loadClasses = async () => {
  const res = await classApi.list()
  classList.value = res.data || []
}

const loadSubjects = async () => {
  const res = await subjectApi.list()
  subjectList.value = res.data || []
}

const loadStudents = async () => {
  const res = await studentApi.list()
  allStudentList.value = res.data || []
}

const onTabChange = () => {
  if (activeTab.value === 'class' && classQuery.classId) {
    loadClassStats()
  }
}

const onStudentClassChange = () => {
  studentQuery.studentId = null
}

const loadStudentStats = async () => {
  if (!studentQuery.studentId) {
    studentChartData.value = []
    return
  }
  studentLoading.value = true
  try {
    const res = await scoreApi.getStudentStats(studentQuery.studentId, studentQuery.subjectIds)
    studentChartData.value = res.data || []
  } catch (error) {
    console.error(error)
  } finally {
    studentLoading.value = false
  }
}

const loadClassStats = async () => {
  if (!classQuery.classId) {
    classChartData.value = []
    return
  }
  classLoading.value = true
  try {
    const res = await scoreApi.getClassStats(classQuery.classId, classQuery.subjectIds)
    classChartData.value = res.data || []
  } catch (error) {
    console.error(error)
  } finally {
    classLoading.value = false
  }
}
</script>

<style scoped>
.page-container { padding: 20px; }
.card-header { font-weight: 600; }
.filter-bar { display: flex; align-items: center; flex-wrap: wrap; margin-bottom: 20px; }
.chart-container { width: 100%; min-height: 500px; }
.empty-tip { text-align: center; color: #909399; padding: 100px 0; font-size: 14px; }
</style>
