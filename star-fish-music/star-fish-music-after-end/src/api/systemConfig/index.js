import request from '@/utils/request'

// 拉取网站系统配置信息
export function getSystemConfig() {
  return request({
    url: `/music/system-config/get-system-config`,
    method: 'get'
  })
}

// 修改 网站系统信息
export function editSystemConfig(param) {
  return request({
    url: `/music/system-config/edit-system-config`,
    method: 'put',
    data: param
  })
}
