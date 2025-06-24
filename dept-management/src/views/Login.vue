<template>
  <div class="login-container">
    <div class="login-wrapper">
      <!-- 左侧装饰 -->
      <div class="login-banner">
        <div class="banner-content">
          <h1 class="banner-title">企业管理系统</h1>
          <p class="banner-subtitle">高效管理您的企业组织架构</p>
          <div class="banner-features">
            <div class="feature-item">
              <el-icon><OfficeBuilding /></el-icon>
              <span>部门管理</span>
            </div>
            <div class="feature-item">
              <el-icon><User /></el-icon>
              <span>员工管理</span>
            </div>
            <div class="feature-item">
              <el-icon><Folder /></el-icon>
              <span>项目管理</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧登录表单 -->
      <div class="login-form-container">
        <div class="login-form">
          <div class="form-header">
            <h2>登录账户</h2>
            <p>欢迎回来，请输入您的登录信息</p>
          </div>

          <el-form 
            :model="loginForm" 
            :rules="loginRules" 
            ref="loginFormRef"
            @keyup.enter="handleLogin"
          >
            <el-form-item prop="loginField">
              <el-input
                v-model="loginForm.loginField"
                placeholder="请输入用户名或邮箱"
                size="large"
                prefix-icon="User"
                class="login-input"
              />
            </el-form-item>

            <el-form-item prop="password">
              <el-input
                v-model="loginForm.password"
                type="password"
                placeholder="请输入密码"
                size="large"
                prefix-icon="Lock"
                show-password
                class="login-input"
              />
            </el-form-item>

            <el-form-item prop="captcha">
              <div class="captcha-container">
                <el-input
                  v-model="loginForm.captcha"
                  placeholder="请输入验证码"
                  size="large"
                  prefix-icon="Key"
                  class="captcha-input"
                />
                <div class="captcha-image" @click="refreshCaptcha">
                  <img v-if="captchaImage" :src="captchaImage" alt="验证码" />
                  <div v-else class="captcha-loading">加载中...</div>
                </div>
              </div>
            </el-form-item>

            <el-form-item>
              <el-button 
                type="primary" 
                size="large" 
                @click="handleLogin"
                :loading="loading"
                class="login-button"
              >
                <el-icon v-if="!loading"><Right /></el-icon>
                {{ loading ? '登录中...' : '登录' }}
              </el-button>
            </el-form-item>
          </el-form>

          <div class="form-footer">
            <div class="footer-links">
              <el-button type="text" @click="showForgotPassword" class="link-button">
                忘记密码？
              </el-button>
            </div>
            <p>还没有账户？ 
              <el-button type="text" @click="goToRegister" class="link-button">
                立即注册
              </el-button>
            </p>
          </div>
        </div>
      </div>
    </div>

    <!-- 忘记密码对话框 -->
    <el-dialog 
      title="忘记密码" 
      v-model="forgotPasswordVisible" 
      width="450px"
      :close-on-click-modal="false"
      class="forgot-password-dialog"
    >
      <el-form :model="forgotPasswordForm" :rules="forgotPasswordRules" ref="forgotPasswordRef">
        <el-form-item label="邮箱地址" prop="email">
          <el-input
            v-model="forgotPasswordForm.email"
            placeholder="请输入注册时的邮箱地址"
            size="large"
            prefix-icon="Message"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="forgotPasswordVisible = false">取消</el-button>
        <el-button type="primary" @click="handleForgotPassword" :loading="forgotPasswordLoading">
          发送重置邮件
        </el-button>
      </template>
    </el-dialog>

    <!-- 重置密码对话框 -->
    <el-dialog 
      title="重置密码" 
      v-model="resetPasswordVisible" 
      width="450px"
      :close-on-click-modal="false"
      class="reset-password-dialog"
    >
      <el-form :model="resetPasswordForm" :rules="resetPasswordRules" ref="resetPasswordRef">
        <el-form-item label="邮箱地址" prop="email">
          <el-input
            v-model="resetPasswordForm.email"
            placeholder="请输入邮箱地址"
            size="large"
            prefix-icon="Message"
            :disabled="true"
          />
        </el-form-item>
        
        <el-form-item label="验证码" prop="verificationCode">
          <el-input
            v-model="resetPasswordForm.verificationCode"
            placeholder="请输入邮箱收到的验证码"
            size="large"
            prefix-icon="Key"
          />
        </el-form-item>
        
        <el-form-item label="新密码" prop="newPassword">
          <el-input
            v-model="resetPasswordForm.newPassword"
            type="password"
            placeholder="请输入新密码（6-50字符）"
            size="large"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="resetPasswordForm.confirmPassword"
            type="password"
            placeholder="请再次输入新密码"
            size="large"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="resetPasswordVisible = false">取消</el-button>
        <el-button type="primary" @click="handleResetPassword" :loading="resetPasswordLoading">
          重置密码
        </el-button>
      </template>
    </el-dialog>

    <!-- 背景装饰 -->
    <div class="bg-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { authApi } from '@/api'
import tokenManager from '@/utils/tokenManager'

const router = useRouter()
const loading = ref(false)
const loginFormRef = ref()

const captchaImage = ref('')
const sessionId = ref('')
const forgotPasswordVisible = ref(false)
const resetPasswordVisible = ref(false)
const forgotPasswordLoading = ref(false)
const resetPasswordLoading = ref(false)
const forgotPasswordRef = ref()
const resetPasswordRef = ref()

const loginForm = reactive({
  loginField: '',
  password: '',
  captcha: '',
  sessionId: ''
})

const forgotPasswordForm = reactive({
  email: ''
})

const resetPasswordForm = reactive({
  email: '',
  verificationCode: '',
  newPassword: '',
  confirmPassword: ''
})

const loginRules = {
  loginField: [
    { required: true, message: '请输入用户名或邮箱', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少6位', trigger: 'blur' }
  ],
  captcha: [
    { required: true, message: '请输入验证码', trigger: 'blur' }
  ]
}

const forgotPasswordRules = {
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ]
}

const resetPasswordRules = {
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
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
        if (value !== resetPasswordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 生成会话ID
const generateSessionId = () => {
  return 'login_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9)
}

// 加载验证码
const loadCaptcha = async () => {
  try {
    sessionId.value = generateSessionId()
    loginForm.sessionId = sessionId.value
    
    const response = await authApi.getCaptcha(sessionId.value)
    captchaImage.value = response.imageBase64
  } catch (error) {
    console.error('加载验证码失败:', error)
    ElMessage.error('验证码加载失败')
  }
}

// 刷新验证码
const refreshCaptcha = () => {
  loadCaptcha()
}

const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const response = await authApi.login(loginForm)
        
        // 使用token管理器保存token信息
        tokenManager.setTokens(response)
        
        ElMessage.success('登录成功')
        
        // 跳转到企业管理系统首页
        router.push('/system/home')
      } catch (error) {
        console.error('登录失败:', error)
        // 登录失败后刷新验证码
        refreshCaptcha()
        loginForm.captcha = ''
      } finally {
        loading.value = false
      }
    }
  })
}

// 显示忘记密码对话框
const showForgotPassword = () => {
  // 跳转到独立的忘记密码页面
  router.push('/system/forgot-password')
}

// 处理忘记密码
const handleForgotPassword = async () => {
  if (!forgotPasswordRef.value) return
  
  await forgotPasswordRef.value.validate(async (valid) => {
    if (valid) {
      forgotPasswordLoading.value = true
      try {
        await authApi.forgotPassword({ email: forgotPasswordForm.email })
        ElMessage.success('重置密码邮件已发送，请查收')
        forgotPasswordVisible.value = false
        
        // 显示重置密码对话框
        resetPasswordForm.email = forgotPasswordForm.email
        resetPasswordForm.verificationCode = ''
        resetPasswordForm.newPassword = ''
        resetPasswordForm.confirmPassword = ''
        resetPasswordVisible.value = true
      } catch (error) {
        console.error('发送重置邮件失败:', error)
      } finally {
        forgotPasswordLoading.value = false
      }
    }
  })
}

// 处理重置密码
const handleResetPassword = async () => {
  if (!resetPasswordRef.value) return
  
  await resetPasswordRef.value.validate(async (valid) => {
    if (valid) {
      resetPasswordLoading.value = true
      try {
        await authApi.resetPassword(resetPasswordForm)
        ElMessage.success('密码重置成功，请使用新密码登录')
        resetPasswordVisible.value = false
        
        // 清空登录表单
        loginForm.loginField = resetPasswordForm.email
        loginForm.password = ''
        loginForm.captcha = ''
        refreshCaptcha()
      } catch (error) {
        console.error('重置密码失败:', error)
      } finally {
        resetPasswordLoading.value = false
      }
    }
  })
}

const goToRegister = () => {
  router.push('/system/register')
}

// 初始化
onMounted(() => {
  loadCaptcha()
})
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
  padding: 20px;
}

.login-wrapper {
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

.login-banner {
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

.login-form-container {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px;
}

.login-form {
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
}

.login-input {
  margin-bottom: 20px;
}

.login-input :deep(.el-input__inner) {
  height: 50px;
  border-radius: 10px;
  border: 2px solid #e9ecef;
  transition: all 0.3s ease;
}

.login-input :deep(.el-input__inner:focus) {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.login-button {
  width: 100%;
  height: 50px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 10px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  transition: all 0.3s ease;
}

.login-button:hover {
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

/* 验证码相关样式 */
.captcha-container {
  display: flex;
  gap: 12px;
  align-items: center;
}

.captcha-input {
  flex: 1;
}

.captcha-image {
  width: 120px;
  height: 40px;
  border: 2px solid #e9ecef;
  border-radius: 6px;
  cursor: pointer;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.captcha-image:hover {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.captcha-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.captcha-loading {
  font-size: 12px;
  color: #999;
}

/* 忘记密码链接 */
.footer-links {
  text-align: center;
  margin-bottom: 16px;
}

/* 对话框样式 */
.forgot-password-dialog :deep(.el-dialog) {
  border-radius: 12px;
}

.reset-password-dialog :deep(.el-dialog) {
  border-radius: 12px;
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
  .login-wrapper {
    width: 100%;
    min-height: auto;
    flex-direction: column;
  }

  .login-banner {
    padding: 30px;
    display: none; /* 在移动端隐藏左侧装饰 */
  }

  .login-form-container {
    padding: 30px 20px;
  }

  .login-form {
    max-width: 100%;
  }

  .form-header h2 {
    font-size: 24px;
  }

  .form-header p {
    font-size: 13px;
  }

  .login-input {
    width: 100%;
  }

  .login-button {
    width: 100%;
  }

  .bg-decoration {
    display: none; /* 在移动端隐藏背景装饰 */
  }
}

/* 平板设备样式 */
@media screen and (min-width: 769px) and (max-width: 1024px) {
  .login-wrapper {
    width: 90%;
    min-height: 500px;
  }

  .login-banner {
    padding: 40px;
  }

  .banner-title {
    font-size: 28px;
  }

  .banner-subtitle {
    font-size: 16px;
  }
}
</style> 