#!/bin/bash

# Function to run a service in the background
run_service() {
    echo "Starting $1..."
    (cd "$1" && mvn spring-boot:run) &
}

# Start the MySQL Docker container first
echo "Starting MySQL Container..."
docker compose up -d

until docker exec ratings-mysql-db mysqladmin ping -h localhost --silent; do
    echo "   ...MySQL is still unzipping the SQL file or starting up..."
    sleep 3
done
echo "✅ MySQL is up!"

# Start Discovery Server (Infrastructure)
run_service "discovery-server"

# Wait a few seconds for Eureka to initialize
echo "Waiting for infrastructure to initialize..."
sleep 10

# Start the other services
run_service "movie-info-service"
run_service "ratings-data-service"
run_service "trending-movies-service"

sleep 10

run_service "movie-catalog-service"

echo "-----------------------------------------------------------------"
echo "-----------------------------------------------------------------"
echo "-----------------------------------------------------------------"
echo "-----------------------------------------------------------------"
echo "-----------------------------------------------------------------"
echo "All services are running. Check http://localhost:8761 for status."
wait