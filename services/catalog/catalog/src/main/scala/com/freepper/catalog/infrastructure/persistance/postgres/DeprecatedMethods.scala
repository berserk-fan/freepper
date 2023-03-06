package com.freepper.catalog.infrastructure.persistance.postgres

import doobie._
import doobie.implicits._

object DeprecatedMethods {
  def jsonList(imageListId: String): Fragment = {
    sql"""
        (select COALESCE((
            select json_agg(json_build_object('id', img.id, 'src', img.src, 'alt', img.alt) ORDER BY ilm.list_order)
            from images img join image_list_member ilm on img.id = ilm.image_id
            where ilm.image_list_id = ${Fragment.const0(imageListId)}
        ), '[]'::json))
      """
  }
}
