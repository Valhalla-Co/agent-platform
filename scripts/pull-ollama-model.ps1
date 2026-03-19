param(
    [string]$Model = "codellama:3b"
)

$ErrorActionPreference = "Stop"

$ollamaExe = Join-Path $env:LOCALAPPDATA "Programs\Ollama\ollama.exe"
Write-Host "OLLAMA EXE PATH: $ollamaExe"

if (!(Test-Path $ollamaExe)) {
    throw "ollama.exe not found at $ollamaExe"
}

Write-Host "Pulling model: $Model"
& $ollamaExe pull $Model

Write-Host "Done pulling: $Model"

