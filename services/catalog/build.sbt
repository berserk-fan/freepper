import Dependencies._
import scalapb.GeneratorOption

ThisBuild / scalaVersion := "2.13.6"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "ua.pomo.catalog"
ThisBuild / organizationName := "Pomo"

//Env File
ThisBuild / envFileName := ".env.local"
Test / envFileName := ".env.local"
Test / envVars := (Test / envFromFile).value

//ScalaTest

Test / testOptions += Tests.Argument(TestFrameworks.ScalaTest, "-P16")

//grpc plugins

enablePlugins(Fs2Grpc)
scalapbCodeGeneratorOptions += CodeGeneratorOption.Fs2Grpc
scalapbCodeGeneratorOptions += CodeGeneratorOption.FlatPackage

//output into src_managed/main for intellij to see generated code

Compile / managedSourceDirectories -= baseDirectory.value / "target" / "scala-2.13" / "src_managed" / "main"
Compile / managedSourceDirectories += baseDirectory.value / "target" / "scala-2.13" / "src_managed" / "main" / "scala"

Compile / PB.targets := scalapbCodeGenerators.value
  .map(_.copy(outputPath = (Compile / sourceManaged).value / "scala"))
  .:+(
    scalapb.validate.gen(GeneratorOption.FlatPackage) -> (Compile / sourceManaged).value / "scala": protocbridge.Target
  )

//docker
enablePlugins(DockerPlugin)

//enable the Ash plugin, which tells our package manager to generate our binary using Ash instead of Bash
//???
enablePlugins(AshScriptPlugin)

//packaging
enablePlugins(JavaAppPackaging)
Universal / maintainer := "Dima"

Universal / packageName := {
  val buildId = java.util.UUID.randomUUID().toString
  sLog.value.log(sbt.util.Level.Info, s"Packaging scala app. Id=$buildId")
  buildId
}

Universal / mappings ++= {
  import NativePackagerHelper._
  val deploymentMappings = directory("deployment/ec2") ++ directory("deployment/common")
  val withPrefix = deploymentMappings.map { case (file, path) => (file, s"deployment/$path") }
  val other = Seq(file(".env.template") -> ".env")
  withPrefix ++ other
}

//jvm opts
Universal / javaOptions ++= Seq(
  // -J params will be added as jvm parameters
  "-J-Xms512m",
  "-J-Xmx900m"
)

//migration task
lazy val runMigrate = taskKey[Unit]("Migrates the database schema.")
addCommandAlias("run-db-migrations", "runMigrate")

//main project
lazy val root = (project in file("."))
  .settings(
    name := "catalog",
    Docker / packageName := "catalog",
    scalacOptions ++= List("-Ymacro-annotations", "-Yrangepos", "-Wconf:cat=unused:info", "-Ywarn-macros:after"),
    dockerExposedPorts ++= Seq(9090),
    dockerUpdateLatest := true,
    dockerBaseImage := "openjdk:17",
    dockerUpdateLatest := true,
    fullRunTask(runMigrate, Compile, "ua.pomo.catalog.DBMigrationsCommand"),
    runMigrate / fork := true,
    libraryDependencies ++= Seq(
      Libraries.scalaTest,
      Libraries.scalaCheck,
      Libraries.scalaTestScalaCheck,
      Libraries.grpcNetty,
      Libraries.grpcNettyShaded,
      Libraries.scalaPbCommonProtosProtobuf,
      Libraries.scalaPbCommonProtosScala,
      Libraries.scalaPbValidation,
      Libraries.gprcServerReflection,
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
      CompilerPlugin.kindProjector,
      CompilerPlugin.betterMonadicFor,
      CompilerPlugin.semanticDB
    )
  )

addCommandAlias("runLinter", ";scalafixAll --rules RemoveUnused")
