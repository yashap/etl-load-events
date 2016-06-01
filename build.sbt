name := "etl-load-events"

version := "1.0"

scalaVersion := "2.11.8"

val awsVersion = "1.10.76"

val slickVersion = "3.1.1"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-json" % "2.5.1",
  "com.github.scopt" %% "scopt" % "3.4.0",

  // AWS
  "com.amazonaws" % "aws-java-sdk-lambda" % awsVersion,
  "com.amazonaws" % "aws-lambda-java-core" % "1.1.0",
  "com.amazonaws" % "aws-java-sdk-s3" % awsVersion,

  // DB
  "com.typesafe.slick" %% "slick" % slickVersion,
  "com.typesafe.slick" %% "slick-hikaricp" % slickVersion,
  "mysql" % "mysql-connector-java" % "5.1.38",
  "com.h2database" % "h2" % "1.4.191",

  // Logging
  "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
  "ch.qos.logback" % "logback-classic" % "1.1.3",

  // Test
  "org.scalatest" %% "scalatest" % "2.2.4" % "test"
)

resolvers += Resolver.sonatypeRepo("public")

testOptions in Test += Tests.Argument("-oDS")

scalacOptions ++= Seq(
  "-deprecation",
  "-feature"
)

assemblyOutputPath in assembly := new File(s"target/${name.value}.jar")
