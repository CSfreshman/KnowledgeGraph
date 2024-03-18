import axios from 'axios'

// 创建axios实例
const http = axios.create({
  // baseURL: 'http:/127.0.0.1:9000',
  baseURL: '/web',
  timeout: 3000,
  headers: {'Content-Type': 'multipart/form-data'}
})

// 获取列表数据
export const getData = () => {
  return axios.get('/Knowledge/listKnowledge?id=123465')
}

// 按关键字查找知识
export const queryNode = node => {
  const form = new FormData()
  form.append('knowledgeKeyword', node.knowledgeKeyword)
  form.append('startPage', node.startPage)
  form.append('endPage', node.endPage)
  return http.post('/Knowledge/queryKnowledgeByKeyword', form)
}

// 插入知识
export const insertNode = node => {
  const form = new FormData()
  form.append('classId', node.classId)
  form.append('knowledgeName', node.knowledgeName)
  form.append('knowledgeContent', node.knowledgeContent)
  form.append('size', node.size)
  form.append('color', node.color)
  return http.post('/Knowledge/insertKnowledge', form, {
    headers: {
      'Content-Type': 'multipart/form-data',
      'code': window.sessionStorage.code,
      'token': window.sessionStorage.token
    }
  })
}

// 获取知识分类
export const getCategoies = () => {
  const form = new FormData()
  form.append('startPage', 0)
  form.append('endPage', 2000)
  return http.post('/Class/listClass', form)
}

// 按id删除知识
export const deleteNode = id => {
  const form = new FormData()
  form.append('id', id)
  return http.post('/Knowledge/deleKnowledge', form, {
    headers: {
      'Content-Type': 'multipart/form-data',
      'code': window.sessionStorage.code,
      'token': window.sessionStorage.token
    }
  })
}

// 更新知识信息
export const updateNode = node => {
  const form = new FormData()
  form.append('id', node.id)
  form.append('classId', node.classId)
  form.append('knowledgeName', node.knowledgeName)
  form.append('knowledgeContent', node.knowledgeContent)
  form.append('size', node.size)
  form.append('knowledgeTag', node.knowledgeTag)
  form.append('color', node.color)
  return http.post('/Knowledge/updateKnowledge', form, {
    headers: {
      'Content-Type': 'multipart/form-data',
      'code': window.sessionStorage.code,
      'token': window.sessionStorage.token
    }
  })
}

// 建立知识联系
export const insertRelative = node => {
  const form = new FormData()
  form.append('fatherId', node.fatherId)
  form.append('sonId', node.sonId)
  return http.post('/Relation/insertRelation', form, {
    headers: {
      'Content-Type': 'multipart/form-data',
      'code': window.sessionStorage.code,
      'token': window.sessionStorage.token
    }
  })
}
