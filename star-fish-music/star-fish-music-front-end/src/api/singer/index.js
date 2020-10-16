import request from '@/utils/request'

// 获取所有歌手信息
export function getAllSinger() {
  return request({
    url: `/music/api/singer/get-all-singer`,
    method: 'get'
  })
}

// 根据歌手id获取歌曲信息
export function getSongBySingerId(id) {
  return request({
    url: `/music/api/song/get-singer-info-by-id/${id}`,
    method: 'post'
  })
}

// 根据歌手类型获取歌手信息
export function getSingerByClassify(classifyId) {
  return request({
    url: `/music/api/singer/get-singer-by-classify/${classifyId}`,
    method: 'post'
  })
}

// 根据歌手名查询歌曲信息
export function getSongBySingerName(keyword) {
  return request({
    url: `/music/api/singer/get-song-by-singer-name/${keyword}`,
    method: 'get'
  })
}
