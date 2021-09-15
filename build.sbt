// *****************************************************************************
// Projects
// *****************************************************************************

lazy val root =
  project
    .in(file("."))
    .settings(settings)
    .settings(
      libraryDependencies ++= Seq(
        library.playJson
      ),
      publishArtifact := true,
      scalaVersion := "2.13.3",
      crossScalaVersions := Seq("2.12.12", "2.13.3")
    )

// *****************************************************************************
// Library dependencies
// *****************************************************************************

lazy val library =
  new {

    object Version {
      val playJson = "2.9.0"
    }

    val playJson = "com.typesafe.play" %% "play-json" % Version.playJson

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

