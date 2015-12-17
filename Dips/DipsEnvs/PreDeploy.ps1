########Variables######
$DipsDriveFolder = "C:\Dips"
#######################

function Prerequisite()
{
    Write-Host "Checking Prerequisites..."
    if(-not(Test-Path $DipsDriveFolder))
    {
        Write-Error $DipsDriveFolder " does not exist, check setup is correct"     
    }
    Write-Host "Checking complete"
}


Try
{
    Prerequisite

}
Catch
{
    Write-Error "Prerequisite failed, check setup is correct"    
    Exit 1
}
