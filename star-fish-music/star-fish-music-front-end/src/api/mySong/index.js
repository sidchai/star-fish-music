import request from '@/utils/request'

export function getMySongById(id) {
  return request({
    url: `/music/api/my-song/get-my-song-by-id/${id}`,
    method: 'get'
  })
}

// 收藏
export function addCollection(params) {
  return request({
    url: `/music/api/my-song/add-collection`,
    method: 'post',
    data: params
  })
}
