-- Function: public.clone_schema(text, text, boolean)

-- DROP FUNCTION public.clone_schema(text, text, boolean);

CREATE OR REPLACE FUNCTION public.clone_schema(
    source_schema text,
    dest_schema text,
    include_recs boolean)
    RETURNS void AS
$BODY$
    --  Initial code by Emanuel '3manuek'
--  Last revision 2017-04-17 by Melvin Davidson
--  Added SELECT REPLACE for schema views
--
--  This function will clone all sequences, tables, indexes, rules, triggers,
--  data(optional), views & functions from any existing schema to a new schema
-- SAMPLE CALL:
-- SELECT clone_schema('public', 'new_schema', TRUE);

DECLARE
    src_oid          oid;
    tbl_oid          oid;
    func_oid         oid;
    con_oid          oid;
    v_path           text;
    v_func           text;
    v_args           text;
    v_conname        text;
    v_rule           text;
    v_trig           text;
    object           text;
    buffer           text;
    srctbl           text;
    default_         text;
    v_column         text;
    qry              text;
    dest_qry         text;
    v_def            text;
    v_stat           integer;
    seqval           bigint;
    sq_last_value    bigint;
    sq_max_value     bigint;
    sq_start_value   bigint;
    sq_increment_by  bigint;
    sq_min_value     bigint;
    sq_cache_value   bigint;
    sq_log_cnt       bigint;
    sq_is_called     boolean;
    sq_is_cycled     boolean;
    sq_cycled        char(10);

BEGIN
    RAISE NOTICE 'CLONING SCHEMA % to %', source_schema, dest_schema;
    -- Check that source_schema exists
    SELECT oid INTO src_oid
    FROM pg_namespace
    WHERE nspname = quote_ident(source_schema);
    IF NOT FOUND
    THEN
        RAISE NOTICE 'source schema % does not exist!', source_schema;
        RETURN ;
    END IF;

    -- Check that dest_schema does not yet exist
    PERFORM nspname
    FROM pg_namespace
    WHERE nspname = quote_ident(dest_schema);
    IF FOUND
    THEN
        RAISE NOTICE 'dest schema % already exists!', dest_schema;
        RETURN ;
    END IF;

    EXECUTE 'CREATE SCHEMA ' || quote_ident(dest_schema) ;

    -- Add schema comment
    SELECT description INTO v_def
    FROM pg_description
    WHERE objoid = src_oid
      AND objsubid = 0;
    IF FOUND
    THEN
        EXECUTE 'COMMENT ON SCHEMA ' || quote_ident(dest_schema) || ' IS ' || quote_literal(v_def);
    END IF;

    -- Create sequences
    -- TODO: Find a way to make this sequence's owner is the correct table.
    FOR object IN
        SELECT sequence_name::text
        FROM information_schema.sequences
        WHERE sequence_schema = quote_ident(source_schema)
        LOOP
            EXECUTE 'CREATE SEQUENCE ' || quote_ident(dest_schema) || '.' || quote_ident(object);
            srctbl := quote_ident(source_schema) || '.' || quote_ident(object);

            EXECUTE 'SELECT last_value, max_value, start_value, increment_by, min_value, cache_value, log_cnt, is_cycled, is_called
              FROM ' || quote_ident(source_schema) || '.' || quote_ident(object) || ';'
                INTO sq_last_value, sq_max_value, sq_start_value, sq_increment_by, sq_min_value, sq_cache_value, sq_log_cnt, sq_is_cycled, sq_is_called ;

            IF sq_is_cycled
            THEN
                sq_cycled := 'CYCLE';
            ELSE
                sq_cycled := 'NO CYCLE';
            END IF;

            EXECUTE 'ALTER SEQUENCE '   || quote_ident(dest_schema) || '.' || quote_ident(object)
                        || ' INCREMENT BY ' || sq_increment_by
                        || ' MINVALUE '     || sq_min_value
                        || ' MAXVALUE '     || sq_max_value
                        || ' START WITH '   || sq_start_value
                        || ' RESTART '      || sq_min_value
                        || ' CACHE '        || sq_cache_value
                        || sq_cycled || ' ;' ;

            buffer := quote_ident(dest_schema) || '.' || quote_ident(object);
            IF include_recs
            THEN
                EXECUTE 'SELECT setval( ''' || buffer || ''', ' || sq_last_value || ', ' || sq_is_called || ');' ;
            ELSE
                EXECUTE 'SELECT setval( ''' || buffer || ''', ' || sq_start_value || ', ' || sq_is_called || ');' ;
            END IF;

            -- add sequence comments
            SELECT oid INTO tbl_oid
            FROM pg_class
            WHERE relkind = 'S'
              AND relnamespace = src_oid
              AND relname = quote_ident(object);

            SELECT description INTO v_def
            FROM pg_description
            WHERE objoid = tbl_oid
              AND objsubid = 0;

            IF FOUND
            THEN
                EXECUTE 'COMMENT ON SEQUENCE ' || quote_ident(dest_schema) || '.' || quote_ident(object)
                            || ' IS ''' || v_def || ''';';
            END IF;


        END LOOP;

-- Create tables
    FOR object IN
        SELECT TABLE_NAME::text
        FROM information_schema.tables
        WHERE table_schema = quote_ident(source_schema)
          AND table_type = 'BASE TABLE'

        LOOP
            buffer := quote_ident(dest_schema) || '.' || quote_ident(object);
            EXECUTE 'CREATE TABLE ' || buffer || ' (LIKE ' || quote_ident(source_schema) || '.' || quote_ident(object)
                || ' INCLUDING ALL)';

            -- Add table comment
            SELECT oid INTO tbl_oid
            FROM pg_class
            WHERE relkind = 'r'
              AND relnamespace = src_oid
              AND relname = quote_ident(object);

            SELECT description INTO v_def
            FROM pg_description
            WHERE objoid = tbl_oid
              AND objsubid = 0;

            IF FOUND
            THEN
                EXECUTE 'COMMENT ON TABLE ' || quote_ident(dest_schema) || '.' || quote_ident(object)
                            || ' IS ''' || v_def || ''';';
            END IF;

            IF include_recs
            THEN
                -- Insert records from source table
                EXECUTE 'INSERT INTO ' || buffer || ' SELECT * FROM ' || quote_ident(source_schema) || '.' || quote_ident(object) || ';';
            END IF;

            FOR v_column, default_ IN
                SELECT column_name::text,
                       REPLACE(column_default::text, quote_ident(source_schema) || '.', quote_ident(dest_schema) || '.' )
                FROM information_schema.COLUMNS
                WHERE table_schema = dest_schema
                  AND TABLE_NAME = object
                  AND column_default LIKE 'nextval(%' || quote_ident(source_schema) || '%::regclass)'
                LOOP
                    EXECUTE 'ALTER TABLE ' || buffer || ' ALTER COLUMN ' || v_column || ' SET DEFAULT ' || default_;

                END LOOP;

        END LOOP;

    -- set column statistics
    FOR tbl_oid, srctbl IN
        SELECT oid, relname
        FROM pg_class
        WHERE relnamespace = src_oid
          AND relkind = 'r'

        LOOP

            FOR v_column, v_stat IN
                SELECT attname, attstattarget
                FROM pg_attribute
                WHERE attrelid = tbl_oid
                  AND attnum > 0

                LOOP

                    buffer := quote_ident(dest_schema) || '.' || quote_ident(srctbl);
--      RAISE EXCEPTION 'ALTER TABLE % ALTER COLUMN % SET STATISTICS %', buffer, v_column, v_stat::text;
                    EXECUTE 'ALTER TABLE ' || buffer || ' ALTER COLUMN ' || quote_ident(v_column) || ' SET STATISTICS ' || v_stat || ';';

                END LOOP;
        END LOOP;

--  add FK constraint
    FOR qry IN
        SELECT 'ALTER TABLE ' || quote_ident(dest_schema) || '.' || quote_ident(rn.relname)
                   || ' ADD CONSTRAINT ' || quote_ident(ct.conname) || ' ' || replace(pg_get_constraintdef(ct.oid), quote_ident(source_schema) || '.', quote_ident(dest_schema) || '.') || ';'
        FROM pg_constraint ct
                 JOIN pg_class rn ON rn.oid = ct.conrelid
        WHERE connamespace = src_oid
          AND rn.relkind = 'r'
          AND ct.contype = 'f'

        LOOP
            EXECUTE qry;

        END LOOP;

    -- Add constraint comment
    FOR con_oid IN
        SELECT oid
        FROM pg_constraint
        WHERE conrelid = tbl_oid

        LOOP
            SELECT conname INTO v_conname
            FROM pg_constraint
            WHERE oid = con_oid;

            SELECT description INTO v_def
            FROM pg_description
            WHERE objoid = con_oid;

            IF FOUND
            THEN
                EXECUTE 'COMMENT ON CONSTRAINT ' || v_conname || ' ON ' || quote_ident(dest_schema) || '.' || quote_ident(object)
                            || ' IS ''' || v_def || ''';';
            END IF;

        END LOOP;


-- Create views
    FOR object IN
        SELECT table_name::text,
               view_definition
        FROM information_schema.views
        WHERE table_schema = quote_ident(source_schema)

        LOOP
            buffer := quote_ident(dest_schema) || '.' || quote_ident(object);
            SELECT view_definition INTO v_def
            FROM information_schema.views
            WHERE table_schema = quote_ident(source_schema)
              AND table_name = quote_ident(object);

            SELECT REPLACE(v_def, source_schema, dest_schema) INTO v_def;
--    RAISE NOTICE 'view def, % , source % , dest % ',  v_def, source_schema, dest_schema;

            EXECUTE 'CREATE OR REPLACE VIEW ' || buffer || ' AS ' || v_def || ';' ;

            -- Add comment
            SELECT oid INTO tbl_oid
            FROM pg_class
            WHERE relkind = 'v'
              AND relnamespace = src_oid
              AND relname = quote_ident(object);

            SELECT description INTO v_def
            FROM pg_description
            WHERE objoid = tbl_oid
              AND objsubid = 0;

            IF FOUND
            THEN
                EXECUTE 'COMMENT ON VIEW ' || quote_ident(dest_schema) || '.' || quote_ident(object)
                            || ' IS ' || quote_literal(v_def);
            END IF;


        END LOOP;

-- Create functions
    FOR func_oid IN
        SELECT oid, proargnames
        FROM pg_proc
        WHERE pronamespace = src_oid

        LOOP
            SELECT pg_get_functiondef(func_oid) INTO qry;
            SELECT proname, oidvectortypes(proargtypes) INTO v_func, v_args
            FROM pg_proc
            WHERE oid = func_oid;
            SELECT replace(qry, quote_ident(source_schema) || '.', quote_ident(dest_schema) || '.') INTO dest_qry;
            EXECUTE dest_qry;

            -- Add function comment
            SELECT description INTO v_def
            FROM pg_description
            WHERE objoid = func_oid
              AND objsubid = 0;

            IF FOUND
            THEN
--        RAISE NOTICE 'func_oid %, object %,  v_args %',  func_oid::text, quote_ident(object), v_args;
                EXECUTE 'COMMENT ON FUNCTION ' || quote_ident(dest_schema) || '.' || quote_ident(v_func) || '(' || v_args || ')'
                            || ' IS ' || quote_literal(v_def) ||';' ;
            END IF;


        END LOOP;

    -- add Rules
    FOR v_def IN
        SELECT definition
        FROM pg_rules
        WHERE schemaname = quote_ident(source_schema)

        LOOP

            IF v_def IS NOT NULL
            THEN
                SELECT replace(v_def, 'TO ', 'TO ' || quote_ident(dest_schema) || '.') INTO v_def;
                EXECUTE ' ' || v_def;
            END IF;
        END LOOP;

    -- add triggers
    FOR v_def IN
        SELECT pg_get_triggerdef(oid)
        FROM pg_trigger
        WHERE tgname NOT LIKE 'RI_%'
          AND tgrelid IN (SELECT oid
                          FROM pg_class
                          WHERE relkind = 'r'
                            AND relnamespace = src_oid)

        LOOP

            SELECT replace(v_def, ' ON ' || quote_ident(source_schema) || '.', ' ON ' || quote_ident(dest_schema) || '.') INTO dest_qry;
            EXECUTE dest_qry;

        END LOOP;
    --  Disable inactive triggers
    --  D = disabled
    FOR tbl_oid IN
        SELECT oid
        FROM pg_trigger
        WHERE tgenabled = 'D'
          AND tgname NOT LIKE 'RI_%'
          AND tgrelid IN (SELECT oid
                          FROM pg_class
                          WHERE relkind = 'r'
                            AND relnamespace = src_oid)
        LOOP
            SELECT t.tgname, c.relname INTO object, srctbl
            FROM pg_trigger t
                     JOIN pg_class c ON c.oid = t.tgrelid
            WHERE t.oid = tbl_oid;

            IF FOUND
            THEN
                EXECUTE 'ALTER TABLE ' || dest_schema || '.' || srctbl || ' DISABLE TRIGGER ' || object || ';';
            END IF;

        END LOOP;

    -- Add index comment

    FOR tbl_oid IN
        SELECT oid
        FROM pg_class
        WHERE relkind = 'i'
          AND relnamespace = src_oid

        LOOP

            SELECT relname INTO object
            FROM pg_class
            WHERE oid = tbl_oid;
            SELECT description INTO v_def
            FROM pg_description
            WHERE objoid = tbl_oid
              AND objsubid = 0;

            IF FOUND
            THEN
                EXECUTE 'COMMENT ON INDEX ' || quote_ident(dest_schema) || '.' || quote_ident(object)
                            || ' IS ''' || v_def || ''';';
            END IF;

        END LOOP;

    -- add rule comments
    FOR con_oid IN
        SELECT oid, *
        FROM pg_rewrite
        WHERE rulename <> '_RETURN'::name

        LOOP

            SELECT rulename, ev_class INTO v_rule, tbl_oid
            FROM pg_rewrite
            WHERE oid = con_oid;

            SELECT relname INTO object
            FROM pg_class
            WHERE oid = tbl_oid
              AND relkind = 'r';

            SELECT description INTO v_def
            FROM pg_description
            WHERE objoid = con_oid
              AND objsubid = 0;

            IF FOUND
            THEN
                EXECUTE 'COMMENT ON RULE ' || v_rule || ' ON ' || quote_ident(dest_schema) || '.' || object || ' IS ' || quote_literal(v_def);
            END IF;

        END LOOP;

    -- add trigger comments
    FOR con_oid IN
        SELECT oid, *
        FROM pg_trigger
        WHERE tgname NOT LIKE 'RI_%'

        LOOP

            SELECT tgname, tgrelid INTO v_trig, tbl_oid
            FROM pg_trigger
            WHERE oid = con_oid;

            SELECT relname INTO object
            FROM pg_class
            WHERE oid = tbl_oid
              AND relkind = 'r';

            SELECT description INTO v_def
            FROM pg_description
            WHERE objoid = con_oid
              AND objsubid = 0;

            IF FOUND
            THEN
                EXECUTE 'COMMENT ON TRIGGER ' || v_trig || ' ON ' || quote_ident(dest_schema) || '.' || object || ' IS ' || quote_literal(v_def);
            END IF;

        END LOOP;

    RETURN;


END;

$BODY$
    LANGUAGE plpgsql VOLATILE
                     COST 100;

COMMENT ON FUNCTION public.clone_schema(text, text, boolean) IS 'Duplicates sequences, tables, indexes, rules, triggers, data(optional),
     views & functions from the source schema to the destination schema';