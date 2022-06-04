#!/usr/bin/env bash

set -x -e
TEMP=$(mktemp)

set +x
echo "$EC2_KEY_PAIR_CONTENTS" > "$TEMP"
set -x

deployment_folder="$GITHUB_WORKSPACE/services/catalog/deployment"

full_host="$EC2_USERNAME@$EC2_HOST"
options='StrictHostKeyChecking no'

ssh -o "$options" -i "$TEMP" "$full_host" "sudo rm -rf ./**; mkdir -p deployment"

scp -r -o "$options" -i "$TEMP" \
  "$deployment_folder/common" \
  "$full_host:deployment/common"

scp -r -o "$options" -i "$TEMP" \
  "$deployment_folder/ec2" \
  "$full_host:deployment/ec2"

ssh -o "$options" -i "$TEMP" "$full_host" "bash deployment/ec2/deploy.sh $ENV $S3_PATH $RUN_MIGRATIONS"

rm "$TEMP"
