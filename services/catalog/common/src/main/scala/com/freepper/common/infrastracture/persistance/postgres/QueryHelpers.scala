package com.freepper.common.infrastracture.persistance.postgres

import com.freepper.common.domain.crud.{PageToken, Query}
import com.freepper.common.infrastracture.persistance.GenericSelectorModule
import doobie.implicits.toSqlInterpolator
import doobie.util.update.Update0
import doobie.{Fragment, Fragments, Get, Put}
import shapeless.ops.hlist.{Drop, Mapper, ToTraversable}
import shapeless.{HList, Nat, Poly1}
import doobie.util.Write

object QueryHelpers {
  private def compileToken(token: PageToken.NonEmpty): Fragment = {
    fr"limit ${token.size} offset ${token.offset}"
  }

  def defaultDelete[T: DbFieldDef](tableName: String, id: T): List[Update0] = {
    val fd = implicitly[DbFieldDef[T]]
    if (!fd.isId) {
      throw new IllegalArgumentException(s"Not an id passed. $id")
    }
    implicit val q: Write[T] = fd.write
    val idColName = Fragment.const0(fd.name)
    val tableNameFr = Fragment.const0(tableName)

    List(sql"""delete from $tableNameFr t where t.$idColName=$id""".update)
  }

  def deleteEither[T: DbFieldDef, U: DbFieldDef](tableName: String, id: Either[T, U]): List[Update0] = {
    id match {
      case Left(value)  => defaultDelete(tableName, value)
      case Right(value) => defaultDelete(tableName, value)
    }
  }

  def defaultUpdateRaw[TT, U <: HList, U2 <: HList, U3 <: HList](
      g: U,
      id: TT,
      poly: DbUpdaterPoly,
      tableName: String
  )(implicit
      d: Drop.Aux[U, Nat._1, U2],
      m: Mapper.Aux[poly.type, U2, U3],
      l: ToTraversable.Aux[U3, List, Option[Fragment]],
      w: Put[TT]
  ): Option[Fragment] = {
    val qq: Seq[Option[Fragment]] = g.drop[Nat._1].map(poly).toList
    if (qq.forall(_.isEmpty)) {
      None
    } else {
      val setFr = Fragments.setOpt(qq: _*)
      val res = sql"""
            update ${Fragment.const0(tableName)}
            $setFr
            where id=$id
          """
      Some(res)
    }
  }

  def defaultUpdate[TT, U <: HList, U2 <: HList, U3 <: HList](
      g: U,
      id: TT,
      poly: DbUpdaterPoly,
      tableName: String
  )(implicit
      d: Drop.Aux[U, Nat._1, U2],
      m: Mapper.Aux[poly.type, U2, U3],
      l: ToTraversable.Aux[U3, List, Option[Fragment]],
      w: Put[TT]
  ): List[Update0] = {
    defaultUpdateRaw(g, id, poly, tableName).map(_.update).toList
  }

  object UniversalUpdaterPoly extends Poly1 {

    implicit def getFieldFragment[T](implicit fieldDescriptor: DbFieldDef[T]): Case.Aux[T, Option[Fragment]] = {
      implicit val q: doobie.Write[T] = fieldDescriptor.write
      val fieldNameFr = Fragment.const0(fieldDescriptor.name)
      if (fieldDescriptor.isId) {
        at(_ => None)
      } else {
        at(field => Some(fr0"$fieldNameFr = $field"))
      }
    }

    implicit def getFieldFragmentForOption[T](implicit
        fieldDescriptor: DbFieldDef[T]
    ): Case.Aux[Option[T], Option[Fragment]] = {
      implicit val q: doobie.Write[T] = fieldDescriptor.write
      val fieldNameFr = Fragment.const0(fieldDescriptor.name)
      at(x => x.map(res => fr0"$fieldNameFr = $res"))
    }

    implicit def getFieldFragmentForEither[T, U](implicit
        fieldDescriptor: DbFieldDef[T],
        fieldDescriptor2: DbFieldDef[U]
    ): Case.Aux[Either[T, U], Option[Fragment]] = {
      implicit val q: doobie.Write[T] = fieldDescriptor.write
      implicit val q2: doobie.Write[U] = fieldDescriptor2.write
      val fieldNameFr = Fragment.const0(fieldDescriptor.name)
      val fieldNameFr2 = Fragment.const0(fieldDescriptor2.name)
      at {
        case Left(value)  => Option.when(!fieldDescriptor.isId)(fr0"$fieldNameFr = $value")
        case Right(value) => Option.when(!fieldDescriptor2.isId)(fr0"$fieldNameFr2 = $value")
      }
    }
  }

  object IdFragmentGetter extends Poly1 {
    implicit def getFieldFragment[T](implicit fieldDescriptor: DbFieldDef[T]): Case.Aux[T, Option[Fragment]] = {
      implicit val q: doobie.Write[T] = fieldDescriptor.write
      val fieldNameFr = Fragment.const0(fieldDescriptor.name)
      at(field => Option.when(fieldDescriptor.isId)(fr0"$fieldNameFr = $field"))
    }

    implicit def getFieldFragmentForOption[T](implicit
        fieldDescriptor: DbFieldDef[T]
    ): Case.Aux[Option[T], Option[Fragment]] = {
      implicit val q: doobie.Write[T] = fieldDescriptor.write
      val fieldNameFr = Fragment.const0(fieldDescriptor.name)
      at(_.flatMap(field => Option.when(fieldDescriptor.isId)(fr0"$fieldNameFr = $field")))
    }

    implicit def getFieldFragmentForEither[T, U](implicit
        fieldDescriptor: DbFieldDef[T],
        fieldDescriptor2: DbFieldDef[U]
    ): Case.Aux[Either[T, U], Option[Fragment]] = {
      implicit val q: doobie.Write[T] = fieldDescriptor.write
      implicit val q2: doobie.Write[U] = fieldDescriptor2.write
      val fieldNameFr = Fragment.const0(fieldDescriptor.name)
      val fieldNameFr2 = Fragment.const0(fieldDescriptor2.name)
      at { field =>
        Option.when(fieldDescriptor.isId && fieldDescriptor2.isId) {
          field match {
            case Left(value)  => fr0"$fieldNameFr = $value"
            case Right(value) => fr0"$fieldNameFr2 = $value"
          }
        }
      }
    }
  }

  def queryForGenericSelector[T, U: Get](
      comp: DoobieCompiler[T],
      query: Query[T],
      viewName: String
  ): doobie.Query0[U] = {
    val limitOffset = QueryHelpers.compileToken(query.page)
    val where = comp.compile(viewName, query.selector)

    val viewNameFr = Fragment.const0(viewName)
    // val orderByFr = orderBy.fold(Fragment.empty)(colName => fr0"order by t.${Fragment.const0(colName)}")

    sql"""
        select t.json
        from $viewNameFr t
        where $where
        $limitOffset
        """
      .query[U]
  }

  def updateSql[U <: HList, U2 <: HList](
      fields: U,
      tableName: String
  )(implicit
      m: Mapper.Aux[UniversalUpdaterPoly.type, U, U2],
      m2: Mapper.Aux[IdFragmentGetter.type, U, U2],
      l: ToTraversable.Aux[U2, List, Option[Fragment]]
  ): Option[Fragment] = {
    val fieldFragments: List[Option[Fragment]] = fields.map(UniversalUpdaterPoly).toList
    val idFragments: List[Fragment] = fields.map(IdFragmentGetter).toList.flatten
    if (idFragments.length != 1) {
      throw new IllegalArgumentException(
        s"Received 0 or >1 id fragments. ${idFragments.map(_.internals.sql)}"
      )
    }
    val idFragment = idFragments.head

    if (fieldFragments.forall(_.isEmpty)) {
      None
    } else {
      val setFr = Fragments.setOpt(fieldFragments: _*)
      val res =
        sql"""
            update ${Fragment.const0(tableName)}
            $setFr
            where $idFragment
          """
      Some(res)
    }
  }

  def update[U <: HList, U2 <: HList](
      fields: U,
      tableName: String
  )(implicit
      m: Mapper.Aux[UniversalUpdaterPoly.type, U, U2],
      m2: Mapper.Aux[IdFragmentGetter.type, U, U2],
      l: ToTraversable.Aux[U2, List, Option[Fragment]]
  ): List[Update0] = {
    updateSql(fields, tableName).map(_.update).toList
  }
}
