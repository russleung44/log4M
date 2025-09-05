# PowerShell script to run log4M with environment variables
# Usage: .\run-local.ps1

Write-Host "Starting log4M Application..." -ForegroundColor Green

# Set your Bot Token here (replace with your actual token)
$env:BOT_TOKEN = "your_actual_bot_token_here"

# Navigate to backend directory
Set-Location -Path "$PSScriptRoot\backend"

# Run the Spring Boot application
try {
    mvn spring-boot:run "-Dspring-boot.run.profiles=local"
} catch {
    Write-Error "Failed to start the application: $_"
}

# Return to original directory
Set-Location -Path $PSScriptRoot