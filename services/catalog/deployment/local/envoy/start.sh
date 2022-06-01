#!/usr/bin/env bash
set -e -x

DIR_NAME="$(dirname "$0")"
(
  export $(grep -v '^#' .env.local | xargs);
  #populate env substitutions in yaml
  cat "${DIR_NAME/local/common}"/envoy.tmpl.yaml | envsubst > $DIR_NAME/'envoy.yaml'
)
docker-compose -f $DIR_NAME/docker-compose.yaml -p catalog --env-file .env.local up down || echo "already down"
docker-compose -f $DIR_NAME/docker-compose.yaml -p catalog --env-file .env.local up
