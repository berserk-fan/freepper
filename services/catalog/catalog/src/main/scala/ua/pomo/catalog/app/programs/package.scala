package ua.pomo.catalog.app

import cats.~>
import ua.pomo.common.domain.repository.PageToken

package object programs {
  implicit class MapKOps[F[_], T](value: F[T]) {
    def mapK[G[_]](f: F ~> G): G[T] = f(value)
  }

  def computeNextPageToken[T](page: PageToken.NonEmpty, res: List[T]): PageToken = {
    val nextPage = if (res.length != page.size) {
      PageToken.Empty
    } else {
      PageToken.NonEmpty(page.size, page.size + page.offset)
    }
    nextPage
  }
}
