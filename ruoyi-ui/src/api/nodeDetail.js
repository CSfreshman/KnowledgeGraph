import request from '@/utils/request'

export function getNodeDetail(nodeId,degree) {
  return request({
    url: '/graph/getNodeDetail',
    method: 'post',
    data: {'nodeId':nodeId,'degree':degree}
  })
}
