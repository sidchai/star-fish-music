import request from '@/utils/request'

// 获取所有歌曲信息列表
export function getSongList() {
  return request({
    url: '/music/song/get-song-list',
    method: 'get'
  })
}

// 添加歌曲信息
export function addSong(params) {
  return request({
    url: '/music/song/add-song',
    method: 'post',
    data: params
  })
}

// 修改歌曲信息
export function editSong(params) {
  return request({
    url: '/music/song/edit-song',
    method: 'put',
    data: params
  })
}

// 删除歌曲信息
export function deleteSong(id) {
  return request({
    url: `/music/song/delete-song/${id}`,
    method: 'delete'
  })
}

// 批量删除歌曲信息
export function deleteBatchSong(params) {
  return request({
    url: `/music/song/delete-batch-song`,
    method: 'delete',
    data: params
  })
}

// 修改歌曲的状态
export function changeSongStatus(params) {
  return request({
    url: `/music/song/change-song-status`,
    method: 'put',
    data: params
  })
}

// 添加或修改歌曲专辑图片
export function addOrChangeSongAvatar(params) {
  return request({
    url: `/music/song/add-or-change-song-avatar`,
    method: 'put',
    data: params
  })
}

// 添加或修改歌曲文件
export function addOrChangeSongUrl(params) {
  return request({
    url: `/music/song/add-or-change-song-url`,
    method: 'put',
    data: params
  })
}
