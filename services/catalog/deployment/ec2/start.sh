#!/usr/bin/env bash
set -e -x

# This is executed inside a package directory
# Write as if you in package directory
# Available env vars - ENV

echo "Starting up server"

install_envoy() {
# check envoy is installed
  if ! command -v envoy &> /dev/null
  then
      sudo apt update
      sudo apt install apt-transport-https gnupg2 curl lsb-release
      curl -sL 'https://deb.dl.getenvoy.io/public/gpg.8115BA8E629CC074.key' | sudo gpg --dearmor -o /usr/share/keyrings/getenvoy-keyring.gpg
      echo a077cb587a1b622e03aa4bf2f3689de14658a9497a9af2c427bba5f4cc3c4723 /usr/share/keyrings/getenvoy-keyring.gpg | sha256sum --check
      echo "deb [arch=amd64 signed-by=/usr/share/keyrings/getenvoy-keyring.gpg] https://deb.dl.getenvoy.io/public/deb/ubuntu $(lsb_release -cs) main" | sudo tee /etc/apt/sources.list.d/getenvoy.list
      sudo apt update
      sudo apt install -y getenvoy-envoy
  else
      echo "Envoy already installed"
  fi
}

install_java() {
  if ! command -v java &> /dev/null
  then
      sudo apt install default-jre
  else
      echo "JRE already installed"
  fi
}

populate_env_file() {
  file_name=$1
  populate_to=$2
  substituted=$(envsubst < $file_name)
  first_line=$(echo $substituted | head -n 1)
  rest_lines=$(echo $substituted | tail -n +2)
  if ! [[ $first_line == "PREFIX="* ]]
  then
    echo "No prefix found"
    exit 1
  fi
  prefix=${first_line#"PREFIX="}
  res=''
  echo $rest_lines | while read env_var_setter
  do
     param_name="$prefix${env_var_setter%"="}"
     echo "Searching for param_name=$param_name"
     param=$(aws ssm get-parameter --with-decryption --name $param_name)
     param_value=$(echo $param | jq -r '.Parameter.Value')
     res="$res$env_var_setter$param_value\n"
  done
  (echo $res > "$populate_to")
}

install_envoy
install_java
populate_to=".env.populated"
populate_env_file "$package_name/.env" "$populate_to"
