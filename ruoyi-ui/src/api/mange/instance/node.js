import request from '@/utils/request'

// 查询【请填写功能名称】列表
export function listNode(query) {
  return request({
    url: '/mange/instance/node/list',
    method: 'get',
    params: query
  })
}

export function getAllByClassId(query) {
  return request({
    url: '/mange/instance/node/getAllByClassId',
    method: 'post',
    data: query
  })
}



// 查询【请填写功能名称】详细
export function getNode(id) {
  return request({
    url: '/mange/instance/node/' + id,
    method: 'get'
  })
}

// 新增【请填写功能名称】
export function addNode(data) {
  return request({
    url: '/mange/instance/node',
    method: 'post',
    data: data
  })
}

// 修改【请填写功能名称】
export function updateNode(data) {
  return request({
    url: '/mange/instance/node',
    method: 'put',
    data: data
  })
}

// 删除【请填写功能名称】
export function delNode(id) {
  return request({
    url: '/mange/instance/node/' + id,
    method: 'delete'
  })
}
