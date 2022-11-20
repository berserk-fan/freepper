package ua.pomo.catalog.infrastructure.persistance.postgres

import doobie.Fragment
import doobie.implicits.toSqlInterpolator

object ParameterListRepository {
  object Queries {
    def jsonList(modelId: String): Fragment = {
      val imagesQuery = DeprecatedMethods.json("par.image_id")

      fr"""COALESCE( (
             select json_agg(json_build_object(
                 'id', pl.id, 
                 'displayName', pl.display_name, 
                 'parameters', COALESCE( (
                     select json_agg(json_build_object(
                         'id', par.id,
                         'displayName', par.display_name ,
                         'image', $imagesQuery) ORDER BY par.list_order)
                     from parameters par where par.parameter_list_id = pl.id), '[]'))
                 )
             from parameter_lists pl join model_parameter_lists mpl on pl.id = mpl.parameter_list_id 
             WHERE mpl.model_id = ${Fragment.const0(modelId)}
        ), '[]')"""
    }
  }
}
