#!/usr/bin/env bash
set -e -x

DIR_NAME="$(dirname "$0")"
(
  export $(grep -v '^#' .env | xargs);
  #populate env substitutions in yaml
  cat $DIR_NAME/envoy.tmpl.yaml | envsubst > $DIR_NAME/'envoy.yaml'
)
docker-compose -f $DIR_NAME/docker-compose.yaml -p catalog down || echo "already down"
docker-compose -f $DIR_NAME/docker-compose.yaml -p catalog --env-file .env up
