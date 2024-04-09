import request from '@/utils/request'

export function submitSymptomsDesc(data) {
  return request({
    url: '/extra/diagnose',
    method: 'post',
    data: data
  })
}
