# 1. Start Docker containers in the background
Write-Host "Starting Docker Infrastructure (MySQL & MongoDB)..." -ForegroundColor Cyan
docker-compose up -d

# 2. Wait a few seconds for DBs to initialize
Write-Host "Waiting 10 seconds for databases to warm up..." -ForegroundColor Yellow
Start-Sleep -Seconds 10

# 3. Define the services in order (Discovery first!)
$services = @(
    "discovery-server",
    "movie-info-service",
    "ratings-data-service",
    "trending-movies-service",
    "movie-catalog-service"
)

# 4. Loop through and start each in a new window
foreach ($service in $services) {
    if (Test-Path ".\$service") {
        Write-Host "Launching $service in a new window..." -ForegroundColor Green
        
        # This opens a new PowerShell window, changes directory, and runs Maven
        Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd .\$service; mvn spring-boot:run"
        
        # Small delay so they don't all fight for CPU at the exact same millisecond
        Start-Sleep -Seconds 2
    } else {
        Write-Warning "Directory $service not found. Skipping..."
    }
}

Write-Host "`nAll services triggered!" -ForegroundColor Magentax
Write-Host "Check http://localhost:8761 for the Eureka dashboard." -ForegroundColor White