import request from '@/utils/request'

// 拉取网站基础配置表
export function getWebConfig() {
  return request({
    url: `/music/web-config/get-web-config`,
    method: 'get'
  })
}

// 修改 网站基础信息
export function editWebConfig(param) {
  return request({
    url: `/music/web-config/edit-web-config`,
    method: 'put',
    data: param
  })
}
