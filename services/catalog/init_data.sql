set search_path = 'catalog';
INSERT INTO categories (description, display_name, readable_id)
VALUES ('Кровати описание', 'Кровати', 'beds'),`
       ('Аммуниция описание', 'Аммуниция', 'ammo');

insert INTO models (readable_id, display_name, description, category_id, image_list_id, parameter_list_ids)
