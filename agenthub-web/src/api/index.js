import api from '@/utils/api'

/**
 * 文章相关API
 */
export const articleApi = {
  // 获取文章列表
  getList(params) {
    return api.get('/articles', { params })
  },

  // 搜索文章
  search(keyword, pageNum = 1, pageSize = 10) {
    return api.get('/articles/search', { params: { keyword, pageNum, pageSize } })
  },

  // 按分类查询
  getByCategory(category, pageNum = 1, pageSize = 10) {
    return api.get(`/articles/category/${category}`, { params: { pageNum, pageSize } })
  },

  // 按标签查询
  getByTag(tag, pageNum = 1, pageSize = 10) {
    return api.get(`/articles/tag/${tag}`, { params: { pageNum, pageSize } })
  },

  // 获取文章详情
  getDetail(id) {
    return api.get(`/articles/${id}`)
  },

  // 获取文章内容
  getContent(id) {
    return api.get(`/articles/${id}/content`)
  },

  // 创建文章
  create(data) {
    return api.post('/articles', data)
  },

  // 更新文章
  update(id, data) {
    return api.put(`/articles/${id}`, data)
  },

  // 删除文章
  delete(id) {
    return api.delete(`/articles/${id}`)
  },

  // 提交审核
  submitReview(id) {
    return api.post(`/articles/${id}/submit-review`)
  },

  // 获取作者的文章
  getByAuthor(authorId, pageNum = 1, pageSize = 10) {
    return api.get(`/articles/author/${authorId}`, { params: { pageNum, pageSize } })
  }
}

/**
 * 评论相关API
 */
export const commentApi = {
  // 获取文章评论
  getArticleComments(articleId, pageNum = 1, pageSize = 10) {
    return api.get(`/comments/article/${articleId}`, { params: { pageNum, pageSize } })
  },

  // 创建评论
  create(data) {
    return api.post('/comments', data)
  },

  // 删除评论
  delete(id) {
    return api.delete(`/comments/${id}`)
  },

  // 点赞评论
  like(id) {
    return api.post(`/comments/${id}/like`)
  },

  // 获取评论数
  getCount(articleId) {
    return api.get(`/comments/article/${articleId}/count`)
  }
}

/**
 * 点赞相关API
 */
export const likeApi = {
  // 点赞
  like(data) {
    return api.post('/likes', data)
  },

  // 取消点赞
  unlike(targetId, targetType) {
    return api.delete('/likes', { params: { targetId, targetType } })
  },

  // 获取点赞数
  getCount(targetId, targetType) {
    return api.get('/likes/count', { params: { targetId, targetType } })
  }
}

/**
 * Agent相关API
 */
export const agentApi = {
  // 获取所有Agent
  getAll() {
    return api.get('/discovery')
  },

  // 获取Agent详情
  getById(id) {
    return api.get(`/discovery/${id}`)
  }
}