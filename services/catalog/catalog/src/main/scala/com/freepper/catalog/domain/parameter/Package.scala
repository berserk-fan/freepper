package com.freepper.catalog.domain.parameter

import com.freepper.catalog.domain.image.{Image, ImageId}
import com.freepper.common.domain.crud.{Crud, Repository}
import com.freepper.common.domain.crud

import java.util.UUID

case class ParameterId(value: UUID)

case class ParameterDisplayName(value: String)

case class ParameterDescription(value: String)

case class Parameter(
    id: ParameterId,
    displayName: ParameterDisplayName,
    image: Option[Image],
    description: Option[ParameterDescription]
)

case class CreateParameter(
    id: ParameterId,
    displayName: ParameterDisplayName,
    image: Option[ImageId],
    description: Option[ParameterDescription]
)

case class ParameterListId(value: UUID)
object ParameterListId:
  given cats.Show[ParameterListId] with
    override def show(t: ParameterListId): String = t.value.toString

case class ParamListDisplayName(value: String)

case class ParameterList(id: ParameterListId, displayName: ParamListDisplayName, parameters: List[Parameter])

case class CreateParameterList(
    id: ParameterListId,
    displayName: ParamListDisplayName,
    parameters: List[CreateParameter]
)

case class UpdateParameterList(
    id: ParameterListId,
    displayName: Option[ParamListDisplayName],
    parameters: Option[List[CreateParameter]]
)

sealed trait ParameterListSelector
object ParameterListSelector {
  case object All extends ParameterListSelector
  case class IdIs(id: ParameterListId) extends ParameterListSelector
}

type ParameterListQuery = crud.Query[ParameterListSelector]
type ParameterListRepository[F[_]] = Repository[F, ParameterListCrud]

import Crud.*
type ParameterListCrud[X] = X match {
  case Create   => CreateParameterList
  case Update   => UpdateParameterList
  case Entity   => ParameterList
  case EntityId => ParameterListId
  case Query    => ParameterListQuery
  case CrudName => "parameterList"
}
