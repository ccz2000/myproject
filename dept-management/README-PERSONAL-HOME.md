# 个人主站使用说明

## 概述

这个项目现在包含两个主要部分：
1. **个人主站** - 作为主页面，展示个人信息和各种应用入口
2. **企业管理系统** - 原有的管理系统功能

## 路由结构

### 个人主站
- `/` - 个人主站首页（新增）

### 企业管理系统
- `/system` - 系统主入口（重定向到 `/system/home`）
- `/system/login` - 登录页面
- `/system/home` - 系统首页
- `/system/departments` - 部门管理
- `/system/employees` - 员工管理
- `/system/projects` - 项目管理
- `/system/ai-chat` - AI智能助手
- `/system/profile` - 个人中心
- 其他系统功能...

## 个性化配置

### 1. 修改个人信息

编辑 `dept-management/src/views/PersonalHome.vue` 文件中的 `profileData` 对象：

```javascript
const profileData = reactive({
  name: '您的真实姓名',
  title: '您的职位描述',
  description: '您的个人简介',
  avatar: '', // 头像URL，留空使用默认头像
  socialLinks: [
    {
      name: 'GitHub',
      url: 'https://github.com/yourusername', // 修改为您的实际链接
      icon: 'fab fa-github'
    },
    // 添加或修改其他社交链接...
  ],
  skills: [
    { name: '技能名称', level: 90 }, // level 为 0-100
    // 添加更多技能...
  ]
})
```

### 2. 配置应用列表

修改 `applications` 数组来添加或修改应用卡片：

```javascript
const applications = reactive([
  {
    name: '应用名称',
    description: '应用描述',
    icon: 'fas fa-icon-name', // FontAwesome 图标
    route: '/internal-route', // 内部路由
    // 或者
    url: 'https://external-url.com', // 外部链接
    status: 'online', // online, development, coming-soon
    statusText: '状态文字'
  },
  // 添加更多应用...
])
```

### 3. 自定义头像

有两种方式设置头像：

#### 方式1：使用外部链接
```javascript
avatar: 'https://your-avatar-url.com/avatar.jpg'
```

#### 方式2：使用本地文件
1. 将头像文件放在 `dept-management/src/assets/` 目录下
2. 在代码中引用：
```javascript
avatar: '/src/assets/your-avatar.jpg'
```

### 4. 更新最新动态

修改 `recentUpdates` 数组来添加或更新动态信息：

```javascript
const recentUpdates = reactive([
  {
    id: 1,
    date: new Date('2024-01-15'),
    title: '动态标题',
    description: '动态描述内容'
  },
  // 添加更多动态...
])
```

## 功能特点

### 🎨 现代化设计
- 响应式布局，支持移动端
- 渐变背景和动画效果
- 毛玻璃材质卡片设计

### 🔗 多平台链接
- 支持 GitHub、Bilibili、LinkedIn 等主流平台
- 可自定义添加任意社交平台链接
- FontAwesome 图标支持

### 📱 应用管理
- 应用状态显示（运行中、开发中、即将推出）
- 支持内部路由和外部链接
- 卡片悬停效果和状态指示

### ⚡ 快速访问
- 从个人主站快速跳转到企业管理系统
- 在管理系统中可返回个人主站
- 保持登录状态的无缝切换

## 开发建议

1. **个人信息配置**：首次使用时请务必修改个人信息
2. **链接检查**：确保所有外部链接都是有效的
3. **图标选择**：使用 FontAwesome 图标库中的图标
4. **响应式测试**：在不同设备上测试显示效果
5. **性能优化**：大图片请使用适当的尺寸和格式

## 兼容性说明

- 所有原有的路由都保持兼容，会自动重定向到新的路由结构
- 用户登录状态在个人主站和管理系统间保持同步
- 管理系统的所有功能保持不变

## 部署注意事项

1. 确保静态资源路径配置正确
2. 如果使用 CDN，请确保 FontAwesome 可以正常加载
3. 检查所有外部链接的可访问性

## 自定义样式

如果需要修改样式，可以编辑 `PersonalHome.vue` 文件中的 `<style>` 部分。主要的样式变量：

- 主色调：`#667eea` 到 `#764ba2` 的渐变
- 卡片圆角：`15px` 和 `20px`
- 动画时间：`0.3s` 和 `6s`
- 响应式断点：`768px`

## 技术栈

- Vue 3 + Composition API
- Element Plus UI 框架
- FontAwesome 图标库
- CSS3 动画和渐变
- 响应式设计 