#!/bin/bash

# AgentHub 部署脚本
# 用法: ./deploy.sh [command]
# 命令: build | up | down | logs | restart | clean

set -e

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 打印信息
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

# 检查依赖
check_dependencies() {
    info "检查依赖..."

    if ! command -v docker &> /dev/null; then
        error "Docker 未安装，请先安装 Docker"
    fi

    if ! command -v docker-compose &> /dev/null && ! docker compose version &> /dev/null; then
        error "Docker Compose 未安装，请先安装 Docker Compose"
    fi

    info "依赖检查通过"
}

# 构建应用
build() {
    info "构建后端应用..."

    # 检查 Maven
    if ! command -v mvn &> /dev/null; then
        error "Maven 未安装，请先安装 Maven"
    fi

    # Maven 构建
    mvn clean package -DskipTests

    if [ $? -ne 0 ]; then
        error "后端构建失败"
    fi

    info "后端构建完成"

    # 构建前端
    info "构建前端应用..."

    if [ -d "agenthub-web" ]; then
        cd agenthub-web

        if [ ! -d "node_modules" ]; then
            info "安装前端依赖..."
            npm install
        fi

        npm run build

        if [ $? -ne 0 ]; then
            error "前端构建失败"
        fi

        cd ..
        info "前端构建完成"
    else
        warn "前端目录不存在，跳过前端构建"
    fi

    # 构建 Docker 镜像
    info "构建 Docker 镜像..."
    docker-compose build

    info "构建完成"
}

# 启动服务
up() {
    info "启动服务..."

    # 检查 .env 文件
    if [ ! -f ".env" ]; then
        warn ".env 文件不存在，使用默认配置"
        cp .env.example .env
        warn "请修改 .env 文件中的敏感配置"
    fi

    docker-compose up -d

    info "服务启动完成"
    info "访问地址:"
    echo "  - 前端: http://localhost:${WEB_PORT:-80}"
    echo "  - 后端: http://localhost:${APP_PORT:-8080}"
    echo "  - Nacos: http://localhost:${NACOS_PORT:-8848}/nacos"
    echo "  - MinIO: http://localhost:${MINIO_CONSOLE_PORT:-9001}"
}

# 停止服务
down() {
    info "停止服务..."
    docker-compose down
    info "服务已停止"
}

# 查看日志
logs() {
    local service=$1
    if [ -z "$service" ]; then
        docker-compose logs -f
    else
        docker-compose logs -f "$service"
    fi
}

# 重启服务
restart() {
    info "重启服务..."
    docker-compose restart
    info "服务已重启"
}

# 清理
clean() {
    warn "这将删除所有容器、镜像和数据卷！"
    read -p "确认继续？(y/N): " confirm

    if [ "$confirm" = "y" ] || [ "$confirm" = "Y" ]; then
        info "清理中..."
        docker-compose down -v --rmi all
        mvn clean
        rm -rf agenthub-web/dist agenthub-web/node_modules
        info "清理完成"
    else
        info "已取消"
    fi
}

# 帮助信息
help() {
    echo "AgentHub 部署脚本"
    echo ""
    echo "用法: $0 [command]"
    echo ""
    echo "命令:"
    echo "  build    构建 Docker 镜像"
    echo "  up       启动所有服务"
    echo "  down     停止所有服务"
    echo "  logs     查看日志 (可选指定服务名)"
    echo "  restart  重启所有服务"
    echo "  clean    清理所有容器、镜像和数据"
    echo "  help     显示帮助信息"
}

# 主函数
main() {
    cd "$(dirname "$0")"

    local command=${1:-help}

    case $command in
        build)
            check_dependencies
            build
            ;;
        up)
            check_dependencies
            up
            ;;
        down)
            down
            ;;
        logs)
            logs $2
            ;;
        restart)
            restart
            ;;
        clean)
            clean
            ;;
        help|--help|-h)
            help
            ;;
        *)
            error "未知命令: $command"
            help
            exit 1
            ;;
    esac
}

main "$@"