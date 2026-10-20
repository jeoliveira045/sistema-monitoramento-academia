# Requisições de teste

Os arquivos `.http` (IntelliJ HTTP Client / VS Code REST Client) ficam nesta pasta e
usam os corpos JSON de `json/`. Execute na ordem abaixo, com a aplicação rodando em
`http://localhost:8080`. Os ids citados assumem um banco recém-criado.

1. `exercicio.http` — cria os exercícios 1, 2 e 3
2. `aluno.http` — cria os alunos já com treinos, itens e exercícios aninhados
3. `treino.http`
4. `item-exercicio.http`

O mesmo fluxo com curl, a partir de `docs/requests`:

```bash
H='Content-Type: application/json'
URL=http://localhost:8080

# Exercícios (precisam existir antes dos alunos: os itens referenciam os ids 1, 2 e 3)
curl -X POST   $URL/exercicios   -H "$H" -d @json/exercicio/criar-supino-reto.json
curl -X POST   $URL/exercicios   -H "$H" -d @json/exercicio/criar-agachamento-livre.json
curl -X POST   $URL/exercicios   -H "$H" -d @json/exercicio/criar-remada-curvada.json
curl           $URL/exercicios
curl           "$URL/exercicios?musculo=Peitoral"
curl -X PUT    $URL/exercicios/1 -H "$H" -d @json/exercicio/atualizar-exercicio.json

# Alunos (o save também salva treinos e itens; o IMC é calculado no save)
curl -X POST   $URL/alunos   -H "$H" -d @json/aluno/criar-aluno.json
curl -X POST   $URL/alunos   -H "$H" -d @json/aluno/criar-aluno-2.json
curl           $URL/alunos
curl           "$URL/alunos?situacao=ATIVO"
curl           $URL/alunos/1
curl -X PUT    $URL/alunos/1 -H "$H" -d @json/aluno/atualizar-aluno.json

# Treinos do aluno (o POST já cria os itens)
curl -X POST   $URL/alunos/1/treinos   -H "$H" -d @json/treino/criar-treino.json
curl           $URL/alunos/1/treinos
curl           $URL/alunos/1/treinos/1
curl -X PUT    $URL/alunos/1/treinos/1 -H "$H" -d @json/treino/atualizar-treino.json

# Itens do treino
curl -X POST   $URL/treinos/1/itens   -H "$H" -d @json/item-exercicio/criar-item-exercicio.json
curl           $URL/treinos/1/itens
curl -X PUT    $URL/treinos/1/itens/9 -H "$H" -d @json/item-exercicio/atualizar-item-exercicio.json
curl -X DELETE $URL/treinos/1/itens/9

# Remoções
curl -X DELETE $URL/alunos/1/treinos/3
curl -X DELETE $URL/alunos/2
```
