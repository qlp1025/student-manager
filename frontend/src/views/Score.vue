<template>
  <div class="page-container">
    <div class="toolbar">
      <div class="search-bar">
        <el-select v-model="queryParams.classId" placeholder="选择班级" clearable @change="onClassChange" style="width: 150px">
          <el-option v-for="c in classList" :key="c.id" :label="c.className" :value="c.id" />
        </el-select>
        <el-select v-model="queryParams.studentId" placeholder="选择学生" clearable filterable style="width: 180px; margin-left: 10px" @change="loadData">
          <el-option v-for="s in filteredStudentList" :key="s.id" :label="`${s.name} (${s.studentNo})`" :value="s.id" />
        </el-select>
        <el-select v-model="queryParams.examId" placeholder="选择考试" clearable style="width: 150px; margin-left: 10px" @change="loadData">
          <el-option v-for="e in examList" :key="e.id" :label="e.examName" :value="e.id" />
        </el-select>
        <el-date-picker v-model="queryParams.examDate" type="date" value-format="YYYY-MM-DD" placeholder="选择考试日期" clearable style="width: 150px; margin-left: 10px" @change="loadData" />
        <el-select v-model="queryParams.subjectIds" multiple placeholder="筛选科目" clearable style="width: 250px; margin-left: 10px" @change="loadData">
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
      <el-table-column prop="examName" label="考试名称" width="120" />
      <el-table-column prop="examDate" label="考试日期" width="120" />
      <el-table-column v-for="subject in displayedSubjects" :key="subject.id" :label="subject.subjectName" width="100" align="center">
        <template #default="{ row }">
          <span :class="getScoreClass(row.scores[subject.id])">{{ row.scores[subject.id] || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="总分" width="100" align="center">
        <template #default="{ row }">
          <strong>{{ row.totalScore }}</strong>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" v-if="!userStore.isStudent">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
          <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="table-info">
      共 {{ total }} 名学生
    </div>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="90%" :close-on-click-modal="false">
      <el-form :model="batchForm" label-width="100px" style="margin-bottom: 15px">
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="班级">
              <el-select v-model="batchForm.classId" placeholder="选择班级" clearable @change="onBatchClassChange" style="width: 100%">
                <el-option v-for="c in classList" :key="c.id" :label="c.className" :value="c.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="考试名称">
              <el-select v-model="batchForm.examId" placeholder="选择考试" style="width: 100%">
                <el-option v-for="e in examList" :key="e.id" :label="e.examName" :value="e.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="考试日期">
              <el-date-picker v-model="batchForm.examDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <div v-if="batchForm.classId && batchForm.examId && batchForm.examDate" class="batch-table">
        <el-table :data="batchStudents" border max-height="400">
          <el-table-column prop="studentNo" label="学号" width="120" fixed />
          <el-table-column prop="name" label="姓名" width="120" fixed />
          <el-table-column v-for="subject in subjectList" :key="subject.id" :label="subject.subjectName" width="130" align="center">
            <template #default="{ row }">
              <el-input-number
                v-model="batchScores[row.id + '_' + subject.id]"
                :min="0"
                :max="100"
                :precision="1"
                size="small"
                controls-position="right"
                style="width: 100px"
              />
            </template>
          </el-table-column>
        </el-table>
        <div class="batch-tip">分数为空表示未录入该科目</div>
      </div>
      <div v-else class="batch-tip-warning">请选择班级和考试日期</div>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleBatchSubmit">确定提交</el-button>
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

    <el-dialog v-model="editDialogVisible" title="编辑成绩" width="500px">
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="学生">
          <el-select v-model="editForm.studentId" style="width: 100%" filterable disabled>
            <el-option :label="editForm.studentName" :value="editForm.studentId" />
          </el-select>
        </el-form-item>
        <el-form-item label="考试日期">
          <el-date-picker v-model="editForm.examDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" @change="loadStudentScores" />
        </el-form-item>
        <el-form-item label="科目">
          <el-select v-model="editForm.subjectId" style="width: 100%" @change="loadStudentScores">
            <el-option v-for="s in subjectList" :key="s.id" :label="s.subjectName" :value="s.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="分数">
          <el-input-number v-model="editForm.score" :min="0" :max="100" :precision="1" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleUpdate">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="deleteDialogVisible" title="删除成绩" width="400px">
      <p>确定要删除学生 <strong>{{ deleteForm.studentName }}</strong> 的成绩记录吗？</p>
      <el-form :model="deleteForm" label-width="80px" style="margin-top: 15px">
        <el-form-item label="考试日期">
          <el-date-picker v-model="deleteForm.examDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" @change="loadDeleteScores" />
        </el-form-item>
        <el-form-item label="科目">
          <el-select v-model="deleteForm.subjectId" style="width: 100%" clearable placeholder="留空删除全部">
            <el-option v-for="s in subjectList" :key="s.id" :label="s.subjectName" :value="s.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="deleteDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="confirmDelete">确定删除</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Search, Download, Refresh, Upload } from '@element-plus/icons-vue'
import { scoreApi, studentApi, classApi, subjectApi, examApi } from '../api/student'
import { useUserStore } from '../stores/user'

const userStore = useUserStore()
const loading = ref(false)
const dialogVisible = ref(false)
const importDialogVisible = ref(false)
const editDialogVisible = ref(false)
const deleteDialogVisible = ref(false)
const dialogTitle = ref('')
const tableData = ref([])
const total = ref(0)
const studentList = ref([])
const classList = ref([])
const subjectList = ref([])
const examList = ref([])
const allStudentList = ref([])
const uploadRef = ref()
const token = userStore.token
const importUrl = '/api/score/import'

const queryParams = reactive({
  classId: null,
  studentId: null,
  examId: null,
  examDate: null,
  subjectIds: [],
  pageNum: 1,
  pageSize: 20
})

const batchForm = reactive({
  classId: null,
  examId: null,
  examDate: ''
})
const batchScores = reactive({})

const editForm = reactive({
  id: null,
  studentId: null,
  studentName: '',
  subjectId: null,
  examDate: '',
  score: null
})

const deleteForm = reactive({
  studentId: null,
  studentName: '',
  examDate: '',
  subjectId: null
})

const filteredStudentList = computed(() => {
  if (!queryParams.classId) return allStudentList.value
  return allStudentList.value.filter(s => s.classId === queryParams.classId)
})

const batchStudents = computed(() => {
  if (!batchForm.classId) return []
  return allStudentList.value.filter(s => s.classId === batchForm.classId)
})

const onBatchClassChange = () => {
  Object.keys(batchScores).forEach(key => delete batchScores[key])
}

const handleBatchSubmit = async () => {
  const scores = []
  for (const student of batchStudents.value) {
    for (const subject of subjectList.value) {
      const key = student.id + '_' + subject.id
      const score = batchScores[key]
      if (score !== undefined && score !== null && score !== '') {
        scores.push({
          studentId: student.id,
          subjectId: subject.id,
          score: score,
          examId: batchForm.examId,
          examDate: batchForm.examDate,
          status: 1
        })
      }
    }
  }

  if (scores.length === 0) {
    ElMessage.warning('请至少录入一条成绩')
    return
  }

  try {
    await scoreApi.batchAdd(scores)
    ElMessage.success('成功录入 ' + scores.length + ' 条成绩')
    dialogVisible.value = false
    resetBatchForm()
    loadData()
  } catch (error) {
    console.error(error)
    ElMessage.error('录入失败')
  }
}

const resetBatchForm = () => {
  batchForm.classId = null
  batchForm.examDate = ''
  Object.keys(batchScores).forEach(key => delete batchScores[key])
}

const displayedSubjects = computed(() => {
  if (queryParams.subjectIds && queryParams.subjectIds.length > 0) {
    return subjectList.value.filter(s => queryParams.subjectIds.includes(s.id))
  }
  return subjectList.value
})

onMounted(async () => {
  await Promise.all([loadClasses(), loadSubjects(), loadExams()])
  await loadStudents()
  if (userStore.isStudent) {
    queryParams.studentId = userStore.userId
  }
  loadData()
})

const loadStudents = async () => {
  const res = await studentApi.list()
  allStudentList.value = res.data || []
  studentList.value = res.data || []
}

const loadClasses = async () => {
  const res = await classApi.list()
  classList.value = res.data || []
}

const loadSubjects = async () => {
  const res = await subjectApi.list()
  subjectList.value = res.data || []
}

const loadExams = async () => {
  const res = await examApi.list()
  examList.value = res.data || []
}

const loadData = async () => {
  loading.value = true
  try {
    let scores
    if (queryParams.studentId) {
      scores = await scoreApi.getByStudentId(queryParams.studentId)
      scores = scores.data || []
    } else {
      scores = await scoreApi.list()
      scores = scores.data || []
    }

    const scoreMap = new Map()
    scores.forEach(score => {
      if (queryParams.classId && score.classId !== queryParams.classId) return
      if (queryParams.examId && score.examId !== queryParams.examId) return
      if (queryParams.subjectIds && queryParams.subjectIds.length > 0) {
        if (!queryParams.subjectIds.includes(score.subjectId)) return
      }
      if (queryParams.examDate && score.examDate !== queryParams.examDate) return
      const key = score.studentId + '_' + score.examId + '_' + score.examDate
      if (!scoreMap.has(key)) {
        scoreMap.set(key, {
          studentId: score.studentId,
          studentNo: score.studentNo,
          studentName: score.studentName,
          className: score.className,
          examId: score.examId,
          examName: score.examName,
          examDate: score.examDate,
          scores: {},
          totalScore: 0
        })
      }
      const record = scoreMap.get(key)
      record.scores[score.subjectId] = score.score
      if (score.status === 1) {
        record.totalScore = (record.totalScore || 0) + Number(score.score)
      }
    })

    const result = Array.from(scoreMap.values())
    result.sort((a, b) => {
      if (a.studentNo !== b.studentNo) return a.studentNo.localeCompare(b.studentNo)
      return b.examDate.localeCompare(a.examDate)
    })

    tableData.value = result
    total.value = result.length
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const onClassChange = () => {
  queryParams.studentId = null
  loadData()
}

const resetQuery = () => {
  queryParams.classId = null
  queryParams.studentId = null
  queryParams.examId = null
  queryParams.examDate = null
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
  resetBatchForm()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  editForm.studentId = row.studentId
  editForm.studentName = row.studentName
  editForm.subjectId = null
  editForm.examDate = ''
  editForm.score = null
  editForm.id = null
  editDialogVisible.value = true
}

const loadStudentScores = async () => {
  if (!editForm.studentId || !editForm.examDate || !editForm.subjectId) return
  try {
    const scores = await scoreApi.getByStudentId(editForm.studentId)
    const scoreList = scores.data || []
    const score = scoreList.find(s => s.examDate === editForm.examDate && s.subjectId === editForm.subjectId)
    if (score) {
      editForm.id = score.id
      editForm.score = score.score
    } else {
      editForm.id = null
      editForm.score = null
    }
  } catch (error) {
    console.error(error)
  }
}

const handleUpdate = async () => {
  if (!editForm.subjectId || !editForm.examDate || editForm.score === null) {
    ElMessage.warning('请填写完整信息')
    return
  }
  try {
    const data = {
      id: editForm.id,
      studentId: editForm.studentId,
      subjectId: editForm.subjectId,
      examDate: editForm.examDate,
      score: editForm.score,
      status: 1
    }
    if (editForm.id) {
      await scoreApi.update(data)
      ElMessage.success('更新成功')
    } else {
      await scoreApi.add(data)
      ElMessage.success('添加成功')
    }
    editDialogVisible.value = false
    loadData()
  } catch (error) {
    console.error(error)
    ElMessage.error('操作失败')
  }
}

const handleDelete = (row) => {
  deleteForm.studentId = row.studentId
  deleteForm.studentName = row.studentName
  deleteForm.examDate = row.examDate
  deleteForm.subjectId = null
  deleteDialogVisible.value = true
}

const loadDeleteScores = async () => {
  // just need to trigger UI update
}

const confirmDelete = async () => {
  if (!deleteForm.examDate) {
    ElMessage.warning('请选择考试日期')
    return
  }
  try {
    const scores = await scoreApi.getByStudentId(deleteForm.studentId)
    const scoreList = scores.data || []
    const toDelete = scoreList.filter(s => s.examDate === deleteForm.examDate && (deleteForm.subjectId === null || deleteForm.subjectId === s.subjectId))
    if (toDelete.length === 0) {
      ElMessage.warning('没有找到可删除的成绩记录')
      return
    }
    for (const score of toDelete) {
      await scoreApi.delete(score.id)
    }
    ElMessage.success('删除成功')
    deleteDialogVisible.value = false
    loadData()
  } catch (error) {
    console.error(error)
    ElMessage.error('删除失败')
  }
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
    ElMessage.success('导入成功！共导入 ' + response.data + ' 条记录')
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
.batch-table { margin-top: 20px; }
.batch-tip { margin-top: 10px; color: #909399; font-size: 12px; text-align: center; }
.batch-tip-warning { margin-top: 50px; color: #E6A23C; font-size: 14px; text-align: center; }
.table-info { margin-top: 15px; color: #606266; font-size: 14px; }
</style>
