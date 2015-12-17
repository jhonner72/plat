[CmdletBinding()]
param()

$currentPath= Split-Path $script:MyInvocation.MyCommand.Path
$global:ServiceName,$global:ServiceDescription,$global:ServiceParameters,$global:BinaryPath,$global:DependsOn,$global:StartupType,$glocal:ServiceUserAccount,$global:ServiceUserPassword, $global:Credential

function GetSettingsFromConfig($Service)
{
    try
    {
        Write-Host "Getting settings for" $Service
        # Import settings from config file
        [xml]$ConfigFile = Get-Content "$currentPath\Settings.xml"
        $global:ServiceName =  $ConfigFile.Settings.Services.$Service.ServiceName
        $global:ServiceDescription = $ConfigFile.Settings.Services.$Service.ServiceDescription
        $global:ServiceParameters = $ConfigFile.Settings.Services.$Service.ServiceParameters
        $global:BinaryPath = $ConfigFile.Settings.Services.$Service.BinaryPath
        $global:StartupType = $ConfigFile.Settings.Services.StartupType
        $global:ServiceUserAccount = $ConfigFile.Settings.Services.$Service.ServiceUserAccount
        $global:ServiceUserPassword = $ConfigFile.Settings.Services.$Service.ServiceUserPassword |  ConvertTo-SecureString -AsPlainText -Force
        $global:Credential = new-object -typename System.Management.Automation.PSCredential -argumentlist $ServiceUserAccount, $ServiceUserPassword
        
        Write-Host "Service Name:"$ServiceName + "Service Description:"$ServiceDescription 
    }
    catch
    {
        Write-Error "Failed to getting settings" $_.Exception.Message
    }
}

function InstallService($Service)
{
    try
    {
        GetSettingsFromConfig $Service
        Write-Host "Installing" $Service
        Write-Host $global:ServiceName "will be installed..."
        New-Service -Name $global:ServiceName -BinaryPathName "$global:BinaryPath $global:ServiceParameters" -DisplayName $global:ServiceName -StartupType $global:StartupType -Description $global:ServiceDescription -Credential $global:Credential
        Write-Host $global:ServiceName "installed"
    }
    catch
    {
        Write-Error "Failed to install" $Service + $_.Exception.Message
    }
}

function UninstallService($Service, $Process)
{
    try
    {
        GetSettingsFromConfig $Service


        #Force kill the process just to be sure
        if(Get-Process -Name $Process -ErrorAction SilentlyContinue)
        { 
            Write-Host "Stopping $Process ..."
            Stop-Process -Name $Process -Force -ErrorAction SilentlyContinue |Write-Host
            Write-Host "$Process Stopped"
        }

        [System.ServiceProcess.ServiceController]$ServiceProcess = Get-Service -Name $global:ServiceName -ErrorAction SilentlyContinue
        If ($ServiceProcess.Status -eq [System.ServiceProcess.ServiceControllerStatus]::Running) 
        {              
	        "$($MyInvocation.MyCommand): Stopping service" | Write-Host
            $ServiceProcess.Stop()
	        $timeout = '00:10:00'
	        $ServiceProcess.WaitForStatus('Stopped',$timeout)
        }

        if(Get-Service $ServiceName -ErrorAction SilentlyContinue)
        {
            Write-Host $ServiceName "will be uninstalled..."
            sc.exe delete $ServiceName -ErrorAction Stop | Write-Host
            Write-Host $ServiceName "uninstalled"
        }
    }
    catch
    {
        Write-Error "Failed to uninstall" $_.Exception.Message
    }
}

function StartService($Service)
{
    try
    {
        Write-Host "Starting $Service Service"
        GetSettingsFromConfig $Service

        [System.ServiceProcess.ServiceController]$ServiceProcess = Get-Service -Name $global:ServiceName -ErrorAction SilentlyContinue

        $ServiceProcess.Start()
        Write-Host "Started $Service Service started successfully"
    }
    catch
    {
            Write-Error "Failed to start service" $_.Exception.Message
    }
}
