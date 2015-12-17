#requires -version 3

Import-Module WindowsServiceDeployment

$ServiceName = $OctopusParameters['topshelf:ServiceName']
$ServiceDescription = $OctopusParameters['topshelf:ServiceDescription']
$ServiceDisplayName = $OctopusParameters['topshelf:ServiceDisplayName']
$ServiceAccount = $OctopusParameters['topshelf:ServiceUserAccount']
$ServicePassword = $OctopusParameters['topshelf:ServiceUserPassword']

$serviceExe = Join-Path -Path $OctopusPackageDirectoryPath -ChildPath 'FujiXerox.Adapters.A2iaAdapter.exe'

Deploy-WindowsService -ServiceName $serviceName -ExecutablePath $serviceExe -Username $ServiceAccount -Password $ServicePassword -AutoStart:([System.Convert]::ToBoolean($Active))
