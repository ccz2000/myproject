<template>
  <div class="payment-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h2 class="page-title">
          <el-icon class="title-icon"><Wallet /></el-icon>
          薪资发放
        </h2>
        <p class="page-subtitle">批量发放员工薪资</p>
      </div>
      <div class="header-actions">
        <el-button 
          type="success" 
          @click="handleBatchPay" 
          :disabled="!selectedRows.length"
          class="batch-pay-btn"
        >
          <el-icon><Money /></el-icon>
          批量发放 ({{ selectedRows.length }})
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
            @change="fetchSalaryList"
          >
            <el-option
              v-for="dept in departmentOptions"
              :key="dept.id"
              :label="dept.name"
              :value="dept.id">
            </el-option>
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
            @change="fetchSalaryList">
          </el-date-picker>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchSalaryList" :loading="loading">
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
          <div class="stat-icon pending">
            <el-icon><Clock /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-number">{{ tableData.length }}</div>
            <div class="stat-label">待发放记录</div>
          </div>
        </div>
      </el-card>
      <el-card class="stat-card" shadow="hover">
        <div class="stat-content">
          <div class="stat-icon selected">
            <el-icon><Select /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-number">{{ selectedRows.length }}</div>
            <div class="stat-label">已选择</div>
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
            <div class="stat-label">待发放金额</div>
          </div>
        </div>
      </el-card>
      <el-card class="stat-card" shadow="hover">
        <div class="stat-content">
          <div class="stat-icon selected-amount">
            <el-icon><Wallet /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-number">¥{{ formatMoney(selectedAmount) }}</div>
            <div class="stat-label">选中金额</div>
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
        @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" />
        <el-table-column prop="employeeName" label="员工姓名" min-width="120" show-overflow-tooltip />
        <el-table-column prop="departmentName" label="部门" min-width="120" show-overflow-tooltip>
          <template #default="scope">
            <el-tag size="small">{{ scope.row.departmentName || '未知' }}</el-tag>
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
        <el-table-column prop="paymentStatus" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.paymentStatus === 1 ? 'success' : 'warning'" size="small">
              {{ scope.row.paymentStatus === 1 ? '已发放' : '未发放' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="80" fixed="right" class-name="operation-column">
          <template #default="scope">
            <div class="action-buttons">
              <el-button 
                class="desktop-button"
                type="success" 
                size="small" 
                @click="handleSinglePay(scope.row)"
                :disabled="scope.row.paymentStatus === 1"
              >
                <el-icon><Money /></el-icon>
                发放
              </el-button>
              
              <!-- 移动端按钮 -->
              <el-button 
                class="mobile-button"
                type="success" 
                size="small" 
                @click="handleSinglePay(scope.row)"
                :disabled="scope.row.paymentStatus === 1"
              >
                <el-icon><Money /></el-icon>
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Clock, Select, Money, Wallet, Search } from '@element-plus/icons-vue'
import salaryApi from '@/api/salary'
import { departmentApi } from '@/api'

// 数据
const loading = ref(false)
const tableData = ref([])
const selectedRows = ref([])

// 选项
const departmentOptions = ref([])

// 搜索表单
const searchForm = reactive({
  departmentId: '',
  month: ''
})

// 统计
const selectedAmount = computed(() => {
  return selectedRows.value.reduce((sum, item) => sum + (Number(item.netSalary) || 0), 0)
})

const totalAmount = computed(() => {
  return tableData.value.reduce((sum, item) => sum + (Number(item.netSalary) || 0), 0)
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
    
    // 只显示未发放的记录
    const allData = Array.isArray(res) ? res : (res.data || [])
    tableData.value = allData.filter(item => item.paymentStatus === 0)
  } catch (error) {
    console.error('获取薪资列表失败:', error)
    ElMessage.error('获取薪资列表失败')
  } finally {
    loading.value = false
  }
}

// 操作
const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

const handleSinglePay = async (row) => {
  try {
    await ElMessageBox.confirm('确认发放该员工的薪资吗？', '提示', { type: 'warning' })
    await salaryApi.paySalary(row.id)
    ElMessage.success('薪资发放成功')
    fetchSalaryList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('发放失败')
    }
  }
}

const handleBatchPay = async () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请先选择要发放的薪资记录')
    return
  }
  
  try {
    await ElMessageBox.confirm(
      `确认批量发放 ${selectedRows.value.length} 条薪资记录吗？总金额：¥${formatMoney(selectedAmount.value)}`,
      '提示',
      { type: 'warning' }
    )
    
    const ids = selectedRows.value.map(row => row.id)
    await salaryApi.batchPaySalaries(ids)
    ElMessage.success('批量发放成功')
    selectedRows.value = []
    fetchSalaryList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量发放失败')
    }
  }
}

// 重置搜索
const resetSearch = () => {
  searchForm.departmentId = ''
  searchForm.month = ''
  fetchSalaryList()
}

// 格式化
const formatMonth = (date) => {
  if (!date) return '-'
  return date.substring(0, 7)
}

const formatMoney = (amount) => {
  if (!amount) return '0'
  return Number(amount).toLocaleString()
}

// 初始化
onMounted(() => {
  fetchDepartments()
  fetchSalaryList()
})
</script>

<style scoped>
.payment-container {
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

.batch-pay-btn {
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

.stat-icon.pending {
  background: linear-gradient(135deg, #f59e0b, #d97706);
}

.stat-icon.selected {
  background: linear-gradient(135deg, #3b82f6, #1d4ed8);
}

.stat-icon.amount {
  background: linear-gradient(135deg, #10b981, #059669);
}

.stat-icon.selected-amount {
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

.desktop-button {
  display: flex;
  align-items: center;
  gap: 4px;
}

.mobile-button {
  display: none;
}

/* PC端操作列样式 */
@media (min-width: 769px) {
  :deep(.operation-column) {
    width: 120px !important;
    min-width: 120px !important;
  }
}

/* 移动端响应式调整 */
@media (max-width: 768px) {
  .payment-container {
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
  
  .batch-pay-btn {
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
  .desktop-button {
    display: none;
  }
  
  .mobile-button {
    display: flex;
  }
  
  :deep(.operation-column) {
    width: 60px !important;
    min-width: 60px !important;
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
