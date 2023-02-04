#!/usr/bin/env bash
set -e -x

DIR_NAME="$(dirname "$0")"
ENV_FILE=".env.local"

mkdir -p $DIR_NAME/volume/data

find "$DIR_NAME/init" -type f -name '*.sql' -exec cat {} + >> "$DIR_NAME/init.sql"

docker-compose -f $DIR_NAME/docker-compose.yaml -p catalog --env-file "$ENV_FILE" up -d
