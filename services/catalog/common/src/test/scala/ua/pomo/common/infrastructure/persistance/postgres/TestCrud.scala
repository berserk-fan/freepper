package ua.pomo.common.infrastructure.persistance.postgres

import ua.pomo.common.domain.repository.Crud

abstract class TestCrud[T <: Crud]()(implicit c: ValueOf[T]) { self: Singleton =>
  type Fixture
  final val crud: T = c.value
}
