#!/usr/bin/env bash
set -e -x

export ENV=$1
S3_PATH=$2
#should be in the default folder
ENVOY_FILE=$3

get_param() {
  >&2 echo "Searching param value of $1"
  param=$(aws ssm get-parameter --with-decryption --name "$1")
  res=$(echo "$param" | jq -r '.Parameter.Value')
  echo "$res"
}

resolve_param_reference() {
  param_ref=$1
  if [[ $param_ref == "{{ssm:"* ]]
  then
    param_name_no_prefix=${param_ref#"{{ssm:"}
    param_name=${param_name_no_prefix%"}}"}
    res=$(get_param "$param_name")
    echo "$res"
  else
    echo "$param_ref"
  fi
}

function populate_env_file() {
  while read -r line
  do
    line_sub=$(echo "$line" | envsubst)
    IFS='=' read -r param_name param_reference <<< "$line_sub"
    param_value=$(resolve_param_reference "$param_reference")
    echo "$param_name=$param_value"
  done < "$1"
}

start_envoy() {
  if pgrep envoy; then killall envoy; fi
  #populate env substitutions in yaml
  envsubst < "../$ENVOY_FILE" > "envoy.yaml"
  mkdir -p ~/logs
  nohup envoy -c envoy.yaml --log-path ~/logs/envoy.log < /dev/null &
}

start_app() {
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
start_envoy
start_app

echo "finised executing script"
exit
