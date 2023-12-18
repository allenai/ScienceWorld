#!/usr/bin/env bash

set -euo pipefail

pushd "$(dirname "$(readlink -f "$0")")"

sbt assembly
version=$(grep version build.sbt | cut -d '"' -f2)
mv -f "target/scala-2.12/scienceworld-scala-assembly-${version}.jar" ../scienceworld/scienceworld.jar

popd
