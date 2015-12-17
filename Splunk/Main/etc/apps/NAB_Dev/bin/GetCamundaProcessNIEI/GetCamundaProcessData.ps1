param (    
    [string]$camundaProcessDefinitionKey = $(throw "-camundaProcessDefinitionKey is required."),   
    [string]$previousRunJsonPath = $(throw "-previousRunJsonPath is required.")
)

$currentPath= Split-Path $script:MyInvocation.MyCommand.Path
$configpath = Join-Path $currentPath "App.config"
$config = new-object System.Xml.XmlDocument
#default?
[string]$camundaRestBaseUri = ""

$config.Load($configpath)

foreach($url in $config.appSettings.add)
{
    $urlName = $url.key
    if($urlName -eq "camundaRestBaseUrl"){
        $camundaRestBaseUri=$url.value
    }
}
if($camundaRestBaseUri -eq ""){
    $(throw "camundaRestBaseUrl is not set in App.config but is required.")
}


#$DebugPreference = "Continue"

#$camundaRestBaseUri = "http://svmd-9552ccs:8080/engine-rest/history/process-instance/";
#$camundaProcessDefinitionKey = "EAID_5ADCF598_C32A_49e8_9487_F525E3CB6C06";
#$previousRunJsonPath = "/processData/process-instance.json"
$previousRunJsonPath = Join-Path $currentPath $previousRunJsonPath
if (Test-Path $previousRunJsonPath)
{
    $previousResultContent = Get-Content $previousRunJsonPath
}

if (!$previousResultContent)
{
    $previousResultContent = "{}"
}
$previousResult = $previousResultContent | ConvertFrom-Json

$camundaProcessStartedAfter = $previousResult.dateProcessedTill;

$uri = $camundaRestBaseUri + "?processDefinitionKey=" + $camundaProcessDefinitionKey
if ($camundaProcessStartedAfter -ne "")
{
    $uri += "&startedAfter=" + $camundaProcessStartedAfter
}

Write-Debug "Calling Camunda RestApi [$uri]"

$results = Invoke-RestMethod -Uri $uri -Method Get

$maxDateProcessed = $null

$recordCount = if ($results -is [array]) { $results.Count } else { 0 }

Write-Debug "Total records returned: $recordCount"

foreach($result in $results)
{
    $isIndexed = $false
    if ($previousResult.results.value.id -notcontains $result.id)
    {
        $result | ConvertTo-Json | Write-Host
    }
    else
    {
        Write-Debug $result.id
    }
    
    $startTime = Get-Date $result.startTime -Format s
    if (($startTime -gt $maxDateProcessed) -Or !$maxDateProcessed)
    {
        $maxDateProcessed = $startTime
    }
}

$resultsInString = ConvertTo-Json $results -Compress

$customJsonObject = @"
{"results": $resultsInString,"dateProcessedTill": "$maxDateProcessed"}
"@

#Write-Debug "Writing to $previousRunJsonPath"

$customJsonObject | Out-File -FilePath $previousRunJsonPath -Force