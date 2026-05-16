<template>
  <div class="page-container">
    <el-row :gutter="20">
      <el-col :span="16">
        <el-card class="import-card">
          <template #header>
            <div class="card-header">
              <span>Excel数据导入（创建新表）</span>
            </div>
          </template>

          <el-form :model="form" label-width="140px">
            <el-form-item label="输入表名（不含前缀）">
              <el-input v-model="form.tableName" placeholder="例如：abc" clearable style="width: 300px" />
              <span class="tip">表名将自动添加 I_ 前缀</span>
            </el-form-item>

            <el-form-item label="预览表名">
              <el-tag type="success" size="large">I_{{ form.tableName || 'xxx' }}</el-tag>
            </el-form-item>

            <el-form-item label="上传Excel文件">
              <el-upload
                ref="uploadRef"
                class="upload-demo"
                drag
                :auto-upload="false"
                :on-change="handleFileChange"
                :on-remove="handleFileRemove"
                accept=".xlsx,.xls"
                style="width: 100%"
              >
                <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
                <div class="el-upload__text">拖拽文件到此处，或 <em>点击上传</em></div>
              </el-upload>
            </el-form-item>

            <el-form-item label="列映射预览" v-if="columnCount > 0">
              <el-alert type="success" :closable="false" show-icon>
                <template #default>
                  <div class="column-preview">
                    <span v-for="i in columnCount" :key="i" class="column-item">
                      {{ String.fromCharCode(64 + i) }} → COL_{{ String.fromCharCode(64 + i) }}
                    </span>
                  </div>
                </template>
              </el-alert>
            </el-form-item>

            <el-form-item>
              <el-button type="primary" :icon="Upload" @click="handleImport" :loading="loading" :disabled="!canImport">
                创建表并导入数据
              </el-button>
              <el-button :icon="Download" @click="downloadTemplate">下载模板</el-button>
            </el-form-item>
          </el-form>

          <el-divider />

          <div class="result-section" v-if="importResult">
            <el-alert :type="importResult.failed === 0 ? 'success' : 'warning'" :closable="false" show-icon>
              <template #default>
                <div class="result-info">
                  <p>表名: <strong>{{ importResult.tableName }}</strong></p>
                  <p>列数: {{ importResult.columnCount }} | 数据行数: {{ importResult.dataRowCount }}</p>
                  <p>导入成功: {{ importResult.success }} 条 | 失败: {{ importResult.failed }} 条</p>
                </div>
                <div v-if="importResult.errors && importResult.errors.length > 0" class="error-list">
                  <div v-for="(err, i) in importResult.errors.slice(0, 10)" :key="i" class="error-item">
                    第{{ err.row }}行: {{ err.message }}
                  </div>
                  <div v-if="importResult.errors.length > 10" class="error-item">
                    ...还有 {{ importResult.errors.length - 10 }} 条错误
                  </div>
                </div>
              </template>
            </el-alert>
          </div>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card class="table-list-card">
          <template #header>
            <div class="card-header">
              <span>已导入的表</span>
              <el-button type="text" @click="loadTables">刷新</el-button>
            </div>
          </template>

          <div v-if="tables.length === 0" class="empty-tip">
            暂无导入的表
          </div>

          <el-table v-else :data="tables" stripe size="small" max-height="400">
            <el-table-column prop="tableName" label="表名" />
            <el-table-column label="操作" width="120">
              <template #default="{ row }">
                <el-button type="text" size="small" @click="viewData(row.tableName)">查看</el-button>
                <el-button type="text" size="small" @click="deleteTable(row.tableName)" style="color: #f56c6c">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>

        <el-card class="info-card" style="margin-top: 20px">
          <template #header>
            <div class="card-header">
              <span>使用说明</span>
            </div>
          </template>
          <div class="info-content">
            <p>1. 输入表名（不含前缀），如输入 <code>abc</code></p>
            <p>2. 系统将创建表 <code>I_abc</code></p>
            <p>3. Excel的A列对应 <code>COL_A</code>，B列对应 <code>COL_B</code>，以此类推</p>
            <p>4. 第一行作为列注释，可用于标识数据含义</p>
            <p>5. 自动创建 <code>id</code> 主键和 <code>create_time</code> 时间戳</p>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-dialog v-model="dataDialogVisible" :title="'查看数据: ' + currentTable" width="90%">
      <el-table :data="tableData" stripe max-height="500">
        <el-table-column type="index" width="60" />
        <el-table-column v-for="col in tableColumns" :key="col.columnName" :prop="col.columnName" :label="col.columnName" min-width="150" show-overflow-tooltip />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Upload, Download } from '@element-plus/icons-vue'
import axios from 'axios'
import { useUserStore } from '../stores/user'

const userStore = useUserStore()
const loading = ref(false)
const uploadRef = ref()
const tables = ref([])
const tableData = ref([])
const tableColumns = ref([])
const columnCount = ref(0)
const importResult = ref(null)
const dataDialogVisible = ref(false)
const currentTable = ref('')

const form = reactive({
  tableName: ''
})

const canImport = computed(() => {
  return form.tableName.trim() && columnCount.value > 0
})

const handleFileChange = (file) => {
  columnCount.value = 0
  importResult.value = null

  // 读取Excel获取列数
  const reader = new FileReader()
  reader.onload = (e) => {
    try {
      const data = new Uint8Array(e.target.result)
      const workbook = XLSX.read(data, { type: 'array' })
      const sheetName = workbook.SheetNames[0]
      const sheet = workbook.Sheets[sheetName]
      const range = XLSX.utils.decode_range(sheet['!ref'] || 'A1')
      columnCount.value = range.e.c + 1
    } catch (error) {
      console.error(error)
      ElMessage.error('读取Excel文件失败')
    }
  }
  reader.readAsArrayBuffer(file.raw)
}

const handleFileRemove = () => {
  columnCount.value = 0
  importResult.value = null
}

const handleImport = async () => {
  if (!form.tableName.trim()) {
    ElMessage.warning('请输入表名')
    return
  }

  const file = uploadRef.value?.uploadFiles?.[0]?.raw
  if (!file) {
    ElMessage.warning('请上传Excel文件')
    return
  }

  loading.value = true
  importResult.value = null

  try {
    const formData = new FormData()
    formData.append('tableName', form.tableName.trim())
    formData.append('file', file)

    const res = await axios.post('/api/import/createAndImport', formData, {
      headers: {
        'Authorization': `Bearer ${userStore.token}`,
        'Content-Type': 'multipart/form-data'
      }
    })

    if (res.data.code === 200) {
      importResult.value = res.data.data
      ElMessage.success('导入成功')
      loadTables()
    } else {
      ElMessage.error(res.data.message || '导入失败')
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('导入失败: ' + (error.response?.data?.message || error.message))
  } finally {
    loading.value = false
  }
}

const downloadTemplate = () => {
  window.open('/api/import/template?columnCount=' + Math.max(5, columnCount.value), '_blank')
}

const loadTables = async () => {
  try {
    const res = await axios.get('/api/import/tables', {
      headers: { 'Authorization': `Bearer ${userStore.token}` }
    })
    tables.value = (res.data.data || []).map(t => ({ tableName: t }))
  } catch (error) {
    console.error(error)
  }
}

const viewData = async (tableName) => {
  currentTable.value = tableName
  try {
    const [columnsRes, dataRes] = await Promise.all([
      axios.get('/api/import/columns', {
        params: { tableName },
        headers: { 'Authorization': `Bearer ${userStore.token}` }
      }),
      axios.get(`/api/import/table/${tableName}/data`, {
        headers: { 'Authorization': `Bearer ${userStore.token}` }
      })
    ])
    tableColumns.value = columnsRes.data.data || []
    tableData.value = dataRes.data.data || []
    dataDialogVisible.value = true
  } catch (error) {
    console.error(error)
    ElMessage.error('获取数据失败')
  }
}

const deleteTable = async (tableName) => {
  try {
    await ElMessageBox.confirm('确定要删除表 ' + tableName + ' 吗？此操作不可恢复！', '警告', {
      type: 'warning',
      confirmButtonText: '删除',
      cancelButtonText: '取消'
    })

    const res = await axios.delete(`/api/import/table/${tableName}`, {
      headers: { 'Authorization': `Bearer ${userStore.token}` }
    })

    if (res.data.code === 200) {
      ElMessage.success('删除成功')
      loadTables()
    } else {
      ElMessage.error(res.data.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
      ElMessage.error('删除失败')
    }
  }
}

loadTables()
</script>

<style scoped>
.page-container { padding: 20px; }
.card-header { font-weight: 600; display: flex; justify-content: space-between; align-items: center; }
.import-card { margin-bottom: 20px; }
.tip { margin-left: 10px; color: #909399; font-size: 12px; }
.column-preview { display: flex; flex-wrap: wrap; gap: 8px; }
.column-item {
  background: #ecf5ff;
  padding: 4px 10px;
  border-radius: 4px;
  font-size: 12px;
  color: #409eff;
}
.result-section { margin-top: 20px; }
.result-info p { margin: 5px 0; }
.error-list { margin-top: 10px; border-top: 1px solid #eee; padding-top: 10px; }
.error-item { font-size: 12px; color: #f56c6c; margin-top: 2px; }
.table-list-card { margin-bottom: 20px; }
.empty-tip { color: #909399; text-align: center; padding: 20px; }
.info-content p { margin: 8px 0; font-size: 14px; color: #606266; }
.info-content code { background: #f5f7fa; padding: 2px 6px; border-radius: 4px; color: #409eff; }
</style>
