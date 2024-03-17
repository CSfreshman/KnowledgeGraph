import request from '@/utils/request'

export function getEdgeInstanceGraph(label){
  return request({
    url: '/graph/getEdgeInstanceGraph',
    method: 'post',
    data: {label:label}
  })
}
