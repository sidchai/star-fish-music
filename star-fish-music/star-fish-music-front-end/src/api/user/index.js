import request from '@/utils/request'

// 登录
export function loginIn(params) {
  return request({
    url: `/music/api/user/login`,
    method: 'post',
    data: params
  })
}

// 获取登录用户信息
export function getUserInfoById(id) {
  return request({
    url: `/music/api/user/info/${id}`,
    method: 'post'
  })
}

// 修改用户信息
export function editUserInfo(params) {
  return request({
    url: `/music/api/user/edit-user`,
    method: 'put',
    data: params
  })
}

// 根据字典类型查询字典数据
export function getListByDictType(dictType) {
  return request({
    url: `/music/sys-dict/get-list-by-dict-type/${dictType}`,
    method: 'get'
  })
}

// 添加或修改用户头像
export function addOrChangeUserAvatar(params) {
  return request({
    url: `/music/api/user/add-or-change-user-avatar`,
    method: 'put',
    data: params
  })
}
