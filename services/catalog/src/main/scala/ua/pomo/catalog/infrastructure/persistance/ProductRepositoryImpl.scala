package ua.pomo.catalog.infrastructure.persistance

import cats.data.OptionT
import cats.effect.kernel.Sync
import cats.implicits.catsSyntaxApplicativeErrorId
import doobie._
import doobie.implicits._
import doobie.postgres.implicits._
import squants.market.{Money, USD}
import ua.pomo.catalog.domain.error.NotFound
import ua.pomo.catalog.domain.model.ModelUUID
import ua.pomo.catalog.domain.product._
import ua.pomo.catalog.domain.image.{
  Image,
  ImageAlt,
  ImageId,
  ImageList,
  ImageListDisplayName,
  ImageListId,
  ImageListRepository,
  ImageSrc
}

class ProductRepositoryImpl(imageListRepo: ImageListRepository[ConnectionIO]) extends ProductRepository[ConnectionIO] {
  import ProductRepositoryImpl._

  override def create(model: CreateProduct): ConnectionIO[ProductUUID] = ???

  override def get(id: ProductUUID): ConnectionIO[Product] = {
    OptionT(find(id)).getOrElseF(NotFound("product", id).raiseError[ConnectionIO, Product])
  }

  override def find(id: ProductUUID): ConnectionIO[Option[Product]] = {
    val res = for {
      product <- OptionT(Queries.get(id).option)
      imageList <- OptionT.liftF(imageListRepo.get(product.imageList.id))
    } yield product.copy(imageList = imageList)
    res.value
  }

  override def findAll(req: FindProduct): ConnectionIO[List[Product]] = for {
    products <- Queries.
  }

  override def update(req: UpdateProduct): ConnectionIO[Int] = ???

  override def delete(id: ProductUUID): ConnectionIO[Unit] = ???
}

object ProductRepositoryImpl {
  private[persistance] object Queries {
    def compiel(alias: String, id: Option[ProductUUID]): Fragment = {

      id.fold(fr"1 = 1", fr"p.id = $id")
    }
    def get(id: ProductUUID): Query0[Product] = {
      implicit val readStandardPrice: Read[ProductStandardPrice] =
        Read[Double].map(x => ProductStandardPrice(Money(x, USD)))
      implicit val readPromoPrice: Read[ProductPromoPrice] = Read[Double].map(x => ProductPromoPrice(Money(x, USD)))

      sql"""
            select p.id, p.model_id, p.display_name, f.id, f.display_name, i.id, i.src, i.alt, s.id, s.display_name, p.image_list_id, p.price_usd, p.promo_price_usd
            from products p
                join fabrics f on p.fabric_id = f.id
                join sizes s on p.size_id = s.id
                join images i on f.image_id = i.id
            where p.id=$id
         """
        .query[(ProductUUID,
                ModelUUID,
                ProductDisplayName,
                FabricUUID,
                FabricDisplayName,
                ImageId,
                ImageSrc,
                ImageAlt,
                SizeUUID,
                SizeDisplayName,
                ImageListId,
                ProductStandardPrice,
                ProductPromoPrice)]
        .map { res =>
          Product(
            res._1,
            res._2,
            res._3,
            Fabric(res._4, res._5, Image(res._6, res._7, res._8)),
            Size(res._9, res._10),
            ImageList(
              res._11,
              ImageListDisplayName(""),
              Nil
            ),
            ProductPrice(res._12, res._13)
          )
        }
    }
  }
}
