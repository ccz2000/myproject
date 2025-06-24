<template>
  <div class="adjustment-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h2 class="page-title">
          <el-icon class="title-icon"><TrendCharts /></el-icon>
          薪资调整
        </h2>
        <p class="page-subtitle">管理员工薪资调整记录</p>
      </div>
      <div class="header-actions">
        <el-button type="success" @click="handleAdd" class="add-btn">
          <el-icon><Plus /></el-icon>
          新增调整
        </el-button>
      </div>
    </div>
    
    <!-- 搜索表单 -->
    <div class="search-form">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="部门筛选">
          <el-select 
            v-model="searchForm.departmentId" 
            placeholder="选择部门" 
            clearable 
            style="min-width: 180px"
            @change="fetchAdjustments"
          >
            <el-option
              v-for="dept in departmentOptions"
              :key="dept.id"
              :label="dept.name"
              :value="dept.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchAdjustments" :loading="loading">
            <el-icon><Search /></el-icon>
            查询
          </el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 统计信息 -->
    <div class="stats-row">
      <el-card class="stat-card" shadow="hover">
        <div class="stat-content">
          <div class="stat-icon total">
            <el-icon><Document /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-number">{{ tableData.length }}</div>
            <div class="stat-label">调整记录</div>
          </div>
        </div>
      </el-card>
      <el-card class="stat-card" shadow="hover">
        <div class="stat-content">
          <div class="stat-icon increase">
            <el-icon><TrendCharts /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-number">{{ increaseCount }}</div>
            <div class="stat-label">升薪记录</div>
          </div>
        </div>
      </el-card>
      <el-card class="stat-card" shadow="hover">
        <div class="stat-content">
          <div class="stat-icon decrease">
            <el-icon><Bottom /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-number">{{ decreaseCount }}</div>
            <div class="stat-label">降薪记录</div>
          </div>
        </div>
      </el-card>
      <el-card class="stat-card" shadow="hover">
        <div class="stat-content">
          <div class="stat-icon amount">
            <el-icon><Money /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-number">¥{{ formatMoney(totalAdjustment) }}</div>
            <div class="stat-label">调整总额</div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 数据表格 -->
    <div class="table-container">
      <el-table 
        :data="tableData" 
        v-loading="loading" 
        border
        style="width: 100%"
        :header-cell-style="{ background: '#f8fafc', color: '#606266', fontWeight: '600' }"
        table-layout="auto"
      >
        <el-table-column prop="employeeName" label="员工姓名" min-width="120" show-overflow-tooltip>
          <template #default="scope">
            {{ scope.row.employeeName || '未知' }}
          </template>
        </el-table-column>
        
        <el-table-column prop="departmentName" label="部门" min-width="120" show-overflow-tooltip>
          <template #default="scope">
            <el-tag size="small">{{ scope.row.departmentName || '未知' }}</el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="adjustmentType" label="调整类型" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.adjustmentType === 1 ? 'success' : 'danger'" size="small">
              <el-icon>
                <component :is="scope.row.adjustmentType === 1 ? 'TrendCharts' : 'Bottom'" />
              </el-icon>
              {{ scope.row.adjustmentType === 1 ? '升薪' : '降薪' }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="beforeSalary" label="调整前" min-width="120" align="right">
          <template #default="scope">
            ¥{{ formatMoney(scope.row.beforeSalary) }}
          </template>
        </el-table-column>
        
        <el-table-column prop="afterSalary" label="调整后" min-width="120" align="right">
          <template #default="scope">
            <span :style="{ color: scope.row.adjustmentType === 1 ? '#67C23A' : '#F56C6C', fontWeight: 'bold' }">
              ¥{{ formatMoney(scope.row.afterSalary) }}
            </span>
          </template>
        </el-table-column>
        
        <el-table-column prop="adjustmentReason" label="调整原因" min-width="150" show-overflow-tooltip />
        
        <el-table-column prop="effectiveDate" label="生效日期" width="120" />
        
        <el-table-column prop="approverName" label="审批人" min-width="100" show-overflow-tooltip>
          <template #default="scope">
            {{ scope.row.approverName || '未知' }}
          </template>
        </el-table-column>
        
        <el-table-column label="操作" min-width="80" fixed="right" class-name="operation-column">
          <template #default="scope">
            <div class="action-buttons">
              <el-button-group class="desktop-buttons">
                <el-button 
                  size="small" 
                  type="primary"
                  @click="handleEdit(scope.row)"
                >
                  <el-icon><Edit /></el-icon>
                  编辑
                </el-button>
                <el-button 
                  size="small" 
                  type="danger" 
                  @click="handleDelete(scope.row)"
                >
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
    </div>

    <!-- 新增/编辑对话框 -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="dialogType === 'add' ? '新增调整' : '编辑调整'" 
      width="600px"
      :close-on-click-modal="false"
      class="modern-dialog"
      destroy-on-close
    >
      <el-form 
        :model="form" 
        :rules="rules" 
        ref="formRef" 
        label-width="80px"
        class="modern-form"
        @submit.prevent
      >
        <el-form-item label="员工" prop="employeeId">
          <el-select v-model="form.employeeId" placeholder="选择员工" style="min-width: 200px">
            <el-option
              v-for="emp in employeeOptions"
              :key="emp.id"
              :label="emp.name"
              :value="emp.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="调整类型" prop="adjustmentType">
          <el-radio-group v-model="form.adjustmentType">
            <el-radio :label="1">升薪</el-radio>
            <el-radio :label="2">降薪</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="调整前工资" prop="beforeSalary">
          <el-input-number v-model="form.beforeSalary" />
        </el-form-item>
        <el-form-item label="调整后工资" prop="afterSalary">
          <el-input-number v-model="form.afterSalary" />
        </el-form-item>
        <el-form-item label="调整原因" prop="adjustmentReason">
          <el-input v-model="form.adjustmentReason" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="生效日期" prop="effectiveDate">
          <el-date-picker v-model="form.effectiveDate" type="date" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="审批人" prop="approverId">
          <el-select v-model="form.approverId" placeholder="选择审批人" style="min-width: 200px">
            <el-option
              v-for="emp in employeeOptions"
              :key="emp.id"
              :label="emp.name"
              :value="emp.id">
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Document, TrendCharts, Bottom, Money, Plus, Search, Edit, Delete, More } from '@element-plus/icons-vue'
import salaryApi from '@/api/salary'
import { departmentApi, employeeApi } from '@/api'

// 数据
const loading = ref(false)
const submitting = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogType = ref('add')
const formRef = ref()

// 选项
const departmentOptions = ref([])
const employeeOptions = ref([])

// 搜索表单
const searchForm = reactive({
  departmentId: ''
})

// 编辑表单
const form = reactive({
  id: null,
  employeeId: '',
  adjustmentType: 1,
  beforeSalary: 0,
  afterSalary: 0,
  adjustmentReason: '',
  effectiveDate: '',
  approverId: ''
})

// 验证规则
const rules = {
  employeeId: [{ required: true, message: '请选择员工', trigger: 'change' }],
  beforeSalary: [{ required: true, message: '请输入调整前工资', trigger: 'blur' }],
  afterSalary: [{ required: true, message: '请输入调整后工资', trigger: 'blur' }],
  adjustmentReason: [{ required: true, message: '请输入调整原因', trigger: 'blur' }],
  effectiveDate: [{ required: true, message: '请选择生效日期', trigger: 'change' }],
  approverId: [{ required: true, message: '请选择审批人', trigger: 'change' }]
}

// 统计数据
const increaseCount = computed(() => tableData.value.filter(item => item.adjustmentType === 1).length)
const decreaseCount = computed(() => tableData.value.filter(item => item.adjustmentType === 2).length)
const totalAdjustment = computed(() => {
  return tableData.value.reduce((sum, item) => {
    const diff = (Number(item.afterSalary) || 0) - (Number(item.beforeSalary) || 0)
    return sum + diff
  }, 0)
})

// 获取数据
const fetchDepartments = async () => {
  try {
    const res = await departmentApi.getAll()
    departmentOptions.value = Array.isArray(res) ? res : (res.data || [])
  } catch (error) {
    console.error('获取部门列表失败:', error)
  }
}

const fetchEmployees = async () => {
  try {
    const res = await employeeApi.getAll()
    employeeOptions.value = Array.isArray(res) ? res : (res.data || [])
  } catch (error) {
    console.error('获取员工列表失败:', error)
  }
}

const fetchAdjustments = async () => {
  loading.value = true
  try {
    let res
    if (searchForm.departmentId) {
      // 根据部门筛选
      res = await salaryApi.getAdjustmentsByDepartment(searchForm.departmentId)
    } else {
      // 获取所有调整记录
      res = await salaryApi.getAllAdjustments()
    }
    tableData.value = Array.isArray(res) ? res : (res.data || [])
  } catch (error) {
    console.error('获取调整记录失败:', error)
    ElMessage.error('获取调整记录失败')
  } finally {
    loading.value = false
  }
}

// 操作
const handleAdd = () => {
  dialogType.value = 'add'
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogType.value = 'edit'
  Object.assign(form, row)
  dialogVisible.value = true
}

// 重置搜索
const resetSearch = () => {
  searchForm.departmentId = ''
  fetchAdjustments()
}

// 处理下拉菜单命令
const handleCommand = (command, row) => {
  switch (command) {
    case 'edit':
      handleEdit(row)
      break
    case 'delete':
      handleDelete(row)
      break
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确认删除该调整记录吗？', '提示', { type: 'warning' })
    await salaryApi.deleteAdjustment(row.id)
    ElMessage.success('删除成功')
    fetchAdjustments()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    submitting.value = true
    
    if (dialogType.value === 'add') {
      await salaryApi.createAdjustment(form)
      ElMessage.success('新增成功')
    } else {
      await salaryApi.updateAdjustment(form.id, form)
      ElMessage.success('更新成功')
    }
    
    dialogVisible.value = false
    fetchAdjustments()
  } catch (error) {
    ElMessage.error('操作失败')
  } finally {
    submitting.value = false
  }
}

const resetForm = () => {
  Object.assign(form, {
    id: null,
    employeeId: '',
    adjustmentType: 1,
    beforeSalary: 0,
    afterSalary: 0,
    adjustmentReason: '',
    effectiveDate: '',
    approverId: ''
  })
}

// 格式化方法
const formatMoney = (amount) => {
  if (!amount) return '0'
  return Number(amount).toLocaleString()
}

// 初始化
onMounted(() => {
  fetchDepartments()
  fetchEmployees()
  fetchAdjustments()
})
</script>

<style scoped>
.adjustment-container {
  padding: 24px;
  max-width: 100%;
  overflow-x: auto;
  background: #f5f7fa;
  min-height: 100vh;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
  background: white;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
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
  color: #10b981;
  font-size: 32px;
}

.page-subtitle {
  margin: 0;
  color: #6b7280;
  font-size: 16px;
  font-weight: 400;
}

.header-actions {
  display: flex;
  gap: 12px;
  align-items: center;
}

.add-btn {
  height: 40px;
  padding: 0 20px;
  border-radius: 8px;
  font-weight: 500;
}

.search-form {
  background: white;
  padding: 24px;
  border-radius: 12px;
  margin-bottom: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.stats-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  border-radius: 12px;
  border: none;
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 8px;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
  flex-shrink: 0;
}

.stat-icon.total {
  background: linear-gradient(135deg, #3b82f6, #1d4ed8);
}

.stat-icon.increase {
  background: linear-gradient(135deg, #10b981, #059669);
}

.stat-icon.decrease {
  background: linear-gradient(135deg, #ef4444, #dc2626);
}

.stat-icon.amount {
  background: linear-gradient(135deg, #f59e0b, #d97706);
}

.stat-info {
  flex: 1;
}

.stat-number {
  font-size: 24px;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 4px;
  line-height: 1.2;
}

.stat-label {
  font-size: 14px;
  color: #6b7280;
  font-weight: 500;
}

.table-container {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  overflow: hidden;
}

/* 操作按钮样式 */
.action-buttons {
  display: flex;
  justify-content: center;
}

.desktop-buttons {
  display: flex;
  gap: 8px;
}

.mobile-dropdown {
  display: none;
}

.modern-dialog {
  border-radius: 12px;
}

.modern-form {
  padding: 8px 0;
}

/* PC端操作列样式 */
@media (min-width: 769px) {
  :deep(.operation-column) {
    width: 180px !important;
    min-width: 180px !important;
  }
}

/* 移动端响应式调整 */
@media (max-width: 768px) {
  .adjustment-container {
    padding: 12px;
    background: #f8fafc;
  }
  
  .page-header {
    flex-direction: column;
    align-items: stretch;
    padding: 20px;
    margin-bottom: 16px;
  }
  
  .header-left {
    margin-bottom: 16px;
  }
  
  .page-title {
    font-size: 24px;
  }
  
  .title-icon {
    font-size: 28px;
  }
  
  .page-subtitle {
    font-size: 14px;
  }
  
  .header-actions {
    justify-content: stretch;
  }
  
  .add-btn {
    width: 100%;
    justify-content: center;
  }
  
  .search-form {
    padding: 20px;
    margin-bottom: 16px;
  }
  
  .stats-row {
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
    margin-bottom: 16px;
  }
  
  .stat-content {
    gap: 12px;
    padding: 4px;
  }
  
  .stat-icon {
    width: 48px;
    height: 48px;
    font-size: 20px;
  }
  
  .stat-number {
    font-size: 20px;
  }
  
  .stat-label {
    font-size: 12px;
  }
  
  /* 移动端操作列 */
  .desktop-buttons {
    display: none;
  }
  
  .mobile-dropdown {
    display: block;
  }
  
  :deep(.operation-column) {
    width: 80px !important;
    min-width: 80px !important;
  }
}

@media (max-width: 480px) {
  .stats-row {
    grid-template-columns: 1fr;
    gap: 8px;
  }
  
  .stat-content {
    padding: 8px;
  }
}
</style>
