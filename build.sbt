import org.scalajs.linker.interface.ModuleInitializer
val commonSettings = Seq(
  scalaVersion := "3.0.2",
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
    // Compile / scalaJSMainModuleInitializer := Some(
    //   ModuleInitializer
    //     .mainMethod("secs.examples.asteroids.Asteroids", "main")
    // ),
    // Compile / mainClass := Some("secs.examples.asteroids.Asteroids"),
    Compile / npmDependencies ++= Seq(
      "three" -> "0.132.2"
    ),
    // webpack plugins to enable turning module imports into globals
    // https://scalacenter.github.io/scalajs-bundler/cookbook.html#global-namespace
    Compile / npmDevDependencies ++= Seq(
      "webpack-merge" -> "5.8.0",
      "imports-loader" -> "3.0.0",
      "expose-loader" -> "3.0.0"
    ),
    // custom webpack config file
    fastOptJS / webpackConfigFile := Some(
      baseDirectory.value / "my.webpack.config.js"
    ),
    // faster webpack performance
    webpack / version := "5.54.0",
    webpackBundlingMode := BundlingMode.LibraryOnly(),
    webpackEmitSourceMaps := false,
    libraryDependencies ++= Seq(
      "org.cascaval" %%% "three-typings" % "0.1.7-SNAPSHOT",
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
