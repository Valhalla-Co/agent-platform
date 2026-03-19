param(
    [Parameter(Mandatory=$true)][string]$agentName,
    [Parameter(Mandatory=$true)][string]$taskName
)

# Creates a branch skeleton under branches/<agentName>/<timestamp>-<taskName>
$timestamp = Get-Date -Format 'yyyyMMdd-HHmmss'
$branchDir = Join-Path -Path (Join-Path $PSScriptRoot '..\branches') -ChildPath "$agentName\$timestamp-$taskName"
if (-not (Test-Path $branchDir)) { New-Item -ItemType Directory -Path $branchDir -Force | Out-Null }
# Initialize minimal repo copy (git not required) - create placeholder
New-Item -ItemType File -Path (Join-Path $branchDir 'README.md') -Force -Value "Branch created for agent $agentName on $timestamp - task: $taskName"
Write-Host "Branch skeleton created: $branchDir"
Write-Host "To use: copy repo files into this folder, then execute builds locally by agents."
