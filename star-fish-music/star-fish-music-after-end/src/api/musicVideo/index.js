import request from '@/utils/request'

// 获取MV全部信息
export function getMvList() {
  return request({
    url: `/music/music-video/get-mv-list`,
    method: 'get'
  })
}

// 添加MV信息
export function addMv(params) {
  return request({
    url: `/music/music-video/add-mv`,
    method: 'post',
    data: params
  })
}

// 修改MV信息
export function editMv(params) {
  return request({
    url: `/music/music-video/edit-mv`,
    method: 'put',
    data: params
  })
}

// 删除MV信息
export function deleteMv(id) {
  return request({
    url: `/music/music-video/delete-mv/${id}`,
    method: 'delete'
  })
}

// 改变mv状态
export function changeMvStatus(params) {
  return request({
    url: `/music/music-video/change-mv-status`,
    method: 'put',
    data: params
  })
}

// 删除视频文件api
export function removeByVodId(id) {
  return request({
    url: `/music/vod/media/remove/${id}`,
    method: 'delete'
  })
}

// 改变数据库中视频信息
export function changeVideo(params) {
  return request({
    url: `/music/music-video/change-video`,
    method: 'put',
    data: params
  })
}
