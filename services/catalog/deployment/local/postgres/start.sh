#!/usr/bin/env bash
set -e -x

DIR_NAME="$(dirname "$0")"
mkdir -p $DIR_NAME/volume/data
docker-compose -f $DIR_NAME/docker-compose.yaml -p catalog --env-file ./.env down || echo "already down"
docker-compose -f $DIR_NAME/docker-compose.yaml -p catalog --env-file ./.env up -d
