$application = "lombard-integration"
$tomcatdir = [environment]::GetEnvironmentVariable("CATALINA_HOME","Machine")
$webappsdir = Join-Path $tomcatdir webapps
$catalinaTemp = Join-Path $tomcatdir "work\Catalina\localhost"
$TomcatService = "Tomcat7"

function RemoveWebAppsFiles()
{
	Write-Host "Removing " $application " from " $webappsdir
	Remove-Item $webappsdir\$application* -Recurse
	Write-Host $application "removed from " $webappsdir
}

function RemoveTempFiles()
{
	Write-Host "Removing " $application " temp files from " $catalinaTemp
	Remove-Item $catalinaTemp\$application* -Recurse
	Write-Host $application "removed from " $catalinaTemp
}

function StopTomcat7()
{
    Write-Host "Stopping " $TomcatService
    $service = Get-Service -Name $TomcatService

    if($service.Status -eq "Running")
    {
        net stop $TomcatService
        Write-Host $TomcatService " stopped"
    }
    else
    {
        Write-Host $TomcatService " is already stopped"
    }
}

Try
{
    StopTomcat7
    RemoveWebAppsFiles
    RemoveTempFiles
}
Catch
{
    Write-Error "Problem with pre-deploy"
    Exit 1
}
