@echo off
setlocal enabledelayedexpansion

cd /d "%~dp0\.."

set MYSQL_ROOT_PASSWORD=123456
set MYSQL_DATABASE=inventory_db

if exist ".env" (
  for /f "usebackq tokens=1,* delims==" %%A in (".env") do (
    set "line=%%A"
    if not "!line:~0,1!"=="#" (
      if "%%A"=="MYSQL_ROOT_PASSWORD" set MYSQL_ROOT_PASSWORD=%%B
      if "%%A"=="MYSQL_DATABASE" set MYSQL_DATABASE=%%B
    )
  )
)

docker ps --format "{{.Names}}" | findstr /x "inventory-mysql" >nul
if %errorlevel%==0 (
  docker exec -i inventory-mysql mysql -uroot -p%MYSQL_ROOT_PASSWORD% %MYSQL_DATABASE% < "%~dp0seed-mock-data.sql"
  goto :done
)

docker compose ps --status running mysql 2>nul | findstr /i "mysql" >nul
if %errorlevel%==0 (
  docker compose exec -T mysql mysql -uroot -p%MYSQL_ROOT_PASSWORD% %MYSQL_DATABASE% < "%~dp0seed-mock-data.sql"
  goto :done
)

echo MySQL container is not running. Start it first:
echo   docker compose up -d mysql
exit /b 1

:done
echo.
echo Mock data loaded into %MYSQL_DATABASE%
echo.
echo Test accounts (password for all: 123):
echo   admin@inventory.com     (ADMIN)
echo   manager@inventory.com   (MANAGER)
echo   mai.staff@inventory.com (STAFF)
endlocal
