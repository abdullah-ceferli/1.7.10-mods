param([string[]]$Tasks=@('build'),[switch]$UseExistingCache)
$ErrorActionPreference='Stop'
$taskRoot=[IO.Path]::GetFullPath((Join-Path $PSScriptRoot '..'))
$taskConfig=Get-Content -LiteralPath (Join-Path $taskRoot '.toolchain/toolchain.json') -Raw | ConvertFrom-Json
if(-not (Test-Path -LiteralPath $taskConfig.javac)){throw 'Java 8 JDK is required; update .toolchain/toolchain.json.'}
$env:JAVA_HOME=Split-Path (Split-Path $taskConfig.javac -Parent) -Parent
$env:GRADLE_USER_HOME=Join-Path $taskRoot '.gradle-user'
if($UseExistingCache){$env:GRADLE_USER_HOME=$taskConfig.existingGradleCache}
Push-Location $taskRoot
try { & .\gradlew.bat @Tasks --console=plain; if($LASTEXITCODE -ne 0){throw "Build failed: $LASTEXITCODE. See docs/PORT_STATUS.md; no playable release is available yet."} } finally { Pop-Location }
