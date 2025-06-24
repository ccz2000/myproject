import request from '@/utils/request'

// 薪资相关接口
export function getSalaryList(params) {
  return request({
    url: '/salaries/month',
    method: 'get',
    params
  })
}

export function getSalaryByEmployee(employeeId, params) {
  return request({
    url: `/salaries/employee/${employeeId}`,
    method: 'get',
    params
  })
}

export function getSalaryByDepartment(departmentId, params) {
  return request({
    url: `/salaries/department/${departmentId}`,
    method: 'get',
    params
  })
}

export function createSalary(data) {
  return request({
    url: '/salaries',
    method: 'post',
    data
  })
}

export function updateSalary(id, data) {
  return request({
    url: `/salaries/${id}`,
    method: 'put',
    data
  })
}

export function deleteSalary(id) {
  return request({
    url: `/salaries/${id}`,
    method: 'delete'
  })
}

export function paySalary(id) {
  return request({
    url: `/salaries/${id}/pay`,
    method: 'post'
  })
}

export function batchPaySalaries(ids) {
  return request({
    url: '/salaries/batch-pay',
    method: 'post',
    data: ids
  })
}

// 薪资调整相关接口
export function getAdjustmentList(employeeId) {
  return request({
    url: `/salary-adjustments/employee/${employeeId}`,
    method: 'get'
  })
}

export function getAdjustmentsByDepartment(departmentId) {
  return request({
    url: `/salary-adjustments/department/${departmentId}`,
    method: 'get'
  })
}

export function getAdjustmentsByDateRange(params) {
  return request({
    url: '/salary-adjustments/date-range',
    method: 'get',
    params
  })
}

export function createAdjustment(data) {
  return request({
    url: '/salary-adjustments',
    method: 'post',
    data
  })
}

export function updateAdjustment(id, data) {
  return request({
    url: `/salary-adjustments/${id}`,
    method: 'put',
    data
  })
}

export function deleteAdjustment(id) {
  return request({
    url: `/salary-adjustments/${id}`,
    method: 'delete'
  })
} 