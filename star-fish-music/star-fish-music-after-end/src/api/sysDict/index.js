import request from '@/utils/request'

// 根据字典类型查询字典数据
export function getListByDictType(dictType) {
  return request({
    url: `/music/sys-dict/get-list-by-dict-type/${dictType}`,
    method: 'get'
  })
}

// 根据字典类型数组查询字典数据
export function getListByDictTypeList(params) {
  return request({
    url: `/music/sys-dict/get-list-by-dict-type-list`,
    method: 'post',
    data: params
  })
}

// 查询字典数据分页信息
export function getSysDictList(params) {
  return request({
    url: `/music/sys-dict/get-sys-dict-list`,
    method: 'post',
    data: params
  })
}

// 添加字典数据
export function addSysDict(params) {
  return request({
    url: `/music/sys-dict/add-sys-dict`,
    method: 'post',
    data: params
  })
}

// 修改字典数据
export function editSysDict(params) {
  return request({
    url: `/music/sys-dict/edit-sys-dict`,
    method: 'put',
    data: params
  })
}

// 删除字典数据
export function deleteSysDict(id) {
  return request({
    url: `/music/sys-dict/delete-sys-dict/${id}`,
    method: 'delete'
  })
}

// 批量删除字典数据
export function deleteBatchSysDict(params) {
  return request({
    url: `/music/sys-dict/delete-batch-sys-dict`,
    method: 'delete',
    data: params
  })
}
