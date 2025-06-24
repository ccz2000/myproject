import { api } from '@/api'

// 薪资相关接口
export function getSalaryList(params) {
  return api.get('/salaries/month', { params })
}

export function getSalaryByEmployee(employeeId, params) {
  return api.get(`/salaries/employee/${employeeId}`, { params })
}

export function getSalaryByDepartment(departmentId, params) {
  return api.get(`/salaries/department/${departmentId}`, { params })
}

export function createSalary(data) {
  return api.post('/salaries', data)
}

export function updateSalary(id, data) {
  return api.put(`/salaries/${id}`, data)
}

export function deleteSalary(id) {
  return api.delete(`/salaries/${id}`)
}

export function paySalary(id) {
  return api.post(`/salaries/${id}/pay`)
}

export function batchPaySalaries(ids) {
  return api.post('/salaries/batch-pay', { ids })
}

// 薪资调整相关接口
export function getAdjustmentList(employeeId) {
  return api.get(`/salary-adjustments/employee/${employeeId}`)
}

export function getAdjustmentsByDepartment(departmentId) {
  return api.get(`/salary-adjustments/department/${departmentId}`)
}

export function getAdjustmentsByDateRange(params) {
  return api.get('/salary-adjustments/date-range', { params })
}

export function createAdjustment(data) {
  return api.post('/salary-adjustments', data)
}

export function updateAdjustment(id, data) {
  return api.put(`/salary-adjustments/${id}`, data)
}

export function deleteAdjustment(id) {
  return api.delete(`/salary-adjustments/${id}`)
}

// 薪资管理API
const salaryApi = {
  // 获取薪资列表
  getSalaryList: (params) => api.get('/salaries/month', { params }),
  
  // 根据员工获取薪资
  getSalaryByEmployee: (employeeId, params) => api.get(`/salaries/employee/${employeeId}`, { params }),
  
  // 根据部门获取薪资
  getSalaryByDepartment: (departmentId, params) => api.get(`/salaries/department/${departmentId}`, { params }),
  
  // 创建薪资记录
  createSalary: (data) => api.post('/salaries', data),
  
  // 更新薪资记录
  updateSalary: (id, data) => api.put(`/salaries/${id}`, data),
  
  // 删除薪资记录
  deleteSalary: (id) => api.delete(`/salaries/${id}`),
  
  // 发放薪资
  paySalary: (id) => api.post(`/salaries/${id}/pay`),
  
  // 批量发放薪资
  batchPaySalaries: (ids) => api.post('/salaries/batch-pay', { ids }),
  
  // 获取薪资调整记录
  getAdjustmentsByDateRange: (params) => api.get('/salary-adjustments/date-range', { params }),
  
  // 获取部门薪资调整记录
  getAdjustmentsByDepartment: (departmentId) => api.get(`/salary-adjustments/department/${departmentId}`),
  
  // 创建薪资调整
  createAdjustment: (data) => api.post('/salary-adjustments', data),
  
  // 更新薪资调整
  updateAdjustment: (id, data) => api.put(`/salary-adjustments/${id}`, data),
  
  // 删除薪资调整
  deleteAdjustment: (id) => api.delete(`/salary-adjustments/${id}`),
  
  // 获取所有薪资调整记录
  getAllAdjustments: () => api.get('/salary-adjustments')
}

export default salaryApi 