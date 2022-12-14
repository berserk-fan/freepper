package ua.pomo.catalog.domain

import derevo.cats.{eqv, show}
import derevo.circe.magnolia.decoder
import derevo.derive
import io.estatico.newtype.macros.newtype
import ua.pomo.catalog.domain.image.{Image, ImageId}

import java.util.UUID

import ua.pomo.common.domain.repository._

object parameter {
  @derive(eqv, show, decoder)
  @newtype
  case class ParameterId(value: UUID)

  @derive(eqv, show, decoder)
  @newtype
  case class ParameterDisplayName(value: String)

  @derive(eqv, show, decoder)
  @newtype
  case class ParameterDescription(value: String)

  @derive(eqv, show, decoder)
  case class Parameter(
      id: ParameterId,
      displayName: ParameterDisplayName,
      image: Option[Image],
      description: ParameterDescription
  )

  @derive(eqv, show, decoder)
  case class CreateParameter(
      id: Option[ParameterId],
      displayName: ParameterDisplayName,
      image: Option[ImageId],
      description: ParameterDescription
  )

  @derive(eqv, show, decoder)
  @newtype
  case class ParameterListId(value: UUID)

  @derive(eqv, show, decoder)
  @newtype
  case class ParamListDisplayName(value: String)

  @derive(eqv, show, decoder)
  case class ParameterList(id: ParameterListId, displayName: ParamListDisplayName, parameters: List[Parameter])

  @derive(eqv, show)
  case class CreateParameterList(
      id: Option[ParameterListId],
      displayName: ParamListDisplayName,
      parameters: List[CreateParameter]
  )

  @derive(eqv, show)
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

  type ParameterListCrud = Crud.type
  type ParameterListQuery = Query[ParameterListSelector]
  type ParameterListRepository[F[_]] = Repository[F, ParameterListCrud]

  object Crud extends Crud {
    override type Create = CreateParameterList
    override type Update = UpdateParameterList
    override type Entity = ParameterList
    override type EntityId = ParameterListId
    override type Selector = ParameterListSelector
    
    implicit val co: CrudOps[ParameterListCrud] = new CrudOps[ParameterListCrud] {
      override def getIdUpdate(update: UpdateParameterList): ParameterListId = update.id

      override def getIdCreate(update: CreateParameterList): Option[ParameterListId] = update.id

      override def getIdEntity(entity: ParameterList): ParameterListId = entity.id

      override def entityDisplayName: EntityDisplayName = EntityDisplayName("parameter-list")
    }
  }
}
