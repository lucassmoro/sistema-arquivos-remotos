#!/bin/bash
CURRENT_DIR=$(pwd)

mkdir -p "$CURRENT_DIR/genproto"
rm -rf "$CURRENT_DIR/genproto/*"

for file in $CURRENT_DIR/protos/*.proto; do
    echo "Compilando $file..."
    protoc -I "$CURRENT_DIR/protos" \
           --cpp_out="$CURRENT_DIR/genproto" \
           --grpc_out="$CURRENT_DIR/genproto" \
           --plugin=protoc-gen-grpc=$(which grpc_cpp_plugin) \
           "$file"
done