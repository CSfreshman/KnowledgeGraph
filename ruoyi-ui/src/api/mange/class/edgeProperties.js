import request from '@/utils/request'

// 查询【请填写功能名称】列表
export function listClassProperties(query) {
  return request({
    url: '/mange/class/edgeProperties/list',
    method: 'get',
    params: query
  })
}

// 查询【请填写功能名称】详细
export function getClassProperties(id) {
  return request({
    url: '/mange/class/edgeProperties/' + id,
    method: 'get'
  })
}

// 新增【请填写功能名称】
export function addClassProperties(data) {
  return request({
    url: '/mange/class/edgeProperties',
    method: 'post',
    data: data
  })
}

// 修改【请填写功能名称】
export function updateClassProperties(data) {
  return request({
    url: '/mange/class/edgeProperties',
    method: 'put',
    data: data
  })
}

// 删除【请填写功能名称】
export function delClassProperties(id) {
  return request({
    url: '/mange/class/edgeProperties/' + id,
    method: 'delete'
  })
}
