<template>
  <div class="email-verification-container">
    <div class="verification-wrapper">
      <div class="verification-card">
        <div class="card-header">
          <div class="icon-wrapper">
            <el-icon size="60" color="#667eea"><Message /></el-icon>
          </div>
          <h2>邮箱验证</h2>
          <p class="subtitle">我们已向您的邮箱发送了验证码</p>
        </div>

        <div class="card-content">
          <div class="email-info">
            <p>验证码已发送至：</p>
            <p class="email-address">{{ email }}</p>
          </div>

          <el-form :model="verificationForm" :rules="verificationRules" ref="verificationFormRef">
            <el-form-item prop="verificationCode">
              <el-input
                v-model="verificationForm.verificationCode"
                placeholder="请输入6位验证码"
                size="large"
                maxlength="6"
                class="verification-input"
                @input="handleCodeInput"
              >
                <template #prefix>
                  <el-icon><Key /></el-icon>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item>
              <el-button 
                type="primary" 
                size="large" 
                @click="handleVerification"
                :loading="loading"
                :disabled="verificationForm.verificationCode.length !== 6"
                class="verify-button"
              >
                <el-icon v-if="!loading"><Check /></el-icon>
                {{ loading ? '验证中...' : '验证邮箱' }}
              </el-button>
            </el-form-item>
          </el-form>

          <div class="resend-section">
            <p class="resend-text">
              没有收到验证码？
              <el-button 
                type="text" 
                @click="handleResend"
                :loading="resendLoading"
                :disabled="countdown > 0"
                class="resend-button"
              >
                <span v-if="countdown > 0">{{ countdown }}秒后重新发送</span>
                <span v-else>{{ resendLoading ? '发送中...' : '重新发送' }}</span>
              </el-button>
            </p>
          </div>

          <div class="tips">
            <el-alert
              title="提示"
              type="info"
              :closable="false"
              show-icon
            >
              <template #default>
                <ul>
                  <li>验证码有效期为30分钟</li>
                  <li>请检查您的垃圾邮件文件夹</li>
                  <li>验证成功后即可正常登录系统</li>
                </ul>
              </template>
            </el-alert>
          </div>
        </div>

        <div class="card-footer">
          <el-button @click="goToLogin" class="back-button">
            <el-icon><ArrowLeft /></el-icon>
            返回登录
          </el-button>
        </div>
      </div>
    </div>

    <!-- 成功提示弹窗 -->
    <el-dialog
      v-model="successDialogVisible"
      title="验证成功"
      width="400px"
      :show-close="false"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
    >
      <div class="success-content">
        <el-icon size="60" color="#67C23A" class="success-icon"><CircleCheck /></el-icon>
        <p>邮箱验证成功！</p>
        <p class="success-subtitle">您现在可以使用账户登录系统了</p>
      </div>
      <template #footer>
        <el-button type="primary" @click="goToLoginAfterSuccess">
          立即登录
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { authApi } from '@/api'

const router = useRouter()
const route = useRoute()

const loading = ref(false)
const resendLoading = ref(false)
const countdown = ref(0)
const email = ref('')
const successDialogVisible = ref(false)
const verificationFormRef = ref()

const verificationForm = reactive({
  verificationCode: ''
})

const verificationRules = {
  verificationCode: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { len: 6, message: '验证码长度为6位', trigger: 'blur' },
    { pattern: /^\d{6}$/, message: '验证码只能是数字', trigger: 'blur' }
  ]
}

const handleCodeInput = (value) => {
  // 只允许输入数字
  verificationForm.verificationCode = value.replace(/[^\d]/g, '')
}

const handleVerification = async () => {
  if (!verificationFormRef.value) return
  
  await verificationFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        await authApi.verifyEmail(email.value, verificationForm.verificationCode)
        successDialogVisible.value = true
      } catch (error) {
        console.error('邮箱验证失败:', error)
      } finally {
        loading.value = false
      }
    }
  })
}

const handleResend = async () => {
  resendLoading.value = true
  try {
    await authApi.resendVerification(email.value)
    ElMessage.success('验证邮件已重新发送，请查收')
    startCountdown()
  } catch (error) {
    console.error('重新发送失败:', error)
  } finally {
    resendLoading.value = false
  }
}

const startCountdown = () => {
  countdown.value = 60
  const timer = setInterval(() => {
    countdown.value--
    if (countdown.value <= 0) {
      clearInterval(timer)
    }
  }, 1000)
}

const goToLogin = () => {
  router.push('/login')
}

const goToLoginAfterSuccess = () => {
  successDialogVisible.value = false
  router.push('/login')
}

onMounted(() => {
  // 从URL参数获取邮箱地址
  email.value = route.query.email || ''
  if (!email.value) {
    ElMessage.warning('缺少邮箱参数，请重新注册')
    router.push('/register')
  }
  
  // 开始倒计时
  startCountdown()
})
</script>

<style scoped>
.email-verification-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.verification-wrapper {
  width: 100%;
  max-width: 500px;
}

.verification-card {
  background: white;
  border-radius: 20px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.card-header {
  text-align: center;
  padding: 40px 40px 20px;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
}

.icon-wrapper {
  margin-bottom: 20px;
}

.card-header h2 {
  font-size: 28px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 8px;
}

.subtitle {
  color: #6c757d;
  font-size: 16px;
  margin: 0;
}

.card-content {
  padding: 40px;
}

.email-info {
  text-align: center;
  margin-bottom: 30px;
}

.email-info p {
  margin: 0;
  color: #6c757d;
}

.email-address {
  font-size: 18px;
  font-weight: 600;
  color: #667eea !important;
  margin-top: 8px !important;
}

.verification-input {
  margin-bottom: 20px;
}

.verification-input :deep(.el-input__inner) {
  height: 60px;
  font-size: 18px;
  text-align: center;
  letter-spacing: 8px;
  border-radius: 12px;
  border: 2px solid #e9ecef;
  transition: all 0.3s ease;
}

.verification-input :deep(.el-input__inner:focus) {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.verify-button {
  width: 100%;
  height: 50px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 10px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  transition: all 0.3s ease;
}

.verify-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.3);
}

.verify-button:disabled {
  transform: none;
  box-shadow: none;
  opacity: 0.6;
}

.resend-section {
  text-align: center;
  margin: 30px 0;
}

.resend-text {
  color: #6c757d;
  font-size: 14px;
  margin: 0;
}

.resend-button {
  color: #667eea;
  font-weight: 600;
  padding: 0;
  margin-left: 8px;
}

.resend-button:hover {
  color: #764ba2;
}

.resend-button:disabled {
  color: #adb5bd;
}

.tips {
  margin-top: 30px;
}

.tips :deep(.el-alert__content) {
  padding-left: 0;
}

.tips ul {
  margin: 0;
  padding-left: 16px;
}

.tips li {
  margin-bottom: 5px;
  color: #6c757d;
  font-size: 14px;
}

.card-footer {
  padding: 20px 40px 40px;
  text-align: center;
}

.back-button {
  color: #6c757d;
  border-color: #e9ecef;
}

.back-button:hover {
  color: #667eea;
  border-color: #667eea;
}

.success-content {
  text-align: center;
  padding: 20px 0;
}

.success-icon {
  margin-bottom: 20px;
}

.success-content p {
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 8px;
}

.success-subtitle {
  font-size: 14px !important;
  font-weight: normal !important;
  color: #6c757d !important;
}

/* 移动端样式 */
@media screen and (max-width: 768px) {
  .email-verification-container {
    padding: 15px;
  }

  .verification-wrapper {
    max-width: 100%;
  }

  .verification-card {
    border-radius: 15px;
  }

  .card-header {
    padding: 30px 20px 15px;
  }

  .icon-wrapper {
    margin-bottom: 15px;
  }

  .card-header h2 {
    font-size: 24px;
  }

  .subtitle {
    font-size: 14px;
  }

  .card-content {
    padding: 20px;
  }

  .email-info {
    margin-bottom: 20px;
  }

  .email-address {
    font-size: 14px;
    word-break: break-all;
  }

  .verification-input {
    width: 100%;
  }

  .verify-button {
    width: 100%;
  }

  .resend-section {
    margin: 20px 0;
  }

  .resend-text {
    font-size: 13px;
  }

  .tips {
    margin-top: 20px;
  }

  .tips :deep(.el-alert) {
    padding: 10px;
  }

  .tips :deep(.el-alert__title) {
    font-size: 14px;
  }

  .tips :deep(.el-alert__content) {
    font-size: 13px;
  }

  .card-footer {
    padding: 15px 20px;
  }

  .back-button {
    width: 100%;
  }
}

/* 平板设备样式 */
@media screen and (min-width: 769px) and (max-width: 1024px) {
  .verification-wrapper {
    max-width: 450px;
  }

  .card-header {
    padding: 35px 35px 15px;
  }

  .card-content {
    padding: 30px;
  }
}
</style> 