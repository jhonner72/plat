Import-Module Documentum

$workspace = Join-Path $env:DM_HOME "install\composer\ComposerHeadless\workspace\NAB_DCTM_X"

Install-Dar -InstallWorkspace $workspace -InstallFile "install.xml"
