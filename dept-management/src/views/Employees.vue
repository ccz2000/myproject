<template>
  <div class="employees-container">
    <!-- 顶部操作栏 -->
    <div class="page-header">
      <div class="header-left">
        <h2 class="page-title">
          <el-icon class="title-icon"><User /></el-icon>
          员工管理
        </h2>
        <p class="page-subtitle">管理公司员工信息和组织关系</p>
      </div>
      <div class="header-actions">
        <el-select 
          v-model="selectedDepartment" 
          placeholder="按部门筛选"
          clearable
          style="width: 220px;"
          @change="filterByDepartment"
        >
          <el-option 
            v-for="dept in departments" 
            :key="dept.id" 
            :label="dept.name" 
            :value="dept.id"
          >
            <div class="dept-option">
              <el-icon><OfficeBuilding /></el-icon>
              <span>{{ dept.name }}</span>
            </div>
          </el-option>
        </el-select>
        <el-button type="primary" @click="showAddDialog" class="add-btn">
          <el-icon><Plus /></el-icon>
          添加员工
        </el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-row">
      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-icon total">
            <el-icon><UserFilled /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-number">{{ employees.length }}</div>
            <div class="stat-label">员工总数</div>
          </div>
        </div>
      </el-card>
      
      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-icon filtered">
            <el-icon><Search /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-number">{{ filteredEmployees.length }}</div>
            <div class="stat-label">当前显示</div>
          </div>
        </div>
      </el-card>
      
      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-icon departments">
            <el-icon><OfficeBuilding /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-number">{{ departments.length }}</div>
            <div class="stat-label">部门数量</div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 数据展示卡片 -->
    <el-card class="data-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <div class="card-title">
            <el-icon><DataBoard /></el-icon>
            <span>员工列表</span>
            <el-tag v-if="selectedDepartment" type="warning" class="filter-tag">
              {{ getDepartmentName(selectedDepartment) }}
            </el-tag>
          </div>
          <div class="card-actions">
            <el-button size="small" @click="loadEmployees" :loading="loading">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>
        </div>
      </template>

      <el-table 
        :data="filteredEmployees" 
        style="width: 100%"
        v-loading="loading"
        class="modern-table"
        :header-cell-style="{ background: '#f8f9fa', color: '#495057' }"
        :row-style="{ height: '60px' }"
      >
        <el-table-column prop="name" label="员工姓名" width="140">
          <template #default="scope">
            <div class="employee-name">
              <el-avatar :size="32" style="margin-right: 8px;">
                {{ scope.row.name.charAt(0) }}
              </el-avatar>
              <span class="name-text">{{ scope.row.name }}</span>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column prop="email" label="邮箱地址" min-width="200">
          <template #default="scope">
            <div class="email-info">
              <el-icon class="email-icon"><Message /></el-icon>
              <span class="email-text">{{ scope.row.email }}</span>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column prop="position" label="职位" width="150">
          <template #default="scope">
            <el-tag type="primary" size="default">
              {{ scope.row.position }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="departmentName" label="所属部门" width="150">
          <template #default="scope">
            <div class="dept-info">
              <el-icon class="dept-icon"><OfficeBuilding /></el-icon>
              <span>{{ scope.row.departmentName }}</span>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column prop="createdAt" label="入职时间" width="120">
          <template #default="scope">
            <div class="time-info">
              <el-icon><Calendar /></el-icon>
              <span>{{ formatDate(scope.row.createdAt) }}</span>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column label="操作" min-width="80" fixed="right" class-name="operation-column">
          <template #default="scope">
            <div class="action-buttons">
              <el-button-group class="desktop-buttons">
                <el-button size="small" type="primary" @click="editEmployee(scope.row)">
                  <el-icon><Edit /></el-icon>
                  编辑
                </el-button>
                <el-button size="small" type="warning" @click="transferEmployee(scope.row)">
                  <el-icon><Switch /></el-icon>
                  转岗
                </el-button>
                <el-button size="small" type="danger" @click="deleteEmployee(scope.row)">
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
                    <el-dropdown-item command="transfer">
                      <el-icon><Switch /></el-icon>
                      转岗
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

    <!-- 添加/编辑员工对话框 -->
    <el-dialog 
      :title="dialogTitle" 
      v-model="dialogVisible" 
      width="500px"
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
            <el-form-item label="姓名" prop="name" class="form-item">
              <el-input 
                v-model="formData.name" 
                placeholder="请输入员工姓名"
                clearable
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email" class="form-item">
              <el-input 
                v-model="formData.email" 
                placeholder="请输入邮箱地址"
                clearable
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="职位" prop="position" class="form-item">
              <el-input 
                v-model="formData.position" 
                placeholder="请输入职位"
                clearable
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="部门" prop="departmentId" class="form-item">
              <el-select 
                v-model="formData.departmentId" 
                placeholder="请选择部门"
                style="width: 100%"
                filterable
              >
                <el-option 
                  v-for="dept in departments" 
                  :key="dept.id" 
                  :label="dept.name" 
                  :value="dept.id"
                >
                  <div class="dept-option">
                    <el-icon><OfficeBuilding /></el-icon>
                    <span>{{ dept.name }}</span>
                  </div>
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
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

    <!-- 员工转岗对话框 -->
    <el-dialog title="员工转岗" v-model="transferDialogVisible" width="450px" class="modern-dialog" destroy-on-close>
      <el-form :model="transferForm" label-width="80px" class="modern-form">
        <el-form-item label="员工信息">
          <el-card class="employee-card" shadow="never">
            <div class="employee-info">
              <el-avatar :size="40">{{ selectedEmployee?.name?.charAt(0) }}</el-avatar>
              <div class="info-details">
                <div class="emp-name">{{ selectedEmployee?.name }}</div>
                <div class="emp-position">{{ selectedEmployee?.position }}</div>
              </div>
            </div>
          </el-card>
        </el-form-item>
        
        <el-form-item label="原部门">
          <el-input :value="selectedEmployee?.departmentName" readonly>
            <template #prefix>
              <el-icon><OfficeBuilding /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item label="新部门" required>
          <el-select 
            v-model="transferForm.departmentId" 
            placeholder="请选择目标部门"
            style="width: 100%"
            filterable
          >
            <el-option 
              v-for="dept in departments" 
              :key="dept.id" 
              :label="dept.name" 
              :value="dept.id"
            >
              <div class="dept-option">
                <el-icon><OfficeBuilding /></el-icon>
                <span>{{ dept.name }}</span>
                <el-tag 
                  v-if="dept.id === selectedEmployee?.departmentId" 
                  size="small" 
                  type="info"
                >
                  当前部门
                </el-tag>
              </div>
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="transferDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitTransfer" :loading="submitting">
            {{ submitting ? '转岗中...' : '确认转岗' }}
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { employeeApi, departmentApi } from '@/api'

const loading = ref(false)
const submitting = ref(false)
const employees = ref([])
const departments = ref([])
const selectedDepartment = ref(null)
const dialogVisible = ref(false)
const transferDialogVisible = ref(false)
const formRef = ref()
const isEdit = ref(false)
const selectedEmployee = ref(null)

const formData = ref({
  name: '',
  email: '',
  position: '',
  departmentId: null
})

const transferForm = ref({
  departmentId: null
})

const rules = {
  name: [
    { required: true, message: '请输入员工姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '姓名长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  position: [
    { required: true, message: '请输入职位', trigger: 'blur' },
    { min: 2, max: 30, message: '职位长度在 2 到 30 个字符', trigger: 'blur' }
  ],
  departmentId: [
    { required: true, message: '请选择部门', trigger: 'change' }
  ]
}

// 计算属性
const dialogTitle = computed(() => isEdit.value ? '编辑员工信息' : '添加新员工')

const filteredEmployees = computed(() => {
  if (!selectedDepartment.value) {
    return employees.value
  }
  return employees.value.filter(emp => emp.departmentId === selectedDepartment.value)
})

// 方法
const loadEmployees = async () => {
  loading.value = true
  try {
    const data = await employeeApi.getAll() || []
    // 添加部门名称
    employees.value = data.map(emp => ({
      ...emp,
      departmentName: getDepartmentName(emp.departmentId)
    }))
  } catch (error) {
    ElMessage.error('加载员工数据失败')
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

const getDepartmentName = (departmentId) => {
  const dept = departments.value.find(d => d.id === departmentId)
  return dept ? dept.name : '--'
}

const filterByDepartment = () => {
  // 过滤逻辑已在computed中处理
}

const showAddDialog = () => {
  isEdit.value = false
  formData.value = {
    name: '',
    email: '',
    position: '',
    departmentId: null
  }
  dialogVisible.value = true
}

const editEmployee = (employee) => {
  isEdit.value = true
  formData.value = {
    name: employee.name,
    email: employee.email,
    position: employee.position,
    departmentId: employee.departmentId
  }
  selectedEmployee.value = employee
  dialogVisible.value = true
}

const submitForm = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        if (isEdit.value) {
          await employeeApi.update(selectedEmployee.value.id, formData.value)
          ElMessage.success('员工信息编辑成功')
        } else {
          await employeeApi.create(formData.value)
          ElMessage.success('员工添加成功')
        }
        dialogVisible.value = false
        await loadEmployees()
      } catch (error) {
        ElMessage.error(isEdit.value ? '编辑失败' : '添加失败')
      } finally {
        submitting.value = false
      }
    }
  })
}

const transferEmployee = (employee) => {
  selectedEmployee.value = {
    ...employee,
    departmentName: getDepartmentName(employee.departmentId)
  }
  transferForm.value.departmentId = null
  transferDialogVisible.value = true
}

const submitTransfer = async () => {
  if (!transferForm.value.departmentId) {
    ElMessage.warning('请选择目标部门')
    return
  }
  
  if (transferForm.value.departmentId === selectedEmployee.value.departmentId) {
    ElMessage.warning('目标部门不能与当前部门相同')
    return
  }
  
  submitting.value = true
  try {
    await employeeApi.transfer(
      selectedEmployee.value.id, 
      transferForm.value.departmentId
    )
    ElMessage.success('员工转岗成功')
    transferDialogVisible.value = false
    await loadEmployees()
  } catch (error) {
    ElMessage.error('转岗失败')
  } finally {
    submitting.value = false
  }
}

const deleteEmployee = async (employee) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除员工"${employee.name}"吗？此操作不可恢复！`,
      '删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning',
        dangerouslyUseHTMLString: true
      }
    )
    
    await employeeApi.delete(employee.id)
    ElMessage.success('员工删除成功')
    loadEmployees()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const formatDate = (date) => {
  if (!date) return '--'
  return new Date(date).toLocaleDateString('zh-CN')
}

// 处理下拉菜单命令
const handleCommand = (command, employee) => {
  switch (command) {
    case 'edit':
      editEmployee(employee)
      break
    case 'transfer':
      transferEmployee(employee)
      break
    case 'delete':
      deleteEmployee(employee)
      break
  }
}

onMounted(() => {
  loadDepartments().then(() => {
    loadEmployees()
  })
})
</script>

<style scoped>
/* PC端基础样式 */
.employees-container {
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
  color: #67C23A;
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

.add-btn {
  height: 44px;
  padding: 0 24px;
  font-size: 16px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(103, 194, 58, 0.3);
}

/* 统计卡片 */
.stats-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 20px;
}

.stat-card {
  border-radius: 8px;
  transition: transform 0.2s;
  
  &:hover {
    transform: translateY(-2px);
  }
}

.stat-content {
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  color: white;
  font-size: 20px;
}

.stat-icon.total {
  background: linear-gradient(135deg, #67C23A, #85ce61);
}

.stat-icon.filtered {
  background: linear-gradient(135deg, #409EFF, #66b3ff);
}

.stat-icon.departments {
  background: linear-gradient(135deg, #E6A23C, #ebb563);
}

.stat-info {
  flex: 1;
}

.stat-number {
  font-size: 24px;
  font-weight: bold;
  color: #2c3e50;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 13px;
  color: #909399;
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

.filter-tag {
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

  :deep(.el-table__empty-block) {
    min-height: 200px;
  }
}

.employee-name {
  display: flex;
  align-items: center;
}

.name-text {
  font-weight: 500;
  color: #2c3e50;
}

.email-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.email-icon {
  color: #409EFF;
}

.email-text {
  color: #6c757d;
}

.dept-info {
  display: flex;
  align-items: center;
  gap: 6px;
}

.dept-icon {
  color: #E6A23C;
}

.time-info {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #6c757d;
  font-size: 12px;
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
    width: 500px;
    margin: 15vh auto;
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

.dept-option {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 4px 0;
}

/* 移动端适配 */
@media screen and (max-width: 768px) {
  .employees-container {
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
    flex-direction: column;
    width: 100%;
    
    .el-select {
      width: 100% !important;
    }
    
    .add-btn {
      width: 100%;
    }
  }

  .stats-row {
    grid-template-columns: 1fr;
    gap: 12px;
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

/* PC端操作列宽度 */
@media screen and (min-width: 769px) {
  .modern-table {
    :deep(.operation-column) {
      width: 250px !important;
    }
  }
}

/* 平板端适配 */
@media screen and (min-width: 769px) and (max-width: 1024px) {
  .employees-container {
    padding: 20px;
  }

  .stats-row {
    grid-template-columns: repeat(2, 1fr);
  }

  .modern-dialog {
    :deep(.el-dialog) {
      width: 80% !important;
    }
  }
}

.modern-form {
  padding: 20px 0;
}

.modern-form .el-form-item {
  margin-bottom: 20px;
}

.employee-card {
  background-color: #f8f9fa;
  border: 1px solid #e9ecef;
}

.employee-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.info-details {
  flex: 1;
}

.emp-name {
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 4px;
}

.emp-position {
  color: #6c757d;
  font-size: 13px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding-top: 16px;
  border-top: 1px solid #ebeef5;
}

:deep(.el-table__row:hover) {
  background-color: #f8f9ff;
}

:deep(.el-table th) {
  background-color: #fafbfc !important;
  border-bottom: 2px solid #e9ecef;
}

:deep(.el-table td) {
  border-bottom: 1px solid #f1f3f4;
}

:deep(.el-card__header) {
  background-color: #fafbfc;
  border-bottom: 1px solid #e9ecef;
}

:deep(.el-avatar) {
  background-color: #67C23A;
  color: white;
  font-weight: 600;
}
</style> 