package ua.pomo.catalog.app.programs

import cats.implicits.{catsSyntaxApplicativeError, toFlatMapOps, toFunctorOps}
import cats.{Monad, MonadThrow, ~>}
import org.typelevel.log4cats.LoggerFactory
import ua.pomo.catalog.domain.image._

case class ImageServiceImpl[DBIO[_]: Monad, F[_]: MonadThrow: LoggerFactory] private (
    repo: ImageRepository[DBIO],
    dataRepository: ImageDataRepository[F],
    xa: DBIO ~> F
) extends ImageService[F] {
  override def create(image: CreateImage): F[Image] = for {
    logger <- LoggerFactory[F].create
    res <- repo
      .create(CreateImageMetadata(image.src, image.alt))
      .flatMap(id => repo.get(id))
      .mapK(xa)
    _ <- dataRepository.create(CreateImageData(image.src, image.data)) onError { case e =>
      for {
        _ <- logger.warn(e)(s"Failed to upload image to s3. Image: $image")
        _ <- repo.delete(res.id).mapK[F](xa) onError { case e =>
          logger.error(e)(s"Failed to recover from image upload failure. You should delete image $res from db")
        }
      } yield ()
    }
  } yield res

  override def get(id: ImageId): F[Image] = repo.get(id).mapK(xa)

  override def delete(imageId: ImageId): F[Unit] = for {
    logger <- LoggerFactory[F].create
    src <- deleteFromRepo(imageId).mapK(xa)
    _ <- dataRepository.delete(src) onError { case e =>
      logger.error(e)(s"Failed to delete image from s3. $src. You should delete it by yourself")
    }
  } yield ()

  private def deleteFromRepo(imageId: ImageId): DBIO[ImageSrc] = {
    repo.get(imageId).map(_.src).flatTap(_ => repo.delete(imageId))
  }

  override def query(query: ImageQuery): F[FindImagesResponse] = {
    repo
      .query(query)
      .map(images => FindImagesResponse(images, computeNextPageToken(query.page, images)))
      .mapK(xa)
  }
}
