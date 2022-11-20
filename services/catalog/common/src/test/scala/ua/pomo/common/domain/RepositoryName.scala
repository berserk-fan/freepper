package ua.pomo.common.domain

import derevo.cats.{eqv, show}
import derevo.derive

@derive(eqv, show)
case class RepositoryName(value: String)
