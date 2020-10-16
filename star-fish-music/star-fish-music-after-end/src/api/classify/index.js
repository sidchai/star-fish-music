import request from '@/utils/request'

// 所有类型列表及包含的歌手信息
export function getClassifyBySinger() {
  return request({
    url: `/music/classify/get-classify-by-singer`,
    method: 'get'
  })
}
