---
title: Hexagonal Architecture Pattern
category: architecture
tags: architecture, ports-adapters, clean-architecture
applicable_to: Java, Spring, Enterprise
difficulty: advanced
---

# Hexagonal Architecture (Ports & Adapters)

## Overview
Hexagonal Architecture, also known as Ports and Adapters, is an architectural pattern that separates the core business logic from external concerns.

## Core Concepts

### Layers
1. **Domain (Core)** - Pure business logic
2. **Ports** - Interfaces that define how to interact with core
3. **Adapters** - Implementations that connect external world to ports

### Benefits
- Technology independence
- Testability
- Flexibility to change external dependencies
- Clear separation of concerns

## Structure

```
domain/
  ├── entities/
  ├── usecases/
  └── ports/
      ├── input/  (driving ports - API, CLI)
      └── output/ (driven ports - DB, external APIs)
      
adapters/
  ├── input/
  │   ├── rest/
  │   └── cli/
  └── output/
      ├── persistence/
      └── external/
```

## Detection Patterns

When analyzing a project, look for:
- Packages named: `domain`, `ports`, `adapters`, `infrastructure`
- Interfaces in domain that are implemented outside
- Use cases or services in domain layer
- Repositories as interfaces

## Improvements to Suggest

1. **Missing port interfaces** - Create interfaces for external dependencies
2. **Domain contamination** - Move framework dependencies out of domain
3. **Adapter organization** - Separate input and output adapters
4. **Dependency direction** - Ensure dependencies point inward to domain

## Example Structure for Java/Spring

```java
// Domain Layer
public interface UserRepository {  // Port (output)
    User save(User user);
    Optional<User> findById(UserId id);
}

public class CreateUserUseCase {  // Use Case
    private final UserRepository repository;
    
    public User execute(CreateUserCommand command) {
        // Business logic here
    }
}

// Adapter Layer  
@Repository
public class JpaUserRepository implements UserRepository {
    // JPA implementation
}

@RestController
public class UserController {  // Input Adapter
    private final CreateUserUseCase useCase;
}
```

## When to Suggest

- Projects with complex business logic
- Systems that need to be technology-independent
- Applications with multiple interfaces (REST, CLI, messaging)
- Long-term maintained projects
