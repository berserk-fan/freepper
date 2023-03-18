package com.freepper.common.infrastracture.persistance.inmemory

import shapeless._
import shapeless.ops.hlist.MapFolder

object InMemoryQueryHelpers {
  object UpdaterHelper extends Poly1 {
    implicit def baseCase[T](implicit fd: InMemFieldDef[T]): Case.Aux[T, fd.Parent => fd.Parent] = {
      at(t => fd.lens.replace(t))
    }

    implicit def forOption[T, U](implicit base: Case.Aux[T, U => U]): Case.Aux[Option[T], U => U] = {
      at(tOpt => tOpt.fold[U => U](identity)(t => base.apply(t)))
    }
  }

  class UpdatePartiallyApplied[U]() {
    def apply[T <: HList](update: T)(implicit m: MapFolder[T, U => U, UpdaterHelper.type]): U => U = {
      update.foldMap[U => U](x => x)(UpdaterHelper)((x, y) => x.andThen(y))(m)
    }
  }

  def updater[U]: UpdatePartiallyApplied[U] = new UpdatePartiallyApplied[U]

}
