$RabbitInstall = [environment]::GetEnvironmentVariable("RABBITMQ_BASE","Machine")
$RabbitBinLocation = Join-Path $RabbitInstall "rabbitmq_server-3.5.6\sbin"
$RabbitMQCtlBat = "rabbitmqctl.bat"
$RabbitMqCtl = Join-Path $RabbitBinLocation $RabbitMQCtlBat
$Policy = "set_policy DLX_HA `"lombard.dlx.system.queue`" `"{`"`"dead-letter-exchange`"`":`"`"lombard.dlx.retry`"`",`"`"dead-letter-routing-key`"`":`"`"system`"`",`"`"message-ttl`"`":300000, `"`"ha-mode`"`":`"`"all`"`", `"`"ha-sync-mode`"`":`"`"automatic`"`"}`" --apply-to queues"

function DeclarePolicy()
{
    write-host "Set Policy - " -ForegroundColor cyan
    write-host `t $RabbitMqCtl $Policy
    
    Start-Process $RabbitMqCtl $Policy
}

DeclarePolicy