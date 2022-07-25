import Dependencies._
import scalapb.GeneratorOption

lazy val globalResources = file("app/src/main/resources")

ThisBuild / scalaVersion := "2.13.6"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "ua.pomo"
ThisBuild / organizationName := "Pomo"

// set the prompt (for this build) to include the project id.
ThisBuild / shellPrompt := { state => Project.extract(state).currentRef.project + "> " }

//packaging
enablePlugins(JavaAppPackaging)
Universal / maintainer := "Dima"

Universal / packageName := {
  val buildId = java.util.UUID.randomUUID().toString
  sLog.value.log(sbt.util.Level.Info, s"Packaging scala app. Id=$buildId")
  buildId
}

Universal / mappings ++= {
  val envFile = ".env.prod"
  List(file(envFile) -> envFile)
}

//jvm opts
Universal / javaOptions ++= Seq(
  // -J params will be added as jvm parameters
  "-J-Xms512m",
  "-J-Xmx900m"
)

lazy val runLinter = taskKey[Unit]("Run linter")

lazy val test = taskKey[Unit]("Test unit and it tests")
lazy val testEvil = taskKey[Unit]("Test external services like communication to S3")

ThisBuild / envFileName := (baseDirectory.value / ".env.local").toString

lazy val commonLibs = Seq(
  scalacOptions ++= List(
    "-Ymacro-annotations",
    "-Yrangepos",
    "-Wconf:cat=unused:info",
    "-Ywarn-macros:after"
  ),
  libraryDependencies ++= Seq(
    Libraries.scalaTest,
    Libraries.scalaTestHtml,
    Libraries.scalaCheck,
    Libraries.scalaTestScalaCheck,
    Libraries.doobieCore,
    Libraries.doobiePostgres,
    Libraries.doobieScalaTest,
    Libraries.typesafeConfig,
    Libraries.pureConfig,
    Libraries.scalaLogging,
    Libraries.flyway,
    Libraries.logbackClassic,
    Libraries.cats,
    Libraries.catsEffect,
    Libraries.catsRetry,
    Libraries.circeCore,
    Libraries.circeGeneric,
    Libraries.circeParser,
    Libraries.circeRefined,
    Libraries.derevoCore,
    Libraries.derevoCats,
    Libraries.derevoCirce,
    Libraries.newtype,
    Libraries.refinedCore,
    Libraries.refinedCats,
    Libraries.squants,
    Libraries.monocleCore,
    Libraries.monocleMacro,
    Libraries.postgresJdbcDriver,
    Libraries.parserCombinators,
    Libraries.log4CatsSlf4j,
    Libraries.log4Cats,

    // plugins
    CompilerPlugin.kindProjector,
    CompilerPlugin.betterMonadicFor,
    CompilerPlugin.semanticDB,

    // grpc
    Libraries.grpcNetty,
    Libraries.grpcNettyShaded,
    Libraries.scalaPbCommonProtosProtobuf,
    Libraries.scalaPbCommonProtosScala,
    Libraries.scalaPbValidation,
    Libraries.gprcServerReflection
  )
)

lazy val grpcServiceSettings = commonLibs ++ Seq(
  // CODE
  runLinter := {
    scalafixAll.toTask(" --rules RemoveUnused").value
  },

  // RESOURCES
  Runtime / unmanagedResourceDirectories += globalResources,
  Test / unmanagedResourceDirectories += globalResources,

  // COMMANDS
  Test / test := (Test / testOnly).toTask(" *Test *IT").value,
  Test / testEvil := (Test / testOnly).toTask(" *Evil").value,

  // SCALA TEST
  Test / testOptions += Tests.Argument(TestFrameworks.ScalaTest, "-P32"),
  Test / testOptions += Tests.Argument(
    TestFrameworks.ScalaTest,
    "-h",
    (baseDirectory.value.toPath / "test-results").toString,
    "-o"
  ),

  // GRPC
  scalapbCodeGeneratorOptions += CodeGeneratorOption.Fs2Grpc,
  scalapbCodeGeneratorOptions += CodeGeneratorOption.FlatPackage,
  // output into src_managed/main for intellij to see generated code
  Compile / managedSourceDirectories := List(
    (Compile / sourceManaged).value / "scala"
  ),
  Compile / PB.targets := scalapbCodeGenerators.value
    .map(_.copy(outputPath = (Compile / sourceManaged).value / "scala"))
    .:+(
      scalapb.validate
        .gen(GeneratorOption.FlatPackage) -> (Compile / sourceManaged).value / "scala": protocbridge.Target
    )
)

val commonServiceSettings = (p: Project) => p.enablePlugins(Fs2Grpc)

//main project

lazy val common = (project in file("common"))
  .settings(
    commonLibs,
    name := "common"
  )

lazy val catalog = commonServiceSettings(project in file("catalog"))
  .settings(
    grpcServiceSettings,
    name := "catalog",
    libraryDependencies ++= Seq(
      Libraries.awsS3Sdk
    )
  )
  .dependsOn(common % "test->test;compile->compile")

lazy val app = (project in file("app"))
  .settings(
    name := "app"
  )
  .dependsOn(catalog, common)

//migration task
lazy val runMigrate = inputKey[Unit]("Migrates the database schema.")

lazy val runServer = taskKey[Unit]("Run sbt server")
lazy val root = (project in file("."))
  .settings(
    name := "pomo",
    // migration
    fullRunInputTask(runMigrate, Compile, "ua.pomo.app.DBMigrationsCommand"),
    runMigrate / fork := true,
    // server
    fullRunTask(runServer, Compile, "ua.pomo.app.Server"),
    runServer / fork := true
  )
  .dependsOn(app)
  .aggregate(app, common, catalog)
