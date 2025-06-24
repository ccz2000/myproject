const ProjectView = {
    name: 'ProjectView',
    template: `
        <div>
            <el-button type="primary" @click="showProjectsDialog('add')">新增项目</el-button>
            <el-table :data="projects" v-loading="loading">
                <el-table-column prop="name" label="项目名称"></el-table-column>
                <el-table-column prop="description" label="项目描述"></el-table-column>
                <el-table-column label="项目状态" width="120">
                    <template #default="{row}">
                        <div class="status-wrapper">
                            <span class="status-dot" :class="{
                                'ongoing': row.status === 'ONGOING',
                                'completed': row.status === 'COMPLETED',
                                'delayed': row.status === 'DELAYED',
                                'paused': row.status === 'PAUSED'
                            }"></span>
                            {{ statusText[row.status] }}
                        </div>
                    </template>
                </el-table-column>
                <el-table-column label="时间" width="220">
                    <template #default="{row}">
                        <div class="date-range">
                            <div>{{ $filters.dateFormat(row.startDate) }}</div>
                            <div>至</div>
                            <div>{{ $filters.dateFormat(row.endDate) }}</div>
                        </div>
                    </template>
                </el-table-column>
                <el-table-column label="预算" align="right" width="120">
                    <template #default="{row}">
                        {{ $filters.currency(row.budget) }}
                    </template>
                </el-table-column>
                <el-table-column label="负责部门" width="150">
                    <template #default="{row}">
                        {{ getDepartmentName(row.departmentId) }}
                    </template>
                </el-table-column>
                <el-table-column label="负责人" width="120">
                    <template #default="{row}">
                        {{ getEmployeeName(row.leaderId) }}
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="180" fixed="right">
                    <template #default="scope">
                        <div class="operation-column">
                            <el-button size="small" @click="showProjectsDialog('edit', scope.row)">编辑</el-button>
                            <el-button size="small" type="danger" @click="deleteProject(scope.row.id)">删除</el-button>
                        </div>
                    </template>
                </el-table-column>
            </el-table>

            <!-- 项目对话框 -->
            <el-dialog
                :title="dialogType === 'add' ? '新增项目' : '编辑项目'"
                v-model="projectsDialog.visible"
                width="600px">
                <el-form :model="projectsForm" label-width="100px" :rules="projectRules">
                    <el-form-item label="项目名称" prop="name">
                        <el-input v-model="projectsForm.name"></el-input>
                    </el-form-item>
                    <el-form-item label="项目描述" prop="description">
                        <el-input type="textarea" v-model="projectsForm.description"></el-input>
                    </el-form-item>
                    <el-form-item label="项目状态" prop="status">
                        <el-select v-model="projectsForm.status" placeholder="请选择状态">
                            <el-option
                                v-for="(value, key) in statusText"
                                :key="key"
                                :label="value"
                                :value="key">
                            </el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item label="时间范围" required>
                        <el-date-picker
                            v-model="projectsForm.startDate"
                            type="date"
                            placeholder="开始日期"
                            style="width: 45%">
                        </el-date-picker>
                        <span style="margin: 0 10px">至</span>
                        <el-date-picker
                            v-model="projectsForm.endDate"
                            type="date"
                            placeholder="结束日期"
                            style="width: 45%">
                        </el-date-picker>
                    </el-form-item>
                    <el-form-item label="项目预算" prop="budget">
                        <el-input-number 
                            v-model="projectsForm.budget"
                            :min="0"
                            :precision="2"
                            :step="1000"
                            style="width: 100%">
                        </el-input-number>
                    </el-form-item>
                    <el-form-item label="所属部门" prop="departmentId">
                        <el-select v-model="projectsForm.departmentId" placeholder="请选择部门">
                            <el-option
                                v-for="dept in departments"
                                :key="dept.id"
                                :label="dept.name"
                                :value="dept.id">
                            </el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item label="项目负责人" prop="leaderId">
                        <el-select v-model="projectsForm.leaderId" placeholder="请选择负责人">
                            <el-option
                                v-for="emp in employees"
                                :key="emp.id"
                                :label="emp.name"
                                :value="emp.id">
                            </el-option>
                        </el-select>
                    </el-form-item>
                </el-form>
                <template #footer>
                    <el-button @click="projectsDialog.visible = false">取消</el-button>
                    <el-button type="primary" @click="saveProject">确定</el-button>
                </template>
            </el-dialog>
        </div>
    `,
    data() {
        return {
            projects: [],
            departments: [],
            employees: [],
            loading: false,
            dialogType: 'add',
            projectsDialog: {
                visible: false
            },
            projectsForm: {
                name: '',
                description: '',
                status: '',
                startDate: '',
                endDate: '',
                budget: 0,
                departmentId: null,
                leaderId: null
            },
            projectRules: {
                name: [{ required: true, message: '请输入项目名称', trigger: 'blur' }],
                status: [{ required: true, message: '请选择项目状态', trigger: 'change' }],
                departmentId: [{ required: true, message: '请选择所属部门', trigger: 'change' }],
                leaderId: [{ required: true, message: '请选择项目负责人', trigger: 'change' }]
            },
            statusText: {
                'ONGOING': '进行中',
                'COMPLETED': '已完成',
                'DELAYED': '延期',
                'PAUSED': '暂停'
            }
        }
    },
    methods: {
        async loadProjects() {
            this.loading = true;
            try {
                const response = await this.$http.get('/dept-api/projects');
                this.projects = Array.isArray(response.data) ? response.data : [];
            } catch (error) {
                this.$message.error('加载项目数据失败');
                this.projects = [];
            } finally {
                this.loading = false;
            }
        },
        async loadDepartments() {
            try {
                const response = await this.$http.get('/dept-api/departments');
                this.departments = response.data;
            } catch (error) {
                this.$message.error('加载部门数据失败');
            }
        },
        async loadEmployees() {
            try {
                const response = await this.$http.get('/dept-api/employees');
                this.employees = response.data;
            } catch (error) {
                this.$message.error('加载员工数据失败');
            }
        },
        showProjectsDialog(type, row) {
            this.dialogType = type;
            if (type === 'edit' && row) {
                this.projectsForm = { ...row };
            } else {
                this.projectsForm = {
                    name: '',
                    description: '',
                    status: '',
                    startDate: '',
                    endDate: '',
                    budget: 0,
                    departmentId: null,
                    leaderId: null
                };
            }
            this.projectsDialog.visible = true;
        },
        async saveProject() {
            try {
                if (this.dialogType === 'add') {
                    await this.$http.post('/dept-api/projects', this.projectsForm);
                } else {
                    await this.$http.put(`/dept-api/projects/${this.projectsForm.id}`, this.projectsForm);
                }
                this.$message.success('保存成功');
                this.projectsDialog.visible = false;
                this.loadProjects();
            } catch (error) {
                this.$message.error('保存失败');
            }
        },
        async deleteProject(id) {
            try {
                await this.$confirm('确认删除该项目?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                });
                await this.$http.delete(`/dept-api/projects/${id}`);
                this.$message.success('删除成功');
                this.loadProjects();
            } catch (error) {
                if (error !== 'cancel') {
                    this.$message.error('删除失败');
                }
            }
        },
        getDepartmentName(id) {
            const dept = this.departments.find(d => d.id === id);
            return dept ? dept.name : '';
        },
        getEmployeeName(id) {
            const emp = this.employees.find(e => e.id === id);
            return emp ? emp.name : '';
        }
    },
    created() {
        this.loadProjects();
        this.loadDepartments();
        this.loadEmployees();
    }
}; 