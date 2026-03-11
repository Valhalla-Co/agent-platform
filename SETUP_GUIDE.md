# 🚀 Guia de Inicialização do Projeto Agent Platform

## ✅ Arquivos de Setup Criados

Os seguintes arquivos foram criados para facilitar a inicialização do projeto:

- ✅ `.mvn/wrapper/maven-wrapper.properties` - Configuração do Maven Wrapper
- ✅ `mvnw` - Script Maven Wrapper para Linux/Mac
- ✅ `mvnw.cmd` - Script Maven Wrapper para Windows

## 📋 Pré-requisitos

Para executar este projeto, você precisa ter o **Java Development Kit (JDK) 21** instalado.

### 🔧 Instalando o JDK 21

Escolha uma das opções abaixo:

#### Opção 1: Eclipse Temurin (Recomendado)
1. Acesse: https://adoptium.net/temurin/releases/?version=21
2. Baixe o instalador para Windows (x64)
3. Execute o instalador e marque a opção "Set JAVA_HOME variable"
4. Marque também "Add to PATH"

#### Opção 2: Oracle JDK
1. Acesse: https://www.oracle.com/java/technologies/downloads/#java21
2. Baixe o instalador para Windows
3. Execute o instalador

#### Opção 3: Microsoft OpenJDK
1. Acesse: https://www.microsoft.com/openjdk
2. Baixe o instalador do JDK 21 para Windows
3. Execute o instalador

### ⚙️ Configurando JAVA_HOME Manualmente

Se o instalador não configurar automaticamente, siga estes passos:

1. Abra o PowerShell como Administrador
2. Execute os comandos (ajuste o caminho conforme sua instalação):

```powershell
# Defina JAVA_HOME (exemplo com Eclipse Temurin)
[System.Environment]::SetEnvironmentVariable("JAVA_HOME", "C:\Program Files\Eclipse Adoptium\jdk-21.0.1.12-hotspot", [System.EnvironmentVariableTarget]::Machine)

# Adicione ao PATH
$path = [System.Environment]::GetEnvironmentVariable("Path", [System.EnvironmentVariableTarget]::Machine)
[System.Environment]::SetEnvironmentVariable("Path", "$path;%JAVA_HOME%\bin", [System.EnvironmentVariableTarget]::Machine)
```

3. Feche e reabra o PowerShell/Terminal

4. Verifique a instalação:
```powershell
java -version
```

## 🏗️ Construindo o Projeto

Após instalar o Java, execute:

```powershell
cd C:\Users\xleos\Documents\projects\agent-platform
.\mvnw.cmd clean install
```

O Maven Wrapper irá:
1. ✅ Baixar o Maven automaticamente (primeira execução)
2. ✅ Compilar todos os módulos do projeto
3. ✅ Executar os testes
4. ✅ Gerar os arquivos JAR

## 📦 Estrutura do Projeto

```
agent-platform/
├── agent-core/          # Biblioteca core com modelos de domínio e ports
└── agent-base/          # CLI e engine de orquestração
```

## 🎯 Executando a Aplicação

Após a construção bem-sucedida, você pode executar o CLI:

```powershell
java -jar agent-base\target\agent-base-1.0.0-SNAPSHOT.jar analyze --project .\my-project
```

### Comandos Disponíveis

```powershell
# Análise de código
java -jar agent-base\target\agent-base-1.0.0-SNAPSHOT.jar analyze --project <caminho>

# Review de arquitetura
java -jar agent-base\target\agent-base-1.0.0-SNAPSHOT.jar review --project <caminho>

# Sugestões de melhoria
java -jar agent-base\target\agent-base-1.0.0-SNAPSHOT.jar improve --project <caminho>
```

## 🧪 Executando os Testes

```powershell
# Todos os testes
.\mvnw.cmd test

# Apenas um módulo
.\mvnw.cmd test -pl agent-core
```

## 📝 Desenvolvimento

### Compilação Rápida (sem testes)
```powershell
.\mvnw.cmd clean install -DskipTests
```

### Limpeza
```powershell
.\mvnw.cmd clean
```

### Atualizar Dependências
```powershell
.\mvnw.cmd versions:display-dependency-updates
```

## 🐛 Solução de Problemas

### Erro: "JAVA_HOME not found"
- Certifique-se de que o Java está instalado
- Verifique se JAVA_HOME está configurado corretamente
- Reinicie o terminal após configurar as variáveis de ambiente

### Erro de compilação
- Execute: `.\mvnw.cmd clean install -U` (força atualização de dependências)
- Verifique se está usando Java 21 ou superior

### Problemas de rede
- Se estiver atrás de um proxy, configure no arquivo `~/.m2/settings.xml`

## 📚 Recursos Adicionais

- [Documentação do Maven](https://maven.apache.org/guides/)
- [Java 21 Documentation](https://docs.oracle.com/en/java/javase/21/)
- [Picocli Documentation](https://picocli.info/)

## ✨ Próximos Passos

1. ✅ Instalar Java JDK 21
2. ✅ Executar `.\mvnw.cmd clean install`
3. ✅ Testar o CLI com `java -jar agent-base\target\agent-base-1.0.0-SNAPSHOT.jar --help`
4. 🚀 Começar a desenvolver!

---

**Nota**: Este projeto usa Java 21 e Maven 3.9.6. Certifique-se de ter essas versões ou superiores instaladas.
