<template>
  <div id="app">
    <!-- 个人主站布局 - 无导航栏 -->
    <div v-if="isBlankLayout">
      <router-view />
    </div>
    
    <!-- 系统管理布局 - 有导航栏 -->
    <div v-else>
      <!-- 只有在已登录时才显示导航栏 -->
      <div v-if="isLoggedIn">
        <el-container class="layout-container">
          <!-- 侧边栏 -->
          <el-aside :class="['sidebar', { show: isSidebarVisible }]">
            <div class="logo">
              <h2>企业管理系统</h2>
            </div>
            <el-menu
              :default-active="$route.path"
              router
              class="sidebar-menu"
              background-color="#304156"
              text-color="#bfcbd9"
              active-text-color="#409EFF"
            >
              <!-- 首页 - 所有用户可见 -->
              <el-menu-item index="/system/home">
                <el-icon><House /></el-icon>
                <span>系统首页</span>
              </el-menu-item>

              
              <!-- 管理功能 - 仅管理员可见 -->
              <template v-if="isAdmin">
                <el-menu-item index="/system/departments">
                  <el-icon><OfficeBuilding /></el-icon>
                  <span>部门管理</span>
                </el-menu-item>
                <el-menu-item index="/system/employees">
                  <el-icon><User /></el-icon>
                  <span>员工管理</span>
                </el-menu-item>
                <el-menu-item index="/system/projects">
                  <el-icon><Folder /></el-icon>
                  <span>项目管理</span>
                </el-menu-item>
                <el-sub-menu index="/system/salary">
                  <template #title>
                    <el-icon><Money /></el-icon>
                    <span>薪资管理</span>
                  </template>
                  <el-menu-item index="/system/salary/list">
                    <el-icon><List /></el-icon>
                    <span>薪资列表</span>
                  </el-menu-item>
                  <el-menu-item index="/system/salary/adjustment">
                    <el-icon><Edit /></el-icon>
                    <span>薪资调整</span>
                  </el-menu-item>
                  <el-menu-item index="/system/salary/payment">
                    <el-icon><Wallet /></el-icon>
                    <span>薪资发放</span>
                  </el-menu-item>
                </el-sub-menu>
                <el-menu-item index="/system/user-management">
                  <el-icon><UserFilled /></el-icon>
                  <span>用户管理</span>
                </el-menu-item>
              </template>
              
              <!-- AI功能 - 所有用户可见 -->
              <el-menu-item index="/system/ai-chat">
                <el-icon><ChatDotRound /></el-icon>
                <span>AI智能助手</span>
              </el-menu-item>
              
              <!-- 个人中心 - 所有用户可见 -->
              <el-menu-item index="/system/profile">
                <el-icon><Avatar /></el-icon>
                <span>个人中心</span>
              </el-menu-item>
            </el-menu>
          </el-aside>

          <!-- 主内容区 -->
          <el-container>
            <el-header class="header">
              <div class="header-content">
                <!-- 添加菜单切换按钮 -->
                <div class="header-left">
                  <div class="menu-toggle" @click="toggleSidebar">
                    <el-icon size="20"><Menu /></el-icon>
                  </div>
                  <span class="page-title">{{ getPageTitle() }}</span>
                </div>
                <div class="header-actions">
                  <!-- Token状态显示 -->
                  <TokenStatus />
                  
                  <!-- 用户信息下拉菜单 -->
                  <el-dropdown @command="handleCommand">
                    <div class="user-info">
                      <el-avatar :size="32" :src="userAvatar" class="user-avatar">
                        {{ userInfo?.username?.charAt(0)?.toUpperCase() }}
                      </el-avatar>
                      <span class="username">{{ userInfo?.username }}</span>
                      <el-icon class="dropdown-icon"><ArrowDown /></el-icon>
                    </div>
                    <template #dropdown>
                      <el-dropdown-menu>
                        <el-dropdown-item command="profile" icon="User">
                          个人中心
                        </el-dropdown-item>
                        <el-dropdown-item command="edit-profile" icon="Edit">
                          编辑资料
                        </el-dropdown-item>
                        <el-dropdown-item command="change-password" icon="Key">
                          修改密码
                        </el-dropdown-item>
                        <el-dropdown-item command="settings" icon="Setting">
                          系统设置
                        </el-dropdown-item>
                        <el-dropdown-item divided command="logout" icon="SwitchButton">
                          退出登录
                        </el-dropdown-item>
                      </el-dropdown-menu>
                    </template>
                  </el-dropdown>
                  
                  <el-button 
                    type="success" 
                    @click="openAiFloatingWindow" 
                    style="margin-left: 12px;"
                    title="打开AI助手 (Ctrl+Shift+A)"
                  >
                    <el-icon><ChatDotRound /></el-icon>
                    AI助手
                  </el-button>
                  
                  <el-button type="primary" @click="refreshPage" style="margin-left: 12px;">
                    <el-icon><Refresh /></el-icon>
                    刷新
                  </el-button>
                </div>
              </div>
            </el-header>
            
            <el-main class="main-content">
              <router-view />
            </el-main>
          </el-container>
        </el-container>

        <!-- 添加遮罩层 -->
        <div 
          class="sidebar-mask" 
          :class="{ show: isSidebarVisible }"
          @click="toggleSidebar"
        ></div>
      </div>
      
      <!-- 未登录时直接显示登录/注册页面 -->
      <div v-else>
        <router-view />
      </div>

      <!-- AI悬浮窗组件 - 系统内可用 -->
      <AiFloatingWindow v-if="isLoggedIn" ref="aiFloatingWindow" />
    </div>
  </div>
</template>

<script setup>
import { useRoute, useRouter } from 'vue-router'
import { computed, ref, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import AiFloatingWindow from './components/AiFloatingWindow.vue'
import TokenStatus from './components/TokenStatus.vue'
import tokenManager from './utils/tokenManager'
import { 
  House, OfficeBuilding, User, Folder, ChatDotRound, 
  Avatar, ArrowDown, Refresh, Money, List, Edit, Wallet,
  Menu, UserFilled, Monitor
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

const userInfo = ref(null)
const userAvatar = ref('')
const aiFloatingWindow = ref(null)

// 计算是否使用空白布局（个人主站）
const isBlankLayout = computed(() => {
  return route.meta.layout === 'blank' || route.name === 'PersonalHome'
})

// 计算是否已登录
const isLoggedIn = computed(() => {
  return userInfo.value && tokenManager.getToken()
})

// 计算是否为管理员
const isAdmin = computed(() => {
  return userInfo.value && userInfo.value.permission === '1'
})

const getPageTitle = () => {
  const titleMap = {
    '/system/home': '系统首页',
    '/system/departments': '部门管理',
    '/system/employees': '员工管理',
    '/system/projects': '项目管理',
    '/system/ai-chat': 'AI智能助手',
    '/system/profile': '个人中心',
    '/system/profile/edit': '编辑资料',
    '/system/profile/change-password': '修改密码',
    '/system/profile/settings': '系统设置',
    '/system/salary/list': '薪资列表',
    '/system/salary/adjustment': '薪资调整',
    '/system/salary/payment': '薪资发放',
    '/system/user-management': '用户管理',
    '/system/login': '用户登录',
    '/system/register': '用户注册',
    '/system/verify': '邮箱验证',
    '/system/forgot-password': '忘记密码'
  }
  
  return titleMap[route.path] || '企业管理系统'
}



const refreshPage = () => {
  window.location.reload()
}

const openAiFloatingWindow = () => {
  if (aiFloatingWindow.value) {
    aiFloatingWindow.value.openWindow()
  }
}

const handleCommand = async (command) => {
  switch (command) {
    case 'profile':
      router.push('/system/profile')
      break
    case 'edit-profile':
      router.push('/system/profile/edit')
      break
    case 'change-password':
      router.push('/system/profile/change-password')
      break
    case 'settings':
      router.push('/system/profile/settings')
      break
    case 'logout':
      await handleLogout()
      break
  }
}

const handleLogout = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要退出登录吗？',
      '退出确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )
    
    // 使用token管理器清除所有token信息
    tokenManager.clearTokens()
    
    // 清除用户信息
    userInfo.value = null
    userAvatar.value = ''
    
    ElMessage.success('已退出登录')
    
    // 跳转到登录页
    router.push('/system/login')
  } catch (error) {
    // 用户取消退出
  }
}

const loadUserInfo = () => {
  const storedUserInfo = localStorage.getItem('userInfo')
  if (storedUserInfo) {
    try {
      userInfo.value = JSON.parse(storedUserInfo)
      // 这里可以从用户资料中获取头像URL
      // userAvatar.value = userInfo.value.avatar || ''
    } catch (error) {
      console.error('解析用户信息失败:', error)
      localStorage.removeItem('userInfo')
      localStorage.removeItem('token')
    }
  }
}

// 监听路由变化，更新用户信息
watch(() => route.path, () => {
  loadUserInfo()
})

// 添加侧边栏控制
const isSidebarVisible = ref(false)

const toggleSidebar = () => {
  isSidebarVisible.value = !isSidebarVisible.value
}

// 监听路由变化，在移动端自动关闭侧边栏
watch(() => route.path, () => {
  if (window.innerWidth <= 768) {
    isSidebarVisible.value = false
  }
})

onMounted(async () => {
  // 检查并刷新token
  await tokenManager.checkAndRefreshToken()
  
  loadUserInfo()
  setupKeyboardShortcuts()
})

// 设置键盘快捷键
const setupKeyboardShortcuts = () => {
  const handleKeydown = (event) => {
    // Ctrl+Shift+A 打开AI悬浮窗
    if (event.ctrlKey && event.shiftKey && event.key === 'A') {
      event.preventDefault()
      if (aiFloatingWindow.value) {
        aiFloatingWindow.value.openWindow()
      }
    }
    
    // ESC 关闭AI悬浮窗
    if (event.key === 'Escape') {
      if (aiFloatingWindow.value) {
        aiFloatingWindow.value.closeWindow()
      }
    }
  }
  
  document.addEventListener('keydown', handleKeydown)
}
</script>

<style scoped>
.layout-container {
  min-height: 100vh;
}

.sidebar {
  background-color: #304156;
  transition: width 0.3s;
  overflow-x: hidden;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  background-color: #2b3649;
}

.logo h2 {
  margin: 0;
  font-size: 18px;
  white-space: nowrap;
}

.sidebar-menu {
  border-right: none;
}

.header {
  background-color: white;
  border-bottom: 1px solid #e6e6e6;
  padding: 0 20px;
}

.header-content {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.page-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.user-info:hover {
  background-color: #f5f7fa;
}

.username {
  margin: 0 8px;
  color: #606266;
}

.dropdown-icon {
  color: #909399;
}

.main-content {
  background-color: #f0f2f5;
  padding: 20px;
}

/* 移动端样式 */
@media screen and (max-width: 768px) {
  .sidebar {
    position: fixed;
    left: -250px;
    top: 0;
    bottom: 0;
    z-index: 1000;
    width: 250px !important;
  }

  .sidebar.show {
    left: 0;
  }

  .header {
    padding: 0 15px;
  }

  .header-content {
    height: 50px;
  }

  .page-title {
    font-size: 16px;
  }

  .header-actions {
    gap: 8px;
  }

  .user-info {
    padding: 2px 4px;
  }

  .username {
    display: none;
  }

  .header-actions .el-button {
    padding: 8px;
  }

  .header-actions .el-button span {
    display: none;
  }

  .main-content {
    padding: 15px;
    margin-left: 0;
  }

  /* 添加菜单按钮 */
  .menu-toggle {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 40px;
    height: 40px;
    margin-right: 10px;
    cursor: pointer;
  }
}

/* 平板设备样式 */
@media screen and (min-width: 769px) and (max-width: 1024px) {
  .sidebar {
    width: 200px !important;
  }

  .logo h2 {
    font-size: 16px;
  }

  .header {
    padding: 0 15px;
  }

  .main-content {
    padding: 15px;
  }
}

/* 遮罩层 */
.sidebar-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 999;
  display: none;
}

@media screen and (max-width: 768px) {
  .sidebar-mask.show {
    display: block;
  }
}

.header-left {
  display: flex;
  align-items: center;
}

.menu-toggle {
  display: none;
}

@media screen and (max-width: 768px) {
  .menu-toggle {
    display: flex;
  }
}
</style> 