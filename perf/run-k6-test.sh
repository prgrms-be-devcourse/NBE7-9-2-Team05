#!/bin/bash
set -e

TEST_ID=$(date +"%Y%m%d_%H%M%S")
LOG_DIR="./logs"
mkdir -p $LOG_DIR

echo "🎯 Running k6 load test with TEST_ID=$TEST_ID"
docker compose run --rm -e TEST_ID=$TEST_ID k6 | tee "$LOG_DIR/k6_${TEST_ID}.log"
echo "✅ Test complete! Logs saved to $LOG_DIR/k6_${TEST_ID}.log"