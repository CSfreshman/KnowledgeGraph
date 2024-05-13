import request from '@/utils/request'

// 查询列表
export function listClass(query) {
  return request({
    url: '/system/mange/class/node/list',
    method: 'get',
    params: query
  })
}

export function getAllNodeClass(query) {
  return request({
    url: '/system/mange/class/node/getAll',
    method: 'get',
    params: query
  })
}

// 查询详细
export function getClass(id) {
  return request({
    url: '/system/mange/class/node/' + id,
    method: 'get'
  })
}

// 新增
export function addClass(data) {
  return request({
    url: '/system/mange/class/node',
    method: 'post',
    data: data
  })
}

// 修改
export function updateClass(data) {
  return request({
    url: '/system/mange/class/node',
    method: 'put',
    data: data
  })
}

// 删除
export function delClass(id) {
  return request({
    url: '/system/mange/class/node/' + id,
    method: 'delete'
  })
}
