<template>
  <div class="user-settings-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h2 class="page-title">
          <el-icon class="title-icon"><Setting /></el-icon>
          系统设置
        </h2>
        <p class="page-subtitle">个性化您的系统偏好和隐私设置</p>
      </div>
      <div class="header-actions">
        <el-button @click="$router.back()" class="back-btn">
          <el-icon><ArrowLeft /></el-icon>
          返回
        </el-button>
      </div>
    </div>

    <div class="settings-content">
      <!-- 界面设置卡片 -->
      <el-card class="settings-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <div class="header-title">
              <el-icon><Monitor /></el-icon>
              <span>界面设置</span>
            </div>
          </div>
        </template>
        
        <el-form
          ref="formRef"
          :model="formData"
          label-width="140px"
          @submit.prevent="handleSubmit"
          class="settings-form"
        >
          <el-row :gutter="24">
            <el-col :lg="12" :md="24">
              <el-form-item label="主题风格">
                <el-radio-group v-model="formData.theme" @change="previewTheme" class="theme-group">
                  <el-radio-button label="light" class="theme-option">
                    <el-icon><Sunny /></el-icon>
                    浅色主题
                  </el-radio-button>
                  <el-radio-button label="dark" class="theme-option">
                    <el-icon><Moon /></el-icon>
                    深色主题
                  </el-radio-button>
                </el-radio-group>
                <div class="form-tip">
                  <el-icon><InfoFilled /></el-icon>
                  选择您喜欢的界面主题风格
                </div>
              </el-form-item>
            </el-col>
            <el-col :lg="12" :md="24">
              <el-form-item label="语言设置">
                <el-select v-model="formData.language" placeholder="请选择语言" class="language-select">
                  <el-option label="简体中文" value="zh-CN">
                    <span class="option-content">
                      <span class="flag">🇨🇳</span>
                      简体中文
                    </span>
                  </el-option>
                  <el-option label="English" value="en-US">
                    <span class="option-content">
                      <span class="flag">🇺🇸</span>
                      English
                    </span>
                  </el-option>
                  <el-option label="繁體中文" value="zh-TW">
                    <span class="option-content">
                      <span class="flag">🇹🇼</span>
                      繁體中文
                    </span>
                  </el-option>
                  <el-option label="日本語" value="ja-JP">
                    <span class="option-content">
                      <span class="flag">🇯🇵</span>
                      日本語
                    </span>
                  </el-option>
                </el-select>
                <div class="form-tip">
                  <el-icon><InfoFilled /></el-icon>
                  系统界面显示语言
                </div>
              </el-form-item>
            </el-col>
          </el-row>
          
          <el-form-item label="时区设置">
            <el-select v-model="formData.timezone" placeholder="请选择时区" filterable class="timezone-select">
              <el-option
                v-for="timezone in timezones"
                :key="timezone.value"
                :label="timezone.label"
                :value="timezone.value"
              />
            </el-select>
            <div class="form-tip">
              <el-icon><InfoFilled /></el-icon>
              选择您所在的时区，影响时间显示格式
            </div>
          </el-form-item>
        </el-form>
      </el-card>
      
      <!-- 通知设置卡片 -->
      <el-card class="settings-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <div class="header-title">
              <el-icon><Bell /></el-icon>
              <span>通知设置</span>
            </div>
          </div>
        </template>
        
        <el-form
          :model="formData"
          label-width="140px"
          class="settings-form"
        >
          <el-row :gutter="24">
            <el-col :lg="12" :md="24">
              <el-form-item label="邮件通知">
                <div class="switch-container">
                  <el-switch
                    v-model="emailNotification"
                    active-text="开启"
                    inactive-text="关闭"
                    active-color="#67c23a"
                    inactive-color="#dcdfe6"
                  />
                  <el-icon class="notification-icon"><Message /></el-icon>
                </div>
                <div class="form-tip">
                  <el-icon><InfoFilled /></el-icon>
                  接收系统重要通知邮件
                </div>
              </el-form-item>
            </el-col>
            <el-col :lg="12" :md="24">
              <el-form-item label="短信通知">
                <div class="switch-container">
                  <el-switch
                    v-model="smsNotification"
                    active-text="开启"
                    inactive-text="关闭"
                    active-color="#67c23a"
                    inactive-color="#dcdfe6"
                  />
                  <el-icon class="notification-icon"><ChatDotRound /></el-icon>
                </div>
                <div class="form-tip">
                  <el-icon><InfoFilled /></el-icon>
                  接收系统重要通知短信
                </div>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </el-card>
      
      <!-- 隐私设置卡片 -->
      <el-card class="settings-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <div class="header-title">
              <el-icon><Lock /></el-icon>
              <span>隐私设置</span>
            </div>
          </div>
        </template>
        
        <el-form
          :model="formData"
          label-width="140px"
          class="settings-form"
        >
          <el-form-item label="隐私级别">
            <el-radio-group v-model="formData.privacyLevel" class="privacy-group">
              <el-radio :label="0" class="privacy-option">
                <div class="radio-content">
                  <div class="radio-header">
                    <el-icon><View /></el-icon>
                    <span class="radio-label">完全公开</span>
                  </div>
                  <div class="radio-desc">所有用户都可以查看您的完整资料信息</div>
                </div>
              </el-radio>
              <el-radio :label="1" class="privacy-option">
                <div class="radio-content">
                  <div class="radio-header">
                    <el-icon><User /></el-icon>
                    <span class="radio-label">部分公开</span>
                  </div>
                  <div class="radio-desc">仅同事和朋友可以查看您的详细资料</div>
                </div>
              </el-radio>
              <el-radio :label="2" class="privacy-option">
                <div class="radio-content">
                  <div class="radio-header">
                    <el-icon><Hide /></el-icon>
                    <span class="radio-label">完全私密</span>
                  </div>
                  <div class="radio-desc">仅您自己可以查看完整的个人资料</div>
                </div>
              </el-radio>
            </el-radio-group>
          </el-form-item>
        </el-form>
      </el-card>
      
      <!-- 操作按钮 -->
      <div class="action-buttons">
        <el-button type="primary" @click="handleSubmit" :loading="loading" size="large" class="save-btn">
          <el-icon><Check /></el-icon>
          保存设置
        </el-button>
        <el-button @click="handleReset" size="large" class="reset-btn">
          <el-icon><RefreshLeft /></el-icon>
          重置为默认
        </el-button>
        <el-button @click="$router.back()" size="large" class="cancel-btn">
          <el-icon><Close /></el-icon>
          取消
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Setting,
  ArrowLeft,
  Monitor,
  Bell,
  Lock,
  Sunny,
  Moon,
  InfoFilled,
  Message,
  ChatDotRound,
  View,
  User,
  Hide,
  Check,
  RefreshLeft,
  Close
} from '@element-plus/icons-vue'
import { settingsApi } from '@/api'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)

const formData = ref({
  userId: null,
  theme: 'light',
  language: 'zh-CN',
  timezone: 'Asia/Shanghai',
  emailNotification: 1,
  smsNotification: 1,
  privacyLevel: 1
})

// 邮件和短信通知的计算属性（用于switch组件）
const emailNotification = computed({
  get: () => formData.value.emailNotification === 1,
  set: (value) => formData.value.emailNotification = value ? 1 : 0
})

const smsNotification = computed({
  get: () => formData.value.smsNotification === 1,
  set: (value) => formData.value.smsNotification = value ? 1 : 0
})

// 时区选项
const timezones = [
  { label: '北京时间 (UTC+8)', value: 'Asia/Shanghai' },
  { label: '东京时间 (UTC+9)', value: 'Asia/Tokyo' },
  { label: '首尔时间 (UTC+9)', value: 'Asia/Seoul' },
  { label: '香港时间 (UTC+8)', value: 'Asia/Hong_Kong' },
  { label: '新加坡时间 (UTC+8)', value: 'Asia/Singapore' },
  { label: '纽约时间 (UTC-5)', value: 'America/New_York' },
  { label: '洛杉矶时间 (UTC-8)', value: 'America/Los_Angeles' },
  { label: '伦敦时间 (UTC+0)', value: 'Europe/London' },
  { label: '巴黎时间 (UTC+1)', value: 'Europe/Paris' },
  { label: '莫斯科时间 (UTC+3)', value: 'Europe/Moscow' },
  { label: '悉尼时间 (UTC+10)', value: 'Australia/Sydney' },
  { label: 'UTC 协调世界时', value: 'UTC' }
]

// 获取当前用户ID
const getCurrentUserId = () => {
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
  return userInfo.id
}

// 加载用户设置
const loadSettings = async () => {
  try {
    const userId = getCurrentUserId()
    if (!userId) {
      ElMessage.error('请先登录')
      router.push('/login')
      return
    }
    
    const data = await settingsApi.getSettings(userId)
    Object.keys(formData.value).forEach(key => {
      if (data[key] !== undefined) {
        formData.value[key] = data[key]
      }
    })
    formData.value.userId = userId
  } catch (error) {
    console.error('加载用户设置失败:', error)
    ElMessage.error('加载用户设置失败')
  }
}

// 预览主题
const previewTheme = (theme) => {
  // 这里可以实现主题预览功能
  console.log('预览主题:', theme)
  // 实际项目中可以添加主题切换逻辑
}

// 提交表单
const handleSubmit = async () => {
  try {
    loading.value = true
    await settingsApi.updateSettings(formData.value)
    ElMessage.success('设置保存成功')
    
    // 如果语言或主题发生变化，可以提示用户刷新页面
    const needRefresh = formData.value.theme !== 'light' || formData.value.language !== 'zh-CN'
    if (needRefresh) {
      await ElMessageBox.confirm(
        '语言或主题设置已更改，是否刷新页面以应用新设置？',
        '提示',
        {
          confirmButtonText: '刷新页面',
          cancelButtonText: '稍后手动刷新',
          type: 'info'
        }
      )
      window.location.reload()
    }
  } catch (error) {
    if (error === 'cancel') {
      return
    }
    console.error('保存设置失败:', error)
    ElMessage.error('保存设置失败')
  } finally {
    loading.value = false
  }
}

// 重置为默认设置
const handleReset = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要重置为默认设置吗？此操作不可恢复。',
      '确认重置',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    loading.value = true
    const userId = getCurrentUserId()
    const data = await settingsApi.resetToDefault(userId)
    
    Object.keys(formData.value).forEach(key => {
      if (data[key] !== undefined) {
        formData.value[key] = data[key]
      }
    })
    
    ElMessage.success('设置已重置为默认值')
  } catch (error) {
    if (error === 'cancel') {
      return
    }
    console.error('重置设置失败:', error)
    ElMessage.error('重置设置失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadSettings()
})
</script>

<style scoped>
.user-settings-container {
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

.back-btn {
  border-radius: 8px;
  padding: 12px 20px;
  font-weight: 500;
  transition: all 0.2s ease;
}

.back-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

/* 设置内容 */
.settings-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.settings-card {
  border-radius: 16px;
  border: 1px solid #e5e7eb;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  overflow: hidden;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0;
}

.header-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  font-weight: 700;
  color: #1f2937;
}

.header-title .el-icon {
  color: #667eea;
  font-size: 20px;
}

.settings-form {
  padding: 0;
}

/* 表单项样式 */
.form-tip {
  font-size: 12px;
  color: #6b7280;
  margin-top: 8px;
  line-height: 1.5;
  display: flex;
  align-items: center;
  gap: 6px;
  background: #f3f4f6;
  padding: 8px 12px;
  border-radius: 6px;
}

.form-tip .el-icon {
  color: #667eea;
  font-size: 14px;
}

/* 主题选择 */
.theme-group {
  width: 100%;
}

.theme-option {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  border-radius: 8px;
  transition: all 0.2s ease;
}

/* 语言选择 */
.language-select,
.timezone-select {
  width: 100%;
}

.option-content {
  display: flex;
  align-items: center;
  gap: 8px;
}

.flag {
  font-size: 16px;
}

/* 通知开关 */
.switch-container {
  display: flex;
  align-items: center;
  gap: 12px;
}

.notification-icon {
  color: #667eea;
  font-size: 18px;
}

/* 隐私设置 */
.privacy-group {
  width: 100%;
}

.privacy-option {
  display: block;
  margin-bottom: 20px;
  margin-right: 0;
  padding: 16px 20px;
  background: #f8fafc;
  border-radius: 12px;
  border: 2px solid #e5e7eb;
  transition: all 0.2s ease;
}

.privacy-option:hover {
  border-color: #667eea;
  background: #f0f7ff;
}

.privacy-option.is-checked {
  border-color: #667eea;
  background: #f0f7ff;
}

.privacy-option:last-child {
  margin-bottom: 0;
}

.radio-content {
  margin-left: 0;
}

.radio-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.radio-label {
  font-weight: 600;
  color: #1f2937;
  font-size: 16px;
}

.radio-desc {
  font-size: 14px;
  color: #6b7280;
  line-height: 1.5;
  margin-left: 24px;
}

/* 操作按钮 */
.action-buttons {
  display: flex;
  gap: 16px;
  justify-content: center;
  padding: 24px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid #e5e7eb;
}

.save-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 8px;
  padding: 14px 28px;
  font-weight: 600;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
  transition: all 0.2s ease;
}

.save-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(102, 126, 234, 0.5);
}

.reset-btn,
.cancel-btn {
  border-radius: 8px;
  padding: 14px 28px;
  font-weight: 500;
  transition: all 0.2s ease;
}

.reset-btn:hover,
.cancel-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .user-settings-container {
    padding: 20px;
  }
  
  .page-header {
    padding: 20px 24px;
  }
  
  .page-title {
    font-size: 24px;
  }
}

@media (max-width: 768px) {
  .user-settings-container {
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
  
  .back-btn {
    width: 100%;
    justify-content: center;
  }
  
  .settings-form {
    padding: 0;
  }
  
  .action-buttons {
    flex-direction: column;
    gap: 12px;
  }
  
  .save-btn,
  .reset-btn,
  .cancel-btn {
    width: 100%;
    justify-content: center;
  }
  
  .privacy-option {
    padding: 14px 16px;
  }
  
  .radio-desc {
    margin-left: 20px;
    font-size: 13px;
  }
}

@media (max-width: 480px) {
  .user-settings-container {
    padding: 12px;
  }
  
  .page-header {
    padding: 16px;
    margin-bottom: 16px;
  }
  
  .page-title {
    font-size: 20px;
  }
  
  .settings-content {
    gap: 16px;
  }
  
  .privacy-option {
    padding: 12px 14px;
  }
  
  .radio-label {
    font-size: 15px;
  }
  
  .radio-desc {
    font-size: 12px;
    margin-left: 18px;
  }
}
</style> 