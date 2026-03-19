$out = Join-Path (Resolve-Path ..).ProviderPath 'hex_report.txt'
if (Test-Path $out) { Remove-Item $out -Force }
$files = @(
    'agent-core\src\main\java\br\com\valhalla\core\llm\LLMResponse.java',
    'agent-core\src\main\java\br\com\valhalla\core\llm\LLMConfig.java',
    'agent-core\src\main\java\br\com\valhalla\core\repository\GitConfig.java',
    'agent-core\src\main\java\br\com\valhalla\core\repository\WorkspaceManager.java',
    'agent-core\src\main\java\br\com\valhalla\core\brain\Lesson.java',
    'agent-core\src\main\java\br\com\valhalla\core\llm\PromptTemplate.java',
    'agent-core\src\main\java\br\com\valhalla\core\llm\LLMProvider.java',
    'agent-core\src\main\java\br\com\valhalla\core\brain\KnowledgeBase.java',
    'agent-core\src\main\java\br\com\valhalla\core\repository\RepositoryManager.java',
    'agent-core\src\main\java\br\com\valhalla\core\brain\LessonRepository.java',
    'agent-core\src\main\java\br\com\valhalla\core\brain\LessonParser.java'
)
foreach ($f in $files) {
    $full = Join-Path (Resolve-Path .).ProviderPath $f
    if (Test-Path $full) {
        $b = [System.IO.File]::ReadAllBytes($full)
        $len = [math]::Min(16, $b.Length)
        $hex = ($b[0..($len-1)] | ForEach-Object { '{0:X2}' -f $_ }) -join ' '
        Add-Content $out "FILE: $f"
        Add-Content $out "HEX: $hex"
    } else {
        Add-Content $out "MISSING: $f"
    }
}
Write-Host "WROTE: $out"
