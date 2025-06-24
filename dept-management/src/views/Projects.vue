<template>
  <div class="projects-container">
    <!-- 顶部操作栏 -->
    <div class="page-header">
      <div class="header-left">
        <h2 class="page-title">
          <el-icon class="title-icon"><Briefcase /></el-icon>
          项目管理
        </h2>
        <p class="page-subtitle">管理公司项目进度和团队协作</p>
      </div>
      <div class="header-actions">
        <el-button type="primary" @click="showAddDialog" class="add-btn">
          <el-icon><Plus /></el-icon>
          添加项目
        </el-button>
      </div>
    </div>

    <!-- 数据展示卡片 -->
    <el-card class="data-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <div class="card-title">
            <span>项目列表</span>
            <el-tag type="info" class="count-tag" size="small">{{ projects.length }} 个项目</el-tag>
          </div>
          <div class="card-actions">
            <el-button size="small" @click="loadProjects" :loading="loading">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>
        </div>
      </template>

      <el-table 
        :data="projects" 
        style="width: 100%"
        v-loading="loading"
        class="modern-table"
      >
        <el-table-column prop="name" label="项目名称" min-width="180">
          <template #default="scope">
            <div class="project-name">
              <el-icon class="project-icon"><Briefcase /></el-icon>
              <span class="name-text">{{ scope.row.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="项目描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)" size="small">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startDate" label="开始日期" width="110">
          <template #default="scope">
            {{ formatDate(scope.row.startDate) }}
          </template>
        </el-table-column>
        <el-table-column prop="endDate" label="结束日期" width="110">
          <template #default="scope">
            {{ formatDate(scope.row.endDate) }}
          </template>
        </el-table-column>
        <el-table-column prop="budget" label="预算" width="100">
          <template #default="scope">
            <span class="budget-text">¥{{ formatCurrency(scope.row.budget) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="departmentName" label="所属部门" width="120" />
        <el-table-column label="操作" min-width="80" fixed="right" class-name="operation-column">
          <template #default="scope">
            <div class="action-buttons">
              <el-button-group class="desktop-buttons">
                <el-button size="small" type="primary" @click="editProject(scope.row)">
                  <el-icon><Edit /></el-icon>
                  编辑
                </el-button>
                <el-button size="small" type="success" @click="manageMembers(scope.row)">
                  <el-icon><User /></el-icon>
                  成员
                </el-button>
                <el-button size="small" type="danger" @click="deleteProject(scope.row)">
                  <el-icon><Delete /></el-icon>
                  删除
                </el-button>
              </el-button-group>
              
              <!-- 移动端下拉菜单 -->
              <el-dropdown class="mobile-dropdown" trigger="click" @command="handleCommand($event, scope.row)">
                <el-button size="small" type="primary">
                  <el-icon><More /></el-icon>
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="edit">
                      <el-icon><Edit /></el-icon>
                      编辑
                    </el-dropdown-item>
                    <el-dropdown-item command="members">
                      <el-icon><User /></el-icon>
                      成员管理
                    </el-dropdown-item>
                    <el-dropdown-item command="delete" divided>
                      <el-icon><Delete /></el-icon>
                      删除
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 添加/编辑项目对话框 -->
    <el-dialog 
      :title="dialogTitle" 
      v-model="dialogVisible" 
      width="600px"
      :close-on-click-modal="false"
      class="modern-dialog"
      destroy-on-close
    >
      <el-form 
        :model="formData" 
        :rules="rules" 
        ref="formRef" 
        label-width="80px"
        class="modern-form"
        @submit.prevent
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="项目名称" prop="name">
              <el-input v-model="formData.name" placeholder="请输入项目名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="项目状态" prop="status">
              <el-select v-model="formData.status" placeholder="请选择状态" style="width: 100%">
                <el-option label="进行中" value="进行中" />
                <el-option label="已完成" value="已完成" />
                <el-option label="已暂停" value="已暂停" />
                <el-option label="已取消" value="已取消" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="项目描述" prop="description">
          <el-input 
            v-model="formData.description" 
            type="textarea" 
            placeholder="请输入项目描述"
            :rows="3"
          />
        </el-form-item>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开始日期" prop="startDate">
              <el-date-picker
                v-model="formData.startDate"
                type="date"
                placeholder="选择开始日期"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束日期" prop="endDate">
              <el-date-picker
                v-model="formData.endDate"
                type="date"
                placeholder="选择结束日期"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="项目预算" prop="budget">
              <el-input-number
                v-model="formData.budget"
                :min="0"
                :precision="2"
                placeholder="请输入预算"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属部门" prop="departmentId">
              <el-select v-model="formData.departmentId" placeholder="请选择部门" style="width: 100%">
                <el-option 
                  v-for="dept in departments" 
                  :key="dept.id" 
                  :label="dept.name" 
                  :value="dept.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="项目负责人" prop="leaderId">
          <el-select v-model="formData.leaderId" placeholder="请选择负责人" style="width: 100%">
            <el-option 
              v-for="employee in employees" 
              :key="employee.id" 
              :label="employee.name" 
              :value="employee.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm" :loading="submitting">
            {{ submitting ? '提交中...' : '确定' }}
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 项目成员管理对话框 -->
    <el-dialog title="项目成员管理" v-model="membersDialogVisible" width="600px" class="modern-dialog" destroy-on-close>
      <div style="margin-bottom: 16px;">
        <el-button type="primary" @click="showAddMemberDialog">
          <el-icon><Plus /></el-icon>
          添加成员
        </el-button>
      </div>
      
      <el-table :data="projectMembers" style="width: 100%">
        <el-table-column prop="employeeName" label="员工姓名" />
        <el-table-column prop="role" label="角色" />
        <el-table-column prop="joinDate" label="加入日期" width="120">
          <template #default="scope">
            {{ formatDate(scope.row.joinDate) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="scope">
            <el-button 
              size="small" 
              type="danger" 
              @click="removeMember(scope.row)"
            >
              移除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <template #footer>
        <el-button @click="membersDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 添加成员对话框 -->
    <el-dialog title="添加项目成员" v-model="addMemberDialogVisible" width="400px">
      <el-form :model="memberForm" label-width="80px">
        <el-form-item label="选择员工">
          <el-select v-model="memberForm.employeeId" placeholder="请选择员工" style="width: 100%">
            <el-option 
              v-for="employee in availableEmployees" 
              :key="employee.id" 
              :label="employee.name" 
              :value="employee.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="角色">
          <el-input v-model="memberForm.role" placeholder="请输入角色（如：开发工程师）" />
        </el-form-item>
        <el-form-item label="加入日期">
          <el-date-picker
            v-model="memberForm.joinedDate"
            type="date"
            placeholder="请选择加入日期"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="addMemberDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="addMember">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { projectApi, departmentApi, employeeApi } from '@/api'

const loading = ref(false)
const submitting = ref(false)
const projects = ref([])
const departments = ref([])
const employees = ref([])
const projectMembers = ref([])
const dialogVisible = ref(false)
const membersDialogVisible = ref(false)
const addMemberDialogVisible = ref(false)
const formRef = ref()
const isEdit = ref(false)
const selectedProject = ref(null)

const formData = ref({
  name: '',
  description: '',
  status: '进行中',
  startDate: null,
  endDate: null,
  budget: 0,
  departmentId: null,
  leaderId: null
})

const memberForm = ref({
  employeeId: null,
  role: '',
  joinedDate: new Date()
})

const rules = {
  name: [
    { required: true, message: '请输入项目名称', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择项目状态', trigger: 'change' }
  ],
  departmentId: [
    { required: true, message: '请选择所属部门', trigger: 'change' }
  ]
}

// 计算属性
const dialogTitle = computed(() => isEdit.value ? '编辑项目' : '添加项目')

const availableEmployees = computed(() => {
  const memberIds = projectMembers.value.map(m => m.employeeId)
  return employees.value.filter(emp => !memberIds.includes(emp.id))
})

// 方法
const loadProjects = async () => {
  loading.value = true
  try {
    const data = await projectApi.getAll() || []
    projects.value = data.map(project => ({
      ...project,
      departmentName: getDepartmentName(project.departmentId)
    }))
  } catch (error) {
    ElMessage.error('加载项目数据失败')
  } finally {
    loading.value = false
  }
}

const loadDepartments = async () => {
  try {
    departments.value = await departmentApi.getAll() || []
  } catch (error) {
    console.error('加载部门数据失败:', error)
  }
}

const loadEmployees = async () => {
  try {
    employees.value = await employeeApi.getAll() || []
  } catch (error) {
    console.error('加载员工数据失败:', error)
  }
}

const getDepartmentName = (departmentId) => {
  const dept = departments.value.find(d => d.id === departmentId)
  return dept ? dept.name : '--'
}

const showAddDialog = () => {
  isEdit.value = false
  formData.value = {
    name: '',
    description: '',
    status: '进行中',
    startDate: null,
    endDate: null,
    budget: 0,
    departmentId: null,
    leaderId: null
  }
  dialogVisible.value = true
}

const editProject = (project) => {
  isEdit.value = true
  formData.value = {
    name: project.name,
    description: project.description,
    status: project.status,
    startDate: project.startDate ? new Date(project.startDate) : null,
    endDate: project.endDate ? new Date(project.endDate) : null,
    budget: project.budget,
    departmentId: project.departmentId,
    leaderId: project.leaderId
  }
  selectedProject.value = project
  dialogVisible.value = true
}

const submitForm = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        const submitData = {
          ...formData.value,
          startDate: formData.value.startDate ? formData.value.startDate.toISOString().split('T')[0] : null,
          endDate: formData.value.endDate ? formData.value.endDate.toISOString().split('T')[0] : null
        }
        
        if (isEdit.value) {
          await projectApi.update(selectedProject.value.id, submitData)
          ElMessage.success('编辑成功')
        } else {
          await projectApi.create(submitData)
          ElMessage.success('添加成功')
        }
        dialogVisible.value = false
        await loadProjects()
      } catch (error) {
        ElMessage.error(isEdit.value ? '编辑失败' : '添加失败')
      } finally {
        submitting.value = false
      }
    }
  })
}

// 处理下拉菜单命令
const handleCommand = (command, project) => {
  switch (command) {
    case 'edit':
      editProject(project)
      break
    case 'members':
      manageMembers(project)
      break
    case 'delete':
      deleteProject(project)
      break
  }
}

const manageMembers = async (project) => {
  selectedProject.value = project
  try {
    const members = await projectApi.getMembers(project.id) || []
    projectMembers.value = members.map(member => ({
      ...member,
      employeeName: getEmployeeName(member.employeeId)
    }))
    membersDialogVisible.value = true
  } catch (error) {
    ElMessage.error('加载项目成员失败')
  }
}

const getEmployeeName = (employeeId) => {
  const emp = employees.value.find(e => e.id === employeeId)
  return emp ? emp.name : '--'
}

const showAddMemberDialog = () => {
  memberForm.value = {
    employeeId: null,
    role: '',
    joinedDate: new Date()
  }
  addMemberDialogVisible.value = true
}

const addMember = async () => {
  if (!memberForm.value.employeeId) {
    ElMessage.warning('请选择员工')
    return
  }
  
  try {
    const joinedDate = memberForm.value.joinedDate ? 
      memberForm.value.joinedDate.toISOString().split('T')[0] : 
      new Date().toISOString().split('T')[0]
      
    await projectApi.addMember(selectedProject.value.id, {
      employeeId: memberForm.value.employeeId,
      role: memberForm.value.role || '项目成员',
      joinedDate: joinedDate
    })
    ElMessage.success('添加成员成功')
    addMemberDialogVisible.value = false
    manageMembers(selectedProject.value)
  } catch (error) {
    ElMessage.error('添加成员失败')
  }
}

const removeMember = async (member) => {
  try {
    await ElMessageBox.confirm(
      `确定要移除成员"${member.employeeName}"吗？`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )
    
    await projectApi.removeMember(selectedProject.value.id, member.employeeId)
    ElMessage.success('移除成员成功')
    manageMembers(selectedProject.value)
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('移除成员失败')
    }
  }
}

const deleteProject = async (project) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除项目"${project.name}"吗？`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )
    
    await projectApi.delete(project.id)
    ElMessage.success('删除成功')
    loadProjects()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const getStatusType = (status) => {
  const typeMap = {
    '进行中': 'success',
    '已完成': 'info',
    '已暂停': 'warning',
    '已取消': 'danger'
  }
  return typeMap[status] || 'info'
}

const formatDate = (date) => {
  if (!date) return '--'
  return new Date(date).toLocaleDateString('zh-CN')
}

const formatCurrency = (amount) => {
  if (!amount) return '0.00'
  return Number(amount).toLocaleString('zh-CN', { minimumFractionDigits: 2 })
}

onMounted(() => {
  Promise.all([
    loadDepartments(),
    loadEmployees()
  ]).then(() => {
    loadProjects()
  })
})
</script>

<style scoped>
/* PC端基础样式 */
.projects-container {
  padding: 24px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 60px);
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* 顶部区域 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  flex-wrap: wrap;
  gap: 16px;
}

.header-left {
  flex: 1;
  min-width: 250px;
}

.page-title {
  display: flex;
  align-items: center;
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 600;
  color: #2c3e50;
}

.title-icon {
  margin-right: 12px;
  color: #409EFF;
}

.page-subtitle {
  margin: 0;
  color: #6c757d;
  font-size: 14px;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

/* 表格样式 */
.data-card {
  border-radius: 8px;
  overflow: hidden;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: #2c3e50;
}

.count-tag {
  margin-left: 8px;
}

.modern-table {
  :deep(.el-table__header) th {
    background-color: var(--el-table-header-bg-color);
    height: 48px;
    font-weight: 500;
  }

  :deep(.el-table__row) td {
    padding: 12px 0;
  }
}

.project-name {
  display: flex;
  align-items: center;
  gap: 8px;
}

.project-icon {
  color: #409EFF;
}

.name-text {
  font-weight: 500;
  color: #2c3e50;
}

.budget-text {
  color: #67C23A;
  font-weight: 500;
}

/* 操作按钮组 */
.action-buttons {
  display: flex;
  justify-content: center;
  
  .desktop-buttons {
    display: flex;
    gap: 2px;
    
    .el-button {
      padding: 6px 12px;
    }
  }
  
  .mobile-dropdown {
    display: none;
  }
}

/* 弹窗样式 */
.modern-dialog {
  :deep(.el-dialog) {
    border-radius: 8px;
  }

  :deep(.el-dialog__header) {
    margin: 0;
    padding: 20px;
    border-bottom: 1px solid var(--el-border-color-lighter);
  }

  :deep(.el-dialog__body) {
    padding: 24px;
  }

  :deep(.el-dialog__footer) {
    margin: 0;
    padding: 20px;
    border-top: 1px solid var(--el-border-color-lighter);
  }
}

.modern-form {
  .el-form-item {
    margin-bottom: 20px;
  }
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

/* PC端操作列宽度 */
@media screen and (min-width: 769px) {
  .modern-table {
    :deep(.operation-column) {
      width: 220px !important;
    }
  }
}

/* 移动端适配 */
@media screen and (max-width: 768px) {
  .projects-container {
    padding: 16px;
    gap: 16px;
  }

  .page-header {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
  }

  .header-left {
    min-width: unset;
  }

  .page-title {
    font-size: 20px;
  }

  .header-actions {
    width: 100%;
    
    .add-btn {
      width: 100%;
    }
  }

  .modern-dialog {
    :deep(.el-dialog) {
      width: 90% !important;
      margin: 10vh auto;
    }

    :deep(.el-dialog__body) {
      padding: 16px;
    }
  }

  .action-buttons {
    .desktop-buttons {
      display: none;
    }
    
    .mobile-dropdown {
      display: block;
    }
  }

  .modern-table {
    :deep(.el-table__header) th {
      height: 40px;
      font-size: 14px;
    }

    :deep(.el-table__row) td {
      padding: 8px 0;
    }
    
    :deep(.operation-column) {
      width: 80px !important;
    }
  }
}

/* 平板端适配 */
@media screen and (min-width: 769px) and (max-width: 1024px) {
  .projects-container {
    padding: 20px;
  }

  .modern-dialog {
    :deep(.el-dialog) {
      width: 80% !important;
    }
  }
}
</style> 