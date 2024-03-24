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
