package ua.pomo.catalog.domain

import derevo.cats.{eqv, show}
import derevo.circe.magnolia.decoder
import derevo.derive
import io.estatico.newtype.macros.newtype
import ua.pomo.catalog.domain.image.{Image, ImageId}
import ua.pomo.common.domain.crud._

import java.util.UUID

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
      description: Option[ParameterDescription]
  )

  @derive(eqv, show, decoder)
  case class CreateParameter(
      id: ParameterId,
      displayName: ParameterDisplayName,
      image: Option[ImageId],
      description: Option[ParameterDescription]
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
      id: ParameterListId,
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

    implicit val co: RepoOps[ParameterListCrud] = new RepoOps[ParameterListCrud] {
      override def getIdUpdate(update: UpdateParameterList): ParameterListId = update.id

      override def getIdCreate(create: CreateParameterList): ParameterListId = create.id

      override def getIdEntity(entity: ParameterList): ParameterListId = entity.id

      override def entityDisplayName: EntityDisplayName = Entity.ParameterList.name
    }
  }
}
