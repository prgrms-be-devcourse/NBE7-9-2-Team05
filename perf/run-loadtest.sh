#!/bin/bash
set -e

# 🕒 TEST_ID를 타임스탬프로 생성
TEST_ID=$(date +"%Y%m%d_%H%M%S")
LOG_DIR="./logs"
mkdir -p $LOG_DIR

echo "🧹 Cleaning previous containers..."
docker compose down -v --remove-orphans > /dev/null 2>&1
docker network prune -f > /dev/null 2>&1

echo "⬇️ Pulling latest images..."
docker compose pull prometheus grafana k6 > /dev/null 2>&1

echo "🚀 Starting Prometheus + Grafana..."
docker compose up -d prometheus grafana

echo "⏳ Waiting for services to initialize..."
sleep 10

echo "🔍 Current container status:"
docker compose ps

echo "🎯 Running k6 load test with TEST_ID=$TEST_ID"
docker compose run --rm -e TEST_ID=$TEST_ID k6 | tee "$LOG_DIR/k6_${TEST_ID}.log"

echo "✅ Test complete! Logs saved to $LOG_DIR/k6_${TEST_ID}.log"
echo "📊 Grafana dashboard: http://localhost:3300"
echo "🧠 Prometheus: http://localhost:9090"