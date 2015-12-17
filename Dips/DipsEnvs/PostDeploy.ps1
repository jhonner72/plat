$Services = @("AutoBalancing","CodelineValidation","GenerateCorrespondingVoucher","GenerateBulkCredit")

$currentPath= Split-Path $script:MyInvocation.MyCommand.Path
Import-Module "$currentPath\Common.ps1" -ErrorAction Stop

function StartWindowService()
{
    foreach($service in $Services)
    {
        StartService $service
    }
}

Try
{
    StartWindowService
}
Catch
{
    Write-Error "failed to start service" $_.Exception.Message    
    Exit 1
}
