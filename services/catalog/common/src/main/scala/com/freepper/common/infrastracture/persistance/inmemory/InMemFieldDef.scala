package com.freepper.common.infrastracture.persistance.inmemory

trait InMemFieldDef[T] {
  type Parent
  def lens: monocle.Lens[Parent, T]
}
