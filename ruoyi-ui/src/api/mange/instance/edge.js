import request from '@/utils/request'

export function getEdgeInstanceGraph(label){
  return request({
    url: '/graph/getEdgeInstanceGraph',
    method: 'post',
    data: {label:label}
  })
}

export function getAllEdge(query) {
  return request({
    url: '/mange/instance/edge/getAll',
    method: 'get',
    params: query
  })
}


// 查询【请填写功能名称】列表
export function listInstance(query) {
  return request({
    url: '/mange/instance/edge/list',
    method: 'get',
    params: query
  })
}

// 查询【请填写功能名称】详细
export function getInstance(id) {
  return request({
    url: '/mange/instance/edge/' + id,
    method: 'get'
  })
}

// 新增【请填写功能名称】
export function addEdgeInstance(data) {
  return request({
    url: '/mange/instance/edge',
    method: 'post',
    data: data
  })
}

// 修改【请填写功能名称】
export function updateInstance(data) {
  return request({
    url: '/mange/instance/edge',
    method: 'put',
    data: data
  })
}

// 删除【请填写功能名称】
export function delInstance(id) {
  return request({
    url: '/mange/instance/edge/' + id,
    method: 'delete'
  })
}

