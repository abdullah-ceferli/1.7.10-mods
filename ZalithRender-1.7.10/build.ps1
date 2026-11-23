param([string]$JavaHome=$env:JAVA_HOME,[string]$Cache,[switch]$Offline,[switch]$SkipNative)
$ErrorActionPreference='Stop'
$sibling=Join-Path (Split-Path $PSScriptRoot -Parent) 'industrialMOD'
if(-not $JavaHome -and (Test-Path -LiteralPath (Join-Path $sibling '.toolchain\jdk8'))) {
    $JavaHome=(Get-ChildItem -LiteralPath (Join-Path $sibling '.toolchain\jdk8') -Directory | Select-Object -First 1).FullName
}
if(-not $JavaHome){throw 'Set -JavaHome to a Java 8 JDK.'}
if(-not $Cache){
    $shared=Join-Path $sibling '.gradle-user'
    if(Test-Path -LiteralPath $shared){$Cache=$shared}else{$Cache=Join-Path $PSScriptRoot '.gradle-user'}
}
$api=Join-Path $Cache 'caches\minecraft\net\minecraftforge\forge\1.7.10-10.13.4.1614-1.7.10\forgeSrc-1.7.10-10.13.4.1614-1.7.10.jar'
if(-not (Test-Path -LiteralPath $api)){
    if($Offline){throw 'The Forge development cache is missing; prepare it once without -Offline.'}
    $env:JAVA_HOME=$JavaHome
    & (Join-Path $PSScriptRoot 'gradlew.bat') --gradle-user-home $Cache setupDecompWorkspace --console=plain
    if($LASTEXITCODE -ne 0){throw 'Forge workspace preparation failed.'}
}
$buildArgs=@((Join-Path $PSScriptRoot 'build.py'),'--java-home',$JavaHome,'--cache',$Cache)
if($Offline){$buildArgs+='--offline'}
if($SkipNative){$buildArgs+='--skip-native'}
& python @buildArgs
if($LASTEXITCODE -ne 0){throw "Build failed with exit code $LASTEXITCODE"}
