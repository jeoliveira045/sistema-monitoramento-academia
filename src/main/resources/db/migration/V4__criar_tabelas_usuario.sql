CREATE TABLE IF NOT EXISTS usuario (
    id       BIGSERIAL    PRIMARY KEY,
    username VARCHAR(50)  NOT NULL UNIQUE,
    senha    VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS roles (
    id        BIGSERIAL    PRIMARY KEY,
    type_role VARCHAR(50)  NOT NULL UNIQUE,
    desc_role VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS usuario_roles (
    usuario_id BIGINT NOT NULL REFERENCES usuario (id),
    role_id    BIGINT NOT NULL REFERENCES roles (id),
    PRIMARY KEY (usuario_id, role_id)
);

INSERT INTO roles (type_role, desc_role) VALUES
    ('ADMIN', 'Administrador do sistema'),
    ('PROFESSOR', 'Instrutor de alunos'),
    ('ALUNO', 'aluno'),
    ('SERVICE', 'Conta de serviço para comunicação entre sistemas');

INSERT INTO usuario (username, senha) VALUES
    ('admin', crypt('admin', gen_salt('bf'))),
    ('service-account', crypt('service-account', gen_salt('bf')));

INSERT INTO usuario_roles (usuario_id, role_id)
SELECT u.id, r.id
FROM usuario u
INNER JOIN roles r ON r.type_role = 'ADMIN'
WHERE u.username = 'admin';

INSERT INTO usuario_roles (usuario_id, role_id)
SELECT u.id, r.id
FROM usuario u
INNER JOIN roles r ON r.type_role = 'SERVICE'
WHERE u.username = 'service-account';
