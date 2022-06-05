#!/usr/bin/env bash

ENVOY_LOG_FILE=$1
ENVOY_CONFIG_FILE=$2

envoy -c "$ENVOY_CONFIG_FILE" --log-path "$ENVOY_LOG_FILE"