#!/usr/bin/env bash

./scripts/wait-for-rnode.sh
./scripts/generate.sh 10

$@
