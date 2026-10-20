# Sistema de Monitoramento da Academia

API REST para acompanhar os alunos da academia: dados físicos e de saúde, treinos,
exercícios e o vínculo entre professores e alunos.

Os dados cadastrais de alunos e professores (nome, CPF, email, situação etc.) **não são
criados aqui**: eles vêm do [sistema-usuario-academia](../sistema-usuario-academia), que é
a fonte desses cadastros. Este sistema importa esses registros e completa com as
informações do monitoramento (peso, altura, IMC, histórico de lesões, PAR-Q, treinos...).

## Tecnologias

- Java 17+ (o `.sdkmanrc` usa `21.0.2-open`)
- Spring Boot 4.1 (Web MVC + JDBC com `JdbcClient`)
- PostgreSQL 16
- Flyway para as migrations
- Lombok
- `java.net.http.HttpClient` para chamar o sistema de usuários

## Estrutura

```
src/main/java/monokai
├── config/        # Flyway e HttpClient
├── controller/    # Endpoints de alunos, treinos, itens e exercícios
├── domain/        # Modelo do monitoramento (Aluno, Professor, Treino, Exercicio...)
├── repository/    # Acesso ao banco com JdbcClient
├── service/       # Regras de negócio
└── integration/   # Importação do sistema de usuários
    ├── controller/  # /import/alunos e /import/professores
    ├── domain/      # Formato dos dados recebidos (AlunoImport, ProfessorImport...)
    ├── dto/         # Login da service-account
    └── service/     # Busca na API externa e conversão para o domínio
```

As migrations ficam em `src/main/resources/db/migration` e rodam ao subir a aplicação.

## Como rodar

1. Suba o banco:

   ```bash
   docker compose up -d
   ```

   Cria o banco `sistema-monitoramento-academia` (usuário `admin` / senha `admin`) na porta `5432`.

2. Configure o endereço do sistema de usuários em `src/main/resources/application.properties`:

   ```properties
   api.baseurl=http://<host>:<porta>
   ```

   Com o sistema de usuários no minikube, o endereço sai de
   `minikube service java-app-service --url`.

3. Rode a aplicação:

   ```bash
   mvn spring-boot:run
   ```

   A API sobe em `http://localhost:8080`.

> O sistema de usuários também usa a porta `5432` no `docker compose` local. Para rodar os
> dois bancos ao mesmo tempo na mesma máquina, mude a porta de um deles.

## Endpoints

| Recurso | Rota |
|---|---|
| Exercícios | `GET/POST /exercicios`, `GET/PUT/DELETE /exercicios/{id}` (filtro `?musculo=`) |
| Alunos | `GET/POST /alunos`, `GET/PUT/DELETE /alunos/{id}` (filtro `?situacao=`) |
| Treinos do aluno | `GET/POST /alunos/{alunoId}/treinos`, `GET/PUT/DELETE /alunos/{alunoId}/treinos/{id}` |
| Itens do treino | `GET/POST /treinos/{treinoId}/itens`, `GET/PUT/DELETE /treinos/{treinoId}/itens/{id}` |
| Importar alunos | `GET /import/alunos` |
| Importar professores | `GET /import/professores` |

### Importação do sistema de usuários

`GET /import/alunos` e `GET /import/professores`:

1. fazem login no sistema de usuários com a `service-account` (`POST /auth/login`);
2. buscam a lista em `/alunos` ou `/professores` com o token JWT;
3. convertem cada registro para o domínio local e salvam com upsert pelo `id`.

Se o aluno ou professor já existir, os dados preenchidos só no monitoramento são mantidos.
Só os dados cadastrais vindos do sistema de usuários são atualizados.

## Requisições de teste

Em `docs/requests` há arquivos `.http` (IntelliJ HTTP Client / VS Code REST Client) e os
JSONs usados neles. A ordem de execução e o mesmo fluxo em `curl` estão em
[`docs/requests/README.md`](docs/requests/README.md).

## Documentação

Os diagramas em PlantUML ficam em `docs/`:

- `use-case-diagram-sistema-monitoramento.plantuml` — casos de uso
- `diagram-class-sistema-monitoramento-domain.plantuml` — classes do domínio
- `sequence-diagram-*.plantuml` e `service-class-diagram-*.plantuml` — fluxos de listar
  alunos e montar treino
