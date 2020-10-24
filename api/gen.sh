#!/usr/bin/env bash
# Path to this plugin
PROTOC_GEN_TS_PATH="../../node_modules/.bin/protoc-gen-ts_proto"

# Directory to write generated code to (.js and .d.ts files)
OUT_DIR="./generated"

protoc --plugin="$PROTOC_GEN_TS_PATH" --ts_proto_out=. ./simple.proto

protoc \
    --plugin="${PROTOC_GEN_TS_PATH}" \
    --js_out="import_style=commonjs,binary:${OUT_DIR}" \
    --ts_proto_out="${OUT_DIR}" \
    --ts_proto_opt=oneof=unions \
    shop_service.proto
