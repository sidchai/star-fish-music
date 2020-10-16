import request from '@/utils/request'

// 添加信息到回收站
export function addRecycle(param) {
  return request({
    url: `/music/recycle/add-recycle`,
    method: 'post',
    data: param
  })
}

// 获取 回收站中所有信息
export function getRecycleList() {
  return request({
    url: `/music/recycle/get-list`,
    method: 'get'
  })
}

// 恢复 把信息从回收站中移除
export function restoreMessage(param) {
  return request({
    url: `/music/recycle/restore-message`,
    method: 'delete',
    data: param
  })
}
