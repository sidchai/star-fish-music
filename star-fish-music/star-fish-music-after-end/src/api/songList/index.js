import request from '@/utils/request'

// 获取所有歌单信息列表
export function getSongListInfo() {
  return request({
    url: '/music/song-list/get-song-list-info',
    method: 'get'
  })
}

// 添加歌单信息
export function addSongList(params) {
  return request({
    url: '/music/song-list/add-song-list',
    method: 'post',
    data: params
  })
}

// 修改歌单信息
export function editSongList(params) {
  return request({
    url: '/music/song-list/edit-song-list',
    method: 'put',
    data: params
  })
}

// 删除歌单信息
export function deleteSongList(id) {
  return request({
    url: `/music/song-list/delete-song-list/${id}`,
    method: 'delete'
  })
}

// 批量删除歌单信息
export function deleteBatchSongList(params) {
  return request({
    url: `/music/song-list/delete-batch-song-list`,
    method: 'delete',
    data: params
  })
}

// 修改歌单的状态
export function changeSongListStatus(params) {
  return request({
    url: `/music/song-list/change-song-list-status`,
    method: 'put',
    data: params
  })
}

// 添加或修改歌单图片
export function addOrChangeSongListAvatar(params) {
  return request({
    url: `/music/song-list/add-or-change-song-list-avatar`,
    method: 'put',
    data: params
  })
}
