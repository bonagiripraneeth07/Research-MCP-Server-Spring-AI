package com.mcp_research.research.controller;

import com.mcp_research.research.service.McpService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequestMapping("/api")
public class AiController {

      private final McpService service;

        public AiController(McpService service) {
            this.service = service;
        }

        @GetMapping("/ask/{question}")
        public String researchAI(@PathVariable String question) {
            return service.research(question);
        }
    }

