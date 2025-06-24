import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

export default defineConfig(({ mode }) => {
  // 获取本机IP地址（需要手动设置）
  const LOCAL_IP = process.env.VITE_LOCAL_IP || 'localhost'
  
  return {
    plugins: [vue()],
    resolve: {
      alias: {
        '@': resolve(__dirname, 'src')
      }
    },
    server: {
      port: 3000,
      host: '0.0.0.0', // 允许外部访问
      proxy: {
        '/dept-api': {
          target: `http://${LOCAL_IP}:9999`,
          changeOrigin: true,
          secure: false
        },
        '/avatars': {
          target: `http://${LOCAL_IP}:9999`,
          changeOrigin: true,
          secure: false,
          configure: (proxy, options) => {
            proxy.on('error', (err, req, res) => {
              console.log('proxy error', err);
            });
            proxy.on('proxyReq', (proxyReq, req, res) => {
              console.log('Sending Request to the Target:', req.method, req.url);
            });
            proxy.on('proxyRes', (proxyRes, req, res) => {
              console.log('Received Response from the Target:', proxyRes.statusCode, req.url);
            });
          }
        }
      }
    },
    preview: {
      port: 3000,
      host: '0.0.0.0',
      proxy: {
        '/dept-api': {
          target: `http://${LOCAL_IP}:9999`,
          changeOrigin: true,
          secure: false
        },
        '/avatars': {
          target: `http://${LOCAL_IP}:9999`,
          changeOrigin: true,
          secure: false
        }
      }
    }
  }
}) 