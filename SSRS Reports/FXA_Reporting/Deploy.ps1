Import-Module ReportServiceDeployment

$rs = Get-ReportService -ServiceUrl $ServiceUrl -Username $ServiceUsername -Password $ServicePassword
Deploy-DataSource -ReportService $rs -Name $DsName -Path $DataSourcePathOnServer -ConnectionString $DsConnectionString -Username $DataSourceUsername -Password $DataSourcePassword
Deploy-DataSource -ReportService $rs -Name $DCDsName -Path $DataSourcePathOnServer -ConnectionString $DCDsConnectionString -Username $DataSourceUsername -Password $DataSourcePassword

$RsdFolderPath = Join-Path -Path $OctopusPackageDirectoryPath -ChildPath $RsdLocation
$RdlFolderPath = Join-Path -Path $OctopusPackageDirectoryPath -ChildPath $RdlLocation

Get-ChildItem $RsdFolderPath -filter "*.rsd" | where {(!($_.Name -eq "NAB_EOD_EDA2.rsd"))} | ForEach-Object {
    Deploy-DataSet -ReportService $rs -RsdFilePath $_.FullName -DataSetPath $DataSetPathOnServer -DataSourceNames $DsName -DataSourcePaths $DataSourcePathOnServer
}

Get-ChildItem $RsdFolderPath -filter "NAB_EOD_EDA2.rsd" | ForEach-Object {
    Deploy-DataSet -ReportService $rs -RsdFilePath $_.FullName -DataSetPath $DataSetPathOnServer -DataSourceNames $DCDsName -DataSourcePaths $DataSourcePathOnServer
}

Get-ChildItem $RdlFolderPath -filter "*.rdl" | where {(!($_.Name -eq "NAB_EOD_EDA2.rdl"))} | ForEach-Object {
    Deploy-Report -ReportService $rs -RdlFilePath $_.FullName -ReportPath $ReportPathOnServer -DataSourceNames $DsName -DataSourcePaths $DataSourcePathOnServer -DataSetPath $DataSetPathOnServer
}

Get-ChildItem $RdlFolderPath -filter "NAB_EOD_EDA2.rdl" | ForEach-Object {
    Deploy-Report -ReportService $rs -RdlFilePath $_.FullName -ReportPath $ReportPathOnServer -DataSourceNames $DCDsName -DataSourcePaths $DataSourcePathOnServer -DataSetPath $DataSetPathOnServer
}