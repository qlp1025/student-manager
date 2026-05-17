@echo off
chcp 65001 >nul
echo Starting backend (Spring Boot)...
start "Backend" cmd /k "cd /d %~dp0backend && mvn spring-boot:run"

echo Starting frontend (Vite dev server)...
start "Frontend" cmd /k "cd /d %~dp0frontend && npm run dev"

echo.
echo Services started:
echo   Backend:  http://localhost:8080
echo   Frontend: http://localhost:5173
echo.
pause
