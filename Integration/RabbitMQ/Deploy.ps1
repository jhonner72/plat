#Import RabbitMQ Constants
$currentPath= Split-Path $script:MyInvocation.MyCommand.Path
$configpath = Join-Path $currentPath "config.xml"
$rabbitMQAdmin = Join-Path $currentPath "rabbitmqadmin"
$RabbitInstall = [environment]::GetEnvironmentVariable("RABBITMQ_BASE","Machine")
$RabbitBinLocation = Join-Path $RabbitInstall "rabbitmq_server-3.5.6\sbin"
$RabbitMQCtlBat = "rabbitmqctl.bat"
$RabbitMqCtl = Join-Path $RabbitBinLocation $RabbitMQCtlBat
$RabbitMqPluginsBat = "rabbitmq-plugins.bat"
$RabbitMqPlugins = Join-Path $RabbitBinLocation $RabbitMqPluginsBat
$xmlDoc = new-object System.Xml.XmlDocument


function StartProcess($executablefile, $workingDir, $parameters)
{   
    $startInfo = New-Object System.Diagnostics.ProcessStartInfo
    $startInfo.Arguments = $parameters
    $ext = [System.IO.Path]::GetExtension($executablefile)

    if($ext -eq ".exe")
    { 
        $startInfo.FileName = $executablefile
        $startInfo.RedirectStandardOutput = $true
        $startInfo.UseShellExecute = $false    
    }
    
    if($ext -eq ".bat")
    {
        
        $startInfo.WorkingDirectory = $workingDir;
        $startInfo.RedirectStandardOutput = $false
        $startInfo.UseShellExecute = $false
        $startInfo.FileName = "cmd.exe"
        
    }

    $thisProcess = New-Object System.Diagnostics.Process
    $thisProcess.StartInfo = $startInfo
    $thisProcess.Start() | out-null

    #Checking the process and wait.
   
    $iskeepWaiting = $true
    
    $currentProcessID = $thisProcess.Id
    while ($iskeepWaiting)
    {
        $isRunning = get-process | where-object {$_.Id -eq $currentProcessID}
        if ($isRunning -ne $null)
        {
            $iskeepWaiting = $true
            Start-Sleep -Seconds 0.5
        }
        else
        {
            $iskeepWaiting = $false;
        }
    }
}

function DeclareQueue()
{
    foreach ($binding in $xmlDoc.RabbitMQ.binding)
    {
         ##Getting Request Values
         $ReqRoutineKey = $binding.RoutingKey;
         $ReqDestinationType = $binding.DestinationType;
         $ReqQueueName = $binding.Queue.Name;
         $ReqQueueDurable = $binding.Queue.Durable;
         $ReqQueueDlx = $binding.Queue.Dlx;
         $ReqQueueTtl = $binding.Queue.Ttl;

         $Qparams = " $rabbitMQAdmin declare queue name=$ReqQueueName durable=$ReqQueueDurable" 
         
         $Qparams += " arguments=`"{"
         if (![string]::IsNullOrEmpty($ReqQueueDlx))
         {
            $Qparams += " \`"x-dead-letter-exchange\`": \`"$ReqQueueDlx\`","
         }
         if (![string]::IsNullOrEmpty($ReqQueueTtl))
         {
            $Qparams += " \`"x-message-ttl\`": $ReqQueueTtl,"
         }
         if ($Qparams.EndsWith(","))
         {
         	$Qparams = $Qparams.Substring(0, $Qparams.length -1)
         }
         $Qparams += "}`""
         
         if (![string]::IsNullOrEmpty($ReqQueueName) -or 
              ![string]::IsNullOrEmpty($ReqQueueDurable) -or
              ![string]::IsNullOrEmpty($ReqDestinationType))
            {
                write-host "Declaring Queue  -" -ForegroundColor yellow
                write-host `t $Qparams 
                StartProcess -executablefile "python.exe" -workingDir $currentPath -parameters $Qparams
            }
          else
          {
            write-host $ReqQueueName $ReqQueueDurable $ReqDestinationType
          }

         $ReqExchangeName = $binding.Exchange.Name;
         $ReqExchangeType = $binding.Exchange.Type;
         $ReqExchangeDurable = $binding.Exchange.Durable;

         $ExParams = " $rabbitMQAdmin declare exchange name=$ReqExchangeName durable=$ReqQueueDurable type=$ReqExchangeType"
         if (![string]::IsNullOrEmpty($ReqExchangeName) -or 
              ![string]::IsNullOrEmpty($ReqExchangeType) -or
              ![string]::IsNullOrEmpty($ReqExchangeDurable))
            {
                write-host "Declaring Exchange -" -ForegroundColor Green
                write-host `t $ExParams
                StartProcess -executablefile "python.exe" -workingDir $currentPath -parameters $ExParams

                #Create the binding when exchange existed.
                $BindingParam = " $rabbitMQAdmin declare binding source=$ReqExchangeName destination_type=$ReqDestinationType destination=$ReqQueueName routing_key=$ReqRoutineKey"
                write-host "Binding Queue - " -ForegroundColor cyan
                write-host `t $BindingParam
                StartProcess -executablefile "python.exe" -workingDir $currentPath -parameters $BindingParam
            }
          else
          {
            write-host $ReqExchangeName $ReqExchangeType $ReqExchangeDurable
          }

         Write-Host `n
    }
}

function SetPolicy()
{
    foreach ($policy in $xmlDoc.RabbitMQ.Policies.Policy)
    {
        if($policy.Enable -eq $true)
        {
            $args = BuildPolicyCommand($policy)
            Write-Host "Creating Policy:" $policy.Name
            Write-Host "Executing command:" $RabbitMqCtl $args               
            Start-Process $RabbitMqCtl $args
            Sleep -Seconds 1
        }
    }
}

function BuildPolicyCommand($policy)
{
    #eg.it should like this $args  = 'set_policy --apply-to exchanges federate-dr ^dr.lombard.service.job$ ^ "{\"federation-upstream-set\":\"all\"}"'
    $args =  ' ' + $xmlDoc.RabbitMQ.Policies.Command.Argument + ' '
    $args += $policy.ApplyTo + " "
    $args += $policy.Name + " "
    $args += '"' + $policy.Pattern + '" ^ '
    $args += '"{\"' + $policy.Mode + '\":\"' + $policy.ModeTo + '\"}"'
    return $args
}

function LoadConfig()
{
    $xmlDoc.Load($configpath)
}

function EnablePlugins()
{

    foreach ($plugin in $xmlDoc.RabbitMQ.Plugins.Plugin)
    {
        if($plugin.Enable -eq $true)
        {
            $args = BuildPluginCommand($plugin)
            Write-Host "Enabling plugin:" $plugin.Name
            Write-Host "Executing command:" $RabbitMqPlugins $args               
            Start-Process $RabbitMqPlugins $args
            Sleep -Seconds 1
        }
    }
}

function BuildPluginCommand($plugin)
{
        $args = $xmlDoc.RabbitMQ.Plugins.Enable.Argument
        $args += " " + $plugin.Name
        return $args
}

LoadConfig
DeclareQueue
EnablePlugins
SetPolicy
