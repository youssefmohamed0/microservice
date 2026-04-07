#!/bin/bash

# Function to run a service in the background
run_service() {
    echo "Starting $1..."
    (cd "$1" && mvn spring-boot:run) &
}

# 1. Start Discovery Server first (Infrastructure)
run_service "discovery-server"

# Wait a few seconds for Eureka to initialize
sleep 10

# 2. Start the other services
run_service "movie-info-service"
run_service "ratings-data-service"
run_service "movie-catalog-service"

echo "All services are starting. Check http://localhost:8761 for status."
wait