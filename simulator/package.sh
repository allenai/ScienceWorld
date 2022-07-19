#!/bin/bash
sbt assembly
cp target/scala-2.12/scienceworld-scala-assembly-1.0.2.jar ../scienceworld/scienceworld-1.0.2.jar
