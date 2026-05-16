<template>
  <div class="page-container">
    <div class="toolbar">
      <div class="action-bar">
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增科目</el-button>
      </div>
    </div>

    <el-table :data="tableData" v-loading="loading" stripe style="width: 100%; margin-top: 20px">
      <el-table-column prop="subjectName" label="科目名称" width="200" />
      <el-table-column prop="teacherName" label="任课老师" width="150" />
      <el-table-column label="操作" width="150">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
          <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="科目名称" prop="subjectName">
          <el-input v-model="form.subjectName" />
        </el-form-item>
        <el-form-item label="任课老师" prop="teacherId">
          <el-select v-model="form.teacherId" style="width: 100%" placeholder="选择任课老师">
            <el-option v-for="t in teacherList" :key="t.id" :label="t.realName" :value="t.id" />
          </el-select>
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
import { subjectApi, userApi } from '../api/student'

const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const tableData = ref([])
const teacherList = ref([])
const formRef = ref()

const form = reactive({ id: null, subjectName: '', teacherId: null })

const rules = {
  subjectName: [{ required: true, message: '请输入科目名称', trigger: 'blur' }]
}

onMounted(async () => {
  const [subjectRes, teacherRes] = await Promise.all([subjectApi.list(), userApi.getTeachers()])
  tableData.value = subjectRes.data || []
  teacherList.value = teacherRes.data || []
})

const handleAdd = () => {
  dialogTitle.value = '新增科目'
  Object.assign(form, { id: null, subjectName: '', teacherId: null })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑科目'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (form.id) await subjectApi.update(form)
        else await subjectApi.add(form)
        ElMessage.success(dialogTitle.value + '成功')
        dialogVisible.value = false
        const res = await subjectApi.list()
        tableData.value = res.data || []
      } catch (error) { console.error(error) }
    }
  })
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确定要删除该科目吗？', '提示', { type: 'warning' })
  await subjectApi.delete(row.id)
  ElMessage.success('删除成功')
  const res = await subjectApi.list()
  tableData.value = res.data || []
}
</script>

<style scoped>
.page-container { padding: 20px; }
.toolbar { display: flex; justify-content: space-between; }
</style>
