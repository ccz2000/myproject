<template>
  <div class="token-status" v-if="showStatus">
    <el-tooltip :content="tooltipContent" placement="bottom">
      <div class="status-indicator" :class="statusClass">
        <el-icon><Clock /></el-icon>
        <span class="remaining-time">{{ displayTime }}</span>
      </div>
    </el-tooltip>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { Clock } from '@element-plus/icons-vue'
import tokenManager from '@/utils/tokenManager'

const remainingMinutes = ref(0)
const showStatus = ref(false)

// 计算显示的时间
const displayTime = computed(() => {
  if (remainingMinutes.value <= 0) return '过期'
  if (remainingMinutes.value < 60) return `${remainingMinutes.value}分`
  
  const hours = Math.floor(remainingMinutes.value / 60)
  const mins = remainingMinutes.value % 60
  
  if (hours < 24) {
    return mins > 0 ? `${hours}时${mins}分` : `${hours}时`
  }
  
  const days = Math.floor(hours / 24)
  const remainingHours = hours % 24
  return remainingHours > 0 ? `${days}天${remainingHours}时` : `${days}天`
})

// 计算状态样式（适应30分钟token）
const statusClass = computed(() => {
  if (remainingMinutes.value <= 0) return 'expired'
  if (remainingMinutes.value <= 3) return 'critical'  // 3分钟内为危险
  if (remainingMinutes.value <= 10) return 'warning'  // 10分钟内为警告
  return 'normal'
})

// 计算提示内容
const tooltipContent = computed(() => {
  if (remainingMinutes.value <= 0) return 'Token已过期，请重新登录'
  if (remainingMinutes.value <= 3) return 'Token即将过期，系统会自动刷新'
  return `登录有效时间还剩 ${displayTime.value}`
})

let timer = null

// 更新token状态
const updateStatus = () => {
  const token = tokenManager.getToken()
  if (token) {
    showStatus.value = true
    remainingMinutes.value = tokenManager.getTokenRemainingMinutes()
  } else {
    showStatus.value = false
  }
}

// 生命周期
onMounted(() => {
  updateStatus()
  // 每分钟更新一次状态
  timer = setInterval(updateStatus, 60000)
})

onUnmounted(() => {
  if (timer) {
    clearInterval(timer)
  }
})
</script>

<style scoped>
.token-status {
  display: inline-flex;
  align-items: center;
}

.status-indicator {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.status-indicator.normal {
  background: #f0f9ff;
  color: #1d4ed8;
  border: 1px solid #dbeafe;
}

.status-indicator.warning {
  background: #fffbeb;
  color: #d97706;
  border: 1px solid #fed7aa;
}

.status-indicator.critical {
  background: #fef2f2;
  color: #dc2626;
  border: 1px solid #fecaca;
  animation: pulse 2s infinite;
}

.status-indicator.expired {
  background: #f3f4f6;
  color: #6b7280;
  border: 1px solid #d1d5db;
}

.remaining-time {
  white-space: nowrap;
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}
</style> 