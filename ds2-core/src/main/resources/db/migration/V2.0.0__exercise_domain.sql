CREATE TABLE workout_type (
    id BIGSERIAL PRIMARY KEY,
    version INTEGER,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITH TIME ZONE,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE exercise_definition (
    id BIGSERIAL PRIMARY KEY,
    version INTEGER,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITH TIME ZONE,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,
    UNIQUE (name, type)
);

CREATE TABLE workout (
    id BIGSERIAL PRIMARY KEY,
    version INTEGER,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITH TIME ZONE,
    user_id BIGINT NOT NULL REFERENCES users(id),
    workout_type_id BIGINT NOT NULL REFERENCES workout_type(id),
    status VARCHAR(20) NOT NULL,
    start_time TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    end_time TIMESTAMP(6) WITH TIME ZONE
);

CREATE TABLE exercise_entry (
    id BIGSERIAL PRIMARY KEY,
    version INTEGER,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITH TIME ZONE,
    workout_id BIGINT NOT NULL REFERENCES workout(id),
    exercise_definition_id BIGINT NOT NULL REFERENCES exercise_definition(id),
    order_index INTEGER NOT NULL,
    start_time TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    end_time TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    weight_kg DOUBLE PRECISION,
    reps INTEGER,
    failure BOOLEAN,
    partial_reps INTEGER,
    distance DOUBLE PRECISION,
    distance_unit VARCHAR(20),
    duration_seconds BIGINT
);
