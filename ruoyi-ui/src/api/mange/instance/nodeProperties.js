import request from '@/utils/request'

// 查询列表
export function listNodeProperties(query) {
  return request({
    url: '/mange/instance/nodeProperties/list',
    method: 'get',
    params: query
  })
}

// 查询详细
export function getNodeProperties(id) {
  return request({
    url: '/mange/instance/nodeProperties/' + id,
    method: 'get'
  })
}

// 新增
export function addNodeProperties(data) {
  return request({
    url: '/mange/instance/nodeProperties',
    method: 'post',
    data: data
  })
}

// 修改
export function updateNodeProperties(data) {
  return request({
    url: '/mange/instance/nodeProperties',
    method: 'put',
    data: data
  })
}

// 删除
export function delNodeProperties(id) {
  return request({
    url: '/mange/instance/nodeProperties/' + id,
    method: 'delete'
  })
}
