# auth-service

Serviço de **autenticação e autorização** do ecossistema Prolab. É o **Authorization Server**: gerencia usuários e scopes, autentica credenciais e emite JWTs assinados com chave privada RSA. Também publica eventos de usuário no RabbitMQ para que outros serviços reajam de forma assíncrona (ex.: envio de e-mail de boas-vindas).

## 🔗 Deploy em produção

- **Frontend:** https://front-end-prolab-system.vercel.app
- **Auth Service:** https://auth-service-xa2p.onrender.com
- **Prolab System:** https://prolabsystem.onrender.com
- **Message Service:** https://message-service-mp5h.onrender.com (worker, sem endpoint público)

> ⚠️ Os backends estão hospedados no plano gratuito do Render, que "dorme" após inatividade. A primeira requisição após um período sem uso pode levar de 30 a 60 segundos para responder (cold start).

## CORS

Em produção, o CORS está restrito ao domínio do frontend: `https://front-end-prolab-system.vercel.app`. Para desenvolvimento local, ajuste `CorsConfig.java` ou use `addAllowedOriginPattern("*")` temporariamente.

## Arquitetura

Faz parte de um ecossistema de três microsserviços:

| Serviço | Papel | Repositório |
|---|---|---|
| **auth-service** (este) | Emite o JWT (assina com chave privada RSA), gerencia usuários/scopes, publica eventos no RabbitMQ | — |
| **ProlabSystem** | API de domínio (gestão de resíduos). Valida o JWT com a chave pública | https://github.com/SAMUELMARQUES692/ProlabSystem |
| **message-service** | Consome os eventos do RabbitMQ e dispara e-mails | https://github.com/SAMUELMARQUES692/message-service |

```
  Cliente ──(email+senha)──► auth-service ──(JWT RS256)──► Cliente
                                  │
                                  │ publica UsuarioEvent
                                  ▼
                           RabbitMQ (prolab.exchange)
                                  │
                                  ▼
                           message-service ──► e-mail

  Cliente ──(Bearer JWT)──► ProlabSystem  (valida com a chave pública)
```

O par de chaves RSA é a peça central: a **privada** vive só aqui (assina os tokens); a **pública** é distribuída aos resource servers (ProlabSystem) para validarem a assinatura sem precisar consultar o auth a cada request.

## Stack

- Java 17
- Spring Boot 4.1.0 (Web, Data JPA, Security, OAuth2 Resource Server, Validation)
- Spring AMQP (RabbitMQ)
- PostgreSQL + Flyway
- Nimbus JOSE (encode/decode de JWT RSA)
- BCrypt (hash de senha)
- MapStruct + Lombok
- springdoc-openapi (Swagger)

## Segurança e token

- Senhas são armazenadas com **BCrypt**.
- Token **JWT RS256** gerado pelo `TokenService`, com:
    - `iss`: `auth-service`
    - `sub`: e-mail do usuário
    - `scope`: scopes do usuário separados por espaço (padrão OAuth2)
    - expiração: 1 hora
- Rotas públicas: `POST /auth/login`, `POST /api/usuarios/cadastrar`, Swagger.
- Rotas que exigem `SCOPE_ADMIN`: `PUT /api/usuarios/{id}`, `DELETE /api/usuarios/{id}`.
- Demais rotas exigem apenas JWT válido (qualquer scope).

## Como rodar

### Pré-requisitos
- JDK 17+
- PostgreSQL
- Um broker RabbitMQ (o projeto está configurado para CloudAMQP com SSL)
- Par de chaves RSA (privada + pública) em formato PEM

### Variáveis de ambiente

| Variável | Descrição |
|---|---|
| `DATABASE_URL` | JDBC do Postgres, ex.: `jdbc:postgresql://localhost:5432/auth` |
| `DATABASE_USERNAME` | usuário do banco |
| `DATABASE_PASSWORD` | senha do banco |
| `PATH_PRIVATE_KEY` | caminho do `.pem` da chave **privada** RSA |
| `PATH_PUBLIC_KEY` | caminho do `.pem` da chave **pública** RSA |
| `RABBIT_ADDRESSES` | endereço(s) do broker RabbitMQ |
| `RABBIT_USERNAME` | usuário do RabbitMQ |
| `RABBIT_PASSWORD` | senha do RabbitMQ |
| `RABBIT_VIRTUAL_HOST` | virtual host do RabbitMQ |

> Gerando um par de chaves RSA (exemplo):
> ```bash
> openssl genrsa -out private.pem 2048
> openssl rsa -in private.pem -pubout -out public.pem
> ```

### Configurando as variáveis de ambiente (IntelliJ)

1. Abra o menu de Run/Debug Configurations (ícone ao lado do botão ▶️ Run)
2. Selecione a configuração da aplicação principal (`AuthServiceApplication`)
3. Em **Environment variables**, adicione cada variável da tabela acima no formato `CHAVE=valor`, separadas por `;`
4. Salve e execute normalmente

> As chaves RSA (`private_key.pem`/`public_key.pem`) não estão no repositório por segurança. Gere seu próprio par (veja a seção acima) e aponte `PATH_PRIVATE_KEY`/`PATH_PUBLIC_KEY` para o caminho onde você salvou os arquivos.

### Executando

```bash
./mvnw spring-boot:run
```

Sobe em `http://localhost:8081`. O Flyway aplica as migrations na subida.

- Swagger UI: `http://localhost:8081/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8081/v3/api-docs`

## Endpoints

### Autenticação — `/auth/login`
- `POST /auth/login` *(público)* — autentica e retorna o JWT

```bash
curl -X POST http://localhost:8081/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "usuario@email.com", "senha": "minhaSenha"}'
```

Resposta:
```json
{
  "accessToken": "eyJhbGciOiJSUzI1NiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 3600
}
```

Use o `accessToken` nas chamadas ao ProlabSystem: `Authorization: Bearer <accessToken>`.

### Usuários — `/api/usuarios`
- `POST /cadastrar` *(público)* — cria usuário

```bash
curl -X POST http://localhost:8081/api/usuarios/cadastrar \
  -H "Content-Type: application/json" \
  -d '{"nome": "Fulano", "email": "usuario@email.com", "senha": "minhaSenha"}'
```

> **Nota de segurança:** o cadastro público sempre atribui o scope `USER`, independentemente do que for enviado no campo `scopes` — isso impede que qualquer pessoa não autenticada se auto-promova a `ADMIN`.

- `GET /` *(autenticado)* — lista todos
- `GET /{id}` *(autenticado)* — busca por id
- `GET /buscar-email?email=` *(autenticado)* — busca por e-mail
- `PUT /{id}` *(requer scope ADMIN)* — atualiza
- `DELETE /{id}` *(requer scope ADMIN)* — remove

O cadastro e a atualização de usuário publicam um `UsuarioEvent` no RabbitMQ, consumido pelo `message-service`.

## Mensageria (RabbitMQ)

- **Exchange:** `prolab.exchange` (topic)
- **Fila:** `usuario.queue` (durável)
- **Routing key:** `usuario.mensagem`

O `EventPublisher` envia o `UsuarioEvent` (nome + e-mail) na criação e na atualização de usuário. O consumo fica a cargo do `message-service`.

## Organização do código

```
dev.samuel.auth_service
├── configuration   # SecurityConfig, RsaKeyConfig, TokenService, RabbitMQConfig, CorsConfig, SwaggerConfig
├── controller      # AuthController, UsuarioController
├── documentation   # interfaces com anotações OpenAPI
├── dto (request/response)  # inclui UsuarioEvent (payload publicado no RabbitMQ)
├── entity          # Usuario, Scope
├── exception       # exceptions de negócio
├── handler         # GlobalExceptionHandler
├── mapper          # MapStruct
├── repository      # Spring Data JPA
└── service         # AuthService, UsuarioService, ScopeService, EventPublisher
```

---

## Testes

```bash
./mvnw test
```

A suíte cobre três camadas com ferramentas específicas para cada uma:

- **Mapper** — testes unitários puros (JUnit 5), validando a conversão MapStruct entre `Usuario` e os DTOs de request/response, incluindo o método auxiliar de conversão de scopes para exibição (`mapScopeEntitiesToStringScopes`) e seus casos de borda (lista nula, lista vazia).
- **Service** — testes unitários com **Mockito**, isolando a lógica de negócio dos repositórios e mappers. Cobrem os fluxos de autenticação (`AuthService`), cadastro/gestão de usuários (`UsuarioService`), resolução de scopes (`ScopeService`) e publicação de eventos (`EventPublisher`), incluindo cenários de erro: credenciais inválidas, e-mail duplicado, e recursos não encontrados.
- **Controller** — testes de integração com **Testcontainers**, subindo um PostgreSQL real em container e exercitando a API de ponta a ponta via `MockMvc`. Cobrem o fluxo completo de login (geração de token), a regra que impede um usuário público de se auto-atribuir o scope `ADMIN` no cadastro, e a restrição de que apenas `ADMIN` pode atualizar ou remover usuários (com testes negativos confirmando `403 Forbidden` para usuários sem essa permissão).

Projeto de portfólio — Samuel Marques.
