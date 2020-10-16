import request from '@/utils/request'

// 获取所有歌曲 & 歌单 中间表信息
export function getSongListSongInfo(id) {
  return request({
    url: `/music/song-list-song/get-song-list-song-info/${id}`,
    method: 'get'
  })
}

// 添加歌曲信息到 中间表中
export function addSongListSongInfo(params) {
  return request({
    url: `/music/song-list-song/add-song-list-song-info`,
    method: 'post',
    data: params
  })
}

// 删除中间表中信息    id : 歌曲id
export function deleteSongListSongInfo(id) {
  return request({
    url: `/music/song-list-song/delete-song-list-song-info/${id}`,
    method: 'delete'
  })
}

// 批量删除信息   params : 歌曲id集合
export function deleteBatchSongListSongInfo(params) {
  return request({
    url: `/music/song-list-song/delete-batch-song-list-song-info`,
    method: 'delete',
    data: params
  })
}
