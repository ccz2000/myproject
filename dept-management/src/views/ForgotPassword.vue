<template>
  <div class="forgot-password-container">
    <div class="forgot-password-wrapper">
      <!-- 左侧装饰 -->
      <div class="forgot-password-banner">
        <div class="banner-content">
          <h1 class="banner-title">找回密码</h1>
          <p class="banner-subtitle">通过邮箱验证重置您的密码</p>
          <div class="banner-features">
            <div class="feature-item">
              <el-icon><Message /></el-icon>
              <span>邮箱验证</span>
            </div>
            <div class="feature-item">
              <el-icon><Key /></el-icon>
              <span>安全重置</span>
            </div>
            <div class="feature-item">
              <el-icon><Lock /></el-icon>
              <span>密码加密</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧表单 -->
      <div class="forgot-password-form-container">
        <!-- 步骤1：输入邮箱 -->
        <div v-if="currentStep === 1" class="step-form">
          <div class="form-header">
            <h2>忘记密码</h2>
            <p>请输入您的注册邮箱，我们将发送验证码到您的邮箱</p>
          </div>

          <el-form 
            :model="emailForm" 
            :rules="emailRules" 
            ref="emailFormRef"
            @keyup.enter="handleSendCode"
          >
            <el-form-item prop="email">
              <el-input
                v-model="emailForm.email"
                placeholder="请输入注册时的邮箱地址"
                size="large"
                prefix-icon="Message"
                class="forgot-input"
              />
            </el-form-item>

            <el-form-item>
              <el-button 
                type="primary" 
                size="large" 
                @click="handleSendCode"
                :loading="loading"
                class="forgot-button"
              >
                {{ loading ? '发送中...' : '发送验证码' }}
              </el-button>
            </el-form-item>
          </el-form>

          <div class="form-footer">
            <p>想起密码了？ 
              <el-button type="text" @click="goToLogin" class="link-button">
                返回登录
              </el-button>
            </p>
          </div>
        </div>

        <!-- 步骤2：输入验证码和新密码 -->
        <div v-else-if="currentStep === 2" class="step-form">
          <div class="form-header">
            <h2>重置密码</h2>
            <p>验证码已发送到 <strong>{{ emailForm.email }}</strong></p>
            <p class="resend-tip">
              <span v-if="countdown > 0">{{ countdown }}秒后可重新发送</span>
              <el-button v-else type="text" @click="handleResendCode" class="link-button">
                重新发送验证码
              </el-button>
            </p>
          </div>

          <el-form 
            :model="resetForm" 
            :rules="resetRules" 
            ref="resetFormRef"
            @keyup.enter="handleResetPassword"
          >
            <el-form-item prop="verificationCode">
              <el-input
                v-model="resetForm.verificationCode"
                placeholder="请输入邮箱收到的验证码"
                size="large"
                prefix-icon="Key"
                class="forgot-input"
              />
            </el-form-item>

            <el-form-item prop="newPassword">
              <el-input
                v-model="resetForm.newPassword"
                type="password"
                placeholder="请输入新密码（6-50字符）"
                size="large"
                prefix-icon="Lock"
                show-password
                class="forgot-input"
              />
            </el-form-item>

            <el-form-item prop="confirmPassword">
              <el-input
                v-model="resetForm.confirmPassword"
                type="password"
                placeholder="请再次确认新密码"
                size="large"
                prefix-icon="Lock"
                show-password
                class="forgot-input"
              />
            </el-form-item>

            <el-form-item>
              <el-button 
                type="primary" 
                size="large" 
                @click="handleResetPassword"
                :loading="loading"
                class="forgot-button"
              >
                {{ loading ? '重置中...' : '重置密码' }}
              </el-button>
            </el-form-item>
          </el-form>

          <div class="form-footer">
            <el-button type="text" @click="goBack" class="link-button">
              返回上一步
            </el-button>
          </div>
        </div>

        <!-- 步骤3：成功提示 -->
        <div v-else-if="currentStep === 3" class="step-form success-step">
          <div class="success-content">
            <el-icon class="success-icon"><CircleCheck /></el-icon>
            <h2>密码重置成功</h2>
            <p>您的密码已成功重置，请使用新密码登录</p>
            
            <el-button 
              type="primary" 
              size="large" 
              @click="goToLogin"
              class="forgot-button"
            >
              返回登录
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 背景装饰 -->
    <div class="bg-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { authApi } from '@/api'
import { Message, Key, Lock, CircleCheck } from '@element-plus/icons-vue'

const router = useRouter()
const loading = ref(false)
const currentStep = ref(1)
const countdown = ref(0)
let countdownTimer = null

const emailFormRef = ref()
const resetFormRef = ref()

const emailForm = reactive({
  email: ''
})

const resetForm = reactive({
  verificationCode: '',
  newPassword: '',
  confirmPassword: ''
})

const emailRules = {
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ]
}

const resetRules = {
  verificationCode: [
    { required: true, message: '请输入验证码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 50, message: '密码长度必须在6-50字符之间', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== resetForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 发送验证码
const handleSendCode = async () => {
  if (!emailFormRef.value) return
  
  await emailFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        await authApi.forgotPassword({ email: emailForm.email })
        ElMessage.success('验证码已发送到您的邮箱')
        currentStep.value = 2
        startCountdown()
      } catch (error) {
        console.error('发送验证码失败:', error)
      } finally {
        loading.value = false
      }
    }
  })
}

// 重新发送验证码
const handleResendCode = async () => {
  if (countdown.value > 0) return
  
  loading.value = true
  try {
    await authApi.forgotPassword({ email: emailForm.email })
    ElMessage.success('验证码已重新发送')
    startCountdown()
  } catch (error) {
    console.error('重新发送验证码失败:', error)
  } finally {
    loading.value = false
  }
}

// 重置密码
const handleResetPassword = async () => {
  if (!resetFormRef.value) return
  
  await resetFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        await authApi.resetPassword({
          email: emailForm.email,
          verificationCode: resetForm.verificationCode,
          newPassword: resetForm.newPassword,
          confirmPassword: resetForm.confirmPassword
        })
        ElMessage.success('密码重置成功')
        currentStep.value = 3
      } catch (error) {
        console.error('重置密码失败:', error)
      } finally {
        loading.value = false
      }
    }
  })
}

// 开始倒计时
const startCountdown = () => {
  countdown.value = 60
  countdownTimer = setInterval(() => {
    countdown.value--
    if (countdown.value <= 0) {
      clearInterval(countdownTimer)
    }
  }, 1000)
}

// 返回上一步
const goBack = () => {
  currentStep.value = 1
  resetForm.verificationCode = ''
  resetForm.newPassword = ''
  resetForm.confirmPassword = ''
}

// 返回登录
const goToLogin = () => {
  router.push('/login')
}

// 清理定时器
onUnmounted(() => {
  if (countdownTimer) {
    clearInterval(countdownTimer)
  }
})
</script>

<style scoped>
.forgot-password-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
  padding: 20px;
}

.forgot-password-wrapper {
  display: flex;
  background: white;
  border-radius: 20px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  width: 900px;
  min-height: 600px;
  position: relative;
  z-index: 2;
}

.forgot-password-banner {
  flex: 1;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  padding: 60px;
}

.banner-content {
  text-align: center;
}

.banner-title {
  font-size: 36px;
  font-weight: bold;
  margin-bottom: 16px;
  color: white;
}

.banner-subtitle {
  font-size: 18px;
  margin-bottom: 40px;
  opacity: 0.9;
}

.banner-features {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 16px;
  opacity: 0.9;
}

.feature-item .el-icon {
  font-size: 20px;
}

.forgot-password-form-container {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px;
}

.step-form {
  width: 100%;
  max-width: 400px;
}

.form-header {
  text-align: center;
  margin-bottom: 40px;
}

.form-header h2 {
  font-size: 28px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 8px;
}

.form-header p {
  color: #6c757d;
  font-size: 14px;
  margin-bottom: 8px;
}

.resend-tip {
  font-size: 12px;
  color: #999;
}

.forgot-input {
  margin-bottom: 20px;
}

.forgot-input :deep(.el-input__inner) {
  height: 50px;
  border-radius: 10px;
  border: 2px solid #e9ecef;
  transition: all 0.3s ease;
}

.forgot-input :deep(.el-input__inner:focus) {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.forgot-button {
  width: 100%;
  height: 50px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 10px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  transition: all 0.3s ease;
}

.forgot-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.3);
}

.form-footer {
  text-align: center;
  margin-top: 30px;
}

.form-footer p {
  color: #6c757d;
  font-size: 14px;
}

.link-button {
  color: #667eea;
  font-weight: 600;
  padding: 0;
}

.link-button:hover {
  color: #764ba2;
}

.success-step {
  text-align: center;
}

.success-content {
  padding: 40px 0;
}

.success-icon {
  font-size: 64px;
  color: #67c23a;
  margin-bottom: 20px;
}

.success-content h2 {
  font-size: 24px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 16px;
}

.success-content p {
  color: #6c757d;
  font-size: 14px;
  margin-bottom: 30px;
}

.bg-decoration {
  position: absolute;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
  z-index: 1;
  pointer-events: none;
}

.circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 6s ease-in-out infinite;
}

.circle-1 {
  width: 100px;
  height: 100px;
  top: 10%;
  left: 10%;
  animation-delay: 0s;
}

.circle-2 {
  width: 150px;
  height: 150px;
  top: 60%;
  right: 10%;
  animation-delay: 2s;
}

.circle-3 {
  width: 80px;
  height: 80px;
  bottom: 20%;
  left: 15%;
  animation-delay: 4s;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-20px);
  }
}

/* 移动端样式 */
@media screen and (max-width: 768px) {
  .forgot-password-wrapper {
    width: 100%;
    min-height: auto;
    flex-direction: column;
  }

  .forgot-password-banner {
    padding: 30px;
    display: none;
  }

  .forgot-password-form-container {
    padding: 30px 20px;
  }

  .step-form {
    max-width: 100%;
  }

  .form-header h2 {
    font-size: 24px;
  }

  .form-header p {
    font-size: 13px;
  }

  .bg-decoration {
    display: none;
  }
}
</style> 