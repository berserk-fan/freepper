CREATE OR REPLACE VIEW users_view AS
select *,
       json_build_object('id', u.id,
                         'uid', u.uid,
                         'displayName', u.display_name,
                         'email', u.email,
                         'emailVerificationTime', to_char(u.email_verification_time_utc, 'YYYY-MM-DD"T"HH24:MI:SS.MSZ'),
                         'imageSrc', u.image_src) as json
from users u;
