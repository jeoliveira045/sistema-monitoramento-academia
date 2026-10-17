CREATE TABLE IF NOT EXISTS aluno (
    id                          BIGINT PRIMARY KEY,
    data_criacao                TIMESTAMP NOT NULL,
    data_alteracao              TIMESTAMP NOT NULL,
    nome                        VARCHAR(100),
    sobrenome                   VARCHAR(150),
    dt_nascimento               DATE,
    cpf                         VARCHAR(14),
    endereco                    VARCHAR(255),
    tipo_sanguineo              VARCHAR(20),
    whatsapp                    VARCHAR(20),
    email                       VARCHAR(150),
    situacao                    SMALLINT,
    foto                        TEXT,
    peso                        DOUBLE PRECISION,
    altura                      DOUBLE PRECISION,
    percentual_gordura_corporal DOUBLE PRECISION,
    imc                         DOUBLE PRECISION,
    circunferencias             TEXT,
    historico_lesoes            TEXT,
    condicoes_medicas           TEXT,
    uso_medicamentos_continuos  TEXT,
    restricoes_medicas          TEXT,
    par_q                       TEXT,
    atestado_medico             TEXT
);

CREATE TABLE IF NOT EXISTS exercicio (
    id             BIGSERIAL PRIMARY KEY,
    data_criacao   TIMESTAMP NOT NULL,
    data_alteracao TIMESTAMP NOT NULL,
    nome_exercicio VARCHAR(150) NOT NULL,
    musculo        VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS treino (
    id                 BIGSERIAL PRIMARY KEY,
    data_criacao       TIMESTAMP NOT NULL,
    data_alteracao     TIMESTAMP NOT NULL,
    aluno_id           BIGINT NOT NULL REFERENCES aluno (id) ON DELETE CASCADE,
    treino_periodo     VARCHAR(20),
    tipo               VARCHAR(20),
    treino_mes_vigente VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS item_exercicio (
    id                BIGSERIAL PRIMARY KEY,
    data_criacao      TIMESTAMP NOT NULL,
    data_alteracao    TIMESTAMP NOT NULL,
    treino_id         BIGINT NOT NULL REFERENCES treino (id) ON DELETE CASCADE,
    exercicio_id      BIGINT NOT NULL REFERENCES exercicio (id),
    item_agrupador_id BIGINT REFERENCES item_exercicio (id) ON DELETE CASCADE,
    serie             INTEGER,
    repeticoes        INTEGER,
    tipo_exercicio    VARCHAR(20)
);

CREATE INDEX IF NOT EXISTS idx_treino_aluno_id ON treino (aluno_id);
CREATE INDEX IF NOT EXISTS idx_item_exercicio_treino_id ON item_exercicio (treino_id);
CREATE INDEX IF NOT EXISTS idx_item_exercicio_item_agrupador_id ON item_exercicio (item_agrupador_id);
