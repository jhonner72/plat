$currentPath= Split-Path $script:MyInvocation.MyCommand.Path
$RabbitInstall = [environment]::GetEnvironmentVariable("RABBITMQ_BASE","Machine")
$RabbitService = Join-Path $RabbitInstall "rabbitmq_server-3.5.6\sbin\rabbitmq-service.bat"

& $RabbitService stop

If(!(Test-Path $SslCert) -or !(Test-Path $SslKey))
{
    Write-Error "Unable to find $SslCert or $SslKey"
}

Write-Host "Copying rabbitmq.config to $RabbitInstall"
Copy-Item "rabbitmq.config" $RabbitInstall -Force
Write-Host "Copying enabled_plugins to $RabbitInstall"
Copy-Item "enabled_plugins" $RabbitInstall -Force

[System.ServiceProcess.ServiceController]$service = Get-Service -Name "RabbitMQ"
& $RabbitService start
$service.WaitForStatus([System.ServiceProcess.ServiceControllerStatus]::Running,"00:02:00")
Write-Host $service.Name $service.Status
