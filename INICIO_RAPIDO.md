# 🎯 Início Rápido - Agent Platform

## ⚠️ Requisito Principal

**É necessário ter o Java JDK 21 instalado!**

### Verificar se o Java está instalado:
```powershell
java -version
```

Se o comando acima falhar, você precisa instalar o Java JDK 21.

## 📥 Instalando Java JDK 21

### Opção Mais Fácil: Eclipse Temurin
1. Acesse: https://adoptium.net/temurin/releases/?version=21
2. Baixe o instalador Windows x64 (.msi)
3. Durante a instalação:
   - ✅ Marque "Set JAVA_HOME variable"
   - ✅ Marque "Add to PATH"
4. Clique em "Install"

### Verificar instalação:
Após instalar, **feche e reabra o terminal**, então execute:
```powershell
java -version
```

Deve mostrar algo como: `openjdk version "21.0.x"`

## 🚀 Construir o Projeto

```powershell
# No diretório do projeto
.\mvnw.cmd clean install
```

O Maven Wrapper irá baixar o Maven automaticamente na primeira execução!

## ✅ Executar a Aplicação

Após a construção bem-sucedida, você pode executar:

```powershell
java -jar agent-base\target\agent-base-1.0.0-SNAPSHOT.jar --help
```

### Comandos Disponíveis:

```powershell
# Análise de código
java -jar agent-base\target\agent-base-1.0.0-SNAPSHOT.jar analyze --project .\seu-projeto

# Review de arquitetura
java -jar agent-base\target\agent-base-1.0.0-SNAPSHOT.jar review --project .\seu-projeto

# Sugestões de melhoria
java -jar agent-base\target\agent-base-1.0.0-SNAPSHOT.jar improve --project .\seu-projeto
```

---

📖 Para mais detalhes, consulte o [SETUP_GUIDE.md](SETUP_GUIDE.md)
