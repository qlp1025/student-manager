<template>
  <div class="page-container">
    <div class="toolbar">
      <div class="action-bar">
        <el-button type="primary" :icon="Plus" @click="handleAdd">发布公告</el-button>
      </div>
    </div>

    <el-table :data="tableData" v-loading="loading" stripe style="width: 100%; margin-top: 20px">
      <el-table-column prop="title" label="标题" min-width="200" />
      <el-table-column prop="content" label="内容" min-width="300" show-overflow-tooltip />
      <el-table-column prop="publisher" label="发布人" width="100" />
      <el-table-column prop="publishTime" label="发布时间" width="160" />
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
            {{ row.status === 1 ? '已发布' : '已下架' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="250">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
          <el-button type="success" link @click="handlePublish(row)" v-if="row.status === 0">发布</el-button>
          <el-button type="warning" link @click="handleUnpublish(row)" v-else>下架</el-button>
          <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="queryParams.pageNum"
      v-model:page-size="queryParams.pageSize"
      :total="total"
      :page-sizes="[10, 20, 50]"
      layout="total, sizes, prev, pager, next"
      style="margin-top: 20px; justify-content: flex-end"
      @size-change="loadData"
      @current-change="loadData"
    />

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入公告标题" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="6" placeholder="请输入公告内容" />
        </el-form-item>
        <el-form-item label="发布人" prop="publisher">
          <el-input v-model="form.publisher" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { noticeApi } from '../api/student'
import { useUserStore } from '../stores/user'

const userStore = useUserStore()
const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const tableData = ref([])
const total = ref(0)
const formRef = ref()

const queryParams = reactive({ pageNum: 1, pageSize: 10 })

const form = reactive({ id: null, title: '', content: '', publisher: userStore.realName || userStore.username, status: 1 })

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
}

onMounted(() => loadData())

const loadData = async () => {
  loading.value = true
  try {
    const res = await noticeApi.page(queryParams)
    tableData.value = res.data.list || []
    total.value = res.data.total || 0
  } catch (error) { console.error(error) } finally { loading.value = false }
}

const handleAdd = () => {
  dialogTitle.value = '发布公告'
  Object.assign(form, { id: null, title: '', content: '', publisher: userStore.realName || userStore.username, status: 1 })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑公告'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (form.id) await noticeApi.update(form)
        else await noticeApi.add(form)
        ElMessage.success('操作成功')
        dialogVisible.value = false
        loadData()
      } catch (error) { console.error(error) }
    }
  })
}

const handlePublish = async (row) => {
  await noticeApi.updateStatus(row.id, 1)
  ElMessage.success('发布成功')
  loadData()
}

const handleUnpublish = async (row) => {
  await noticeApi.updateStatus(row.id, 0)
  ElMessage.success('下架成功')
  loadData()
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确定要删除该公告吗？', '提示', { type: 'warning' })
  await noticeApi.delete(row.id)
  ElMessage.success('删除成功')
  loadData()
}
</script>

<style scoped>
.page-container { padding: 20px; }
.toolbar { display: flex; justify-content: space-between; }
</style>
