val commonSettings = Seq(
  scalaVersion := "3.0.1",
  version := "0.1.0",
  scalacOptions ++= Seq(
    "-source:future"
  ),
  libraryDependencies ++= Seq(
    "org.scalameta" %%% "munit" % "0.7.28" % Test
  ),
  Test / parallelExecution := false,
  Test / testOptions += Tests.Argument(TestFrameworks.MUnit, "-b")
)

lazy val root = project
  .in(file("."))
  .aggregate(coreJS, coreJVM, examples)

lazy val core = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Full)
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
