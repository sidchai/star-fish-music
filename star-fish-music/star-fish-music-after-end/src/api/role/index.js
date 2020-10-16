import request from '@/utils/request'

// 获得角色分页信息
export function getRoleList(currentPage, pageSize) {
  return request({
    url: `/music/role/get-role-list/${currentPage}/${pageSize}`,
    method: 'get'
  })
}

// 根据关键字获得角色分页信息
export function getRoleListByKeyword(currentPage, pageSize, keyword) {
  return request({
    url: `/music/role/get-role-list/${currentPage}/${pageSize}/${keyword}`,
    method: 'get'
  })
}

// 增加角色
export function addRole(param) {
  return request({
    url: `/music/role/add-role`,
    method: 'post',
    data: param
  })
}

// 编辑角色
export function editRole(param) {
  return request({
    url: `/music/role/edit-role`,
    method: 'put',
    data: param
  })
}

// 删除角色
export function deleteRole(id) {
  return request({
    url: `/music/role/delete-role/${id}`,
    method: 'delete'
  })
}

