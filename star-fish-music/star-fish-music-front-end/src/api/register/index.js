import request from '@/utils/request'

export function sendMessage(phone) {
 return request({
   url: `/music/sms/send/${phone}`,
   method: 'get'
 })
}

export function register(params) {
  return request({
    url: `/music/api/user/register`,
    method: 'post',
    data: params
  })
}
