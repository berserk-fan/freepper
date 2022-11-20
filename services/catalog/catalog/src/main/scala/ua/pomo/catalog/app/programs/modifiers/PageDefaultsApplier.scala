package ua.pomo.catalog.app.programs.modifiers

import cats.effect.Sync
import scalapb.descriptors.{FieldDescriptor, PInt, PValue}

case class PageDefaultsApplier[F[_]: Sync](defaultSize: Int) extends MessageModifier[F] {
  override def names: List[String] = List("page_size")
  override def transformation[T <: PValue](field: FieldDescriptor, v: T): F[T] = {
    v match {
      case PInt(value) =>
        Sync[F].pure({ val res = if (value == 0) PInt(defaultSize) else PInt(value); res.asInstanceOf[T] })
      case x => Sync[F].pure(x)
    }
  }
}
