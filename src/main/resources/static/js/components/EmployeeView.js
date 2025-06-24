const EmployeeView = {
    name: 'EmployeeView',
    template: `
        <div>
            <el-button type="primary" @click="showEmployeeDialog('add')">新增员工</el-button>
            <el-table :data="employees">
                <el-table-column prop="name" label="姓名"></el-table-column>
                <el-table-column prop="email" label="邮箱"></el-table-column>
                <el-table-column prop="position" label="职位"></el-table-column>
                <el-table-column label="操作" width="200">
                    <template #default="scope">
                        <div class="operation-column">
                            <el-button size="small" @click="showEmployeeDialog('edit', scope.row)">编辑</el-button>
                            <el-button size="small" type="danger" @click="deleteEmployee(scope.row.id)">删除</el-button>
                        </div>
                    </template>
                </el-table-column>
            </el-table>

            <!-- 员工对话框 -->
            <el-dialog
                :title="dialogType === 'add' ? '新增员工' : '编辑员工'"
                v-model="employeeDialog.visible"
                width="500px">
                <el-form :model="employeeForm" label-width="100px">
                    <el-form-item label="姓名" required>
                        <el-input v-model="employeeForm.name"></el-input>
                    </el-form-item>
                    <el-form-item label="邮箱" required>
                        <el-input v-model="employeeForm.email"></el-input>
                    </el-form-item>
                    <el-form-item label="职位" required>
                        <el-input v-model="employeeForm.position"></el-input>
                    </el-form-item>
                    <el-form-item label="所属部门" required>
                        <el-select v-model="employeeForm.departmentId" placeholder="请选择">
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
                    <el-button @click="employeeDialog.visible = false">取消</el-button>
                    <el-button type="primary" @click="saveEmployee">确定</el-button>
                </template>
            </el-dialog>
        </div>
    `,
    data() {
        return {
            employees: [],
            departments: [],
            dialogType: 'add',
            employeeDialog: {
                visible: false
            },
            employeeForm: {
                name: '',
                email: '',
                position: '',
                departmentId: null
            }
        }
    },
    methods: {
        async loadEmployees() {
            try {
                const response = await this.$http.get('/dept-api/employees');
                this.employees = Array.isArray(response.data) ? response.data : [];
            } catch (error) {
                this.$message.error('加载员工数据失败');
                this.employees = [];
            }
        },
        async loadDepartments() {
            try {
                const response = await this.$http.get('/dept-api/departments');
                this.departments = Array.isArray(response.data) ? response.data : [];
            } catch (error) {
                this.$message.error('加载部门数据失败');
                this.departments = [];
            }
        },
        showEmployeeDialog(type, row) {
            this.dialogType = type;
            if (type === 'edit' && row) {
                this.employeeForm = { ...row };
            } else {
                this.employeeForm = {
                    name: '',
                    email: '',
                    position: '',
                    departmentId: null
                };
            }
            this.employeeDialog.visible = true;
        },
        async saveEmployee() {
            try {
                if (this.dialogType === 'add') {
                    await this.$http.post('/dept-api/employees', this.employeeForm);
                } else {
                    await this.$http.put(`/dept-api/employees/${this.employeeForm.id}`, this.employeeForm);
                }
                this.$message.success('保存成功');
                this.employeeDialog.visible = false;
                this.loadEmployees();
            } catch (error) {
                this.$message.error('保存失败');
            }
        },
        async deleteEmployee(id) {
            try {
                await this.$confirm('确认删除该员工?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                });
                await this.$http.delete(`/dept-api/employees/${id}`);
                this.$message.success('删除成功');
                this.loadEmployees();
            } catch (error) {
                if (error !== 'cancel') {
                    this.$message.error('删除失败');
                }
            }
        }
    },
    created() {
        this.loadEmployees();
        this.loadDepartments();
    }
}; 