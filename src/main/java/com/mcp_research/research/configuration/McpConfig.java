package com.mcp_research.research.configuration;
import io.modelcontextprotocol.client.transport.customizer.McpSyncHttpClientRequestCustomizer;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Configuration
public class McpConfig {
    @Bean
    public McpSyncHttpClientRequestCustomizer McpConfigAuth(@Value("${parallel.api.key:}") String parallelApiKey){
        return (builder, method, uri, body, context) -> {
          System.out.println("MCP Calling URI: " + uri);
            builder.header("Authorization", "Bearer " + parallelApiKey);
            if (parallelApiKey != null && !parallelApiKey.isBlank()) {
                builder.header("x-api-key", parallelApiKey);
            }
        };
    }

    @Bean
    public CallAdvisor stripReasoningAdvisor() {
        return new CallAdvisor() {
            @Override
            public String getName() {
                return "StripReasoningAdvisor";
            }

            @Override
            public int getOrder() {
                // Runs right before ChatModelCallAdvisor sends the request to the LLM
                return 0;
            }

            @Override
            public ChatClientResponse adviseCall(ChatClientRequest request, CallAdvisorChain chain) {
                List<Message> cleanedMessages = request.prompt().getInstructions().stream()
                        .map(msg -> {
                            if (msg instanceof AssistantMessage assistantMsg
                                    && assistantMsg.getMetadata().containsKey("reasoningContent")) {
                                Map<String, Object> cleanMeta = new HashMap<>(assistantMsg.getMetadata());
                                cleanMeta.remove("reasoningContent");
                                return AssistantMessage.builder()
                                        .content(assistantMsg.getText())
                                        .properties(cleanMeta)
                                        .toolCalls(assistantMsg.getToolCalls())
                                        .media(assistantMsg.getMedia())
                                        .build();
                            }
                            return msg;
                        })
                        .toList();

                Prompt cleanedPrompt = new Prompt(cleanedMessages, request.prompt().getOptions());
                return chain.nextCall(request.mutate().prompt(cleanedPrompt).build());
            }
        };
    }
}