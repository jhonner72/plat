$application = "asset-services"
$tomcatdir = [environment]::GetEnvironmentVariable("CATALINA_HOME","Machine")
$webappsdir = Join-Path $tomcatdir webapps
$currentPath= Split-Path $script:MyInvocation.MyCommand.Path

Write-Host "Copying $application to $webappsdir"
Copy-Item $currentPath\$application* -Destination $webappsdir
