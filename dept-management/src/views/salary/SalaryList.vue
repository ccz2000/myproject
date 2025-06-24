<template>
  <div class="salary-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h2 class="page-title">
          <el-icon class="title-icon"><Money /></el-icon>
          薪资管理
        </h2>
        <p class="page-subtitle">管理员工薪资发放和记录</p>
      </div>
      <div class="header-actions">
        <el-button type="primary" @click="handleAdd" class="add-btn">
          <el-icon><Plus /></el-icon>
          新增薪资
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
            @change="handleSearch"
          >
            <el-option 
              v-for="dept in departmentOptions" 
              :key="dept.id" 
              :label="dept.name" 
              :value="dept.id"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="月份筛选">
          <el-date-picker
            v-model="searchForm.month"
            type="month"
            placeholder="选择月份"
            format="YYYY-MM"
            value-format="YYYY-MM"
            style="min-width: 160px"
            @change="handleSearch"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleSearch" :loading="loading">查询</el-button>
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
            <div class="stat-label">总记录数</div>
          </div>
        </div>
      </el-card>
      <el-card class="stat-card" shadow="hover">
        <div class="stat-content">
          <div class="stat-icon paid">
            <el-icon><CircleCheck /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-number">{{ paidCount }}</div>
            <div class="stat-label">已发放</div>
          </div>
        </div>
      </el-card>
      <el-card class="stat-card" shadow="hover">
        <div class="stat-content">
          <div class="stat-icon unpaid">
            <el-icon><Clock /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-number">{{ unpaidCount }}</div>
            <div class="stat-label">待发放</div>
          </div>
        </div>
      </el-card>
      <el-card class="stat-card" shadow="hover">
        <div class="stat-content">
          <div class="stat-icon amount">
            <el-icon><Money /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-number">¥{{ formatMoney(totalAmount) }}</div>
            <div class="stat-label">总金额</div>
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
        
        <el-table-column prop="position" label="职位" min-width="140" show-overflow-tooltip>
          <template #default="scope">
            {{ scope.row.position || '未知' }}
          </template>
        </el-table-column>
        
        <el-table-column prop="salaryMonth" label="薪资月份" width="120">
          <template #default="scope">
            {{ formatMonth(scope.row.salaryMonth) }}
          </template>
        </el-table-column>
        
        <el-table-column prop="baseSalary" label="基本工资" min-width="120" align="right">
          <template #default="scope">
            ¥{{ formatMoney(scope.row.baseSalary) }}
          </template>
        </el-table-column>
        
        <el-table-column prop="bonus" label="奖金" min-width="100" align="right">
          <template #default="scope">
            ¥{{ formatMoney(scope.row.bonus) }}
          </template>
        </el-table-column>
        
        <el-table-column prop="netSalary" label="实发工资" min-width="130" align="right">
          <template #default="scope">
            <span style="color: #67C23A; font-weight: bold;">
              ¥{{ formatMoney(scope.row.netSalary) }}
            </span>
          </template>
        </el-table-column>
        
        <el-table-column prop="paymentStatus" label="发放状态" width="100">
          <template #default="scope">
            <el-tag 
              :type="scope.row.paymentStatus === 1 ? 'success' : 'warning'"
              size="small"
            >
              {{ scope.row.paymentStatus === 1 ? '已发放' : '未发放' }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="paymentDate" label="发放时间" min-width="170" show-overflow-tooltip>
          <template #default="scope">
            {{ scope.row.paymentDate ? formatDateTime(scope.row.paymentDate) : '-' }}
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
                  :disabled="scope.row.paymentStatus === 1"
                >
                  <el-icon><Edit /></el-icon>
                  编辑
                </el-button>
                <el-button 
                  size="small" 
                  type="success" 
                  @click="handlePay(scope.row)"
                  :disabled="scope.row.paymentStatus === 1"
                >
                  <el-icon><Money /></el-icon>
                  发放
                </el-button>
                <el-button 
                  size="small" 
                  type="danger" 
                  @click="handleDelete(scope.row)"
                  :disabled="scope.row.paymentStatus === 1"
                >
                  <el-icon><Delete /></el-icon>
                  删除
                </el-button>
              </el-button-group>
              
              <!-- 移动端下拉菜单 -->
              <el-dropdown class="mobile-dropdown" trigger="click" @command="handleCommand($event, scope.row)">
                <el-button size="small" type="primary" :disabled="scope.row.paymentStatus === 1">
                  <el-icon><More /></el-icon>
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="edit" :disabled="scope.row.paymentStatus === 1">
                      <el-icon><Edit /></el-icon>
                      编辑
                    </el-dropdown-item>
                    <el-dropdown-item command="pay" :disabled="scope.row.paymentStatus === 1">
                      <el-icon><Money /></el-icon>
                      发放
                    </el-dropdown-item>
                    <el-dropdown-item command="delete" divided :disabled="scope.row.paymentStatus === 1">
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
      :title="dialogType === 'add' ? '新增薪资记录' : '编辑薪资记录'" 
      v-model="dialogVisible" 
      width="600px"
      :close-on-click-modal="false"
      class="modern-dialog"
      destroy-on-close
    >
      <el-form 
        :model="salaryForm" 
        :rules="rules" 
        ref="salaryFormRef" 
        label-width="80px"
        class="modern-form"
        @submit.prevent
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="员工" prop="employeeId">
              <el-select 
                v-model="salaryForm.employeeId" 
                placeholder="请选择员工"
                style="min-width: 200px"
                filterable
              >
                <el-option
                  v-for="item in employeeOptions"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="工资月份" prop="salaryMonth">
              <el-date-picker
                v-model="salaryForm.salaryMonth"
                type="month"
                placeholder="选择月份"
                format="YYYY-MM"
                value-format="YYYY-MM"
                style="min-width: 180px"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="基本工资" prop="baseSalary">
              <el-input-number
                v-model="salaryForm.baseSalary"
                :precision="2"
                :min="0"
                style="width: 100%"
                @change="calculateNetSalary"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="奖金">
              <el-input-number
                v-model="salaryForm.bonus"
                :precision="2"
                :min="0"
                style="width: 100%"
                @change="calculateNetSalary"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="加班费">
              <el-input-number
                v-model="salaryForm.overtimePay"
                :precision="2"
                :min="0"
                style="width: 100%"
                @change="calculateNetSalary"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="社保扣除">
              <el-input-number
                v-model="salaryForm.socialSecurity"
                :precision="2"
                :min="0"
                style="width: 100%"
                @change="calculateNetSalary"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="个税扣除">
              <el-input-number
                v-model="salaryForm.tax"
                :precision="2"
                :min="0"
                style="width: 100%"
                @change="calculateNetSalary"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="实发工资">
              <el-input-number
                v-model="salaryForm.netSalary"
                :precision="2"
                :disabled="true"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="备注">
          <el-input
            v-model="salaryForm.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息（可选）"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          {{ dialogType === 'add' ? '确认新增' : '确认修改' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Document, CircleCheck, Clock, Money, Plus, Edit, Delete, More } from '@element-plus/icons-vue'
import salaryApi from '@/api/salary'
import { departmentApi, employeeApi } from '@/api'

// 响应式数据
const loading = ref(false)
const submitting = ref(false)
const tableData = ref([])

// 搜索表单
const searchForm = reactive({
  departmentId: '',
  month: ''
})

// 统计数据
const paidCount = computed(() => tableData.value.filter(item => item.paymentStatus === 1).length)
const unpaidCount = computed(() => tableData.value.filter(item => item.paymentStatus === 0).length)
const totalAmount = computed(() => tableData.value.reduce((sum, item) => sum + (Number(item.netSalary) || 0), 0))

// 部门和员工选项
const departmentOptions = ref([])
const employeeOptions = ref([])

// 对话框相关
const dialogVisible = ref(false)
const dialogType = ref('add')
const salaryFormRef = ref(null)
const salaryForm = reactive({
  id: null,
  employeeId: '',
  salaryMonth: '',
  baseSalary: 0,
  bonus: 0,
  overtimePay: 0,
  socialSecurity: 0,
  tax: 0,
  netSalary: 0,
  remark: ''
})

// 表单验证规则
const rules = {
  employeeId: [{ required: true, message: '请选择员工', trigger: 'change' }],
  salaryMonth: [{ required: true, message: '请选择工资月份', trigger: 'change' }],
  baseSalary: [{ required: true, message: '请输入基本工资', trigger: 'blur' }]
}

// 获取部门列表
const fetchDepartments = async () => {
  try {
    const res = await departmentApi.getAll()
    departmentOptions.value = Array.isArray(res) ? res : (res.data || [])
  } catch (error) {
    console.error('获取部门列表失败:', error)
  }
}

// 获取员工列表
const fetchEmployees = async () => {
  try {
    const res = await employeeApi.getAll()
    employeeOptions.value = Array.isArray(res) ? res : (res.data || [])
  } catch (error) {
    console.error('获取员工列表失败:', error)
  }
}

// 获取薪资列表
const fetchSalaryList = async () => {
  loading.value = true
  try {
    const params = {}
    if (searchForm.month) {
      params.month = `${searchForm.month}-01`
    }
    
    let res
    if (searchForm.departmentId) {
      res = await salaryApi.getSalaryByDepartment(searchForm.departmentId, params)
    } else {
      res = await salaryApi.getSalaryList(params)
    }
    
    tableData.value = Array.isArray(res) ? res : (res.data || [])
  } catch (error) {
    console.error('获取薪资列表失败:', error)
    ElMessage.error('获取薪资列表失败')
    tableData.value = []
  } finally {
    loading.value = false
  }
}

// 搜索处理
const handleSearch = () => {
  fetchSalaryList()
}

// 重置搜索
const resetSearch = () => {
  searchForm.departmentId = ''
  searchForm.month = ''
  fetchSalaryList()
}

// 新增处理
const handleAdd = () => {
  dialogType.value = 'add'
  resetForm()
  dialogVisible.value = true
}

// 编辑处理
const handleEdit = (row) => {
  dialogType.value = 'edit'
  Object.assign(salaryForm, {
    ...row,
    salaryMonth: formatMonth(row.salaryMonth)
  })
  dialogVisible.value = true
}

// 发放薪资
const handlePay = async (row) => {
  try {
    await ElMessageBox.confirm('确认发放该员工的薪资吗？', '确认发放', {
      type: 'warning'
    })
    
    await salaryApi.paySalary(row.id)
    ElMessage.success('薪资发放成功')
    fetchSalaryList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('发放薪资失败:', error)
      ElMessage.error('发放薪资失败')
    }
  }
}

// 删除处理
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确认删除该薪资记录吗？', '确认删除', {
      type: 'warning'
    })
    
    await salaryApi.deleteSalary(row.id)
    ElMessage.success('删除成功')
    fetchSalaryList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 处理下拉菜单命令
const handleCommand = (command, row) => {
  switch (command) {
    case 'edit':
      handleEdit(row)
      break
    case 'pay':
      handlePay(row)
      break
    case 'delete':
      handleDelete(row)
      break
  }
}

// 表单提交
const handleSubmit = async () => {
  try {
    await salaryFormRef.value.validate()
    submitting.value = true
    
    const formData = {
      ...salaryForm,
      salaryMonth: `${salaryForm.salaryMonth}-01`
    }
    
    if (dialogType.value === 'add') {
      await salaryApi.createSalary(formData)
      ElMessage.success('新增成功')
    } else {
      await salaryApi.updateSalary(formData.id, formData)
      ElMessage.success('更新成功')
    }
    
    dialogVisible.value = false
    fetchSalaryList()
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error('提交失败')
  } finally {
    submitting.value = false
  }
}

// 计算实发工资
const calculateNetSalary = () => {
  const baseSalary = Number(salaryForm.baseSalary) || 0
  const bonus = Number(salaryForm.bonus) || 0
  const overtimePay = Number(salaryForm.overtimePay) || 0
  const socialSecurity = Number(salaryForm.socialSecurity) || 0
  const tax = Number(salaryForm.tax) || 0
  
  salaryForm.netSalary = baseSalary + bonus + overtimePay - socialSecurity - tax
}

// 重置表单
const resetForm = () => {
  Object.assign(salaryForm, {
    id: null,
    employeeId: '',
    salaryMonth: '',
    baseSalary: 0,
    bonus: 0,
    overtimePay: 0,
    socialSecurity: 0,
    tax: 0,
    netSalary: 0,
    remark: ''
  })
}

// 工具函数
const formatMonth = (date) => {
  if (!date) return '-'
  return date.substring(0, 7)
}

const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  return new Date(dateTime).toLocaleString('zh-CN')
}

const formatMoney = (amount) => {
  if (!amount) return '0'
  return Number(amount).toLocaleString()
}

// 组件挂载时获取数据
onMounted(() => {
  fetchDepartments()
  fetchEmployees()
  fetchSalaryList()
})
</script>

<style scoped>
.salary-container {
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

.stat-icon.paid {
  background: linear-gradient(135deg, #10b981, #059669);
}

.stat-icon.unpaid {
  background: linear-gradient(135deg, #f59e0b, #d97706);
}

.stat-icon.amount {
  background: linear-gradient(135deg, #ef4444, #dc2626);
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
    width: 250px !important;
    min-width: 250px !important;
  }
}

/* 移动端响应式调整 */
@media (max-width: 768px) {
  .salary-container {
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