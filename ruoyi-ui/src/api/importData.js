import request from '@/utils/request'

export function importData(data) {
  return request({
    url: '/import/importData',
    method: 'post',
    data: data
  })
}
