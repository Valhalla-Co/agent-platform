# 🤖 Valhalla Agent Platform

Multi-Agent Orchestration Platform for automated code analysis, architecture review, and continuous improvement.

## 📦 Components

- **agent-core**: Framework-agnostic domain models, DTOs, and ports
- **agent-base**: CLI tool + orchestration engine

## 🚀 Quick Start

### Build
```bash
mvn clean install
```

### Run CLI
```bash
java -jar agent-base/target/agent-base-1.0.0-SNAPSHOT.jar analyze --project ./my-project
```

### Use as Library
```xml
<dependency>
    <groupId>br.com.valhalla</groupId>
    <artifactId>agent-core</artifactId>
    <version>1.0.0</version>
</dependency>
```

## 📚 Documentation

See [docs/](docs/) for detailed documentation.

## 🤝 Contributing

Pull requests are welcome! See [CONTRIBUTING.md](CONTRIBUTING.md).

## 📄 License

MIT License - see [LICENSE](LICENSE) for details.
