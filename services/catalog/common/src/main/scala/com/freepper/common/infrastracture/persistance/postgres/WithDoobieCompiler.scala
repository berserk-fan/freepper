package com.freepper.common.infrastracture.persistance.postgres

import com.freepper.common.infrastracture.persistance.GenericSelectorModule
import doobie.Fragment
import doobie.implicits.toSqlInterpolator

trait DoobieCompiler[T] {
  def compile(tableName: String, selector: T): Fragment
}

trait ToDbFieldDef[T] {
  def apply[U <: T](t: U): DbFieldDef[U]
}

object DoobieCompilerImpl {
  def apply(
      module: GenericSelectorModule
  )(toDbFieldDef: ToDbFieldDef[module.DomainField]): DoobieCompiler[module.GenericSelector] = {
    (tableName: String, selector: module.GenericSelector) =>
      {
        selector match {
          case module.GenericSelector.FieldEquals(value) =>
            val fd = toDbFieldDef.apply(value)
            val tableNameFr: Fragment = Fragment.const0(tableName)
            val fieldDbName = Fragment.const0(fd.name)
            val fieldDbRepr = fd.write.toFragment(value)
            fr0"$tableNameFr.$fieldDbName = $fieldDbRepr"

          case module.GenericSelector.All => fr0"1 = 1"
        }
      }
  }
}
