#!/bin/bash
set -e

# 현재 스크립트가 위치한 디렉토리 경로로 이동
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

# --- 환경변수 불러오기 ---
ENV_FILE="$SCRIPT_DIR/.env"

if [ -f "$ENV_FILE" ]; then
  export $(grep -v '^#' "$ENV_FILE" | xargs)
else
  echo "❌ .env 파일을 찾을 수 없습니다. ($ENV_FILE)"
  exit 1
fi
# --- 테스트 파라미터 ---
VUS=${1:-10}               # 기본 10명, 첫 번째 인자로 변경 가능
DURATION=${2:-10s}         # 기본 10초, 두 번째 인자로 변경 가능
ROOM_ID=${3:-1}            # 기본 방 ID 1
BASE_URL=${4:-http://host.docker.internal:8080}
ENABLE_LOG=${5:-true}     # JWT 콘솔 로그 여부
# -------------------
TEST_ID=$(date +"%Y%m%d_%H%M%S")
LOG_DIR="$(realpath "$SCRIPT_DIR/logs")"
mkdir -p "$LOG_DIR"

echo "🚀 Starting k6 load test"
echo "   • TEST_ID: $TEST_ID"
echo "   • VUS: $VUS"
echo "   • DURATION: $DURATION"
echo "   • ROOM_ID: $ROOM_ID"
echo "   • BASE_URL: $BASE_URL"
echo "   • JWT_SECRET: (loaded from perf/.env)"
echo "   • LOG FILE: $LOG_DIR/k6_${TEST_ID}.log"

# --- 실행 ---
docker compose run --rm -T \
  -e TEST_ID=$TEST_ID \
  -e JWT_SECRET=$JWT_SECRET \
  -e VUS=$VUS \
  -e DURATION=$DURATION \
  -e ROOM_ID=$ROOM_ID \
  -e BASE_URL=$BASE_URL \
  -e ENABLE_LOG=$ENABLE_LOG \
  k6 | tee "$LOG_DIR/k6_${VUS}vus_${DURATION}_${TEST_ID}.log"

echo "✅ Test complete! Logs saved to $LOG_DIR/k6_${TEST_ID}.log"