@echo off
echo ========================================
echo    启动 HORIZON 后端服务器
echo ========================================
echo.
echo 正在启动 Spring Boot...
echo.

cd /d "%~dp0"
call mvn spring-boot:run

pause
