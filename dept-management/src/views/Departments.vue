<template>
  <div class="departments-container">
    <!-- 顶部操作栏 -->
    <div class="page-header">
      <div class="header-left">
        <h2 class="page-title">部门管理</h2>
        <p class="page-subtitle">管理公司组织架构和部门信息</p>
      </div>
      <div class="header-actions">
        <el-button type="primary" @click="showAddDialog" class="add-btn">
          <el-icon><Plus /></el-icon>
          添加部门
        </el-button>
      </div>
    </div>

    <!-- 数据展示卡片 -->
    <el-card class="data-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <div class="card-title">
            <span>部门列表</span>
            <el-tag type="info" class="count-tag" size="small">{{ departments.length }} 个部门</el-tag>
          </div>
          <div class="card-actions">
            <el-button size="small" @click="loadDepartments" :loading="loading">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>
        </div>
      </template>

      <!-- 表格部分 -->
      <el-table 
        :data="departments" 
        style="width: 100%"
        v-loading="loading"
        row-key="id"
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
        class="modern-table"
      >
        <el-table-column 
          prop="name" 
          label="部门名称" 
          min-width="200"
          class-name="el-table__column--name"
        >
          <template #default="scope">
            <div class="department-name">
              <el-icon class="dept-icon"><OfficeBuilding /></el-icon>
              <span class="dept-text">{{ scope.row.name }}</span>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column 
          prop="description" 
          label="描述" 
          min-width="250" 
          show-overflow-tooltip
          class-name="el-table__column--description"
        />
        
        <el-table-column 
          prop="managerId" 
          label="负责人" 
          width="120"
          class-name="el-table__column--manager"
        >
          <template #default="scope">
            <el-tag v-if="scope.row.managerId" size="small" type="success">
              {{ scope.row.managerId }}
            </el-tag>
            <span v-else class="no-manager">待分配</span>
          </template>
        </el-table-column>
        
        <el-table-column 
          prop="createdAt" 
          label="创建时间" 
          width="120"
          class-name="el-table__column--date"
        >
          <template #default="scope">
            {{ formatDate(scope.row.createdAt) }}
          </template>
        </el-table-column>
        
        <el-table-column 
          label="操作" 
          min-width="60" 
          fixed="right"
          class-name="operation-column"
        >
          <template #default="scope">
            <div class="action-buttons">
              <el-button-group class="desktop-buttons">
                <el-button size="small" type="primary" @click="editDepartment(scope.row)">
                  <el-icon><Edit /></el-icon>
                </el-button>
                <el-button size="small" type="warning" @click="assignManager(scope.row)">
                  <el-icon><User /></el-icon>
                </el-button>
                <el-button size="small" type="success" @click="addSubDepartment(scope.row)">
                  <el-icon><Plus /></el-icon>
                </el-button>
                <el-button size="small" type="danger" @click="deleteDepartment(scope.row)">
                  <el-icon><Delete /></el-icon>
                </el-button>
              </el-button-group>
              
              <!-- 移动端下拉菜单 -->
              <el-dropdown class="mobile-dropdown" trigger="click" @command="handleDeptCommand($event, scope.row)">
                <el-button size="small" type="primary">
                  <el-icon><More /></el-icon>
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="edit">
                      <el-icon><Edit /></el-icon>
                      编辑
                    </el-dropdown-item>
                    <el-dropdown-item command="manager">
                      <el-icon><User /></el-icon>
                      分配经理
                    </el-dropdown-item>
                    <el-dropdown-item command="sub">
                      <el-icon><Plus /></el-icon>
                      子部门
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

    <!-- 添加/编辑部门对话框 -->
    <el-dialog 
      :title="dialogTitle" 
      v-model="dialogVisible" 
      width="400px"
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
        <el-form-item label="名称" prop="name" class="form-item">
          <el-input 
            v-model="formData.name" 
            placeholder="请输入部门名称"
            clearable
          />
        </el-form-item>
        
        <el-form-item label="描述" prop="description" class="form-item">
          <el-input 
            v-model="formData.description" 
            type="textarea" 
            placeholder="请输入部门描述（可选）"
            :rows="3"
            show-word-limit
            maxlength="200"
          />
        </el-form-item>
        
        <el-form-item label="上级部门" prop="parentId" class="form-item">
          <el-select 
            v-model="formData.parentId" 
            placeholder="请选择上级部门（可选）"
            clearable
            style="width: 100%"
            filterable
          >
            <el-option 
              v-for="dept in departmentOptions" 
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
  </div>
</template>

<style scoped>
.departments-container {
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.header-left {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.page-title {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.title-icon {
  font-size: 24px;
  color: #409EFF;
}

.page-subtitle {
  margin: 0;
  font-size: 14px;
  color: #909399;
}

.data-card {
  border-radius: 8px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0;
}

.card-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 500;
  color: #303133;
}

.count-tag {
  margin-left: 8px;
  font-weight: normal;
}

.department-name {
  display: flex;
  align-items: center;
  gap: 8px;
}

.dept-icon {
  color: #409EFF;
}

.dept-text {
  font-weight: 500;
}

.description-text {
  color: #606266;
}

.no-manager {
  color: #909399;
  font-size: 13px;
}

.time-info {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #606266;
  font-size: 13px;
}

.action-buttons {
  display: flex;
  gap: 8px;
}

/* PC端样式 */
.action-buttons {
  display: flex;
  justify-content: center;
  
  .desktop-buttons {
    display: flex;
    gap: 2px;
    
    .el-button {
      padding: 6px 12px;
      margin: 0;
    }
  }
  
  .mobile-dropdown {
    display: none;
  }
}

.modern-dialog {
  :deep(.el-dialog) {
    width: 450px;
    margin: 15vh auto;
  }

  :deep(.el-dialog__header) {
    margin: 0;
    padding: 20px;
    border-bottom: 1px solid var(--el-border-color-lighter);
  }

  :deep(.el-dialog__body) {
    padding: 20px;
  }

  :deep(.el-dialog__footer) {
    margin: 0;
    padding: 20px;
    border-top: 1px solid var(--el-border-color-lighter);
  }
}

.modern-table {
  :deep(.el-table__header) th {
    background-color: var(--el-table-header-bg-color);
    height: 48px;
  }

  :deep(.el-table__row) td {
    padding: 12px 0;
  }

  .department-name {
    display: flex;
    align-items: center;
    gap: 8px;
  }
}

/* 移动端样式 */
@media screen and (max-width: 768px) {
  .modern-dialog {
    :deep(.el-dialog) {
      width: 85% !important;
      margin: 5vh auto;
    }

    :deep(.el-dialog__body) {
      padding: 15px;
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
    :deep(.operation-column) {
      width: 60px !important;
    }
  }

  .modern-table {
    :deep(.el-table__header) th {
      height: 44px;
    }

    :deep(.el-table__row) td {
      padding: 8px 0;
    }
  }
}

/* 表格列宽度设置 */
:deep(.el-table) {
  .el-table__header-wrapper,
  .el-table__body-wrapper {
    .cell {
      padding: 0 12px;
    }
  }
}

/* PC端操作列宽度 */
@media screen and (min-width: 769px) {
  .modern-table {
    :deep(.operation-column) {
      width: 180px !important;
    }
  }
}
</style>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { departmentApi, employeeApi } from '@/api'

const loading = ref(false)
const departments = ref([])
const employees = ref([])
const dialogVisible = ref(false)
const managerDialogVisible = ref(false)
const formRef = ref()
const isEdit = ref(false)
const selectedDepartment = ref(null)

const formData = ref({
  name: '',
  description: '',
  parentId: null
})

const managerForm = ref({
  managerId: null
})

const rules = {
  name: [
    { required: true, message: '请输入部门名称', trigger: 'blur' },
    { min: 2, max: 50, message: '部门名称长度在 2 到 50 个字符', trigger: 'blur' }
  ]
}

// 计算属性
const dialogTitle = computed(() => isEdit.value ? '编辑部门信息' : '添加新部门')

const allDepartments = computed(() => {
  const flatten = (list) => {
    let result = []
    list.forEach(item => {
      result.push(item)
      if (item.children && item.children.length > 0) {
        result = result.concat(flatten(item.children))
      }
    })
    return result
  }
  return flatten(departments.value)
})

const tableData = computed(() => buildTree(departments.value))

// 构建树形结构
const buildTree = (list) => {
  const map = {}
  const roots = []
  
  // 创建映射
  list.forEach(item => {
    map[item.id] = { ...item, children: [] }
  })
  
  // 构建树
  list.forEach(item => {
    if (item.parentId && map[item.parentId]) {
      map[item.parentId].children.push(map[item.id])
    } else {
      roots.push(map[item.id])
    }
  })
  
  return roots
}

// 方法
const loadDepartments = async () => {
  loading.value = true
  try {
    departments.value = await departmentApi.getAll() || []
  } catch (error) {
    ElMessage.error('加载部门数据失败')
  } finally {
    loading.value = false
  }
}

const loadEmployees = async () => {
  try {
    employees.value = await employeeApi.getAll() || []
  } catch (error) {
    console.error('加载员工数据失败:', error)
  }
}

const showAddDialog = () => {
  isEdit.value = false
  formData.value = {
    name: '',
    description: '',
    parentId: null
  }
  dialogVisible.value = true
}

const editDepartment = (department) => {
  isEdit.value = true
  formData.value = {
    name: department.name,
    description: department.description,
    parentId: department.parentId
  }
  selectedDepartment.value = department
  dialogVisible.value = true
}

const addSubDepartment = (parent) => {
  isEdit.value = false
  formData.value = {
    name: '',
    description: '',
    parentId: parent.id
  }
  dialogVisible.value = true
}

const submitForm = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (isEdit.value) {
          await departmentApi.update(selectedDepartment.value.id, formData.value)
          ElMessage.success('部门信息编辑成功')
        } else {
          await departmentApi.create(formData.value)
          ElMessage.success('部门创建成功')
        }
        dialogVisible.value = false
        loadDepartments()
      } catch (error) {
        ElMessage.error(isEdit.value ? '编辑失败' : '添加失败')
      }
    }
  })
}

const assignManager = (department) => {
  selectedDepartment.value = department
  managerForm.value.managerId = department.managerId
  managerDialogVisible.value = true
}

const submitManager = async () => {
  try {
    await departmentApi.assignManager(
      selectedDepartment.value.id, 
      managerForm.value.managerId
    )
    ElMessage.success('经理分配成功')
    managerDialogVisible.value = false
    loadDepartments()
  } catch (error) {
    ElMessage.error('分配经理失败')
  }
}

const deleteDepartment = async (department) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除部门"${department.name}"吗？此操作不可恢复！`,
      '删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning',
        dangerouslyUseHTMLString: true
      }
    )
    
    await departmentApi.delete(department.id)
    ElMessage.success('部门删除成功')
    loadDepartments()
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

// 添加计算属性用于部门选项过滤
const departmentOptions = computed(() => {
  // 过滤掉当前编辑的部门及其子部门
  return departments.value.filter(dept => {
    if (isEdit.value) {
      return dept.id !== formData.value.id && !isChildDepartment(dept, formData.value.id);
    }
    return true;
  });
});

// 判断是否为子部门
const isChildDepartment = (dept, parentId) => {
  if (!dept.children) return false;
  return dept.children.some(child => 
    child.id === parentId || isChildDepartment(child, parentId)
  );
};

// 处理部门操作下拉菜单命令
const handleDeptCommand = (command, department) => {
  switch (command) {
    case 'edit':
      editDepartment(department)
      break
    case 'manager':
      assignManager(department)
      break
    case 'sub':
      addSubDepartment(department)
      break
    case 'delete':
      deleteDepartment(department)
      break
  }
};

onMounted(() => {
  loadDepartments()
  loadEmployees()
})
</script> 