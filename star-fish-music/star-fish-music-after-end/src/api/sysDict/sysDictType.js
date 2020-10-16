import request from '@/utils/request'

// 获得字典类型分页信息
export function getSysDictTypeList(params) {
  return request({
    url: `/music/sys-dict-type/get-sys-dict-type-list`,
    method: 'post',
    data: params
  })
}

// 添加字典信息
export function addSysDictType(params) {
  return request({
    url: `/music/sys-dict-type/add-sys-dict-type`,
    method: 'post',
    data: params
  })
}

// 修改字典信息
export function editSysDictType(params) {
  return request({
    url: `/music/sys-dict-type/edit-sys-dict-type`,
    method: 'put',
    data: params
  })
}

// 删除字典信息
export function deleteSysDictType(id) {
  return request({
    url: `/music/sys-dict-type/delete-sys-dict-type/${id}`,
    method: 'delete'
  })
}

// 批量删除字典信息
export function deleteBatchSysDictType(params) {
  return request({
    url: `/music/sys-dict-type/delete-batch-sys-dict-type`,
    method: 'delete',
    data: params
  })
}
