<template>
  <div class="profile-edit-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h2 class="page-title">
          <el-icon class="title-icon"><EditPen /></el-icon>
          编辑个人资料
        </h2>
        <p class="page-subtitle">完善您的个人信息，让其他人更好地了解您</p>
      </div>
      <div class="header-actions">
        <el-button @click="$router.back()" class="back-btn">
          <el-icon><ArrowLeft /></el-icon>
          返回
        </el-button>
      </div>
    </div>

    <div class="edit-content">
      <!-- 基本信息卡片 -->
      <el-card class="info-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <div class="header-title">
              <el-icon><User /></el-icon>
              <span>基本信息</span>
            </div>
          </div>
        </template>
        
        <el-form
          ref="formRef"
          :model="formData"
          :rules="rules"
          label-width="120px"
          @submit.prevent="handleSubmit"
          class="profile-form"
        >
          <el-row :gutter="24">
            <el-col :lg="12" :md="24">
              <el-form-item label="真实姓名" prop="realName">
                <el-input 
                  v-model="formData.realName" 
                  placeholder="请输入真实姓名"
                  :prefix-icon="User"
                  clearable
                />
              </el-form-item>
            </el-col>
            <el-col :lg="12" :md="24">
              <el-form-item label="昵称" prop="nickname">
                <el-input 
                  v-model="formData.nickname" 
                  placeholder="请输入昵称"
                  :prefix-icon="Avatar"
                  @blur="checkNickname"
                  clearable
                />
                <div class="form-tip" v-if="nicknameCheckResult">
                  <el-icon>
                    <component :is="nicknameCheckResult.available ? 'CircleCheck' : 'CircleClose'" />
                  </el-icon>
                  <span :class="nicknameCheckResult.available ? 'success' : 'error'">
                    {{ nicknameCheckResult.message }}
                  </span>
                </div>
              </el-form-item>
            </el-col>
          </el-row>
          
          <el-row :gutter="24">
            <el-col :lg="12" :md="24">
              <el-form-item label="手机号码" prop="phone">
                <el-input 
                  v-model="formData.phone" 
                  placeholder="请输入手机号码"
                  :prefix-icon="Phone"
                  clearable
                />
              </el-form-item>
            </el-col>
            <el-col :lg="12" :md="24">
              <el-form-item label="性别" prop="gender">
                <el-select v-model="formData.gender" placeholder="请选择性别" class="gender-select">
                  <el-option label="未知" :value="0">
                    <span class="option-content">
                      <el-icon><QuestionFilled /></el-icon>
                      未知
                    </span>
                  </el-option>
                  <el-option label="男" :value="1">
                    <span class="option-content">
                      <el-icon><Male /></el-icon>
                      男
                    </span>
                  </el-option>
                  <el-option label="女" :value="2">
                    <span class="option-content">
                      <el-icon><Female /></el-icon>
                      女
                    </span>
                  </el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          
          <el-row :gutter="24">
            <el-col :lg="12" :md="24">
              <el-form-item label="生日" prop="birthday">
                <el-date-picker
                  v-model="formData.birthday"
                  type="date"
                  placeholder="请选择生日"
                  format="YYYY-MM-DD"
                  value-format="YYYY-MM-DD"
                  :prefix-icon="Calendar"
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
            <el-col :lg="12" :md="24">
              <el-form-item label="地址" prop="address">
                <el-input 
                  v-model="formData.address" 
                  placeholder="请输入地址"
                  :prefix-icon="Location"
                  clearable
                />
              </el-form-item>
            </el-col>
          </el-row>
          
          <el-form-item label="个人简介" prop="bio">
            <el-input
              v-model="formData.bio"
              type="textarea"
              :rows="4"
              placeholder="请输入个人简介，让其他人更好地了解您..."
              maxlength="1000"
              show-word-limit
              resize="none"
            />
          </el-form-item>
        </el-form>
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
        
        <el-form
          :model="formData"
          :rules="rules"
          label-width="120px"
          class="profile-form"
        >
          <el-row :gutter="24">
            <el-col :lg="12" :md="24">
              <el-form-item label="公司" prop="company">
                <el-input 
                  v-model="formData.company" 
                  placeholder="请输入公司名称"
                  :prefix-icon="OfficeBuilding"
                  clearable
                />
              </el-form-item>
            </el-col>
            <el-col :lg="12" :md="24">
              <el-form-item label="职位" prop="position">
                <el-input 
                  v-model="formData.position" 
                  placeholder="请输入职位"
                  :prefix-icon="Postcard"
                  clearable
                />
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
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
        
        <el-form
          :model="formData"
          :rules="rules"
          label-width="120px"
          class="profile-form"
        >
          <el-row :gutter="24">
            <el-col :lg="12" :md="24">
              <el-form-item label="个人网站" prop="website">
                <el-input 
                  v-model="formData.website" 
                  placeholder="请输入个人网站地址"
                  :prefix-icon="Link"
                  clearable
                />
              </el-form-item>
            </el-col>
            <el-col :lg="12" :md="24">
              <el-form-item label="QQ号码" prop="qq">
                <el-input 
                  v-model="formData.qq" 
                  placeholder="请输入QQ号码"
                  :prefix-icon="ChatRound"
                  clearable
                />
              </el-form-item>
            </el-col>
          </el-row>
          
          <el-row :gutter="24">
            <el-col :lg="12" :md="24">
              <el-form-item label="微信号" prop="wechat">
                <el-input 
                  v-model="formData.wechat" 
                  placeholder="请输入微信号"
                  :prefix-icon="ChatLineRound"
                  clearable
                />
              </el-form-item>
            </el-col>
            <el-col :lg="12" :md="24">
              <el-form-item label="微博" prop="weibo">
                <el-input 
                  v-model="formData.weibo" 
                  placeholder="请输入微博账号"
                  :prefix-icon="Share"
                  clearable
                />
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </el-card>
      
      <!-- 操作按钮 -->
      <div class="action-buttons">
        <el-button type="primary" @click="handleSubmit" :loading="loading" size="large" class="save-btn">
          <el-icon><Check /></el-icon>
          保存修改
        </el-button>
        <el-button @click="handleReset" size="large" class="reset-btn">
          <el-icon><RefreshLeft /></el-icon>
          重置
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
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { 
  EditPen,
  ArrowLeft,
  User,
  Briefcase,
  ChatDotRound,
  Avatar,
  Phone,
  QuestionFilled,
  Male,
  Female,
  Calendar,
  Location,
  OfficeBuilding,
  Postcard,
  Link,
  ChatRound,
  ChatLineRound,
  Share,
  Check,
  RefreshLeft,
  Close,
  CircleCheck,
  CircleClose
} from '@element-plus/icons-vue'
import { profileApi } from '@/api'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)
const nicknameCheckResult = ref(null)

const formData = ref({
  userId: null,
  realName: '',
  nickname: '',
  phone: '',
  gender: null,
  birthday: '',
  bio: '',
  address: '',
  company: '',
  position: '',
  website: '',
  qq: '',
  wechat: '',
  weibo: ''
})

const rules = {
  realName: [
    { max: 50, message: '真实姓名长度不能超过50字符', trigger: 'blur' }
  ],
  nickname: [
    { max: 50, message: '昵称长度不能超过50字符', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  bio: [
    { max: 1000, message: '个人简介长度不能超过1000字符', trigger: 'blur' }
  ],
  address: [
    { max: 200, message: '地址长度不能超过200字符', trigger: 'blur' }
  ],
  company: [
    { max: 100, message: '公司名称长度不能超过100字符', trigger: 'blur' }
  ],
  position: [
    { max: 100, message: '职位长度不能超过100字符', trigger: 'blur' }
  ],
  website: [
    { max: 200, message: '网站地址长度不能超过200字符', trigger: 'blur' }
  ],
  qq: [
    { pattern: /^[1-9]\d{4,10}$/, message: '请输入正确的QQ号码', trigger: 'blur' }
  ],
  wechat: [
    { max: 50, message: '微信号长度不能超过50字符', trigger: 'blur' }
  ],
  weibo: [
    { max: 100, message: '微博账号长度不能超过100字符', trigger: 'blur' }
  ]
}

// 获取当前用户ID
const getCurrentUserId = () => {
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
  return userInfo.id
}

// 加载用户资料
const loadProfile = async () => {
  try {
    const userId = getCurrentUserId()
    if (!userId) {
      ElMessage.error('请先登录')
      router.push('/login')
      return
    }
    
    const data = await profileApi.getProfile(userId)
    Object.keys(formData.value).forEach(key => {
      if (data[key] !== undefined) {
        formData.value[key] = data[key]
      }
    })
    formData.value.userId = userId
  } catch (error) {
    console.error('加载用户资料失败:', error)
    ElMessage.error('加载用户资料失败')
  }
}

// 检查昵称可用性
const checkNickname = async () => {
  if (!formData.value.nickname || !formData.value.userId) {
    nicknameCheckResult.value = null
    return
  }
  
  try {
    const available = await profileApi.checkNickname(
      formData.value.nickname, 
      formData.value.userId
    )
    
    nicknameCheckResult.value = {
      available,
      message: available ? '昵称可用' : '昵称已被使用'
    }
  } catch (error) {
    console.error('检查昵称失败:', error)
  }
}

// 提交表单
const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    
    // 检查昵称是否可用
    if (formData.value.nickname && 
        nicknameCheckResult.value && 
        !nicknameCheckResult.value.available) {
      ElMessage.error('昵称已被使用，请更换')
      return
    }
    
    loading.value = true
    await profileApi.updateProfile(formData.value)
    ElMessage.success('个人资料更新成功')
    router.push('/profile')
  } catch (error) {
    console.error('更新个人资料失败:', error)
    ElMessage.error('更新个人资料失败')
  } finally {
    loading.value = false
  }
}

// 重置表单
const handleReset = () => {
  formRef.value.resetFields()
  nicknameCheckResult.value = null
  loadProfile()
}

onMounted(() => {
  loadProfile()
})
</script>

<style scoped>
.profile-edit-container {
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

/* 编辑内容 */
.edit-content {
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

.profile-form {
  padding: 0;
}

/* 表单项样式 */
.form-tip {
  font-size: 12px;
  margin-top: 8px;
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  border-radius: 6px;
  background: #f3f4f6;
}

.form-tip .success {
  color: #10b981;
}

.form-tip .error {
  color: #ef4444;
}

.form-tip .el-icon {
  font-size: 14px;
}

/* 输入框样式 */
.el-input {
  border-radius: 8px;
}

.el-input :deep(.el-input__wrapper) {
  border-radius: 8px;
  transition: all 0.2s ease;
}

.el-input :deep(.el-input__wrapper:hover) {
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.15);
}

.el-select,
.el-date-picker {
  width: 100%;
}

.el-select :deep(.el-input__wrapper) {
  border-radius: 8px;
}

.gender-select {
  width: 100%;
}

.option-content {
  display: flex;
  align-items: center;
  gap: 8px;
}

/* 文本域样式 */
.el-textarea :deep(.el-textarea__inner) {
  border-radius: 8px;
  transition: all 0.2s ease;
}

.el-textarea :deep(.el-textarea__inner:hover) {
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.15);
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
  .profile-edit-container {
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
  .profile-edit-container {
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
  
  .profile-form {
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
}

@media (max-width: 480px) {
  .profile-edit-container {
    padding: 12px;
  }
  
  .page-header {
    padding: 16px;
    margin-bottom: 16px;
  }
  
  .page-title {
    font-size: 20px;
  }
  
  .edit-content {
    gap: 16px;
  }
  
  .profile-form {
    padding: 0;
  }
}
</style> 