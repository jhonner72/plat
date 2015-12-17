$TomcatService = "Tomcat7"

function StartTomcat7()
{
    Write-Host "Starting " $TomcatService

    if($service.Status -eq "running")
    {
        Write-Host $TomcatService " is already running"
    }
    else
    {
        net start $TomcatService
        Write-Host $TomcatService " started"
    }
}

try
{
    StartTomcat7
}
catch
{
    Write-Error "Error starting " $TomcatService
    Exit 1
}