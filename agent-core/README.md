# agent-core

Core library containing domain models, DTOs and ports for the agent orchestration platform.

Coordinates: br.com.valhalla:agent-core:1.0.0-SNAPSHOT

Usage
- Build and install locally: mvn install
- Publish to your Maven repository (Nexus/Artifactory/GitHub Packages) and reference in consumer projects.

Design
- framework-agnostic POJOs and interfaces
- minimal DTOs: TaskRequest, TaskResponse
- ports: AgentGateway

Security
- No runtime dependencies to keep the artifact lightweight and easy to reuse.
