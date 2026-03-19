# Check environment for building and running the agent-platform
Write-Host "Checking environment..."

# Java
if (Test-Path env:JAVA_HOME) {
    Write-Host "JAVA_HOME: $env:JAVA_HOME"
} else {
    Write-Host "WARNING: JAVA_HOME not set"
}

# Maven wrapper
if (Test-Path .\mvnw.cmd) {
    Write-Host "Found mvnw.cmd"
} else {
    Write-Host "WARNING: mvnw.cmd not found"
}

# Ollama
$ollama = Get-Command ollama -ErrorAction SilentlyContinue
if ($null -ne $ollama) {
    Write-Host "Ollama CLI found: $($ollama.Source)"
} else {
    Write-Host "Ollama CLI not found in PATH. If you use Ollama API, ensure OLLAMA_API_URL is set and service is running."
}

# Test API ping if OLLAMA_API_URL set
if ($env:OLLAMA_API_URL) {
    try {
        $resp = Invoke-RestMethod -Uri "$env:OLLAMA_API_URL/ping" -Method Get -TimeoutSec 3
        Write-Host "Ollama API reachable at $env:OLLAMA_API_URL"
    } catch {
        Write-Host "Ollama API not reachable at $env:OLLAMA_API_URL"
    }
} else {
    Write-Host "OLLAMA_API_URL not set; default assumed http://localhost:11434"
}
