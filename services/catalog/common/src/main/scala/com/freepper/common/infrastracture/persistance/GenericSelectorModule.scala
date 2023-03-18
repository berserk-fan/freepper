package com.freepper.common.infrastracture.persistance

trait GenericSelectorModule { self: Singleton =>
  type DomainField
  
  sealed trait GenericSelector
  object GenericSelector {
    case class FieldEquals(t: DomainField) extends GenericSelector
    case object All extends GenericSelector
  }
}
