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
