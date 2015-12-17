path=%path%;%RABBITMQ_BASE%\rabbitmq_server-3.4.4\sbin
set dd=%date:~4,2%
set mm=%date:~7,2%
set yyyy=%date:~10,4%
set todaydate=%yyyy%-%mm%-%dd%
call rabbitmqctl rotate_logs .%todaydate%.log 