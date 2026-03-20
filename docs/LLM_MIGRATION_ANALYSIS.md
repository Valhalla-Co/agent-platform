# 🧠 LLM Migration Analysis: Replacing Local Ollama

**Date:** 2026-03-20
**Status:** Draft / Proposal

## 🎯 Objective
Analyze the feasibility and benefits of removing the dependency on a local Ollama instance (`OllamaProvider`) and replacing it with a **free, lightweight, cloud-based alternative**.

## 🔴 Current State (Local Ollama)
*   **Architecture**: `OllamaProvider` connects to `localhost:11434`.
*   **Pros**: Complete data privacy, offline capability, cost-free (hardware bound).
*   **Cons**:
    *   **Hardware Usage**: Consumes significant RAM/CPU/GPU on the local machine.
    *   **Setup Friction**: Requires user to install Ollama and pull models manually.
    *   **Performance**: Limited by local hardware (slow token generation on non-GPU machines).

## 🔍 Alternatives Analysis

We evaluated providers based on: **Cost (Free Tier)**, **Latency/Speed**, and **Setup Simplicity** (Cloud-based).

### 1. 🚀 Groq Cloud API (Recommended)
*   **Type**: Cloud API (LPU Inference Engine).
*   **Models**: Llama 3 (8B, 70B), Mixtral 8x7b, Gemma.
*   **Cost**: Free tier currently available (generous limits).
*   **Pros**: **Extremely fast** (hundreds of tokens/sec), compatible with OpenAI SDKs structure, no local resource usage.
*   **Cons**: Requires API Key (Environment Variable `GROQ_API_KEY`).

### 2. ⚡ Google Gemini API
*   **Type**: Cloud API.
*   **Models**: Gemini 1.5 Flash.
*   **Cost**: Free tier available (rate limited but usable).
*   **Pros**: Huge context window (1M tokens), strong reasoning capabilities.
*   **Cons**: Different API structure (needs specific adapter or library), data privacy considerations in free tier.

### 3. 🤗 Hugging Face Inference API
*   **Type**: Cloud API.
*   **Models**: Various open models.
*   **Cost**: Free tier (CPU bound, heavily rate-limited).
*   **Pros**: Access to massive variety of models.
*   **Cons**: **Slow** on free tier, strict rate limits make it unstable for agentic workflows (chains of thought).

## 🏆 Recommendation: Groq

**Groq** is the best fit for "lightweight and free" because:
1.  It offloads all compute from the user's machine.
2.  It uses **Llama 3**, which is the same model family typically used locally with Ollama, ensuring prompt compatibility.
3.  The speed is superior to almost any local setup, improving the Agent feedback loop.

## 🛠️ Implementation Plan

1.  **Create New Module**: `llm-providers/groq-provider`.
2.  **Implementation**:
    *   Implement `LLMProvider` interface.
    *   Use `OkHttp` to post to `https://api.groq.com/openai/v1/chat/completions`.
    *   Map `chat()` to Groq's JSON payload.
3.  **Configuration**:
    *   Add `GROQ_API_KEY` detection in `check-environment.ps1` (optional warning).
    *   Update `run-odin.ps1` to prefer Groq if key is present.

## ⚠️ Migration Impact
*   **User Action Required**: Users must generate a free API Key at `console.groq.com`.
*   **Code Change**: Low impact. The `LLMProvider` interface isolates the change. Agents (Wayland/Mimir) won't know the difference.

---
**Decision**: Proceed with `GroqProvider` implementation?
