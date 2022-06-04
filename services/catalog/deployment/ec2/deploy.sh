#!/usr/bin/env bash
set -e -x

WORKING_DIR="/home/ubuntu"
export ENV=$1
S3_PATH=$2
RUN_MIGRATIONS=$3

echo "Deploying app"


echo "configuring bin files"
export PATH="$PATH:$WORKING_DIR/bin"
sudo cp -r $WORKING_DIR/deployment/ec2/bin/. $WORKING_DIR/bin

mkdir -p server
cd server
echo "Removing old files"
sudo rm -rf ./**

echo "Copying from s3"
file_name=$(basename "$S3_PATH")
aws s3 cp "$S3_PATH" "$file_name"
unzip "$file_name"
folder_name="${file_name%.zip}"
cd "$folder_name"

echo "Populating files and exporting variables"
set +x
env_file=".env.prod.populated"
populate_env_file ".env.prod" > $env_file
export $(cat "$env_file" | xargs)
set -x

echo "Starting envoy"
echo "Creating envoy.yaml"
ENVOY_CONFIG_FILE="$WORKING_DIR/envoy.yaml"
envsubst < "$WORKING_DIR/deployment/common/envoy/envoy.tmpl.yaml" | sudo tee $ENVOY_CONFIG_FILE

echo "Creating envoy.service file"
export WORKING_DIR
export ENVOY_CONFIG_FILE
export ENVOY_LOG_FILE="$WORKING_DIR/envoy.log"
create_file $ENVOY_LOG_FILE
envsubst < $WORKING_DIR/deployment/ec2/envoy.tmpl.service  | sudo tee /etc/systemd/system/envoy.service
echo "Starting envoy service with systemd"
sudo systemctl daemon-reload

echo "Envoy started"

java_log_file=$(get_param "JAVA_LOG_FILE")
create_file "$java_log_file"
if [ "$RUN_MIGRATIONS" ]; then
  sh ./bin/db-migrations-command
else
  echo "Starting java"
  if pgrep java; then killall java; fi
  nohup sh ./bin/server < /dev/null > /dev/null 2>&1 &
  echo "started java app"
fi

echo "finished executing script"
exit
