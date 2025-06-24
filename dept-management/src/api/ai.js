import axios from 'axios'
import tokenManager from '@/utils/tokenManager'

// AI API基础路径
const AI_API_BASE = '/dept-api/api/ai'

// 创建axios实例
const apiClient = axios.create({
  baseURL: '', // 使用相对路径，由代理处理
  timeout: 60000, // 60秒超时
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器 - 添加认证token
apiClient.interceptors.request.use(
  config => {
    // 添加认证token
    const token = tokenManager.getToken()
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    console.log('AI API 请求:', config.url, config.data)
    return config
  },
  error => {
    console.error('AI API 请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
apiClient.interceptors.response.use(
  response => {
    console.log('AI API 响应:', response.data)
    return response
  },
  error => {
    console.error('AI API 响应错误:', error)
    // 如果是401错误，可能需要刷新token
    if (error.response?.status === 401) {
      // 清除过期token并跳转到登录页
      tokenManager.clearTokens()
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

// AI聊天API
export const aiApi = {
  /**
   * 发送聊天消息
   * @param {string} question - 用户问题
   * @param {string} sessionId - 会话ID（可选）
   * @returns {Promise} 返回AI回复
   */
  async chat(question, sessionId = null) {
    try {
      const response = await apiClient.post(`${AI_API_BASE}/chat`, {
        question: question.trim(),
        sessionId: sessionId || this.generateSessionId(),
        stream: false
      })
      
      return {
        success: true,
        data: response.data
      }
    } catch (error) {
      console.error('聊天请求失败:', error)
      return {
        success: false,
        error: error.response?.data?.error || error.message || '网络请求失败'
      }
    }
  },

  /**
   * 检查AI服务状态
   * @returns {Promise} 返回服务状态
   */
  async checkStatus() {
    try {
      const response = await apiClient.get(`${AI_API_BASE}/status`)
      return {
        success: true,
        data: response.data
      }
    } catch (error) {
      console.error('状态检查失败:', error)
      return {
        success: false,
        error: error.message || '服务状态检查失败'
      }
    }
  },

  /**
   * 获取AI配置信息
   * @returns {Promise} 返回配置信息
   */
  async getConfig() {
    try {
      const response = await apiClient.get(`${AI_API_BASE}/config`)
      return {
        success: true,
        data: response.data
      }
    } catch (error) {
      console.error('配置获取失败:', error)
      return {
        success: false,
        error: error.message || '配置获取失败'
      }
    }
  },

  /**
   * 生成会话ID
   * @returns {string} 会话ID
   */
  generateSessionId() {
    return 'session_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9)
  }
}

export default aiApi 