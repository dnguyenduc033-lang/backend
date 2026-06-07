#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BACKEND_ROOT="$(dirname "$SCRIPT_DIR")"
SEED_FILE="$SCRIPT_DIR/seed-mock-data.sql"

if [ -f "$BACKEND_ROOT/.env" ]; then
  set -a
  # shellcheck disable=SC1091
  source "$BACKEND_ROOT/.env"
  set +a
fi

MYSQL_ROOT_PASSWORD="${MYSQL_ROOT_PASSWORD:-123456}"
MYSQL_DATABASE="${MYSQL_DATABASE:-inventory_db}"

PROJECT_ROOT="$(dirname "$BACKEND_ROOT")"
if [ -f "$PROJECT_ROOT/docker-compose.yml" ]; then
  COMPOSE_DIR="$PROJECT_ROOT"
elif [ -f "$BACKEND_ROOT/docker-compose.yml" ]; then
  COMPOSE_DIR="$BACKEND_ROOT"
else
  COMPOSE_DIR="$BACKEND_ROOT"
fi

cd "$COMPOSE_DIR"

if docker ps --format '{{.Names}}' | grep -q '^inventory-mysql$'; then
  docker exec -i inventory-mysql \
    mysql \
    -uroot \
    -p"${MYSQL_ROOT_PASSWORD}" \
    "${MYSQL_DATABASE}" < "${SEED_FILE}"
elif docker compose ps --status running mysql 2>/dev/null | grep -q mysql; then
  docker compose exec -T mysql \
    mysql \
    -uroot \
    -p"${MYSQL_ROOT_PASSWORD}" \
    "${MYSQL_DATABASE}" < "${SEED_FILE}"
else
  echo "MySQL container is not running. Start it first:"
  echo "  docker compose up -d mysql"
  exit 1
fi

echo "Mock data loaded into ${MYSQL_DATABASE}"
echo
echo "Test accounts (password for all: 123):"
echo "  admin@inventory.com    (ADMIN)"
echo "  manager@inventory.com  (MANAGER)"
echo "  mai.staff@inventory.com (STAFF)"
