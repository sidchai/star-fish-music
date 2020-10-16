import request from '@/utils/request'

export function changeCount(id) {
  return request({
    url: `/music/api/song/change-count/${id}`,
    method: 'put'
  })
}
