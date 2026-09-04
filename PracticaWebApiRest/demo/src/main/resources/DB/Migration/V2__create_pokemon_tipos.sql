CREATE TABLE pokemon_tipos (
    pokemon_id BIGINT NOT NULL REFERENCES pokemones(id) ON DELETE CASCADE,
    tipo VARCHAR(255)
);