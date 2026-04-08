#!/bin/bash
echo "🛑 Stopping all services..."
# Kills all Java processes started by Maven
pkill -f 'java -jar' 
# Stops the Docker container
docker compose down
echo "Done."