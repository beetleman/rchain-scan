#!/usr/bin/env bash
ROOT=`dirname "$0"`
$ROOT/wait-for-it.sh $RNODE__HOST:40401 -t 3600

$@
