CREATE TABLE usuario_scopes (
    usuario_id  BIGINT NOT NULL REFERENCES usuarios(id),
    scope_id BIGINT NOT NULL REFERENCES scopes(id),
    PRIMARY KEY (usuario_id, scope_id)
);