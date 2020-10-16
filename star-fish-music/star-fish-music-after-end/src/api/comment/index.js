import request from '@/utils/request'

// 获取所有评论信息
export function getCommentList() {
  return request({
    url: `/music/comment/get-comment-list`,
    method: 'get'
  })
}

// 删除评论信息
export function deleteComment(id) {
  return request({
    url: `/music/comment/delete-comment/${id}`,
    method: 'delete'
  })
}

// 批量删除评论信息
export function deleteBatchComment(params) {
  return request({
    url: `/music/comment/delete-batch-comment`,
    method: 'delete',
    data: params
  })
}
