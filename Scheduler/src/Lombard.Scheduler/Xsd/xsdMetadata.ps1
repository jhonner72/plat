﻿$xsd = "C:\Program Files (x86)\Microsoft SDKs\Windows\v8.1A\bin\NETFX 4.5.1 Tools\x64\xsd.exe"   
& $xsd /c /l:cs /n:Lombard.Vif.Service.Messages.XsdImports /out:..\Messages\XsdImports "..\..\..\..\BusinessProcess\lombard-integration\lombard-integration-model\src\main\resources\schema\Voucher.xsd" "..\..\..\..\BusinessProcess\lombard-integration\lombard-integration-model\src\main\resources\schema\Metadata.xsd" 