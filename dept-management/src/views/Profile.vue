<template>
  <div class="profile-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h2 class="page-title">
          <el-icon class="title-icon"><User /></el-icon>
          个人中心
        </h2>
        <p class="page-subtitle">管理您的个人信息和账户设置</p>
      </div>
      <div class="header-actions">
        <el-button type="primary" @click="$router.push('/profile/edit')" class="edit-btn">
          <el-icon><Edit /></el-icon>
          编辑资料
        </el-button>
      </div>
    </div>

    <el-row :gutter="24" class="profile-content">
      <!-- 左侧个人信息卡片 -->
      <el-col :xl="8" :lg="10" :md="24" :sm="24" :xs="24">
        <el-card class="profile-card" shadow="hover">
          <div class="profile-header">
            <div class="avatar-section">
              <div class="avatar-wrapper">
                <el-avatar :size="120" :src="processAvatarUrl(profileData.avatar)" class="profile-avatar">
                  <el-icon size="40"><User /></el-icon>
                </el-avatar>
                <div class="avatar-overlay" @click="handleAvatarUpload">
                  <el-icon><Camera /></el-icon>
                  <span>更换头像</span>
                </div>
                <div class="avatar-status">
                  <el-tag type="success" size="small" round>
                    <el-icon><CircleCheck /></el-icon>
                    已认证
                  </el-tag>
                </div>
              </div>
              <input 
                ref="fileInput" 
                type="file" 
                accept="image/*" 
                style="display: none" 
                @change="onFileChange"
              />
              <div class="upload-tips">
                <el-icon><InfoFilled /></el-icon>
                支持 JPG/PNG/GIF，文件大小不超过 5MB
              </div>
            </div>
            
            <div class="profile-info">
              <div class="name-section">
                <h3 class="profile-name">
                  {{ profileData.realName || profileData.username }}
                  <el-icon class="verified-icon" v-if="profileData.verified"><CircleCheckFilled /></el-icon>
                </h3>
                <el-tag v-if="profileData.nickname" size="small" type="primary" class="nickname-tag">
                  @{{ profileData.nickname }}
                </el-tag>
              </div>
              
              <div class="contact-info">
                <div class="contact-item">
                  <el-icon><Message /></el-icon>
                  <span>{{ profileData.email }}</span>
                </div>
                <div class="contact-item" v-if="profileData.phone">
                  <el-icon><Phone /></el-icon>
                  <span>{{ profileData.phone }}</span>
                </div>
              </div>
              
              <p class="bio" v-if="profileData.bio">{{ profileData.bio }}</p>
              
              <div class="profile-stats">
                <div class="stat-item">
                  <span class="stat-number">{{ profileData.loginCount || 0 }}</span>
                  <span class="stat-label">登录次数</span>
                </div>
                <div class="stat-item">
                  <span class="stat-number">{{ formatDate(profileData.lastLoginTime) }}</span>
                  <span class="stat-label">最后登录</span>
                </div>
              </div>
            </div>
          </div>
          
          <el-divider />
          
          <div class="quick-actions">
            <el-button type="primary" @click="$router.push('/profile/edit')" class="action-btn" block>
              <el-icon><Edit /></el-icon>
              编辑资料
            </el-button>
            <el-button @click="$router.push('/profile/change-password')" class="action-btn" block>
              <el-icon><Key /></el-icon>
              修改密码
            </el-button>
            <el-button @click="$router.push('/profile/settings')" class="action-btn" block>
              <el-icon><Setting /></el-icon>
              系统设置
            </el-button>
            <el-button @click="handleLogout" class="action-btn logout-btn" block>
              <el-icon><SwitchButton /></el-icon>
              退出登录
            </el-button>
          </div>
        </el-card>
      </el-col>
      
      <!-- 右侧详细信息 -->
      <el-col :xl="16" :lg="14" :md="24" :sm="24" :xs="24">
        <div class="details-section">
          <!-- 基础信息卡片 -->
          <el-card class="info-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <div class="header-title">
                  <el-icon><UserFilled /></el-icon>
                  <span>个人信息</span>
                </div>
                <el-button type="primary" size="small" @click="$router.push('/profile/edit')">
                  <el-icon><Edit /></el-icon>
                  编辑
                </el-button>
              </div>
            </template>
            
            <div class="info-grid">
              <div class="info-row">
                <div class="info-item">
                  <div class="item-label">
                    <el-icon><User /></el-icon>
                    <span>真实姓名</span>
                  </div>
                  <span class="item-value">{{ profileData.realName || '--' }}</span>
                </div>
                <div class="info-item">
                  <div class="item-label">
                    <el-icon><Avatar /></el-icon>
                    <span>昵称</span>
                  </div>
                  <span class="item-value">{{ profileData.nickname || '--' }}</span>
                </div>
                <div class="info-item">
                  <div class="item-label">
                    <el-icon><Phone /></el-icon>
                    <span>手机号</span>
                  </div>
                  <span class="item-value">{{ profileData.phone || '--' }}</span>
                </div>
                <div class="info-item">
                  <div class="item-label">
                    <el-icon><Male /></el-icon>
                    <span>性别</span>
                  </div>
                  <el-tag :type="getGenderTagType(profileData.gender)" size="small" class="item-value">
                    {{ getGenderText(profileData.gender) }}
                  </el-tag>
                </div>
                <div class="info-item">
                  <div class="item-label">
                    <el-icon><Calendar /></el-icon>
                    <span>生日</span>
                  </div>
                  <span class="item-value">{{ profileData.birthday || '--' }}</span>
                </div>
                <div class="info-item">
                  <div class="item-label">
                    <el-icon><Location /></el-icon>
                    <span>地址</span>
                  </div>
                  <span class="item-value">{{ profileData.address || '--' }}</span>
                </div>
              </div>
            </div>
          </el-card>
          
          <!-- 工作信息卡片 -->
          <el-card class="info-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <div class="header-title">
                  <el-icon><Briefcase /></el-icon>
                  <span>工作信息</span>
                </div>
              </div>
            </template>
            
            <div class="info-grid">
              <div class="info-row">
                <div class="info-item">
                  <div class="item-label">
                    <el-icon><OfficeBuilding /></el-icon>
                    <span>公司</span>
                  </div>
                  <span class="item-value">{{ profileData.company || '--' }}</span>
                </div>
                <div class="info-item">
                  <div class="item-label">
                    <el-icon><Postcard /></el-icon>
                    <span>职位</span>
                  </div>
                  <span class="item-value">{{ profileData.position || '--' }}</span>
                </div>
              </div>
            </div>
          </el-card>
          
          <!-- 联系方式卡片 -->
          <el-card class="info-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <div class="header-title">
                  <el-icon><ChatDotRound /></el-icon>
                  <span>联系方式</span>
                </div>
              </div>
            </template>
            
            <div class="info-grid">
              <div class="info-row">
                <div class="info-item">
                  <div class="item-label">
                    <el-icon><Link /></el-icon>
                    <span>个人网站</span>
                  </div>
                  <el-link v-if="profileData.website" :href="profileData.website" target="_blank" type="primary" class="item-value">
                    {{ profileData.website }}
                  </el-link>
                  <span v-else class="item-value">--</span>
                </div>
                <div class="info-item">
                  <div class="item-label">
                    <el-icon><ChatRound /></el-icon>
                    <span>QQ号码</span>
                  </div>
                  <span class="item-value">{{ profileData.qq || '--' }}</span>
                </div>
                <div class="info-item">
                  <div class="item-label">
                    <el-icon><ChatLineRound /></el-icon>
                    <span>微信号</span>
                  </div>
                  <span class="item-value">{{ profileData.wechat || '--' }}</span>
                </div>
              </div>
            </div>
          </el-card>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  User, 
  Edit, 
  Key, 
  Setting, 
  Camera, 
  Message, 
  Phone,
  UserFilled,
  Avatar,
  Male,
  Calendar,
  Location,
  Briefcase,
  OfficeBuilding,
  Postcard,
  ChatDotRound,
  Link,
  ChatRound,
  ChatLineRound,
  CircleCheck,
  CircleCheckFilled,
  InfoFilled,
  SwitchButton
} from '@element-plus/icons-vue'
import { profileApi } from '@/api'
import { useRouter } from 'vue-router'
import { getAvatarUrl } from '@/utils/url'

const router = useRouter()
const profileData = ref({})
const fileInput = ref(null)

// 获取当前用户ID
const getCurrentUserId = () => {
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
  return userInfo.id
}

// 处理头像URL（使用工具函数）
const processAvatarUrl = (avatar) => {
  return getAvatarUrl(avatar)
}

// 加载用户资料
const loadProfile = async () => {
  try {
    const userId = getCurrentUserId()
    if (!userId) {
      ElMessage.error('请先登录')
      return
    }
    
    const data = await profileApi.getProfile(userId)
    profileData.value = data
  } catch (error) {
    console.error('加载用户资料失败:', error)
    ElMessage.error('加载用户资料失败')
  }
}

// 性别文本转换
const getGenderText = (gender) => {
  const genderMap = {
    0: '未知',
    1: '男',
    2: '女'
  }
  return genderMap[gender] || '未知'
}

// 性别标签类型
const getGenderTagType = (gender) => {
  const typeMap = {
    0: 'info',
    1: 'primary',
    2: 'danger'
  }
  return typeMap[gender] || 'info'
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return '--'
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  })
}

// 退出登录
const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '确认操作', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    localStorage.removeItem('userInfo')
    localStorage.removeItem('token')
    ElMessage.success('已退出登录')
    router.push('/login')
  })
}

// 处理头像上传
const handleAvatarUpload = () => {
  fileInput.value.click()
}

// 文件选择处理
const onFileChange = async (event) => {
  const file = event.target.files[0]
  if (!file) return
  
  const loading = ElMessage({
    message: '正在验证和上传头像...',
    type: 'info',
    duration: 0
  })
  
  try {
    // 验证文件类型
    const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif']
    if (!allowedTypes.includes(file.type)) {
      throw new Error(`文件格式不支持：${file.type.split('/')[1].toUpperCase()}，仅支持 JPG、PNG、GIF`)
    }
    
    // 验证文件大小（5MB）
    const maxSize = 5 * 1024 * 1024
    if (file.size > maxSize) {
      const fileSizeMB = (file.size / (1024 * 1024)).toFixed(2)
      throw new Error(`文件过大：${fileSizeMB}MB，最大允许 5MB`)
    }
    
    // 验证图片完整性
    await new Promise((resolve, reject) => {
      const img = new Image()
      img.onload = () => {
        if (img.width < 50 || img.height < 50) {
          reject(new Error(`图片尺寸过小：${img.width}x${img.height}，建议至少 200x200 像素`))
        } else {
          resolve()
        }
      }
      img.onerror = () => reject(new Error('图片文件损坏或格式不正确'))
      img.src = URL.createObjectURL(file)
    })
    
    // 开始上传
    const userId = getCurrentUserId()
    if (!userId) {
      throw new Error('用户未登录，请重新登录后再试')
    }
    
    const avatarUrl = await profileApi.uploadAvatar(userId, file)
    profileData.value.avatar = avatarUrl
    
    loading.close()
    ElMessage.success('头像上传成功！')
    
  } catch (error) {
    loading.close()
    
    // 根据错误类型显示不同的提示
    let errorMessage = '头像上传失败'
    
    if (error.message) {
      // 自定义错误信息
      errorMessage = error.message
    } else if (error.response) {
      // HTTP 错误
      const status = error.response.status
      const responseData = error.response.data
      
      switch (status) {
        case 413:
          errorMessage = '文件过大，服务器拒绝接收'
          break
        case 415:
          errorMessage = '服务器不支持该文件格式'
          break
        case 401:
          errorMessage = '登录已过期，请重新登录'
          break
        case 403:
          errorMessage = '您没有权限，请联系管理员'
          break
        case 500:
          // 检查是否是文件路径错误
          if (responseData && responseData.message && responseData.message.includes('FileNotFoundException')) {
            errorMessage = '服务器存储配置错误，请联系管理员检查上传目录'
          } else if (responseData && responseData.message && responseData.message.includes('找不到指定的路径')) {
            errorMessage = '上传目录不存在，正在尝试创建...'
            // 可以尝试重新上传一次
            setTimeout(() => {
              ElMessage.info('请重新尝试上传头像')
            }, 2000)
          } else {
            errorMessage = '服务器内部错误，请稍后重试'
          }
          break
        case 502:
        case 503:
          errorMessage = '服务器暂时不可用，请稍后重试'
          break
        case 404:
          errorMessage = '上传接口不存在'
          break
        default:
          errorMessage = `服务器错误 (${status})，请联系管理员`
      }
    } else if (error.code === 'NETWORK_ERROR') {
      errorMessage = '网络连接失败，请检查网络设置'
    } else if (error.code === 'TIMEOUT') {
      errorMessage = '上传超时，请检查网络或重试'
    }
    
    ElMessage({
      message: errorMessage,
      type: 'error',
      duration: 5000,
      showClose: true
    })
    
    console.error('头像上传详细错误:', error)
  }
  
  // 清空文件输入
  event.target.value = ''
}

onMounted(() => {
  loadProfile()
})
</script>

<style scoped>
.profile-container {
  padding: 24px;
  background: #f8fafc;
  min-height: calc(100vh - 120px);
}

/* 页面头部 */
.page-header {
  background: white;
  padding: 24px 32px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  margin-bottom: 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border: 1px solid #e5e7eb;
}

.header-left {
  flex: 1;
}

.page-title {
  margin: 0 0 8px 0;
  font-size: 28px;
  font-weight: 700;
  color: #1f2937;
  display: flex;
  align-items: center;
  gap: 12px;
}

.title-icon {
  width: 32px;
  height: 32px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 18px;
}

.page-subtitle {
  margin: 0;
  color: #6b7280;
  font-size: 16px;
  font-weight: 400;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.edit-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 8px;
  padding: 12px 24px;
  font-weight: 600;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
  transition: all 0.2s ease;
}

.edit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(102, 126, 234, 0.5);
}

.profile-content {
  margin-top: 0;
}

/* 个人信息卡片 */
.profile-card {
  border-radius: 16px;
  border: 1px solid #e5e7eb;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  margin-bottom: 24px;
}

.profile-header {
  text-align: center;
  padding: 32px 24px 24px;
  background: linear-gradient(135deg, #f8fafc 0%, #e5e7eb 100%);
}

.avatar-section {
  margin-bottom: 24px;
  position: relative;
}

.avatar-wrapper {
  position: relative;
  display: inline-block;
  margin-bottom: 16px;
}

.profile-avatar {
  border: 4px solid white;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
  transition: all 0.3s ease;
}

.avatar-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  border-radius: 50%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: all 0.3s ease;
  cursor: pointer;
  color: white;
  font-size: 12px;
  font-weight: 500;
}

.avatar-wrapper:hover .avatar-overlay {
  opacity: 1;
}

.avatar-status {
  position: absolute;
  bottom: 8px;
  right: 8px;
}

.upload-tips {
  font-size: 12px;
  color: #6b7280;
  display: flex;
  align-items: center;
  gap: 6px;
  justify-content: center;
  background: #f3f4f6;
  padding: 8px 12px;
  border-radius: 8px;
  margin-top: 8px;
}

.profile-info {
  text-align: center;
}

.name-section {
  margin-bottom: 16px;
}

.profile-name {
  margin: 0 0 8px 0;
  color: #1f2937;
  font-size: 24px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  flex-wrap: wrap;
}

.verified-icon {
  color: #10b981;
  font-size: 20px;
}

.nickname-tag {
  font-size: 12px;
  font-weight: 500;
  border-radius: 6px;
}

.contact-info {
  margin-bottom: 16px;
}

.contact-item {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin: 8px 0;
  color: #6b7280;
  font-size: 14px;
}

.bio {
  color: #6b7280;
  font-size: 14px;
  margin: 16px 0;
  line-height: 1.6;
  padding: 12px 16px;
  background: #f8fafc;
  border-radius: 8px;
  border-left: 4px solid #667eea;
}

.profile-stats {
  display: flex;
  justify-content: space-around;
  margin-top: 20px;
  padding: 16px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.stat-item {
  text-align: center;
}

.stat-number {
  display: block;
  font-size: 18px;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 12px;
  color: #6b7280;
  font-weight: 500;
}

.quick-actions {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 24px;
  background: #f8fafc;
}

.action-btn {
  justify-content: flex-start;
  height: 44px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s ease;
  border: 1px solid #e5e7eb;
}

.action-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.logout-btn {
  color: #ef4444;
  border-color: #fecaca;
  background: #fef2f2;
}

.logout-btn:hover {
  background: #fee2e2;
  border-color: #fca5a5;
}

/* 详细信息卡片 */
.details-section {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.info-card {
  border-radius: 16px;
  border: 1px solid #e5e7eb;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  overflow: hidden;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  padding: 0;
}

.header-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  font-weight: 700;
}

.header-title .el-icon {
  color: #667eea;
  font-size: 20px;
}

.info-grid {
  padding: 0;
}

.info-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background: #f8fafc;
  border-radius: 12px;
  border: 1px solid #e5e7eb;
  min-height: 60px;
  transition: all 0.2s ease;
}

.info-item:hover {
  background: #f0f7ff;
  border-color: #667eea;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
}

.item-label {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #6b7280;
  font-weight: 500;
  flex-shrink: 0;
}

.item-label .el-icon {
  color: #667eea;
  font-size: 16px;
}

.item-value {
  font-size: 14px;
  color: #1f2937;
  font-weight: 500;
  text-align: right;
  word-break: break-all;
  margin-left: 12px;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .profile-container {
    padding: 20px;
  }
  
  .page-header {
    padding: 20px 24px;
  }
  
  .page-title {
    font-size: 24px;
  }
  
  .info-row {
    grid-template-columns: 1fr;
    gap: 12px;
  }
}

@media (max-width: 768px) {
  .profile-container {
    padding: 16px;
  }
  
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
    padding: 20px;
  }
  
  .page-title {
    font-size: 22px;
  }
  
  .title-icon {
    width: 28px;
    height: 28px;
    font-size: 16px;
  }
  
  .page-subtitle {
    font-size: 14px;
  }
  
  .header-actions {
    align-self: stretch;
  }
  
  .edit-btn {
    width: 100%;
    justify-content: center;
  }
  
  .profile-header {
    padding: 24px 20px 20px;
  }
  
  .profile-name {
    font-size: 20px;
    flex-direction: column;
    gap: 8px;
  }
  
  .contact-item {
    font-size: 13px;
  }
  
  .profile-stats {
    flex-direction: column;
    gap: 12px;
  }
  
  .stat-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 8px 0;
  }
  
  .stat-number {
    font-size: 16px;
    margin-bottom: 0;
  }
  
  .quick-actions {
    padding: 20px;
  }
  
  .info-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
    padding: 14px 16px;
    min-height: auto;
  }
  
  .item-label {
    font-size: 13px;
  }
  
  .item-value {
    text-align: left;
    margin-left: 0;
    font-size: 13px;
  }
}

@media (max-width: 480px) {
  .profile-container {
    padding: 12px;
  }
  
  .page-header {
    padding: 16px;
    margin-bottom: 16px;
  }
  
  .page-title {
    font-size: 20px;
  }
  
  .profile-header {
    padding: 20px 16px 16px;
  }
  
  .profile-name {
    font-size: 18px;
  }
  
  .profile-avatar {
    width: 100px !important;
    height: 100px !important;
  }
  
  .quick-actions {
    padding: 16px;
    gap: 10px;
  }
  
  .action-btn {
    height: 40px;
    font-size: 13px;
  }
  
  .details-section {
    gap: 16px;
  }
  
  .info-item {
    padding: 12px 14px;
  }
}
</style> 