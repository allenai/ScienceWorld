#!/usr/bin/env bash

set -euo pipefail

sbt assembly
version=`cat build.sbt | grep version | cut -d '"' -f2`
cp target/scala-2.12/scienceworld-scala-assembly-${version}.jar ../scienceworld/scienceworld-${version}.jar
