<template>
  <div class="page-container">
    <div class="toolbar">
      <div class="search-bar">
        <el-input v-model="queryParams.keyword" placeholder="搜索姓名/学号" clearable @change="loadData" style="width: 200px" />
        <el-select v-model="queryParams.classId" placeholder="选择班级" clearable @change="loadData" style="width: 160px; margin-left: 10px">
          <el-option v-for="c in classList" :key="c.id" :label="c.className" :value="c.id" />
        </el-select>
        <el-select v-model="queryParams.status" placeholder="状态" clearable @change="loadData" style="width: 100px; margin-left: 10px">
          <el-option label="启用" :value="1" />
          <el-option label="禁用" :value="0" />
        </el-select>
        <el-button type="primary" :icon="Search" @click="loadData" style="margin-left: 10px">查询</el-button>
      </div>
      <div class="action-bar">
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增学生</el-button>
        <el-button type="success" :icon="Upload" @click="showImportDialog">Excel导入</el-button>
        <el-button type="warning" :icon="Download" @click="handleExport">Excel导出</el-button>
        <el-button type="danger" :icon="Delete" @click="handleBatchDelete" :disabled="!selectedIds.length">批量删除</el-button>
      </div>
    </div>

    <el-table :data="tableData" v-loading="loading" @selection-change="handleSelectionChange" stripe style="width: 100%; margin-top: 20px">
      <el-table-column type="selection" width="55" />
      <el-table-column prop="studentNo" label="学号" width="120" />
      <el-table-column prop="name" label="姓名" width="100" />
      <el-table-column prop="genderName" label="性别" width="60" />
      <el-table-column prop="age" label="年龄" width="60" />
      <el-table-column prop="className" label="班级" width="120" />
      <el-table-column prop="phone" label="手机号" width="130" />
      <el-table-column prop="address" label="地址" min-width="150" show-overflow-tooltip />
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
          <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination v-model:current-page="queryParams.pageNum" v-model:page-size="queryParams.pageSize" :total="total" :page-sizes="[10, 20, 50, 100]" layout="total, sizes, prev, pager, next, jumper" style="margin-top: 20px; justify-content: flex-end" @size-change="loadData" @current-change="loadData" />

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="800px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="学号" prop="studentNo">
              <el-input v-model="form.studentNo" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="form.name" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-select v-model="form.gender" style="width: 100%">
                <el-option label="男" :value="1" />
                <el-option label="女" :value="0" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="年龄" prop="age">
              <el-input-number v-model="form.age" :min="1" :max="100" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="班级" prop="classId">
              <el-select v-model="form.classId" style="width: 100%">
                <el-option v-for="c in classList" :key="c.id" :label="c.className" :value="c.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="form.phone" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="入学日期" prop="enrollDate">
              <el-date-picker v-model="form.enrollDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="身份证号" prop="idCard">
              <el-input v-model="form.idCard" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="地址" prop="address">
          <el-input v-model="form.address" />
        </el-form-item>
        <el-divider>家长信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="父亲姓名" prop="fatherName">
              <el-input v-model="form.fatherName" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="父亲电话" prop="fatherPhone">
              <el-input v-model="form.fatherPhone" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="父亲身份证" prop="fatherIdCard">
              <el-input v-model="form.fatherIdCard" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="母亲姓名" prop="motherName">
              <el-input v-model="form.motherName" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="母亲电话" prop="motherPhone">
              <el-input v-model="form.motherPhone" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="母亲身份证" prop="motherIdCard">
              <el-input v-model="form.motherIdCard" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" rows="2" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="importDialogVisible" title="导入学生信息" width="500px">
      <div class="import-template">
        <p>请下载模板文件，按格式填写后上传：</p>
        <el-button type="text" @click="downloadTemplate">下载学生信息导入模板</el-button>
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Download, Delete, Upload } from '@element-plus/icons-vue'
import { studentApi, classApi } from '../api/student'
import { useUserStore } from '../stores/user'

const userStore = useUserStore()
const loading = ref(false)
const dialogVisible = ref(false)
const importDialogVisible = ref(false)
const dialogTitle = ref('')
const tableData = ref([])
const total = ref(0)
const selectedIds = ref([])
const classList = ref([])
const formRef = ref()
const uploadRef = ref()
const token = userStore.token
const importUrl = '/api/student/import'

const queryParams = reactive({
  keyword: '',
  classId: null,
  status: null,
  pageNum: 1,
  pageSize: 10
})

const form = reactive({
  id: null,
  studentNo: '',
  name: '',
  gender: 1,
  age: null,
  classId: null,
  phone: '',
  address: '',
  enrollDate: '',
  idCard: '',
  fatherName: '',
  fatherPhone: '',
  fatherIdCard: '',
  motherName: '',
  motherPhone: '',
  motherIdCard: '',
  remark: '',
  status: 1
})

const rules = {
  studentNo: [{ required: true, message: '请输入学号', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  classId: [{ required: true, message: '请选择班级', trigger: 'change' }]
}

onMounted(async () => {
  await loadClasses()
  loadData()
})

const loadClasses = async () => {
  const res = await classApi.list()
  classList.value = res.data || []
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await studentApi.page(queryParams)
    tableData.value = res.data.list || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleSelectionChange = (selection) => {
  selectedIds.value = selection.map(s => s.id)
}

const handleAdd = () => {
  dialogTitle.value = '新增学生'
  Object.assign(form, {
    id: null, studentNo: '', name: '', gender: 1, age: null, classId: null,
    phone: '', address: '', enrollDate: '', idCard: '',
    fatherName: '', fatherPhone: '', fatherIdCard: '',
    motherName: '', motherPhone: '', motherIdCard: '',
    remark: '', status: 1
  })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑学生'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (form.id) {
          await studentApi.update(form)
          ElMessage.success('更新成功')
        } else {
          await studentApi.add(form)
          ElMessage.success('添加成功')
        }
        dialogVisible.value = false
        loadData()
      } catch (error) {
        console.error(error)
      }
    }
  })
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确定要删除该学生吗？', '提示', { type: 'warning' })
  await studentApi.delete(row.id)
  ElMessage.success('删除成功')
  loadData()
}

const handleBatchDelete = async () => {
  await ElMessageBox.confirm(`确定要删除选中的 ${selectedIds.value.length} 名学生吗？`, '提示', { type: 'warning' })
  await studentApi.batchDelete(selectedIds.value)
  ElMessage.success('批量删除成功')
  loadData()
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
  window.open('/api/student/template', '_blank')
}

const handleExport = async () => {
  ElMessage.info('正在导出...')
  try {
    const res = await studentApi.export()
    const blob = new Blob([res], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = window.URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = '学生信息.xlsx'
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
.import-template { padding: 10px; background: #f5f7fa; border-radius: 4px; }
.import-template p { margin-bottom: 10px; color: #606266; }
</style>
