package ua.pomo.common.domain

trait Iso[F,G] {
  def from(f: F): G
  def to(g: G): F
}

object Iso {
  implicit def iso[F]: Iso[F,F] = new Iso[F,F] {
    override def from(f: F): F = f
    override def to(g: F): F = g
  }
}
