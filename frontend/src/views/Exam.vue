<template>
  <div class="page-container">
    <div class="toolbar">
      <div class="search-bar">
        <el-button type="primary" :icon="Plus" @click="handleAdd">添加考试</el-button>
      </div>
    </div>

    <el-table :data="tableData" v-loading="loading" stripe style="width: 100%; margin-top: 20px" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="examName" label="考试名称" />
      <el-table-column prop="remark" label="备注" show-overflow-tooltip />
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="150">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
          <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="考试名称" prop="examName">
          <el-input v-model="form.examName" placeholder="请输入考试名称" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
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
import { examApi } from '../api/student'

const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const tableData = ref([])
const formRef = ref()
const form = reactive({
  id: null,
  examName: '',
  remark: ''
})

const rules = {
  examName: [{ required: true, message: '请输入考试名称', trigger: 'blur' }]
}

onMounted(() => {
  loadData()
})

const loadData = async () => {
  loading.value = true
  try {
    const res = await examApi.list()
    tableData.value = res.data || []
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  dialogTitle.value = '添加考试'
  Object.assign(form, { id: null, examName: '', remark: '' })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑考试'
  Object.assign(form, { id: row.id, examName: row.examName, remark: row.remark })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (form.id) {
          await examApi.update(form)
          ElMessage.success('更新成功')
        } else {
          await examApi.add(form)
          ElMessage.success('添加成功')
        }
        dialogVisible.value = false
        loadData()
      } catch (error) {
        console.error(error)
        ElMessage.error('操作失败')
      }
    }
  })
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除考试 "' + row.examName + '" 吗？', '警告', {
      type: 'warning'
    })
    await examApi.delete(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
      ElMessage.error('删除失败')
    }
  }
}
</script>

<style scoped>
.page-container { padding: 20px; }
.toolbar { display: flex; justify-content: space-between; flex-wrap: wrap; gap: 15px; }
.search-bar { display: flex; align-items: center; flex-wrap: wrap; }
</style>
