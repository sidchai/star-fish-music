import request from '@/utils/request'

// 管理员登录
export function login(data) {
  return request({
    url: `/music/auth/login`,
    method: 'post',
    data
  })
}

// 登录后 获取管理员信息
export function getInfo() {
  return request({
    url: `/music/auth/info`,
    method: 'get'
  })
}

export function getMenu() {
  return request({
    url: `/music/auth/get-menu`,
    method: 'get'
  })
}

// 登出
export function logout() {
  return request({
    url: `/music/auth/logout`,
    method: 'post'
  })
}
