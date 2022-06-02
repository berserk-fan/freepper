#!/usr/bin/env bash

set -x -e
TEMP=$(mktemp)

set +x
echo "$EC2_KEY_PAIR_CONTENTS" > "$TEMP"
set -x

deployment_folder="$GITHUB_WORKSPACE/services/catalog/deployment"
full_host="$EC2_USERNAME@$EC2_HOST"
options='StrictHostKeyChecking no'
envoy_file="envoy.tmpl.yaml"
scp -o "$options" -i "$TEMP" \
  "$deployment_folder/common/envoy/envoy.tmpl.yaml" \
  "$full_host:$envoy_file"

ssh -o "$options" -i "$TEMP" "$full_host" \
  "bash -s -e" < "$deployment_folder/ec2/deploy.sh" "$ENV $S3_PATH $envoy_file"
rm "$TEMP"
