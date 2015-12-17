"C:\Program Files (x86)\WiX Toolset v3.9\bin\heat.exe" dir "..\..\DipsEnvs\dips402_NABCHQ_SCANNING\Dips402" -out ".\File2.wxs" -gg -sfrag -var var.DipsSource -cg DipsServiceComponents -ke -dr INSTALLFOLDER -sreg -suid

#Replace the service startup code for DataProcessor.exe