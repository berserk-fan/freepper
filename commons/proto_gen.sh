#!/usr/bin/env bash
# Path to this plugin
PROTOC_GEN_TS_PATH="./node_modules/.bin/protoc-gen-ts_proto"
GOOGLE_PROTOBUF_FILES_PATH="./node_modules/google-proto-files"
# Directory to write generated code to (.js and .d.ts files)
BASE_URL=$1
PROTO_SERVICE_PATH=$2
OUTPUT_PATH=$3
mkdir -p ${OUTPUT_PATH}

./node_modules/.bin/grpc_tools_node_protoc \
    --proto_path="${GOOGLE_PROTOBUF_FILES_PATH}" \
    --proto_path="../$BASE_URL/api" \
    --plugin="${PROTOC_GEN_TS_PATH}" \
    --js_out="import_style=commonjs,binary:${OUTPUT_PATH}" \
    --ts_proto_out=${OUTPUT_PATH} \
    --ts_proto_opt=oneof=unions \
    --ts_proto_opt=lowerCaseServiceMethods=true \
    --ts_proto_opt=outputClientImpl=grpc-web \
    --ts_proto_opt=outputEncodeMethods=false \
    --ts_proto_opt=emitImportedFiles=false \
    --ts_proto_opt=useOptionals='messages' \
    --ts_proto_opt=stringEnums=true \
    --ts_proto_opt=unrecognizedEnum=false \
    "../${BASE_URL}/api/${PROTO_SERVICE_PATH}"

