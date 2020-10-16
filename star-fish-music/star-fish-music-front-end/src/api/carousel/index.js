import request from '@/utils/request'

export function getCarouselAll() {
 return request({
   url: `/music/api/carousel/get-carousel-all`,
   method: 'get'
 })
}
