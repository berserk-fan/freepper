package com.freepper.common.infrastracture.persistance.inmemory

import monocle.Lens

trait InMemFieldDef[Field, Entity] {
  def lens: monocle.Lens[Entity, Field]
}

object InMemFieldDef {
  implicit def fromLens[Entity, Field](implicit l: monocle.Lens[Entity, Field]): InMemFieldDef[Field, Entity] =
    new InMemFieldDef[Field, Entity] {
      override def lens: Lens[Entity, Field] = l
    }
}
