name := """play-scala-first"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  specs2 % Test,
  "org.xerial" % "sqlite-jdbc" % "3.8.0-SNAPSHOT",
  "com.typesafe.play" %% "anorm" % "2.4.0",
  "org.postgresql" % "postgresql" % "9.4-1204-jdbc42"
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
resolvers += "SQLite-JDBC Repository" at "https://oss.sonatype.org/content/repositories/snapshots" 

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator


//fork in run := true