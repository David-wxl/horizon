@echo off
chcp 65001 >nul
echo ============================================
echo 地平线 | 视界 - 数据库初始化脚本
echo ============================================
echo.

REM 设置 MySQL 路径（请根据实际安装路径修改）
set MYSQL_PATH="C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe"

REM 如果上面的路径不对，尝试常见路径
if not exist %MYSQL_PATH% set MYSQL_PATH="C:\Program Files (x86)\MySQL\MySQL Server 8.0\bin\mysql.exe"
if not exist %MYSQL_PATH% set MYSQL_PATH="D:\MySQL\MySQL Server 8.0\bin\mysql.exe"
if not exist %MYSQL_PATH% set MYSQL_PATH=mysql.exe

echo 步骤 1: 删除旧数据库...
%MYSQL_PATH% -u root -pWxl576566 -e "DROP DATABASE IF EXISTS horizon;"
if %errorlevel% neq 0 (
    echo [错误] 无法连接到 MySQL，请检查：
    echo   1. MySQL 服务是否已启动
    echo   2. 密码是否正确（当前使用：Wxl576566）
    echo   3. MySQL 路径是否正确
    pause
    exit /b 1
)
echo [完成] 旧数据库已删除
echo.

echo 步骤 2: 创建新数据库并导入表结构...
%MYSQL_PATH% -u root -pWxl576566 < src\main\resources\sql\init.sql
if %errorlevel% neq 0 (
    echo [错误] 数据库初始化失败
    pause
    exit /b 1
)
echo [完成] 数据库初始化成功！
echo.

echo 步骤 3: 验证数据库表...
%MYSQL_PATH% -u root -pWxl576566 -e "USE horizon; SHOW TABLES;"
echo.

echo ============================================
echo ✅ 数据库初始化完成！
echo ============================================
echo.
echo 测试账号信息：
echo   用户名：demo_user
echo   密码：123456
echo.
echo 下一步：
echo   1. 启动后端：mvn spring-boot:run
echo   2. 启动前端：cd ..\frontend && npm run dev
echo.
pause
