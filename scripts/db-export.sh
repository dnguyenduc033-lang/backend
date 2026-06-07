#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BACKEND_ROOT="$(dirname "$SCRIPT_DIR")"

if [ -f "$BACKEND_ROOT/.env" ]; then
  set -a
  # shellcheck disable=SC1091
  source "$BACKEND_ROOT/.env"
  set +a
fi

MYSQL_ROOT_PASSWORD="${MYSQL_ROOT_PASSWORD:-123456}"
MYSQL_DATABASE="${MYSQL_DATABASE:-inventory_db}"
BACKUP_DIR="$BACKEND_ROOT/backups"
TIMESTAMP="$(date +%Y%m%d_%H%M%S)"
OUTPUT_FILE="$BACKUP_DIR/${MYSQL_DATABASE}_${TIMESTAMP}.sql"

mkdir -p "$BACKUP_DIR"

cd "$BACKEND_ROOT"

if ! docker compose ps --status running mysql | grep -q inventory-mysql; then
  echo "MySQL container is not running. Start it with: docker compose up -d mysql"
  exit 1
fi

docker compose exec -T mysql \
  mysqldump \
  -uroot \
  -p"${MYSQL_ROOT_PASSWORD}" \
  --single-transaction \
  --routines \
  --triggers \
  "${MYSQL_DATABASE}" > "${OUTPUT_FILE}"

echo "Database exported to ${OUTPUT_FILE}"
