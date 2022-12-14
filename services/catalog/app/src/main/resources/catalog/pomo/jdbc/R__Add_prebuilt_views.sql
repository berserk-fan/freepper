CREATE OR REPLACE VIEW images_prebuilt AS
select *, json_build_object('id', img.id, 'src', img.src, 'alt', img.alt) as json
from images img;

CREATE OR REPLACE VIEW parameters_prebuilt AS
select par.*,
       json_build_object(
               'id', par.id,
               'displayName', par.display_name,
               'description', par.description,
               'image', ip.json
           ) as json
from parameters par left join images_prebuilt ip on par.image_id = ip.id;

CREATE OR REPLACE VIEW parameter_lists_prebuilt AS
select pl.*,
       json_build_object(
               'id', pl.id,
               'displayName', pl.display_name,
               'parameters',
               COALESCE((select json_agg(pp.json order by pp.list_order)
                         from parameters_prebuilt pp
                         where pp.parameter_list_id = pl.id
                         ), '[]'::json)
           ) as json
from parameter_lists pl;

CREATE OR REPLACE VIEW image_lists_prebuilt AS
SELECT il.*,
       json_build_object(
               'id', il.id,
               'displayName', il.display_name,
               'images', json_agg(ip.json)
           ) as json
FROM image_lists il
         left join image_list_member ilm on il.id = ilm.image_list_id
         join images_prebuilt ip on ip.id = ilm.image_id
GROUP BY il.id
;

CREATE OR REPLACE VIEW models_prebuilt AS
select m.*,
       json_build_object(
               'id', m.id,
               'readableId', m.readable_id,
               'categoryUid', m.category_id,
               'categoryRid', c.readable_id,
               'displayName', m.display_name,
               'description', m.description,
               'minimalPrice', (select COALESCE(min(COALESCE(p.promo_price, p.price)), 0)
                                from products p
                                where p.model_id = m.id),
               'parameterLists', (select COALESCE(json_agg(plp.json), '[]')
                                  from model_parameter_lists mpl
                                           left join parameter_lists_prebuilt plp on mpl.parameter_list_id = plp.id
                                  where mpl.model_id = m.id),
               'imageList', il.json)
from models m
         left join categories c on c.id = m.category_id
         left join image_lists_prebuilt il on il.id = m.image_list_id;

CREATE OR REPLACE VIEW products_prebuilt AS
(
select p.*,
       json_build_object(
               'id', p.id,
               'modelId', m.id,
               'categoryId', m.category_id,
               'displayName', m.display_name,
               'price', json_build_object('standard', p.price, 'promo', p.promo_price),
               'imageList', il.json,
               'parameterIds', p.parameter_ids,
               'displayName', (select CONCAT(m.display_name,
                                             ' ',
                                             array_to_string(COALESCE(array_agg(ps.display_name ORDER BY ps.id),
                                                                      ARRAY []::VARCHAR[]), ' '))
                               from unnest(p.parameter_ids) pid
                                        left join parameters ps on ps.id = pid)
           ) as json
from products p
         left join models m on p.model_id = m.id
         left join image_lists_prebuilt il on p.image_list_id = il.id
    );

