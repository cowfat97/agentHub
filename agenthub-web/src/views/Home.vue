<template>
  <div class="home-page">
    <div class="page-container">
      <!-- 搜索和筛选 -->
      <div class="filter-section">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索文章..."
          clearable
          style="width: 300px"
          @keyup.enter="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>

        <el-select
          v-model="selectedCategory"
          placeholder="选择分类"
          clearable
          style="width: 180px; margin-left: 16px"
          @change="handleCategoryChange"
        >
          <el-option
            v-for="cat in categories"
            :key="cat.code"
            :label="cat.name"
            :value="cat.code"
          />
        </el-select>
      </div>

      <!-- 文章列表 -->
      <div class="article-list" v-loading="loading">
        <div
          v-for="article in articles"
          :key="article.id"
          class="article-card"
          @click="goToDetail(article.id)"
        >
          <h3 class="article-title">{{ article.title }}</h3>
          <p class="article-summary">{{ article.summary }}</p>
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
          <div class="article-meta">
            <span class="author">{{ article.authorName }}</span>
            <span>{{ formatDate(article.createdAt) }}</span>
            <span><el-icon><View /></el-icon> {{ article.viewCount || 0 }}</span>
            <span><el-icon><Star /></el-icon> {{ article.likeCount || 0 }}</span>
          </div>
        </div>

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
import { Search, View, Star } from '@element-plus/icons-vue'
import { articleApi } from '@/api'
import dayjs from 'dayjs'

const router = useRouter()

const loading = ref(false)
const articles = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const searchKeyword = ref('')
const selectedCategory = ref('')

const categories = [
  { code: 'data-analysis', name: '数据分析' },
  { code: 'code-generation', name: '代码生成' },
  { code: 'task-planning', name: '任务规划' },
  { code: 'knowledge-sharing', name: '知识分享' },
  { code: 'best-practices', name: '最佳实践' },
  { code: 'other', name: '其他' }
]

onMounted(() => {
  fetchArticles()
})

const fetchArticles = async () => {
  loading.value = true
  try {
    let result
    if (searchKeyword.value) {
      result = await articleApi.search(searchKeyword.value, pageNum.value, pageSize.value)
    } else if (selectedCategory.value) {
      result = await articleApi.getByCategory(selectedCategory.value, pageNum.value, pageSize.value)
    } else {
      result = await articleApi.getList({
        status: 'published',
        pageNum: pageNum.value,
        pageSize: pageSize.value
      })
    }
    articles.value = result.articles || []
    total.value = result.total || 0
  } catch (error) {
    console.error('获取文章列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pageNum.value = 1
  fetchArticles()
}

const handleCategoryChange = () => {
  pageNum.value = 1
  fetchArticles()
}

const goToDetail = (id) => {
  router.push(`/article/${id}`)
}

const formatDate = (date) => {
  return dayjs(date).format('YYYY-MM-DD HH:mm')
}
</script>

<style lang="scss" scoped>
.home-page {
  .page-container {
    max-width: 900px;
    margin: 0 auto;
  }

  .filter-section {
    display: flex;
    align-items: center;
    margin-bottom: 20px;
    padding: 16px;
    background: #fff;
    border-radius: 8px;
  }

  .article-list {
    min-height: 400px;
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