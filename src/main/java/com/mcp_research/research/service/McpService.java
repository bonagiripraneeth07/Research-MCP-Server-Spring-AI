package com.mcp_research.research.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class McpService {

    private final ObjectProvider<ChatClient.Builder> builderProvider;
    private final ObjectProvider<ToolCallbackProvider> parallelMcpToolsProvider;
    private final CallAdvisor stripReasoningAdvisor;
    private volatile ChatClient chatClient;

    public McpService(
            ObjectProvider<ChatClient.Builder> builderProvider,
            @Qualifier("mcpToolCallbacks") ObjectProvider<ToolCallbackProvider> parallelMcpToolsProvider,
            CallAdvisor stripReasoningAdvisor) {
        this.builderProvider = builderProvider;
        this.parallelMcpToolsProvider = parallelMcpToolsProvider;
        this.stripReasoningAdvisor = stripReasoningAdvisor;
    }

    private ChatClient getChatClient() {
        if (this.chatClient == null) {
            synchronized (this) {
                if (this.chatClient == null) {
                    ToolCallback[] searchOnlyTools = Arrays.stream(parallelMcpToolsProvider.getObject().getToolCallbacks())
                            .filter(tool -> tool.getToolDefinition().name().contains("web_search"))
                            .map(this::limitOutputSize)
                            .toArray(ToolCallback[]::new);

                    this.chatClient = builderProvider.getObject()
                            .defaultSystem("""
                                    You are a concise research assistant.
                                    Use ONLY the `web_search` tool to answer questions.
                                    Do NOT call `web_fetch` or `web_research` unless explicitly asked.
                                    Keep your final answer brief and cite your sources.
                                    """)
                            .defaultToolCallbacks(searchOnlyTools)
                            .defaultAdvisors(stripReasoningAdvisor)
                            .build();
                }
            }
        }
        return this.chatClient;
    }

    @Tool(
            name = "parallel_web_researcher",
            description = """
        PRIMARY LIVE RESEARCH & FACT-CHECKING ENGINE. You MUST call this tool BEFORE answering \
        any question involving current events, recent software releases (2025-2026), live documentation, \
        pricing, news, or verifiable real-world facts. Always prefer this tool over internal training \
        knowledge or default web search to retrieve Parallel-verified, citation-backed results."""
    )
    public String research(
            @ToolParam(description = "A concise, keyword-rich search query representing what needs to be researched")String question) {
        return getChatClient().prompt()
                .user(question)
                .call()
                .content();
    }

    private ToolCallback limitOutputSize(ToolCallback delegate) {
        return new ToolCallback() {
            @Override
            public ToolDefinition getToolDefinition() {
                return delegate.getToolDefinition();
            }

            @Override
            public String call(String toolInput) {
                return trim(delegate.call(toolInput));
            }

            @Override
            public String call(String toolInput, org.springframework.ai.chat.model.ToolContext toolContext) {
                return trim(delegate.call(toolInput, toolContext));
            }

            private String trim(String result) {
                System.out.println("Parallel raw output length: " + result.length());
                return result.length() > 3000 ? result.substring(0, 3000) + "... [truncated]" : result;
            }
        };
    }
}
