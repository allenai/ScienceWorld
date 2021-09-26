#!/bin/bash
sbt assembly
cp target/scala-2.12/virtualenv-scala-assembly-1.0.jar python-api/
