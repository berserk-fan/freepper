package ua.pomo.common.infrastructure.persistance.postgres

import org.scalacheck.Gen
import ua.pomo.common.domain.repository.Query

trait Generators[T <: TestCrud] {
  def create(f: T#Fixture): Gen[T#Create]

  def update(f: T#Fixture): Gen[T#Update]

  def genE(f: T#Fixture): Gen[T#Entity]

  def id: Gen[T#EntityId]

  def query(f: T#Fixture): Gen[Query[T#Selector]]
}
