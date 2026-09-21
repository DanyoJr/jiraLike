# Documentação Técnica do Projeto JiraLike

Este documento detalha o funcionamento, as tecnologias utilizadas, a arquitetura e os pontos de atenção do projeto JiraLike.

## 1. Visão Geral

O **JiraLike** é uma aplicação Full-Stack composta por três módulos principais:
1. **Backend (API REST):** Desenvolvido com Kotlin e Spring Boot, responsável por gerenciar as regras de negócio, persistência de dados no banco H2, autenticação (JWT) e expor endpoints REST.
2. **Frontend Web:** Uma Single Page Application (SPA) desenvolvida com React e TypeScript (via Vite), que consome a API do backend para fornecer uma interface web rica para os usuários gerenciarem projetos e tarefas.
3. **Frontend Mobile (Android):** Um aplicativo nativo para Android desenvolvido com Kotlin, ViewBinding, Retrofit e Coroutines, oferecendo funcionalidades semelhantes em dispositivos móveis.

## 2. Como foi feito (Tecnologias)

### 2.1. Backend (Spring Boot)
- **Linguagem:** Kotlin 2.1.20.
- **Framework Principal:** Spring Boot 3.4.3.
- **Persistência e Banco de Dados:**
  - **Spring Data JPA** para mapeamento objeto-relacional (ORM) usando Hibernate por baixo dos panos.
  - **H2 Database** configurado para salvar os dados em arquivo local (`./data/jiralike`), facilitando a execução sem necessidade de um banco de dados externo (como PostgreSQL ou MySQL), sendo ideal para desenvolvimento e testes.
  - **Flyway** para controle de versão e migração de esquemas de banco de dados, garantindo que as tabelas sejam criadas na ordem correta na inicialização.
- **Segurança:**
  - **Spring Security** para proteção de rotas e autenticação.
  - **JWT (JSON Web Token)** (`jjwt` versão 0.12.3) usado para controle de sessão sem estado (stateless). Após o login, o cliente recebe um token JWT que deve ser enviado no cabeçalho `Authorization: Bearer <token>` de requisições subsequentes.
- **Documentação de API:**
  - **SpringDoc OpenAPI** (Swagger) gerando automaticamente uma UI interativa em `/swagger-ui.html`.
- **Build System:** Gradle com Kotlin DSL (`build.gradle.kts`).

### 2.2. Frontend Web (React + Vite)
- **Framework & Ferramentas:** React 19.x encapsulado e construído através do Vite, que proporciona um ambiente de desenvolvimento muito rápido com Hot Module Replacement (HMR).
- **Linguagem:** TypeScript para tipagem estática e detecção de erros em tempo de compilação.
- **Roteamento:** `react-router-dom` para navegação entre páginas (Login, Dashboard, Projetos, etc.) de forma eficiente.
- **Ícones:** `lucide-react` para os ícones vetoriais em tela.
- **Linter:** `oxlint` para checagem de código ultrarrápida.

### 2.3. Android UI
- **Linguagem:** Kotlin.
- **Interface de Usuário:** XML e **ViewBinding** para amarração segura de visualizações no código (sem usar `findViewById` ou Kotlin Synthetics, que são obsoletos).
- **Consumo de API:**
  - **Retrofit2** e **Gson** para facilitar o consumo da API REST do Spring Boot, mapeando JSON para Data Classes em Kotlin de forma nativa.
  - **OkHttp Logging Interceptor** para debugar requisições de rede.
- **Assincronismo:** **Coroutines** do Kotlin para gerenciar chamadas de rede sem bloquear a Main Thread (UI Thread).
- **Componentes do Material Design** para uma interface responsiva e aderente às diretrizes do Android.

## 3. Como a Aplicação Funciona (Fluxo de Execução)

1. **Início e Persistência:**
   - Quando o Backend (Spring Boot) é iniciado (`./gradlew bootRun`), o Flyway verifica o estado do banco H2 e roda as migrações SQL necessárias (ex: criar tabelas de usuários, projetos, issues).
   - O Spring Boot levanta o servidor na porta 5000.
2. **Autenticação:**
   - Um usuário acessa o Web ou Android e digita suas credenciais. A requisição vai para `/api/auth/login`.
   - O Spring Security checa as credenciais com as que estão no banco de dados. Se estiverem corretas, o backend gera e devolve um token JWT na resposta.
3. **Requisições Protegidas:**
   - Para criar um projeto (endpoint protegido), o Frontend/Mobile manda o JWT no Header de Authorization. O `JwtAuthFilter` do Spring intercepta, valida o token, extrai o usuário (subject) e diz ao contexto de segurança que o usuário está autenticado para aquela requisição.
4. **Comunicação Web:**
   - O Frontend usa bibliotecas de rede nativas ou de terceiros (como `fetch` ou `axios`) para chamar as rotas `/api/projects`, e renderiza os dados através de estados do React (`useState` e `useEffect`).
5. **Comunicação Mobile:**
   - O Android usa a interface do Retrofit com `@GET` ou `@POST` dentro de funções `suspend` em ViewModels ou Controllers, retornando a resposta em background e atualizando os componentes de interface (ex: `RecyclerView`) na Main Thread.

## 4. Pontos de Atenção (Caveats)

- **Endereço do Servidor no Android:**
  - No frontend web, o cliente roda no browser do host, logo `localhost:5000` atinge diretamente o backend rodando na mesma máquina.
  - No Emulador Android, o `localhost` ou `127.0.0.1` **aponta para o próprio emulador**. Para que o app Android se comunique com o servidor Spring Boot rodando na máquina host, você precisa configurar a *Base URL* do Retrofit para `http://10.0.2.2:5000` (endereço padrão no Android Emulator que mapeia para o host).
- **Banco de Dados em Memória / Arquivo:**
  - O H2 está configurado para salvar arquivos localmente em `./data/jiralike`. Se a pasta `data` for deletada, todos os dados serão perdidos e recriados vazios na próxima execução via Flyway.
  - Para um ambiente de produção real, o H2 deve ser trocado por um banco relacional robusto como PostgreSQL, alterando as dependências no `build.gradle.kts` e ajustando as credenciais de banco.
- **CORS (Cross-Origin Resource Sharing):**
  - O Backend precisa estar com o CORS configurado para aceitar chamadas da URL do frontend (`http://localhost:5173`), caso contrário, o navegador bloqueará as requisições por segurança (retornando erro de preflight).
- **Versão do JDK:**
  - O projeto Backend especifica uso da versão Java 21 (`jvmTarget.set(JvmTarget.JVM_21)` e `sourceCompatibility = "21"`). Você precisará do JDK 21 na sua máquina para rodar ou buildar a API. O Android costuma rodar no Java 17.
- **Segurança de JWT:**
  - A chave secreta (`secret-key`) do JWT não deve ser incluída (hardcoded) no repositório final. Em produção, você deve extrair as chaves para variáveis de ambiente (Environment Variables) no servidor de deployment.
