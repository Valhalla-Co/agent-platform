# 🦙 Guia de Integração Ollama - Odin Agent

**Data:** 12/03/2026  
**Status:** ⚡ PRONTO PARA COMEÇAR  
**Custo:** 💚 $0.00 - Totalmente Gratuito!

---

## ✅ O Que Você Precisa Fazer

### 1. Instalar Ollama (10 minutos)

#### Windows:
```powershell
# 1. Download
# Acesse: https://ollama.ai/download/windows

# 2. Instalar o .exe

# 3. Verificar instalação
ollama --version
```

### 2. Baixar Modelo CodeLlama (5 min + download 3.8GB)
```powershell
# CodeLlama 7B - Recomendado para código
ollama pull codellama:7b

# Verificar download
ollama list
```

### 3. Testar Ollama (2 minutos)
```powershell
# Testar o modelo
ollama run codellama:7b "Explique o que é REST API em português"

# Se funcionou, você verá uma resposta detalhada!
```

### 4. Verificar Servidor
- Ollama inicia automaticamente na instalação
- Servidor: `http://localhost:11434`
- Para testar API: `curl http://localhost:11434/api/tags`

---

## 📦 Adicionar Dependências ao Projeto

Editar `odin-agent/pom.xml` e adicionar:

```xml
<!-- Cliente HTTP para Ollama -->
<dependency>
    <groupId>com.squareup.okhttp3</groupId>
    <artifactId>okhttp</artifactId>
    <version>4.12.0</version>
</dependency>
```

Recompilar:
```powershell
.\mvnw.cmd clean install -DskipTests
```

---

## 💻 Implementação

### Estrutura de Pastas
```
odin-agent/src/main/java/br/com/valhalla/odin/
└── llm/
    ├── LLMProvider.java          (interface)
    ├── LLMRequest.java           (model)
    ├── LLMResponse.java          (model)
    ├── OllamaProvider.java       (implementação)
    ├── LLMConfig.java            (configuração)
    └── PromptTemplate.java       (templates)
```

### 1. Interface LLMProvider
```java
package br.com.valhalla.odin.llm;

public interface LLMProvider {
    LLMResponse chat(String prompt);
    LLMResponse analyze(String code, String instruction);
    boolean isAvailable();
    String getProviderName();
}
```

### 2. Model LLMResponse
```java
package br.com.valhalla.odin.llm;

public class LLMResponse {
    private String content;
    private boolean success;
    private String error;
    private long responseTimeMs;
    private int tokensUsed;
    
    public static LLMResponse success(String content, long time) {
        LLMResponse response = new LLMResponse();
        response.content = content;
        response.success = true;
        response.responseTimeMs = time;
        return response;
    }
    
    public static LLMResponse error(String error) {
        LLMResponse response = new LLMResponse();
        response.success = false;
        response.error = error;
        return response;
    }
    
    // Getters
    public String getContent() { return content; }
    public boolean isSuccess() { return success; }
    public String getError() { return error; }
    public long getResponseTimeMs() { return responseTimeMs; }
}
```

### 3. OllamaProvider (Implementação Principal)
```java
package br.com.valhalla.odin.llm;

import okhttp3.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.Map;

public class OllamaProvider implements LLMProvider {
    private static final String DEFAULT_URL = "http://localhost:11434";
    private static final String DEFAULT_MODEL = "codellama:7b";
    
    private final OkHttpClient client;
    private final ObjectMapper mapper;
    private final String baseUrl;
    private final String model;
    
    public OllamaProvider() {
        this(DEFAULT_URL, DEFAULT_MODEL);
    }
    
    public OllamaProvider(String baseUrl, String model) {
        this.baseUrl = baseUrl;
        this.model = model;
        this.mapper = new ObjectMapper();
        this.client = new OkHttpClient.Builder()
            .connectTimeout(Duration.ofSeconds(30))
            .readTimeout(Duration.ofMinutes(5))
            .build();
    }
    
    @Override
    public LLMResponse chat(String prompt) {
        try {
            // Montar request JSON
            Map<String, Object> requestBody = Map.of(
                "model", model,
                "prompt", prompt,
                "stream", false
            );
            
            String json = mapper.writeValueAsString(requestBody);
            
            Request request = new Request.Builder()
                .url(baseUrl + "/api/generate")
                .post(RequestBody.create(json, MediaType.get("application/json")))
                .build();
            
            long startTime = System.currentTimeMillis();
            
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    return LLMResponse.error("HTTP " + response.code());
                }
                
                String responseBody = response.body().string();
                Map<String, Object> result = mapper.readValue(responseBody, Map.class);
                
                String content = (String) result.get("response");
                long responseTime = System.currentTimeMillis() - startTime;
                
                return LLMResponse.success(content, responseTime);
            }
        } catch (Exception e) {
            return LLMResponse.error("Erro ao chamar Ollama: " + e.getMessage());
        }
    }
    
    @Override
    public LLMResponse analyze(String code, String instruction) {
        String prompt = String.format("""
            Você é Odin, um especialista em análise de código.
            
            Instrução: %s
            
            Código:
            ```
            %s
            ```
            
            Forneça sugestões específicas e práticas.
            """, instruction, code);
        
        return chat(prompt);
    }
    
    @Override
    public boolean isAvailable() {
        try {
            Request request = new Request.Builder()
                .url(baseUrl + "/api/tags")
                .build();
            
            try (Response response = client.newCall(request).execute()) {
                return response.isSuccessful();
            }
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public String getProviderName() {
        return "Ollama (" + model + ")";
    }
}
```

---

## 🔗 Integrar com OdinCli

Atualizar `OdinCli.processPrompt()`:

```java
private void processPrompt(String prompt, OdinAgent odin, String workingDir) {
    // Inicializar Ollama
    OllamaProvider llm = new OllamaProvider();
    
    if (!llm.isAvailable()) {
        System.out.println("❌ Ollama não está disponível.");
        System.out.println("   Certifique-se que Ollama está rodando.");
        System.out.println("   Comando: ollama serve");
        return;
    }
    
    String lowerPrompt = prompt.toLowerCase();
    
    // Detectar tipo de solicitação
    if (lowerPrompt.contains("analisa") || lowerPrompt.contains("analyze")) {
        System.out.println("🤔 Processando com Ollama...\n");
        
        String analysisPrompt = String.format(
            "Analise o projeto em '%s' e sugira melhorias específicas.", 
            workingDir
        );
        
        LLMResponse response = llm.chat(analysisPrompt);
        
        if (response.isSuccess()) {
            System.out.println("📊 Análise:\n");
            System.out.println(response.getContent());
            System.out.println("\n⚡ Tempo: " + response.getResponseTimeMs() + "ms");
        } else {
            System.err.println("❌ Erro: " + response.getError());
        }
        return;
    }
    
    // Prompt genérico
    System.out.println("🤔 Processando com Ollama...\n");
    LLMResponse response = llm.chat(prompt);
    
    if (response.isSuccess()) {
        System.out.println(response.getContent());
    } else {
        System.err.println("❌ Erro: " + response.getError());
    }
}
```

---

## 🎨 Exemplo de Uso

```bash
# Iniciar Odin
$ java -jar odin-agent.jar

╔══════════════════════════════════════════════════════════════╗
║                         ODIN AGENT                           ║
╚══════════════════════════════════════════════════════════════╝

🔱 Status: INICIALIZADO
👂 Modo: ESCUTANDO
🦙 LLM: Ollama (codellama:7b) ✅

🔱 odin> analise este projeto

🤔 Processando com Ollama...

📊 Análise:

Baseado na estrutura do projeto, identifiquei as seguintes melhorias:

1. **Testes Unitários** (Alta Prioridade)
   - Nenhum teste detectado
   - Recomendo implementar testes com JUnit 5
   - Focar em classes core primeiro

2. **Documentação de Código**
   - Falta JavaDoc em classes públicas
   - Adicionar comments em métodos complexos

3. **Error Handling**
   - Exceções genéricas sendo usadas
   - Criar hierarquia de exceções customizadas

4. **Logging**
   - Usar SLF4J de forma consistente
   - Adicionar níveis apropriados (DEBUG, INFO, ERROR)

⚡ Tempo: 18543ms | Custo: $0.00

🔱 odin> o que é o padrão repository?

🤔 Processando com Ollama...

O Repository Pattern é um padrão de design que abstrai o acesso
a dados, fornecendo uma interface entre a lógica de negócio e
a camada de persistência.

Benefícios:
✅ Desacoplamento
✅ Facilita testes (mocking)
✅ Centraliza queries
✅ Melhora manutenibilidade

Exemplo no seu projeto:
- LessonRepository: Gerencia acesso às lessons
- Abstrai se vem de arquivo, banco, API, etc.

🔱 odin> exit

👋 Odin finalizando... Até logo!
```

---

## 📊 Modelos Disponíveis

### Para Código (Recomendado):
```powershell
# CodeLlama 7B - Rápido, bom para código
ollama pull codellama:7b          # 3.8GB

# CodeLlama 13B - Melhor qualidade
ollama pull codellama:13b         # 7.3GB
```

### Uso Geral:
```powershell
# Llama 3 8B - Mais inteligente
ollama pull llama3:8b             # 4.7GB

# Mistral 7B - Balanceado
ollama pull mistral:7b            # 4.1GB
```

### Trocar Modelo:
Editar em `OllamaProvider`:
```java
private static final String DEFAULT_MODEL = "llama3:8b";
```

---

## 🔧 Troubleshooting

### Ollama não responde:
```powershell
# Verificar se está rodando
curl http://localhost:11434/api/tags

# Se não estiver, iniciar
ollama serve

# Em outro terminal, testar
ollama list
```

### Modelo não encontrado:
```powershell
# Listar modelos instalados
ollama list

# Baixar modelo
ollama pull codellama:7b
```

### Resposta muito lenta:
- Modelos 7B: ~10-30s
- Modelos 13B: ~30-60s
- Com GPU: 2-5x mais rápido

### Falta de memória:
```powershell
# Usar modelo menor
ollama pull codellama:7b  # ao invés de 13b
```

---

## ✅ Checklist Rápido

### Setup (20 min):
- [ ] Instalar Ollama
- [ ] Baixar `codellama:7b`
- [ ] Testar com `ollama run`
- [ ] Verificar servidor

### Código (1 dia):
- [ ] Adicionar OkHttp ao `pom.xml`
- [ ] Criar pacote `llm/`
- [ ] Implementar `LLMProvider` interface
- [ ] Implementar `OllamaProvider`
- [ ] Integrar com `OdinCli`
- [ ] Testar end-to-end

### Finalização:
- [ ] Documentar no README
- [ ] Adicionar exemplos
- [ ] Testar diferentes prompts

---

## 🎯 Próximo Passo

**AGORA:** Instalar Ollama

```powershell
# 1. Download
# https://ollama.ai/download/windows

# 2. Instalar

# 3. Baixar modelo
ollama pull codellama:7b

# 4. Testar
ollama run codellama:7b "Hello"
```

**DEPOIS:** Implementar `OllamaProvider`

---

## 📚 Links Úteis

- 🦙 [Ollama Download](https://ollama.ai/download)
- 📖 [Documentação](https://github.com/ollama/ollama/tree/main/docs)
- 🔌 [API Reference](https://github.com/ollama/ollama/blob/main/docs/api.md)
- 📦 [Library de Modelos](https://ollama.ai/library)
- 🔧 [OkHttp](https://square.github.io/okhttp/)

---

**Atualizado:** 12/03/2026  
**Próximo:** `ollama pull codellama:7b` 🚀
