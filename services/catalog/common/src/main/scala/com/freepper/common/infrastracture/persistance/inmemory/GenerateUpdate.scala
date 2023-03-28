//package com.freepper.common.infrastracture.persistance.inmemory
//
//import shapeless.{Generic, HList, Poly1}
//import shapeless.ops.hlist.MapFolder
//
//trait GenerateUpdate[Entity, Update] {
//  def toUpdate(t: Entity): Update => Update
//}
//
//object GenerateUpdate {
//  trait IgnoredInUpdate[T]
//
//  object UpdaterHelper extends Poly1 {
//    implicit def ignoreCase[DomainField: IgnoredInUpdate, Entity]: Case.Aux[DomainField, Entity => Entity] = {
//      at(_ => identity[Entity])
//    }
//
//    implicit def baseCase[DomainField, Entity](implicit
//        fd: InMemFieldDef[DomainField, Entity]
//    ): Case.Aux[DomainField, Entity => Entity] = {
//      at(t => fd.lens.replace(t))
//    }
//
//    implicit def forOption[DomainField, Entity](implicit
//        base: Case.Aux[DomainField, Entity => Entity]
//    ): Case.Aux[Option[DomainField], Entity => Entity] = {
//      at(tOpt => tOpt.fold[Entity => Entity](identity)(t => base.apply(t)))
//    }
//  }
//
//  implicit def fromInMemFieldDef[Update, UpdateHList <: HList, Entity](implicit
//      g: Generic.Aux[Update, UpdateHList],
//      m: MapFolder[UpdateHList, Entity => Entity, UpdaterHelper.type]
//  ): GenerateUpdate[Update, Entity] = { (t: Update) =>
//    {
//      g.to(t).foldMap[Entity => Entity](x => x)(UpdaterHelper)((x, y) => x.andThen(y))(m)
//    }
//  }
//}
