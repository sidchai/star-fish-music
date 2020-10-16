import request from '@/utils/request'

// 获取所有用户信息
export function getAllUser() {
  return request({
    url: `/music/user/get-all-user`,
    method: 'get'
  })
}

// 删除用户信息
export function deleteUser(id) {
  return request({
    url: `/music/user/delete-user/${id}`,
    method: 'delete'
  })
}

// 修改用户信息
export function editUser(params) {
  return request({
    url: `/music/user/edit-user`,
    method: 'post',
    data: params
  })
}

// 改变用户状态
export function changeStatus(params) {
  return request({
    url: `/music/user/change-status`,
    method: 'put',
    data: params
  })
}

// 改变用户评论状态
export function changeCommentStatus(params) {
  return request({
    url: `/music/user/change-comment-status`,
    method: 'put',
    data: params
  })
}

// 添加或修改用户头像
export function addOrChangeUserAvatar(params) {
  return request({
    url: `/music/user/add-or-change-user-avatar`,
    method: 'put',
    data: params
  })
}
