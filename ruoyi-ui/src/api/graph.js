import request from '@/utils/request'

export function getAllGraph() {
  return request({
    url: '/graph/getAllGraph',
    method: 'get'
  })
}
