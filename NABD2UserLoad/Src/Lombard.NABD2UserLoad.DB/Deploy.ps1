Function Publish-Database {
    param(        
        [Parameter(Mandatory=$true)]
        [string]$TargetDatabaseName,
        [Parameter(Mandatory=$true)]
        [string]$TargetServerName
    )
    sqlpackage /Action:Publish /SourceFile:"Lombard.NABD2UserLoad.DB.dacpac" /TargetDatabaseName:"$TargetDatabaseName" /TargetServerName:"$TargetServerName" 
}

Publish-Database -TargetDatabaseName $DatabaseName -TargetServerName $ServerName
