val commonSettings = Seq(
  scalaVersion := "3.0.1",
  version := "0.1.0",
  scalacOptions ++= Seq(
    "-source:future"
  )
)

lazy val root = project
  .in(file("."))
  .aggregate(coreJS, coreJVM, examples)

lazy val core = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("core"))
  .settings(commonSettings)
  .jsSettings()
  .jvmSettings()

lazy val coreJS = core.js
lazy val coreJVM = core.jvm

lazy val examples = project
  .in(file("examples"))
  .enablePlugins(ScalaJSPlugin)
  .settings(commonSettings)
  .dependsOn(coreJS)
