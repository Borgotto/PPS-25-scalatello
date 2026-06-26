val scala3Version = "3.3.8"

lazy val root = project
  .in(file("."))
  .settings(
    name := "scalatello",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.2.20" % Test,
      "org.mockito" % "mockito-core" % "5.23.0" % Test
    ),
  )
