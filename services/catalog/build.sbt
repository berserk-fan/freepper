import Dependencies._

ThisBuild / scalaVersion := "2.13.6"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "ua.pomo.catalog"
ThisBuild / organizationName := "Pomo"

enablePlugins(Fs2Grpc)
scalapbCodeGeneratorOptions += CodeGeneratorOption.Fs2Grpc
scalapbCodeGeneratorOptions += CodeGeneratorOption.FlatPackage

//output into src_managed/main for intellij to see generated code
Compile / PB.targets := Fs2GrpcPlugin.autoImport.scalapbCodeGenerators.value
  .map(_.copy(outputPath = (Compile / sourceManaged).value))

lazy val root = (project in file("."))
  .settings(
    name := "catalog",
    libraryDependencies ++= Seq(
      Libraries.scalaTest,
      Libraries.grpcNetty,
      Libraries.grpcNettyShaded,
      Libraries.scalaPbCommonProtosProtobuf,
      Libraries.scalaPbCommonProtosScala,
      Libraries.doobieCore,
      Libraries.typesafeConfig,
      Libraries.pureConfig,
      Libraries.scalaLogging,
      Libraries.flyway,
      Libraries.logbackRuntime,
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
      CompilerPlugin.kindProjector,
      CompilerPlugin.betterMonadicFor,
      CompilerPlugin.semanticDB
    )
  )

addCommandAlias("runLinter", ";scalafixAll --rules OrganizeImports")
