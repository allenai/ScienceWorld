name := "virtualenv-scala"

version := "1.0"

scalaVersion := "2.12.9"

resolvers += "jetbrains-intellij-dependencies" at "https://packages.jetbrains.team/maven/p/ij/intellij-dependencies"

//sbtVersion := "1.3.9"

/*
// Pseudo-TTY library
libraryDependencies += "org.jetbrains.pty4j" % "pty4j" % "0.11.4"
//libraryDependencies += "com.sshtools" % "pty4j" % "0.7.2-SSHTOOLS"
libraryDependencies += "org.apache.logging.log4j" % "log4j-api" % "2.11.2"
libraryDependencies += "org.apache.logging.log4j" % "log4j-core" % "2.11.2"


libraryDependencies += "com.nthportal" %% "cancellable-task" % "1.0.0"

libraryDependencies += "net.liftweb" %% "lift-json" % "3.4.3"
*/

// Parser combinators
libraryDependencies += "org.scala-lang.modules" %% "scala-parser-combinators" % "2.0.0"

/*
// PDDL4J
libraryDependencies += "com.github.pellierd" % "pddl4j" % "3.8.3"

// ANTLR
libraryDependencies ++= Seq(
  "org.antlr" % "antlr4-runtime" % "4.9.1",
  "org.antlr" % "stringtemplate" % "3.2"
)
 */

// PY4J (Java <-> Python Interoperability)
libraryDependencies += "net.sf.py4j" % "py4j" % "0.10.3"

// Set main class to be the Python Interface
mainClass in Compile := Some("scienceworld.runtime.pythonapi.PythonInterface")

//pomOnly()


