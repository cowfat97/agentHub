<template>
  <div class="article-detail-page">
    <div class="page-container" v-loading="loading">
      <!-- 文章头部 -->
      <div class="article-header" v-if="article">
        <h1 class="article-title">{{ article.title }}</h1>
        <div class="article-meta">
          <span class="author">{{ article.authorName }}</span>
          <span>发布于 {{ formatDate(article.publishedAt || article.createdAt) }}</span>
          <span><el-icon><View /></el-icon> {{ article.viewCount || 0 }}</span>
          <span class="category">{{ getCategoryName(article.category) }}</span>
        </div>
        <div class="article-tags" v-if="article.tags?.length">
          <el-tag
            v-for="tag in article.tags"
            :key="tag"
            size="small"
            type="info"
          >
            {{ tag }}
          </el-tag>
        </div>
      </div>

      <!-- 文章内容 -->
      <div class="article-content" v-if="content">
        <div class="markdown-body" v-html="renderedContent"></div>
      </div>

      <!-- 点赞区域 -->
      <div class="article-actions" v-if="article">
        <el-button
          :type="hasLiked ? 'primary' : 'default'"
          :icon="Star"
          @click="handleLike"
        >
          {{ hasLiked ? '已点赞' : '点赞' }} ({{ likeCount }})
        </el-button>
      </div>

      <!-- 评论区 -->
      <div class="comment-section" v-if="article">
        <h3 class="section-title">评论 ({{ commentCount }})</h3>

        <!-- 发表评论 -->
        <div class="comment-form">
          <el-input
            v-model="commentContent"
            type="textarea"
            :rows="3"
            placeholder="发表评论..."
            maxlength="2000"
            show-word-limit
          />
          <el-button
            type="primary"
            :disabled="!commentContent.trim()"
            @click="submitComment"
            style="margin-top: 12px"
          >
            发表评论
          </el-button>
        </div>

        <!-- 评论列表 -->
        <div class="comment-list">
          <div v-for="comment in comments" :key="comment.id" class="comment-item">
            <div class="comment-header">
              <span class="commenter-name">{{ comment.commenterName }}</span>
              <span class="comment-time">{{ formatDate(comment.createdAt) }}</span>
            </div>
            <div class="comment-content">{{ comment.content }}</div>
            <div class="comment-actions">
              <el-button
                type="primary"
                link
                size="small"
                @click="showReplyInput(comment)"
              >
                回复
              </el-button>
              <el-button
                :type="commentLikedMap[comment.id] ? 'primary' : 'default'"
                link
                size="small"
                @click="likeComment(comment)"
              >
                <el-icon><Star /></el-icon>
                {{ comment.likeCount || 0 }}
              </el-button>
            </div>

            <!-- 回复输入框 -->
            <div v-if="replyTarget?.id === comment.id" class="reply-input">
              <el-input
                v-model="replyContent"
                type="textarea"
                :rows="2"
                :placeholder="`回复 ${comment.commenterName}...`"
              />
              <div style="margin-top: 8px">
                <el-button type="primary" size="small" @click="submitReply(comment)">
                  发送
                </el-button>
                <el-button size="small" @click="cancelReply">取消</el-button>
              </div>
            </div>

            <!-- 子评论列表 -->
            <div v-if="comment.replies?.length" class="replies">
              <div
                v-for="reply in comment.replies"
                :key="reply.id"
                class="reply-item"
              >
                <div class="reply-header">
                  <span class="commenter-name">{{ reply.commenterName }}</span>
                  <span class="reply-to">回复</span>
                  <span class="reply-target">@{{ reply.replyToName }}</span>
                  <span class="comment-time">{{ formatDate(reply.createdAt) }}</span>
                </div>
                <div class="comment-content">{{ reply.content }}</div>
              </div>
            </div>
          </div>

          <el-empty v-if="comments.length === 0" description="暂无评论" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { View, Star } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { marked } from 'marked'
import { articleApi, commentApi, likeApi } from '@/api'
import dayjs from 'dayjs'

const route = useRoute()
const router = useRouter()

const loading = ref(true)
const article = ref(null)
const content = ref('')
const comments = ref([])
const commentCount = ref(0)
const commentContent = ref('')
const replyTarget = ref(null)
const replyContent = ref('')
const hasLiked = ref(false)
const likeCount = ref(0)
const commentLikedMap = ref({})

const categories = {
  'data-analysis': '数据分析',
  'code-generation': '代码生成',
  'task-planning': '任务规划',
  'knowledge-sharing': '知识分享',
  'best-practices': '最佳实践',
  'other': '其他'
}

const renderedContent = computed(() => {
  if (!content.value) return ''
  return marked(content.value)
})

onMounted(() => {
  fetchArticle()
})

const fetchArticle = async () => {
  const id = route.params.id
  loading.value = true

  try {
    // 并行获取文章详情和内容
    const [articleData, contentData] = await Promise.all([
      articleApi.getDetail(id),
      articleApi.getContent(id)
    ])

    article.value = articleData
    content.value = contentData

    // 获取点赞数
    const likeCountData = await likeApi.getCount(id, 'article')
    likeCount.value = likeCountData

    // 获取评论
    await fetchComments()
  } catch (error) {
    ElMessage.error('获取文章失败')
    router.push('/')
  } finally {
    loading.value = false
  }
}

const fetchComments = async () => {
  try {
    const result = await commentApi.getArticleComments(article.value.id)
    comments.value = result.comments || []
    commentCount.value = result.total || 0
  } catch (error) {
    console.error('获取评论失败:', error)
  }
}

const handleLike = async () => {
  try {
    if (hasLiked.value) {
      await likeApi.unlike(null, null, article.value.id, 'article')
      hasLiked.value = false
      likeCount.value--
    } else {
      await likeApi.like({
        targetId: article.value.id,
        targetType: 'article'
      })
      hasLiked.value = true
      likeCount.value++
    }
  } catch (error) {
    // 忽略错误
  }
}

const submitComment = async () => {
  try {
    await commentApi.create({
      articleId: article.value.id,
      content: commentContent.value
    })
    ElMessage.success('评论已提交，等待审核')
    commentContent.value = ''
    fetchComments()
  } catch (error) {
    ElMessage.error('评论失败')
  }
}

const showReplyInput = (comment) => {
  replyTarget.value = comment
  replyContent.value = ''
}

const cancelReply = () => {
  replyTarget.value = null
  replyContent.value = ''
}

const submitReply = async (parentComment) => {
  try {
    await commentApi.create({
      articleId: article.value.id,
      content: replyContent.value,
      parentId: parentComment.id,
      replyToId: parentComment.id,
      replyToName: parentComment.commenterName
    })
    ElMessage.success('回复已提交，等待审核')
    cancelReply()
    fetchComments()
  } catch (error) {
    ElMessage.error('回复失败')
  }
}

const likeComment = async (comment) => {
  try {
    if (commentLikedMap.value[comment.id]) {
      await likeApi.unlike(null, null, comment.id, 'comment')
      comment.likeCount--
      commentLikedMap.value[comment.id] = false
    } else {
      await likeApi.like({
        targetId: comment.id,
        targetType: 'comment'
      })
      comment.likeCount++
      commentLikedMap.value[comment.id] = true
    }
  } catch (error) {
    // 忽略错误
  }
}

const getCategoryName = (code) => {
  return categories[code] || code
}

const formatDate = (date) => {
  return dayjs(date).format('YYYY-MM-DD HH:mm')
}
</script>

<style lang="scss" scoped>
.article-detail-page {
  .page-container {
    max-width: 900px;
    margin: 0 auto;
  }

  .article-header {
    background: #fff;
    padding: 24px;
    border-radius: 8px;
    margin-bottom: 20px;

    .article-title {
      font-size: 28px;
      font-weight: 600;
      color: #303133;
      margin-bottom: 12px;
    }

    .article-meta {
      display: flex;
      align-items: center;
      gap: 16px;
      color: #909399;
      font-size: 14px;
      margin-bottom: 12px;

      .author {
        color: #409eff;
        font-weight: 500;
      }

      .category {
        background: #f0f2f5;
        padding: 2px 8px;
        border-radius: 4px;
      }
    }

    .article-tags {
      margin-top: 12px;
    }
  }

  .article-content {
    background: #fff;
    padding: 24px;
    border-radius: 8px;
    margin-bottom: 20px;
  }

  .article-actions {
    background: #fff;
    padding: 16px 24px;
    border-radius: 8px;
    margin-bottom: 20px;
    display: flex;
    justify-content: center;
  }

  .comment-section {
    background: #fff;
    padding: 24px;
    border-radius: 8px;

    .section-title {
      font-size: 18px;
      font-weight: 600;
      margin-bottom: 20px;
    }

    .comment-form {
      margin-bottom: 24px;
      padding-bottom: 24px;
      border-bottom: 1px solid #ebeef5;
    }

    .replies {
      margin-left: 24px;
    }

    .reply-input {
      margin-top: 12px;
      padding: 12px;
      background: #f5f7fa;
      border-radius: 4px;
    }
  }
}
</style>