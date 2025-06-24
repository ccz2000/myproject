import axios from 'axios'
import { ElMessage } from 'element-plus'

/**
 * Token管理工具类
 */
class TokenManager {
  constructor() {
    this.isRefreshing = false
    this.failedQueue = []
    this.setupInterceptors()
  }

  /**
   * 获取token
   */
  getToken() {
    return localStorage.getItem('token')
  }

  /**
   * 获取刷新token
   */
  getRefreshToken() {
    return localStorage.getItem('refreshToken')
  }

  /**
   * 设置token信息
   */
  setTokens(tokenData) {
    localStorage.setItem('token', tokenData.token)
    if (tokenData.refreshToken) {
      localStorage.setItem('refreshToken', tokenData.refreshToken)
    }
    if (tokenData.expiresIn) {
      // 计算过期时间戳
      const expiresAt = Date.now() + (tokenData.expiresIn * 1000)
      localStorage.setItem('tokenExpiresAt', expiresAt.toString())
    }
    if (tokenData.userInfo) {
      localStorage.setItem('userInfo', JSON.stringify(tokenData.userInfo))
    }
  }

  /**
   * 清除所有token信息
   */
  clearTokens() {
    localStorage.removeItem('token')
    localStorage.removeItem('refreshToken')
    localStorage.removeItem('tokenExpiresAt')
    localStorage.removeItem('userInfo')
  }

  /**
   * 检查token是否即将过期（3分钟内）
   */
  isTokenNearExpiry() {
    const expiresAt = localStorage.getItem('tokenExpiresAt')
    if (!expiresAt) return false

    const now = Date.now()
    const expiry = parseInt(expiresAt)
    
    // 如果token在3分钟内过期，认为即将过期（适应30分钟token）
    return (expiry - now) < 3 * 60 * 1000
  }

  /**
   * 检查token是否已过期
   */
  isTokenExpired() {
    const expiresAt = localStorage.getItem('tokenExpiresAt')
    if (!expiresAt) return false

    return Date.now() >= parseInt(expiresAt)
  }

  /**
   * 刷新token
   */
  async refreshToken() {
    const refreshToken = this.getRefreshToken()
    if (!refreshToken) {
      throw new Error('没有刷新token')
    }

    try {
      const response = await axios.post('/dept-api/auth/refresh-token', null, {
        params: { refreshToken }
      })

      if (response.data.code === 200) {
        this.setTokens(response.data.data)
        return response.data.data.token
      } else {
        throw new Error(response.data.message || 'Token刷新失败')
      }
    } catch (error) {
      console.error('Token刷新失败:', error)
      this.handleLogout()
      throw error
    }
  }

  /**
   * 处理登出
   */
  handleLogout() {
    this.clearTokens()
    window.location.href = '/login'
    ElMessage.error('登录已过期，请重新登录')
  }

  /**
   * 处理失败的请求队列
   */
  processQueue(error, token = null) {
    this.failedQueue.forEach(({ resolve, reject }) => {
      if (error) {
        reject(error)
      } else {
        resolve(token)
      }
    })

    this.failedQueue = []
  }

  /**
   * 设置axios拦截器
   */
  setupInterceptors() {
    // 请求拦截器
    axios.interceptors.request.use(
      async (config) => {
        // 跳过刷新token的请求
        if (config.url?.includes('/auth/refresh-token')) {
          return config
        }

        let token = this.getToken()

                 // 检查token是否即将过期，如果是则自动刷新（30分钟token，3分钟内刷新）
         if (token && this.isTokenNearExpiry() && !this.isRefreshing) {
           try {
             this.isRefreshing = true
             console.log('Token即将过期（剩余<3分钟），自动刷新...')
             token = await this.refreshToken()
             this.processQueue(null, token)
           } catch (error) {
             this.processQueue(error, null)
             return Promise.reject(error)
           } finally {
             this.isRefreshing = false
           }
         }

        // 如果token已过期，直接跳转登录
        if (token && this.isTokenExpired()) {
          this.handleLogout()
          return Promise.reject(new Error('Token已过期'))
        }

        // 如果正在刷新token，将请求加入队列
        if (this.isRefreshing) {
          return new Promise((resolve, reject) => {
            this.failedQueue.push({ resolve, reject })
          }).then(token => {
            config.headers.Authorization = `Bearer ${token}`
            return config
          }).catch(err => {
            return Promise.reject(err)
          })
        }

        // 添加token到请求头
        if (token) {
          config.headers.Authorization = `Bearer ${token}`
        }

        return config
      },
      (error) => {
        return Promise.reject(error)
      }
    )

    // 响应拦截器
    axios.interceptors.response.use(
      (response) => {
        return response
      },
      async (error) => {
        const originalRequest = error.config

        // 处理403权限不足错误
        if (error.response?.status === 403) {
          ElMessage.error('您没有权限，请联系管理员')
          return Promise.reject(error)
        }

        // 处理401未授权错误
        if (error.response?.status === 401 && !originalRequest._retry) {
          if (this.isRefreshing) {
            // 如果正在刷新，将请求加入队列
            return new Promise((resolve, reject) => {
              this.failedQueue.push({ resolve, reject })
            }).then(token => {
              originalRequest.headers.Authorization = `Bearer ${token}`
              return axios(originalRequest)
            }).catch(err => {
              return Promise.reject(err)
            })
          }

          originalRequest._retry = true
          this.isRefreshing = true

          try {
            const newToken = await this.refreshToken()
            this.processQueue(null, newToken)
            originalRequest.headers.Authorization = `Bearer ${newToken}`
            return axios(originalRequest)
          } catch (refreshError) {
            this.processQueue(refreshError, null)
            this.handleLogout()
            return Promise.reject(refreshError)
          } finally {
            this.isRefreshing = false
          }
        }

        return Promise.reject(error)
      }
    )
  }

  /**
   * 主动检查并刷新token（用于页面初始化）
   */
  async checkAndRefreshToken() {
    const token = this.getToken()
    if (!token) return false

    if (this.isTokenExpired()) {
      this.handleLogout()
      return false
    }

    if (this.isTokenNearExpiry()) {
      try {
        await this.refreshToken()
        console.log('Token已自动刷新')
        return true
      } catch (error) {
        console.error('Token刷新失败:', error)
        return false
      }
    }

    return true
  }

  /**
   * 获取token剩余时间（分钟）
   */
  getTokenRemainingMinutes() {
    const expiresAt = localStorage.getItem('tokenExpiresAt')
    if (!expiresAt) return 0

    const remaining = parseInt(expiresAt) - Date.now()
    return Math.max(0, Math.floor(remaining / (60 * 1000)))
  }
}

// 创建单例实例
const tokenManager = new TokenManager()

export default tokenManager 