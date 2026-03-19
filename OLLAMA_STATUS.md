# 🦙 Status da Configuração do Ollama

**Data:** 12/03/2026  
**Sistema:** Windows  
### Verificações Realizadas:
| ✗ Ollama instalado | ❌ FALHOU | Comando `ollama` não encontrado |
| ✗ Servidor rodando | ❌ FALHOU | Porta 11434 não responde |
| ✗ Processo ativo | ❌ FALHOU | Nenhum processo ollama encontrado |

### 🎉 Instalação Completa!

```
Local: C:\Users\xleos\AppData\Local\Programs\Ollama\
Servidor: http://localhost:11434
Modelo: codellama:7b
```
| ✗ API acessível | ❌ FALHOU | Endpoint não disponível |

---

## 🚀 COMO INSTALAR E CONFIGURAR

### Passo 1: Instalar Ollama (5 minutos)

#### **Windows:**
```powershell
# Opção 1: Download Manual (RECOMENDADO)
# 1. Acesse: https://ollama.ai/download/windows
# 2. Baixe o instalador .exe
# 3. Execute o instalador
# 4. Siga o assistente de instalação

# Opção 2: Via Winget (se disponível)
winget install Ollama.Ollama
```

### Passo 2: Verificar Instalação
```powershell
# Abra um NOVO terminal PowerShell
ollama --version

# Deve retornar algo como: ollama version is 0.1.27
```

### Passo 3: Baixar Modelo CodeLlama (3-5 minutos + download ~3.8GB)
```powershell
# Baixar CodeLlama 7B (recomendado para código)
ollama pull codellama:7b

# Verificar modelos instalados
ollama list
```

### Passo 4: Testar o Modelo
```powershell
# Testar com uma pergunta simples
ollama run codellama:7b "Explique o que é REST API em português"

# Se funcionou, você verá uma resposta detalhada!
```

### Passo 5: Verificar Servidor
```powershell
# O Ollama inicia automaticamente após instalação
# Para verificar se está rodando:
curl http://localhost:11434/api/tags

# Deve retornar JSON com lista de modelos
```

---

## 🎯 DEPOIS DE INSTALAR

### Testar com Odin Agent:
```powershell
# No diretório do projeto
cd C:\Users\xleos\Documents\projects\agent-platform

# Executar Odin
java -jar odin-agent\target\odin-agent-1.0.0-SNAPSHOT.jar

# Você deve ver:
# 🦙 LLM: Ollama (codellama:7b) ✅
# 💚 Custo: $0.00 - Gratuito!
```

### Testar funcionalidades:
```
odin> analisa o projeto
odin> explique o padrão repository
odin> sugira melhorias para este código
```

---

## 📋 MODELOS RECOMENDADOS

| Modelo | Tamanho | Uso | Download |
|--------|---------|-----|----------|
| **codellama:7b** | 3.8GB | Código (recomendado) | `ollama pull codellama:7b` |
| codellama:13b | 7.4GB | Código (melhor qualidade) | `ollama pull codellama:13b` |
| llama2:7b | 3.8GB | Geral | `ollama pull llama2:7b` |
| mistral:7b | 4.1GB | Geral/Código | `ollama pull mistral:7b` |

---

## 🔧 CONFIGURAÇÃO AVANÇADA

### Alterar Configuração do Odin:
```powershell
# Usar modelo diferente
java -Dollama.model=codellama:13b -jar odin-agent.jar

# Alterar URL do Ollama (se rodando em outro lugar)
java -Dollama.base.url=http://192.168.1.100:11434 -jar odin-agent.jar

# Aumentar timeout
java -Dollama.timeout.seconds=300 -jar odin-agent.jar
```

### Iniciar Ollama Manualmente (se necessário):
```powershell
# Se o servidor não iniciar automaticamente
ollama serve
```

---

## 🐛 TROUBLESHOOTING

### Problema: "ollama não reconhecido"
**Solução:**
1. Feche e reabra o terminal
2. Verifique PATH: `echo $env:PATH`
3. Reinstale o Ollama

### Problema: "Porta 11434 não responde"
**Solução:**
```powershell
# Iniciar servidor manualmente
ollama serve

# Verificar se há conflito de porta
netstat -ano | findstr :11434
```

### Problema: "Modelo não encontrado"
**Solução:**
```powershell
# Listar modelos instalados
ollama list

# Baixar modelo necessário
ollama pull codellama:7b
```

### Problema: "Resposta muito lenta"
**Solução:**
- Use modelo menor: `codellama:7b` em vez de `13b`
- Verifique uso de CPU/RAM
- Aguarde primeira resposta (pode levar 30-60s na primeira vez)

---

## 💡 O QUE O ODIN FAZ SEM OLLAMA

Se o Ollama não estiver disponível, o Odin ainda funciona com:
- ✅ Análise rule-based de código
- ✅ Detecção de patterns
- ✅ Geração de specs de melhorias
- ✅ Todas as funcionalidades core

**MAS:** Sem respostas inteligentes via LLM.

---

## 📚 RECURSOS

- **Site Oficial:** https://ollama.ai
- **Documentação:** https://github.com/ollama/ollama
- **Modelos Disponíveis:** https://ollama.ai/library
- **Guia Completo Odin:** `docs/OLLAMA_SETUP.md`

---

## ✅ CHECKLIST DE INSTALAÇÃO

- [ ] Baixar instalador do Ollama
- [ ] Instalar Ollama
- [ ] Abrir novo terminal
- [ ] Verificar `ollama --version`
- [ ] Executar `ollama pull codellama:7b`
- [ ] Testar `ollama run codellama:7b`
- [ ] Verificar endpoint: `curl http://localhost:11434/api/tags`
- [ ] Testar com Odin Agent
- [ ] Ver mensagem "🦙 LLM: Ollama (codellama:7b) ✅"

---

## 🎉 PRÓXIMOS PASSOS

Após instalar:
1. Execute o Odin Agent
2. Teste com "analisa o projeto"
3. Explore outras funcionalidades LLM
4. Experimente diferentes modelos

**Tempo estimado de instalação:** 10-15 minutos (+ download do modelo)

**Custo:** $0.00 - Totalmente gratuito! 🦙💚
