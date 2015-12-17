$tomcatdir = [environment]::GetEnvironmentVariable("CATALINA_HOME","Machine")
$webappsdir = Join-Path $tomcatdir "webapps"
$currentPath = Split-Path $script:MyInvocation.MyCommand.Path
$application = Get-ChildItem "asset-services-*.war" -Name

Write-Host "Copying $application to $webappsdir\$AppPath.war"
Copy-Item $currentPath\$application -Destination "$webappsdir\$AppPath.war"
