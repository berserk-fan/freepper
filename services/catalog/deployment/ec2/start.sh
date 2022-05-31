#!/usr/bin/bash
set -e -x

# This is executed inside a package directory
# Write as if you in package directory
# Available env vars - ENV

echo "Starting up server"

populate_env_file() {
  set +x
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
  echo "$rest_lines" | while read env_var_setter
  do
     param_name="$prefix${env_var_setter%"="}"
     echo "Searching for param_name=$param_name"
     param=$(aws ssm get-parameter --with-decryption --name $param_name)
     param_value=$(echo "$param" | jq -r '.Parameter.Value')
     (printf "$env_var_setter$param_value\n") >> "$env_file"
  done
  set -x
}

start_envoy() {
  if pgrep envoy; then killall envoy; fi
  process_name="envoy"
  dir_name="deployment/common/envoy"
  #populate env substitutions in yaml
  cat "$dir_name/envoy.tmpl.yaml" | envsubst > "$dir_name/envoy.yaml"
  cmd="envoy -c $dir_name/envoy.yaml --log-path envoy.log"
  bash -c "exec -a $process_name $cmd &"
}

start_app() {
  if pgrep java; then killall java; fi
  process_name="java_app"
  cmd="./bin/server"
  bash -c "exec -a $process_name $cmd &"
}

env_file=".env.populated"
echo '' > $env_file
populate_env_file ".env" "$env_file"]
set +x
export $(cat $env_file | xargs)
set -x
start_envoy
start_app
