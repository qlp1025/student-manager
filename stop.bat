@echo off
chcp 65001 >nul
echo Stopping services...

echo Killing processes on port 8080 (backend)...
for /f "tokens=5" %%a in ('powershell -command "Get-NetTCPConnection -LocalPort 8080 -State Listen | Select-Object -ExpandProperty OwningProcess"') do (
    echo Stopping PID %%a...
    taskkill /PID %%a /F >nul 2>&1
)

echo Killing processes on port 5173 (frontend)...
for /f "tokens=5" %%a in ('powershell -command "Get-NetTCPConnection -LocalPort 5173 -State Listen | Select-Object -ExpandProperty OwningProcess"') do (
    echo Stopping PID %%a...
    taskkill /PID %%a /F >nul 2>&1
)

echo All services stopped.
pause
