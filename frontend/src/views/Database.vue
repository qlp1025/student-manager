<template>
  <div class="database-container">
    <el-card class="table-selector">
      <template #header>
        <div class="card-header">
          <span>数据库查询</span>
        </div>
      </template>
      <el-select v-model="selectedTable" placeholder="请选择数据表" @change="onTableChange" filterable style="width: 300px">
        <el-option v-for="table in tables" :key="table" :label="table" :value="table" />
      </el-select>
    </el-card>

    <el-card class="sql-editor">
      <template #header>
        <div class="card-header">
          <span>自定义SQL查询</span>
          <el-button type="primary" size="small" @click="executeSql" :loading="sqlLoading">
            <el-icon><VideoPlay /></el-icon> 执行
          </el-button>
        </div>
      </template>
      <el-input
        v-model="customSql"
        type="textarea"
        :rows="4"
        placeholder="请输入SQL查询语句（仅支持SELECT）"
        style="font-family: monospace"
      />
      <div class="sql-tip">提示：仅支持SELECT查询语句，禁止执行DROP、DELETE、UPDATE、INSERT等危险操作</div>
    </el-card>

    <el-card v-if="sqlResult.length > 0" class="sql-result">
      <template #header>
        <div class="card-header">
          <span>查询结果 - {{ sqlResult.length }} 条记录</span>
          <el-button type="primary" size="small" text @click="sqlResult = []">
            <el-icon><Close /></el-icon> 关闭
          </el-button>
        </div>
      </template>
      <el-table :data="sqlResult" border stripe max-height="400" show-overflow-tooltip>
        <el-table-column v-for="(value, key) in sqlResult[0]" :key="key"
          :prop="String(key)"
          :label="String(key)"
          min-width="150"
          show-overflow-tooltip />
      </el-table>
    </el-card>

    <el-card v-if="selectedTable" class="data-viewer">
      <template #header>
        <div class="card-header">
          <span>{{ selectedTable }} - {{ total }} 条记录</span>
          <el-button type="primary" size="small" @click="refreshData">
            <el-icon><Refresh /></el-icon> 刷新
          </el-button>
        </div>
      </template>

      <el-table :data="tableData" border stripe max-height="500" v-loading="loading">
        <el-table-column v-for="col in columns" :key="col.COLUMN_NAME"
          :prop="col.COLUMN_NAME"
          :label="col.COLUMN_NAME"
          :width="getColumnWidth(col)"
          show-overflow-tooltip />
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[50, 100, 200, 500]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange" />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh, VideoPlay, Close } from '@element-plus/icons-vue'
import request from '../api'

const tables = ref([])
const selectedTable = ref('')
const columns = ref([])
const tableData = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(100)
const loading = ref(false)

const customSql = ref('')
const sqlResult = ref([])
const sqlLoading = ref(false)

const loadTables = async () => {
  try {
    const res = await request.get('/api/database/tables')
    if (res.code === 200) {
      tables.value = res.data
    }
  } catch (error) {
    ElMessage.error('加载表列表失败')
  }
}

const loadTableData = async () => {
  if (!selectedTable.value) return
  loading.value = true
  try {
    const offset = (currentPage.value - 1) * pageSize.value
    const res = await request.get(`/api/database/table/${selectedTable.value}`, {
      params: { limit: pageSize.value, offset }
    })
    if (res.code === 200) {
      columns.value = res.data.columns
      tableData.value = res.data.rows
      total.value = res.data.total
    }
  } catch (error) {
    ElMessage.error('加载表数据失败')
  } finally {
    loading.value = false
  }
}

const executeSql = async () => {
  if (!customSql.value.trim()) {
    ElMessage.warning('请输入SQL语句')
    return
  }
  sqlLoading.value = true
  try {
    const res = await request.post('/api/database/query', { sql: customSql.value })
    if (res.code === 200) {
      sqlResult.value = res.data
      ElMessage.success(`查询成功，返回 ${res.data.length} 条记录`)
    } else {
      ElMessage.error(res.message || 'SQL执行失败')
    }
  } catch (error) {
    ElMessage.error('SQL执行失败')
  } finally {
    sqlLoading.value = false
  }
}

const onTableChange = () => {
  currentPage.value = 1
  loadTableData()
}

const refreshData = () => {
  loadTableData()
}

const handleSizeChange = () => {
  currentPage.value = 1
  loadTableData()
}

const handlePageChange = () => {
  loadTableData()
}

const getColumnWidth = (col) => {
  const typeMap = {
    'varchar': 150,
    'int': 100,
    'bigint': 120,
    'datetime': 180,
    'timestamp': 180,
    'text': 300,
    'decimal': 120
  }
  return typeMap[col.DATA_TYPE] || 150
}

onMounted(() => {
  loadTables()
})
</script>

<style scoped>
.database-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.table-selector {
  flex-shrink: 0;
}

.sql-editor {
  flex-shrink: 0;
}

.sql-tip {
  margin-top: 10px;
  font-size: 12px;
  color: #909399;
}

.sql-result {
  flex-shrink: 0;
}

.data-viewer {
  flex: 1;
  min-height: 0;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
