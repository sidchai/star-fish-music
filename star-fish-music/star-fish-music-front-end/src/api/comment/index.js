import request from '@/utils/request'

// 获取评论信息
export function getAllComment(type, id) {
  console.log(type)
  if(type === 1) {
    return request({
      url: `/music/api/comment/get-song-list-comment/${id}`,
      method: 'get'
    })
  }
  if(type === 0) {
    return request({
      url: `/music/api/comment/get-song-comment/${id}`,
      method: 'get'
    })
  }
}

// 添加评论信息
export function addComment(params) {
  return request({
    url: `/music/api/comment/add-comment`,
    method: 'post',
    data: params
  })
}

// 点赞
export function setLike(params) {
  return request({
    url:`/music/api/comment/set-up`,
    method: 'put',
    data: params
  })
}
