const app = Vue.createApp({
    data() {
        return {
            activeMenu: 'departments',
            pageTitle: '部门管理'
        }
    },
    methods: {
        handleSelect(key) {
            this.activeMenu = key;
            switch (key) {
                case 'departments':
                    this.pageTitle = '部门管理';
                    break;
                case 'employees':
                    this.pageTitle = '员工管理';
                    break;
                case 'projects':
                    this.pageTitle = '项目管理';
                    break;
            }
        }
    }
});

// 注册组件
app.component('department-view', DepartmentView);
app.component('employee-view', EmployeeView);
app.component('project-view', ProjectView);

// 注册图标
if (window.ElementPlusIconsVue) {
    for (const [key, component] of Object.entries(window.ElementPlusIconsVue)) {
        app.component(key, component);
    }
}

// 配置全局属性
const http = axios.create({
    baseURL: '/',
    timeout: 5000
});

app.config.globalProperties.$http = http;
app.config.globalProperties.$filters = {
    dateFormat: window.filters.dateFormat,
    currency: window.filters.currency
};

// 使用Element Plus
app.use(ElementPlus);

// 挂载应用
app.mount('#app'); 