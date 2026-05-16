<template>
  <div class="page-container">
    <div class="toolbar">
      <div class="action-bar">
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增班级</el-button>
      </div>
    </div>

    <el-table :data="tableData" v-loading="loading" stripe style="width: 100%; margin-top: 20px">
      <el-table-column prop="className" label="班级名称" width="150" />
      <el-table-column prop="grade" label="年级" width="120" />
      <el-table-column prop="teacherName" label="班主任" width="120" />
      <el-table-column prop="studentCount" label="学生人数" width="100" />
      <el-table-column label="操作" width="150">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
          <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="班级名称" prop="className">
          <el-input v-model="form.className" placeholder="如：高一(1)班" />
        </el-form-item>
        <el-form-item label="年级" prop="grade">
          <el-select v-model="form.grade" style="width: 100%">
            <el-option label="高一" value="高一" />
            <el-option label="高二" value="高二" />
            <el-option label="高三" value="高三" />
          </el-select>
        </el-form-item>
        <el-form-item label="班主任" prop="teacherId">
          <el-select v-model="form.teacherId" style="width: 100%" placeholder="选择班主任">
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
import { classApi, userApi } from '../api/student'

const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const tableData = ref([])
const teacherList = ref([])
const formRef = ref()

const form = reactive({ id: null, className: '', grade: '', teacherId: null })

const rules = {
  className: [{ required: true, message: '请输入班级名称', trigger: 'blur' }],
  grade: [{ required: true, message: '请选择年级', trigger: 'change' }]
}

onMounted(async () => {
  const [classRes, teacherRes] = await Promise.all([classApi.list(), userApi.getTeachers()])
  tableData.value = classRes.data || []
  teacherList.value = teacherRes.data || []
})

const handleAdd = () => {
  dialogTitle.value = '新增班级'
  Object.assign(form, { id: null, className: '', grade: '', teacherId: null })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑班级'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (form.id) await classApi.update(form)
        else await classApi.add(form)
        ElMessage.success(dialogTitle.value + '成功')
        dialogVisible.value = false
        const res = await classApi.list()
        tableData.value = res.data || []
      } catch (error) { console.error(error) }
    }
  })
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确定要删除该班级吗？', '提示', { type: 'warning' })
  await classApi.delete(row.id)
  ElMessage.success('删除成功')
  const res = await classApi.list()
  tableData.value = res.data || []
}
</script>

<style scoped>
.page-container { padding: 20px; }
.toolbar { display: flex; justify-content: space-between; }
</style>
