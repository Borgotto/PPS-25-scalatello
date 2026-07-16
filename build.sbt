val scala3Version = "3.3.8"

lazy val root = project
  .in(file("."))
  .settings(
    name := "scalatello",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.2.20" % Test,
      "org.mockito" % "mockito-scala_3" % "2.2.1" % Test,
      "org.mockito" % "mockito-scala-scalatest_3" % "2.2.1" % Test,
      "org.scala-lang" %% "toolkit" % "0.9.2",
      "org.jline" % "jline" % "4.3.1"
    ),
  )
