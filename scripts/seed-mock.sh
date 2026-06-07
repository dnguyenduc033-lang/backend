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

if ! docker compose ps --status running mysql | grep -q inventory-mysql; then
  echo "MySQL container is not running. Start it with: docker compose up -d mysql"
  exit 1
fi

docker compose exec -T mysql \
  mysql \
  -uroot \
  -p"${MYSQL_ROOT_PASSWORD}" \
  "${MYSQL_DATABASE}" < "${SEED_FILE}"

echo "Mock data loaded into ${MYSQL_DATABASE}"
echo
echo "Test accounts (password for all: password123):"
echo "  admin@inventory.com    (ADMIN)"
echo "  manager@inventory.com  (MANAGER)"
echo "  mai.staff@inventory.com (STAFF)"
