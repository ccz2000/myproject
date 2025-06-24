import MarkdownIt from 'markdown-it'
import hljs from 'highlight.js/lib/core'

// 导入常用语言
import javascript from 'highlight.js/lib/languages/javascript'
import typescript from 'highlight.js/lib/languages/typescript'
import java from 'highlight.js/lib/languages/java'
import python from 'highlight.js/lib/languages/python'
import sql from 'highlight.js/lib/languages/sql'
import xml from 'highlight.js/lib/languages/xml'
import css from 'highlight.js/lib/languages/css'
import json from 'highlight.js/lib/languages/json'
import bash from 'highlight.js/lib/languages/bash'
import yaml from 'highlight.js/lib/languages/yaml'

// 注册语言
hljs.registerLanguage('javascript', javascript)
hljs.registerLanguage('typescript', typescript)
hljs.registerLanguage('java', java)
hljs.registerLanguage('python', python)
hljs.registerLanguage('sql', sql)
hljs.registerLanguage('xml', xml)
hljs.registerLanguage('html', xml)
hljs.registerLanguage('css', css)
hljs.registerLanguage('json', json)
hljs.registerLanguage('bash', bash)
hljs.registerLanguage('shell', bash)
hljs.registerLanguage('yaml', yaml)
hljs.registerLanguage('yml', yaml)

// 创建MarkdownIt实例
const md = new MarkdownIt({
  html: true,        // 允许HTML标签
  linkify: true,     // 自动转换URL为链接
  typographer: true, // 启用一些语言中性的替换 + 引号美化
  breaks: true,      // 转换换行符为<br>
  highlight: function (str, lang) {
    // 代码高亮处理
    if (lang && hljs.getLanguage(lang)) {
      try {
        return '<pre class="hljs"><code>' +
               hljs.highlight(str, { language: lang, ignoreIllegals: true }).value +
               '</code></pre>'
      } catch (__) {}
    }

    return '<pre class="hljs"><code>' + md.utils.escapeHtml(str) + '</code></pre>'
  }
})

// 自定义渲染规则

// 增强表格渲染
md.renderer.rules.table_open = function () {
  return '<div class="table-container"><table class="markdown-table">'
}

md.renderer.rules.table_close = function () {
  return '</table></div>'
}

// 增强链接渲染（添加target="_blank"）
const defaultLinkRender = md.renderer.rules.link_open || function(tokens, idx, options, env, renderer) {
  return renderer.renderToken(tokens, idx, options)
}

md.renderer.rules.link_open = function (tokens, idx, options, env, renderer) {
  const aIndex = tokens[idx].attrIndex('target')
  
  if (aIndex < 0) {
    tokens[idx].attrPush(['target', '_blank'])
  } else {
    tokens[idx].attrs[aIndex][1] = '_blank'
  }
  
  return defaultLinkRender(tokens, idx, options, env, renderer)
}

// 增强代码块渲染
const defaultCodeBlockRender = md.renderer.rules.code_block || function(tokens, idx, options, env, renderer) {
  return renderer.renderToken(tokens, idx, options)
}

md.renderer.rules.code_block = function (tokens, idx, options, env, renderer) {
  const token = tokens[idx]
  const content = token.content
  
  return `<div class="code-block">
    <div class="code-header">
      <span class="code-lang">代码</span>
      <button class="copy-btn" onclick="copyToClipboard(this)" data-code="${md.utils.escapeHtml(content)}">
        <i class="el-icon-document-copy"></i> 复制
      </button>
    </div>
    <pre class="hljs"><code>${md.utils.escapeHtml(content)}</code></pre>
  </div>`
}

// 增强围栏代码块渲染
const defaultFenceRender = md.renderer.rules.fence || function(tokens, idx, options, env, renderer) {
  return renderer.renderToken(tokens, idx, options)
}

md.renderer.rules.fence = function (tokens, idx, options, env, renderer) {
  const token = tokens[idx]
  const info = token.info ? md.utils.unescapeAll(token.info).trim() : ''
  const langName = info.split(/\s+/g)[0]
  const content = token.content
  
  let highlighted = ''
  if (langName && hljs.getLanguage(langName)) {
    try {
      highlighted = hljs.highlight(content, { language: langName, ignoreIllegals: true }).value
    } catch (__) {
      highlighted = md.utils.escapeHtml(content)
    }
  } else {
    highlighted = md.utils.escapeHtml(content)
  }
  
  return `<div class="code-block">
    <div class="code-header">
      <span class="code-lang">${langName || '代码'}</span>
      <button class="copy-btn" onclick="copyToClipboard(this)" data-code="${md.utils.escapeHtml(content)}">
        <i class="el-icon-document-copy"></i> 复制
      </button>
    </div>
    <pre class="hljs"><code class="language-${langName}">${highlighted}</code></pre>
  </div>`
}

/**
 * 渲染Markdown文本
 * @param {string} text - Markdown文本
 * @returns {string} 渲染后的HTML
 */
export function renderMarkdown(text) {
  if (!text) return ''
  
  try {
    return md.render(text)
  } catch (error) {
    console.error('Markdown渲染失败:', error)
    return `<p>渲染失败: ${error.message}</p>`
  }
}

/**
 * 渲染行内Markdown（不包含块级元素）
 * @param {string} text - Markdown文本
 * @returns {string} 渲染后的HTML
 */
export function renderInlineMarkdown(text) {
  if (!text) return ''
  
  try {
    return md.renderInline(text)
  } catch (error) {
    console.error('行内Markdown渲染失败:', error)
    return text
  }
}

/**
 * 复制代码到剪贴板（全局函数，供HTML调用）
 */
window.copyToClipboard = function(button) {
  const code = button.getAttribute('data-code')
  
  if (navigator.clipboard) {
    navigator.clipboard.writeText(code).then(() => {
      button.innerHTML = '<i class="el-icon-check"></i> 已复制'
      setTimeout(() => {
        button.innerHTML = '<i class="el-icon-document-copy"></i> 复制'
      }, 2000)
    }).catch(err => {
      console.error('复制失败:', err)
    })
  } else {
    // 降级方案
    const textarea = document.createElement('textarea')
    textarea.value = code
    document.body.appendChild(textarea)
    textarea.select()
    document.execCommand('copy')
    document.body.removeChild(textarea)
    
    button.innerHTML = '<i class="el-icon-check"></i> 已复制'
    setTimeout(() => {
      button.innerHTML = '<i class="el-icon-document-copy"></i> 复制'
    }, 2000)
  }
}

export default {
  renderMarkdown,
  renderInlineMarkdown
} 