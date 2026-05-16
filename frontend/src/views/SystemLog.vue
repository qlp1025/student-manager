<template>
  <div class="page-container">
    <el-table :data="tableData" v-loading="loading" stripe style="width: 100%">
      <el-table-column prop="username" label="操作人" width="120" />
      <el-table-column prop="operation" label="操作类型" width="120" />
      <el-table-column prop="method" label="请求方法" min-width="200" show-overflow-tooltip />
      <el-table-column prop="params" label="请求参数" min-width="200" show-overflow-tooltip />
      <el-table-column prop="ip" label="IP地址" width="140" />
      <el-table-column prop="createTime" label="操作时间" width="160" />
    </el-table>

    <el-pagination
      v-model:current-page="queryParams.pageNum"
      v-model:page-size="queryParams.pageSize"
      :total="total"
      :page-sizes="[10, 20, 50, 100]"
      layout="total, sizes, prev, pager, next, jumper"
      style="margin-top: 20px; justify-content: flex-end"
      @size-change="loadData"
      @current-change="loadData"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { logApi } from '../api/student'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)

const queryParams = reactive({ pageNum: 1, pageSize: 10 })

onMounted(() => loadData())

const loadData = async () => {
  loading.value = true
  try {
    const res = await logApi.page(queryParams)
    tableData.value = res.data.list || []
    total.value = res.data.total || 0
  } catch (error) { console.error(error) } finally { loading.value = false }
}
</script>

<style scoped>
.page-container { padding: 20px; }
</style>
