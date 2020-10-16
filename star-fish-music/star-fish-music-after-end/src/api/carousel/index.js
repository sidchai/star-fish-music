import request from '@/utils/request'

// 获取所有轮播图信息
export function getCarouselAll() {
  return request({
    url: `/music/carousel/get-carousel-all`,
    method: 'get'
  })
}

// 新增轮播图
export function addCarousel(parmas) {
  return request({
    url: `/music/carousel/add-carousel`,
    method: 'post',
    data: parmas
  })
}

// 修改轮播图
export function editCarousel(params) {
  return request({
    url: `/music/carousel/edit-carousel`,
    method: 'put',
    data: params
  })
}

// 删除轮播图
export function deleteCarousel(id) {
  return request({
    url: `/music/carousel/delete-carousel/${id}`,
    method: 'delete'
  })
}

// 批量删除轮播图
export function deleteBatchCarousel(params) {
  return request({
    url: `/music/carousel/delete-batch-carousel`,
    method: 'delete',
    data: params
  })
}

// 改变轮播图状态
export function changeCarouselStatus(params) {
  return request({
    url: `/music/carousel/change-carousel-status`,
    method: 'put',
    data: params
  })
}

// 添加或修改轮播图图片
export function addOrChangeCarouselAvatar(params) {
  return request({
    url: `/music/carousel/add-or-change-carousel-avatar`,
    method: 'put',
    data: params
  })
}
