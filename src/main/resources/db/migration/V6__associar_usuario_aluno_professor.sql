ALTER TABLE usuario
    ADD COLUMN IF NOT EXISTS aluno_id     BIGINT REFERENCES aluno (id) ON DELETE SET NULL,
    ADD COLUMN IF NOT EXISTS professor_id BIGINT REFERENCES professor (id) ON DELETE SET NULL;

ALTER TABLE usuario
    ADD CONSTRAINT uk_usuario_aluno_id UNIQUE (aluno_id),
    ADD CONSTRAINT uk_usuario_professor_id UNIQUE (professor_id),
    ADD CONSTRAINT ck_usuario_aluno_ou_professor CHECK (aluno_id IS NULL OR professor_id IS NULL);
