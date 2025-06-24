<template>
  <div class="user-management-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <div class="header-left">
          <h1 class="page-title">
            <el-icon class="title-icon"><UserFilled /></el-icon>
            用户管理
          </h1>
          <p class="page-description">管理系统用户权限和账户状态</p>
        </div>
        <div class="header-actions">
          <el-button 
            type="primary" 
            :icon="Refresh" 
            @click="refreshUsers"
            :loading="loading"
          >
            刷新
          </el-button>
        </div>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-cards">
      <div class="stat-card">
        <div class="stat-icon admin">
          <el-icon><UserFilled /></el-icon>
        </div>
        <div class="stat-content">
          <h3>{{ adminCount }}</h3>
          <p>管理员</p>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon user">
          <el-icon><User /></el-icon>
        </div>
        <div class="stat-content">
          <h3>{{ userCount }}</h3>
          <p>普通用户</p>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon blocked">
          <el-icon><Lock /></el-icon>
        </div>
        <div class="stat-content">
          <h3>{{ blockedCount }}</h3>
          <p>禁用用户</p>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon total">
          <el-icon><Avatar /></el-icon>
        </div>
        <div class="stat-content">
          <h3>{{ totalCount }}</h3>
          <p>总用户数</p>
        </div>
      </div>
    </div>

    <!-- 用户列表 -->
    <div class="user-list-container">
      <div class="list-header">
        <h2>用户列表</h2>
        <div class="search-actions">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索用户名或邮箱"
            :prefix-icon="Search"
            @input="handleSearch"
            clearable
            style="width: 300px;"
          />
        </div>
      </div>

      <!-- PC端表格 -->
      <div class="desktop-table">
        <el-table 
          :data="filteredUsers" 
          v-loading="loading"
          :empty-text="'暂无用户数据'"
          row-key="id"
          stripe
        >
          <el-table-column prop="id" label="ID" width="80" align="center" />
          <el-table-column prop="username" label="用户名" min-width="150">
            <template #default="{ row }">
              <div class="user-info">
                <el-avatar :size="32" class="user-avatar">
                  {{ row.username?.charAt(0)?.toUpperCase() }}
                </el-avatar>
                <div class="user-details">
                  <div class="username">{{ row.username }}</div>
                  <div class="user-id">ID: {{ row.id }}</div>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="email" label="邮箱" min-width="200">
            <template #default="{ row }">
              <el-link :href="`mailto:${row.email}`" type="primary">
                {{ row.email }}
              </el-link>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100" align="center">
            <template #default="{ row }">
              <el-tag 
                :type="row.status === 1 ? 'success' : 'danger'"
                effect="light"
                round
              >
                {{ row.status === 1 ? '已激活' : '未激活' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="permission" label="权限" width="120" align="center">
            <template #default="{ row }">
              <el-tag 
                :type="getPermissionType(row.permission)"
                effect="light"
                round
              >
                <el-icon style="margin-right: 4px;">
                  <component :is="getPermissionIcon(row.permission)" />
                </el-icon>
                {{ getPermissionText(row.permission) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createdAt" label="创建时间" width="180">
            <template #default="{ row }">
              <div class="date-info">
                <div>{{ formatDate(row.createdAt) }}</div>
                <div class="date-relative">{{ getRelativeTime(row.createdAt) }}</div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" align="center" fixed="right">
            <template #default="{ row }">
              <div class="action-buttons">
                <el-button 
                  type="primary" 
                  size="small"
                  :icon="Key"
                  @click="editPermission(row)"
                  :disabled="row.id === currentUserId"
                  plain
                >
                  授权
                </el-button>
                <el-button 
                  type="danger" 
                  size="small"
                  :icon="Delete"
                  @click="deleteUser(row)"
                  :disabled="row.id === currentUserId"
                  plain
                >
                  删除
                </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 移动端卡片 -->
      <div class="mobile-cards">
        <div 
          v-for="user in filteredUsers" 
          :key="user.id"
          class="user-card"
        >
          <div class="card-header">
            <div class="user-avatar-section">
              <el-avatar :size="48" class="user-avatar">
                {{ user.username?.charAt(0)?.toUpperCase() }}
              </el-avatar>
              <div class="user-basic-info">
                <h3 class="username">{{ user.username }}</h3>
                <p class="email">{{ user.email }}</p>
              </div>
            </div>
            <div class="status-badges">
              <el-tag 
                :type="user.status === 1 ? 'success' : 'danger'"
                effect="light"
                size="small"
                round
              >
                {{ user.status === 1 ? '已激活' : '未激活' }}
              </el-tag>
            </div>
          </div>
          
          <div class="card-body">
            <div class="info-row">
              <span class="label">用户ID:</span>
              <span class="value">{{ user.id }}</span>
            </div>
            <div class="info-row">
              <span class="label">权限级别:</span>
              <el-tag 
                :type="getPermissionType(user.permission)"
                effect="light"
                size="small"
                round
              >
                <el-icon style="margin-right: 4px;">
                  <component :is="getPermissionIcon(user.permission)" />
                </el-icon>
                {{ getPermissionText(user.permission) }}
              </el-tag>
            </div>
            <div class="info-row">
              <span class="label">创建时间:</span>
              <span class="value">{{ formatDate(user.createdAt) }}</span>
            </div>
          </div>
          
          <div class="card-actions">
            <el-button 
              type="primary" 
              size="small"
              :icon="Key"
              @click="editPermission(user)"
              :disabled="user.id === currentUserId"
              plain
            >
              授权
            </el-button>
            <el-button 
              type="danger" 
              size="small"
              :icon="Delete"
              @click="deleteUser(user)"
              :disabled="user.id === currentUserId"
              plain
            >
              删除
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 权限编辑对话框 -->
    <el-dialog 
      v-model="permissionDialogVisible" 
      title="用户授权"
      width="500px"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <div class="permission-dialog">
        <div class="user-summary">
          <el-avatar :size="60" class="dialog-avatar">
            {{ selectedUser?.username?.charAt(0)?.toUpperCase() }}
          </el-avatar>
          <div class="user-info">
            <h3>{{ selectedUser?.username }}</h3>
            <p>{{ selectedUser?.email }}</p>
          </div>
        </div>
        
        <el-form 
          :model="permissionForm" 
          label-width="100px"
          class="permission-form"
        >
          <el-form-item label="当前权限:">
            <el-tag :type="getPermissionType(selectedUser?.permission)">
              {{ getPermissionText(selectedUser?.permission) }}
            </el-tag>
          </el-form-item>
          
          <el-form-item label="新权限级别:" required>
            <el-select 
              v-model="permissionForm.permission" 
              placeholder="请选择权限级别"
              style="width: 100%"
            >
              <el-option 
                label="管理员" 
                value="1"
                :disabled="selectedUser?.id === currentUserId"
              >
                <div class="permission-option">
                  <el-icon><UserFilled /></el-icon>
                  <span>管理员</span>
                </div>
              </el-option>
              <el-option label="普通用户" value="2">
                <div class="permission-option">
                  <el-icon><User /></el-icon>
                  <span>普通用户</span>
                </div>
              </el-option>
              <el-option label="禁止登录" value="3">
                <div class="permission-option">
                  <el-icon><Lock /></el-icon>
                  <span>禁止登录</span>
                </div>
              </el-option>
            </el-select>
          </el-form-item>
        </el-form>
        
        <div class="permission-description">
          <h4><el-icon><InfoFilled /></el-icon> 权限说明</h4>
          <div class="permission-list">
            <div class="permission-item">
              <el-icon class="perm-icon admin"><UserFilled /></el-icon>
              <div class="perm-content">
                <strong>管理员</strong>
                <p>拥有所有系统权限，可以管理用户、部门、员工、项目和薪资</p>
              </div>
            </div>
            <div class="permission-item">
              <el-icon class="perm-icon user"><User /></el-icon>
              <div class="perm-content">
                <strong>普通用户</strong>
                <p>只能查看和修改自己的个人信息，无法访问管理功能</p>
              </div>
            </div>
            <div class="permission-item">
              <el-icon class="perm-icon blocked"><Lock /></el-icon>
              <div class="perm-content">
                <strong>禁止登录</strong>
                <p>账户被禁用，无法登录系统</p>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="permissionDialogVisible = false">取消</el-button>
          <el-button 
            type="primary" 
            @click="updatePermission"
            :loading="updating"
          >
            确认更新
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  UserFilled, User, Lock, Key, Delete, 
  Refresh, Search, InfoFilled, Avatar
} from '@element-plus/icons-vue'
import axios from 'axios'

const users = ref([])
const loading = ref(false)
const updating = ref(false)
const permissionDialogVisible = ref(false)
const selectedUser = ref(null)
const currentUserId = ref(null)
const searchKeyword = ref('')

const permissionForm = ref({
  permission: ''
})

// 计算属性
const filteredUsers = computed(() => {
  if (!searchKeyword.value) return users.value
  const keyword = searchKeyword.value.toLowerCase()
  return users.value.filter(user => 
    user.username?.toLowerCase().includes(keyword) ||
    user.email?.toLowerCase().includes(keyword)
  )
})

const adminCount = computed(() => 
  users.value.filter(user => user.permission === '1').length
)

const userCount = computed(() => 
  users.value.filter(user => user.permission === '2').length
)

const blockedCount = computed(() => 
  users.value.filter(user => user.permission === '3').length
)

const totalCount = computed(() => users.value.length)

// 方法
const getCurrentUser = async () => {
  try {
    const response = await axios.get('/dept-api/auth/profile')
    if (response.data.code === 200) {
      currentUserId.value = response.data.data.id
    }
  } catch (error) {
    console.error('获取当前用户信息失败:', error)
  }
}

const fetchUsers = async () => {
  loading.value = true
  try {
    const response = await axios.get('/dept-api/auth/users')
    if (response.data.code === 200) {
      users.value = response.data.data
    } else {
      ElMessage.error(response.data.message || '获取用户列表失败')
    }
  } catch (error) {
    ElMessage.error('获取用户列表失败')
    console.error('获取用户列表失败:', error)
  } finally {
    loading.value = false
  }
}

const refreshUsers = () => {
  fetchUsers()
}

const handleSearch = () => {
  // 搜索在计算属性中处理，这里可以添加防抖逻辑
}

const editPermission = (user) => {
  selectedUser.value = user
  permissionForm.value.permission = user.permission
  permissionDialogVisible.value = true
}

const updatePermission = async () => {
  updating.value = true
  try {
    const response = await axios.put(
      `/dept-api/auth/users/${selectedUser.value.id}/permission`,
      null,
      {
        params: {
          permission: permissionForm.value.permission
        }
      }
    )
    
    if (response.data.code === 200) {
      ElMessage.success('权限更新成功')
      permissionDialogVisible.value = false
      fetchUsers()
    } else {
      ElMessage.error(response.data.message || '权限更新失败')
    }
  } catch (error) {
    ElMessage.error('您没有权限，请联系管理员')
    console.error('权限更新失败:', error)
  } finally {
    updating.value = false
  }
}

const deleteUser = async (user) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除用户 "${user.username}" 吗？此操作不可恢复！`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
        dangerouslyUseHTMLString: false
      }
    )
    
    const response = await axios.delete(`/dept-api/auth/users/${user.id}`)
    
    if (response.data.code === 200) {
      ElMessage.success('用户删除成功')
      fetchUsers()
    } else {
      ElMessage.error(response.data.message || '用户删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('您没有权限，请联系管理员')
      console.error('用户删除失败:', error)
    }
  }
}

const getPermissionType = (permission) => {
  switch (permission) {
    case '1': return 'warning'
    case '2': return 'success' 
    case '3': return 'danger'
    default: return 'info'
  }
}

const getPermissionText = (permission) => {
  switch (permission) {
    case '1': return '管理员'
    case '2': return '普通用户'
    case '3': return '禁止登录'
    default: return '普通用户'
  }
}

const getPermissionIcon = (permission) => {
  switch (permission) {
    case '1': return UserFilled
    case '2': return User
    case '3': return Lock
    default: return User
  }
}

const formatDate = (dateString) => {
  if (!dateString) return '-'
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN') + ' ' + date.toLocaleTimeString('zh-CN', { 
    hour: '2-digit', 
    minute: '2-digit' 
  })
}

const getRelativeTime = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  const now = new Date()
  const diff = now - date
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))
  
  if (days === 0) return '今天'
  if (days === 1) return '昨天'
  if (days < 7) return `${days}天前`
  if (days < 30) return `${Math.floor(days / 7)}周前`
  if (days < 365) return `${Math.floor(days / 30)}个月前`
  return `${Math.floor(days / 365)}年前`
}

onMounted(() => {
  getCurrentUser()
  fetchUsers()
})
</script>

<style scoped>
.user-management-container {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 20px;
}

/* 页面头部 */
.page-header {
  background: white;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  flex: 1;
}

.page-title {
  margin: 0 0 8px 0;
  color: #1f2937;
  font-size: 28px;
  font-weight: 700;
  display: flex;
  align-items: center;
  gap: 12px;
}

.title-icon {
  color: #3b82f6;
}

.page-description {
  margin: 0;
  color: #6b7280;
  font-size: 16px;
}

/* 统计卡片 */
.stats-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: transform 0.2s, box-shadow 0.2s;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: white;
}

.stat-icon.admin { background: #f59e0b; }
.stat-icon.user { background: #10b981; }
.stat-icon.blocked { background: #ef4444; }
.stat-icon.total { background: #3b82f6; }

.stat-content h3 {
  margin: 0 0 4px 0;
  font-size: 24px;
  font-weight: 700;
  color: #1f2937;
}

.stat-content p {
  margin: 0;
  color: #6b7280;
  font-size: 14px;
}

/* 用户列表容器 */
.user-list-container {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  overflow: hidden;
}

.list-header {
  padding: 24px;
  border-bottom: 1px solid #e5e7eb;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.list-header h2 {
  margin: 0;
  color: #1f2937;
  font-size: 20px;
  font-weight: 600;
}

/* 表格样式 */
.desktop-table {
  display: block;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-details .username {
  font-weight: 600;
  color: #1f2937;
}

.user-details .user-id {
  font-size: 12px;
  color: #6b7280;
}

.date-info .date-relative {
  font-size: 12px;
  color: #6b7280;
}

.action-buttons {
  display: flex;
  gap: 8px;
}

/* 移动端卡片 */
.mobile-cards {
  display: none;
  padding: 16px;
  gap: 16px;
  flex-direction: column;
}

.user-card {
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 16px;
  background: #fafafa;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.user-avatar-section {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-basic-info .username {
  margin: 0 0 4px 0;
  font-weight: 600;
  color: #1f2937;
}

.user-basic-info .email {
  margin: 0;
  color: #6b7280;
  font-size: 14px;
}

.card-body {
  margin-bottom: 16px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.info-row .label {
  color: #6b7280;
  font-size: 14px;
}

.info-row .value {
  color: #1f2937;
  font-weight: 500;
}

.card-actions {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}

/* 权限对话框 */
.permission-dialog {
  padding: 16px 0;
}

.user-summary {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
  padding: 16px;
  background: #f8fafc;
  border-radius: 8px;
}

.dialog-avatar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.user-summary .user-info h3 {
  margin: 0 0 4px 0;
  color: #1f2937;
}

.user-summary .user-info p {
  margin: 0;
  color: #6b7280;
}

.permission-form {
  margin-bottom: 24px;
}

.permission-option {
  display: flex;
  align-items: center;
  gap: 8px;
}

.permission-description {
  background: #f8fafc;
  border-radius: 8px;
  padding: 16px;
  border-left: 4px solid #3b82f6;
}

.permission-description h4 {
  margin: 0 0 16px 0;
  color: #1f2937;
  display: flex;
  align-items: center;
  gap: 8px;
}

.permission-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.permission-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.perm-icon {
  width: 24px;
  height: 24px;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  flex-shrink: 0;
  margin-top: 2px;
}

.perm-icon.admin { background: #f59e0b; }
.perm-icon.user { background: #10b981; }
.perm-icon.blocked { background: #ef4444; }

.perm-content strong {
  color: #1f2937;
  display: block;
  margin-bottom: 4px;
}

.perm-content p {
  margin: 0;
  color: #6b7280;
  font-size: 14px;
  line-height: 1.5;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .user-management-container {
    padding: 16px;
  }
  
  .page-header {
    padding: 16px;
    margin-bottom: 16px;
  }
  
  .header-content {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
  
  .page-title {
    font-size: 24px;
  }
  
  .stats-cards {
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
    margin-bottom: 16px;
  }
  
  .stat-card {
    padding: 16px;
  }
  
  .stat-icon {
    width: 40px;
    height: 40px;
    font-size: 16px;
  }
  
  .stat-content h3 {
    font-size: 20px;
  }
  
  .list-header {
    padding: 16px;
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
  
  .search-actions {
    width: 100%;
  }
  
  .search-actions .el-input {
    width: 100% !important;
  }
  
  .desktop-table {
    display: none;
  }
  
  .mobile-cards {
    display: flex;
  }
  
  .permission-dialog .user-summary {
    flex-direction: column;
    text-align: center;
  }
  
  .permission-list {
    gap: 16px;
  }
  
  .permission-item {
    flex-direction: column;
    text-align: center;
    gap: 8px;
  }
  
  .perm-icon {
    align-self: center;
  }
}

@media (max-width: 480px) {
  .stats-cards {
    grid-template-columns: 1fr;
  }
  
  .card-actions {
    flex-direction: column;
  }
  
  .action-buttons {
    flex-direction: column;
  }
}

/* 深色模式支持 */
@media (prefers-color-scheme: dark) {
  .user-management-container {
    background: #0f172a;
  }
  
  .page-header,
  .user-list-container,
  .stat-card {
    background: #1e293b;
    border-color: #334155;
  }
  
  .page-title,
  .stat-content h3,
  .list-header h2 {
    color: #f8fafc;
  }
  
  .page-description,
  .stat-content p {
    color: #94a3b8;
  }
  
  .user-card {
    background: #334155;
    border-color: #475569;
  }
}
</style>