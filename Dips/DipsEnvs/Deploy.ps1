[CmdletBinding()]
param()
########Variables######
$DipsInstallDirectory = "C:\Lombard\Dips\DipsEnvs\dips402_NABCHQ\Dips402"
$NabChqFolder = "dips402_NABCHQ\Dips402"
$sys402Folder = "sys402"
$ExcludeTopFolders = @("data",$Sys402Folder)
$ExcludeProcessFolder ="Process"
$Services = @("AutoBalancing","CodelineValidation","GenerateCorrespondingVoucher","GenerateBulkCredit")
$Process = "DataProcessor"
$RetryCount = 0
$Retries = 5
$SecondsDelay = 5
#######################
$Completed = $false


$currentPath= Split-Path $script:MyInvocation.MyCommand.Path
Import-Module "$currentPath\Common.ps1" -ErrorAction Stop

function CopyFolders()
{
    Write-Host $NabChqFolder " copying Folders to " $DipsInstallDirectory
    if(-not(Test-Path $DipsInstallDirectory))
    {
        New-Item -ItemType Directory -Force $DipsInstallDirectory
    }
    Write-Host "Getting folders to Exclude from " $sys402Folder
    Get-ChildItem $NabChqFolder -Exclude $sys402Folder | Copy-Item -Destination $DipsInstallDirectory -Recurse -Force -ErrorAction Stop
    Write-Host "Copying " $NabChqFolder "to " $DipsInstallDirectory
    Get-ChildItem (Join-Path $NabChqFolder $sys402Folder) -Exclude $ExcludeProcessFolder | Copy-Item -Destination (Join-Path $DipsInstallDirectory $sys402Folder) -Recurse -Force -ErrorAction Stop
    
    Write-Host "DIP folders are copy completed"
}

function InstallWindowService()
{
    foreach($service in $Services)
    {
        InstallService $service
    }
}


function UninstallWindowService()
{
    foreach($service in $Services)
    {
        UninstallService $service $Process
    }
}

#Remove all top level folders except the excluded list which contains files that are persistent
function RemoveInstallDirectory()
{


    if(Test-Path $DipsInstallDirectory)
    {
        RemoveSys402Folder
        

            Write-Host "Removing install folders in " $DipsInstallDirectory " excluding " $ExcludeTopFolders
            Get-ChildItem $DipsInstallDirectory -Exclude $ExcludeTopFolders  | Remove-Item -Recurse -Force -ErrorAction Stop
            Write-Host "Install folder removed"
        
    }
}

#Remove Sys402 Folder except the Process Folder which contains files that are persistent
function RemoveSys402Folder()
{
    $Sys402Path = Join-Path $DipsInstallDirectory $Sys402Folder
    Write-Host "Removing Sys402 folder excluding folder " $ExcludeProcessFolder 
    Get-ChildItem $Sys402Path -Exclude $ExcludeProcessFolder | Remove-Item -Recurse -Force -ErrorAction Stop
    Write-Host "Sys402 folder removed"
}

while(-not $Completed)
{
    Try
    {
        Write-Host "Starting Deployment.."
        UninstallWindowService
        RemoveInstallDirectory
        CopyFolders
        InstallWindowService
        $Completed = $true
		Exit 0
    }
    Catch
    {
        if($RetryCount -le $Retries)
        {
            Write-Host ("Failed to deploy {0} out of {1}, retrying in {2} seconds." -f $RetryCount, $Retries, $SecondsDelay)
            Start-Sleep $SecondsDelay
            $RetryCount++
        }
        else
        {
            $_.Exception.Message
            Write-Error "Error with the deployment, make sure the processes and files are not in use"
            $Completed = $true
            Exit 1
        }
    }
}