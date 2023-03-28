//package com.freepper.common.infrastracture.persistance.inmemory
//
//import com.freepper.common.infrastracture.persistance.GenericSelector
//import shapeless.*
//import shapeless.ops.hlist.MapFolder
//
//object InMemoryQueryHelpers extends FilterGenerator
//
//trait FilterGenerator {
////  private object InMemoryQueryCompiler {
////    def compile[DomainField, Entity](
////        inMemFdGen: DomainField => InMemFieldDef[DomainField, Entity],
////        selector: GenericSelector[DomainField],
////        entity: Entity
////    ): Boolean = selector match {
////      case GenericSelector.FieldEquals(t) => inMemFdGen(t).lens.get(entity) == t
////      case GenericSelector.All            => true
////    }
////  }
//
//
//  trait UserTo[A] {
//    def userId(id: String): A
//    def userUid(id: Int): A
//  }
//  
////  def generateFilter[T, U](entity: T, s: GenericSelector[U], getFieldDef: UserTo[InMemFieldDef[U, T]]): Boolean = {
////    InMemoryQueryCompiler.compile[U, T](getFieldDef, s, entity)
////  }
//}
