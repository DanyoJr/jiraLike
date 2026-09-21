# JiraLike — Projeto Completo (Backend, Frontend e Android)

Um sistema completo para gerenciamento de projetos e tarefas estilo Jira, desenvolvido com **Kotlin Spring Boot** (Backend), **React + Vite** (Frontend Web) e **Kotlin + ViewBinding** (Android App).

---

## 🚀 Como executar o projeto localmente (Ponta a Ponta)

O projeto é composto por três partes: um Backend (Spring Boot), um Frontend Web (Vite + React) e um App Android (Kotlin). Para que o sistema funcione completamente, é necessário rodar o backend e o cliente de sua preferência.

### Pré-requisitos
- JDK 21 (para o Backend)
- Node.js e npm (para o Frontend Web)
- Android Studio (para o App Android)

### 1. Rodando o Backend (API)

Abra um terminal na raiz do projeto (`kotlin_desire`) e execute o comando abaixo:

```bash
# Executar a aplicação Spring Boot
./gradlew bootRun
```

A API estará rodando e disponível em: **http://localhost:5000**

### 2. Rodando o Frontend Web (Interface Gráfica)

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

### 3. Rodando o App Android (Interface Mobile)

1. Abra o **Android Studio**.
2. Selecione **Open an existing project** e escolha a pasta raiz do projeto (`kotlin_desire`). O Android Studio reconhecerá o módulo `:android_ui`.
3. Aguarde o Gradle Sync finalizar.
4. Conecte um dispositivo Android físico ou inicie um Emulador (AVD).
5. No Android Studio, selecione o módulo `android_ui` na barra de execução (Run/Debug Configurations).
6. Clique no botão de **Run** (Shift + F10).

> **Aviso para Emuladores:** O app Android por padrão tenta acessar a API. Caso esteja rodando no emulador, a URL do backend deve ser `http://10.0.2.2:5000` em vez de `http://localhost:5000`, pois `localhost` se refere ao próprio emulador.

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

| Camada | Tecnologia | Versão/Detalhe |
|--------|------------|--------|
| **Backend** | Kotlin | 2.1.20 |
| **Backend** | Spring Boot | 3.4.3 |
| **Backend** | Spring Security & JWT | - |
| **Backend** | H2 Database & Flyway | - |
| **Backend** | SpringDoc OpenAPI | 2.8.5 |
| **Frontend** | React | 19.2.8 |
| **Frontend** | Vite | 8.2.2 |
| **Frontend** | TypeScript | ~6.0.2 |
| **Android** | Kotlin | 2.1.20 |
| **Android** | Android SDK | Min 24, Target 34 |
| **Android** | ViewBinding | - |
| **Android** | Retrofit & Coroutines | - |
| **Geral** | Gradle (Kotlin DSL) | - |

---

## 🧪 Testes

```bash
# Rodar todos os testes do backend
./gradlew test

# Relatório de testes do backend
open build/reports/tests/test/index.html
```
