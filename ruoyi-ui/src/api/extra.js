import request from '@/utils/request'

export function submitSymptomsDesc(data) {
  return request({
    url: '/extra/participle',
    method: 'post',
    data: data
  })
}

export function executeDiagnose(data) {
  return request({
    url: '/extra/executeDiagnose',
    method: 'post',
    data: data
  })
}
