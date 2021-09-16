
// *****************************************************************************
// Projects
// *****************************************************************************

val versions = Seq("2.12.12", "2.13.3")

lazy val root =
  project
    .in(file("."))
    .settings(settings)
    .settings(
      libraryDependencies ++= library.circe ++ library.testDependencies,
      publishArtifact := true,
//      scalaVersion := versions.last,
      crossScalaVersions := versions)
    .settings(
      libraryDependencies ++= {
        CrossVersion.partialVersion(scalaVersion.value) match {
          case Some((2, n)) if n <= 12 =>
            List(compilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full))
          case _                       => Nil
        }
      },
      Compile / scalacOptions ++= {
        CrossVersion.partialVersion(scalaVersion.value) match {
          case Some((2, n)) if n <= 12 => Nil
          case _                       => List("-Ymacro-annotations")
        }
      }
    )

// *****************************************************************************
// Library dependencies
// *****************************************************************************

lazy val library =
  new {

    object Version {
      val circe = "0.14.1"
      val scalaTest = "3.2.9"
    }

    val circe = Seq(
      "io.circe" %% "circe-core",
      "io.circe" %% "circe-generic",
      "io.circe" %% "circe-parser"
    ).map(_ % Version.circe)

    val testDependencies = Seq(
      "org.scalactic" %% "scalactic" % Version.scalaTest,
      "org.scalatest" %% "scalatest"% Version.scalaTest,
      "io.circe" %%"circe-literal" % Version.circe
    ).map(_ % "test")

  }

// *****************************************************************************
// Settings
// *****************************************************************************

lazy val settings =
  commonSettings ++
//    scalafmtSettings ++
    commandAliases

lazy val commonSettings =
  Seq(
    name := "scalastix",
    organization := "io.sqooba.oss",
    organizationName := "Sqooba",
    homepage := Some(url("https://github.com/Sqooba/scalastix")),
    licenses := Seq("Apache 2" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
    publishMavenStyle := true,
    publishArtifact in Test := false,
    pomIncludeRepository := { _ => false },
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value)
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },
    scmInfo := Some(
      ScmInfo(
        url("https://github.com/Sqooba/scalastix.git"),
        "scm:git@github.com:Sqooba/scalastix.git"
      )
    )
    // TODO: Expose developers
//    developers := List(
//      Developer("example", "githubUsername", "", url("githubprofile")),
//    ),
  )

//lazy val scalafmtSettings =
//  Seq(
//    scalafmtOnCompile := true
//  )

lazy val commandAliases =
  addCommandAlias("fmt", "all scalafmtSbt scalafmt test:scalafmt") ++
    addCommandAlias("check", "all scalafmtSbtCheck scalafmtCheck test:scalafmtCheck")

