/**
 * URL工具函数
 */

// 获取API基础URL
export const getApiBaseUrl = () => {
  // 开发环境
  if (import.meta.env.DEV) {
    return '/dept-api'
  }
  
  // 生产环境 - 动态获取
  const { protocol, hostname, port } = window.location
  const contextPath = '/dept-api'
  
  // 如果是标准端口，不显示端口号
  const portStr = (
    (protocol === 'https:' && port === '443') ||
    (protocol === 'http:' && port === '80') ||
    !port
  ) ? '' : `:${port}`
  
  return `${protocol}//${hostname}${portStr}${contextPath}`
}

// 获取静态资源完整URL
export const getStaticUrl = (relativePath) => {
  if (!relativePath) return ''
  
  // 如果已经是完整URL，直接返回
  if (relativePath.startsWith('http://') || relativePath.startsWith('https://')) {
    return relativePath
  }
  
  // 开发环境通过代理访问
  if (import.meta.env.DEV) {
    return relativePath.startsWith('/') ? relativePath : `/${relativePath}`
  }
  
  // 生产环境拼接完整URL
  const baseUrl = getApiBaseUrl()
  const path = relativePath.startsWith('/') ? relativePath : `/${relativePath}`
  return `${baseUrl}${path}`
}

// 获取头像URL
export const getAvatarUrl = (avatar) => {
  return getStaticUrl(avatar)
} 