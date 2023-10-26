#!/usr/bin/env bash
set -e -x

DIR_NAME="$(dirname "$0")"
(
  export $(grep -v '^#' .env.local | xargs);
  #populate env substitutions in yaml
  common_folder="${DIR_NAME/local/common}"
  envsubst < "$common_folder/envoy.tmpl.yaml" > "$DIR_NAME/envoy.yaml"
)
docker-compose -f "$DIR_NAME/docker-compose.yaml" -p catalog --env-file .env.local up