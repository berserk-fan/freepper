package ua.pomo.catalog.infrastructure.persistance.postgres

import monocle.AppliedLens
import shapeless.Poly1

trait InMemoryUpdaterPoly[U] extends Poly1 {
  protected type Res[T] = Case.Aux[Option[T], Option[U => U]]
  // creates updater functon from path optinaly
  // e.g. gen[ImageListId](_.focus(_.imageList.id)) will create Option[[Product => Product]]
  protected def gen[T](f: U => AppliedLens[U, T]): Res[T] = at(
    _.map(t => (p: U) => f(p).replace(t))
  )

}
