const DepartmentView = {
    name: 'DepartmentView',
    template: `
        <div>
            <el-button type="primary" @click="showDepartmentDialog('add')">新增部门</el-button>
            <el-table :data="departments">
                <el-table-column prop="name" label="部门名称"></el-table-column>
                <el-table-column prop="description" label="描述"></el-table-column>
                <el-table-column label="操作" width="280">
                    <template #default="scope">
                        <div class="operation-column">
                            <el-button size="small" @click="showDepartmentDialog('edit', scope.row)">编辑</el-button>
                            <el-button size="small" type="danger" @click="deleteDepartment(scope.row.id)">删除</el-button>
                            <el-button size="small" type="success" @click="showManagerDialog(scope.row)">设置经理</el-button>
                        </div>
                    </template>
                </el-table-column>
            </el-table>

            <!-- 部门对话框 -->
            <el-dialog
                :title="dialogType === 'add' ? '新增部门' : '编辑部门'"
                v-model="departmentDialog.visible"
                width="500px">
                <el-form :model="departmentForm" label-width="100px">
                    <el-form-item label="部门名称" required>
                        <el-input v-model="departmentForm.name"></el-input>
                    </el-form-item>
                    <el-form-item label="描述">
                        <el-input type="textarea" v-model="departmentForm.description"></el-input>
                    </el-form-item>
                    <el-form-item label="上级部门">
                        <el-select v-model="departmentForm.parentId" clearable placeholder="请选择">
                            <el-option
                                v-for="dept in departments"
                                :key="dept.id"
                                :label="dept.name"
                                :value="dept.id">
                            </el-option>
                        </el-select>
                    </el-form-item>
                </el-form>
                <template #footer>
                    <el-button @click="departmentDialog.visible = false">取消</el-button>
                    <el-button type="primary" @click="saveDepartment">确定</el-button>
                </template>
            </el-dialog>

            <!-- 设置经理对话框 -->
            <el-dialog
                title="设置部门经理"
                v-model="managerDialog.visible"
                width="500px">
                <el-form :model="managerForm" label-width="100px">
                    <el-form-item label="选择经理">
                        <el-select v-model="managerForm.managerId" placeholder="请选择">
                            <el-option
                                v-for="emp in departmentEmployees"
                                :key="emp.id"
                                :label="emp.name"
                                :value="emp.id">
                            </el-option>
                        </el-select>
                    </el-form-item>
                </el-form>
                <template #footer>
                    <el-button @click="managerDialog.visible = false">取消</el-button>
                    <el-button type="primary" @click="assignManager">确定</el-button>
                </template>
            </el-dialog>
        </div>
    `,
    data() {
        return {
            departments: [],
            dialogType: 'add',
            departmentDialog: {
                visible: false
            },
            departmentForm: {
                name: '',
                description: '',
                parentId: null
            },
            managerDialog: {
                visible: false
            },
            managerForm: {
                managerId: null,
                departmentId: null
            },
            departmentEmployees: []
        }
    },
    methods: {
        async loadDepartments() {
            try {
                const response = await this.$http.get('/dept-api/departments');
                this.departments = Array.isArray(response.data) ? response.data : [];
            } catch (error) {
                this.$message.error('加载部门数据失败');
                this.departments = [];
            }
        },
        showDepartmentDialog(type, row) {
            this.dialogType = type;
            if (type === 'edit' && row) {
                this.departmentForm = { ...row };
            } else {
                this.departmentForm = {
                    name: '',
                    description: '',
                    parentId: null
                };
            }
            this.departmentDialog.visible = true;
        },
        async saveDepartment() {
            try {
                if (this.dialogType === 'add') {
                    await this.$http.post('/dept-api/departments', this.departmentForm);
                } else {
                    await this.$http.put(`/dept-api/departments/${this.departmentForm.id}`, this.departmentForm);
                }
                this.$message.success('保存成功');
                this.departmentDialog.visible = false;
                this.loadDepartments();
            } catch (error) {
                this.$message.error('保存失败');
            }
        },
        async deleteDepartment(id) {
            try {
                await this.$confirm('确认删除该部门?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                });
                await this.$http.delete(`/dept-api/departments/${id}`);
                this.$message.success('删除成功');
                this.loadDepartments();
            } catch (error) {
                if (error !== 'cancel') {
                    this.$message.error('删除失败');
                }
            }
        },
        async showManagerDialog(department) {
            this.managerForm.departmentId = department.id;
            try {
                const response = await this.$http.get(`/dept-api/departments/${department.id}/employees`);
                this.departmentEmployees = response.data;
                this.managerDialog.visible = true;
            } catch (error) {
                this.$message.error('加载员工数据失败');
            }
        },
        async assignManager() {
            try {
                await this.$http.post(`/dept-api/departments/${this.managerForm.departmentId}/manager`, {
                    managerId: this.managerForm.managerId
                });
                this.$message.success('设置经理成功');
                this.managerDialog.visible = false;
                this.loadDepartments();
            } catch (error) {
                this.$message.error('设置经理失败');
            }
        }
    },
    created() {
        this.loadDepartments();
    }
}; 