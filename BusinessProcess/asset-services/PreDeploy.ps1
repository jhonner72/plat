Import-Module WindowsServiceDeployment

$tomcatdir = [environment]::GetEnvironmentVariable("CATALINA_HOME","Machine")
$webappsdir = Join-Path $tomcatdir "webapps"
$catalinaTemp = Join-Path $tomcatdir "work\Catalina\localhost"

Stop-WinSvc -ServiceName $ServiceName

Write-Host "Removing $webappsdir\$AppPath..."
Remove-Item $webappsdir\$AppPath* -Recurse

Write-Host "Removing $catalinaTemp\$AppPath..."
Remove-Item $catalinaTemp\$AppPath* -Recurse
