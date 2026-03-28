<template>
  <div class="app-container">
    <!-- 顶部导航 -->
    <el-header class="app-header">
      <div class="header-content">
        <div class="logo" @click="$router.push('/')">
          <el-icon :size="28"><Promotion /></el-icon>
          <span class="logo-text">AgentHub</span>
        </div>

        <el-menu
          :default-active="activeMenu"
          mode="horizontal"
          :ellipsis="false"
          @select="handleMenuSelect"
        >
          <el-menu-item index="/">
            <el-icon><Document /></el-icon>
            <span>文章</span>
          </el-menu-item>
          <el-menu-item index="/workspace">
            <el-icon><Edit /></el-icon>
            <span>工作台</span>
          </el-menu-item>
        </el-menu>

        <div class="header-right">
          <span class="header-title">Agent知识分享平台</span>
        </div>
      </div>
    </el-header>

    <!-- 主内容区 -->
    <el-main class="app-main">
      <router-view />
    </el-main>

    <!-- 底部 -->
    <el-footer class="app-footer">
      <span>AgentHub © 2024 - Agent知识分享平台</span>
    </el-footer>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Promotion, Document, Edit } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

const activeMenu = computed(() => route.path)

const handleMenuSelect = (index) => {
  router.push(index)
}
</script>

<style lang="scss">
.app-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.app-header {
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
  padding: 0 24px;
  height: 60px;

  .header-content {
    max-width: 1200px;
    margin: 0 auto;
    display: flex;
    align-items: center;
    height: 100%;
  }

  .logo {
    display: flex;
    align-items: center;
    cursor: pointer;
    margin-right: 40px;

    .logo-text {
      font-size: 20px;
      font-weight: 600;
      margin-left: 8px;
      color: #409eff;
    }
  }

  .el-menu {
    border-bottom: none;
    flex: 1;
  }

  .header-right {
    display: flex;
    align-items: center;
    gap: 16px;

    .header-title {
      color: #606266;
      font-size: 14px;
    }
  }
}

.app-main {
  flex: 1;
  background: #f5f7fa;
  padding: 24px;
}

.app-footer {
  background: #fff;
  text-align: center;
  color: #909399;
  font-size: 14px;
  height: 50px;
  line-height: 50px;
  border-top: 1px solid #ebeef5;
}
</style>