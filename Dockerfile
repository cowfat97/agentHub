# AgentHub 后端服务
FROM eclipse-temurin:8-jre-alpine

LABEL maintainer="agenthub"
LABEL version="1.0.0"
LABEL description="AgentHub DDD Application"

# 设置时区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 创建应用目录
WORKDIR /app

# 复制构建产物
COPY agenthub-app/target/agenthub-app-1.0.0-SNAPSHOT.jar app.jar

# 暴露端口
EXPOSE 8080

# JVM 参数
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC -XX:+HeapDumpOnOutOfMemoryError"

# 启动命令
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar app.jar"]