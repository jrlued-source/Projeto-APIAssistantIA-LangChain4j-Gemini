    package com.decoder.langchain4j;

    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;

    import io.swagger.v3.oas.models.ExternalDocumentation;
    import io.swagger.v3.oas.models.OpenAPI;
    import io.swagger.v3.oas.models.info.Contact;
    import io.swagger.v3.oas.models.info.Info;
    import io.swagger.v3.oas.models.info.License;

    @Configuration
    public class SwaggerConfig {

        @Bean
        public OpenAPI langchain4jAssistantOpenAPI() {
            return new OpenAPI()
                    .info(new Info()
                            .title("API Assistant IA - LangChain4j & Gemini")
                            .description("API desenvolvida em Java Spring Boot com integração LangChain4j + Google Gemini.\n"
                                    + "Permite conversas inteligentes com memória de contexto e autenticação básica via Spring Security.")
                            .version("v1.0.0")
                            .license(new License()
                                    .name("MIT License")
                                    .url("https://opensource.org/licenses/MIT"))
                            .contact(new Contact()
                                    .name("Edvaldo Dev")
                                    .url("https://www.linkedin.com/in/edvaldo-dev")
                                    .email("jrlued@gmail.com")))
                    .externalDocs(new ExternalDocumentation()
                            .description("Repositório do Projeto no GitHub")
                            .url("https://github.com/jrlued-source"));
        }
    }
