CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    version INTEGER,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITH TIME ZONE,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE workout_type (
    id BIGSERIAL PRIMARY KEY,
    version INTEGER,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITH TIME ZONE,
    name VARCHAR(255) UNIQUE
);

CREATE TABLE workout (
    id BIGSERIAL PRIMARY KEY,
    version INTEGER,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITH TIME ZONE,
    user_id BIGINT NOT NULL REFERENCES users(id),
    duration BIGINT NOT NULL,
    start_time TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    end_time TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    workout_type_id BIGINT NOT NULL REFERENCES workout_type(id),
    average_rest_time DOUBLE PRECISION
);

CREATE TABLE exercise_definition (
    id BIGSERIAL PRIMARY KEY,
    version INTEGER,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITH TIME ZONE,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(255) NOT NULL,
    UNIQUE (name, type)
);

CREATE TABLE exercise_record (
    id BIGSERIAL PRIMARY KEY,
    version INTEGER,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITH TIME ZONE,
    exercise_definition_id BIGINT NOT NULL REFERENCES exercise_definition(id),
    workout_id BIGINT NOT NULL REFERENCES workout(id),
    start_time TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    end_time TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    order_index INTEGER NOT NULL
);

CREATE TABLE set_based_exercise_record (
    id BIGINT PRIMARY KEY REFERENCES exercise_record(id),
    average_rest_time DOUBLE PRECISION
);

CREATE TABLE distance_exercise_record (
    id BIGINT PRIMARY KEY REFERENCES exercise_record(id),
    distance DOUBLE PRECISION NOT NULL,
    distance_unit VARCHAR(255) NOT NULL,
    duration BIGINT NOT NULL,
    distance_per_unit DOUBLE PRECISION,
    weight_kg DOUBLE PRECISION
);

CREATE TABLE exercise_set (
    id BIGSERIAL PRIMARY KEY,
    version INTEGER,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITH TIME ZONE,
    rest_time BIGINT,
    start_time TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    end_time TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    set_duration BIGINT,
    failure BOOLEAN,
    weight_kg DOUBLE PRECISION,
    repetitions INTEGER,
    partial_repetitions INTEGER,
    order_index INTEGER NOT NULL,
    exercise_record_id BIGINT REFERENCES set_based_exercise_record(id)
); 