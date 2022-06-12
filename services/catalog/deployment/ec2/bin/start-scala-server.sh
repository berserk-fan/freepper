#!/usr/bin/env bash
set -x -e

SCALA_SERVER_BIN=$1
ENV_FILE=$2

set +x
export $(cat "$ENV_FILE" | xargs)
set -x

sh "$SCALA_SERVER_BIN"