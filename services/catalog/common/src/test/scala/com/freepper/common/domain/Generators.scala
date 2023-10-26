package com.freepper.common.domain

import org.scalacheck.Gen
import .Crud.*

trait Generators[C[_]] {
  def create: Gen[C[Create]]

  def update: Gen[C[EntityId] => C[Update]]

  def genE: Gen[C[Entity]]

  def id: Gen[C[EntityId]]

  def query: Gen[C[Query]]
}
