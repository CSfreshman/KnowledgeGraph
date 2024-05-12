import request from '@/utils/request'

export function getZzDict(data) {
  return request({
    url: '/monitor/cache/getZzDict',
    method: 'post',
    data: data
  })
}

export function addZzDict(data) {
  return request({
    url: '/monitor/cache/addZzDict',
    method: 'post',
    data: data
  })
}

export function delZzDict(data) {
  return request({
    url: '/monitor/cache/delZzDict',
    method: 'post',
    data: data
  })
}

export function updateZzDict(data) {
  return request({
    url: '/monitor/cache/updateZzDict',
    method: 'post',
    data: data
  })
}
