import request from '@/utils/request'

// 查询【请填写功能名称】列表
export function listNodeProperties(query) {
  return request({
    url: '/system/mange/class/nodeProperties/list',
    method: 'get',
    params: query
  })
}

// 查询【请填写功能名称】详细
export function getNodeProperties(id) {
  return request({
    url: '/system/mange/class/nodeProperties/' + id,
    method: 'get'
  })
}

// 新增【请填写功能名称】
export function addNodeProperties(data) {
  return request({
    url: '/system/mange/class/nodeProperties',
    method: 'post',
    data: data
  })
}

// 修改【请填写功能名称】
export function updateNodeProperties(data) {
  return request({
    url: '/system/mange/class/nodeProperties',
    method: 'put',
    data: data
  })
}

// 删除【请填写功能名称】
export function delNodeProperties(id) {
  return request({
    url: '/system/mange/class/nodeProperties/' + id,
    method: 'delete'
  })
}
