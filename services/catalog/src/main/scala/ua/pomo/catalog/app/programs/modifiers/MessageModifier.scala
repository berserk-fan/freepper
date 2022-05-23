package ua.pomo.catalog.app.programs.modifiers

import cats.effect.Sync
import cats.implicits.{toFunctorOps, toTraverseOps}
import cats.kernel.Monoid
import scalapb.descriptors.{FieldDescriptor, PMessage, PValue}
import scalapb.{GeneratedMessage, GeneratedMessageCompanion}

abstract class MessageModifier[F[_]: Sync]() {
  // names to which we should apply transformation, applying defaults, resolving names
  def names: List[String]
  def transformation[T <: PValue](field: FieldDescriptor, v: T): F[T]

  def modify[T <: GeneratedMessage: GeneratedMessageCompanion](m: T): F[T] = {
    val c = implicitly[GeneratedMessageCompanion[T]]
    modifyHelper(m.toPMessage).map(c.messageReads.read)
  }

  private def modifyHelper(v: PMessage): F[PMessage] = {
    v.value
      .map[F[(FieldDescriptor, PValue)]] { case (descriptor, value) =>
        value match {
          case nested: PMessage => modifyHelper(nested).map((descriptor, _))
          case _ if names.contains(descriptor.name) =>
            transformation(descriptor, value).map((descriptor, _))
          case x => Sync[F].pure((descriptor, x))
        }
      }
      .toSeq
      .sequence
      .map(x => PMessage(x.toMap))
  }
}

object MessageModifier {
  implicit def monoidInstanse[F[_]: Sync]: Monoid[MessageModifier[F]] = new Monoid[MessageModifier[F]] {
    override def empty: MessageModifier[F] = new MessageModifier[F] {
      override def names: List[String] = List()
      override def transformation[T <: PValue](field: FieldDescriptor, v: T): F[T] = ???
    }

    override def combine(x: MessageModifier[F], y: MessageModifier[F]): MessageModifier[F] = new MessageModifier[F] {
      override def names: List[String] = x.names ++ y.names

      override def transformation[T <: PValue](field: FieldDescriptor, v: T): F[T] = {
        if (x.names.contains(field.name)) {
          x.transformation(field, v)
        } else {
          y.transformation(field, v)
        }
      }
    }
  }
}
