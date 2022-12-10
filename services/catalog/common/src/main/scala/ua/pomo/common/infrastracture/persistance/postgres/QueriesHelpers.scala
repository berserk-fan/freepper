package ua.pomo.common.infrastracture.persistance.postgres

import doobie.{Fragment, Fragments, Put, Update0}
import doobie.implicits.toSqlInterpolator
import shapeless.{Generic, HList, Nat}
import shapeless.ops.hlist.{Drop, Mapper, ToTraversable}
import ua.pomo.common.domain.repository.{Crud, CrudOps}

case class QueriesHelpers[T <: Crud: CrudOps]() {
  def updateQHelper[U <: HList, U2 <: HList, U3 <: HList](
      req: T#Update,
      poly: DbUpdaterPoly,
      tableName: String,
      g: Generic.Aux[T#Update, U]
  )(implicit
      d: Drop.Aux[U, Nat._1, U2],
      m: Mapper.Aux[poly.type, U2, U3],
      l: ToTraversable.Aux[U3, List, Option[Fragment]],
      w: Put[T#EntityId]
  ): Option[Update0] = {
    val qq: Seq[Option[Fragment]] = g.to(req).drop[Nat._1].map(poly).toList
    if(qq.forall(_.isEmpty)) {
      None
    } else {
      val setFr = Fragments.setOpt(qq: _*)
      val id = CrudOps[T].getIdUpdate(req)
      val res = sql"""
            update ${Fragment.const0(tableName)}
            $setFr
            where id=$id
          """.update
      Some(res)
    }
  }
}
