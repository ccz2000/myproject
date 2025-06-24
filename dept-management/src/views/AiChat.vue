<template>
  <div class="ai-chat-container">
    <div class="chat-header">
      <div class="header-left">
        <div class="ai-avatar">
          <el-icon class="robot-icon"><ChatDotRound /></el-icon>
        </div>
        <div class="header-info">
          <h2 class="ai-title">AI智能助手</h2>
          <div class="status-info">
            <el-tag :type="serviceStatus.available ? 'success' : 'danger'" size="small" class="status-tag">
              <el-icon>
                <component :is="serviceStatus.available ? 'CircleCheck' : 'CircleClose'" />
              </el-icon>
              {{ serviceStatus.available ? '在线' : '离线' }}
            </el-tag>
            <span class="provider-info">AI助手</span>
          </div>
        </div>
      </div>
      <div class="header-actions">
        <el-button-group class="desktop-actions">
          <el-button type="default" @click="clearChat">
            <el-icon><Delete /></el-icon>
            清空对话
          </el-button>
          <el-button type="primary" @click="checkStatus" :loading="statusLoading">
            <el-icon><Refresh /></el-icon>
            检查状态
          </el-button>
        </el-button-group>
        
        <!-- 移动端下拉菜单 -->
        <el-dropdown class="mobile-actions" trigger="click" @command="handleCommand">
          <el-button type="primary">
            <el-icon><More /></el-icon>
          </el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="clear">
                <el-icon><Delete /></el-icon>
                清空对话
              </el-dropdown-item>
              <el-dropdown-item command="status">
                <el-icon><Refresh /></el-icon>
                检查状态
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>

    <div class="chat-messages" ref="messagesContainer">
      <!-- 欢迎消息 -->
      <div class="welcome-message" v-if="messages.length === 0">
        <div class="welcome-card">
          <div class="welcome-avatar">
            <el-icon><ChatDotRound /></el-icon>
          </div>
          <div class="welcome-content">
            <h3>👋 欢迎使用AI智能助手</h3>
            <p>我可以帮您解答各种问题，提供智能建议和支持</p>
            <div class="quick-questions">
              <el-tag 
                v-for="question in quickQuestions" 
                :key="question"
                @click="sendQuickQuestion(question)"
                class="quick-tag"
                type="info"
              >
                {{ question }}
              </el-tag>
            </div>
          </div>
        </div>
      </div>

      <!-- 聊天消息 -->
      <div v-for="(message, index) in messages" :key="index" 
           class="message-item" :class="message.type + '-message'">
        <div class="message-avatar">
          <el-avatar :size="36" class="avatar">
            <el-icon><User v-if="message.type === 'user'" /><ChatDotRound v-else /></el-icon>
          </el-avatar>
        </div>
        <div class="message-content">
          <div class="message-bubble" :class="message.type + '-bubble'">
            <div class="message-text" 
                 v-if="message.type === 'user'" 
                 v-text="message.content"></div>
            <div class="message-text markdown-content" 
                 v-else 
                 v-html="renderMessageContent(message.content)"></div>
          </div>
          <div class="message-time">{{ formatTime(message.timestamp) }}</div>
        </div>
      </div>

      <!-- 加载状态 -->
      <div v-if="isLoading" class="message-item bot-message">
        <div class="message-avatar">
          <el-avatar :size="36" class="avatar loading-avatar">
            <el-icon><ChatDotRound /></el-icon>
          </el-avatar>
        </div>
        <div class="message-content">
          <div class="message-bubble bot-bubble">
            <div class="typing-indicator">
              <span class="typing-dot"></span>
              <span class="typing-dot"></span>
              <span class="typing-dot"></span>
              <span class="typing-text">AI正在思考中...</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="chat-input">
      <div class="input-container">
        <div class="input-wrapper">
          <el-input 
            v-model="inputMessage" 
            type="textarea" 
            :rows="2" 
            placeholder="请输入您的问题... (Ctrl+Enter发送)" 
            @keydown.ctrl.enter.prevent="sendMessage"
            @keydown.enter.exact.prevent="handleEnterKey"
            :disabled="isLoading" 
            maxlength="2000" 
            show-word-limit 
            class="message-input"
            resize="none"
          />
          <div class="input-actions">
            <el-tooltip content="发送消息 (Ctrl+Enter)" placement="top">
              <el-button 
                type="primary" 
                @click="sendMessage" 
                :loading="isLoading" 
                :disabled="!inputMessage.trim()"
                class="send-button"
                circle
              >
                <el-icon><Promotion /></el-icon>
              </el-button>
            </el-tooltip>
          </div>
        </div>
        <div class="input-tips">
          <span class="tip-text">💡 提示：按 Ctrl+Enter 快速发送消息</span>
          <div class="quick-actions">
            <el-button size="small" text @click="insertTemplate('请帮我分析')">
              <el-icon><DataAnalysis /></el-icon>
              分析
            </el-button>
            <el-button size="small" text @click="insertTemplate('请帮我总结')">
              <el-icon><Document /></el-icon>
              总结
            </el-button>
            <el-button size="small" text @click="insertTemplate('请给我建议')">
              <el-icon><ChatLineRound /></el-icon>
              建议
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  ChatDotRound, 
  User, 
  CircleCheck, 
  CircleClose, 
  Delete, 
  Refresh, 
  More, 
  Promotion,
  DataAnalysis,
  Document,
  ChatLineRound
} from '@element-plus/icons-vue'
import { aiApi } from '@/api/ai'
import { renderMarkdown } from '@/utils/markdown'

const messages = ref([])
const inputMessage = ref('')
const isLoading = ref(false)
const statusLoading = ref(false)
const messagesContainer = ref(null)

const serviceStatus = reactive({
  available: false,
  message: '检查中...'
})

const aiConfig = reactive({
  provider: 'AI助手',
  model: '',
  authType: ''
})

const sessionId = aiApi.generateSessionId()

// 快速问题
const quickQuestions = ref([
  '你好，请介绍一下自己',
  '如何提高工作效率？',
  '请帮我分析数据',
  '给我一些建议'
])

onMounted(() => {
  // 延迟调用，确保用户已完全登录
  setTimeout(() => {
    checkStatus()
    getConfig()
  }, 1000)
})

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
      ElMessage.success('回复成功')
    } else {
      const errorMsg = response.data?.error || response.error || '未知错误'
      addMessage('bot', `❌ 抱歉，遇到了一些问题：${errorMsg}`)
      ElMessage.error('AI回复失败')
    }
  } catch (error) {
    addMessage('bot', '❌ 网络连接异常，请稍后重试。')
    ElMessage.error('网络连接异常')
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

const clearChat = () => {
  ElMessageBox.confirm('确定要清空所有聊天记录吗？', '确认操作', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    messages.value = []
    ElMessage.success('聊天记录已清空')
  })
}

const checkStatus = async () => {
  statusLoading.value = true
  try {
    const response = await aiApi.checkStatus()
    if (response.success) {
      serviceStatus.available = response.data.available
      serviceStatus.message = response.data.message
    }
  } catch (error) {
    serviceStatus.available = false
  } finally {
    statusLoading.value = false
  }
}

const getConfig = async () => {
  try {
    const response = await aiApi.getConfig()
    if (response.success) {
      Object.assign(aiConfig, response.data)
    }
  } catch (error) {
    console.error('获取配置失败:', error)
  }
}

const scrollToBottom = () => {
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}

const formatTime = (date) => {
  return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}

const renderMessageContent = (content) => {
  return renderMarkdown(content)
}

// 处理下拉菜单命令
const handleCommand = (command) => {
  switch (command) {
    case 'clear':
      clearChat()
      break
    case 'status':
      checkStatus()
      break
  }
}

// 发送快速问题
const sendQuickQuestion = (question) => {
  inputMessage.value = question
  sendMessage()
}

// 插入模板
const insertTemplate = (template) => {
  if (inputMessage.value) {
    inputMessage.value += ' ' + template
  } else {
    inputMessage.value = template
  }
}

// 处理Enter键
const handleEnterKey = (event) => {
  // 在移动端，Enter键直接发送消息
  if (window.innerWidth <= 768) {
    sendMessage()
  } else {
    // 在PC端，Enter键换行，Ctrl+Enter发送
    return true
  }
}
</script>

<style scoped>
.ai-chat-container {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 120px);
  background: #f0f2f5;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.chat-header {
  background: white;
  padding: 20px 24px;
  border-bottom: 1px solid #e5e7eb;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
  flex: 1;
}

.ai-avatar {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.robot-icon {
  color: white;
  font-size: 24px;
}

.ai-title {
  margin: 0 0 4px 0;
  font-size: 24px;
  font-weight: 700;
  color: #1f2937;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.status-info {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 14px;
}

.status-tag {
  display: flex;
  align-items: center;
  gap: 4px;
  border-radius: 6px;
  font-weight: 500;
}

.provider-info {
  color: #6b7280;
  font-weight: 500;
}

.header-actions {
  display: flex;
  gap: 8px;
}

.desktop-actions {
  display: flex;
}

.mobile-actions {
  display: none;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
  background: #f8fafc;
  scroll-behavior: smooth;
}

/* 欢迎消息样式 */
.welcome-message {
  display: flex;
  justify-content: center;
  padding: 40px 20px;
}

.welcome-card {
  background: white;
  border-radius: 16px;
  padding: 32px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  text-align: center;
  max-width: 500px;
  border: 1px solid #e5e7eb;
}

.welcome-avatar {
  width: 64px;
  height: 64px;
  border-radius: 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);
}

.welcome-avatar .el-icon {
  color: white;
  font-size: 32px;
}

.welcome-content h3 {
  margin: 0 0 12px 0;
  font-size: 24px;
  font-weight: 700;
  color: #1f2937;
}

.welcome-content p {
  margin: 0 0 24px 0;
  color: #6b7280;
  font-size: 16px;
  line-height: 1.6;
}

.quick-questions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  justify-content: center;
}

.quick-tag {
  cursor: pointer;
  transition: all 0.2s ease;
  border-radius: 8px;
  font-weight: 500;
}

.quick-tag:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

/* 消息样式 */
.message-item {
  display: flex;
  margin-bottom: 20px;
  animation: fadeInUp 0.3s ease;
}

.user-message {
  flex-direction: row-reverse;
}

.bot-message {
  flex-direction: row;
}

.message-avatar {
  margin: 0 12px;
  flex-shrink: 0;
}

.avatar {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.user-message .avatar {
  background: linear-gradient(135deg, #10b981, #059669);
}

.bot-message .avatar {
  background: linear-gradient(135deg, #667eea, #764ba2);
}

.loading-avatar {
  animation: pulse 2s infinite;
}

.message-content {
  max-width: 75%;
  min-width: 120px;
}

.message-bubble {
  border-radius: 16px;
  padding: 14px 18px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  position: relative;
  border: 1px solid #e5e7eb;
}

.user-bubble {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  border: none;
}

.bot-bubble {
  background: white;
  color: #1f2937;
}

.message-text {
  line-height: 1.6;
  word-wrap: break-word;
  font-size: 15px;
}

.message-time {
  font-size: 12px;
  color: #9ca3af;
  margin-top: 6px;
  text-align: center;
  font-weight: 500;
}

.chat-input {
  background: white;
  padding: 20px 24px;
  border-top: 1px solid #e5e7eb;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.05);
}

.input-container {
  max-width: 100%;
}

.input-wrapper {
  display: flex;
  gap: 12px;
  align-items: flex-end;
  margin-bottom: 12px;
}

.message-input {
  flex: 1;
  border-radius: 12px;
}

.input-actions {
  display: flex;
  align-items: flex-end;
}

.send-button {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border: none;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
  transition: all 0.2s ease;
}

.send-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(102, 126, 234, 0.5);
}

.input-tips {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: #6b7280;
}

.tip-text {
  display: flex;
  align-items: center;
  gap: 4px;
}

.quick-actions {
  display: flex;
  gap: 8px;
}

.quick-actions .el-button {
  font-size: 12px;
  padding: 4px 8px;
  border-radius: 6px;
}

/* 加载动画 */
.typing-indicator {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #667eea;
  font-weight: 500;
}

.typing-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #667eea;
  animation: typingDot 1.4s infinite ease-in-out;
}

.typing-dot:nth-child(1) {
  animation-delay: -0.32s;
}

.typing-dot:nth-child(2) {
  animation-delay: -0.16s;
}

.typing-text {
  margin-left: 4px;
}

/* 动画效果 */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}

@keyframes typingDot {
  0%, 80%, 100% {
    transform: scale(0);
    opacity: 0.5;
  }
  40% {
    transform: scale(1);
    opacity: 1;
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .ai-chat-container {
    height: 100vh;
  }
  
  .chat-header {
    padding: 16px 20px;
  }
  
  .ai-avatar {
    width: 40px;
    height: 40px;
  }
  
  .robot-icon {
    font-size: 20px;
  }
  
  .ai-title {
    font-size: 20px;
  }
  
  .status-info {
    font-size: 12px;
    gap: 8px;
  }
  
  .desktop-actions {
    display: none;
  }
  
  .mobile-actions {
    display: block;
  }
  
  .chat-messages {
    padding: 16px;
  }
  
  .welcome-card {
    padding: 24px 20px;
    margin: 0 10px;
  }
  
  .welcome-avatar {
    width: 56px;
    height: 56px;
  }
  
  .welcome-content h3 {
    font-size: 20px;
  }
  
  .welcome-content p {
    font-size: 14px;
  }
  
  .quick-questions {
    gap: 6px;
  }
  
  .quick-tag {
    font-size: 12px;
    padding: 4px 8px;
  }
  
  .message-content {
    max-width: 85%;
  }
  
  .message-bubble {
    padding: 12px 14px;
  }
  
  .message-text {
    font-size: 14px;
  }
  
  .chat-input {
    padding: 16px 20px;
  }
  
  .input-tips {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .quick-actions {
    align-self: stretch;
    justify-content: space-between;
  }
}

@media (max-width: 480px) {
  .header-left {
    gap: 12px;
  }
  
  .ai-title {
    font-size: 18px;
  }
  
  .welcome-card {
    padding: 20px 16px;
  }
  
  .message-avatar {
    margin: 0 8px;
  }
  
  .avatar {
    width: 32px !important;
    height: 32px !important;
  }
  
  .message-bubble {
    padding: 10px 12px;
  }
  
  .input-wrapper {
    gap: 8px;
  }
  
  .send-button {
    width: 36px;
    height: 36px;
  }
}
</style>

<style>
/* Markdown内容样式 */
.markdown-content h1, .markdown-content h2, .markdown-content h3 {
  margin: 16px 0 8px 0;
  font-weight: 600;
}

.markdown-content p {
  margin: 8px 0;
  line-height: 1.6;
}

.markdown-content ul, .markdown-content ol {
  margin: 8px 0;
  padding-left: 20px;
}

.markdown-content blockquote {
  margin: 12px 0;
  padding: 8px 16px;
  border-left: 4px solid #409eff;
  background: #f8f9fa;
  border-radius: 4px;
}

.markdown-content code {
  background: #f1f3f4;
  padding: 2px 6px;
  border-radius: 3px;
  font-family: 'Courier New', monospace;
  font-size: 0.9em;
}

.markdown-content pre {
  background: #f8f8f8;
  padding: 12px;
  border-radius: 6px;
  overflow-x: auto;
  margin: 12px 0;
}

.markdown-content .code-block {
  margin: 12px 0;
  border-radius: 6px;
  overflow: hidden;
  background: #f8f8f8;
}

.markdown-content .code-header {
  background: #e8e8e8;
  padding: 8px 12px;
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #666;
}

.markdown-content .copy-btn {
  background: none;
  border: 1px solid #ccc;
  padding: 4px 8px;
  border-radius: 3px;
  cursor: pointer;
  font-size: 11px;
}

.markdown-content table {
  width: 100%;
  border-collapse: collapse;
  margin: 12px 0;
}

.markdown-content th, .markdown-content td {
  border: 1px solid #ddd;
  padding: 8px 12px;
  text-align: left;
}

.markdown-content th {
  background: #f5f5f5;
  font-weight: 600;
}

/* 代码高亮样式 */
.hljs {
  background: #f6f8fa !important;
  color: #24292e;
}
</style> 