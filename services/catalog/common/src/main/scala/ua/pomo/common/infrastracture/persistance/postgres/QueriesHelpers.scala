package ua.pomo.common.infrastracture.persistance.postgres

import doobie.{Fragment, Fragments, Put, Update0}
import doobie.implicits.toSqlInterpolator
import shapeless.{Generic, HList, Nat}
import shapeless.ops.hlist.{Drop, Mapper, ToTraversable}

object QueriesHelpers {
  def updateQHelper[TT, U <: HList, U2 <: HList, U3 <: HList](
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
}
