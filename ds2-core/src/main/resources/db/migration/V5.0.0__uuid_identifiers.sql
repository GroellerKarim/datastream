-- Identity moves from database-generated BIGSERIAL to a UUID the domain assigns to itself.
--
-- A domain object now has an id from the moment it is constructed, before anything is written.
-- That is impossible with a sequence, whose value only exists after the INSERT, and it is what
-- lets the application refer to an object it has just created without a round trip to storage.
--
-- Existing rows keep their relationships: each table gets a fresh UUID per row, and every foreign
-- key is rewritten by joining on the old BIGINT value before it is dropped. gen_random_uuid() is
-- used only for that backfill -- the column carries no DEFAULT afterwards, because an id arriving
-- without one would mean the application failed to assign it, and that should fail loudly.

-- 1. New identifiers alongside the old ones.
ALTER TABLE users               ADD COLUMN uuid_id UUID NOT NULL DEFAULT gen_random_uuid();
ALTER TABLE workout_type        ADD COLUMN uuid_id UUID NOT NULL DEFAULT gen_random_uuid();
ALTER TABLE exercise_definition ADD COLUMN uuid_id UUID NOT NULL DEFAULT gen_random_uuid();
ALTER TABLE workout             ADD COLUMN uuid_id UUID NOT NULL DEFAULT gen_random_uuid();
ALTER TABLE exercise_entry      ADD COLUMN uuid_id UUID NOT NULL DEFAULT gen_random_uuid();
ALTER TABLE exercise_set        ADD COLUMN uuid_id UUID NOT NULL DEFAULT gen_random_uuid();

-- 2. Foreign keys, resolved through the old integer keys while they still exist.
ALTER TABLE workout ADD COLUMN uuid_user_id UUID;
UPDATE workout w SET uuid_user_id = u.uuid_id FROM users u WHERE u.id = w.user_id;
ALTER TABLE workout ALTER COLUMN uuid_user_id SET NOT NULL;

ALTER TABLE workout ADD COLUMN uuid_workout_type_id UUID;
UPDATE workout w SET uuid_workout_type_id = t.uuid_id FROM workout_type t WHERE t.id = w.workout_type_id;
ALTER TABLE workout ALTER COLUMN uuid_workout_type_id SET NOT NULL;

ALTER TABLE exercise_entry ADD COLUMN uuid_workout_id UUID;
UPDATE exercise_entry e SET uuid_workout_id = w.uuid_id FROM workout w WHERE w.id = e.workout_id;
ALTER TABLE exercise_entry ALTER COLUMN uuid_workout_id SET NOT NULL;

ALTER TABLE exercise_entry ADD COLUMN uuid_exercise_definition_id UUID;
UPDATE exercise_entry e SET uuid_exercise_definition_id = d.uuid_id
    FROM exercise_definition d WHERE d.id = e.exercise_definition_id;
ALTER TABLE exercise_entry ALTER COLUMN uuid_exercise_definition_id SET NOT NULL;

ALTER TABLE exercise_set ADD COLUMN uuid_exercise_entry_id UUID;
UPDATE exercise_set s SET uuid_exercise_entry_id = e.uuid_id FROM exercise_entry e WHERE e.id = s.exercise_entry_id;
ALTER TABLE exercise_set ALTER COLUMN uuid_exercise_entry_id SET NOT NULL;

-- 3. Drop the old columns, children first. Dropping a column takes its constraints and indexes
--    with it, so the primary keys, foreign keys, the exercise_set (entry, order_index) uniqueness
--    and idx_exercise_set_entry all disappear here and are rebuilt in step 5.
ALTER TABLE exercise_set   DROP COLUMN exercise_entry_id;
ALTER TABLE exercise_entry DROP COLUMN workout_id;
ALTER TABLE exercise_entry DROP COLUMN exercise_definition_id;
ALTER TABLE workout        DROP COLUMN user_id;
ALTER TABLE workout        DROP COLUMN workout_type_id;

ALTER TABLE exercise_set        DROP COLUMN id;
ALTER TABLE exercise_entry      DROP COLUMN id;
ALTER TABLE workout             DROP COLUMN id;
ALTER TABLE exercise_definition DROP COLUMN id;
ALTER TABLE workout_type        DROP COLUMN id;
ALTER TABLE users               DROP COLUMN id;

-- 4. The UUID columns take over the old names.
ALTER TABLE users               RENAME COLUMN uuid_id TO id;
ALTER TABLE workout_type        RENAME COLUMN uuid_id TO id;
ALTER TABLE exercise_definition RENAME COLUMN uuid_id TO id;
ALTER TABLE workout             RENAME COLUMN uuid_id TO id;
ALTER TABLE exercise_entry      RENAME COLUMN uuid_id TO id;
ALTER TABLE exercise_set        RENAME COLUMN uuid_id TO id;

ALTER TABLE workout        RENAME COLUMN uuid_user_id TO user_id;
ALTER TABLE workout        RENAME COLUMN uuid_workout_type_id TO workout_type_id;
ALTER TABLE exercise_entry RENAME COLUMN uuid_workout_id TO workout_id;
ALTER TABLE exercise_entry RENAME COLUMN uuid_exercise_definition_id TO exercise_definition_id;
ALTER TABLE exercise_set   RENAME COLUMN uuid_exercise_entry_id TO exercise_entry_id;

ALTER TABLE users               ALTER COLUMN id DROP DEFAULT;
ALTER TABLE workout_type        ALTER COLUMN id DROP DEFAULT;
ALTER TABLE exercise_definition ALTER COLUMN id DROP DEFAULT;
ALTER TABLE workout             ALTER COLUMN id DROP DEFAULT;
ALTER TABLE exercise_entry      ALTER COLUMN id DROP DEFAULT;
ALTER TABLE exercise_set        ALTER COLUMN id DROP DEFAULT;

-- 5. Rebuild the constraints that went with the dropped columns.
ALTER TABLE users               ADD CONSTRAINT users_pkey PRIMARY KEY (id);
ALTER TABLE workout_type        ADD CONSTRAINT workout_type_pkey PRIMARY KEY (id);
ALTER TABLE exercise_definition ADD CONSTRAINT exercise_definition_pkey PRIMARY KEY (id);
ALTER TABLE workout             ADD CONSTRAINT workout_pkey PRIMARY KEY (id);
ALTER TABLE exercise_entry      ADD CONSTRAINT exercise_entry_pkey PRIMARY KEY (id);
ALTER TABLE exercise_set        ADD CONSTRAINT exercise_set_pkey PRIMARY KEY (id);

ALTER TABLE workout ADD CONSTRAINT workout_user_id_fkey
    FOREIGN KEY (user_id) REFERENCES users (id);
ALTER TABLE workout ADD CONSTRAINT workout_workout_type_id_fkey
    FOREIGN KEY (workout_type_id) REFERENCES workout_type (id);
ALTER TABLE exercise_entry ADD CONSTRAINT exercise_entry_workout_id_fkey
    FOREIGN KEY (workout_id) REFERENCES workout (id);
ALTER TABLE exercise_entry ADD CONSTRAINT exercise_entry_exercise_definition_id_fkey
    FOREIGN KEY (exercise_definition_id) REFERENCES exercise_definition (id);
ALTER TABLE exercise_set ADD CONSTRAINT exercise_set_exercise_entry_id_fkey
    FOREIGN KEY (exercise_entry_id) REFERENCES exercise_entry (id) ON DELETE CASCADE;

ALTER TABLE exercise_set ADD CONSTRAINT exercise_set_entry_order_key UNIQUE (exercise_entry_id, order_index);
CREATE INDEX idx_exercise_set_entry ON exercise_set (exercise_entry_id);
