<template>
  <div class="workspace-page">
    <div class="page-container">
      <div class="workspace-header">
        <h2>文章工作台</h2>
        <p class="workspace-desc">
          管理您的文章内容
        </p>
      </div>

      <!-- 操作按钮 -->
      <div class="action-bar">
        <el-button type="primary" :icon="Plus" @click="createArticle">
          发布新文章
        </el-button>
      </div>

      <!-- 文章状态筛选 -->
      <el-tabs v-model="activeStatus" @tab-change="handleTabChange">
        <el-tab-pane label="全部" name="all" />
        <el-tab-pane label="草稿" name="draft" />
        <el-tab-pane label="待审核" name="pending-review" />
        <el-tab-pane label="已发布" name="published" />
        <el-tab-pane label="审核未通过" name="review-failed" />
      </el-tabs>

      <!-- 文章列表 -->
      <div class="article-list" v-loading="loading">
        <el-table :data="articles" style="width: 100%">
          <el-table-column prop="title" label="标题" min-width="200">
            <template #default="{ row }">
              <span class="article-title-link" @click="viewArticle(row)">
                {{ row.title }}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="category" label="分类" width="120">
            <template #default="{ row }">
              {{ getCategoryName(row.category) }}
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)" size="small">
                {{ getStatusName(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="viewCount" label="浏览" width="80" />
          <el-table-column prop="likeCount" label="点赞" width="80" />
          <el-table-column prop="createdAt" label="创建时间" width="160">
            <template #default="{ row }">
              {{ formatDate(row.createdAt) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button
                v-if="canEdit(row)"
                type="primary"
                link
                size="small"
                @click="editArticle(row)"
              >
                编辑
              </el-button>
              <el-button
                v-if="canSubmitReview(row)"
                type="primary"
                link
                size="small"
                @click="submitReview(row)"
              >
                提交审核
              </el-button>
              <el-button
                v-if="canDelete(row)"
                type="danger"
                link
                size="small"
                @click="deleteArticle(row)"
              >
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <el-empty v-if="!loading && articles.length === 0" description="暂无文章" />
      </div>

      <!-- 分页 -->
      <div class="pagination" v-if="total > 0">
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @size-change="fetchArticles"
          @current-change="fetchArticles"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { articleApi } from '@/api'
import dayjs from 'dayjs'

const router = useRouter()

const loading = ref(false)
const articles = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const activeStatus = ref('all')

const categories = {
  'data-analysis': '数据分析',
  'code-generation': '代码生成',
  'task-planning': '任务规划',
  'knowledge-sharing': '知识分享',
  'best-practices': '最佳实践',
  'other': '其他'
}

const statusMap = {
  'draft': { name: '草稿', type: 'info' },
  'pending-review': { name: '待审核', type: 'warning' },
  'review-failed': { name: '审核未通过', type: 'danger' },
  'published': { name: '已发布', type: 'success' },
  'archived': { name: '已归档', type: '' }
}

onMounted(() => {
  fetchArticles()
})

const fetchArticles = async () => {
  loading.value = true
  try {
    const result = await articleApi.getList({ pageNum: pageNum.value, pageSize: pageSize.value })

    let list = result.articles || []
    if (activeStatus.value !== 'all') {
      list = list.filter(a => a.status === activeStatus.value)
    }
    articles.value = list
    total.value = result.total || 0
  } catch (error) {
    ElMessage.error('获取文章列表失败')
  } finally {
    loading.value = false
  }
}

const handleTabChange = () => {
  pageNum.value = 1
  fetchArticles()
}

const createArticle = () => {
  router.push('/workspace/article/create')
}

const viewArticle = (article) => {
  if (article.status === 'published') {
    router.push(`/article/${article.id}`)
  } else {
    ElMessage.info('该文章尚未发布')
  }
}

const editArticle = (article) => {
  router.push(`/workspace/article/edit/${article.id}`)
}

const submitReview = async (article) => {
  try {
    await ElMessageBox.confirm('确认提交审核？提交后将进入审核队列。', '提示', {
      type: 'warning'
    })
    await articleApi.submitReview(article.id)
    ElMessage.success('已提交审核')
    fetchArticles()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('提交失败')
    }
  }
}

const deleteArticle = async (article) => {
  try {
    await ElMessageBox.confirm('确认删除该文章？此操作不可恢复。', '警告', {
      type: 'warning'
    })
    await articleApi.delete(article.id)
    ElMessage.success('删除成功')
    fetchArticles()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const canEdit = (article) => {
  return ['draft', 'review-failed'].includes(article.status)
}

const canSubmitReview = (article) => {
  return article.status === 'draft'
}

const canDelete = (article) => {
  return ['draft', 'review-failed', 'published'].includes(article.status)
}

const getCategoryName = (code) => categories[code] || code

const getStatusName = (status) => statusMap[status]?.name || status

const getStatusType = (status) => statusMap[status]?.type || ''

const formatDate = (date) => dayjs(date).format('YYYY-MM-DD HH:mm')
</script>

<style lang="scss" scoped>
.workspace-page {
  .page-container {
    max-width: 1200px;
    margin: 0 auto;
  }

  .workspace-header {
    background: #fff;
    padding: 24px;
    border-radius: 8px;
    margin-bottom: 20px;

    h2 {
      margin-bottom: 8px;
    }

    .workspace-desc {
      color: #606266;
    }
  }

  .action-bar {
    margin-bottom: 16px;
  }

  .article-list {
    background: #fff;
    border-radius: 8px;
    padding: 16px;
    min-height: 400px;
  }

  .article-title-link {
    color: #409eff;
    cursor: pointer;

    &:hover {
      text-decoration: underline;
    }
  }

  .pagination {
    margin-top: 20px;
    display: flex;
    justify-content: center;
    background: #fff;
    padding: 16px;
    border-radius: 8px;
  }
}
</style>