package ua.pomo.common

import cats.Monad
import cats.syntax.flatMap.toFlatMapOps
import org.scalacheck.Test.{Passed, Proved, Result}
import org.scalacheck.effect.PropF
import org.scalacheck.rng.Seed
import org.scalacheck.util.Pretty
import org.scalacheck.{Gen, Test}
import org.scalactic.source
import org.scalatest.funsuite.AnyFunSuite

trait ScalacheckEffectCheckers { self: AnyFunSuite =>
  protected def scalaCheckTestParameters: Test.Parameters = Test.Parameters.default
  protected def genParameters: Gen.Parameters = Gen.Parameters.default
  protected def scalaCheckPrettyParameters: Pretty.Params = Pretty.Params(2)
  protected def scalaCheckInitialSeed: String = Seed.random().toBase64

  protected def checkProperty[F[_]: Monad](prop: PropF[F])(implicit pos: source.Position): F[Unit] = {
    def makeSeed() =
      scalaCheckTestParameters.initialSeed.getOrElse(
        Seed.fromBase64(scalaCheckInitialSeed).get
      )

    val initialSeed = makeSeed()
    val seed: Seed = initialSeed
    val result = prop.check(scalaCheckTestParameters, genParameters.withInitialSeed(seed))

    def renderResult(r: Result): String = {
      val resultMessage = Pretty.pretty(r, scalaCheckPrettyParameters)
      if (r.passed) {
        resultMessage
      } else {
        val seedMessage =
          s"""|Failing seed: ${initialSeed.toBase64}
              |You can reproduce this failure by adding the fo
              |  override val scalaCheckInitialSeed = "${initialSeed.toBase64}
              |""".stripMargin
        seedMessage + "\n" + resultMessage
      }
    }

    result.flatMap { res =>
      res.status match {
        case Passed | Proved(_) => Monad[F].pure(())
        case _                  => fail("\n" + renderResult(res))
      }
    }
  }
}
