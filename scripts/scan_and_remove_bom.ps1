# Scan and remove UTF-8 BOM (EF BB BF) and U+FEFF from .java files under agent-core
$ErrorActionPreference = 'Stop'
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$root = Resolve-Path (Join-Path $scriptDir '..')
$report = Join-Path $root 'bom_scan_report.txt'
$mvnlog = Join-Path $root 'mvn-after-bom-scan.txt'
if (Test-Path $report) { Remove-Item $report -Force }
if (Test-Path $mvnlog) { Remove-Item $mvnlog -Force }

Write-Host "Scanning Java files under: " (Join-Path $root 'agent-core\src\main\java')
$files = Get-ChildItem -Path (Join-Path $root 'agent-core\src\main\java') -Recurse -Filter *.java -ErrorAction SilentlyContinue
$bomFiles = @()
$fixedCount = 0
foreach ($f in $files) {
    $p = $f.FullName
    try {
        # Read first 4 bytes safely
        $fs = [System.IO.File]::OpenRead($p)
        $buffer = New-Object byte[] 4
        $read = $fs.Read($buffer, 0, $buffer.Length)
        $fs.Close()

        $hasLeadingBOM = ($read -ge 3 -and $buffer[0] -eq 0xEF -and $buffer[1] -eq 0xBB -and $buffer[2] -eq 0xBF)
        $hadUfeffInside = $false

        if ($hasLeadingBOM) {
            Add-Content -Path $report -Value "Leading BOM found: $p"
            # Remove leading 3 bytes
            $all = [System.IO.File]::ReadAllBytes($p)
            $new = $all[3..($all.Length - 1)]
            [System.IO.File]::WriteAllBytes($p, $new)
            Add-Content -Path $report -Value "Removed leading BOM: $p"
            $fixedCount++
        }

        # Check for U+FEFF inside text and remove
        $text = Get-Content -Raw -Encoding UTF8 $p
        if ($text.IndexOf([char]0xFEFF) -ge 0) {
            $hadUfeffInside = $true
            $clean = $text.Replace([char]0xFEFF, '')
            [System.IO.File]::WriteAllText($p, $clean, (New-Object System.Text.UTF8Encoding $false))
            Add-Content -Path $report -Value "Removed internal U+FEFF from: $p"
            if (-not $hasLeadingBOM) { $fixedCount++ }
        }

        if (-not $hasLeadingBOM -and -not $hadUfeffInside) {
            Add-Content -Path $report -Value "No BOM: $p"
        }
    } catch {
        Add-Content -Path $report -Value "ERROR processing $p - $($_.Exception.Message)"
    }
}
Add-Content -Path $report -Value "Total files scanned: $($files.Count)"
Add-Content -Path $report -Value "Files modified: $fixedCount"
Write-Host "BOM scan/cleanup done. Report: $report"
Get-Content $report | Select-Object -First 200 | ForEach-Object { Write-Host $_ }

# Run maven build for agent-core
Write-Host "Running mvn for :agent-core (skip tests). Log: $mvnlog"
& (Join-Path $root 'mvnw.cmd') -pl :agent-core -DskipTests=true -e clean package > $mvnlog 2>&1
Write-Host 'MAVEN_EXIT=' $LASTEXITCODE
Write-Host "Maven log saved to: $mvnlog"