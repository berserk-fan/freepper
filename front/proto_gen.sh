#!/usr/bin/env bash
set -e -x
# Path to this plugin
PROTOC_GEN_TS_PATH="./node_modules/.bin/protoc-gen-ts_proto"
GOOGLE_PROTOBUF_FILES_PATH="./node_modules/google-proto-files"
# Directory to write generated code to (.js and .d.ts files)
PROTO_FOLDER=$1
OUTPUT_PATH=apis
mkdir -p ${OUTPUT_PATH}
mkdir -p ${OUTPUT_PATH}/validate

wget -O "${OUTPUT_PATH}/validate/validate.proto" 'https://raw.githubusercontent.com/envoyproxy/protoc-gen-validate/main/validate/validate.proto'

./node_modules/.bin/grpc_tools_node_protoc \
    --proto_path="${OUTPUT_PATH}" \
    --proto_path="${GOOGLE_PROTOBUF_FILES_PATH}" \
    --proto_path="$PROTO_FOLDER" \
    --plugin="${PROTOC_GEN_TS_PATH}" \
    --js_out="import_style=commonjs,binary:$OUTPUT_PATH" \
    --ts_proto_opt=esModuleInterop=true \
    --ts_proto_out=$OUTPUT_PATH \
    --ts_proto_opt=nestJs=false \
    --ts_proto_opt=addGrpcMetadata=false \
    --ts_proto_opt=fileSuffix=.pb \
    --ts_proto_opt=oneof=unions \
    --ts_proto_opt=lowerCaseServiceMethods=true \
    --ts_proto_opt=outputClientImpl=grpc-web \
    --ts_proto_opt=outputJsonMethods=false \
    --ts_proto_opt=useOptionals='messages' \
    --ts_proto_opt=stringEnums=true \
    --ts_proto_opt=unrecognizedEnum=false \
    $(find $PROTO_FOLDER -iname "*.proto")

