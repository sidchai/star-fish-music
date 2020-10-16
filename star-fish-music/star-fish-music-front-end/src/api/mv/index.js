import request from '@/utils/request'

// 获取所有歌手信息
export function getAllMV() {
  return request({
    url: `/music/api/music-video/get-mv-list`,
    method: 'get'
  })
}

// 获取播放凭证
export function getPlayAuth(vid) {
  return request({
    url: `/music/api/vod/media/get-play-auth/${vid}`,
    method: 'get'
  })
}
