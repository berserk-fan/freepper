import Dependencies._
import scalapb.GeneratorOption

lazy val globalResources = file("app/src/main/resources")

ThisBuild / scalaVersion := "3.2.2"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.freepper"
ThisBuild / organizationName := "Freepper"

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
  "-J-Xmx900m",
  // add cats effect tracing
  "-Dcats.effect.stackTracingMode=full",
  "-Dcats.effect.traceBufferSize=1024"
)

lazy val runLinter = taskKey[Unit]("Run linter")

lazy val test = taskKey[Unit]("Test unit and it tests")
lazy val testEvil = taskKey[Unit]("Test external services like communication to S3")

ThisBuild / envFileName := (baseDirectory.value / ".env.local").toString

lazy val commonLibs = Seq(
  scalacOptions ~= (_.filterNot(Set("-explain", "-explain-types"))), // removes explain option
  scalacOptions ++= List("-source:3.0-migration", "-language:adhocExtensions"),
  libraryDependencies ++= Seq(
    Libraries.shapeless,
    Libraries.scalaTest,
    Libraries.scalaTestHtml,
    Libraries.scalaCheck,
    Libraries.scalaTestScalaCheck,
    Libraries.scalaCheckEffect,
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
    Libraries.squants,
    Libraries.monocleCore,
    Libraries.monocleMacro,
    Libraries.postgresJdbcDriver,
    Libraries.parserCombinators,
    Libraries.log4CatsSlf4j,
    Libraries.log4Cats,

    // grpc
    Libraries.grpcNetty,
    Libraries.grpcNettyShaded,
    Libraries.scalaPbCommonProtosProtobuf,
    Libraries.scalaPbCommonProtosScala,
    Libraries.scalaPbValidationProto,
    Libraries.scalaPbValidationScala,
    Libraries.gprcServerReflection,

    // crypto
    Libraries.jose4JJwt
  )
)

addCommandAlias("fix", "; all compile:scalafix test:scalafix; all scalafmtSbt scalafmtAll")

lazy val scalaFixSettings = Seq(
  semanticdbEnabled := true,
  semanticdbVersion := scalafixSemanticdb.revision,
  ThisBuild / scalafixScalaBinaryVersion := CrossVersion.binaryScalaVersion(scalaVersion.value),
  ThisBuild / scalafixDependencies ++= List(
    "com.github.liancheng" %% "organize-imports" % "0.6.0",
    "com.github.vovapolu" %% "scaluzzi" % "0.1.23"
  )
)

lazy val grpcServiceSettings = commonLibs ++ scalaFixSettings ++ Seq(
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

lazy val auth = commonServiceSettings(project in file("auth"))
  .settings(
    grpcServiceSettings,
    name := "auth"
  )
  .dependsOn(common % "test->test;compile->compile")

lazy val app = (project in file("app"))
  .settings(
    scalaFixSettings,
    name := "app"
  )
  .dependsOn(catalog, common, auth)

//migration task
lazy val runMigrate = inputKey[Unit]("Migrates the database schema.")

lazy val runServer = taskKey[Unit]("Run sbt server")

lazy val root = (project in file("."))
  .settings(
    // CODE
    scalaFixSettings,
    name := "freepper",
    // migration
    fullRunInputTask(runMigrate, Compile, "ua.freepper.app.DBMigrationsCommand"),
    runMigrate / fork := true,
    // server
    fullRunTask(runServer, Compile, "ua.freepper.app.Server"),
    runServer / fork := true
  )
  .dependsOn(app)
  .aggregate(app, common, catalog, auth)
