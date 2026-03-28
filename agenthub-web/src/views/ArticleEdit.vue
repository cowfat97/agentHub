<template>
  <div class="article-edit-page">
    <div class="page-container">
      <div class="edit-header">
        <h2>{{ isEdit ? '编辑文章' : '发布新文章' }}</h2>
        <el-button @click="goBack">返回</el-button>
      </div>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="80px"
        class="edit-form"
      >
        <el-form-item label="标题" prop="title">
          <el-input
            v-model="form.title"
            placeholder="请输入文章标题"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="分类" prop="category">
          <el-select v-model="form.category" placeholder="请选择分类">
            <el-option
              v-for="cat in categories"
              :key="cat.code"
              :label="cat.name"
              :value="cat.code"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="内容" prop="content">
          <div class="editor-container">
            <div class="editor-toolbar">
              <el-button-group>
                <el-button size="small" @click="insertMarkdown('**', '**')">加粗</el-button>
                <el-button size="small" @click="insertMarkdown('*', '*')">斜体</el-button>
                <el-button size="small" @click="insertMarkdown('### ', '')">标题</el-button>
                <el-button size="small" @click="insertMarkdown('- ', '')">列表</el-button>
                <el-button size="small" @click="insertMarkdown('`', '`')">代码</el-button>
                <el-button size="small" @click="insertMarkdown('> ', '')">引用</el-button>
                <el-button size="small" @click="insertMarkdown('[链接](', ')')">链接</el-button>
              </el-button-group>
              <el-button
                size="small"
                :icon="View"
                @click="showPreview = !showPreview"
              >
                {{ showPreview ? '隐藏预览' : '预览' }}
              </el-button>
            </div>
            <div class="editor-main">
              <el-input
                ref="editorRef"
                v-model="form.content"
                type="textarea"
                :rows="20"
                placeholder="请输入文章内容，支持Markdown格式..."
                @input="handleContentChange"
              />
              <div v-if="showPreview" class="preview-panel">
                <div class="markdown-body" v-html="renderedContent"></div>
              </div>
            </div>
          </div>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="saveDraft" :loading="saving">
            保存草稿
          </el-button>
          <el-button type="success" @click="saveAndSubmit" :loading="saving">
            保存并提交审核
          </el-button>
          <el-button @click="goBack">取消</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { View } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { marked } from 'marked'
import { articleApi } from '@/api'

const route = useRoute()
const router = useRouter()

const formRef = ref()
const editorRef = ref()
const form = ref({
  title: '',
  category: '',
  content: ''
})

const rules = {
  title: [
    { required: true, message: '请输入文章标题', trigger: 'blur' },
    { max: 200, message: '标题最多200字符', trigger: 'blur' }
  ],
  category: [
    { required: true, message: '请选择分类', trigger: 'change' }
  ],
  content: [
    { required: true, message: '请输入文章内容', trigger: 'blur' }
  ]
}

const categories = [
  { code: 'data-analysis', name: '数据分析' },
  { code: 'code-generation', name: '代码生成' },
  { code: 'task-planning', name: '任务规划' },
  { code: 'knowledge-sharing', name: '知识分享' },
  { code: 'best-practices', name: '最佳实践' },
  { code: 'other', name: '其他' }
]

const saving = ref(false)
const showPreview = ref(false)
const articleId = ref(null)

const isEdit = computed(() => !!articleId.value)

const renderedContent = computed(() => {
  if (!form.value.content) return ''
  return marked(form.value.content)
})

onMounted(async () => {
  articleId.value = route.params.id
  if (articleId.value) {
    await fetchArticle()
  }
})

const fetchArticle = async () => {
  try {
    const [article, content] = await Promise.all([
      articleApi.getDetail(articleId.value),
      articleApi.getContent(articleId.value)
    ])
    form.value.title = article.title
    form.value.category = article.category
    form.value.content = content
  } catch (error) {
    ElMessage.error('获取文章失败')
    router.push('/workspace')
  }
}

const handleContentChange = () => {
  // 内容变化时可以自动保存草稿
}

const insertMarkdown = (prefix, suffix) => {
  const textarea = editorRef.value?.$el?.querySelector('textarea')
  if (!textarea) return

  const start = textarea.selectionStart
  const end = textarea.selectionEnd
  const selectedText = form.value.content.substring(start, end)

  const before = form.value.content.substring(0, start)
  const after = form.value.content.substring(end)

  form.value.content = before + prefix + selectedText + suffix + after

  // 恢复焦点和选中
  setTimeout(() => {
    textarea.focus()
    textarea.setSelectionRange(start + prefix.length, end + prefix.length)
  }, 0)
}

const saveDraft = async () => {
  await saveArticle(false)
}

const saveAndSubmit = async () => {
  await saveArticle(true)
}

const saveArticle = async (submitForReview) => {
  try {
    await formRef.value.validate()
  } catch {
    return
  }

  saving.value = true
  try {
    const data = {
      title: form.value.title,
      content: form.value.content,
      category: form.value.category,
      submitForReview
    }

    if (articleId.value) {
      await articleApi.update(articleId.value, {
        ...data,
        id: articleId.value
      })
    } else {
      await articleApi.create(data)
    }

    ElMessage.success(submitForReview ? '已保存并提交审核' : '草稿已保存')
    router.push('/workspace')
  } catch (error) {
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

const goBack = () => {
  router.push('/workspace')
}
</script>

<style lang="scss" scoped>
.article-edit-page {
  .page-container {
    max-width: 1000px;
    margin: 0 auto;
  }

  .edit-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    background: #fff;
    padding: 16px 24px;
    border-radius: 8px;
    margin-bottom: 20px;
  }

  .edit-form {
    background: #fff;
    padding: 24px;
    border-radius: 8px;
  }

  .editor-container {
    width: 100%;

    .editor-toolbar {
      display: flex;
      justify-content: space-between;
      margin-bottom: 12px;
      padding: 8px;
      background: #f5f7fa;
      border-radius: 4px;
    }

    .editor-main {
      display: flex;
      gap: 20px;

      .el-textarea {
        flex: 1;
      }

      .preview-panel {
        flex: 1;
        border: 1px solid #dcdfe6;
        border-radius: 4px;
        padding: 16px;
        max-height: 500px;
        overflow-y: auto;
        background: #fafafa;
      }
    }
  }
}
</style>