import org.scalajs.linker.interface.ModuleInitializer
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
  .settings(
    scalaJSUseMainModuleInitializer := true,
    // Compile / scalaJSMainModuleInitializer := Some(
    //   ModuleInitializer
    //     .mainMethod("secs.examples.asteroids.Asteroids", "main")
    // ),
    // Compile / mainClass := Some("secs.examples.asteroids.Asteroids"),
    libraryDependencies ++= Seq(
      ("org.scala-js" %%% "scalajs-dom" % "1.2.0").cross(CrossVersion.for3Use2_13)
    )
  )
  .dependsOn(coreJS)
