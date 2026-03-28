#!/usr/bin/env bash

# ============================================================
# AgentHub Git Worktree 管理脚本
#
# 用法: ./worktree.sh <command> [args]
#
# 命令:
#   init                        初始化 worktree 环境
#   create <branch> [base]      创建新分支和 worktree
#   remove <branch>             删除 worktree 和分支
#   list                        列出所有 worktree
#   status                      查看各 worktree 状态
#   merge <branch>              合并 worktree 分支到当前分支
#   sync                        同步所有 worktree
#   module <name>               快速创建模块 worktree
#   help                        显示帮助
# ============================================================

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# 配置
WORKTREES_DIR="../worktrees"
MAIN_BRANCH="main"
DEVELOP_BRANCH="develop"

# 模块列表 (模块名:分支名)
MODULES="agent:feat/agent article:feat/article recommendation:feat/recommendation gateway:feat/gateway common:feat/common"

# ============================================================
# 工具函数
# ============================================================

info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

error() {
    echo -e "${RED}[ERROR]${NC} $1"
    exit 1
}

get_repo_root() {
    git rev-parse --show-toplevel
}

get_current_branch() {
    git branch --show-current
}

branch_exists() {
    git show-ref --verify --quiet "refs/heads/$1"
}

remote_branch_exists() {
    git show-ref --verify --quiet "refs/remotes/origin/$1"
}

get_module_branch() {
    local module="$1"
    echo "$MODULES" | tr ' ' '\n' | grep "^$module:" | cut -d: -f2
}

list_modules() {
    echo "$MODULES" | tr ' ' '\n' | while read line; do
        local mod=$(echo "$line" | cut -d: -f1)
        local branch=$(echo "$line" | cut -d: -f2)
        echo "  - $mod → $branch"
    done
}

# ============================================================
# 命令实现
# ============================================================

cmd_init() {
    info "初始化 Worktree 环境..."

    # 创建 worktrees 目录
    if [ ! -d "$WORKTREES_DIR" ]; then
        mkdir -p "$WORKTREES_DIR"
        info "创建 worktrees 目录: $WORKTREES_DIR"
    fi

    # 确保 develop 分支存在
    if ! branch_exists "$DEVELOP_BRANCH"; then
        if remote_branch_exists "$DEVELOP_BRANCH"; then
            info "从远程拉取 develop 分支..."
            git fetch origin "$DEVELOP_BRANCH"
            git checkout -b "$DEVELOP_BRANCH" "origin/$DEVELOP_BRANCH"
            git checkout "$MAIN_BRANCH"
        else
            info "创建 develop 分支..."
            git checkout -b "$DEVELOP_BRANCH"
            git push -u origin "$DEVELOP_BRANCH"
            git checkout "$MAIN_BRANCH"
        fi
    fi

    info "初始化完成!"
    echo ""
    echo "可用的模块分支:"
    list_modules
    echo ""
    echo "快速开始:"
    echo "  ./worktree.sh module article"
}

cmd_create() {
    local branch="$1"
    local base="${2:-$DEVELOP_BRANCH}"

    if [ -z "$branch" ]; then
        error "请指定分支名称，例如: ./worktree.sh create feat/article-review"
    fi

    # 标准化分支名
    local safe_name=$(echo "$branch" | sed 's/\//-/g')
    local worktree_path="$WORKTREES_DIR/$safe_name"

    info "创建 worktree: $safe_name"
    echo "  分支: $branch"
    echo "  基于分支: $base"
    echo "  目录: $worktree_path"
    echo ""

    # 检查 worktree 是否已存在
    if [ -d "$worktree_path" ]; then
        error "Worktree 目录已存在: $worktree_path"
    fi

    # 创建分支（如果不存在）
    if branch_exists "$branch"; then
        warn "分支已存在: $branch，将使用现有分支"
    else
        # 确保基分支是最新的
        if remote_branch_exists "$base"; then
            git fetch origin "$base"
        fi

        # 创建新分支
        git branch "$branch" "origin/$base" 2>/dev/null || git branch "$branch" "$base"
        info "创建分支: $branch"
    fi

    # 创建 worktree
    git worktree add "$worktree_path" "$branch"
    info "创建 worktree 成功!"

    # 在 worktree 中设置上游分支
    cd "$worktree_path"
    git push -u origin "$branch" 2>/dev/null || true
    cd - > /dev/null

    echo ""
    echo "开始开发:"
    echo "  cd $worktree_path"
    echo "  # 打开 IDE: idea . 或 code ."
}

cmd_remove() {
    local branch="$1"

    if [ -z "$branch" ]; then
        error "请指定分支名称"
    fi

    local safe_name=$(echo "$branch" | sed 's/\//-/g')
    local worktree_path="$WORKTREES_DIR/$safe_name"

    if [ ! -d "$worktree_path" ]; then
        error "Worktree 不存在: $worktree_path"
    fi

    warn "将删除:"
    echo "  Worktree: $worktree_path"
    echo "  分支: $branch"
    echo ""
    read -p "确认删除？(y/N): " confirm

    if [ "$confirm" = "y" ] || [ "$confirm" = "Y" ]; then
        # 移除 worktree
        git worktree remove "$worktree_path" --force

        # 删除本地分支
        if branch_exists "$branch"; then
            git branch -D "$branch" 2>/dev/null || true
        fi

        # 删除远程分支
        if remote_branch_exists "$branch"; then
            read -p "是否同时删除远程分支？(y/N): " del_remote
            if [ "$del_remote" = "y" ] || [ "$del_remote" = "Y" ]; then
                git push origin --delete "$branch"
                info "删除远程分支: $branch"
            fi
        fi

        info "删除完成!"
    else
        info "已取消"
    fi
}

cmd_list() {
    info "Worktree 列表:"
    echo ""
    git worktree list
    echo ""

    # 显示各 worktree 的状态
    local worktrees=$(git worktree list | tail -n +2 | awk '{print $1}')
    for wt in $worktrees; do
        if [ -d "$wt" ]; then
            local branch=$(cd "$wt" && git branch --show-current)
            local status=$(cd "$wt" && git status --short | head -1)
            local status_text="干净"
            if [ -n "$status" ]; then
                status_text="有修改"
            fi
            echo "  $(basename $wt) [$branch] - $status_text"
        fi
    done
}

cmd_status() {
    info "各 Worktree 状态:"
    echo ""

    local worktrees=$(git worktree list | tail -n +2 | awk '{print $1}')

    for wt in $worktrees; do
        if [ -d "$wt" ]; then
            local branch=$(cd "$wt" && git branch --show-current)
            local behind=$(cd "$wt" && git rev-list --count HEAD..origin/$branch 2>/dev/null || echo "0")
            local ahead=$(cd "$wt" && git rev-list --count origin/$branch..HEAD 2>/dev/null || echo "0")

            echo -e "${BLUE}$(basename $wt)${NC} [$branch]"
            echo "  落后远程: $behind 个提交"
            echo "  领先远程: $ahead 个提交"
            echo ""
        fi
    done
}

cmd_merge() {
    local branch="$1"

    if [ -z "$branch" ]; then
        error "请指定要合并的分支"
    fi

    local current_branch=$(get_current_branch)

    info "合并 $branch 到 $current_branch..."

    # 拉取最新代码
    git pull origin "$current_branch"

    # 合并
    git merge "$branch" --no-ff -m "Merge branch '$branch' into $current_branch"

    if [ $? -eq 0 ]; then
        info "合并成功!"
        echo ""
        echo "推送到远程:"
        echo "  git push origin $current_branch"
    else
        error "合并失败，请解决冲突后重新提交"
    fi
}

cmd_sync() {
    info "同步所有 worktree..."

    local worktrees=$(git worktree list | tail -n +2 | awk '{print $1}')

    for wt in $worktrees; do
        if [ -d "$wt" ]; then
            local branch=$(cd "$wt" && git branch --show-current)
            info "同步: $(basename $wt) [$branch]"
            cd "$wt"
            git fetch origin
            if remote_branch_exists "$branch"; then
                git pull origin "$branch" 2>/dev/null || true
            fi
            cd - > /dev/null
        fi
    done

    info "同步完成!"
}

cmd_module() {
    local module="$1"

    if [ -z "$module" ]; then
        echo "可用模块:"
        list_modules
        return
    fi

    local branch=$(get_module_branch "$module")
    if [ -z "$branch" ]; then
        error "未知模块: $module"
    fi

    cmd_create "$branch" "$DEVELOP_BRANCH"
}

cmd_help() {
    echo "AgentHub Git Worktree 管理脚本"
    echo ""
    echo "用法: ./worktree.sh <command> [args]"
    echo ""
    echo "命令:"
    echo "  init                      初始化 worktree 环境"
    echo "  create <branch> [base]    创建新分支和 worktree"
    echo "  remove <branch>           删除 worktree 和分支"
    echo "  list                      列出所有 worktree"
    echo "  status                    查看各 worktree 状态"
    echo "  merge <branch>            合并分支到当前分支"
    echo "  sync                      同步所有 worktree"
    echo "  module <name>             快速创建模块 worktree"
    echo "  help                      显示帮助"
    echo ""
    echo "模块列表:"
    list_modules
    echo ""
    echo "示例:"
    echo "  # 初始化"
    echo "  ./worktree.sh init"
    echo ""
    echo "  # 创建文章模块 worktree"
    echo "  ./worktree.sh module article"
    echo ""
    echo "  # 创建功能分支"
    echo "  ./worktree.sh create feat/article-ai-review develop"
    echo ""
    echo "  # 查看所有 worktree"
    echo "  ./worktree.sh list"
    echo ""
    echo "  # 删除 worktree"
    echo "  ./worktree.sh remove feat/article-ai-review"
}

# ============================================================
# 主入口
# ============================================================

main() {
    cd "$(get_repo_root)"

    local command="${1:-help}"
    shift 2>/dev/null || true

    case $command in
        init)
            cmd_init "$@"
            ;;
        create)
            cmd_create "$@"
            ;;
        remove|rm)
            cmd_remove "$@"
            ;;
        list|ls)
            cmd_list "$@"
            ;;
        status|st)
            cmd_status "$@"
            ;;
        merge)
            cmd_merge "$@"
            ;;
        sync)
            cmd_sync "$@"
            ;;
        module|mod)
            cmd_module "$@"
            ;;
        help|--help|-h)
            cmd_help "$@"
            ;;
        *)
            error "未知命令: $command"
            cmd_help
            exit 1
            ;;
    esac
}

main "$@"