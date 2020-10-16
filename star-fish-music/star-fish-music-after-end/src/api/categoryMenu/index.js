import request from '@/utils/request'

// 获取所有菜单列表
export function getAllMenu(params) {
  return request({
    url: `/music/category-menu/get-all`,
    method: 'get',
    data: params
  })
}

// 获取菜单列表
export function getMenuList(params) {
  return request({
    url: `/music/category-menu/get-list`,
    method: 'post',
    data: params
  })
}

// 获取所有二级菜单-按钮列表
export function getButtonAll(params) {
  return request({
    url: `/music/category-menu/get-button-all`,
    method: 'get',
    data: params
  })
}

// 增加菜单
export function addMenu(params) {
  return request({
    url: `/music/category-menu/add-menu`,
    method: 'post',
    data: params
  })
}

// 修改菜单
export function editMenu(params) {
  return request({
    url: `/music/category-menu/edit-menu`,
    method: 'put',
    data: params
  })
}

// 删除菜单
export function deleteMenu(id) {
  return request({
    url: `/music/category-menu/delete-menu/${id}`,
    method: 'delete'
  })
}

// 置顶菜单
export function stickMenu(params) {
  return request({
    url: `/music/category-menu/stick-menu`,
    method: 'post',
    data: params
  })
}
