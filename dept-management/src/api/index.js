import axios from 'axios'
import { ElMessage } from 'element-plus'

// 创建axios实例
export const api = axios.create({
  baseURL: '/dept-api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
api.interceptors.request.use(
  config => {
    // 添加token到请求头
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
api.interceptors.response.use(
  response => {
    const { data } = response
    if (data.code === 200) {
      return data.data
    } else {
      ElMessage.error(data.message || '请求失败')
      return Promise.reject(new Error(data.message || '请求失败'))
    }
  },
  error => {
    if (error.response?.status === 401) {
      // 对于验证码接口，不需要处理401错误
      if (error.config?.url?.includes('/auth/captcha')) {
        return Promise.reject(error)
      }
      
      // 清除token
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      
      // 只在不是登录页面时显示提示并跳转
      if (window.location.pathname !== '/login') {
        ElMessage.error('登录已过期，请重新登录')
        // 使用 Vue Router 进行跳转而不是 window.location
        if (window.__VUE_ROUTER__) {
          window.__VUE_ROUTER__.push('/login')
        } else {
          window.location.href = '/login'
        }
      }
    } else {
      ElMessage.error(error.message || '网络错误')
    }
    return Promise.reject(error)
  }
)

// 认证API
export const authApi = {
  // 用户登录
  login: (data) => api.post('/auth/login', data),
  
  // 用户注册
  register: (data) => api.post('/auth/register', data),
  
  // 邮箱验证
  verifyEmail: (email, code) => api.post('/auth/verify-email', null, {
    params: { email, code }
  }),
  
  // 重新发送验证邮件
  resendVerification: (email) => api.post('/auth/resend-verification', null, {
    params: { email }
  }),
  
  // 检查用户名是否可用
  checkUsername: (username) => api.get(`/auth/check-username/${username}`),
  
  // 检查邮箱是否可用
  checkEmail: (email) => api.get(`/auth/check-email/${email}`),
  
  // 激活用户
  activateUser: (userId) => api.put(`/auth/activate/${userId}`),
  
  // 获取验证码
  getCaptcha: (sessionId) => api.get('/auth/captcha', { params: { sessionId } }),
  
  // 忘记密码
  forgotPassword: (data) => api.post('/auth/forgot-password', data),
  
  // 重置密码
  resetPassword: (data) => api.post('/auth/reset-password', data)
}

// 部门API
export const departmentApi = {
  // 获取所有部门
  getAll: () => api.get('/departments'),
  
  // 根据ID获取部门
  getById: (id) => api.get(`/departments/${id}`),
  
  // 创建部门
  create: (data) => api.post('/departments', data),
  
  // 更新部门
  update: (id, data) => api.put(`/departments/${id}`, data),
  
  // 删除部门
  delete: (id) => api.delete(`/departments/${id}`),
  
  // 获取子部门
  getSubDepartments: (parentId) => api.get(`/departments/sub/${parentId}`),
  
  // 分配经理
  assignManager: (departmentId, managerId) => 
    api.put(`/departments/${departmentId}/manager/${managerId}`)
}

// 员工API
export const employeeApi = {
  // 获取所有员工
  getAll: () => api.get('/employees'),
  
  // 根据ID获取员工
  getById: (id) => api.get(`/employees/${id}`),
  
  // 创建员工
  create: (data) => api.post('/employees', data),
  
  // 更新员工
  update: (id, data) => api.put(`/employees/${id}`, data),
  
  // 删除员工
  delete: (id) => api.delete(`/employees/${id}`),
  
  // 根据部门获取员工
  getByDepartment: (departmentId) => api.get(`/employees/department/${departmentId}`),
  
  // 员工转岗
  transfer: (employeeId, departmentId) => 
    api.put(`/employees/${employeeId}/transfer/${departmentId}`)
}

// 项目API
export const projectApi = {
  // 获取所有项目
  getAll: () => api.get('/projects'),
  
  // 根据ID获取项目
  getById: (id) => api.get(`/projects/${id}`),
  
  // 创建项目
  create: (data) => api.post('/projects', data),
  
  // 更新项目
  update: (id, data) => api.put(`/projects/${id}`, data),
  
  // 删除项目
  delete: (id) => api.delete(`/projects/${id}`),
  
  // 获取项目成员
  getMembers: (projectId) => api.get(`/projects/${projectId}/members`),
  
  // 添加项目成员
  addMember: (projectId, data) => api.post(`/projects/${projectId}/members`, data),
  
  // 移除项目成员
  removeMember: (projectId, employeeId) => 
    api.delete(`/projects/${projectId}/members/${employeeId}`)
}

// 用户资料API
export const profileApi = {
  // 获取用户资料
  getProfile: (userId) => api.get(`/profile/${userId}`),
  
  // 创建用户资料
  createProfile: (data) => api.post('/profile', data),
  
  // 更新用户资料
  updateProfile: (data) => api.put('/profile', data),
  
  // 修改密码
  changePassword: (userId, data) => api.post(`/profile/${userId}/change-password`, data),
  
  // 上传头像
  uploadAvatar: (userId, file) => {
    const formData = new FormData()
    formData.append('file', file)
    return api.post(`/profile/${userId}/upload-avatar`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },
  
  // 检查昵称是否可用
  checkNickname: (nickname, userId) => api.get('/profile/check-nickname', {
    params: { nickname, userId }
  }),
  
  // 初始化用户资料
  initializeProfile: (userId) => api.post(`/profile/${userId}/initialize`)
}

// 用户设置API
export const settingsApi = {
  // 获取用户设置
  getSettings: (userId) => api.get(`/settings/${userId}`),
  
  // 更新用户设置
  updateSettings: (data) => api.put('/settings', data),
  
  // 初始化用户设置
  initializeSettings: (userId) => api.post(`/settings/${userId}/initialize`),
  
  // 重置设置为默认值
  resetToDefault: (userId) => api.post(`/settings/${userId}/reset`)
}

export default api 