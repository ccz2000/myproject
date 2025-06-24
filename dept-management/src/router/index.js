import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'
import tokenManager from '@/utils/tokenManager'

// 导入页面组件
import PersonalHome from '../views/PersonalHome.vue'
import Home from '../views/Home.vue'
import Departments from '../views/Departments.vue'
import Employees from '../views/Employees.vue'
import Projects from '../views/Projects.vue'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import EmailVerification from '../views/EmailVerification.vue'
import ForgotPassword from '../views/ForgotPassword.vue'
import Profile from '../views/Profile.vue'
import ProfileEdit from '../views/ProfileEdit.vue'
import ChangePassword from '../views/ChangePassword.vue'
import UserSettings from '../views/UserSettings.vue'
import AiChat from '../views/AiChat.vue'
import SalaryList from '../views/salary/SalaryList.vue'
import SalaryAdjustment from '../views/salary/SalaryAdjustment.vue'
import SalaryPayment from '../views/salary/SalaryPayment.vue'
import UserManagement from '../views/UserManagement.vue'

const routes = [
  // 个人主站 - 新的主页
  {
    path: '/',
    name: 'PersonalHome',
    component: PersonalHome,
    meta: { requiresAuth: false, layout: 'blank' }
  },
  
  // 系统相关路由（不需要认证）
  {
    path: '/system/login',
    name: 'Login',
    component: Login,
    meta: { requiresAuth: false, layout: 'blank' }
  },
  {
    path: '/system/register',
    name: 'Register',
    component: Register,
    meta: { requiresAuth: false, layout: 'blank' }
  },
  {
    path: '/system/verify',
    name: 'EmailVerification',
    component: EmailVerification,
    meta: { requiresAuth: false, layout: 'blank' }
  },
  {
    path: '/system/forgot-password',
    name: 'ForgotPassword',
    component: ForgotPassword,
    meta: { requiresAuth: false, layout: 'blank' }
  },
  
  // 企业管理系统主入口
  {
    path: '/system',
    name: 'System',
    redirect: '/system/home',
    meta: { requiresAuth: true }
  },
  
  // 系统功能路由（需要认证）
  {
    path: '/system/home',
    name: 'Home',
    component: Home,
    meta: { requiresAuth: true }
  },
  {
    path: '/system/departments',
    name: 'Departments',
    component: Departments,
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/system/employees',
    name: 'Employees',
    component: Employees,
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/system/projects',
    name: 'Projects',
    component: Projects,
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/system/profile',
    name: 'Profile',
    component: Profile,
    meta: { requiresAuth: true }
  },
  {
    path: '/system/profile/edit',
    name: 'ProfileEdit',
    component: ProfileEdit,
    meta: { requiresAuth: true }
  },
  {
    path: '/system/profile/change-password',
    name: 'ChangePassword',
    component: ChangePassword,
    meta: { requiresAuth: true }
  },
  {
    path: '/system/profile/settings',
    name: 'UserSettings',
    component: UserSettings,
    meta: { requiresAuth: true }
  },
  {
    path: '/system/ai-chat',
    name: 'AiChat',
    component: AiChat,
    meta: { requiresAuth: true }
  },
  {
    path: '/system/salary/list',
    name: 'SalaryList',
    component: SalaryList,
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/system/salary/adjustment',
    name: 'SalaryAdjustment',
    component: SalaryAdjustment,
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/system/salary/payment',
    name: 'SalaryPayment',
    component: SalaryPayment,
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/system/user-management',
    name: 'UserManagement',
    component: UserManagement,
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  
  // 兼容旧路由（重定向到新路由）
  {
    path: '/login',
    redirect: '/system/login'
  },
  {
    path: '/register',
    redirect: '/system/register'
  },
  {
    path: '/verify',
    redirect: '/system/verify'
  },
  {
    path: '/forgot-password',
    redirect: '/system/forgot-password'
  },
  {
    path: '/home',
    redirect: '/system/home'
  },
  {
    path: '/departments',
    redirect: '/system/departments'
  },
  {
    path: '/employees',
    redirect: '/system/employees'
  },
  {
    path: '/projects',
    redirect: '/system/projects'
  },
  {
    path: '/profile',
    redirect: '/system/profile'
  },
  {
    path: '/ai-chat',
    redirect: '/system/ai-chat'
  },
  {
    path: '/salary/:path*',
    redirect: to => `/system/salary/${to.params.path || ''}`
  },
  {
    path: '/user-management',
    redirect: '/system/user-management'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach(async (to, from, next) => {
  const token = tokenManager.getToken()
  const userInfo = localStorage.getItem('userInfo')
  
  // 如果访问个人主站，直接放行
  if (to.name === 'PersonalHome') {
    next()
    return
  }
  
  // 如果路由需要认证
  if (to.meta.requiresAuth) {
    if (token && userInfo) {
      // 检查token是否有效
      if (tokenManager.isTokenExpired()) {
        ElMessage.warning('登录已过期，请重新登录')
        tokenManager.clearTokens()
        next('/system/login')
        return
      }
      
      // 如果token即将过期，尝试刷新
      if (tokenManager.isTokenNearExpiry()) {
        try {
          await tokenManager.refreshToken()
          console.log('路由守卫：Token已自动刷新')
        } catch (error) {
          console.error('路由守卫：Token刷新失败', error)
          ElMessage.warning('登录已过期，请重新登录')
          next('/system/login')
          return
        }
      }
      
      // 检查管理员权限
      if (to.meta.requiresAdmin) {
        try {
          const user = JSON.parse(userInfo)
          if (user.permission !== '1') {
            ElMessage.error('您没有权限，请联系管理员')
            next('/system/home')
            return
          }
        } catch (error) {
          console.error('解析用户信息失败:', error)
          ElMessage.warning('用户信息异常，请重新登录')
          tokenManager.clearTokens()
          next('/system/login')
          return
        }
      }
      
      next()
    } else {
      ElMessage.warning('请先登录')
      next('/system/login')
    }
  } else {
    // 如果已登录用户访问登录/注册页面，重定向到系统首页
    if ((to.path === '/system/login' || to.path === '/system/register') && token && userInfo && !tokenManager.isTokenExpired()) {
      next('/system/home')
    } else {
      next()
    }
  }
})

export default router 