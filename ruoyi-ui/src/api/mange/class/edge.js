import request from '@/utils/request'

// 查询列表
export function listClass(query) {
  return request({
    url: '/mange/class/edge/list',
    method: 'get',
    params: query
  })
}

// 查询列表
export function getAll(query) {
  return request({
    url: '/mange/class/edge/getAll',
    method: 'get',
    params: query
  })
}

// 查询详细
export function getClass(id) {
  return request({
    url: '/mange/class/edge/' + id,
    method: 'get'
  })
}

// 新增
export function addClass(data) {
  return request({
    url: '/mange/class/edge',
    method: 'post',
    data: data
  })
}

// 修改
export function updateClass(data) {
  return request({
    url: '/mange/class/edge',
    method: 'put',
    data: data
  })
}

// 删除
export function delClass(id) {
  return request({
    url: '/mange/class/edge/' + id,
    method: 'delete'
  })
}
