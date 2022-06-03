#!/usr/bin/env bash
set -e -x

Env=$1
S3_PATH=$2
#should be in the default folder
ENVOY_FILE=$3

create_file() {
  file=$1
  if ! test -f "$file"; then
    sudo mkdir -p "$(dirname "$file")"
    sudo touch "$file"
  fi
}

get_param() {
  >&2 echo "Searching param value of $1"
  if ! param=$(aws ssm get-parameter --with-decryption --name "$1"); then
    >&2 echo "Param not found $1"
    exit 1
  fi
  res=$(echo "$param" | jq -r '.Parameter.Value')
  echo "$res"
}

resolve_param_reference() {
  >&2 echo "Resolving param reference $1"
  param_ref=$1
  if [[ $param_ref == "{{ssm:"* ]]
  then
    param_name_no_prefix=${param_ref#"{{ssm:"}
    param_name=${param_name_no_prefix%"}}"}
    if ! res=$(get_param "$param_name"); then
      >&2 echo "Failure during param store resolution for $1"
      exit 1
    fi
    echo "$res"
  else
    echo "$param_ref"
  fi
}

function populate_env_file() {
  >&2 echo "Population env file $1"
  while read -r line || [ -n "$line" ]
  do
    export ENV="$Env"
    line_sub=$(echo "$line" | envsubst)
    IFS='=' read -r param_name param_reference <<< "$line_sub"
    if ! param_value=$(resolve_param_reference "$param_reference"); then
      >&2 echo "Param_ref resolution error for $1"
      exit 1
    fi
    echo "$param_name=$param_value"
  done < "$1"
}

start_envoy() {
  if pgrep envoy; then killall envoy; fi
  #populate env substitutions in yaml
  envsubst < "../$ENVOY_FILE" > "envoy.yaml"
  envoy_log_file="/var/log/pomo/envoy.log"
  create_file $envoy_log_file
  nohup envoy -c envoy.yaml --log-path "$envoy_log_file" < /dev/null > /dev/null 2>&1 &
}

start_app() {
  java_log_file=$(get_param "JAVA_LOG_FILE")
  create_file "$java_log_file"
  if pgrep java; then killall java; fi
  nohup sh ./bin/server < /dev/null > /dev/null 2>&1 &
  echo "started java app"
}

echo "Deploying app"

mkdir -p server
echo "Removing old files"
cd server
rm -rf ./**

echo "Moving envoy file to the server folder"
cd ..
mv "$ENVOY_FILE" "server/$(basename "$ENVOY_FILE")"
cd server

echo "Copying from s3"
file_name=$(basename "$S3_PATH")
aws s3 cp "$S3_PATH" "$file_name"
unzip "$file_name"
folder_name="${file_name%.zip}"
cd "$folder_name"

set +x
echo "Populating files"
env_file=".env.prod.populated"
populate_env_file ".env.prod" > $env_file
export $(cat "$env_file" | xargs)
set -x

echo "Strating everything"

#create logs folder
mkdir -p ~/logs

start_envoy
start_app

echo "finished executing script"
exit
