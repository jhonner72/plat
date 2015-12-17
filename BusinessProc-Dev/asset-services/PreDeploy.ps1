Import-Module WindowsServiceDeployment

$application = "asset-services"
$tomcatdir = [environment]::GetEnvironmentVariable("CATALINA_HOME","Machine")
$webappsdir = Join-Path $tomcatdir webapps
$catalinaTemp = Join-Path $tomcatdir "work\Catalina\localhost"

Stop-WinSvc -ServiceName $ServiceName

Write-Host "Removing $webappsdir\$application..."
Remove-Item $webappsdir\$application* -Recurse

Write-Host "Removing $catalinaTemp\$application..."
Remove-Item $catalinaTemp\$application* -Recurse
