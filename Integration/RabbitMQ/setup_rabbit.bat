path=%path%;D:\RabbitMQ\rabbitmq_server-3.5.6\sbin
call rabbitmqctl stop_app
call rabbitmqctl reset
call rabbitmqctl start_app
call rabbitmqctl add_user lombard lombard
call rabbitmqctl set_user_tags lombard administrator
call rabbitmqctl set_permissions -p / lombard ".*" ".*" ".*"
