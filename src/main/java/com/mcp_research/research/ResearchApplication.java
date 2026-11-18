package com.mcp_research.research;

import com.mcp_research.research.controller.AiController;
import com.mcp_research.research.service.McpService;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class ResearchApplication {

	public static void main(String[] args) {
		SpringApplication.run(ResearchApplication.class, args);
	}
	@Bean
	public List<ToolCallback> tools(McpService mcpService) {
		return List.of(ToolCallbacks.from(mcpService));
	}

}
