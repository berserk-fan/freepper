package ua.pomo.catalog.domain

import derevo.cats.{eqv, show}
import derevo.derive
import io.estatico.newtype.macros.newtype
import ua.pomo.catalog.domain.image.Image

import java.util.UUID

object param {
  @derive(eqv, show)
  case class ParameterId(value: UUID)

  @derive(eqv, show)
  @newtype
  case class ParameterDisplayName(value: String)

  @derive(eqv, show)
  case class Parameter(id: ParameterId, displayName: ParameterDisplayName, image: Image)

  @derive(eqv, show)
  @newtype
  case class ParamListId(value: UUID)

  @derive(eqv, show)
  @newtype
  case class ParamListDisplayName(value: String)

  @derive(eqv, show)
  case class ParameterList(id: ParamListId, displayName: ParamListDisplayName, parameters: List[Parameter])
}
