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

    val logback = "1.2.10"
    val organizeImports = "0.6.0"
    val semanticDB = "4.4.31"

    val weaver = "0.7.6"

    val scalaLogging = "3.9.4"
    val pureConfig = "0.17.2"
    val scalaTest = "3.2.9"
    val scalacheckEffectVersion = "1.0.4"
    val scalaCheck = "1.15.4"
    val scalaTestScalaCheck = "3.2.9.0"

    val scalaPb = "2.5.0-2"
    val scalaPbValidation = scalapb.validate.compiler.BuildInfo.version
    val doobie = "1.0.0-RC1"
    val flyway = "7.2.0"
    val typeSafeConfig = "1.4.1"
    val postgresJdbcDriver = "42.3.1"

    val parserCombinators = "2.1.0"
    val flexMark = "0.35.10"
    val awsS3Sdk = "2.17.214"
    val jose4J = "0.9.2"
  }

  object Libraries {
    def circe(artifact: String): ModuleID = "io.circe" %% s"circe-$artifact" % V.circe

    lazy val scalaTest = "org.scalatest" %% "scalatest" % V.scalaTest % Test
    lazy val scalaCheck = "org.scalacheck" %% "scalacheck" % V.scalaCheck % Test
    lazy val scalaTestHtml = "com.vladsch.flexmark" % "flexmark-all" % V.flexMark % Test
    lazy val scalaTestScalaCheck = "org.scalatestplus" %% "scalacheck-1-15" % V.scalaTestScalaCheck % Test
    lazy val scalaCheckEffect = "org.typelevel" %% "scalacheck-effect-munit" % V.scalacheckEffectVersion % Test

    lazy val grpcNetty = "io.grpc" % "grpc-netty" % scalapb.compiler.Version.grpcJavaVersion
    lazy val grpcNettyShaded = "io.grpc" % "grpc-netty-shaded" % scalapb.compiler.Version.grpcJavaVersion

    // (optional) If you need scalapb/scalapb.proto or anything from
    // google/protobuf/*.proto
    lazy val gprcServerReflection = "io.grpc" % "grpc-services" % scalapb.compiler.Version.grpcJavaVersion
    lazy val scalaPbCommonProtosProtobuf =
      "com.thesamet.scalapb.common-protos" %% "proto-google-common-protos-scalapb_0.11" % V.scalaPb % "protobuf"
    lazy val scalaPbCommonProtosScala =
      "com.thesamet.scalapb.common-protos" %% "proto-google-common-protos-scalapb_0.11" % V.scalaPb
    lazy val scalaPbValidationProto =
      "com.thesamet.scalapb" %% "scalapb-validate-core" % V.scalaPbValidation % "protobuf"
    lazy val scalaPbValidationScala =
      "com.thesamet.scalapb" %% "scalapb-validate-core" % V.scalaPbValidation

    lazy val doobieCore = "org.tpolecat" %% "doobie-core" % V.doobie
    lazy val doobiePostgres = "org.tpolecat" %% "doobie-postgres" % V.doobie
    lazy val doobieScalaTest = "org.tpolecat" %% "doobie-scalatest" % V.doobie
    lazy val postgresJdbcDriver = "org.postgresql" % "postgresql" % V.postgresJdbcDriver

    lazy val flyway = "org.flywaydb" % "flyway-core" % V.flyway
    lazy val typesafeConfig = "com.typesafe" % "config" % V.typeSafeConfig
    lazy val pureConfig = "com.github.pureconfig" %% "pureconfig-core" % V.pureConfig
    lazy val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % V.scalaLogging
    lazy val log4Cats = "org.typelevel" %% "log4cats-core" % V.log4cats
    lazy val log4CatsSlf4j = "org.typelevel" %% "log4cats-slf4j" % V.log4cats
    lazy val logbackClassic = "ch.qos.logback" % "logback-classic" % V.logback

    lazy val cats = "org.typelevel" %% "cats-core" % V.cats
    lazy val catsEffect = "org.typelevel" %% "cats-effect" % V.catsEffect
    lazy val catsRetry = "com.github.cb372" %% "cats-retry" % V.catsRetry
    lazy val squants = "org.typelevel" %% "squants" % V.squants
    lazy val fs2 = "co.fs2" %% "fs2-core" % V.fs2

    lazy val refinedCore = "eu.timepit" %% "refined" % V.refined
    lazy val refinedCats = "eu.timepit" %% "refined-cats" % V.refined

    lazy val monocleCore = "com.github.julien-truffaut" %% "monocle-core" % V.monocle
    lazy val monocleMacro = "com.github.julien-truffaut" %% "monocle-macro" % V.monocle

    lazy val parserCombinators = "org.scala-lang.modules" %% "scala-parser-combinators" % V.parserCombinators

    lazy val circeCore = circe("core")
    lazy val circeGeneric = circe("generic")
    lazy val circeParser = circe("parser")
    lazy val circeRefined = circe("refined")
    lazy val awsS3Sdk = "software.amazon.awssdk" % "s3" % V.awsS3Sdk
    lazy val jose4JJwt = "org.bitbucket.b_c" % "jose4j" % V.jose4J
  }
}
