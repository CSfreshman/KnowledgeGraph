import request from '@/utils/request'

export function getAllGraph() {
  return request({
    url: '/graph/getAllGraph',
    method: 'get'
  })
}

export function graphSelect(data) {
  return request({
    url: '/graph/graphSelect',
    method: 'post',
    data: data
  })
}

export function pathAnalyse(data) {
  return request({
    url: '/graph/analyse/path',
    method: 'post',
    data: data
  })
}

export function centerMultiDegree(data) {
  return request({
    url: '/graph/analyse/centerMultiDegree',
    method: 'post',
    data: data
  })
}

export function centralityCalculation(data) {
  return request({
    url: '/graph/calculation/centrality',
    method: 'post',
    data: data
  })
}

// 相似度计算
export function similarityCalculation(data) {
  return request({
    url: '/graph/calculation/similarity',
    method: 'post',
    data: data
  })
}


export function statistic() {
  return request({
    url: '/graph/statistic',
    method: 'post'
  })
}

export function deleteNode(data) {
  return request({
    url: '/graph/deleteNode',
    method: 'post',
    data: data
  })
}

export function deleteEdge(data) {
  return request({
    url: '/graph/deleteEdge',
    method: 'post',
    data: data
  })
}
