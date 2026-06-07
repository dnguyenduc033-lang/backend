#!/usr/bin/env bash
set -euo pipefail

if [ $# -ne 1 ]; then
  echo "Usage: $0 <path-to-sql-dump>"
  exit 1
fi

DUMP_FILE="$1"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BACKEND_ROOT="$(dirname "$SCRIPT_DIR")"

if [ ! -f "$DUMP_FILE" ]; then
  echo "Dump file not found: ${DUMP_FILE}"
  exit 1
fi

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
  "${MYSQL_DATABASE}" < "${DUMP_FILE}"

echo "Database imported from ${DUMP_FILE}"
