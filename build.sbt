import com.typesafe.sbt.SbtMultiJvm
import com.typesafe.sbt.SbtMultiJvm.MultiJvmKeys.MultiJvm

organization := "com.dispalt"
name := "akka-zk-cluster-seed"
version := "0.1.12"

scalaVersion := "2.12.12"
crossScalaVersions := Seq(scalaVersion.value, "2.13.3")

val akkaVersion = "2.6.9"
val akkaHttpVersion = "10.1.12"

val akkaDependencies = Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-tools" % akkaVersion
)

val exhibitorOptionalDependencies = Seq(
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
  "com.typesafe.akka" %% "akka-http-core" % akkaHttpVersion,
  "org.slf4j" % "log4j-over-slf4j" % "1.7.7",
  "ch.qos.logback" % "logback-classic" % "1.1.2",
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion
).map(_ % Provided)

val curatorVersion = "2.12.0"

val zkDependencies = Seq(
  "curator-framework",
  "curator-recipes"
).map { 
  "org.apache.curator" % _ % curatorVersion exclude("log4j", "log4j") exclude("org.slf4j", "slf4j-log4j12")
}
  

val testDependencies = Seq(
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
  "org.scalatest" %% "scalatest" % "3.2.0",
  "org.scalatest" %% "scalatest-wordspec" % "3.2.0",
  "com.typesafe.akka" %% "akka-multi-node-testkit" % akkaVersion,
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
  "org.slf4j" % "log4j-over-slf4j" % "1.7.7",
  "ch.qos.logback" % "logback-classic" % "1.1.2",
  "org.apache.curator" % "curator-test" % curatorVersion,
).map(_ % Test)

lazy val rootProject = (project in file(".")).
  settings(
    libraryDependencies ++= (akkaDependencies ++ exhibitorOptionalDependencies ++ zkDependencies ++ testDependencies),
    scalacOptions in Compile ++= Seq("-encoding", "UTF-8", "-deprecation", "-feature", "-unchecked", "-Xlog-reflective-calls", "-Xlint", "-language:postfixOps"),
    javacOptions in Compile ++= Seq("-Xlint:unchecked", "-Xlint:deprecation"),
    parallelExecution in Test := false,

    pomExtra := (
      <url>http://github.com/sclasen/akka-zk-cluster-seed</url>
      <licenses>
        <license>
          <name>The Apache Software License, Version 2.0</name>
          <url>http://www.apache.org/licenses/LICENSE-2.0.html</url>
          <distribution>repo</distribution>
        </license>
      </licenses>
      <scm>
        <url>git@github.com:dispalt/akka-zk-cluster-seed.git</url>
        <connection>scm:git:git@github.com:dispalt/akka-zk-cluster-seed.git</connection>
      </scm>
      <developers>
        <developer>
          <id>sclasen</id>
          <name>Scott Clasen</name>
          <url>http://github.com/sclasen</url>
        </developer>
        <developer>
          <id>dispalt</id>
          <name>Dan Di Spaltro</name>
          <url>http://github.com/dispalt</url>
        </developer>
      </developers>),

    publishTo := {
      val v = version.value
      val nexus = "https://oss.sonatype.org/"
      if (v.trim.endsWith("SNAPSHOT")) Some("snapshots" at nexus + "content/repositories/snapshots")
      else Some("releases" at nexus + "service/local/staging/deploy/maven2")
    }
  ).
  settings(Defaults.itSettings:_*).
  settings(SbtMultiJvm.multiJvmSettings:_*).
  settings(compile in MultiJvm := { (compile in MultiJvm).triggeredBy(compile in IntegrationTest).value }).
  settings(executeTests in IntegrationTest := { 
    val testResults = (executeTests in Test).value
    val multiNodeResults = (executeTests in MultiJvm).value  
    val overall =
          multiNodeResults.overall

      Tests.Output(overall,
        testResults.events ++ multiNodeResults.events,
        testResults.summaries ++ multiNodeResults.summaries)
  } ).
  configs(IntegrationTest, MultiJvm)
