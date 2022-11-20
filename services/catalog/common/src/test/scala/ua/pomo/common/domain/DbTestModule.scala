package ua.pomo.common.domain

trait DbTestModule[F[_]] {
  def dbModule: DbModule[F]
  def dbTests: RepoTestRegistry[F] 
}
