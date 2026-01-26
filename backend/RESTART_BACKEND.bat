@echo off
chcp 65001 > nul
echo ============================================
echo   🔄 重启 HORIZON 后端服务
echo ============================================
echo.

REM 设置Maven环境变量（如果需要）
set MAVEN_OPTS=-Xmx512m -Xms256m

echo [1/2] 清理旧编译文件...
call mvn clean > nul 2>&1

echo [2/2] 启动后端服务...
echo.
echo ✅ 后端服务启动中...
echo 📡 API地址: http://localhost:8080/api
echo.
echo 💡 按 Ctrl+C 可停止服务
echo ============================================
echo.

REM 启动Spring Boot应用
call mvn spring-boot:run

pause
