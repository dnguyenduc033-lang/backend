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

cd "$BACKEND_ROOT"

if docker ps --format '{{.Names}}' | grep -q '^inventory-mysql$'; then
  docker exec -i inventory-mysql \
    mysql \
    -uroot \
    -p"${MYSQL_ROOT_PASSWORD}" \
    "${MYSQL_DATABASE}" < "${SEED_FILE}"
elif docker compose ps --status running mysql 2>/dev/null | grep -q inventory-mysql; then
  docker compose exec -T mysql \
    mysql \
    -uroot \
    -p"${MYSQL_ROOT_PASSWORD}" \
    "${MYSQL_DATABASE}" < "${SEED_FILE}"
else
  echo "MySQL container is not running. Start it first."
  exit 1
fi

echo "Mock data loaded into ${MYSQL_DATABASE}"
echo
echo "Test accounts (password for all: password123):"
echo "  admin@inventory.com    (ADMIN)"
echo "  manager@inventory.com  (MANAGER)"
echo "  mai.staff@inventory.com (STAFF)"
