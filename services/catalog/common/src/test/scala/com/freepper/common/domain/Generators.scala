package com.freepper.common.domain

import org.scalacheck.Gen
import com.freepper.common.domain.crud.{Crud, Query}

trait Generators[T <: Crud] {
  def create: Gen[T#Create]

  def update: Gen[T#EntityId => T#Update]

  def genE: Gen[T#Entity]

  def id: Gen[T#EntityId]

  def query: Gen[Query[T#Selector]]
}
