# agent-base

CLI application to orchestrate agents via REST. Uses Picocli for command parsing.

Build
- mvn package

Run
- Export environment variables:
  - AGENT_PLATFORM_URL (e.g. https://localhost:8080)
  - AGENT_PLATFORM_API_TOKEN (optional)
- java -jar target/agent-base-1.0.0-SNAPSHOT.jar create-task -t "Title" -d "Desc"

Design notes
- Depends on br.com.valhalla:agent-core
- Uses Java 11+ HttpClient to talk to the platform
- Commands and gateway are constructor-injectable to ease future migration to Spring
