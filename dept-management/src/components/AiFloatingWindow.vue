<template>
  <div v-show="visible" class="ai-floating-window" 
       :style="windowStyle" 
       @mousedown="onMouseDown"
       ref="floatingWindow">
    
    <!-- 悬浮窗头部 -->
    <div class="floating-header" @mousedown.stop="startDrag">
      <div class="header-left">
        <el-icon class="ai-icon"><ChatDotRound /></el-icon>
        <span class="ai-title">AI助手</span>
        <el-tag v-if="isConnected" type="success" size="small" effect="plain">在线</el-tag>
        <el-tag v-else type="danger" size="small" effect="plain">离线</el-tag>
      </div>
      <div class="header-actions">
        <el-button 
          type="text" 
          size="small" 
          @click="toggleMinimize"
          :icon="isMinimized ? 'ArrowUp' : 'ArrowDown'"
        />
        <el-button 
          type="text" 
          size="small" 
          @click="closeWindow"
          icon="Close"
        />
      </div>
    </div>

    <!-- 悬浮窗内容区 -->
    <div v-show="!isMinimized" class="floating-content">
      <!-- 聊天消息区域 -->
      <div class="chat-messages" ref="messagesContainer">
        <!-- 欢迎消息 -->
        <div class="message-item bot-message" v-if="messages.length === 0">
          <div class="message-avatar">
            <el-icon size="16"><ChatDotRound /></el-icon>
          </div>
          <div class="message-content">
            <div class="message-bubble">
              <div class="message-text">
                👋 我是AI助手！<br>
                可以为您解答问题和编写代码
              </div>
            </div>
          </div>
        </div>

        <!-- 消息列表 -->
        <div v-for="(message, index) in messages" :key="index" 
             class="message-item" :class="message.type + '-message'">
          <div class="message-avatar">
            <el-icon size="16">
              <User v-if="message.type === 'user'" />
              <ChatDotRound v-else />
            </el-icon>
          </div>
          <div class="message-content">
            <div class="message-bubble">
              <div class="message-text" 
                   v-if="message.type === 'user'" 
                   v-text="message.content"></div>
              <div class="message-text markdown-content" 
                   v-else 
                   v-html="renderMessageContent(message.content)"></div>
            </div>
          </div>
        </div>

        <!-- 加载状态 -->
        <div v-if="isLoading" class="message-item bot-message">
          <div class="message-avatar">
            <el-icon size="16"><ChatDotRound /></el-icon>
          </div>
          <div class="message-content">
            <div class="message-bubble">
              <div class="typing-indicator">思考中...</div>
            </div>
          </div>
        </div>
      </div>

      <!-- 输入区域 -->
      <div class="chat-input">
        <div class="input-row">
          <el-input
            v-model="inputMessage"
            placeholder="输入问题..."
            @keydown.enter.exact.prevent="sendMessage"
            :disabled="isLoading"
            size="small"
            maxlength="500"
          />
          <el-button 
            type="primary" 
            size="small"
            @click="sendMessage"
            :loading="isLoading"
            :disabled="!inputMessage.trim()"
          >
            发送
          </el-button>
        </div>
        <div class="input-actions">
          <el-button type="text" size="small" @click="clearMessages">清空</el-button>
          <el-button type="text" size="small" @click="openFullScreen">全屏</el-button>
        </div>
      </div>
    </div>
  </div>

  <!-- 悬浮按钮（当窗口关闭时显示） -->
  <div v-show="!visible" class="floating-button" @click="openWindow">
    <el-icon size="24"><ChatDotRound /></el-icon>
    <span class="button-text">AI</span>
  </div>
</template>

<script setup>
import { ref, reactive, computed, nextTick, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ChatDotRound, User } from '@element-plus/icons-vue'
import { aiApi } from '@/api/ai'
import { renderMarkdown } from '@/utils/markdown'

const router = useRouter()

// 响应式数据
const visible = ref(false)
const isMinimized = ref(false)
const isDragging = ref(false)
const isConnected = ref(false)
const isLoading = ref(false)
const messages = ref([])
const inputMessage = ref('')
const messagesContainer = ref(null)
const floatingWindow = ref(null)

// 窗口位置和拖拽相关
const windowPosition = reactive({
  x: window.innerWidth - 350,
  y: 100
})

const dragStart = reactive({
  x: 0,
  y: 0,
  windowX: 0,
  windowY: 0
})

// 计算窗口样式
const windowStyle = computed(() => ({
  left: `${windowPosition.x}px`,
  top: `${windowPosition.y}px`,
  zIndex: 9999
}))

// 会话ID
const sessionId = aiApi.generateSessionId()

// 生命周期
onMounted(() => {
  // 初始位置调整
  adjustPosition()
  window.addEventListener('resize', adjustPosition)
  
  // 延迟检查状态，确保用户已完全登录
  setTimeout(() => {
    checkStatus()
  }, 1000)
})

onUnmounted(() => {
  window.removeEventListener('resize', adjustPosition)
})

// 方法
const openWindow = () => {
  visible.value = true
  isMinimized.value = false
}

const closeWindow = () => {
  visible.value = false
}

const toggleMinimize = () => {
  isMinimized.value = !isMinimized.value
}

const openFullScreen = () => {
  router.push('/ai-chat')
  closeWindow()
}

const adjustPosition = () => {
  const maxX = window.innerWidth - 320
  const maxY = window.innerHeight - 100
  
  if (windowPosition.x > maxX) windowPosition.x = maxX
  if (windowPosition.y > maxY) windowPosition.y = maxY
  if (windowPosition.x < 0) windowPosition.x = 0
  if (windowPosition.y < 0) windowPosition.y = 0
}

// 拖拽功能
const startDrag = (event) => {
  isDragging.value = true
  dragStart.x = event.clientX
  dragStart.y = event.clientY
  dragStart.windowX = windowPosition.x
  dragStart.windowY = windowPosition.y
  
  document.addEventListener('mousemove', onDrag)
  document.addEventListener('mouseup', stopDrag)
  event.preventDefault()
}

const onDrag = (event) => {
  if (!isDragging.value) return
  
  const deltaX = event.clientX - dragStart.x
  const deltaY = event.clientY - dragStart.y
  
  windowPosition.x = dragStart.windowX + deltaX
  windowPosition.y = dragStart.windowY + deltaY
  
  adjustPosition()
}

const stopDrag = () => {
  isDragging.value = false
  document.removeEventListener('mousemove', onDrag)
  document.removeEventListener('mouseup', stopDrag)
}

const onMouseDown = (event) => {
  // 将窗口置于最前
  if (floatingWindow.value) {
    floatingWindow.value.style.zIndex = 10000
  }
}

// AI聊天功能
const sendMessage = async () => {
  const question = inputMessage.value.trim()
  if (!question || isLoading.value) return

  addMessage('user', question)
  inputMessage.value = ''
  isLoading.value = true

  try {
    const response = await aiApi.chat(question, sessionId)
    
    if (response.success && response.data.success) {
      addMessage('bot', response.data.answer)
    } else {
      const errorMsg = response.data?.error || response.error || '未知错误'
      addMessage('bot', `❌ 抱歉，出现错误：${errorMsg}`)
    }
  } catch (error) {
    addMessage('bot', '❌ 网络连接异常，请稍后重试')
  } finally {
    isLoading.value = false
    scrollToBottom()
  }
}

const addMessage = (type, content) => {
  messages.value.push({
    type,
    content,
    timestamp: new Date()
  })
  nextTick(scrollToBottom)
}

const clearMessages = () => {
  ElMessageBox.confirm('确定要清空聊天记录吗？', '确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    messages.value = []
    ElMessage.success('已清空')
  })
}

const checkStatus = async () => {
  try {
    const response = await aiApi.checkStatus()
    isConnected.value = response.success && response.data.available
  } catch (error) {
    isConnected.value = false
  }
}

const scrollToBottom = () => {
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}

const renderMessageContent = (content) => {
  return renderMarkdown(content)
}

// 暴露给父组件的方法
defineExpose({
  openWindow,
  closeWindow,
  toggleMinimize
})
</script>

<style scoped>
.ai-floating-window {
  position: fixed;
  width: 320px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
  border: 1px solid #e0e0e0;
  overflow: hidden;
  user-select: none;
  transition: all 0.3s ease;
}

.floating-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 8px 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  cursor: move;
  min-height: 40px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 6px;
}

.ai-icon {
  font-size: 16px;
}

.ai-title {
  font-size: 14px;
  font-weight: 600;
}

.header-actions {
  display: flex;
  gap: 4px;
}

.floating-content {
  height: 400px;
  display: flex;
  flex-direction: column;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 8px;
  background: #fafafa;
}

.message-item {
  display: flex;
  margin-bottom: 8px;
  font-size: 12px;
}

.user-message {
  flex-direction: row-reverse;
}

.message-avatar {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: #409eff;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  margin: 0 6px;
  flex-shrink: 0;
}

.user-message .message-avatar {
  background: #67c23a;
}

.message-content {
  max-width: 70%;
}

.message-bubble {
  background: white;
  border-radius: 8px;
  padding: 6px 10px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

.user-message .message-bubble {
  background: #409eff;
  color: white;
}

.message-text {
  line-height: 1.4;
  word-wrap: break-word;
  font-size: 12px;
}

.chat-input {
  padding: 8px;
  background: white;
  border-top: 1px solid #e0e0e0;
}

.input-row {
  display: flex;
  gap: 6px;
  margin-bottom: 6px;
}

.input-row .el-input {
  flex: 1;
}

.input-actions {
  display: flex;
  justify-content: space-between;
}

.typing-indicator {
  color: #409eff;
  font-style: italic;
  font-size: 11px;
}

.floating-button {
  position: fixed;
  bottom: 30px;
  right: 30px;
  width: 60px;
  height: 60px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: white;
  cursor: pointer;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
  transition: all 0.3s ease;
  z-index: 9998;
}

.floating-button:hover {
  transform: scale(1.1);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.2);
}

.button-text {
  font-size: 10px;
  font-weight: 600;
  margin-top: 2px;
}

/* 聊天消息滚动条 */
.chat-messages::-webkit-scrollbar {
  width: 4px;
}

.chat-messages::-webkit-scrollbar-track {
  background: #f1f1f1;
}

.chat-messages::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 2px;
}

/* Markdown内容样式（简化版） */
.markdown-content h1,
.markdown-content h2,
.markdown-content h3 {
  margin: 4px 0;
  font-size: 12px;
  font-weight: 600;
}

.markdown-content p {
  margin: 2px 0;
  font-size: 12px;
}

.markdown-content code {
  background: #f1f3f4;
  padding: 1px 3px;
  border-radius: 2px;
  font-size: 11px;
}

.markdown-content pre {
  background: #f8f8f8;
  padding: 6px;
  border-radius: 4px;
  font-size: 11px;
  margin: 4px 0;
  overflow-x: auto;
}

.markdown-content ul,
.markdown-content ol {
  margin: 2px 0;
  padding-left: 12px;
  font-size: 12px;
}

.markdown-content blockquote {
  margin: 4px 0;
  padding: 4px 8px;
  border-left: 2px solid #409eff;
  background: #f8f9fa;
  border-radius: 2px;
  font-size: 12px;
}
</style> 