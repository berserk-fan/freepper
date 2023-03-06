package com.freepper.catalog.app.programs.modifiers

import cats.Applicative
import cats.effect.Sync
import scalapb.descriptors.{FieldDescriptor, PInt, PValue}

case class PageDefaultsApplier[F[_]: Applicative](defaultSize: Int) extends MessageModifier[F] {
  override def names: List[String] = List("page_size")
  override def transformation[T <: PValue](field: FieldDescriptor, v: T): F[T] = {
    v match {
      case PInt(value) =>
        Applicative[F].pure({ val res = if (value == 0) PInt(defaultSize) else PInt(value); res.asInstanceOf[T] })
      case x => Applicative[F].pure(x)
    }
  }
}
