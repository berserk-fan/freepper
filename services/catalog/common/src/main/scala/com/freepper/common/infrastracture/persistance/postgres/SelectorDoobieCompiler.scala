//package com.freepper.common.infrastracture.persistance.postgres
//
//import com.freepper.common.infrastracture.persistance.GenericSelector
//import doobie.Fragment
//import doobie.implicits.toSqlInterpolator
//
//object SelectorDoobieCompiler {
//  def compile[T](tableName: String, fd: DbFieldDef[T], selector: GenericSelector[T]): Fragment = {
//    selector match {
//      case GenericSelector.FieldEquals(value) =>
//        val tableNameFr: Fragment = Fragment.const0(tableName)
//        val fieldDbName = Fragment.const0(fd.name(Some(value)))
//        val fieldDbRepr = fd.write(value)
//        fr0"$tableNameFr.$fieldDbName = $fieldDbRepr"
//
//      case GenericSelector.All => fr0"1 = 1"
//    }
//  }
//}
