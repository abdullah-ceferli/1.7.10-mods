param([string]$JavaHome, [switch]$Offline, [switch]$SkipNative)
$ErrorActionPreference = 'Stop'
$projectPath = $PSScriptRoot
$pythonPath = Join-Path $env:USERPROFILE '.cache/codex-runtimes/codex-primary-runtime/dependencies/python/python.exe'
if (-not (Test-Path -LiteralPath $pythonPath)) { $pythonPath = (Get-Command python -ErrorAction Stop).Source }
$buildArguments = @((Join-Path $projectPath 'build.py'))
if ($JavaHome) { $buildArguments += @('--java-home', $JavaHome) }
if ($Offline) { $buildArguments += '--offline' }
if ($SkipNative) { $buildArguments += '--skip-native' }
& $pythonPath @buildArguments
if ($LASTEXITCODE -ne 0) { throw "Build failed with exit code $LASTEXITCODE" }
