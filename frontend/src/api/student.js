import request from './index'

export const authApi = {
  login: (data) => request.post('/auth/login', data),
  logout: () => request.post('/auth/logout')
}

export const studentApi = {
  list: () => request.get('/student/list'),
  page: (params) => request.get('/student/page', { params }),
  getById: (id) => request.get(`/student/${id}`),
  getByClassId: (classId) => request.get(`/student/class/${classId}`),
  add: (data) => request.post('/student', data),
  update: (data) => request.put('/student', data),
  updateStatus: (id, status) => request.put(`/student/status/${id}`, null, { params: { status } }),
  delete: (id) => request.delete(`/student/${id}`),
  batchDelete: (ids) => request.delete('/student/batch', { data: ids }),
  export: () => request.get('/student/export', { responseType: 'blob' }),
  count: () => request.get('/student/count')
}

export const classApi = {
  list: () => request.get('/class/list'),
  page: (params) => request.get('/class/page', { params }),
  getById: (id) => request.get(`/class/${id}`),
  getByGrade: (grade) => request.get(`/class/grade/${grade}`),
  add: (data) => request.post('/class', data),
  update: (data) => request.put('/class', data),
  delete: (id) => request.delete(`/class/${id}`)
}

export const subjectApi = {
  list: () => request.get('/subject/list'),
  page: (params) => request.get('/subject/page', { params }),
  getById: (id) => request.get(`/subject/${id}`),
  add: (data) => request.post('/subject', data),
  update: (data) => request.put('/subject', data),
  delete: (id) => request.delete(`/subject/${id}`)
}

export const scoreApi = {
  list: () => request.get('/score/list'),
  page: (params) => request.get('/score/page', { params }),
  getById: (id) => request.get(`/score/${id}`),
  getByStudentId: (studentId) => request.get(`/score/student/${studentId}`),
  getTotalScore: (studentId) => request.get(`/score/student/${studentId}/total`),
  getRanking: (params) => request.get('/score/ranking', { params }),
  add: (data) => request.post('/score', data),
  batchAdd: (data) => request.post('/score/batch', data),
  update: (data) => request.put('/score', data),
  updateStatus: (id, status) => request.put(`/score/status/${id}`, null, { params: { status } }),
  delete: (id) => request.delete(`/score/${id}`),
  export: (params) => request.get('/score/export', { params, responseType: 'blob' })
}

export const noticeApi = {
  list: () => request.get('/notice/list'),
  published: () => request.get('/notice/published'),
  page: (params) => request.get('/notice/page', { params }),
  getById: (id) => request.get(`/notice/${id}`),
  add: (data) => request.post('/notice', data),
  update: (data) => request.put('/notice', data),
  updateStatus: (id, status) => request.put(`/notice/status/${id}`, null, { params: { status } }),
  delete: (id) => request.delete(`/notice/${id}`)
}

export const userApi = {
  list: () => request.get('/user/list'),
  page: (params) => request.get('/user/page', { params }),
  getById: (id) => request.get(`/user/${id}`),
  add: (data) => request.post('/user', data),
  update: (data) => request.put('/user', data),
  updatePassword: (id, password) => request.put('/user/password', null, { params: { id, password } }),
  resetPassword: (id) => request.put(`/user/resetPassword/${id}`),
  delete: (id) => request.delete(`/user/${id}`),
  batchDelete: (ids) => request.delete('/user/batch', { data: ids }),
  getTeachers: () => request.get('/user/teachers')
}

export const roleApi = {
  list: () => request.get('/role/list'),
  getById: (id) => request.get(`/role/${id}`)
}

export const logApi = {
  list: () => request.get('/log/list'),
  page: (params) => request.get('/log/page', { params }),
  getByUsername: (username) => request.get(`/log/user/${username}`),
  delete: (id) => request.delete(`/log/${id}`)
}

export const importApi = {
  listTables: () => request.get('/import/tables'),
  getTableColumns: (tableName) => request.get('/import/columns', { params: { tableName } }),
  getTableData: (tableName) => request.get(`/import/table/${tableName}/data`),
  createAndImport: (formData) => request.post('/import/createAndImport', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  }),
  deleteTable: (tableName) => request.delete(`/import/table/${tableName}`),
  getTemplate: (columnCount) => request.get('/import/template', { params: { columnCount }, responseType: 'blob' })
}
