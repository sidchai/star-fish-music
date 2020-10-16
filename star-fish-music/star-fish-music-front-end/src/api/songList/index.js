import request from '@/utils/request'

// 获取所有歌单信息
export function getAllSongList() {
  return request({
    url: `/music/api/song-list/get-song-list-info`,
    method: 'get'
  })
}

// 根据歌单id查询歌单信息
export function getSongListById(id) {
  return request({
    url: `/music/api/song-list/get-song-list-by-id/${id}`,
    method: 'get'
  })
}

// 根据歌单id查询包含歌曲信息
export function getSongByListId(id) {
  return request({
    url: `/music/api/song-list/get-song-by-list-id/${id}`,
    method: 'get'
  })
}

// 根据歌单标题进行模糊查询
export function getSongListByLikeTitle(keyword) {
  return request({
    url: `/music/api/song-list/get-song-list-by-like-title/${keyword}`,
    method: 'get'
  })
}

// 通过类别获取歌单
export function getSongListByStyle(style) {
  return request({
    url: `/music/api/song-list/get-song-list-by-style/${style}`,
    method: 'get'
  })
}
