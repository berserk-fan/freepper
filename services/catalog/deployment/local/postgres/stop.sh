#!/usr/bin/env bash
set -e -x

DIR_NAME="$(dirname "$0")"
docker-compose -f $DIR_NAME/docker-compose.yaml -p catalog --env-file ./.env.it down || echo "already down"
