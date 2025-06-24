<template>
  <div class="change-password-container">
    <el-card class="password-card">
      <template #header>
        <div class="card-header">
          <span>修改密码</span>
          <el-button @click="$router.back()">返回</el-button>
        </div>
      </template>
      
      <div class="password-form">
        <el-form
          ref="formRef"
          :model="formData"
          :rules="rules"
          label-width="120px"
          @submit.prevent="handleSubmit"
        >
          <el-form-item label="当前密码" prop="currentPassword">
            <el-input
              v-model="formData.currentPassword"
              type="password"
              placeholder="请输入当前密码"
              show-password
              autocomplete="current-password"
            />
          </el-form-item>
          
          <el-form-item label="新密码" prop="newPassword">
            <el-input
              v-model="formData.newPassword"
              type="password"
              placeholder="请输入新密码"
              show-password
              autocomplete="new-password"
            />
            <div class="password-strength">
              <span>密码强度：</span>
              <div class="strength-bar">
                <div 
                  class="strength-fill"
                  :class="passwordStrength.level"
                  :style="{ width: passwordStrength.percentage + '%' }"
                ></div>
              </div>
              <span class="strength-text">{{ passwordStrength.text }}</span>
            </div>
          </el-form-item>
          
          <el-form-item label="确认新密码" prop="confirmPassword">
            <el-input
              v-model="formData.confirmPassword"
              type="password"
              placeholder="请再次输入新密码"
              show-password
              autocomplete="new-password"
            />
          </el-form-item>
          
          <el-form-item>
            <el-button type="primary" @click="handleSubmit" :loading="loading">
              修改密码
            </el-button>
            <el-button @click="handleReset">重置</el-button>
            <el-button @click="$router.back()">取消</el-button>
          </el-form-item>
        </el-form>
        
        <el-divider />
        
        <div class="password-tips">
          <h4>密码安全建议：</h4>
          <ul>
            <li>密码长度至少6位，建议8位以上</li>
            <li>包含大小写字母、数字和特殊字符</li>
            <li>不要使用简单的密码，如123456、password等</li>
            <li>不要使用个人信息相关的密码</li>
            <li>定期更换密码，保障账户安全</li>
          </ul>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { profileApi } from '@/api'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)

const formData = ref({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 自定义验证规则
const validateConfirmPassword = (rule, value, callback) => {
  if (value !== formData.value.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const validateNewPassword = (rule, value, callback) => {
  if (value === formData.value.currentPassword) {
    callback(new Error('新密码不能与当前密码相同'))
  } else {
    callback()
  }
}

const rules = {
  currentPassword: [
    { required: true, message: '请输入当前密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度必须在6-20字符之间', trigger: 'blur' },
    { validator: validateNewPassword, trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

// 密码强度计算
const passwordStrength = computed(() => {
  const password = formData.value.newPassword
  
  if (!password) {
    return { level: 'weak', percentage: 0, text: '请输入密码' }
  }
  
  let score = 0
  const checks = {
    length: password.length >= 8,
    lowercase: /[a-z]/.test(password),
    uppercase: /[A-Z]/.test(password),
    numbers: /\d/.test(password),
    symbols: /[^A-Za-z0-9]/.test(password)
  }
  
  // 基础长度分数
  if (password.length >= 6) score += 20
  if (password.length >= 8) score += 10
  if (password.length >= 12) score += 10
  
  // 字符类型分数
  Object.values(checks).forEach(check => {
    if (check) score += 15
  })
  
  // 避免简单密码
  const commonPasswords = ['123456', 'password', '12345678', 'qwerty', 'abc123']
  if (commonPasswords.includes(password.toLowerCase())) {
    score = Math.min(score, 30)
  }
  
  if (score <= 30) {
    return { level: 'weak', percentage: score, text: '弱' }
  } else if (score <= 60) {
    return { level: 'medium', percentage: score, text: '中等' }
  } else {
    return { level: 'strong', percentage: score, text: '强' }
  }
})

// 获取当前用户ID
const getCurrentUserId = () => {
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
  return userInfo.id
}

// 提交表单
const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    
    await ElMessageBox.confirm(
      '确定要修改密码吗？修改后需要重新登录。',
      '确认修改',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    loading.value = true
    const userId = getCurrentUserId()
    
    await profileApi.changePassword(userId, formData.value)
    
    ElMessage.success('密码修改成功，请重新登录')
    
    // 清除登录信息
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    
    // 跳转到登录页
    setTimeout(() => {
      router.push('/login')
    }, 1500)
    
  } catch (error) {
    if (error === 'cancel') {
      // 用户取消操作
      return
    }
    console.error('修改密码失败:', error)
    ElMessage.error('修改密码失败')
  } finally {
    loading.value = false
  }
}

// 重置表单
const handleReset = () => {
  formRef.value.resetFields()
}
</script>

<style scoped>
.change-password-container {
  padding: 20px;
  display: flex;
  justify-content: center;
}

.password-card {
  width: 100%;
  max-width: 600px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.password-form {
  padding: 20px 0;
}

.password-strength {
  display: flex;
  align-items: center;
  margin-top: 8px;
  font-size: 12px;
  color: #666;
}

.strength-bar {
  width: 100px;
  height: 6px;
  background-color: #f0f0f0;
  border-radius: 3px;
  margin: 0 8px;
  overflow: hidden;
}

.strength-fill {
  height: 100%;
  border-radius: 3px;
  transition: all 0.3s ease;
}

.strength-fill.weak {
  background-color: #f56c6c;
}

.strength-fill.medium {
  background-color: #e6a23c;
}

.strength-fill.strong {
  background-color: #67c23a;
}

.strength-text {
  font-weight: bold;
}

.password-tips {
  background-color: #f8f9fa;
  padding: 16px;
  border-radius: 6px;
  border-left: 4px solid #409eff;
}

.password-tips h4 {
  margin: 0 0 12px 0;
  color: #303133;
  font-size: 14px;
}

.password-tips ul {
  margin: 0;
  padding-left: 20px;
}

.password-tips li {
  color: #606266;
  font-size: 13px;
  line-height: 1.6;
  margin-bottom: 4px;
}
</style> 