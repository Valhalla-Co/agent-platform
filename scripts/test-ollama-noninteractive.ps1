# Non-interactive Ollama health check (writes human-readable output)
$ollamaExe = Join-Path $env:LOCALAPPDATA 'Programs\Ollama\ollama.exe'
Write-Host "OLLAMA EXE PATH: $ollamaExe"
if (Test-Path $ollamaExe) {
    try {
        $ver = & "$ollamaExe" --version 2>&1
        Write-Host "OLLAMA VERSION: $($ver -join ' ')"
    } catch {
        Write-Host "OLLAMA --version returned error: $($_.Exception.Message)"
    }
} else {
    Write-Host "OLLAMA NOT FOUND AT PATH"
}

# Check process
$proc = Get-Process -Name 'ollama' -ErrorAction SilentlyContinue
Write-Host "OLLAMA PROCESS RUNNING: $([bool]$proc)"

# Check HTTP API using UseBasicParsing to avoid interactive prompt
try {
    $resp = Invoke-WebRequest -Uri 'http://localhost:11434/api/tags' -UseBasicParsing -TimeoutSec 5
    Write-Host "API /api/tags: HTTP $($resp.StatusCode)"
    Write-Host "BODY:"
    Write-Host $resp.Content
} catch {
    Write-Host "API /api/tags: ERROR - $($_.Exception.Message)"
}

Write-Host "DONE"
