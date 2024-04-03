import request from '@/utils/request'

// 查询【请填写功能名称】列表
export function listHistory(query) {
  return request({
    url: '/history/list',
    method: 'get',
    params: query
  })
}

// 查询【请填写功能名称】详细
export function getHistory(id) {
  return request({
    url: '/history/' + id,
    method: 'get'
  })
}

// 新增【请填写功能名称】
export function addHistory(data) {
  return request({
    url: '/history',
    method: 'post',
    data: data
  })
}

// 修改【请填写功能名称】
export function updateHistory(data) {
  return request({
    url: '/history',
    method: 'put',
    data: data
  })
}

// 删除【请填写功能名称】
export function delHistory(id) {
  return request({
    url: '/history/' + id,
    method: 'delete'
  })
}
