package ua.pomo.common.domain

import ua.pomo.common.domain.repository.Crud
import ua.pomo.common.domain.repository.CrudOps
import ua.pomo.common.domain

trait EntityTest[F[_], T <: Crud] {
  def repository: domain.repository.Repository[F, T]
  def generators: Generators[T]
  def repositoryName: RepositoryName
  def checkers: Checkers[T]
  def co: CrudOps[T]
}

trait Checkers[T <: Crud] {
  def checkersUpdate(c: T#Update, v: T#Entity): Any
  def checkersCreate(c: T#Create, v: T#Entity): Any
}
