import org.scalajs.linker.interface.ModuleInitializer
val commonSettings = Seq(
  scalaVersion := "3.1.0",
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
  .aggregate(coreJS, coreJVM, examplesJS, examplesJVM)

lazy val core = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("core"))
  .settings(commonSettings)
  .jsSettings()
  .jvmSettings()

lazy val coreJS = core.js
lazy val coreJVM = core.jvm

lazy val osName = System.getProperty("os.name") match {
  case n if n.startsWith("Linux")   => "linux"
  case n if n.startsWith("Mac")     => "mac"
  case n if n.startsWith("Windows") => "win"
  case _                            => throw new Exception("Unknown platform!")
}

lazy val examples = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Full)
  .in(file("examples"))
  .settings(commonSettings)
  .jsConfigure(_.enablePlugins(ScalaJSBundlerPlugin))
  .jsSettings(
    scalaJSUseMainModuleInitializer := true,
    Compile / mainClass := Some("secs.examples.asteroids.Asteroids"),
    // Compile / mainClass := Some("secs.examples.retained.Retained"),
    Compile / npmDependencies ++= Seq(
      "three" -> "0.131.0",
      "camera-controls" -> "1.33.0"
    ),
    // faster webpack performance
    webpack / version := "5.54.0",
    webpackBundlingMode := BundlingMode.LibraryOnly(),
    webpackEmitSourceMaps := false,
    libraryDependencies ++= Seq(
      "io.github.dcascaval" %%% "scala-threejs-facades" % "0.131.0",
      ("org.scala-js" %%% "scalajs-dom" % "1.2.0").cross(CrossVersion.for3Use2_13)
    )
  )
  .jvmSettings(
    Compile / run / fork := true,
    scalacOptions ++= Seq(
      "-language:adhocExtensions"
    ),
    libraryDependencies ++= Seq(
      "org.scalafx" %% "scalafx" % "16.0.0-R24"
    ) ++
      Seq("base", "controls", "fxml", "graphics", "media", "swing", "web").map(m =>
        "org.openjfx" % s"javafx-$m" % "16" classifier osName
      )
  )
  .dependsOn(core)

lazy val examplesJS = examples.js
lazy val examplesJVM = examples.jvm
