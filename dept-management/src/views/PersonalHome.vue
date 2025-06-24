<template>
  <div class="personal-home">
    <!-- 主头部区域 -->
    <header class="hero-section">
      <div class="hero-background">
        <div class="floating-elements">
          <div class="float-element" v-for="i in 6" :key="i"></div>
        </div>
      </div>
      
      <div class="hero-content">
        <div class="profile-card">
          <div class="avatar-container">
            <div class="avatar-placeholder" v-if="!profileData.avatar">
              <svg width="150" height="150" viewBox="0 0 150 150" xmlns="http://www.w3.org/2000/svg">
                <circle cx="75" cy="75" r="75" fill="url(#grad1)"/>
                <defs>
                  <linearGradient id="grad1" x1="0%" y1="0%" x2="100%" y2="100%">
                    <stop offset="0%" style="stop-color:#64748b;stop-opacity:1" />
                    <stop offset="100%" style="stop-color:#475569;stop-opacity:1" />
                  </linearGradient>
                </defs>
                <text x="75" y="85" text-anchor="middle" fill="white" font-size="60" font-family="Arial, sans-serif" font-weight="bold">{{ profileData.name.charAt(0).toUpperCase() }}</text>
              </svg>
            </div>
            <img v-else :src="profileData.avatar" :alt="profileData.name" class="avatar">
            <div class="avatar-ring"></div>
          </div>
          
          <h1 class="name">{{ profileData.name }}</h1>
          <p class="title">{{ profileData.title }}</p>
          <p class="description">{{ profileData.description }}</p>
          
          <!-- 社交链接 -->
          <div class="social-links">
            <a 
              v-for="social in profileData.socialLinks" 
              :key="social.name"
              :href="social.url" 
              target="_blank"
              class="social-link"
              :title="social.name"
            >
              <i :class="social.icon"></i>
            </a>
          </div>
        </div>
      </div>
    </header>

    <!-- 主要功能区域 -->
    <main class="main-content">
      <div class="container">
        <!-- 数据统计 -->
        <section class="stats-section">
          <div class="stats-grid">
            <div v-for="stat in statsData" :key="stat.label" class="stat-card">
              <div class="stat-icon">
                <i :class="stat.icon"></i>
              </div>
              <div class="stat-number">{{ stat.value }}</div>
              <div class="stat-label">{{ stat.label }}</div>
            </div>
          </div>
        </section>

        <!-- 快速访问卡片 -->
        <section class="quick-access">
          <h2 class="section-title">快速访问</h2>
          <div class="cards-grid">
            <div 
              v-for="app in applications" 
              :key="app.name"
              class="app-card"
              @click="navigateToApp(app)"
            >
              <div class="app-icon">
                <i :class="app.icon"></i>
              </div>
              <h3 class="app-name">{{ app.name }}</h3>
              <p class="app-description">{{ app.description }}</p>
              <div class="app-status" :class="app.status">
                {{ app.statusText }}
              </div>
            </div>
          </div>
        </section>

        <!-- 数据统计 -->
        <section class="stats-section">
          <div class="stats-grid">
            <div v-for="stat in statsData" :key="stat.label" class="stat-card">
              <div class="stat-icon">
                <i :class="stat.icon"></i>
              </div>
              <div class="stat-number">{{ stat.value }}</div>
              <div class="stat-label">{{ stat.label }}</div>
            </div>
          </div>
        </section>

        <!-- 技能和兴趣 -->
        <section class="skills-section">
          <h2 class="section-title">技能专长</h2>
          <div class="skills-container">
            <div 
              v-for="skill in profileData.skills" 
              :key="skill.name"
              class="skill-tag"
            >
              <span class="skill-name">{{ skill.name }}</span>
              <div class="skill-level">
                <div 
                  class="skill-progress" 
                  :style="{ width: skill.level + '%' }"
                ></div>
              </div>
            </div>
          </div>
        </section>

        <!-- 项目作品展示 -->
        <section class="projects-showcase">
          <h2 class="section-title">精选项目</h2>
          <div class="projects-grid">
            <div 
              v-for="project in featuredProjects" 
              :key="project.id"
              class="project-card"
            >
              <div class="project-image">
                <img :src="project.image" :alt="project.name" v-if="project.image">
                <div v-else class="project-placeholder">
                  <i :class="project.icon"></i>
                </div>
              </div>
              <div class="project-content">
                <h3 class="project-title">{{ project.name }}</h3>
                <p class="project-description">{{ project.description }}</p>
                <div class="project-tech">
                  <span 
                    v-for="tech in project.technologies" 
                    :key="tech"
                    class="tech-tag"
                  >{{ tech }}</span>
                </div>
                <div class="project-links">
                  <a v-if="project.github" :href="project.github" target="_blank" class="project-link">
                    <i class="fab fa-github"></i> 源码
                  </a>
                  <a v-if="project.demo" :href="project.demo" target="_blank" class="project-link demo">
                    <i class="fas fa-external-link-alt"></i> 演示
                  </a>
                </div>
              </div>
            </div>
          </div>
        </section>

        <!-- 经历时间轴 -->
        <section class="timeline-section">
          <h2 class="section-title">个人经历</h2>
          <div class="timeline">
            <div 
              v-for="(item, index) in timelineData" 
              :key="index"
              class="timeline-item"
            >
              <div class="timeline-date">{{ item.date }}</div>
              <div class="timeline-content">
                <h4 class="timeline-title">{{ item.title }}</h4>
                <p class="timeline-description">{{ item.description }}</p>
              </div>
            </div>
          </div>
        </section>

        <!-- 博客文章 -->
        <section class="blog-section">
          <h2 class="section-title">最新文章</h2>
          <div class="blog-grid">
            <article 
              v-for="article in recentArticles" 
              :key="article.id"
              class="blog-card"
            >
              <div class="blog-meta">
                <span class="blog-date">{{ formatDate(article.date) }}</span>
                <span class="blog-category">{{ article.category }}</span>
              </div>
              <h3 class="blog-title">{{ article.title }}</h3>
              <p class="blog-excerpt">{{ article.excerpt }}</p>
              <div class="blog-tags">
                <span 
                  v-for="tag in article.tags" 
                  :key="tag"
                  class="blog-tag"
                >#{{ tag }}</span>
              </div>
              <a href="#" class="blog-read-more">阅读全文 →</a>
            </article>
          </div>
        </section>

        <!-- 联系方式 -->
        <section class="contact-section">
          <h2 class="section-title">联系我</h2>
          <div class="contact-content">
            <div class="contact-info">
              <h3>让我们开始对话</h3>
              <p>如果您对我的项目感兴趣，或者想要合作交流，欢迎随时联系我！</p>
              <div class="contact-methods">
                <div class="contact-item">
                  <i class="fas fa-envelope"></i>
                  <span>cengd@qq.com</span>
                </div>
                <div class="contact-item">
                  <i class="fas fa-map-marker-alt"></i>
                  <span>中国，北京</span>
                </div>
                <div class="contact-item">
                  <i class="fas fa-clock"></i>
                  <span>周一至周五 9:00-18:00</span>
                </div>
              </div>
            </div>
            <div class="contact-form">
              <h3>快速留言</h3>
              <form @submit.prevent="handleContactSubmit">
                <div class="form-group">
                  <input 
                    type="text" 
                    v-model="contactForm.name" 
                    placeholder="您的姓名" 
                    required
                  >
                </div>
                <div class="form-group">
                  <input 
                    type="email" 
                    v-model="contactForm.email" 
                    placeholder="您的邮箱" 
                    required
                  >
                </div>
                <div class="form-group">
                  <textarea 
                    v-model="contactForm.message" 
                    placeholder="留言内容" 
                    rows="4" 
                    required
                  ></textarea>
                </div>
                <button type="submit" class="contact-submit">发送消息</button>
              </form>
            </div>
          </div>
        </section>

        <!-- 最新动态 -->
        <section class="updates-section">
          <h2 class="section-title">最新动态</h2>
          <div class="updates-list">
            <div 
              v-for="update in recentUpdates" 
              :key="update.id"
              class="update-item"
            >
              <div class="update-date">{{ formatDate(update.date) }}</div>
              <div class="update-content">
                <h4>{{ update.title }}</h4>
                <p>{{ update.description }}</p>
              </div>
            </div>
          </div>
        </section>
      </div>
    </main>

    <!-- 页脚 -->
    <footer class="footer">
      <div class="container">
        <p>&copy; {{ new Date().getFullYear() }} {{ profileData.name }}. 保留所有权利。</p>
        <p class="footer-links">
          <a href="mailto:trenza@example.com">联系我</a> |
          <a href="#" @click="showPrivacyPolicy">隐私政策</a> |
          <a href="#" @click="showTerms">使用条款</a>
        </p>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import avatarImage from '@/assets/avatar.jpg'  // 导入头像图片

const router = useRouter()

// 联系表单数据
const contactForm = reactive({
  name: '',
  email: '',
  message: ''
})

// 精选项目
const featuredProjects = reactive([
  {
    id: 1,
    name: '企业管理系统',
    description: '基于Vue3和Spring Boot的全栈企业管理解决方案，包含用户管理、部门管理、项目管理等功能。',
    image: '',
    icon: 'fas fa-building',
    technologies: ['Vue 3', 'Spring Boot', 'MySQL', 'Element Plus'],
    github: 'https://github.com/trenza/enterprise-system',
    demo: '/system'
  },
  {
    id: 2,
    name: 'AI智能助手',
    description: '集成多种AI模型的智能对话助手，支持文本生成、代码辅助、问答等功能。',
    image: '',
    icon: 'fas fa-robot',
    technologies: ['JavaScript', 'OpenAI API', 'WebSocket', 'Node.js'],
    github: 'https://github.com/trenza/ai-assistant',
    demo: 'https://ai-demo.trenza.com'
  },
  {
    id: 3,
    name: '个人博客系统',
    description: '现代化的个人博客平台，支持Markdown编辑、标签分类、评论系统等功能。',
    image: '',
    icon: 'fas fa-blog',
    technologies: ['React', 'Next.js', 'MongoDB', 'TailwindCSS'],
    github: 'https://github.com/trenza/blog-system',
    demo: 'https://blog.trenza.com'
  },
  {
    id: 4,
    name: '在线工具集',
    description: '实用的在线工具集合，包含代码格式化、图片压缩、文件转换等多种工具。',
    image: '',
    icon: 'fas fa-tools',
    technologies: ['Vue 3', 'TypeScript', 'Vite', 'PWA'],
    github: 'https://github.com/trenza/online-tools',
    demo: 'https://tools.trenza.com'
  }
])

// 时间轴数据
const timelineData = reactive([
  {
    date: '2024年至今',
    title: '全栈开发工程师',
    description: '专注于现代Web技术栈，主导多个企业级项目的开发与维护。'
  },
  {
    date: '2023年',
    title: '开源项目贡献者',
    description: '开始积极参与开源社区，为Vue.js、Element Plus等项目贡献代码。'
  },
  {
    date: '2022年',
    title: '技术博客创作',
    description: '开始撰写技术博客，分享开发经验和最佳实践，累计阅读量超过10万。'
  },
  {
    date: '2021年',
    title: '前端开发工程师',
    description: '专注于React和Vue.js开发，参与多个大型项目的前端架构设计。'
  },
  {
    date: '2020年',
    title: '踏入编程世界',
    description: '开始学习编程，从基础的HTML/CSS到JavaScript，逐步构建技术栈。'
  }
])

// 最新文章
const recentArticles = reactive([
  {
    id: 1,
    title: 'Vue 3 Composition API 最佳实践',
    excerpt: '深入探讨Vue 3 Composition API的使用技巧和最佳实践，帮助开发者更好地组织代码结构。',
    date: new Date('2024-01-20'),
    category: '前端开发',
    tags: ['Vue', 'JavaScript', '最佳实践']
  },
  {
    id: 2,
    title: 'Spring Boot 微服务架构设计',
    excerpt: '从零开始构建Spring Boot微服务架构，包含服务注册、配置管理、API网关等核心组件。',
    date: new Date('2024-01-15'),
    category: '后端开发',
    tags: ['Spring Boot', '微服务', '架构设计']
  },
  {
    id: 3,
    title: '现代化CSS技巧与实践',
    excerpt: '介绍最新的CSS特性和技巧，包括CSS Grid、Flexbox、自定义属性等现代化布局方案。',
    date: new Date('2024-01-10'),
    category: '前端开发',
    tags: ['CSS', '布局', '响应式设计']
  }
])

// 个人资料数据
const profileData = reactive({
  name: 'Trenza',
  title: '全栈开发工程师 | 技术爱好者',
  description: '热衷于技术创新，专注于构建高质量的软件解决方案。喜欢分享技术心得，探索新技术。',
  avatar: avatarImage, // 使用导入的头像图片
  socialLinks: [
    {
      name: 'GitHub',
      url: 'https://github.com/yourusername', // 修改为GitHub链接
      icon: 'fab fa-github'
    },
    {
      name: 'Bilibili',
      url: 'https://space.bilibili.com/yourid', // 修改为Bilibili链接
      icon: 'fab fa-bilibili'
    },
    {
      name: 'LinkedIn',
      url: 'https://linkedin.com/in/yourusername', // 修改为LinkedIn链接
      icon: 'fab fa-linkedin'
    },
    {
      name: '微博',
      url: 'https://weibo.com/yourusername', // 修改为微博链接
      icon: 'fab fa-weibo'
    },
    {
      name: 'Twitter',
      url: 'https://twitter.com/yourusername', // 修改为Twitter链接
      icon: 'fab fa-twitter'
    },
    {
      name: '掘金',
      url: 'https://juejin.cn/user/yourid', // 修改为掘金链接
      icon: 'fas fa-gem'
    }
  ],
  skills: [
    { name: 'JavaScript/TypeScript', level: 90 },
    { name: 'Vue.js/React', level: 85 },
    { name: 'Node.js', level: 80 },
    { name: 'Python', level: 75 },
    { name: 'Java/Spring Boot', level: 85 },
    { name: 'MySQL/MongoDB', level: 80 },
    { name: 'Docker/K8s', level: 70 },
    { name: 'AWS/阿里云', level: 75 }
  ]
})

// 应用列表
const applications = reactive([
  {
    name: '企业管理系统',
    description: '综合企业管理解决方案，包含部门、员工、项目管理等功能',
    icon: 'fas fa-building',
    route: '/system',
    status: 'online',
    statusText: '运行中'
  },
  {
    name: '个人博客',
    description: '分享技术见解和生活感悟的个人博客',
    icon: 'fas fa-blog',
    url: 'https://yourblog.com', // 请修改为您的博客链接
    status: 'coming-soon',
    statusText: '即将推出'
  },
  {
    name: '项目展示',
    description: '我的开源项目和作品集展示',
    icon: 'fas fa-code',
    url: 'https://github.com/yourusername', // 请修改为您的项目链接
    status: 'online',
    statusText: '可访问'
  },
  {
    name: '在线工具',
    description: '实用的在线工具和小应用集合',
    icon: 'fas fa-tools',
    route: '/tools',
    status: 'development',
    statusText: '开发中'
  },
  {
    name: '技术文档',
    description: '个人技术文档和学习笔记',
    icon: 'fas fa-book',
    url: 'https://docs.yoursite.com', // 请修改为您的文档链接
    status: 'coming-soon',
    statusText: '即将推出'
  },
  {
    name: '资源分享',
    description: '有用的开发资源和工具推荐',
    icon: 'fas fa-share-alt',
    route: '/resources',
    status: 'development',
    statusText: '开发中'
  }
])

// 最新动态
const recentUpdates = reactive([
  {
    id: 1,
    date: new Date('2025-6-15'),
    title: '企业管理系统 v2.0 发布',
    description: '新增AI助手功能，优化用户体验，提升系统性能和稳定性'
  },
  {
    id: 2,
    date: new Date('2025-05-10'),
    title: '个人主站正式上线',
    description: '全新的个人主页正式上线，集成多个应用入口，提供更好的用户体验'
  },
  {
    id: 3,
    date: new Date('2025-01-05'),
    title: '参与开源项目贡献',
    description: '开始参与Vue.js生态系统的开源贡献，提交了多个有价值的PR'
  },
  {
    id: 4,
    date: new Date('2025-01-01'),
    title: '技术博客启动',
    description: '开始撰写技术博客，分享开发经验和学习心得'
  }
])

// 导航到应用
const navigateToApp = (app) => {
  if (app.route) {
    router.push(app.route)
  } else if (app.url) {
    window.open(app.url, '_blank')
  } else {
    ElMessage.info('该功能正在开发中...')
  }
}

// 格式化日期
const formatDate = (date) => {
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

// 显示隐私政策
const showPrivacyPolicy = () => {
  ElMessage.info('隐私政策功能待实现')
}

// 显示使用条款
const showTerms = () => {
  ElMessage.info('使用条款功能待实现')
}

// 处理联系表单提交
const handleContactSubmit = () => {
  if (!contactForm.name || !contactForm.email || !contactForm.message) {
    ElMessage.warning('请填写完整信息')
    return
  }
  
  // 这里可以添加实际的表单提交逻辑
  ElMessage.success('消息发送成功！我会尽快回复您。')
  
  // 清空表单
  contactForm.name = ''
  contactForm.email = ''
  contactForm.message = ''
}

onMounted(() => {
  // 可以在这里加载用户的个性化数据
  console.log('个人主站加载完成')
})
</script>

<style scoped>
/* 引入字体图标 */
@import url('https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css');

.personal-home {
  min-height: 100vh;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  overflow-x: hidden;
}

/* 英雄区域 */
.hero-section {
  position: relative;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.hero-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #ffffff 0%, #f1f5f9 100%);
}

.floating-elements {
  position: absolute;
  width: 100%;
  height: 100%;
}

.float-element {
  position: absolute;
  background: rgba(100, 116, 139, 0.1);
  border-radius: 50%;
  animation: float 6s ease-in-out infinite;
}

.float-element:nth-child(1) {
  width: 80px;
  height: 80px;
  left: 10%;
  top: 20%;
  animation-delay: 0s;
}

.float-element:nth-child(2) {
  width: 60px;
  height: 60px;
  right: 10%;
  top: 30%;
  animation-delay: 1s;
}

.float-element:nth-child(3) {
  width: 100px;
  height: 100px;
  left: 20%;
  bottom: 20%;
  animation-delay: 2s;
}

.float-element:nth-child(4) {
  width: 40px;
  height: 40px;
  right: 20%;
  bottom: 30%;
  animation-delay: 3s;
}

.float-element:nth-child(5) {
  width: 70px;
  height: 70px;
  left: 50%;
  top: 10%;
  animation-delay: 4s;
}

.float-element:nth-child(6) {
  width: 90px;
  height: 90px;
  right: 40%;
  bottom: 10%;
  animation-delay: 5s;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-20px);
  }
}

.hero-content {
  position: relative;
  z-index: 2;
  text-align: center;
}

.profile-card {
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  padding: 3rem;
  border: 1px solid rgba(148, 163, 184, 0.2);
  box-shadow: 0 25px 45px rgba(0, 0, 0, 0.1);
  max-width: 500px;
  margin: 0 auto;
}

.avatar-container {
  position: relative;
  display: inline-block;
  margin-bottom: 2rem;
}

.avatar {
  width: 150px;
  height: 150px;
  border-radius: 50%;
  border: 4px solid rgba(148, 163, 184, 0.3);
  object-fit: cover;
}

.avatar-placeholder {
  width: 150px;
  height: 150px;
  border-radius: 50%;
  border: 4px solid rgba(148, 163, 184, 0.3);
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-ring {
  position: absolute;
  top: -10px;
  left: -10px;
  width: 170px;
  height: 170px;
  border: 2px solid rgba(148, 163, 184, 0.4);
  border-radius: 50%;
  animation: rotate 8s linear infinite;
  border-top-color: transparent;
  border-right-color: transparent;
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.name {
  font-size: 2.5rem;
  font-weight: 700;
  margin: 0 0 0.5rem 0;
  color: #1e293b;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);
}

.title {
  font-size: 1.3rem;
  color: #475569;
  margin: 0 0 1rem 0;
  font-weight: 300;
}

.description {
  font-size: 1.1rem;
  color: #64748b;
  margin-bottom: 2rem;
  line-height: 1.6;
}

.social-links {
  display: flex;
  justify-content: center;
  gap: 1rem;
}

.social-link {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 50px;
  height: 50px;
  background: rgba(148, 163, 184, 0.1);
  border-radius: 50%;
  color: #475569;
  text-decoration: none;
  font-size: 1.2rem;
  transition: all 0.3s ease;
  border: 1px solid rgba(148, 163, 184, 0.2);
}

.social-link:hover {
  transform: translateY(-3px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.2);
}

/* GitHub 样式 */
.social-link[href*="github"] {
  background: #333333;
  color: white;
  border: 1px solid #333333;
}

.social-link[href*="github"]:hover {
  background: #24292e;
  box-shadow: 0 10px 20px rgba(51, 51, 51, 0.3);
}

/* Bilibili 样式 */
.social-link[href*="bilibili"] {
  background: linear-gradient(45deg, #fb7299, #00aeec);
  color: white;
  border: 1px solid #fb7299;
}

.social-link[href*="bilibili"]:hover {
  background: linear-gradient(45deg, #fa5c7c, #0099d4);
  box-shadow: 0 10px 20px rgba(251, 114, 153, 0.4);
}

/* LinkedIn 样式 */
.social-link[href*="linkedin"] {
  background: #0077b5;
  color: white;
  border: 1px solid #0077b5;
}

.social-link[href*="linkedin"]:hover {
  background: #005885;
  box-shadow: 0 10px 20px rgba(0, 119, 181, 0.3);
}

/* 微博样式 */
.social-link[href*="weibo"] {
  background: linear-gradient(45deg, #e6162d, #ff8200);
  color: white;
  border: 1px solid #e6162d;
}

.social-link[href*="weibo"]:hover {
  background: linear-gradient(45deg, #d10e24, #e6741a);
  box-shadow: 0 10px 20px rgba(230, 22, 45, 0.3);
}

/* Twitter 样式 */
.social-link[href*="twitter"] {
  background: #1da1f2;
  color: white;
  border: 1px solid #1da1f2;
}

.social-link[href*="twitter"]:hover {
  background: #0d8bd9;
  box-shadow: 0 10px 20px rgba(29, 161, 242, 0.3);
}

/* 掘金样式 */
.social-link[href*="juejin"] {
  background: #007fff;
  color: white;
  border: 1px solid #007fff;
}

.social-link[href*="juejin"]:hover {
  background: #0066cc;
  box-shadow: 0 10px 20px rgba(0, 127, 255, 0.3);
}

/* 主要内容区域 */
.main-content {
  background: #ffffff;
  padding: 4rem 0;
}

/* 统计数据样式 */
.stats-section {
  margin-bottom: 4rem;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 2rem;
  max-width: 800px;
  margin: 0 auto;
}

.stat-card {
  background: white;
  border-radius: 15px;
  padding: 2rem;
  text-align: center;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  border: 1px solid #e2e8f0;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
}

.stat-icon {
  width: 60px;
  height: 60px;
  background: linear-gradient(135deg, #64748b, #475569);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 1rem auto;
  font-size: 1.5rem;
  color: white;
}

.stat-number {
  font-size: 2.5rem;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 0.5rem;
}

.stat-label {
  color: #64748b;
  font-weight: 500;
}

/* 项目展示样式 */
.projects-showcase {
  margin-bottom: 4rem;
}

.projects-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 2rem;
  max-width: 1200px;
  margin: 0 auto;
}

.project-card {
  background: white;
  border-radius: 15px;
  overflow: hidden;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  border: 1px solid #e2e8f0;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.project-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
}

.project-image {
  height: 200px;
  background: linear-gradient(135deg, #f8fafc, #e2e8f0);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.project-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.project-placeholder {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, #64748b, #475569);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 2rem;
  color: white;
}

.project-content {
  padding: 2rem;
}

.project-title {
  font-size: 1.5rem;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 1rem 0;
}

.project-description {
  color: #64748b;
  line-height: 1.6;
  margin-bottom: 1.5rem;
}

.project-tech {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  margin-bottom: 1.5rem;
}

.tech-tag {
  background: #f1f5f9;
  color: #475569;
  padding: 0.3rem 0.8rem;
  border-radius: 15px;
  font-size: 0.85rem;
  font-weight: 500;
}

.project-links {
  display: flex;
  gap: 1rem;
}

.project-link {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 1rem;
  background: #64748b;
  color: white;
  text-decoration: none;
  border-radius: 8px;
  font-size: 0.9rem;
  transition: all 0.3s ease;
}

.project-link:hover {
  background: #475569;
  transform: translateY(-2px);
}

.project-link.demo {
  background: #22c55e;
}

.project-link.demo:hover {
  background: #16a34a;
}

/* 时间轴样式 */
.timeline-section {
  margin-bottom: 4rem;
}

.timeline {
  max-width: 800px;
  margin: 0 auto;
  position: relative;
}

.timeline::before {
  content: '';
  position: absolute;
  left: 30px;
  top: 0;
  bottom: 0;
  width: 2px;
  background: linear-gradient(180deg, #64748b, #475569);
  z-index: 1;
}

.timeline-item {
  display: flex;
  margin-bottom: 3rem;
  position: relative;
}

.timeline-item::before {
  content: '';
  position: absolute;
  left: 24px;
  top: 8px;
  width: 14px;
  height: 14px;
  background: #64748b;
  border-radius: 50%;
  border: 3px solid white;
  box-shadow: 0 0 0 3px #e2e8f0;
  z-index: 2;
}

.timeline-date {
  min-width: 120px;
  color: #64748b;
  font-weight: 600;
  font-size: 0.9rem;
  padding-top: 0.5rem;
}

.timeline-content {
  background: white;
  border-radius: 10px;
  padding: 2rem;
  margin-left: 2rem;
  flex: 1;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.08);
  border: 1px solid #e2e8f0;
}

.timeline-title {
  margin: 0 0 1rem 0;
  color: #1e293b;
  font-size: 1.2rem;
  font-weight: 600;
}

.timeline-description {
  color: #64748b;
  line-height: 1.6;
  margin: 0;
}

/* 博客文章样式 */
.blog-section {
  margin-bottom: 4rem;
}

.blog-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 2rem;
  max-width: 1200px;
  margin: 0 auto;
}

.blog-card {
  background: white;
  border-radius: 15px;
  padding: 2rem;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  border: 1px solid #e2e8f0;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.blog-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
}

.blog-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.blog-date {
  color: #64748b;
  font-size: 0.9rem;
}

.blog-category {
  background: #f1f5f9;
  color: #475569;
  padding: 0.3rem 0.8rem;
  border-radius: 15px;
  font-size: 0.8rem;
  font-weight: 500;
}

.blog-title {
  font-size: 1.3rem;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 1rem 0;
  line-height: 1.4;
}

.blog-excerpt {
  color: #64748b;
  line-height: 1.6;
  margin-bottom: 1.5rem;
}

.blog-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  margin-bottom: 1.5rem;
}

.blog-tag {
  color: #475569;
  font-size: 0.85rem;
  font-weight: 500;
}

.blog-read-more {
  color: #64748b;
  text-decoration: none;
  font-weight: 600;
  transition: color 0.3s ease;
}

.blog-read-more:hover {
  color: #475569;
}

/* 联系方式样式 */
.contact-section {
  margin-bottom: 4rem;
  background: #f8fafc;
  padding: 4rem 2rem;
}

.contact-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 4rem;
  max-width: 1000px;
  margin: 0 auto;
}

.contact-info h3 {
  font-size: 1.8rem;
  color: #1e293b;
  margin-bottom: 1rem;
}

.contact-info p {
  color: #64748b;
  line-height: 1.6;
  margin-bottom: 2rem;
}

.contact-methods {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.contact-item {
  display: flex;
  align-items: center;
  gap: 1rem;
  color: #475569;
}

.contact-item i {
  width: 20px;
  color: #64748b;
}

.contact-form {
  background: white;
  padding: 2rem;
  border-radius: 15px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  border: 1px solid #e2e8f0;
}

.contact-form h3 {
  font-size: 1.5rem;
  color: #1e293b;
  margin-bottom: 1.5rem;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 1rem;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 1rem;
  transition: border-color 0.3s ease, box-shadow 0.3s ease;
  box-sizing: border-box;
}

.form-group input:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #64748b;
  box-shadow: 0 0 0 3px rgba(100, 116, 139, 0.1);
}

.contact-submit {
  background: linear-gradient(135deg, #64748b, #475569);
  color: white;
  border: none;
  padding: 1rem 2rem;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  width: 100%;
}

.contact-submit:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.2);
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 2rem;
}

.section-title {
  font-size: 2.5rem;
  font-weight: 700;
  text-align: center;
  margin-bottom: 3rem;
  color: #2d3748;
  position: relative;
}

.section-title::after {
  content: '';
  position: absolute;
  bottom: -10px;
  left: 50%;
  transform: translateX(-50%);
  width: 80px;
  height: 4px;
  background: linear-gradient(90deg, #64748b, #475569);
  border-radius: 2px;
}

/* 快速访问卡片 */
.quick-access {
  margin-bottom: 4rem;
}

.cards-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 2rem;
  max-width: 1200px;
  margin: 0 auto;
}

.app-card {
  background: white;
  border-radius: 15px;
  padding: 2rem;
  text-align: center;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  cursor: pointer;
  border: 1px solid #e2e8f0;
}

.app-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
}

.app-icon {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, #64748b, #475569);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 1.5rem auto;
  font-size: 2rem;
  color: white;
}

.app-name {
  font-size: 1.5rem;
  font-weight: 600;
  margin: 0 0 1rem 0;
  color: #2d3748;
}

.app-description {
  color: #718096;
  margin-bottom: 1.5rem;
  line-height: 1.6;
}

.app-status {
  display: inline-block;
  padding: 0.5rem 1rem;
  border-radius: 20px;
  font-size: 0.9rem;
  font-weight: 500;
}

.app-status.online {
  background: #c6f6d5;
  color: #22543d;
}

.app-status.development {
  background: #fed7d7;
  color: #742a2a;
}

.app-status.coming-soon {
  background: #feebc8;
  color: #744210;
}

/* 技能区域 */
.skills-section {
  margin-bottom: 4rem;
}

.skills-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 1.5rem;
  max-width: 1200px;
  margin: 0 auto;
}

.skill-tag {
  background: white;
  padding: 1.5rem;
  border-radius: 10px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.08);
  border: 1px solid #e2e8f0;
}

.skill-name {
  display: block;
  font-weight: 600;
  color: #2d3748;
  margin-bottom: 0.5rem;
}

.skill-level {
  background: #e2e8f0;
  height: 8px;
  border-radius: 4px;
  overflow: hidden;
}

.skill-progress {
  height: 100%;
  background: linear-gradient(90deg, #64748b, #475569);
  border-radius: 4px;
  transition: width 2s ease;
}

/* 动态区域 */
.updates-section {
  margin-bottom: 4rem;
}

.updates-list {
  max-width: 800px;
  margin: 0 auto;
}

.update-item {
  background: white;
  border-radius: 10px;
  padding: 2rem;
  margin-bottom: 1.5rem;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.08);
  border: 1px solid #e2e8f0;
  display: flex;
  gap: 2rem;
}

.update-date {
  min-width: 120px;
  color: #64748b;
  font-weight: 600;
  font-size: 0.9rem;
}

.update-content h4 {
  margin: 0 0 0.5rem 0;
  color: #2d3748;
  font-size: 1.2rem;
}

.update-content p {
  margin: 0;
  color: #718096;
  line-height: 1.6;
}

/* 页脚 */
.footer {
  background: #2d3748;
  color: white;
  text-align: center;
  padding: 2rem 0;
}

.footer-links {
  margin-top: 1rem;
  font-size: 0.9rem;
}

.footer-links a {
  color: #a0aec0;
  text-decoration: none;
  margin: 0 0.5rem;
}

.footer-links a:hover {
  color: white;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .profile-card {
    padding: 2rem;
    margin: 1rem;
  }
  
  .name {
    font-size: 2rem;
  }
  
  .container {
    padding: 0 1rem;
  }
  
  .update-item {
    flex-direction: column;
    gap: 1rem;
  }
  
  .update-date {
    min-width: auto;
  }
  
  .social-links {
    flex-wrap: wrap;
  }
  
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 1rem;
  }
  
  .projects-grid {
    grid-template-columns: 1fr;
  }
  
  .blog-grid {
    grid-template-columns: 1fr;
  }
  
  .contact-content {
    grid-template-columns: 1fr;
    gap: 2rem;
  }
  
  .timeline::before {
    left: 20px;
  }
  
  .timeline-item::before {
    left: 14px;
  }
  
  .timeline-date {
    min-width: 100px;
    font-size: 0.8rem;
  }
  
  .timeline-content {
    margin-left: 1rem;
  }
}
</style> 