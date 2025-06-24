<template>
  <div class="register-container">
    <div class="register-wrapper">
      <!-- 左侧装饰 -->
      <div class="register-banner">
        <div class="banner-content">
          <h1 class="banner-title">加入我们</h1>
          <p class="banner-subtitle">开始您的部门管理之旅</p>
          <div class="banner-features">
            <div class="feature-item">
              <el-icon><Shield /></el-icon>
              <span>安全可靠</span>
            </div>
            <div class="feature-item">
              <el-icon><Star /></el-icon>
              <span>功能强大</span>
            </div>
            <div class="feature-item">
              <el-icon><Lightning /></el-icon>
              <span>高效便捷</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧注册表单 -->
      <div class="register-form-container">
        <div class="register-form">
          <div class="form-header">
            <h2>创建账户</h2>
            <p>请填写以下信息完成注册</p>
          </div>

          <el-form 
            :model="registerForm" 
            :rules="registerRules" 
            ref="registerFormRef"
          >
            <el-form-item prop="username">
              <el-input
                v-model="registerForm.username"
                placeholder="请输入用户名（3-50字符）"
                size="large"
                prefix-icon="User"
                class="register-input"
                @blur="checkUsernameAvailable"
              />
              <div v-if="usernameStatus.show" class="field-status">
                <el-icon v-if="usernameStatus.available" class="success-icon"><Check /></el-icon>
                <el-icon v-else class="error-icon"><Close /></el-icon>
                <span :class="{ 'success-text': usernameStatus.available, 'error-text': !usernameStatus.available }">
                  {{ usernameStatus.message }}
                </span>
              </div>
            </el-form-item>

            <el-form-item prop="email">
              <el-input
                v-model="registerForm.email"
                placeholder="请输入邮箱地址"
                size="large"
                prefix-icon="Message"
                class="register-input"
                @blur="checkEmailAvailable"
              />
              <div v-if="emailStatus.show" class="field-status">
                <el-icon v-if="emailStatus.available" class="success-icon"><Check /></el-icon>
                <el-icon v-else class="error-icon"><Close /></el-icon>
                <span :class="{ 'success-text': emailStatus.available, 'error-text': !emailStatus.available }">
                  {{ emailStatus.message }}
                </span>
              </div>
            </el-form-item>

            <el-form-item prop="password">
              <el-input
                v-model="registerForm.password"
                type="password"
                placeholder="请输入密码（6-50字符）"
                size="large"
                prefix-icon="Lock"
                show-password
                class="register-input"
              />
            </el-form-item>

            <el-form-item prop="confirmPassword">
              <el-input
                v-model="registerForm.confirmPassword"
                type="password"
                placeholder="请确认密码"
                size="large"
                prefix-icon="Lock"
                show-password
                class="register-input"
              />
            </el-form-item>

            <el-form-item>
              <el-button 
                type="primary" 
                size="large" 
                @click="handleRegister"
                :loading="loading"
                class="register-button"
              >
                <el-icon v-if="!loading"><UserFilled /></el-icon>
                {{ loading ? '注册中...' : '注册账户' }}
              </el-button>
            </el-form-item>
          </el-form>

          <div class="form-footer">
            <p>已有账户？ 
              <el-button type="text" @click="goToLogin" class="link-button">
                立即登录
              </el-button>
            </p>
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
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { authApi } from '@/api'

const router = useRouter()
const loading = ref(false)
const registerFormRef = ref()

const registerForm = reactive({
  username: '',
  email: '',
  password: '',
  confirmPassword: ''
})

const usernameStatus = reactive({
  show: false,
  available: false,
  message: ''
})

const emailStatus = reactive({
  show: false,
  available: false,
  message: ''
})

// 自定义验证规则
const validateConfirmPassword = (rule, value, callback) => {
  if (value !== registerForm.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 50, message: '用户名长度在3-50字符之间', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 50, message: '密码长度在6-50字符之间', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const checkUsernameAvailable = async () => {
  if (!registerForm.username || registerForm.username.length < 3) {
    usernameStatus.show = false
    return
  }
  
  try {
    const available = await authApi.checkUsername(registerForm.username)
    usernameStatus.show = true
    usernameStatus.available = available
    usernameStatus.message = available ? '用户名可用' : '用户名已被使用'
  } catch (error) {
    usernameStatus.show = false
  }
}

const checkEmailAvailable = async () => {
  if (!registerForm.email || !/\S+@\S+\.\S+/.test(registerForm.email)) {
    emailStatus.show = false
    return
  }
  
  try {
    const available = await authApi.checkEmail(registerForm.email)
    emailStatus.show = true
    emailStatus.available = available
    emailStatus.message = available ? '邮箱可用' : '邮箱已被注册'
  } catch (error) {
    emailStatus.show = false
  }
}

const handleRegister = async () => {
  if (!registerFormRef.value) return
  
  await registerFormRef.value.validate(async (valid) => {
    if (valid) {
      if (!usernameStatus.available && usernameStatus.show) {
        ElMessage.warning('用户名已被使用，请更换')
        return
      }
      
      if (!emailStatus.available && emailStatus.show) {
        ElMessage.warning('邮箱已被注册，请更换')
        return
      }
      
      loading.value = true
      try {
        await authApi.register(registerForm)
        ElMessage.success('注册成功！请前往邮箱验证页面完成验证')
        router.push({
          path: '/verify',
          query: { email: registerForm.email }
        })
      } catch (error) {
        console.error('注册失败:', error)
      } finally {
        loading.value = false
      }
    }
  })
}

const goToLogin = () => {
  router.push('/login')
}
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
  padding: 20px;
}

.register-wrapper {
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

.register-banner {
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

.register-form-container {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px;
}

.register-form {
  width: 100%;
  max-width: 450px;
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

.register-input {
  margin-bottom: 20px;
}

.register-input :deep(.el-input__inner) {
  height: 50px;
  border-radius: 10px;
  border: 2px solid #e9ecef;
  transition: all 0.3s ease;
}

.register-input :deep(.el-input__inner:focus) {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.field-status {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: 5px;
  font-size: 12px;
}

.success-icon {
  color: #67C23A;
}

.error-icon {
  color: #F56C6C;
}

.success-text {
  color: #67C23A;
}

.error-text {
  color: #F56C6C;
}

.register-button {
  width: 100%;
  height: 50px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 10px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  transition: all 0.3s ease;
}

.register-button:hover {
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
  width: 120px;
  height: 120px;
  top: 15%;
  left: 8%;
  animation-delay: 0s;
}

.circle-2 {
  width: 180px;
  height: 180px;
  top: 50%;
  right: 5%;
  animation-delay: 2s;
}

.circle-3 {
  width: 90px;
  height: 90px;
  bottom: 15%;
  left: 12%;
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
  .register-wrapper {
    width: 100%;
    min-height: auto;
    flex-direction: column;
  }

  .register-banner {
    padding: 30px;
    display: none; /* 在移动端隐藏左侧装饰 */
  }

  .register-form-container {
    padding: 30px 20px;
  }

  .register-form {
    max-width: 100%;
  }

  .form-header h2 {
    font-size: 24px;
  }

  .form-header p {
    font-size: 13px;
  }

  .register-input {
    width: 100%;
  }

  .register-button {
    width: 100%;
  }

  .field-status {
    font-size: 12px;
    margin-top: 4px;
  }

  .bg-decoration {
    display: none; /* 在移动端隐藏背景装饰 */
  }

  .form-footer {
    margin-top: 20px;
    text-align: center;
  }
}

/* 平板设备样式 */
@media screen and (min-width: 769px) and (max-width: 1024px) {
  .register-wrapper {
    width: 90%;
    min-height: 500px;
  }

  .register-banner {
    padding: 40px;
  }

  .banner-title {
    font-size: 28px;
  }

  .banner-subtitle {
    font-size: 16px;
  }

  .register-form-container {
    padding: 40px;
  }
}
</style> 