#!/usr/bin/bash
set -e -x

# This is executed inside a package directory
# Write as if you in package directory
# Available env vars - ENV

echo "Starting up server"

populate_env_file() {
  set -e -x
  file_name=$1
  env_file=$2
  substituted=$(cat $file_name | envsubst)
  first_line=$(echo "$substituted" | head -n 1)
  rest_lines=$(echo "$substituted" | tail -n +2)
  if ! [[ $first_line == "PREFIX="* ]]
  then
    echo "No prefix found"
    exit 1
  fi
  prefix=${first_line#"PREFIX="}
  res=''
  echo "$rest_lines" | while read env_var_setter
  do
     param_name="$prefix${env_var_setter%"="}"
     echo "Searching for param_name=$param_name"
     param=$(aws ssm get-parameter --with-decryption --name $param_name)
     param_value=$(echo $param | jq -r '.Parameter.Value')
     res="$res$env_var_setter$param_value\n"
  done
  (echo $res > "$env_file")
}

start_envoy() {
  set -e -x
  process_name="envoy"
  pkill -f $process_name
  dir_name="deployment/common/envoy"
  #populate env substitutions in yaml
  cat "$dir_name/envoy.tmpl.yaml" | envsubst > "$dir_name/envoy.yaml"
  cmd="envoy -c $dir_name/envoy.yaml --log-path envoy.log"
  bash -c "exec -a $process_name $cmd &"
}

start_app() {
  set -e -x
  ./bin/server
}

env_file=".env.populated"
populate_env_file ".env" "$env_file"
export $(grep -v '^#' $env_file | xargs);
start_envoy
start_app
