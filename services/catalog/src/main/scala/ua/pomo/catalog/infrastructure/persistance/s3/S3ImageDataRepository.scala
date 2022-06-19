package ua.pomo.catalog.infrastructure.persistance.s3

import cats.effect.Async
import cats.implicits._
import software.amazon.awssdk.auth.credentials.{AwsBasicCredentials, StaticCredentialsProvider}
import software.amazon.awssdk.core.async.AsyncRequestBody
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.model.{DeleteObjectRequest, ListObjectsV2Request, PutObjectRequest}
import software.amazon.awssdk.services.s3.S3AsyncClient
import ua.pomo.catalog.AwsConfig
import ua.pomo.catalog.domain.image._
import org.typelevel.log4cats._
import scala.jdk.CollectionConverters._

class S3ImageDataRepository[F[_]: Async: LoggerFactory] private (s3: S3AsyncClient, bucketName: String)
    extends ImageDataRepository[F] {
  override def create(image: CreateImageData): F[Unit] = {
    val request = PutObjectRequest
      .builder()
      .bucket(bucketName)
      .key(image.src.value)
      .build()
    for {
      logger <- LoggerFactory[F].create
      _ <- logger.info(s"Uploading image: ${image.src} to S3 bucket. Request: ${request.toString}")
      body = AsyncRequestBody.fromBytes(image.data.value)
      _ <- Async[F].fromCompletableFuture(Async[F].delay(s3.putObject(request, body)))
    } yield ()
  }

  override def delete(src: ImageSrc): F[Unit] = {
    val request = DeleteObjectRequest.builder().bucket(bucketName).key(src.value).build
    for {
      logger <- LoggerFactory[F].create
      _ <- logger.info(s"Deleting image: $src to S3 bucket. Request: ${request.toString}")
      _ <- Async[F].fromCompletableFuture(Async[F].delay(s3.deleteObject(request)))
    } yield ()
  }

  override def list(prefix: String): F[List[ImageSrc]] = {
    val request = ListObjectsV2Request.builder().bucket(bucketName).prefix(prefix).build
    for {
      logger <- LoggerFactory[F].create
      _ <- logger.info(s"Listing bucket objects: prefix=$prefix. Request: ${request.toString}")
      response <- Async[F].fromCompletableFuture(Async[F].delay(s3.listObjectsV2(request)))
      res = response.contents().asScala.toList.map(x => ImageSrc(x.key()))
    } yield res
  }
}

object S3ImageDataRepository {
  def apply[F[_]: Async: LoggerFactory](awsConfig: AwsConfig): F[ImageDataRepository[F]] = {
    Async[F]
      .delay {
        val region = Region.of(awsConfig.region)
        val credProvider =
          StaticCredentialsProvider.create(
            AwsBasicCredentials.create(awsConfig.accessKeyId, awsConfig.secretAccessKey)
          )
        S3AsyncClient
          .builder()
          .region(region)
          .credentialsProvider(credProvider)
          .build
      }
      .map(new S3ImageDataRepository[F](_, awsConfig.imageBucketName))
  }
}
