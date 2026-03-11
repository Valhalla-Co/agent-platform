# 🚀 Referência Rápida - Agent Platform

## Comandos Principais

### Build do Projeto
```cmd
.\build.cmd
```

### Executar Aplicação

#### Opção 1: Usando o script run.cmd
```cmd
.\run.cmd --help
.\run.cmd analyze --project .\meu-projeto
.\run.cmd review --project .\meu-projeto
.\run.cmd improve --project .\meu-projeto
```

#### Opção 2: Diretamente com Java
```cmd
java -jar agent-base\target\agent-base-1.0.0-SNAPSHOT.jar --help
```

## Comandos CLI Disponíveis

### 1. analyze - Análise de Projeto
```cmd
.\run.cmd analyze --project <caminho-do-projeto>
```
Analisa a estrutura e qualidade do código do projeto.

### 2. review - Revisão de Código
```cmd
.\run.cmd review --project <caminho-do-projeto>
```
Realiza revisão de código e arquitetura usando agentes IA.

### 3. improve - Sugestões de Melhoria
```cmd
.\run.cmd improve --project <caminho-do-projeto>
```
Propõe melhorias para o projeto usando agentes IA.

### 4. create-task - Criar Task
```cmd
.\run.cmd create-task [opções]
```
Cria uma task na plataforma via REST API.

## Comandos Maven

### Build Completo
```powershell
.\mvnw.cmd clean install
```

### Build Rápido (sem testes)
```powershell
.\mvnw.cmd clean install -DskipTests
```

### Apenas Testes
```powershell
.\mvnw.cmd test
```

### Apenas um Módulo
```powershell
.\mvnw.cmd install -pl agent-core
.\mvnw.cmd install -pl agent-base
```

### Limpar Build
```powershell
.\mvnw.cmd clean
```

## Estrutura do Projeto

```
agent-platform/
├── agent-core/          # Biblioteca core (domain, DTOs, ports)
│   ├── src/main/java/   # Código fonte
│   └── target/          # Artefatos compilados
├── agent-base/          # CLI e orchestration
│   ├── src/main/java/   # Código fonte
│   └── target/          # JAR executável
├── build.cmd            # Script de build
├── run.cmd              # Script de execução
└── mvnw.cmd             # Maven Wrapper
```

## Arquivos de Configuração

- **pom.xml** - Configuração do projeto Maven
- **agent-core/pom.xml** - Módulo core
- **agent-base/pom.xml** - Módulo CLI
- **.mvn/wrapper/** - Maven Wrapper

## Variáveis de Ambiente

```powershell
# JAVA_HOME
$env:JAVA_HOME = "C:\DevTools\JDK\jdk-21.0.2"

# PATH
$env:Path = "C:\DevTools\JDK\jdk-21.0.2\bin;" + $env:Path
```

## Troubleshooting

### Java não encontrado
```powershell
$env:JAVA_HOME = "C:\DevTools\JDK\jdk-21.0.2"
$env:Path = "C:\DevTools\JDK\jdk-21.0.2\bin;" + $env:Path
```

### Rebuild necessário
```cmd
.\mvnw.cmd clean install
```

### Testes falhando
```cmd
.\mvnw.cmd clean test
```

### Ver logs detalhados
```cmd
.\mvnw.cmd clean install -X
```

## Exemplos de Uso

### Analisar este projeto
```cmd
.\run.cmd analyze --project .
```

### Analisar outro projeto
```cmd
.\run.cmd analyze --project C:\projetos\meu-app
```

### Review com verbosidade
```cmd
.\run.cmd review --project .\meu-projeto --verbose
```

## Links Úteis

- [Maven Documentation](https://maven.apache.org/guides/)
- [Java 21 Documentation](https://docs.oracle.com/en/java/javase/21/)
- [Picocli Documentation](https://picocli.info/)

## Atalhos Rápidos

| Ação | Comando |
|------|---------|
| Build | `.\build.cmd` |
| Executar | `.\run.cmd --help` |
| Testes | `.\mvnw.cmd test` |
| Limpar | `.\mvnw.cmd clean` |
| Analisar | `.\run.cmd analyze --project .` |

---

**Dica:** Use `.\run.cmd --help` para ver todos os comandos disponíveis!
