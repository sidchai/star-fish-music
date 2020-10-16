import request from '@/utils/request'

// 根据关键字查询歌手名信息
export function getNameListByKeyword(keyword) {
  return request({
    url: `/music/singer/get-name-list-by-keyword/${keyword}`,
    method: 'get'
  })
}

// 获取歌手所有信息
export function getAllSinger() {
  return request({
    url: '/music/singer/get-all-singer',
    method: 'get'
  })
}

// 获取歌手分页信息
export function getSingerPage(currentPage, pageSize) {
  return request({
    url: `/music/singer/get-list/${currentPage}/${pageSize}`,
    method: 'get'
  })
}

// 根据关键词查询歌手分页信息
export function getSingerPageByKeyword(currentPage, pageSize, keyword) {
  return request({
    url: `/music/singer/get-list/${currentPage}/${pageSize}/${keyword}`,
    method: 'get'
  })
}

// 所有歌手信息列表以及包含的歌曲信息
export function getSingerBySong() {
  return request({
    url: `/music/singer/get-singer-by-song`,
    method: 'get'
  })
}

// 添加歌手信息
export function addSinger(singer) {
  return request({
    url: `/music/singer/add-singer`,
    method: 'post',
    data: singer
  })
}

// 修改歌手信息
export function editSinger(singer) {
  return request({
    url: `/music/singer/edit-singer`,
    method: 'put',
    data: singer
  })
}

// 删除歌手信息
export function deleteSinger(id) {
  return request({
    url: `/music/singer/delete-singer/${id}`,
    method: 'delete'
  })
}

// 批量删除歌手信息
export function deleteSingers(singer) {
  return request({
    url: `/music/singer/delete-singers`,
    method: 'delete',
    data: singer
  })
}

// 改变歌手状态信息
export function changeSingerStatus(singer) {
  return request({
    url: `/music/singer/change-status`,
    method: 'put',
    data: singer
  })
}

// 添加或修改歌手图片
export function addOrChangeSingerPic(singer) {
  return request({
    url: `/music/singer/add-or-change-singer-pic`,
    method: 'put',
    data: singer
  })
}
