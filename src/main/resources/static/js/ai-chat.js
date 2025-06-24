// AI聊天助手 JavaScript

class AiChatApp {
    constructor() {
        this.baseUrl = '/dept-api/api/ai';
        this.sessionId = this.generateSessionId();
        this.isLoading = false;
        
        this.initElements();
        this.bindEvents();
        this.checkInitialStatus();
        this.updateWelcomeTime();
    }

    // 初始化DOM元素
    initElements() {
        this.messageInput = document.getElementById('messageInput');
        this.sendButton = document.getElementById('sendButton');
        this.chatMessages = document.getElementById('chatMessages');
        this.loadingOverlay = document.getElementById('loadingOverlay');
        this.statusText = document.getElementById('status-text');
        this.toast = document.getElementById('toast');
        this.charCount = document.querySelector('.char-count');
    }

    // 绑定事件
    bindEvents() {
        // 发送按钮点击事件
        this.sendButton.addEventListener('click', () => this.sendMessage());
        
        // 输入框键盘事件
        this.messageInput.addEventListener('keydown', (e) => {
            if (e.key === 'Enter' && !e.shiftKey) {
                e.preventDefault();
                this.sendMessage();
            }
        });

        // 输入框内容变化事件
        this.messageInput.addEventListener('input', () => {
            this.updateCharCount();
            this.autoResizeTextarea();
        });

        // 页面加载完成后聚焦到输入框
        window.addEventListener('load', () => {
            this.messageInput.focus();
        });
    }

    // 生成会话ID
    generateSessionId() {
        return 'session_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9);
    }

    // 更新欢迎消息时间
    updateWelcomeTime() {
        const welcomeTimeElement = document.getElementById('welcomeTime');
        if (welcomeTimeElement) {
            welcomeTimeElement.textContent = this.formatTime(new Date());
        }
    }

    // 检查初始状态
    async checkInitialStatus() {
        await this.checkStatus();
    }

    // 发送消息
    async sendMessage() {
        const question = this.messageInput.value.trim();
        
        if (!question) {
            this.showToast('请输入问题', 'warning');
            return;
        }

        if (this.isLoading) {
            this.showToast('请等待当前请求完成', 'warning');
            return;
        }

        // 添加用户消息到界面
        this.addMessage(question, 'user');
        
        // 清空输入框
        this.messageInput.value = '';
        this.updateCharCount();
        this.autoResizeTextarea();

        // 显示加载状态
        this.showLoading(true);
        
        try {
            const response = await this.callAiApi(question);
            
            if (response.success) {
                this.addMessage(response.answer, 'bot', response);
                this.showToast('回复成功', 'success');
            } else {
                this.addMessage(`抱歉，遇到了一些问题：${response.error || '未知错误'}`, 'bot', response);
                this.showToast('AI回复失败：' + (response.error || '未知错误'), 'error');
            }
        } catch (error) {
            console.error('发送消息失败:', error);
            this.addMessage('抱歉，网络连接异常，请稍后重试。', 'bot');
            this.showToast('网络错误：' + error.message, 'error');
        } finally {
            this.showLoading(false);
            this.messageInput.focus();
        }
    }

    // 调用AI API
    async callAiApi(question) {
        const requestData = {
            question: question,
            sessionId: this.sessionId,
            stream: false
        };

        const response = await fetch(`${this.baseUrl}/chat`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(requestData)
        });

        if (!response.ok) {
            throw new Error(`HTTP ${response.status}: ${response.statusText}`);
        }

        return await response.json();
    }

    // 添加消息到聊天区域
    addMessage(content, type, metadata = null) {
        const messageDiv = document.createElement('div');
        messageDiv.className = `message ${type}-message`;

        const avatar = document.createElement('div');
        avatar.className = 'message-avatar';
        avatar.innerHTML = type === 'user' ? '<i class="fas fa-user"></i>' : '<i class="fas fa-robot"></i>';

        const messageContent = document.createElement('div');
        messageContent.className = 'message-content';

        const messageText = document.createElement('div');
        messageText.className = 'message-text';
        
        // 处理内容中的换行
        messageText.innerHTML = this.formatMessageContent(content);

        const messageTime = document.createElement('div');
        messageTime.className = 'message-time';
        messageTime.textContent = this.formatTime(new Date());

        // 如果有元数据，添加额外信息
        if (metadata && type === 'bot') {
            const metaInfo = document.createElement('div');
            metaInfo.className = 'message-meta';
            metaInfo.style.fontSize = '0.7rem';
            metaInfo.style.color = '#94a3b8';
            metaInfo.style.marginTop = '0.25rem';
            
            const metaItems = [];
            if (metadata.model) metaItems.push(`模型: ${metadata.model}`);
            if (metadata.tokens) metaItems.push(`Tokens: ${metadata.tokens}`);
            if (metadata.duration) metaItems.push(`耗时: ${metadata.duration}ms`);
            
            metaInfo.textContent = metaItems.join(' | ');
            messageContent.appendChild(metaInfo);
        }

        messageContent.appendChild(messageText);
        messageContent.appendChild(messageTime);
        
        messageDiv.appendChild(avatar);
        messageDiv.appendChild(messageContent);

        this.chatMessages.appendChild(messageDiv);
        this.scrollToBottom();
    }

    // 格式化消息内容
    formatMessageContent(content) {
        return content
            .replace(/\n/g, '<br>')
            .replace(/```([\s\S]*?)```/g, '<pre><code>$1</code></pre>')
            .replace(/`([^`]+)`/g, '<code>$1</code>')
            .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
            .replace(/\*(.*?)\*/g, '<em>$1</em>');
    }

    // 格式化时间
    formatTime(date) {
        const now = new Date();
        const isToday = date.toDateString() === now.toDateString();
        
        if (isToday) {
            return date.toLocaleTimeString('zh-CN', { 
                hour: '2-digit', 
                minute: '2-digit' 
            });
        } else {
            return date.toLocaleString('zh-CN', { 
                month: 'short', 
                day: 'numeric', 
                hour: '2-digit', 
                minute: '2-digit' 
            });
        }
    }

    // 滚动到底部
    scrollToBottom() {
        setTimeout(() => {
            this.chatMessages.scrollTop = this.chatMessages.scrollHeight;
        }, 100);
    }

    // 显示/隐藏加载状态
    showLoading(show) {
        this.isLoading = show;
        this.loadingOverlay.style.display = show ? 'flex' : 'none';
        this.sendButton.disabled = show;
        
        if (show) {
            this.sendButton.innerHTML = '<i class="fas fa-circle-notch fa-spin"></i>';
        } else {
            this.sendButton.innerHTML = '<i class="fas fa-paper-plane"></i>';
        }
    }

    // 显示提示消息
    showToast(message, type = 'info') {
        this.toast.textContent = message;
        this.toast.className = `toast ${type} show`;
        
        setTimeout(() => {
            this.toast.classList.remove('show');
        }, 3000);
    }

    // 更新字符计数
    updateCharCount() {
        const length = this.messageInput.value.length;
        this.charCount.textContent = `${length}/2000`;
        
        if (length > 1800) {
            this.charCount.style.color = '#ef4444';
        } else if (length > 1500) {
            this.charCount.style.color = '#f59e0b';
        } else {
            this.charCount.style.color = '#64748b';
        }
    }

    // 自动调整文本框高度
    autoResizeTextarea() {
        this.messageInput.style.height = 'auto';
        this.messageInput.style.height = Math.min(this.messageInput.scrollHeight, 120) + 'px';
    }

    // 检查AI服务状态
    async checkStatus() {
        try {
            const response = await fetch(`${this.baseUrl}/status`);
            const data = await response.json();
            
            this.updateStatusDisplay(data.available, data.message);
            
            if (data.available) {
                this.showToast('AI服务连接成功', 'success');
            } else {
                this.showToast('AI服务不可用', 'error');
            }
        } catch (error) {
            console.error('检查状态失败:', error);
            this.updateStatusDisplay(false, '连接失败');
            this.showToast('无法连接到AI服务', 'error');
        }
    }

    // 更新状态显示
    updateStatusDisplay(available, message) {
        const indicator = `<span class="status-indicator ${available ? 'status-online' : 'status-offline'}"></span>`;
        this.statusText.innerHTML = indicator + message;
    }

    // 清空聊天记录
    clearChat() {
        if (confirm('确定要清空所有聊天记录吗？')) {
            // 保留欢迎消息
            const welcomeMessage = this.chatMessages.querySelector('.welcome-message');
            this.chatMessages.innerHTML = '';
            if (welcomeMessage) {
                this.chatMessages.appendChild(welcomeMessage);
            }
            
            // 重新生成会话ID
            this.sessionId = this.generateSessionId();
            this.showToast('聊天记录已清空', 'success');
        }
    }
}

// 全局函数（供HTML调用）
let aiChatApp;

function sendMessage() {
    if (aiChatApp) {
        aiChatApp.sendMessage();
    }
}

function clearChat() {
    if (aiChatApp) {
        aiChatApp.clearChat();
    }
}

function checkStatus() {
    if (aiChatApp) {
        aiChatApp.checkStatus();
    }
}

// 页面加载完成后初始化应用
document.addEventListener('DOMContentLoaded', function() {
    aiChatApp = new AiChatApp();
});

// 导出供其他脚本使用
if (typeof module !== 'undefined' && module.exports) {
    module.exports = AiChatApp;
} 