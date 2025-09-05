@echo off
REM Windows batch script to run log4M with environment variables
REM Usage: run-local.bat

echo Starting log4M Application...

REM Set your Bot Token here (replace with your actual token)
set BOT_TOKEN=your_actual_bot_token_here

REM Navigate to backend directory
cd /d "%~dp0backend"

REM Run the Spring Boot application
mvn spring-boot:run -Dspring-boot.run.profiles=local

pause