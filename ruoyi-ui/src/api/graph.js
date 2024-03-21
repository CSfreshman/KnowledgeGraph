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
