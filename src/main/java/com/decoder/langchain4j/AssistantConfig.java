package com.decoder.langchain4j;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

        // cria manualmente a memória de chat
        ChatMemory memory = MessageWindowChatMemory.withMaxMessages(10);

        System.out.println("✅ AssistantConfig carregado: memória configurada com 10 mensagens.");

        return AiServices.builder(AssistantAiService.class)
                .chatModel(model)
                .chatMemory(memory)   // usa memória explícita, evita o erro do @MemoryId
                .tools(tools)
                .build();
    }
}
