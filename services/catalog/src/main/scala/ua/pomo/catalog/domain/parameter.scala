package ua.pomo.catalog.domain

import derevo.cats.{eqv, show}
import derevo.circe.magnolia.decoder
import derevo.derive
import io.estatico.newtype.macros.newtype
import ua.pomo.catalog.domain.image.Image

import java.util.UUID

object parameter {
  @derive(eqv, show, decoder)
  @newtype
  case class ParameterId(value: UUID)

  @derive(eqv, show, decoder)
  @newtype
  case class ParameterDisplayName(value: String)

  @derive(eqv, show, decoder)
  case class Parameter(id: ParameterId,
                       parameterListId: ParameterListId,
                       displayName: ParameterDisplayName,
                       image: Image)

  @derive(eqv, show, decoder)
  @newtype
  case class ParameterListId(value: UUID)

  @derive(eqv, show, decoder)
  @newtype
  case class ParamListDisplayName(value: String)

  @derive(eqv, show, decoder)
  case class ParameterList(id: ParameterListId, displayName: ParamListDisplayName)
}
