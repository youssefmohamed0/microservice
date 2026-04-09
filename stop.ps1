Write-Host "🛑 Stopping all services..." -ForegroundColor Red

# Stop the Docker containers
Write-Host "Stopping Docker containers..."
docker compose down

# Stop background PowerShell jobs (if you used the PS start script)
Get-Job | Stop-Job
Get-Job | Remove-Job

# Kill Java processes started by Maven
# Note: This targets java processes. If you have other critical Java apps running, 
# be aware this is a broad stroke similar to pkill.
Stop-Process -Name "java" -Force -ErrorAction SilentlyContinue

Write-Host "✅ Done." -ForegroundColor Green