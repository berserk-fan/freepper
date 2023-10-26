package com.freepper.common.domain

import cats.{data, ~>}
import cats.arrow.FunctionK
import com.freepper.common.domain

case class EntityTest[F[_], G[_], C[_]](
    repository: domain.crud.Repository[F, C],
    generators: Generators[C],
    checkers: Assertions[C],
    runner: F ~> G,
)
