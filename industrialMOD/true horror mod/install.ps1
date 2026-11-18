param([string]$MinecraftDirectory = 'D:\programs\Maincraft\Minecraft\game')
$ErrorActionPreference = 'Stop'
$gamePath = [IO.Path]::GetFullPath($MinecraftDirectory)
$modsPath = Join-Path $gamePath 'mods'
if (!(Test-Path -LiteralPath $modsPath -PathType Container)) { throw 'Minecraft mods directory was not found.' }
$runningGames = @(Get-CimInstance Win32_Process -Filter "Name='java.exe' OR Name='javaw.exe'" | Where-Object {
    $_.CommandLine -match 'launchwrapper\.Launch|net\.minecraft\.client\.main|ServerLaunchWrapper|MinecraftServer'
})
if ($runningGames.Count -gt 0) { throw 'Close Minecraft before installing. No game files were changed.' }
$sourceJar = Join-Path $PSScriptRoot 'build\libs\TrueHorror-1.7.10-1.0.0.jar'
if (!(Test-Path -LiteralPath $sourceJar)) { throw 'Build True Horror first.' }
$destination = Join-Path $modsPath (Split-Path $sourceJar -Leaf)
$previous = @(Get-ChildItem -LiteralPath $modsPath -File | Where-Object { $_.Name -match '^TrueHorror-1\.7\.10-.*\.jar$' })
$backup = $null
if ($previous.Count -gt 0) {
    $backup = Join-Path $gamePath ('mod-backups\TrueHorror-' + (Get-Date -Format 'yyyyMMdd-HHmmss'))
    New-Item -ItemType Directory -Path $backup | Out-Null
    foreach ($jar in $previous) { Move-Item -LiteralPath $jar.FullName -Destination (Join-Path $backup $jar.Name) }
}
try {
    Copy-Item -LiteralPath $sourceJar -Destination $destination
    if ((Get-FileHash -LiteralPath $sourceJar).Hash -ne (Get-FileHash -LiteralPath $destination).Hash) { throw 'Installed JAR does not match the release.' }
} catch {
    if (Test-Path -LiteralPath $destination) { Remove-Item -LiteralPath $destination }
    foreach ($jar in $previous) { Copy-Item -LiteralPath (Join-Path $backup $jar.Name) -Destination $jar.FullName }
    throw
}
[pscustomobject]@{ InstalledJar = $destination; Backup = $backup; SHA256 = (Get-FileHash -LiteralPath $destination).Hash }
