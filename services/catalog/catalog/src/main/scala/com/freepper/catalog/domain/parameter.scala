package com.freepper.catalog.domain

import com.freepper.catalog.domain.image.{Image, ImageId}




import com.freepper.catalog.domain.image.{Image, ImageId}
import com.freepper.common.domain.crud._

import java.util.UUID

object parameter {


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
