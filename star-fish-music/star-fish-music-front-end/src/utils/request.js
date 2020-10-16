import axios from 'axios'
import { Message } from 'element-ui'
import cookie from 'js-cookie'
// 创建 axios 实例
const service = axios.create({
  baseURL: process.env.VUE_APP_URL,
  timeout: 30000 // 请求超时时间
})

// http request 拦截器
service.interceptors.request.use(
  config => {
    // 当cookie中获取到了token信息时，则将token放在header中随请求发送给服务端接口
    if (cookie.get('sidchai_jwt_token')) {
      config.headers['token'] = cookie.get('sidchai_jwt_token')
    }

    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// response 拦截器
service.interceptors.response.use(
  response => {
    /**
       * code为非20000是抛错 可结合自己业务进行修改
       */
    const res = response.data
    if (res.code === 20000) { // 返回正确的结果
      return response.data
    } else if (res.code === 23004) { // 获取用户信息失败
      // 清除cookie
      cookie.set('sidchai_jwt_token', '', { domain: 'localhost' })
      return response.data
    } else if (res.code === 25000) {
      return response.data
    } else if (res.code === 28004) { // 鉴权失败的响应码
      // 跳转到登录页面
      window.location.href = '/login'
      return
    } else { // 其他错误的结果
      Message({
        message: res.message || 'error',
        type: 'error',
        duration: 5 * 1000
      })

      return Promise.reject('error')
    }
  },
  error => {
    console.log('err：' + error) // for debug
    Message({
      message: error.message,
      type: 'error',
      duration: 5 * 1000
    })
    return Promise.reject(error)
  }
)

export default service
