import request from '@/utils/request'

// 查询列表
export function listNode(query) {
  return request({
    url: '/mange/instance/node/list',
    method: 'get',
    params: query
  })
}

export function getAll(query) {
  return request({
    url: '/mange/instance/node/getAll',
    method: 'post',
    data: query
  })
}

export function getAllByClassId(query) {
  return request({
    url: '/mange/instance/node/getAllByClassId',
    method: 'post',
    data: query
  })
}



// 查询详细
export function getNode(id) {
  return request({
    url: '/mange/instance/node/' + id,
    method: 'get'
  })
}

// 新增
export function addNode(data) {
  return request({
    url: '/mange/instance/node',
    method: 'post',
    data: data
  })
}

// 修改
export function updateNode(data) {
  return request({
    url: '/mange/instance/node',
    method: 'put',
    data: data
  })
}

// 删除
export function delNode(id) {
  return request({
    url: '/mange/instance/node/' + id,
    method: 'delete'
  })
}
