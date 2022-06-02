#!/usr/bin/env bash
set -e -x

ENV=$1
S3_PATH=$2

get_param() {
  echo "Searching for param_name=$1"
  param=$(aws ssm get-parameter --with-decryption --name $1)
  res=$(echo "$param" | jq -r '.Parameter.Value')
  echo "$res"
}

resolve_param_reference() {
  param_ref=$1
  if [[ $param_ref == "{{ssm:"* ]]
  then
    echo "resolving param_ref $param_ref throught ssm"
    param_name_no_prefix=${param_ref#"{{ssm:"}
    param_name=${param_name_no_prefix%"}}"}
    res=$(get_param "$param_name")
    echo "$res"
  else
    echo "param_ref $param_ref is not resolved"
    echo "$param_ref"
  fi
}

function populate_env_file() {
  set +x
  result=""
  while read -r line
  do
    line_sub=$(echo "$line" | envsubst)
    IFS='=' read -r param_name param_reference <<< "$line_sub"
    param_value=$(resolve_param_reference "$param_reference")
    result=$(("$result\n$param_name=$param_value"))
  done < "$1"
  echo "$result"
  set -x
}

start_envoy() {
  if pgrep envoy; then killall envoy; fi
  dir_name="deployment/common/envoy"
  #populate env substitutions in yaml
  cat "$dir_name/envoy.tmpl.yaml" | envsubst > "$dir_name/envoy.yaml"
  nohup envoy -c $dir_name/envoy.yaml --log-path envoy.log < /dev/null &
}

start_app() {
  if pgrep java; then killall java; fi
  nohup sh ./bin/server < /dev/null > /dev/null 2>&1 &
  echo "started java app"
}

echo "Deploying app"

mkdir -p server
cd server

echo "Removing old files"
rm -rf ./**
file_name=$(basename "$S3_PATH")

echo "Copying from s3"
aws s3 cp "$S3_PATH" "$file_name"
unzip "$file_name"
folder_name="${file_name%.zip}"
cd "$folder_name"

echo "Populating files"
env_file=".env.populated"
populate_env_file ".env" > $env_file

set +x
export $(cat $env_file | xargs)
set -x

echo "Strating everything"
start_envoy
start_app

echo "finised executing script"
exit
