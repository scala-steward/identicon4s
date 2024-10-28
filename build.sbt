val Scala213 = "2.13.10"
val Scala3 = "3.1.3"

ThisBuild / tlBaseVersion := "0.0"

ThisBuild / scalaVersion := Scala213
ThisBuild / crossScalaVersions := Seq(Scala213, Scala3)
ThisBuild / licenses := Seq(License.Apache2)
ThisBuild / organization := "net.michalp"
ThisBuild / organizationName := "majk-p"
ThisBuild / developers := List(
  tlGitHubDev("majk-p", "Michał Pawlik")
)

def compilerPlugins =
  libraryDependencies ++= {
    if (scalaVersion.value.startsWith("3")) Seq()
    else Seq(compilerPlugin("org.typelevel" % "kind-projector" % "0.13.2" cross CrossVersion.full))
  }

val compilerOptions = Seq(
  scalacOptions ++= Seq(
    "-Ymacro-annotations"
  ),
  scalacOptions -= "-Xfatal-warnings",
  scalacOptions += "-Wconf:msg=\\$implicit\\$:s"
)

val Versions = new {
  val Cats = "2.7.0"
  val CatsEffect = "3.5.5"
}

val Dependencies = new {

  private val cats = Seq(
    "org.typelevel" %% "cats-core" % Versions.Cats
  )

  private val catsEffect = Seq(
    "org.typelevel" %% "cats-effect" % Versions.CatsEffect
  )

  val appDependencies =
    cats ++ catsEffect
}

lazy val root = (project in file("."))
  .settings(
    name := "identicon4s",
    libraryDependencies ++= Dependencies.appDependencies,
    compilerOptions,
    compilerPlugins
  )
