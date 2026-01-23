# 地平线 | 视界 - 数据库初始化脚本 (PowerShell)
# 使用方法：在 backend 目录下运行 .\init_database.ps1

Write-Host "============================================" -ForegroundColor Cyan
Write-Host "地平线 | 视界 - 数据库初始化脚本" -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""

# MySQL 配置
$MySQLUser = "root"
$MySQLPassword = "Wxl576566"
$DatabaseName = "horizon"

# 尝试查找 MySQL 可执行文件
$MySQLPaths = @(
    "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe",
    "C:\Program Files (x86)\MySQL\MySQL Server 8.0\bin\mysql.exe",
    "D:\MySQL\MySQL Server 8.0\bin\mysql.exe",
    "C:\MySQL\MySQL Server 8.0\bin\mysql.exe"
)

$MySQLPath = $null
foreach ($path in $MySQLPaths) {
    if (Test-Path $path) {
        $MySQLPath = $path
        break
    }
}

if (-not $MySQLPath) {
    Write-Host "[错误] 找不到 MySQL 可执行文件" -ForegroundColor Red
    Write-Host "请手动执行以下步骤：" -ForegroundColor Yellow
    Write-Host "1. 打开 MySQL 命令行" -ForegroundColor Yellow
    Write-Host "2. 执行命令：DROP DATABASE IF EXISTS horizon;" -ForegroundColor Yellow
    Write-Host "3. 执行命令：SOURCE $PSScriptRoot\src\main\resources\sql\init.sql" -ForegroundColor Yellow
    pause
    exit 1
}

Write-Host "找到 MySQL: $MySQLPath" -ForegroundColor Green
Write-Host ""

# 步骤 1: 删除旧数据库
Write-Host "步骤 1: 删除旧数据库..." -ForegroundColor Yellow
$dropCmd = "DROP DATABASE IF EXISTS $DatabaseName;"
& $MySQLPath -u $MySQLUser -p$MySQLPassword -e $dropCmd 2>&1 | Out-Null

if ($LASTEXITCODE -ne 0) {
    Write-Host "[错误] 无法连接到 MySQL，请检查：" -ForegroundColor Red
    Write-Host "  1. MySQL 服务是否已启动" -ForegroundColor Yellow
    Write-Host "  2. 密码是否正确（当前使用：$MySQLPassword）" -ForegroundColor Yellow
    pause
    exit 1
}

Write-Host "[完成] 旧数据库已删除" -ForegroundColor Green
Write-Host ""

# 步骤 2: 创建新数据库并导入表结构
Write-Host "步骤 2: 创建新数据库并导入表结构..." -ForegroundColor Yellow
$initSqlPath = Join-Path $PSScriptRoot "src\main\resources\sql\init.sql"

if (-not (Test-Path $initSqlPath)) {
    Write-Host "[错误] 找不到初始化脚本：$initSqlPath" -ForegroundColor Red
    pause
    exit 1
}

Get-Content $initSqlPath | & $MySQLPath -u $MySQLUser -p$MySQLPassword 2>&1 | Out-Null

if ($LASTEXITCODE -ne 0) {
    Write-Host "[错误] 数据库初始化失败" -ForegroundColor Red
    pause
    exit 1
}

Write-Host "[完成] 数据库初始化成功！" -ForegroundColor Green
Write-Host ""

# 步骤 3: 验证数据库表
Write-Host "步骤 3: 验证数据库表..." -ForegroundColor Yellow
$verifyCmd = "USE $DatabaseName; SHOW TABLES;"
& $MySQLPath -u $MySQLUser -p$MySQLPassword -e $verifyCmd

Write-Host ""
Write-Host "============================================" -ForegroundColor Cyan
Write-Host "✅ 数据库初始化完成！" -ForegroundColor Green
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "测试账号信息：" -ForegroundColor Cyan
Write-Host "  用户名：demo_user" -ForegroundColor White
Write-Host "  密码：123456" -ForegroundColor White
Write-Host ""
Write-Host "下一步：" -ForegroundColor Cyan
Write-Host "  1. 启动后端：mvn spring-boot:run" -ForegroundColor White
Write-Host "  2. 启动前端：cd ..\frontend && npm run dev" -ForegroundColor White
Write-Host ""
pause
