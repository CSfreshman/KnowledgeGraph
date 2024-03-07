import request from '@/utils/request'

// 查询【请填写功能名称】列表
export function listClass(query) {
  return request({
    url: '/mange/class/edge/list',
    method: 'get',
    params: query
  })
}

// 查询【请填写功能名称】详细
export function getClass(id) {
  return request({
    url: '/mange/class/edge/' + id,
    method: 'get'
  })
}

// 新增【请填写功能名称】
export function addClass(data) {
  return request({
    url: '/mange/class/edge',
    method: 'post',
    data: data
  })
}

// 修改【请填写功能名称】
export function updateClass(data) {
  return request({
    url: '/mange/class/edge',
    method: 'put',
    data: data
  })
}

// 删除【请填写功能名称】
export function delClass(id) {
  return request({
    url: '/mange/class/edge/' + id,
    method: 'delete'
  })
}
