Import-Module Documentum

$workspace = Join-Path $env:DM_HOME "install\composer\ComposerHeadless\workspace\$DocBase"

Install-Dar -InstallWorkspace $workspace -InstallFile "install.xml"
