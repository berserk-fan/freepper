package ua.pomo.catalog.ext

import scala.annotation.implicitNotFound

import derevo.{Derivation, NewTypeDerivation}

//wtf is this
trait Derive[F[_]] extends Derivation[F] with NewTypeDerivation[F] {
  def instance(implicit ev: OnlyNewtypes): Nothing = ev.absurd

  @implicitNotFound("Only newtypes instances can be derived")
  abstract final class OnlyNewtypes {
    def absurd: Nothing = ???
  }
}
