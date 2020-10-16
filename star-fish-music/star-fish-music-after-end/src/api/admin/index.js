import request from '@/utils/request'

// 获取所有管理员列表信息
export function adminList() {
  return request({
    url: `/music/admin/get-list`,
    method: 'get'
  })
}

// 获取管理员全部信息分页展示
export function getAdminListPage(currentPage, pageSize) {
  return request({
    url: `/music/admin/get-list/${currentPage}/${pageSize}`,
    method: 'get'
  })
}

// 重置管理员密码
export function restPwd(id) {
  return request({
    url: `/music/admin/rest-pwd/${id}`,
    method: 'post'
  })
}

// 删除管理员
export function deleteAdmin(id) {
  return request({
    url: `/music/admin/delete-admin/${id}`,
    method: 'delete'
  })
}

// 批量删除管理员
export function deleteAdmins(params) {
  return request({
    url: `/music/admin/delete-admins/`,
    method: 'delete',
    params
  })
}

// 修改管理员信息
export function editAdmin(params) {
  return request({
    url: `/music/admin/edit-admin`,
    method: 'put',
    data: params
  })
}

// 添加管理员信息
export function addAdmin(param) {
  return request({
    url: `/music/admin/add-admin`,
    method: 'post',
    data: param
  })
}

// 改变管理员状态信息
export function changeStatus(param) {
  return request({
    url: `/music/admin/change-status`,
    method: 'put',
    data: param
  })
}

// 获取当前登录用户信息
export function getLoginAdminInfo() {
  return request({
    url: `/music/admin/get-login-admin-info`,
    method: 'get'
  })
}

// 添加或修改管理员头像
export function addOrChangeAdminAvatar(params) {
  return request({
    url: `/music/admin/add-or-change-admin-avatar`,
    method: 'put',
    data: params
  })
}

// 验证原密码是否正确
export function verifyPwd(params) {
  return request({
    url: `/music/admin/verify-pwd`,
    method: 'post',
    data: params
  })
}

// 修改管理员密码editPassword
export function editPassword(params) {
  return request({
    url: `/music/admin/edit-password`,
    method: 'put',
    data: params
  })
}
