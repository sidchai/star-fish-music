import request from '@/utils/request'

export function getMvList() {
  return request({
    url: '/music/music-video/get-mv-list',
    method: 'get'
  })
}
