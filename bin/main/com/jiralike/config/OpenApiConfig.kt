package com.jiralike.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

    @Bean
    fun openAPI(): OpenAPI {
        val securitySchemeName = "bearerAuth"
        return OpenAPI()
            .info(
                Info()
                    .title("JiraLike API")
                    .description(
                        """
                        ## REST API para gerenciamento de projetos e tarefas estilo Jira
                        
                        ### Como usar:
                        1. Faça login em **POST /api/auth/login** com as credenciais abaixo
                        2. Copie o `accessToken` retornado
                        3. Clique em **Authorize** (🔒) e cole: `Bearer <seu-token>`
                        4. Todos os endpoints autenticados estarão disponíveis
                        
                        ### Credenciais padrão (Admin):
                        - **Email:** `admin@jiralike.com`
                        - **Senha:** `Admin@123`
                        
                        ### Papéis:
                        - **ADMIN**: Gerencia usuários, cria projetos, acesso total
                        - **USER**: Cria/edita issues nos projetos que participa
                        """.trimIndent()
                    )
                    .version("1.0.0")
                    .contact(
                        Contact()
                            .name("JiraLike Team")
                            .email("admin@jiralike.com")
                    )
            )
            .addSecurityItem(SecurityRequirement().addList(securitySchemeName))
            .components(
                Components()
                    .addSecuritySchemes(
                        securitySchemeName,
                        SecurityScheme()
                            .name(securitySchemeName)
                            .type(SecurityScheme.Type.HTTP)
                            .scheme("bearer")
                            .bearerFormat("JWT")
                            .description("Insira o token JWT no formato: Bearer {token}")
                    )
            )
    }
}
