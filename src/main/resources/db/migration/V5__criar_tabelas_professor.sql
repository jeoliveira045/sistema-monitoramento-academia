CREATE TABLE IF NOT EXISTS professor (
    id             BIGINT PRIMARY KEY,
    data_criacao   TIMESTAMP NOT NULL,
    data_alteracao TIMESTAMP NOT NULL,
    nome           VARCHAR(100),
    sobrenome      VARCHAR(150),
    dt_nascimento  DATE,
    cpf            VARCHAR(14),
    cref           VARCHAR(20),
    whatsapp       VARCHAR(20),
    email          VARCHAR(150),
    situacao       SMALLINT,
    foto           TEXT
);

CREATE TABLE IF NOT EXISTS professor_aluno (
    id             BIGSERIAL PRIMARY KEY,
    data_criacao   TIMESTAMP NOT NULL,
    data_alteracao TIMESTAMP NOT NULL,
    professor_id   BIGINT   NOT NULL REFERENCES professor (id) ON DELETE CASCADE,
    aluno_id       BIGINT   NOT NULL REFERENCES aluno (id) ON DELETE CASCADE,
    situacao       SMALLINT NOT NULL DEFAULT 1,
    UNIQUE (professor_id, aluno_id)
);

CREATE INDEX IF NOT EXISTS idx_professor_aluno_aluno_id ON professor_aluno (aluno_id);
