-- Sets become first class.
--
-- Before: one exercise_entry row was one set, and the fact that several rows made up a single
-- exercise was implied by sharing an exercise_definition_id at adjacent order_index values.
-- After: an exercise_entry is one exercise within a workout and owns an ordered list of sets.
--
-- Timestamps and measurements move off exercise_entry; its start, end, duration and rest are
-- derived from its sets.

CREATE TABLE exercise_set (
    id BIGSERIAL PRIMARY KEY,
    version INTEGER,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITH TIME ZONE,
    exercise_entry_id BIGINT NOT NULL REFERENCES exercise_entry(id) ON DELETE CASCADE,
    order_index INTEGER NOT NULL,
    start_time TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    end_time TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    weight_kg DOUBLE PRECISION,
    reps INTEGER,
    failure BOOLEAN,
    partial_reps INTEGER,
    distance DOUBLE PRECISION,
    distance_unit VARCHAR(20),
    duration_seconds BIGINT,
    UNIQUE (exercise_entry_id, order_index)
);

CREATE INDEX idx_exercise_set_entry ON exercise_set (exercise_entry_id);

-- Every existing entry held exactly one set's worth of measurements, so each becomes a
-- single-set entry. Rows that were really sets of one exercise cannot be regrouped reliably
-- after the fact -- the grouping was never recorded -- so they stay as separate entries.
INSERT INTO exercise_set (version, created_at, updated_at, exercise_entry_id, order_index,
                          start_time, end_time, weight_kg, reps, failure, partial_reps,
                          distance, distance_unit, duration_seconds)
SELECT 0, e.created_at, e.updated_at, e.id, 0,
       e.start_time, e.end_time, e.weight_kg, e.reps, e.failure, e.partial_reps,
       e.distance, e.distance_unit, e.duration_seconds
FROM exercise_entry e;

-- One statement per column: multi-column DROP is Postgres-only and this has to run on H2 too.
ALTER TABLE exercise_entry DROP COLUMN start_time;
ALTER TABLE exercise_entry DROP COLUMN end_time;
ALTER TABLE exercise_entry DROP COLUMN weight_kg;
ALTER TABLE exercise_entry DROP COLUMN reps;
ALTER TABLE exercise_entry DROP COLUMN failure;
ALTER TABLE exercise_entry DROP COLUMN partial_reps;
ALTER TABLE exercise_entry DROP COLUMN distance;
ALTER TABLE exercise_entry DROP COLUMN distance_unit;
ALTER TABLE exercise_entry DROP COLUMN duration_seconds;
