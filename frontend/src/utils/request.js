import axios from 'axios'
import { Message } from 'element-ui'
import router from '@/router'

const request = axios.create({ baseURL: '/api', timeout: 15000 })

request.interceptors.request.use(cfg => {
  const t = localStorage.getItem('token')
  if (t) cfg.headers['Authorization'] = 'Bearer ' + t
  return cfg
})

request.interceptors.response.use(resp => {
  const r = resp.data
  if (r && r.code !== undefined && r.code !== 200) {
    Message.error(r.msg || '请求失败')
    if (r.code === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      router.push('/login')
    }
    return Promise.reject(new Error(r.msg || 'error'))
  }
  return r
}, err => {
  if (err.response && err.response.status === 401) {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    router.push('/login')
    Message.error('登录已过期，请重新登录')
  } else {
    Message.error((err.response && err.response.data && err.response.data.msg) || err.message || '网络错误')
  }
  return Promise.reject(err)
})

export default request
