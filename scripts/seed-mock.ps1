$ErrorActionPreference = "Stop"

$BackendRoot = Split-Path -Parent $PSScriptRoot
$SeedFile = Join-Path $PSScriptRoot "seed-mock-data.sql"

Set-Location $BackendRoot

$mysqlPassword = "123456"
$mysqlDatabase = "inventory_db"

$envFile = Join-Path $BackendRoot ".env"
if (Test-Path $envFile) {
    Get-Content $envFile | ForEach-Object {
        if ($_ -match '^\s*([^#=]+)=(.*)$') {
            $name = $matches[1].Trim()
            $value = $matches[2].Trim()
            switch ($name) {
                "MYSQL_ROOT_PASSWORD" { $mysqlPassword = $value }
                "MYSQL_DATABASE" { $mysqlDatabase = $value }
            }
        }
    }
}

$running = docker ps --format "{{.Names}}" 2>$null
if ($running -contains "inventory-mysql") {
    Get-Content $SeedFile -Raw | docker exec -i inventory-mysql mysql -uroot "-p$mysqlPassword" $mysqlDatabase
}
elseif ((docker compose ps --status running mysql 2>$null) -match "mysql") {
    Get-Content $SeedFile -Raw | docker compose exec -T mysql mysql -uroot "-p$mysqlPassword" $mysqlDatabase
}
else {
    Write-Host "MySQL container is not running. Start it first:"
    Write-Host "  docker compose up -d mysql"
    exit 1
}

Write-Host ""
Write-Host "Mock data loaded into $mysqlDatabase"
Write-Host ""
Write-Host "Test accounts (password for all: 123):"
Write-Host "  admin@inventory.com     (ADMIN)"
Write-Host "  manager@inventory.com   (MANAGER)"
Write-Host "  mai.staff@inventory.com (STAFF)"
