# Cleans UTF-8 BOM (EF BB BF) and U+FEFF characters from .java files under agent-core and builds the module
$ErrorActionPreference = 'Stop'
# Use safe paths (don't Resolve-Path on files that may not exist)
$report = Join-Path $PSScriptRoot '..\bom_fixed_list.txt'
$mvnlog = Join-Path $PSScriptRoot '..\mvn-agent-core-clean-build.txt'
# Ensure logs directory exists
$logsDir = Join-Path $PSScriptRoot '..\logs'
if (-not (Test-Path $logsDir)) { New-Item -ItemType Directory -Path $logsDir | Out-Null }
$report = Resolve-Path -Path $report -ErrorAction SilentlyContinue | Select-Object -First 1 -ExpandProperty Path -ErrorAction SilentlyContinue
if (-not $report) { $report = Join-Path $PSScriptRoot '..\bom_fixed_list.txt' }
$mvnlog = Resolve-Path -Path $mvnlog -ErrorAction SilentlyContinue | Select-Object -First 1 -ExpandProperty Path -ErrorAction SilentlyContinue
if (-not $mvnlog) { $mvnlog = Join-Path $PSScriptRoot '..\mvn-agent-core-clean-build.txt' }
if (Test-Path $report) { Remove-Item $report -Force }
if (Test-Path $mvnlog) { Remove-Item $mvnlog -Force }

Write-Host "Scanning .java files in agent-core..."
$files = Get-ChildItem -Path "..\agent-core\src\main\java" -Recurse -Filter *.java -ErrorAction SilentlyContinue
$fixed = 0
foreach ($f in $files) {
    $p = $f.FullName
    try {
        $bytes = [System.IO.File]::ReadAllBytes($p)
        $changed = $false
        # Remove leading UTF-8 BOM bytes
        if ($bytes.Length -ge 3 -and $bytes[0] -eq 0xEF -and $bytes[1] -eq 0xBB -and $bytes[2] -eq 0xBF) {
            $newBytes = $bytes[3..($bytes.Length - 1)]
            [System.IO.File]::WriteAllBytes($p, $newBytes)
            Add-Content -Path $report -Value "Removed leading BOM: $p"
            $changed = $true
        }
        # Remove any U+FEFF characters in decoded UTF8 text
        $text = [System.Text.Encoding]::UTF8.GetString([System.IO.File]::ReadAllBytes($p))
        if ($text.IndexOf([char]0xFEFF) -ge 0) {
            $clean = $text.Replace([char]0xFEFF, '')
            # Write as UTF8 without BOM
            [System.IO.File]::WriteAllText($p, $clean, (New-Object System.Text.UTF8Encoding $false))
            Add-Content -Path $report -Value "Removed U+FEFF characters: $p"
            $changed = $true
        }
        if (-not $changed) { Add-Content -Path $report -Value "No change: $p" }
        if ($changed) { $fixed++ }
    } catch {
        Add-Content -Path $report -Value "ERROR processing $p - $($_.Exception.Message)"
    }
}
Add-Content -Path $report -Value "Total files scanned: $($files.Count)"
Add-Content -Path $report -Value "Files modified: $fixed"

Write-Host "BOM cleanup finished. Report saved to: $report"
if (Test-Path $report) { Get-Content $report | Write-Host }

Write-Host 'Running mvn for module agent-core (skip tests) and saving log to:' $mvnlog
# Run mvnw from repo root
$mvw = Join-Path $PSScriptRoot '..\mvnw.cmd'
try {
    & $mvw -pl :agent-core -DskipTests=true -e clean package > $mvnlog 2>&1
    Write-Host 'MAVEN_EXIT=' $LASTEXITCODE
    Write-Host "Maven log saved to: $mvnlog"
} catch {
    Write-Host 'ERROR running mvnw:' $_.Exception.Message
    Add-Content -Path $mvnlog -Value "ERROR running mvnw: $($_.Exception.Message)"
}