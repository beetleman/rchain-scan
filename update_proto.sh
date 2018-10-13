#!/usr/bin/env bash

NAME=caspar_message_proto
TMP=`mktemp -d --suffix _$NAME`
RCHAIN=$TMP/rchain
SCALAPB=$TMP/scalapb
PROTO=./src/proto
PY=./generated
GIT="git clone --depth 1"

# download scalaPB and rchain repos, sources of proto files
$GIT -b release-rnode-v0.7 https://github.com/rchain/rchain.git $RCHAIN
$GIT -b master https://github.com/scalapb/ScalaPB.git $SCALAPB

# remove all proto, always start from scrach
rm -rf $PROTO
mkdir $PROTO

# copy proto files
cp $RCHAIN/models/src/main/protobuf/CasperMessage.proto $PROTO/CasperMessage.proto
cp $RCHAIN/models/src/main/protobuf/RhoTypes.proto $PROTO/RhoTypes.proto
cp $RCHAIN/node/src/main/protobuf/repl.proto $PROTO/repl.proto
cp -R $SCALAPB/protobuf/scalapb $PROTO/scalapb

rm -rf $TMP
