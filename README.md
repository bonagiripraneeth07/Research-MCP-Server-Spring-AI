# 🔍 Spring AI Live Web Research MCP Server

A Spring Boot & Spring AI hybrid application that acts as both a **REST API** and a custom **Model Context Protocol (MCP) Server**, empowering AI assistants like Claude Desktop with real-time, citation-backed web research powered by **Parallel MCP** and **Groq**.

---

## 🏗️ Architecture

This project demonstrates **MCP Chaining**—acting simultaneously as an **MCP Server** (for desktop AI clients) and an **MCP Client** (to Parallel's hosted search engine):


🛠️ Tech Stack

• Language & Runtime: Java 17+, Maven 3.8+ <br>
• Framework: Spring Boot 4 (spring-boot-starter-web)<br>
• AI & Protocol: Spring AI 2.0 (spring-ai-starter-mcp-server-webmvc, spring-ai-starter-mcp-client, spring-ai-starter-model-openai)<br>
• External APIs: Parallel AI (Live Web Search MCP) & Groq (LLM Inference)<br>

<b>OUTPUT<b> <br>
<img width="1665" height="1002" alt="image" src="https://github.com/user-attachments/assets/178c72f5-0ebc-4d6b-b7dd-3b11fb172e60" />
<img width="1879" height="973" alt="Screenshot 2026-11-18 180237" src="https://github.com/user-attachments/assets/4a2ac3cb-2f65-48fc-8349-5d4b727e6b50" />
