#!/usr/bin/env bash
# Path to this plugin
PROTOC_GEN_TS_PATH="./node_modules/.bin/protoc-gen-ts_proto"
GOOGLE_PROTOBUF_FILES_PATH="./node_modules/google-proto-files"
# Directory to write generated code to (.js and .d.ts files)
PROTO_SERVICE_PATH="shop_service.proto"
OUTPUT_PATH="."
mkdir -p ${OUTPUT_PATH}

./node_modules/.bin/grpc_tools_node_protoc \
    --proto_path="${GOOGLE_PROTOBUF_FILES_PATH}" \
    --proto_path="./proto" \
    --plugin="${PROTOC_GEN_TS_PATH}" \
    --js_out="import_style=commonjs,binary:${OUTPUT_PATH}" \
    --ts_proto_out=${OUTPUT_PATH} \
    --ts_proto_opt=oneof=unions \
    --ts_proto_opt=outputClientImpl=false \
    --ts_proto_opt=lowerCaseServiceMethods=true \
    "${PROTO_SERVICE_PATH}"

echo "\n"
