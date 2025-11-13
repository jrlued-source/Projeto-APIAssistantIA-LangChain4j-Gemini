# 🚗 Assistant IA – LangChain4j + Google Gemini (Spring Boot)

API em Java Spring Boot que expõe um assistente de IA para conversas inteligentes, usando:

- **LangChain4j** para orquestrar o modelo e as ferramentas
- **Google Gemini** como LLM
- **Memória de contexto** (chat com histórico)
- **Spring Security** com autenticação básica (user/senha via `application.properties`)
- **Swagger / OpenAPI** para testar tudo via navegador

---

## 🧠 Ideia do Projeto

A API simula um **Consultor de Frotas Corporativas** que:

- Entende perguntas sobre **locação corporativa de veículos**
- Pode **calcular cotações** com base em categoria e número de dias
- Mantém **contexto** da conversa (memória das últimas mensagens)
- Expõe tudo via endpoint REST `/api/assistant`

É um projeto ótimo para portfólio: mostra **Java + Spring Boot + IA + Segurança + Swagger**.

---

## 🧩 Tecnologias Usadas

- Java **21**
- Spring Boot **3.5.6**
  - `spring-boot-starter-web`
  - `spring-boot-starter-security`
- **LangChain4j**:
  - `langchain4j-google-ai-gemini-spring-boot-starter`
  - `langchain4j-spring-boot-starter`
- **Swagger / OpenAPI**:
  - `springdoc-openapi-starter-webmvc-ui`
- **Hibernate Validator**
- Maven

---

## 🗂 Estrutura (Arquivos / Classes e Funções)

### 1. `pom.xml`

- Define todas as dependências do projeto:
  - Spring Web
  - Spring Security
  - LangChain4j + Gemini
  - SpringDoc (Swagger)
  - Hibernate Validator
- Define:
  - Java 21
  - Plugin do Spring Boot para build e run

Em resumo: é o arquivo que diz ao Maven “quais peças esse projeto precisa para funcionar”.

---

### 2. `application.properties`

Aqui você configura:

```properties
Aqui você configura:

```properties
spring.application.name=langchain4j

# Config do Gemini (você vai colocar sua própria)
gemini.api-key=SEU_API_KEY_AQUI
gemini.model=gemini-2.5-flash

# Usuário e senha do Spring Security (Basic Auth)
spring.security.user.name=admin
spring.security.user.password=123456
spring.security.user.roles=USER
```

#### Como obter sua própria API Key do Gemini

1. Acesse: https://aistudio.google.com/ (Google AI Studio)
2. Faça login com sua conta Google
3. Vá em **“Get API key”** / “API keys”
4. Crie uma **nova API Key**
5. Copie a chave gerada
6. Cole no `application.properties` em:

```properties
gemini.api-key=COLA_AQUI_SUA_CHAVE
```

Importante: nunca suba sua chave real para o GitHub público.
 Use `.env`, variáveis de ambiente ou placeholders no repositório público.

#### Sobre a autenticação básica

- O Spring Security cria um **login HTTP Basic** com:
  - Usuário: `admin`
  - Senha: `123456`
- Você usará esses dados para:
  - Acessar o Swagger
  - Chamar o endpoint `/api/assistant` via Postman/Insomnia/cURL

------

### 3. `Langchain4jApplication.java`

```java
@SpringBootApplication
public class Langchain4jApplication {

    public static void main(String[] args) {
        SpringApplication.run(Langchain4jApplication.class, args);
    }

}
```

Classe **principal** da aplicação.

É o “botão de ligar” do projeto.

Quando você roda `mvn spring-boot:run` ou executa o jar, essa classe é chamada.

------

### 4. `SwaggerConfig.java`

```java
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI langchain4jAssistantOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Assistant IA - LangChain4j & Gemini")
                        .description("API desenvolvida em Java Spring Boot...")
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

```

- Configura o **Swagger / OpenAPI** da aplicação.
- Define:
  - Título da API
  - Descrição
  - Versão
  - Licença
  - Contato
  - Link do repositório

Na prática, é o que faz aparecer a documentação bonitinha em `/swagger-ui/index.html`.

------

### 5. `AssistantAiService.java`

```java
@AiService
public interface AssistantAiService {

    @SystemMessage("""
        Você é o Consultor de Frotas Corporativas Sênior...
        ...
    """)
    Result<String> handleRequest(@UserMessage String userMessage);

}

```

- É a **interface de serviço de IA** usada pelo LangChain4j.
- Principais pontos:
  - `@AiService`: LangChain4j gera uma implementação automática com base nessa interface.
  - `@SystemMessage`: define o **“cérebro” e o papel** do assistente (consultor de frotas corporativas).
  - `@UserMessage`: representa a mensagem do usuário que entra na IA.
  - `Result<String>`: retorna a resposta do modelo, com metadados se você quiser.

Aqui você define as **regras do jogo**:

- Escopo B2B
- Categorias permitidas (`economico`, `suv`, `premium`)
- Quando acionar ferramenta de cotação
- Como responder quando faltar informação
- Quando recusar perguntas fora do escopo

------

### 6. `AssistantTools.java`

```java
@Component
public class AssistantTools {

    private static final Map<String, Double> DAILY_BASE_PRICE = Map.of(
            "economico", 150.0,
            "suv",       280.0,
            "premium",   420.0
    );

    private static final Map<String, Double> INSURANCE_RATE = Map.of(
            "economico", 0.05,
            "suv",       0.08,
            "premium",   0.12
    );

    @Tool("Calcula o valor total do aluguel corporativo com base na categoria do carro e número de dias.")
    public String calculateQuotation(String category, int days) {
        Double base = DAILY_BASE_PRICE.get(category.toLowerCase());
        Double rate = INSURANCE_RATE.get(category.toLowerCase());

        double total = (base * days) * (1 + rate);

        return String.format(
                "Cotação: %s por %d dias → R$ %.2f (inclui seguro %.0f%%)",
                category, days, total, rate * 100
        );
    }
}

```

- Classe que agrupa **ferramentas** que o modelo de IA pode chamar.
- `@Component`: para o Spring gerenciar essa classe.
- `@Tool`: expõe o método para o LangChain4j como **ferramenta chamável pela IA**.
- Lógica:
  - Usa mapas com preços base por categoria.
  - Aplica taxa de seguro.
  - Calcula o total conforme dias e categoria.
  - Retorna uma string formatada com a cotação.

É aqui que a IA “pede ajuda” para fazer contas de verdade, em vez de inventar número.

------

### 7. `AssistantConfig.java`

```java
	@Configuration
public class AssistantConfig {

    @Value("${gemini.api-key}")
    private String geminiApiKey;

    @Value("${gemini.model}")
    private String geminiModel;

    @Bean
    public GoogleAiGeminiChatModel googleAiGeminiChatModel() {
        return GoogleAiGeminiChatModel.builder()
                .apiKey(geminiApiKey)
                .modelName(geminiModel)
                .build();
    }

    @Bean
    public AssistantAiService assistant(GoogleAiGeminiChatModel model, AssistantTools tools) {

        ChatMemory memory = MessageWindowChatMemory.withMaxMessages(10);

        System.out.println("✅ AssistantConfig carregado: memória configurada com 10 mensagens.");

        return AiServices.builder(AssistantAiService.class)
                .chatModel(model)
                .chatMemory(memory)
                .tools(tools)
                .build();
    }
}

```

Função dessa classe:

1. **Criar o modelo do Gemini**:
   - Usa `gemini.api-key` e `gemini.model` vindos do `application.properties`.
2. **Configurar memória de chat**:
   - `MessageWindowChatMemory.withMaxMessages(10)`:
     - Guarda as **últimas 10 mensagens** da conversa.
     - Isso permite que o assistente tenha contexto (“memória recente”).
3. **Construir o serviço de IA**:
   - Usa:
     - Modelo (Gemini)
     - Memória
     - Ferramentas (`AssistantTools`)
   - Cria o bean `AssistantAiService` que é injetado no controller.

Essa classe é basicamente a “central elétrica” que conecta:
 **Gemini + Memória + Ferramentas + Interface de serviço**.

------

### 8. `AssistantController.java`

```java
package com.decoder.langchain4j;

import dev.langchain4j.service.Result;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/assistant")
public class AssistantController {

    private final AssistantAiService assistantAiService;

    private final Map<String, String> conversationMap = new ConcurrentHashMap<>();

    public AssistantController(AssistantAiService assistantAiService) {
        this.assistantAiService = assistantAiService;
    }

    @PostMapping
    public String askAssistant(@RequestBody String userMessage) {
        Result<String> result = assistantAiService.handleRequest(userMessage);
        return result.content();
    }

}

```

- Exposição do endpoint REST da IA.
- Rota base: `/api/assistant`
- Método:
  - `POST /api/assistant`
  - Corpo: texto (mensagem do usuário)
  - Retorno: resposta da IA em string

Fluxo:

1. Cliente envia uma mensagem (`userMessage`) no corpo da requisição.
2. Controller chama `assistantAiService.handleRequest(userMessage)`.
3. LangChain4j + Gemini processam a mensagem, aplicam regras, chamam ferramentas, usam memória, etc.
4. O controller devolve `result.content()` como resposta HTTP.

------

## 🔐 Autenticação (Spring Security)

Com a dependência:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

```

E as propriedades:

```properties
spring.security.user.name=admin
spring.security.user.password=123456
spring.security.user.roles=USER

```

O comportamento é:

- Todas as rotas ficam protegidas por **HTTP Basic**.
- Para acessar:
  - No Swagger (canto superior direito → Authorize):
    - Username: `admin`
    - Password: `123456`
  - No Postman:
    - Aba Authorization → Type: Basic Auth
    - Username: `admin`
    - Password: `123456`

Se você quiser deixar o projeto mais aberto para testes públicos, pode:

- Remover a dependência de `spring-boot-starter-security`, ou
- Criar uma configuração de segurança customizada liberando `/api/assistant` e `/swagger-ui/**`.

------

## ▶️ Como Rodar o Projeto

1. Clonar o repositório

   ```bash
   git clone https://github.com/SEU_USUARIO/SEU_REPO.git
   cd SEU_REPO
   ```

2. Configurar o `application.properties`:

   - Definir `gemini.api-key` com sua chave real.
   - Confirmar `gemini.model=gemini-2.5-flash` (ou outro suportado).
   - Optionally, ajustar usuário/senha de segurança.

3. Rodar com Maven:

   ```cmd
   mvn spring-boot:run
   ```

4. Acessar Swagger:

   - URL padrão: `http://localhost:8080/swagger-ui/index.html`
   - Fazer login (se segurança estiver habilitada).
   - Testar o endpoint `POST /api/assistant`.

------

## 📬 Exemplo de Requisição

### Via Swagger ou Postman (JSON simples com texto no body)

- Método: `POST`
- URL: `http://localhost:8080/api/assistant`
- Body (raw, `text/plain` ou `application/json` dependendo de como você quiser enviar):

```
Quero uma cotação de carro econômico por 7 dias para frota corporativa.
```

Resposta (exemplo esperado):

```
Cotação: economico por 7 dias → R$ XXXX,XX (inclui seguro 5%)
+ explicação sobre política de frotas corporativas...
```

# 🌟 Para Quem Está Estudando

Se você chegou até aqui porque quer aprender:

**Esse projeto é totalmente aberto para estudo.**
 Use como inspiração para:

- entender como integrar IA em Java
- criar suas próprias ferramentas com LangChain4j
- testar modelos do Gemini em aplicações reais
- montar APIs inteligentes com memória de contexto
- aprender sobre segurança básica com Spring Security

Fique à vontade para fork, modificar, quebrar e reconstruir.
 É assim que se aprende de verdade.

------

# 💼 Para Empresas / Recrutadores

Este repositório demonstra:

- domínio real em **Java 21 + Spring Boot**
- integração avançada com **LLMs (Google Gemini)**
- uso do **LangChain4j** (framework corporativo emergente para IA)
- implementação de **ferramentas executáveis** pela IA
- construção de **APIs com memória**, segurança e documentação
- código limpo, modular e fácil de dar manutenção

Se você busca alguém que:

- desenvolve sistemas inteligentes
- integra IA a aplicações corporativas
- domina APIs REST, segurança, arquitetura e boas práticas
- resolve problemas reais com foco em qualidade

Estou disponível para conversar.

LinkedIn: **https://www.linkedin.com/in/edvaldo-dev**

