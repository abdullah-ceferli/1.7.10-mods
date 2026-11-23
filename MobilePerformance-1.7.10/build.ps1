param([string[]]$Tasks=@('test','testModJar','build','sourcesJar','-PintegrationTest'))
$ErrorActionPreference='Stop'
$taskShared=Join-Path (Split-Path $PSScriptRoot -Parent) 'industrialMOD'
$taskJdk=Join-Path $taskShared '.toolchain/jdk8/jdk8u504-b01'
if(-not (Test-Path -LiteralPath (Join-Path $taskJdk 'bin/javac.exe'))){$taskJdk=$env:JAVA_HOME}
if(-not $taskJdk){throw 'Set JAVA_HOME to a Java 8 JDK.'}
$env:JAVA_HOME=$taskJdk
$taskArgs=@()
if(Test-Path -LiteralPath (Join-Path $taskShared '.gradle-user')){$env:GRADLE_USER_HOME=Join-Path $taskShared '.gradle-user';$taskArgs=@('--offline')}
& (Join-Path $PSScriptRoot 'gradlew.bat') -p $PSScriptRoot @Tasks @taskArgs --console=plain
if($LASTEXITCODE -ne 0){throw "Gradle failed: $LASTEXITCODE"}
