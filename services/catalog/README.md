# things to remember

1. name should be empty on creation

# secrets. env file population
.env file is populated on ec2


```postgresql
set search_path = 'catalog';

select json_agg(json_build_object('uid', pl.id, 'displayName', pl.display_name, 'parameters', (
    select json_agg(json_build_object(
                            'uid', p.id,
                            'displayName', p.display_name,
                            'image', (
                                select json_build_object('src', img.src, 'alt', img.alt, 'uid', img.id, 'data', null, 'name', concat('images/', img.id))
                                from images img
                                where img.id = p.image_id)
                        ) ORDER BY p.list_order)
    from parameters p
    where p.parameter_list_id = pl.id
)))
from parameter_lists pl;
```