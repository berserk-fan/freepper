package ua.pomo.common.infrastructure.persistance.postgres

import ua.pomo.common.domain.repository.Crud

trait TestCrud extends Crud { self: Singleton =>
  type Fixture
}
