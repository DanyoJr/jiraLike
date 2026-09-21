# JiraLike — Kotlin Spring Boot

Uma API REST completa para gerenciamento de projetos e tarefas estilo Jira, desenvolvida com **Kotlin** e **Spring Boot 3**.

---

## 🚀 Como executar o projeto localmente (Ponta a Ponta)

O projeto é composto por um Backend (Spring Boot) e um Frontend (Vite + React). Para que o sistema funcione completamente, é necessário rodar ambas as partes.

### Pré-requisitos
- JDK 17+ (para o Backend)
- Node.js e npm (para o Frontend)

### 1. Rodando o Backend (API)

Abra um terminal na raiz do projeto (`kotlin_desire`) e execute o comando abaixo:

```bash
# Executar a aplicação Spring Boot
./gradlew bootRun
```

A API estará rodando e disponível em: **http://localhost:5000**

### 2. Rodando o Frontend (Interface Gráfica)

Abra um **novo terminal** na raiz do projeto e execute os comandos abaixo:

```bash
# Entrar no diretório do frontend
cd frontend

# Instalar as dependências (apenas na primeira vez)
npm install

# Iniciar o servidor de desenvolvimento
npm run dev
```

A interface gráfica estará disponível no seu navegador em: **http://localhost:5173**

---

## 📖 Documentação da API (Swagger UI)

Acesse: **http://localhost:5000/swagger-ui.html**

### Como usar o Swagger:
1. Faça login em `POST /api/auth/login`
2. Copie o `accessToken` da resposta
3. Clique em **Authorize** (🔒) no topo da página
4. Cole: `Bearer <seu-token>`
5. Todos os endpoints ficam disponíveis

---

## 🗄️ Console do H2 (Banco de Dados)

Acesse: **http://localhost:5000/h2-console**

| Campo | Valor |
|-------|-------|
| JDBC URL | `jdbc:h2:file:./data/jiralike` |
| Username | `sa` |
| Password | `password` |

---

## 🔑 Credenciais Padrão

| Papel | Email | Senha |
|-------|-------|-------|
| **Admin** | `admin@jiralike.com` | `Admin@123` |
| **Usuário** | `user@jiralike.com` | `User@123` |

---

## 🏗️ Arquitetura

```
src/main/kotlin/com/jiralike/
├── Application.kt                    # Entry point
├── config/
│   └── OpenApiConfig.kt              # Swagger/OpenAPI config
├── controller/
│   ├── AuthController.kt             # Login, recuperação de senha
│   ├── UserController.kt             # CRUD de usuários (Admin)
│   ├── ProjectController.kt          # Gestão de projetos
│   └── IssueController.kt            # Issues e comentários
├── domain/
│   ├── entity/                       # Entidades JPA
│   │   ├── User.kt
│   │   ├── Project.kt
│   │   ├── Issue.kt
│   │   ├── Comment.kt
│   │   └── PasswordResetToken.kt
│   └── enums/                        # Enumerações
│       ├── Role.kt                   # ADMIN, USER
│       ├── IssueStatus.kt            # TODO, IN_PROGRESS, IN_REVIEW, DONE
│       ├── IssueType.kt              # BUG, FEATURE, TASK, STORY, EPIC
│       └── IssuePriority.kt          # LOWEST, LOW, MEDIUM, HIGH, HIGHEST
├── dto/                              # Data Transfer Objects
│   ├── AuthDtos.kt
│   ├── UserDtos.kt
│   ├── ProjectDtos.kt
│   ├── IssueDtos.kt
│   └── CommentDtos.kt
├── exception/                        # Tratamento de erros
│   ├── Exceptions.kt
│   └── GlobalExceptionHandler.kt
├── repository/                       # Repositórios JPA
│   ├── UserRepository.kt
│   ├── ProjectRepository.kt
│   ├── IssueRepository.kt
│   ├── CommentRepository.kt
│   └── PasswordResetTokenRepository.kt
├── security/                         # Segurança
│   ├── JwtUtil.kt                    # Geração/validação JWT
│   ├── JwtAuthFilter.kt              # Filtro de autenticação
│   ├── SecurityConfig.kt             # Configuração Spring Security
│   └── CustomUserDetailsService.kt   # UserDetailsService
└── service/                          # Lógica de negócio
    ├── AuthService.kt
    ├── UserService.kt
    ├── ProjectService.kt
    ├── IssueService.kt
    └── CommentService.kt
```

---

## 📋 Endpoints da API

### 🔐 Autenticação (`/api/auth`)
| Método | Endpoint | Descrição | Auth |
|--------|----------|-----------|------|
| POST | `/api/auth/login` | Login com email/senha → JWT | ❌ |
| POST | `/api/auth/forgot-password` | Solicitar token de reset | ❌ |
| POST | `/api/auth/reset-password` | Resetar senha com token | ❌ |
| GET | `/api/auth/me` | Meu perfil | ✅ |

### 👥 Usuários (`/api/users`) — Admin
| Método | Endpoint | Descrição | Auth |
|--------|----------|-----------|------|
| POST | `/api/users` | Criar usuário | 🔒 Admin |
| GET | `/api/users` | Listar todos usuários | 🔒 Admin |
| GET | `/api/users/{id}` | Buscar usuário por ID | 🔒 Admin |
| PUT | `/api/users/{id}` | Atualizar usuário | 🔒 Admin |
| DELETE | `/api/users/{id}` | Desativar usuário | 🔒 Admin |
| POST | `/api/users/me/change-password` | Alterar minha senha | ✅ |

### 📋 Projetos (`/api/projects`)
| Método | Endpoint | Descrição | Auth |
|--------|----------|-----------|------|
| POST | `/api/projects` | Criar projeto | 🔒 Admin |
| GET | `/api/projects` | Meus projetos | ✅ |
| GET | `/api/projects/all` | Todos os projetos | 🔒 Admin |
| GET | `/api/projects/{id}` | Detalhes do projeto | ✅ |
| PUT | `/api/projects/{id}` | Editar projeto | ✅ Dono |
| POST | `/api/projects/{id}/members` | Adicionar membro | ✅ Dono |
| DELETE | `/api/projects/{id}/members/{userId}` | Remover membro | ✅ Dono |

### 🎯 Issues e Comentários
| Método | Endpoint | Descrição | Auth |
|--------|----------|-----------|------|
| POST | `/api/projects/{projectId}/issues` | Criar issue | ✅ Membro |
| GET | `/api/projects/{projectId}/issues` | Listar issues | ✅ |
| GET | `/api/issues/{id}` | Detalhes da issue | ✅ |
| GET | `/api/issues/my` | Minhas issues | ✅ |
| PUT | `/api/issues/{id}` | Editar issue | ✅ Membro |
| PATCH | `/api/issues/{id}/status` | Mover no Kanban | ✅ Membro |
| DELETE | `/api/issues/{id}` | Deletar issue | ✅ Reporter/Dono |
| POST | `/api/issues/{issueId}/comments` | Comentar | ✅ Membro |
| GET | `/api/issues/{issueId}/comments` | Listar comentários | ✅ |
| PUT | `/api/comments/{id}` | Editar comentário | ✅ Autor |
| DELETE | `/api/comments/{id}` | Deletar comentário | ✅ Autor/Dono |

---

## 🔄 Fluxo Kanban

```
TODO → IN_PROGRESS → IN_REVIEW → DONE
```

---

## 🛠️ Stack Tecnológica

| Tecnologia | Versão |
|------------|--------|
| Kotlin | 1.9.22 |
| Spring Boot | 3.2.3 |
| Spring Security | 6.x |
| JWT (jjwt) | 0.12.3 |
| H2 Database | - |
| Flyway | - |
| SpringDoc OpenAPI | 2.3.0 |
| Gradle (Kotlin DSL) | - |

---

## 🧪 Testes

```bash
# Rodar todos os testes
./gradlew test

# Relatório de testes
open build/reports/tests/test/index.html
```
