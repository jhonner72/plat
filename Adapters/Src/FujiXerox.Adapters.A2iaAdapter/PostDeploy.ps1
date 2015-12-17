#requires -version 3
Import-Module WindowsServiceDeployment

$serviceName = $OctopusParameters['topshelf:ServiceName']

Start-WinSvc -ServiceName $serviceName
