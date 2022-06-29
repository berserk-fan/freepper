import sbt._

object Dependencies {
  object V {
    val cats = "2.7.0"
    val catsEffect = "3.3.3"
    val catsRetry = "3.1.0"
    val circe = "0.14.1"
    val derevo = "0.13.0"
    val javaxCrypto = "1.0.1"
    val fs2 = "3.1.3"
    val log4cats = "2.3.1"
    val monocle = "3.0.0-M6"
    val newtype = "0.4.4"
    val refined = "0.9.28"
    val redis4cats = "1.0.0"
    val skunk = "0.2.3"
    val squants = "1.8.3"

    val betterMonadicFor = "0.3.1"
    val kindProjector = "0.13.2"
    val logback = "1.2.10"
    val organizeImports = "0.6.0"
    val semanticDB = "4.4.31"

    val weaver = "0.7.6"

    val scalaLogging = "3.9.4"
    val pureConfig = "0.17.1"
    val scalaTest = "3.2.9"
    val scalaPb = "2.5.0-2"
    val doobie = "1.0.0-RC1"
    val flyway = "7.2.0"
    val typeSafeConfig = "1.4.1"
    val postgresJdbcDriver = "42.3.1"
    val scalaCheck = "1.15.4"
    val scalaTestScalaCheck = "3.2.9.0"

    val parserCombinators = "2.1.0"
    val flexMark = "0.35.10"
    val awsS3Sdk = "2.17.214"
  }

  object Libraries {
    def circe(artifact: String): ModuleID = "io.circe" %% s"circe-$artifact" % V.circe
    def derevo(artifact: String): ModuleID = "tf.tofu" %% s"derevo-$artifact" % V.derevo

    lazy val scalaTest = "org.scalatest" %% "scalatest" % V.scalaTest % Test
    lazy val scalaCheck = "org.scalacheck" %% "scalacheck" % V.scalaCheck % Test
    lazy val scalaTestHtml = "com.vladsch.flexmark" % "flexmark-all" % V.flexMark % Test
    lazy val scalaTestScalaCheck = "org.scalatestplus" %% "scalacheck-1-15" % V.scalaTestScalaCheck % Test

    lazy val grpcNetty = "io.grpc" % "grpc-netty" % scalapb.compiler.Version.grpcJavaVersion
    lazy val grpcNettyShaded = "io.grpc" % "grpc-netty-shaded" % scalapb.compiler.Version.grpcJavaVersion

    // (optional) If you need scalapb/scalapb.proto or anything from
    // google/protobuf/*.proto
    lazy val gprcServerReflection = "io.grpc" % "grpc-services" % scalapb.compiler.Version.grpcJavaVersion
    lazy val scalaPbCommonProtosProtobuf =
      "com.thesamet.scalapb.common-protos" %% "proto-google-common-protos-scalapb_0.11" % V.scalaPb % "protobuf"
    lazy val scalaPbCommonProtosScala =
      "com.thesamet.scalapb.common-protos" %% "proto-google-common-protos-scalapb_0.11" % V.scalaPb
    lazy val scalaPbValidation =
      "com.thesamet.scalapb" %% "scalapb-validate-core" % scalapb.validate.compiler.BuildInfo.version % "protobuf"

    lazy val doobieCore = "org.tpolecat" %% "doobie-core" % V.doobie
    lazy val doobiePostgres = "org.tpolecat" %% "doobie-postgres" % V.doobie
    lazy val doobieScalaTest = "org.tpolecat" %% "doobie-scalatest" % V.doobie
    lazy val postgresJdbcDriver = "org.postgresql" % "postgresql" % V.postgresJdbcDriver

    lazy val flyway = "org.flywaydb" % "flyway-core" % V.flyway
    lazy val typesafeConfig = "com.typesafe" % "config" % V.typeSafeConfig
    lazy val pureConfig = "com.github.pureconfig" %% "pureconfig" % V.pureConfig
    lazy val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % V.scalaLogging
    lazy val log4Cats = "org.typelevel" %% "log4cats-core" % V.log4cats
    lazy val log4CatsSlf4j = "org.typelevel" %% "log4cats-slf4j" % V.log4cats
    lazy val logbackClassic = "ch.qos.logback" % "logback-classic" % V.logback

    val cats = "org.typelevel" %% "cats-core" % V.cats
    val catsEffect = "org.typelevel" %% "cats-effect" % V.catsEffect
    val catsRetry = "com.github.cb372" %% "cats-retry" % V.catsRetry
    val squants = "org.typelevel" %% "squants" % V.squants
    val fs2 = "co.fs2" %% "fs2-core" % V.fs2

    val refinedCore = "eu.timepit" %% "refined" % V.refined
    val refinedCats = "eu.timepit" %% "refined-cats" % V.refined

    val newtype = "io.estatico" %% "newtype" % V.newtype

    val monocleCore = "com.github.julien-truffaut" %% "monocle-core" % V.monocle
    val monocleMacro = "com.github.julien-truffaut" %% "monocle-macro" % V.monocle

    val parserCombinators = "org.scala-lang.modules" %% "scala-parser-combinators" % V.parserCombinators

    val circeCore = circe("core")
    val circeGeneric = circe("generic")
    val circeParser = circe("parser")
    val circeRefined = circe("refined")

    val derevoCore = derevo("core")
    val derevoCats = derevo("cats")
    val derevoCirce = derevo("circe-magnolia")

    val awsS3Sdk = "software.amazon.awssdk" % "s3" % V.awsS3Sdk

  }

  object CompilerPlugin {
    val betterMonadicFor = compilerPlugin(
      "com.olegpy" %% "better-monadic-for" % V.betterMonadicFor
    )
    val kindProjector = compilerPlugin(
      "org.typelevel" % "kind-projector" % V.kindProjector cross CrossVersion.full
    )
    val semanticDB = compilerPlugin(
      "org.scalameta" % "semanticdb-scalac" % V.semanticDB cross CrossVersion.full
    )
  }
}
