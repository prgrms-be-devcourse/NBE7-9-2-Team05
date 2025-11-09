#!/bin/bash
set -e

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
ENV_FILE="$SCRIPT_DIR/.env"

# Load environment variables if exists
if [ -f "$ENV_FILE" ]; then
  export $(grep -v '^#' "$ENV_FILE" | xargs)
fi

SCENARIO=${1:-loadtest.js}
VUS=${2:-10}
DURATION=${3:-10s}
ROOM_ID=${4:-1}
BASE_URL=${5:-http://host.docker.internal:8080}
ENABLE_LOG=${6:-true}

# Common setup
TEST_ID=$(date +"%Y%m%d_%H%M%S")
LOG_DIR="$SCRIPT_DIR/logs"
mkdir -p "$LOG_DIR"

echo "🚀 Starting K6 load test"
echo "   • Scenario : $SCENARIO"
echo "   • VUS      : $VUS"
echo "   • Duration : $DURATION"
echo "---------------------------------------------"

# Run test with xk6-dashboard + Prometheus RW output
docker compose run --rm -T \
  -v "$SCRIPT_DIR":/scripts \
  -e TEST_ID="$TEST_ID" \
  -e JWT_SECRET="$JWT_SECRET" \
  -e VUS="$VUS" \
  -e DURATION="$DURATION" \
  -e ROOM_ID="$ROOM_ID" \
  -e BASE_URL="$BASE_URL" \
  -e ENABLE_LOG="$ENABLE_LOG" \
  -e K6_PROMETHEUS_RW_SERVER_URL="http://prometheus:9090/api/v1/write" \
  k6 run \
    --summary-trend-stats="avg,min,max,p(90),p(95),p(99)" \
    --out "dashboard=host=0.0.0.0&port=5665&period=2s&open=true&export=/scripts/logs/k6_report_${TEST_ID}.html" \
    -o experimental-prometheus-rw \
    /scripts/k6-scripts/tests/${SCENARIO} \
    | tee "$LOG_DIR/k6_${VUS}vus_${DURATION}_${TEST_ID}.log"