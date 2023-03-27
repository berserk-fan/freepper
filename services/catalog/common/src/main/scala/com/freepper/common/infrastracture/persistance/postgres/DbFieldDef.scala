package com.freepper.common.infrastracture.persistance.postgres

import doobie.util.fragment.Fragment

trait DbFieldDef[T] {
  def name(t: Option[T]): String

  def write(t: T): doobie.Fragment

  def isId(t: T): Boolean
}

object DbFieldDef {
  implicit def fieldDefForOption[T](implicit fd: DbFieldDef[T]): DbFieldDef[Option[T]] = {
    new DbFieldDef[Option[T]] {
      override def name(t: Option[Option[T]]): String = fd.name(t.flatten)

      override def isId(t: Option[T]): Boolean = t.fold(false)(t => fd.isId(t))

      override def write(t: Option[T]): doobie.Fragment = {
        t.fold(Fragment.const0("NULL"))(fd.write)
      }
    }
  }

  implicit def fieldDefForEither[T, U](implicit fd1: DbFieldDef[T], fd2: DbFieldDef[U]): DbFieldDef[Either[T, U]] = {
    new DbFieldDef[Either[T, U]] {
      override def name(vOpt: Option[Either[T, U]]): String = vOpt match {
        case None    => throw new IllegalArgumentException("can't derive name for None")
        case Some(v) => v.fold(t => fd1.name(Some(t)), u => fd2.name(Some(u)))
      }

      override def write(v: Either[T, U]): Fragment = {
        v match {
          case Left(t)  => fd1.write(t)
          case Right(u) => fd2.write(u)
        }
      }

      override def isId(v: Either[T, U]): Boolean = {
        v.fold(t => fd1.isId(t), u => fd2.isId(u))
      }
    }
  }
}
