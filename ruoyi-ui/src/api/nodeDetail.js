import request from '@/utils/request'

export function getNodeDetail(nodeId,degree) {
  return request({
    url: '/graph/getNodeDetail',
    method: 'post',
    data: {'nodeId':nodeId,'degree':degree}
  })
}

export function getSingleEdgeByEdgeId(edgeId) {
  return request({
    url: '/graph/getSingleEdgeByEdgeId',
    method: 'post',
    data: {'edgeId':edgeId}
  })
}

export function updateNodeDetail(nodeData) {
  return request({
    url: '/graph/updateNodeDetail',
    method: 'post',
    data: nodeData
  })
}

export function updateEdgeDetail(edgeData) {
  return request({
    url: '/graph/updateEdgeDetail',
    method: 'post',
    data: edgeData
  })
}

export function deleteNode(nodeId) {
  return request({
    url: '/graph/deleteNode',
    method: 'post',
    data: {nodeId: nodeId}
  })
}
