<template>
  <div class="home">
    <!-- 统计卡片 -->
    <div class="stats-grid">
      <el-card class="stat-card" shadow="hover">
        <div class="stat-content">
          <div class="stat-icon departments">
            <el-icon><OfficeBuilding /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-number">{{ stats.departments }}</div>
            <div class="stat-label">部门总数</div>
          </div>
        </div>
      </el-card>
      
      <el-card class="stat-card" shadow="hover">
        <div class="stat-content">
          <div class="stat-icon employees">
            <el-icon><User /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-number">{{ stats.employees }}</div>
            <div class="stat-label">员工总数</div>
          </div>
        </div>
      </el-card>
      
      <el-card class="stat-card" shadow="hover">
        <div class="stat-content">
          <div class="stat-icon projects">
            <el-icon><Folder /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-number">{{ stats.projects }}</div>
            <div class="stat-label">项目总数</div>
          </div>
        </div>
      </el-card>
      
      <el-card class="stat-card" shadow="hover">
        <div class="stat-content">
          <div class="stat-icon active-projects">
            <el-icon><TrendCharts /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-number">{{ stats.activeProjects }}</div>
            <div class="stat-label">进行中项目</div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 表格区域 -->
    <div class="tables-grid">
      <el-card class="table-card">
        <template #header>
          <div class="card-header">
            <span>最近创建的部门</span>
          </div>
        </template>
        <el-table :data="recentDepartments" style="width: 100%">
          <el-table-column prop="name" label="部门名称" min-width="120" />
          <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
          <el-table-column prop="createdAt" label="创建时间" width="120">
            <template #default="scope">
              {{ formatDate(scope.row.createdAt) }}
            </template>
          </el-table-column>
        </el-table>
      </el-card>
      
      <el-card class="table-card">
        <template #header>
          <div class="card-header">
            <span>最近创建的项目</span>
          </div>
        </template>
        <el-table :data="recentProjects" style="width: 100%">
          <el-table-column prop="name" label="项目名称" min-width="120" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="scope">
              <el-tag :type="getStatusType(scope.row.status)" size="small">
                {{ scope.row.status }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createdAt" label="创建时间" width="120">
            <template #default="scope">
              {{ formatDate(scope.row.createdAt) }}
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { departmentApi, employeeApi, projectApi } from '@/api'

const stats = ref({
  departments: 0,
  employees: 0,
  projects: 0,
  activeProjects: 0
})

const recentDepartments = ref([])
const recentProjects = ref([])

const loadStats = async () => {
  try {
    const [departments, employees, projects] = await Promise.all([
      departmentApi.getAll(),
      employeeApi.getAll(),
      projectApi.getAll()
    ])
    
    stats.value.departments = departments?.length || 0
    stats.value.employees = employees?.length || 0
    stats.value.projects = projects?.length || 0
    stats.value.activeProjects = projects?.filter(p => p.status === '进行中')?.length || 0
    
    // 最近的部门和项目（取前5个）
    recentDepartments.value = departments?.slice(-5).reverse() || []
    recentProjects.value = projects?.slice(-5).reverse() || []
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

const formatDate = (date) => {
  if (!date) return '--'
  return new Date(date).toLocaleDateString('zh-CN')
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

onMounted(() => {
  loadStats()
})
</script>

<style scoped>
.home {
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.stat-card {
  border-radius: 8px;
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.stat-icon :deep(.el-icon) {
  font-size: 28px;
}

.stat-icon.departments {
  background: linear-gradient(135deg, #409EFF, #66b3ff);
}

.stat-icon.employees {
  background: linear-gradient(135deg, #67C23A, #85ce61);
}

.stat-icon.projects {
  background: linear-gradient(135deg, #E6A23C, #ebb563);
}

.stat-icon.active-projects {
  background: linear-gradient(135deg, #F56C6C, #f78989);
}

.stat-info {
  flex: 1;
}

.stat-number {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  line-height: 1.2;
}

.stat-label {
  font-size: 13px;
  color: #909399;
  margin-top: 4px;
}

.tables-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.table-card {
  border-radius: 8px;
}

.card-header {
  font-size: 15px;
  font-weight: 500;
  color: #303133;
}

/* 移动端样式 */
@media screen and (max-width: 768px) {
  .home {
    padding: 12px;
    gap: 12px;
  }

  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }

  .stat-content {
    padding: 12px;
    gap: 12px;
  }

  .stat-icon {
    width: 44px;
    height: 44px;
  }

  .stat-icon :deep(.el-icon) {
    font-size: 22px;
  }

  .stat-number {
    font-size: 20px;
  }

  .stat-label {
    font-size: 12px;
    margin-top: 2px;
  }

  .tables-grid {
    grid-template-columns: 1fr;
    gap: 12px;
  }

  :deep(.el-table) {
    font-size: 13px;
  }

  :deep(.el-tag) {
    transform: scale(0.9);
    transform-origin: left center;
  }
}

/* 平板设备样式 */
@media screen and (min-width: 769px) and (max-width: 1024px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .stat-icon {
    width: 48px;
    height: 48px;
  }

  .stat-icon :deep(.el-icon) {
    font-size: 24px;
  }

  .stat-number {
    font-size: 22px;
  }
}
</style> 