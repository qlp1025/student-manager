<template>
  <div class="page-container">
    <div class="toolbar">
      <div class="search-bar">
        <el-select v-model="queryParams.classId" placeholder="选择班级" clearable @change="loadData" style="width: 150px">
          <el-option v-for="c in classList" :key="c.id" :label="c.className" :value="c.id" />
        </el-select>
        <el-select v-model="queryParams.subjectIds" multiple placeholder="筛选科目" clearable style="width: 250px; margin-left: 10px" @change="loadData">
          <el-option v-for="s in subjectList" :key="s.id" :label="s.subjectName" :value="s.id" />
        </el-select>
        <el-select v-model="selectedSubjects" multiple placeholder="显示科目" clearable style="width: 250px; margin-left: 10px" @change="loadData">
          <el-option v-for="s in subjectList" :key="s.id" :label="s.subjectName" :value="s.id" />
        </el-select>
        <el-button type="primary" :icon="Search" @click="loadData" style="margin-left: 10px">查询</el-button>
        <el-button :icon="Refresh" @click="resetQuery">重置</el-button>
      </div>
      <div class="action-bar">
        <el-button type="primary" :icon="Plus" @click="handleAdd" v-if="!userStore.isStudent">录入成绩</el-button>
        <el-button type="success" :icon="Upload" @click="showImportDialog" v-if="!userStore.isStudent">导入</el-button>
        <el-button type="warning" :icon="Download" @click="handleExport">导出</el-button>
      </div>
    </div>

    <el-table :data="tableData" v-loading="loading" stripe style="width: 100%; margin-top: 20px" border>
      <el-table-column prop="studentNo" label="学号" width="120" fixed />
      <el-table-column prop="studentName" label="姓名" width="100" fixed />
      <el-table-column prop="className" label="班级" width="120" />
      <el-table-column v-for="subject in displayedSubjects" :key="subject.id" :label="subject.subjectName" width="100" align="center">
        <template #default="{ row }">
          <span :class="getScoreClass(row.scores[subject.id])">{{ row.scores[subject.id] || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="总分" width="100" align="center" fixed="right">
        <template #default="{ row }">
          <strong>{{ row.totalScore }}</strong>
        </template>
      </el-table-column>
      <el-table-column label="排名" width="80" align="center" fixed="right">
        <template #default="{ row }">
          <el-tag type="warning" size="small">第{{ row.rank }}名</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" fixed="right" v-if="!userStore.isStudent">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="queryParams.pageNum"
      v-model:page-size="queryParams.pageSize"
      :total="total"
      :page-sizes="[10, 20, 50]"
      layout="total, sizes, prev, pager, next, jumper"
      style="margin-top: 20px; justify-content: flex-end"
      @size-change="loadData"
      @current-change="loadData"
    />

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="学生" prop="studentId">
          <el-select v-model="form.studentId" style="width: 100%" filterable>
            <el-option v-for="s in studentList" :key="s.id" :label="`${s.name} (${s.studentNo})`" :value="s.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="科目" prop="subjectId">
          <el-select v-model="form.subjectId" style="width: 100%">
            <el-option v-for="s in subjectList" :key="s.id" :label="s.subjectName" :value="s.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="分数" prop="score">
          <el-input-number v-model="form.score" :min="0" :max="100" :precision="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="考试日期" prop="examDate">
          <el-date-picker v-model="form.examDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="importDialogVisible" title="导入成绩信息" width="500px">
      <div class="import-template">
        <p>请下载模板文件，按格式填写后上传：</p>
        <el-button type="text" @click="downloadTemplate">下载成绩导入模板</el-button>
      </div>
      <el-upload
        ref="uploadRef"
        class="upload-demo"
        drag
        :action="importUrl"
        :headers="{ Authorization: 'Bearer ' + token }"
        :on-success="handleImportSuccess"
        :on-error="handleImportError"
        :before-upload="beforeUpload"
        accept=".xlsx,.xls"
        style="margin-top: 20px"
      >
        <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
        <div class="el-upload__text">拖拽文件到此处，或 <em>点击上传</em></div>
        <template #tip>
          <div class="el-upload__tip">只能上传 xlsx/xls 文件</div>
        </template>
      </el-upload>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Download, Refresh, Upload } from '@element-plus/icons-vue'
import { scoreApi, studentApi, classApi, subjectApi } from '../api/student'
import { useUserStore } from '../stores/user'

const userStore = useUserStore()
const loading = ref(false)
const dialogVisible = ref(false)
const importDialogVisible = ref(false)
const dialogTitle = ref('')
const tableData = ref([])
const total = ref(0)
const studentList = ref([])
const classList = ref([])
const subjectList = ref([])
const selectedSubjects = ref([])
const formRef = ref()
const uploadRef = ref()
const token = userStore.token
const importUrl = '/api/score/import'

const queryParams = reactive({
  classId: null,
  subjectIds: [],
  pageNum: 1,
  pageSize: 20
})

const form = reactive({ id: null, studentId: null, subjectId: null, score: null, examDate: '', status: 1 })

const rules = {
  studentId: [{ required: true, message: '请选择学生', trigger: 'change' }],
  subjectId: [{ required: true, message: '请选择科目', trigger: 'change' }],
  score: [{ required: true, message: '请输入分数', trigger: 'blur' }],
  examDate: [{ required: true, message: '请选择考试日期', trigger: 'change' }]
}

const displayedSubjects = computed(() => {
  if (selectedSubjects.value.length > 0) {
    return subjectList.value.filter(s => selectedSubjects.value.includes(s.id))
  }
  return subjectList.value
})

onMounted(async () => {
  await Promise.all([loadClasses(), loadSubjects()])
  await loadStudents()
  if (userStore.isStudent) {
    queryParams.studentId = userStore.userId
  }
  loadData()
})

const loadStudents = async () => {
  const res = await studentApi.list()
  studentList.value = res.data || []
}

const loadClasses = async () => {
  const res = await classApi.list()
  classList.value = res.data || []
}

const loadSubjects = async () => {
  const res = await subjectApi.list()
  subjectList.value = res.data || []
  if (subjectList.value.length > 0 && selectedSubjects.value.length === 0) {
    selectedSubjects.value = subjectList.value.slice(0, 5).map(s => s.id)
  }
}

const loadData = async () => {
  loading.value = true
  try {
    const params = { ...queryParams }
    let scores
    if (queryParams.studentId) {
      scores = await scoreApi.getByStudentId(queryParams.studentId)
      scores = scores.data || []
    } else {
      const pageRes = await scoreApi.page(params)
      scores = pageRes.data?.list || []
    }

    const studentMap = new Map()
    scores.forEach(score => {
      if (queryParams.subjectIds && queryParams.subjectIds.length > 0) {
        if (!queryParams.subjectIds.includes(score.subjectId)) return
      }
      const key = score.studentId
      if (!studentMap.has(key)) {
        studentMap.set(key, {
          studentId: score.studentId,
          studentNo: score.studentNo,
          studentName: score.studentName,
          className: score.className,
          scores: {},
          totalScore: 0
        })
      }
      const student = studentMap.get(key)
      student.scores[score.subjectId] = score.score
      if (score.status === 1) {
        student.totalScore = (student.totalScore || 0) + Number(score.score)
      }
    })

    const students = Array.from(studentMap.values())
    students.sort((a, b) => b.totalScore - a.totalScore)
    students.forEach((s, i) => s.rank = i + 1)

    tableData.value = students
    total.value = students.length
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const resetQuery = () => {
  queryParams.classId = null
  queryParams.subjectIds = []
  loadData()
}

const getScoreClass = (score) => {
  if (!score) return ''
  if (score >= 90) return 'score-excellent'
  if (score >= 80) return 'score-good'
  if (score >= 60) return 'score-pass'
  return 'score-fail'
}

const handleAdd = () => {
  dialogTitle.value = '录入成绩'
  Object.assign(form, { id: null, studentId: null, subjectId: null, score: null, examDate: '', status: 1 })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑成绩'
  Object.assign(form, { id: null, studentId: row.studentId, subjectId: null, score: null, examDate: '', status: 1 })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await scoreApi.add(form)
        ElMessage.success('添加成功')
        dialogVisible.value = false
        loadData()
      } catch (error) {
        console.error(error)
      }
    }
  })
}

const showImportDialog = () => {
  importDialogVisible.value = true
}

const beforeUpload = (file) => {
  const isExcel = file.name.endsWith('.xlsx') || file.name.endsWith('.xls')
  if (!isExcel) {
    ElMessage.error('只能上传 Excel 文件！')
  }
  return isExcel
}

const handleImportSuccess = (response) => {
  if (response.code === 200) {
    ElMessage.success(`导入成功！共导入 ${response.data} 条记录`)
    importDialogVisible.value = false
    loadData()
  } else {
    ElMessage.error(response.message || '导入失败')
  }
}

const handleImportError = (error) => {
  ElMessage.error('导入失败：' + error.message)
}

const downloadTemplate = () => {
  window.open('/api/score/template', '_blank')
}

const handleExport = async () => {
  ElMessage.info('正在导出...')
  try {
    const res = await scoreApi.export(queryParams)
    const blob = new Blob([res], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = window.URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = '成绩信息.xlsx'
    a.click()
    window.URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
  } catch (error) {
    ElMessage.error('导出失败')
  }
}
</script>

<style scoped>
.page-container { padding: 20px; }
.toolbar { display: flex; justify-content: space-between; flex-wrap: wrap; gap: 15px; }
.search-bar { display: flex; align-items: center; flex-wrap: wrap; }
.action-bar { display: flex; gap: 10px; }
.score-excellent { color: #67C23A; font-weight: bold; }
.score-good { color: #409EFF; font-weight: bold; }
.score-pass { color: #E6A23C; }
.score-fail { color: #F56C6C; }
.import-template { padding: 10px; background: #f5f7fa; border-radius: 4px; }
.import-template p { margin-bottom: 10px; color: #606266; }
</style>
