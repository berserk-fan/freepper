#!/usr/bin/env bash
set -e -x

WORKING_DIR="/home/ubuntu"
ENV=$1
S3_PATH=$2
RUN_MIGRATIONS=$3

echo "Deploying app"

echo "configuring bin files"
export PATH="$PATH:$WORKING_DIR/bin"
sudo cp -r $WORKING_DIR/deployment/ec2/bin/. $WORKING_DIR/bin


echo "Copying from s3"
mkdir -p server
cd server
rm -rf "./**"
file_name=$(basename "$S3_PATH")
aws s3 cp "$S3_PATH" "$file_name"
unzip -o "$file_name"
rm "$file_name"
folder_name="${file_name%.zip}"
cp -a "$folder_name/." .
rm -r "$folder_name"

echo "Populating files and exporting variables"
set +x
env_file=".env.prod.populated"
ENV=$ENV JAVA_LOG_FILE="$WORKING_DIR/log/catalog_server.log" populate_env_file ".env.prod" > $env_file
export $(cat "$env_file" | xargs)
set -x



echo "Creating envoy.yaml"
envoy_config="$WORKING_DIR/envoy.yaml"
envsubst < "$WORKING_DIR/deployment/common/envoy/envoy.tmpl.yaml" | sudo tee $envoy_config

echo "Creating envoy.service file"
envoy_log="$WORKING_DIR/envoy.log"
create_file $envoy_log
envoy_service_template=$WORKING_DIR/deployment/ec2/envoy.tmpl.service
envoy_service=/etc/systemd/system/envoy.service
WORKING_DIR=$WORKING_DIR ENVOY_CONFIG_FILE=$envoy_config ENVOY_LOG_FILE=$envoy_log  envsubst < $envoy_service_template  | sudo tee $envoy_service


if [ "$RUN_MIGRATIONS" = true ]; then
  echo "Running migrations"
  sh ./bin/db-migrations-command
else
  echo "Starting java"
  server_bin=$(realpath ./bin/server)
  env_file_absolute=$(realpath $env_file)
  catalog_service_template=$WORKING_DIR/deployment/ec2/catalog-scala.tmpl.service
  catalog_service=/etc/systemd/system/catalog-scala.service
  WORKING_DIR=$WORKING_DIR SCALA_SERVER_BIN=$server_bin ENV_FILE=$env_file_absolute envsubst <  $catalog_service_template | sudo tee $catalog_service
fi

echo "Reloading systemd daemon"
sudo systemctl daemon-reload
echo "Envoy started"

echo "finished deploy script"
exit
